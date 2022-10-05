package io.web;

import chico.Chico;
import io.Giga;
import io.model.*;
import io.repo.*;
import io.service.*;
import jakarta.servlet.http.HttpServletRequest;
import qio.Qio;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.ResponseData;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


@HttpHandler
public class UserHandler {

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
	public String shopSignup(HttpServletRequest req,
							 ResponseData data,
							 @Variable String shopUri){
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
	public String getActivity(HttpServletRequest req,
							 ResponseData data,
							 @Variable String shopUri) throws ParseException {
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
	public String getEdit(ResponseData data,
						  @Variable Long businessId,
						  @Variable Long id){
		return userService.getEdit(id, businessId, data);
	}


	@Post("/users/update/{{businessId}}/{{id}}")
	public String update(HttpServletRequest req,
						 ResponseData data,
						 @Variable Long businessId,
						 @Variable Long id){
		return userService.update(id, businessId, data, req);
	}

	@Get("/users/reset")
	public String reset(ResponseData data){
		data.set("page", "/pages/user/reset.jsp");
		return "/designs/guest.jsp";
	}

	@Post("/users/send")
	public String send(HttpServletRequest req,
							ResponseData data){
		return userService.send(data, req);
	}

	@Post("/users/reset/{{id}}")
	public String resetPassword(HttpServletRequest req,
								ResponseData data,
								@Variable Long id){
    	return userService.resetPassword(id, data, req);
	}

	@Get("/clients/{{id}}")
	public String clients(ResponseData data,
							@Variable Long id){
		return userService.clients(id, data);
	}

	@Get("/{{shop}}/users/password/get")
	public String getPassword(HttpServletRequest req,
							  ResponseData data,
							  @Variable String shopUri){
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
	public String sendPassword(HttpServletRequest req,
							   ResponseData data,
							   @Variable String shopUri){

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
	public String editUser(HttpServletRequest req,
							 ResponseData data,
							 @Variable String shopUri,
						     @Variable Long id){
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
	public String updateUser(HttpServletRequest req,
							   ResponseData data,
							   @Variable String shopUri){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";

		User user = (User) qio.set(req, User.class);
		userRepo.update(user);

		data.set("message", "Successfully updated your account!");
		return "[redirect]/" + shopUri + "/users/edit/" + user.getId();
	}

	@Get("/{{shop}}/users/password/edit")
	public String editPassword(HttpServletRequest req,
							   ResponseData data,
							   @Variable String shopUri,
							   @Variable Long id){
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
	public String savePassword(HttpServletRequest req,
							  ResponseData data,
							  @Variable String shopUri){
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