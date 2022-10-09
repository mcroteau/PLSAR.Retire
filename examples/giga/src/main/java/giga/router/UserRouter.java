package giga.router;

import chico.Chico;
import giga.Giga;
import giga.model.*;
import giga.repo.*;
import giga.service.*;
import jakarta.servlet.http.HttpRequest;
import qio.Qio;
import qio.annotate.HttpRouter;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


@HttpRouter
public class UserRouter {

	@Inject
	Qio qio;

	@Inject
	DesignRepo designRepo;

	@Inject
	SaleRepo saleRepo;

	@Inject
	BusinessRepo businessRepo;

	@Inject
	SiteService siteService;

	@Inject
	UserService userService;

	@Inject
	AuthService authService;

	@Inject
	SaleService saleService;

	@Inject
	RoleRepo roleRepo;

	@Inject
	UserRepo userRepo;

	@Inject
	SmsService smsService;

	@Inject
	MailService mailService;

	@Get("/{{shop}}/signup")
	public String shopSignup(HttpRequest req,
							 Cache cache,
							 @Component String shopUri){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";
		Design design = designRepo.getBase(business.getId());
		data.set("design", design);
		data.set("business", business);
		data.set("siteService", siteService);
		data.set("request", req);
		return "/pages/business/signup.jsp";
	}


	@Get("/{{shop}}/activity")
	public String getActivity(HttpRequest req,
							 Cache cache,
							 @Component String shopUri) throws ParseException {
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		if(!authService.isAuthenticated()){
			return "[redirect]/" + shopUri;
		}

		List<Sale> sales = new ArrayList();
		User authUser = authService.getUser();
		if(business.getAffiliate() == null ||
				!business.getAffiliate()) {
			sales = saleRepo.getListPrimary(business.getId());
		}else{
			sales = saleRepo.getListAffiliate(business.getId());
		}

		saleService.setSaleData(sales);

		Design design = designRepo.getBase(business.getId());
		data.set("design", design);

		data.set("sales", sales);
		data.set("business", business);
		data.set("siteService", siteService);
		data.set("request", req);
		return "/pages/business/activity.jsp";
	}

	@Get("/users/edit/{{businessId}}/{{id}}")
	public String getEdit(Cache cache,
						  @Component Long businessId,
						  @Component Long id){
		String permission = getPermission(Long.toString(id));
		if(!authService.isAdministrator() &&
				!authService.hasPermission(permission)){
			return "[redirect]/";
		}

		businessService.setData(businessId, cache);

		User user = userRepo.get(id);
		cache.set("user", user);

		cache.set("page", "/pages/user/edit.jsp");
		return "/designs/auth.jsp";
	}


	@Post("/users/update/{{businessId}}/{{id}}")
	public String update(HttpRequest req,
						 Cache cache,
						 @Component Long businessId,
						 @Component Long id){
		User user = (User) Qio.get(req, User.class);

		String permission = getPermission(Long.toString(user.getId()));
		if(!authService.isAdministrator() &&
				!authService.hasPermission(permission)){
			return "[redirect]/";
		}

		user.setPhone(Giga.getSpaces(user.getPhone()));
		userRepo.update(user);

		cache.set("message", "Your account was successfully updated");
		return "[redirect]/users/edit/" + businessId + "/" + id;
	}

	@Get("/users/reset")
	public String reset(Cache cache){
		data.set("page", "/pages/user/reset.jsp");
		return "/designs/guest.jsp";
	}

	@Post("/users/send")
	public String send(HttpRequest req,
							Cache cache){
		try {
			String phone = req.getParameter("phone");
			if(phone != null) phone = Giga.getPhone(phone);
			User user = userRepo.getPhone(phone);
			if (user == null) {
				data.put("message", "Unable to find user with cell phone " + phone + ". Please try again or if the problem persists, contact me Mike and I will reset your password for you. croteau.mike@gmail.com.");
				return ("[redirect]/users/reset");
			}

			String guid = Giga.getString(4);
			user.setPassword(Chico.dirty(guid));
			userRepo.updatePassword(user);

			String message = "Giga >_ Your temporary password : "    + guid;
			smsService.send(phone, message);

		}catch(Exception e){
			e.printStackTrace();
		}

		data.put("message", "Successfully sent you your reset instructions");
		return "[redirect]/signin";
	}

