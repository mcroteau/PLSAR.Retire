package giga.web;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import giga.service.*;
import dev.blueocean.RouteAttributes;
import net.plsar.annotations.Component;
import net.plsar.annotations.Controller;
import net.plsar.annotations.Bind;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;
import net.plsar.model.NetworkRequest;
import net.plsar.model.NetworkResponse;
import net.plsar.security.SecurityManager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserRouter {

	@Bind
	UserRepo userRepo;

	@Bind
	SaleRepo saleRepo;

	@Bind
	DesignRepo designRepo;

	@Bind
	CategoryRepo categoryRepo;

	@Bind
	BusinessRepo businessRepo;

	@Bind
	SmsService smsService;

	@Bind
	UserService userService;

	@Bind
	SaleService saleService;

	@Get("/{shop}/signup")
	public String shopSignup(Cache cache,
							 NetworkRequest req,
							 SecurityManager security,
							 @Component String shopUri){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "redirect:/";
		Design design = designRepo.getBase(business.getId());
		SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);
		cache.set("design", design);
		cache.set("business", business);
		cache.set("siteService", siteService);
		cache.set("request", req);
		return "/pages/business/signup.jsp";
	}


	@Get("/{shop}/activity")
	public String getActivity(Cache cache,
							  NetworkRequest req,
							  SecurityManager security,
							  @Component String shopUri) throws ParseException {
		Business business = businessRepo.get(shopUri);
		if(business == null)return "redirect:/";

		if(!security.isAuthenticated(req)){
			return "redirect:/" + shopUri;
		}

		List<Sale> sales = new ArrayList();

		String credential = security.getUser(req);
		User authUser = userRepo.getPhone(credential);
		if(authUser == null){
			authUser = userRepo.getEmail(credential);
		}

		if(business.getAffiliate() == null ||
				!business.getAffiliate()) {
			sales = saleRepo.getListPrimary(business.getId());
		}else{
			sales = saleRepo.getListAffiliate(business.getId());
		}

		saleService.setSaleData(sales);

		SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);
		Design design = designRepo.getBase(business.getId());

		cache.set("design", design);
		cache.set("sales", sales);
		cache.set("business", business);
		cache.set("siteService", siteService);
		cache.set("request", req);
		return "/pages/business/activity.jsp";
	}

	@net.plsar.annotations.Design("/designs/auth.jsp")
	@Get("/users/edit/{businessId}/{id}")
	public String getEdit(Cache cache,
						  NetworkRequest req,
						  SecurityManager security,
						  @Component Long businessId,
						  @Component Long id){
		return userService.getEdit(id, businessId, cache, req, security);
	}


	@Post("/users/update/{businessId}/{id}")
	public String update(Cache cache,
						 NetworkRequest req,
						 SecurityManager security,
						 @Component Long businessId,
						 @Component Long id){
		return userService.update(id, businessId, cache, req, security);
	}

	@net.plsar.annotations.Design("/pages/guest.jsp")
	@Get("/users/reset")
	public String reset(){
		return "/pages/user/reset.jsp";
	}

	@Post("/users/send")
	public String send(Cache cache,
					   NetworkRequest req,
					   SecurityManager security){
		return userService.send(cache, req, security);
	}

	@Post("/users/reset/{id}")
	public String resetPassword(Cache cache,
								NetworkRequest req,
								NetworkResponse resp,
								SecurityManager security,
								@Component Long id){
    	return userService.resetPassword(id, cache, req, resp, security);
	}

	@Get("/clients/{id}")
	public String clients(Cache cache,
						  NetworkRequest req,
						  SecurityManager security,
						  @Component Long id){
		return userService.clients(id, cache, req, security);
	}

	@Get("/{shop}/users/password/get")
	public String getPassword(Cache cache,
							  NetworkRequest req,
							  SecurityManager security,
							  @Component String shopUri){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "redirect:/";

		Design design = designRepo.getBase(business.getId());
		if(design == null)return "redirect:/";

		SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);

		cache.set("design", design);
		cache.set("business", business);
		cache.set("siteService", siteService);
		cache.set("request", req);
		return "/pages/business/get_password.jsp";
	}

	@Post("/{shop}/users/password/send")
	public String sendPassword(Cache cache,
							   NetworkRequest req,
							   SecurityManager security,
							   @Component String shopUri){

		Business business = businessRepo.get(shopUri);
		if(business == null)return "redirect:/";

		User user = (User) req.inflect(User.class);
		String phone = Giga.getPhone(user.getPhone());
		if(phone.equals(""))return "redirect:/" + shopUri;

		User storedUser = userRepo.getPhone(phone);
		if(storedUser == null)return "redirect:/" + shopUri;

		String password = Giga.getString(6);
		storedUser.setPassword(security.hash(password));
		userRepo.updatePassword(storedUser);

		String message = business.getName() + " :: temporary password is " + password;
		RouteAttributes routeAttributes = new RouteAttributes();
		String smsKey = (String) routeAttributes.get("sms.key");
		smsService.send(smsKey, phone, message);

		cache.set("message", "Success! A password has been sent to you!");
		return "redirect:/" + shopUri + "/signin";
	}


	@Get("/{shop}/users/edit/{id}")
	public String editUser(Cache cache,
						   NetworkRequest req,
						   SecurityManager security,
						   @Component String shopUri,
						   @Component Long id){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "redirect:/";

		User user = userRepo.get(id);
		if(user == null)return "redirect:/";

		Design design = designRepo.getBase(business.getId());
		SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);

		cache.set("user", user);
		cache.set("design", design);
		cache.set("business", business);
		cache.set("siteService", siteService);
		cache.set("request", req);
		return "/pages/business/edit_user.jsp";
	}

	@Post("/{shop}/users/update")
	public String updateUser(Cache cache,
							 NetworkRequest req,
							 SecurityManager security,
							 @Component String shopUri){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "redirect:/";

		User user = (User) req.inflect(User.class);
		userRepo.update(user);

		cache.set("message", "Successfully updated your account!");
		return "redirect:/" + shopUri + "/users/edit/" + user.getId();
	}

	@Get("/{shop}/users/password/edit")
	public String editPassword(Cache cache,
							   NetworkRequest req,
							   SecurityManager security,
							   @Component String shopUri,
							   @Component Long id){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "redirect:/";

		Design design = designRepo.getBase(business.getId());
		if(design == null)return "redirect:/";

		User user = userRepo.get(id);
		if(user == null)return "redirect:/";

		SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);

		cache.set("user", user);
		cache.set("business", business);
		cache.set("design", design);
		cache.set("siteService", siteService);
		cache.set("request", req);
		return "/pages/business/edit_password.jsp";
	}


	@Post("/{shop}/users/password/save")
	public String savePassword(Cache cache,
							   NetworkRequest req,
							   SecurityManager security,
							   @Component String shopUri){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "redirect:/";

		User user = (User) req.inflect(User.class);
		if(user.getPassword().length() < 6){
			cache.set("message", "Passwords must be at least 6 characters long.");
			return "redirect:/"+ shopUri + "/users/password/edit";
		}

		if(!user.getPassword().equals("")){
			String password = security.hash(user.getPassword());
			user.setPassword(password);
			userRepo.updatePassword(user);
		}

		cache.set("message", "Successfully updated! You may now sign in with your new credentials.");
		return "redirect:/" + shopUri + "/signin";
	}
}