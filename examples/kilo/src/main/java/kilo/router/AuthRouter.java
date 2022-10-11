package kilo.router;

import kilo.Giga;
import kilo.model.Business;
import kilo.model.Design;
import kilo.model.User;
import kilo.repo.BusinessRepo;
import kilo.repo.CategoryRepo;
import kilo.repo.DesignRepo;
import kilo.repo.UserRepo;
import kilo.service.SiteService;
import net.plsar.annotations.Component;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Inject;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;
import net.plsar.model.HttpRequest;
import net.plsar.model.HttpResponse;
import net.plsar.security.SecurityManager;

@HttpRouter
public class AuthRouter {

	@Inject
	UserRepo userRepo;

	@Inject
	CategoryRepo categoryRepo;

	@Inject
	DesignRepo designRepo;

	@Inject
	BusinessRepo businessRepo;


	@Post("/authenticate")
	public String signin(Cache cache,
						 HttpRequest req,
						 HttpResponse resp,
						 SecurityManager security){

		try{

			security.signout(req, resp);

			String credential = req.getValue("username");
			if(credential != null)credential = Giga.getSpaces(credential);

			String passwordDirty = req.getValue("password");
			if(!security.signin(credential, passwordDirty, req, resp)){
				cache.set("message", "Wrong username and password");
				return "[redirect]/signin";
			}

			User authUser = userRepo.get(credential);
			if(authUser == null){
				authUser = userRepo.getPhone(credential);
			}

			req.getSession(true).set("username", authUser.getUsername());
			req.getSession(true).set("userId", authUser.getId());

		} catch ( Exception e ) {
			e.printStackTrace();
			cache.set("message", "Please yell at one of us, something is a little off!");
			return "[redirect]/";
		}

		return "[redirect]/";
	}

	@Get("/signin")
	public String signin(Cache cache){
		cache.set("page", "/pages/signin.jsp");
		return "/designs/guest.jsp";
	}

	@Get("/{{shop}}/signin")
	public String shopSignin(Cache cache,
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
		return "/pages/business/signin.jsp";
	}

	@Post("/{{shop}}/signin")
	public String shopAuthenticate(Cache cache,
								   HttpRequest req,
								   HttpResponse resp,
								   SecurityManager security,
								   @Component String shopUri){
		try{

			security.signout(req, resp);

			String credential = req.getValue("username");
			if(credential != null)credential = Giga.getSpaces(credential);

			String passwordDirty = req.getValue("password");
			if(!security.signin(credential, passwordDirty, req, resp)){
				cache.set("message", "Wrong username and password");
				return "[redirect]/signin";
			}

			User authUser = userRepo.get(credential);
			if(authUser == null){
				authUser = userRepo.getPhone(credential);
			}

			req.getSession(true).set("username", authUser.getUsername());
			req.getSession(true).set("userId", authUser.getId());

		} catch ( Exception e ) {
			e.printStackTrace();
			cache.set("message", "Please yell at one of us, something is a little off!");
			return "[redirect]/";
		}

		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";
		return "[redirect]/" + shopUri;
	}

	@Get("/signup")
	public String signup(Cache cache){
		cache.set("page", "/pages/signup.jsp");
		return "/designs/guest.jsp";
	}

	@Get("/signout")
	public String signout(Cache cache,
						  HttpRequest req,
						  HttpResponse resp,
						  SecurityManager security){
		security.signout(req, resp);
		cache.set("message", "Successfully signed out");
		req.getSession(true).set("username", "");
		req.getSession(true).set("userId", "");
		return "[redirect]/";
	}

	@Get("/{{shop}}/signout")
	public String shopSignout(Cache cache,
							  HttpRequest req,
							  HttpResponse resp,
							  SecurityManager security,
							  @Component String shopUri){
		security.signout(req, resp);
		cache.set("message", "Successfully signed out");
		req.getSession(true).set("username", "");
		req.getSession(true).set("userId", "");
		return "[redirect]/" + shopUri;
	}

}