	@Post("/users/reset/{{id}}")
	public String resetPassword(HttpRequest req,
								Cache cache,
								@Component Long id){

		User user = userRepo.get(id);
		User reqUser = (User) Qio.get(req, User.class);

		if(reqUser.getPassword().length() < 7){
			data.put("message", "Passwords must be at least 7 characters long.");
			return "[redirect]/users/confirm?phone=" + reqUser.getPhone() + "&uuid=" + reqUser.getUuid();
		}

		if(!reqUser.getPassword().equals("")){
			String password = Chico.dirty(reqUser.getPassword());
			user.setPassword(password);
			userRepo.updatePassword(user);
		}

		authService.signout();

		data.put("message", "Password successfully updated! You can continue now!");
		return "[redirect]/signin";
	}

	@Get("/clients/{{id}}")
	public String clients(Cache cache,
							@Component Long id){
		if(!authService.isAuthenticated()){
			return "[redirect]/snapshot/" + businessId;
		}

		String businessPermission = Giga.BUSINESS_MAINTENANCE + businessId;
		if(!authService.hasPermission(businessPermission)){
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

		cache.set("siteService", siteService);
		cache.set("clients", clients);
		cache.set("page", "/pages/user/list.jsp");

		businessService.setData(businessId, cache);
		return "/designs/auth.jsp";
	}

	@Get("/{{shop}}/users/password/get")
	public String getPassword(HttpRequest req,
							  Cache cache,
							  @Component String shopUri){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		Design design = designRepo.getBase(business.getId());
		if(design == null)return "[redirect]/";

		data.set("design", design);
		data.set("business", business);
		data.set("siteService", siteService);
		data.set("request", req);
		return "/pages/business/get_password.jsp";
	}

	@Post("/{{shop}}/users/password/send")
	public String sendPassword(HttpRequest req,
							   Cache cache,
							   @Component String shopUri){

		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		User user = (User) qio.set(req, User.class);
		String phone = Giga.getPhone(user.getPhone());
		if(phone.equals(""))return "[redirect]/" + shopUri;

		User storedUser = userRepo.getPhone(phone);
		if(storedUser == null)return "[redirect]/" + shopUri;

		String password = Giga.getString(6);
		storedUser.setPassword(Chico.dirty(password));
		userRepo.updatePassword(storedUser);

		String message = business.getName() + " :: temporary password is " + password;
		smsService.send(phone, message);

		data.set("message", "Success! A password has been sent to you!");
		return "[redirect]/" + shopUri + "/signin";
	}


	@Get("/{{shop}}/users/edit/{{id}}")
	public String editUser(HttpRequest req,
							 Cache cache,
							 @Component String shopUri,
						     @Component Long id){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		User user = userRepo.get(id);
		if(user == null)return "[redirect]/";

		Design design = designRepo.getBase(business.getId());

		data.set("user", user);
		data.set("design", design);
		data.set("business", business);
		data.set("siteService", siteService);
		data.set("request", req);
		return "/pages/business/edit_user.jsp";
	}

	@Post("/{{shop}}/users/update")
	public String updateUser(HttpRequest req,
							   Cache cache,
							   @Component String shopUri){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		User user = (User) qio.set(req, User.class);
		userRepo.update(user);

		data.set("message", "Successfully updated your account!");
		return "[redirect]/" + shopUri + "/users/edit/" + user.getId();
	}

	@Get("/{{shop}}/users/password/edit")
	public String editPassword(HttpRequest req,
							   Cache cache,
							   @Component String shopUri,
							   @Component Long id){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		Design design = designRepo.getBase(business.getId());
		if(design == null)return "[redirect]/";

		User user = userRepo.get(id);
		if(user == null)return "[redirect]/";

		data.set("user", user);
		data.set("business", business);
		data.set("design", design);
		data.set("siteService", siteService);
		data.set("request", req);
		return "/pages/business/edit_password.jsp";
	}


	@Post("/{{shop}}/users/password/save")
	public String savePassword(HttpRequest req,
							  Cache cache,
							  @Component String shopUri){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		User user = (User) qio.set(req, User.class);
		if(user.getPassword().length() < 6){
			data.put("message", "Passwords must be at least 6 characters long.");
			return "[redirect]/"+ shopUri + "/users/password/edit";
		}

		if(!user.getPassword().equals("")){
			String password = Chico.dirty(user.getPassword());
			user.setPassword(password);
			userRepo.updatePassword(user);
		}

		data.set("message", "Successfully updated! You may now sign in with your new credentials.");
		return "[redirect]/" + shopUri + "/signin";
	}
}