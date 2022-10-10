package giga.router;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import giga.service.*;
import net.plsar.RouteAttributes;
import net.plsar.annotations.Component;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Inject;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;
import net.plsar.model.HttpRequest;
import net.plsar.model.HttpResponse;
import net.plsar.security.SecurityManager;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@HttpRouter
public class UserRouter {

	@Inject
	SaleRepo saleRepo;

	@Inject
	ItemRepo itemRepo;

	@Inject
	CartRepo cartRepo;

	@Inject
	UserRepo userRepo;

	@Inject
	DesignRepo designRepo;

	@Inject
	BusinessRepo businessRepo;

	@Inject
	CategoryRepo categoryRepo;

	BusinessService businessService;

	public UserRouter(){
		this.businessService = new BusinessService();
	}

	@Get("/{{shop}}/signup")
	public String shopSignup(Cache cache,
							 HttpRequest req,
							 SecurityManager security,
							 @Component String shopUri){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";
		Design design = designRepo.getBase(business.getId());
		SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);

		cache.set("design", design);
		cache.set("business", business);
		cache.set("siteService", siteService);
		cache.set("request", req);
		return "/pages/business/signup.jsp";
	}


	@Get("/{{shop}}/activity")
	public String getActivity(Cache cache,
							  HttpRequest req,
							  SecurityManager security,
							  @Component String shopUri) throws ParseException {
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		if(!security.isAuthenticated(req)){
			return "[redirect]/" + shopUri;
		}

		List<Sale> sales;
		if(business.getAffiliate() == null ||
				!business.getAffiliate()) {
			sales = saleRepo.getListPrimary(business.getId());
		}else{
			sales = saleRepo.getListAffiliate(business.getId());
		}

		SaleService saleService = new SaleService();
		saleService.setSaleData(sales, cartRepo, itemRepo);

		Design design = designRepo.getBase(business.getId());
		SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);

		cache.set("design", design);
		cache.set("sales", sales);
		cache.set("business", business);
		cache.set("siteService", siteService);
		cache.set("request", req);
		return "/pages/business/activity.jsp";
	}

	@Get("/users/edit/{{businessId}}/{{id}}")
	public String getEdit(Cache cache,
						  HttpRequest req,
						  SecurityManager security,
						  @Component Long businessId,
						  @Component Long id){
		String permission = getPermission(Long.toString(id));
		if(!security.hasRole(Giga.SUPER_ROLE, req) &&
				!security.hasPermission(permission, req)){
			return "[redirect]/";
		}
		String credential = security.getUser(req);
		User authUser = userRepo.get(credential);
		if(authUser == null){
			authUser = userRepo.getPhone(credential);
		}
		SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
		businessService.setData(businessId, cache, authUser, businessRepo, siteService);

		User user = userRepo.get(id);
		cache.set("user", user);

		cache.set("page", "/pages/user/edit.jsp");
		return "/designs/auth.jsp";
	}


	@Post("/users/update/{{businessId}}/{{id}}")
	public String update(Cache cache,
						 HttpRequest req,
						 SecurityManager security,
						 @Component Long businessId,
						 @Component Long id){
		User user = (User) req.inflect(User.class);

		String permission = getPermission(Long.toString(user.getId()));
		if(!security.hasRole(Giga.SUPER_ROLE, req) &&
				!security.hasPermission(permission, req)){
			return "[redirect]/";
		}

		user.setPhone(Giga.getSpaces(user.getPhone()));
		userRepo.update(user);

		cache.set("message", "Your account was successfully updated");
		return "[redirect]/users/edit/" + businessId + "/" + id;
	}

	@Get("/users/reset")
	public String reset(Cache cache){
		cache.set("page", "/pages/user/reset.jsp");
		return "/designs/guest.jsp";
	}

	@Post("/users/send")
	public String send(Cache cache,
					   HttpRequest req,
					   SecurityManager security){
		try {
			String phone = req.getValue("phone");
			if(phone != null) phone = Giga.getPhone(phone);
			User user = userRepo.getPhone(phone);
			if (user == null) {
				cache.set("message", "Unable to find user with cell phone " + phone + ". Please try again or if the problem persists, contact me Mike and I will reset your password for you. croteau.mike@gmail.com.");
				return ("[redirect]/users/reset");
			}

			String guid = Giga.getString(4);
			user.setPassword(security.hash(guid));
			userRepo.updatePassword(user);


			RouteAttributes routeAttributes = req.getRouteAttributes();
			String key = (String) routeAttributes.get("sms.key");

			SmsService smsService = new SmsService();
			String message = "Giga >_ Your temporary password : "    + guid;
			smsService.send(key, phone, message);

		}catch(Exception e){
			e.printStackTrace();
		}

		cache.set("message", "Successfully sent you your reset instructions");
		return "[redirect]/signin";
	}

	@Post("/users/reset/{{id}}")
	public String resetPassword(Cache cache,
								HttpRequest req,
								HttpResponse resp,
								SecurityManager security,
								@Component Long id){

		User user = userRepo.get(id);
		User reqUser = (User) req.inflect(User.class);

		if(reqUser.getPassword().length() < 7){
			cache.set("message", "Passwords must be at least 7 characters long.");
			return "[redirect]/users/confirm?phone=" + reqUser.getPhone() + "&uuid=" + reqUser.getUuid();
		}

		if(!reqUser.getPassword().equals("")){
			String password = security.hash(reqUser.getPassword());
			user.setPassword(password);
			userRepo.updatePassword(user);
		}

		security.signout(req, resp);

		cache.set("message", "Password successfully updated! You can continue now!");
		return "[redirect]/signin";
	}

	@Get("/clients/{{businessId}}")
	public String clients(Cache cache,
						  HttpRequest req,
						  SecurityManager security,
						  @Component Long businessId){
		if(!security.hasRole(Giga.SUPER_ROLE, req)){
			return "[redirect]/snapshot/" + businessId;
		}

		String permission = Giga.BUSINESS_MAINTENANCE + businessId;
		if(!security.hasPermission(permission, req)){
			cache.set("message", "whaoo... ");
			return "[redirect]/snapshot/" + businessId;
		}

		List<User> clients = new ArrayList<>();
		List<User> users = businessRepo.getUsers(businessId);
		for(User client : users){
			List<Sale> sales = saleRepo.getUserSales(client.getId());

			BigDecimal salesTotal = new BigDecimal(0);
			for(Sale sale : sales){
				salesTotal = salesTotal.add(sale.getAmount());
			}
			client.setSalesTotal(salesTotal);
			client.setSalesCount(Long.valueOf(sales.size()));
			clients.add(client);

		}

		String credential = security.getUser(req);
		User authUser = userRepo.get(credential);
		if(authUser == null){
			authUser = userRepo.getPhone(credential);
		}

		SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
		businessService.setData(businessId, cache, authUser, businessRepo, siteService);

		cache.set("siteService", siteService);
		cache.set("clients", clients);
		cache.set("page", "/pages/user/list.jsp");

		return "/designs/auth.jsp";
	}

	@Get("/{{shop}}/users/password/get")
	public String getPassword(Cache cache,
							  HttpRequest req,
							  SecurityManager security,
							  @Component String shopUri){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		Design design = designRepo.getBase(business.getId());
		if(design == null)return "[redirect]/";

		SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);

		cache.set("design", design);
		cache.set("business", business);
		cache.set("siteService", siteService);
		cache.set("request", req);
		return "/pages/business/get_password.jsp";
	}

	@Post("/{{shop}}/users/password/send")
	public String sendPassword(Cache cache,
							   HttpRequest req,
							   SecurityManager security,
							   @Component String shopUri){

		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		User user = (User) req.inflect(User.class);
		String phone = Giga.getPhone(user.getPhone());
		if(phone.equals(""))return "[redirect]/" + shopUri;

		User storedUser = userRepo.getPhone(phone);
		if(storedUser == null)return "[redirect]/" + shopUri;

		String password = Giga.getString(6);
		storedUser.setPassword(security.hash(password));
		userRepo.updatePassword(storedUser);


		RouteAttributes routeAttributes = req.getRouteAttributes();
		String key = (String) routeAttributes.get("sms.key");

		SmsService smsService = new SmsService();
		String message = business.getName() + " :: temporary password is " + password;
		smsService.send(key, phone, message);

		cache.set("message", "Success! A password has been sent to you!");
		return "[redirect]/" + shopUri + "/signin";
	}


	@Get("/{{shop}}/users/edit/{{id}}")
	public String editUser(Cache cache,
						   HttpRequest req,
						   SecurityManager security,
						   @Component String shopUri,
						   @Component Long id){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		User user = userRepo.get(id);
		if(user == null)return "[redirect]/";

		Design design = designRepo.getBase(business.getId());
		SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);

		cache.set("user", user);
		cache.set("design", design);
		cache.set("business", business);
		cache.set("siteService", siteService);
		cache.set("request", req);
		return "/pages/business/edit_user.jsp";
	}

	@Post("/{{shop}}/users/update")
	public String updateUser(HttpRequest req,
							   Cache cache,
							   @Component String shopUri){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		User user = (User) req.inflect(User.class);
		userRepo.update(user);

		cache.set("message", "Successfully updated your account!");
		return "[redirect]/" + shopUri + "/users/edit/" + user.getId();
	}

	@Get("/{{shop}}/users/password/edit")
	public String editPassword(Cache cache,
							   HttpRequest req,
							   SecurityManager security,
							   @Component String shopUri,
							   @Component Long id){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		Design design = designRepo.getBase(business.getId());
		if(design == null)return "[redirect]/";

		User user = userRepo.get(id);
		if(user == null)return "[redirect]/";

		SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);

		cache.set("user", user);
		cache.set("business", business);
		cache.set("design", design);
		cache.set("siteService", siteService);
		cache.set("request", req);
		return "/pages/business/edit_password.jsp";
	}


	@Post("/{{shop}}/users/password/save")
	public String savePassword(Cache cache,
							   HttpRequest req,
							   SecurityManager security,
							   @Component String shopUri){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		User user = (User) req.inflect(User.class);
		if(user.getPassword().length() < 6){
			cache.set("message", "Passwords must be at least 6 characters long.");
			return "[redirect]/"+ shopUri + "/users/password/edit";
		}

		if(!user.getPassword().equals("")){
			String password = security.hash(user.getPassword());
			user.setPassword(password);
			userRepo.updatePassword(user);
		}

		cache.set("message", "Successfully updated! You may now sign in with your new credentials.");
		return "[redirect]/" + shopUri + "/signin";
	}

	String getPermission(String id){
		return Giga.USER_MAINTENANCE + id;
	}
}