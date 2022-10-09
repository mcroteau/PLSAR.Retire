package giga.router;

import giga.Giga;
import giga.model.Business;
import giga.model.Design;
import giga.model.User;
import giga.repo.BusinessRepo;
import giga.repo.DesignRepo;
import giga.service.SiteService;
import jakarta.servlet.http.HttpRequest;
import giga.service.AuthService;
import qio.annotate.HttpRouter;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

@HttpRouter
public class AuthRouter {

	@Inject
    AuthService authService;

	@Inject
	SiteService siteService;

	@Inject
	DesignRepo designRepo;

	@Inject
	BusinessRepo businessRepo;


	@Post("/authenticate")
	public String signin(HttpRequest req,
							   Cache cache){

		try{

			signout();

			String credential = req.getParameter("username");
			if(credential != null)credential = Giga.getSpaces(credential);

			String passwordDirty = req.getParameter("password");
			if(!signin(credential, passwordDirty)){
				cache.set("message", "Wrong username and password");
				return "[redirect]/signin";
			}

			User authUser = userRepo.get(credential);
			if(authUser == null){
				authUser = userRepo.getPhone(credential);
			}

			req.getSession().setAttribute("username", authUser.getUsername());
			req.getSession().setAttribute("userId", authUser.getId());

		} catch ( Exception e ) {
			e.printStackTrace();
			cache.set("message", "Please yell at one of us, something is a little off!");
			return "[redirect]/";
		}

		return "[redirect]/";
	}

	@Get("/signin")
	public String signin(Cache cache){
		data.set("page", "/pages/signin.jsp");
		return "/designs/guest.jsp";
	}

	@Get("/{{shop}}/signin")
	public String shopSignin(HttpRequest qer,
							 Cache cache,
							 @RouteComponent String shopUri){
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";
		Design design = designRepo.getBase(business.getId());
		data.set("design", design);
		data.set("business", business);
		data.set("siteService", siteService);
		data.set("request", qer);
		return "/pages/business/signin.jsp";
	}

	@Post("/{{shop}}/signin")
	public String shopAuthenticate(HttpRequest req,
							   Cache cache,
							   @RouteComponent String shopUri){
		authService.authenticate(cache, req);
		Business business = businessRepo.get(shopUri);
		if(business == null)return "[redirect]/";
		return "[redirect]/" + shopUri;
	}

	@Get("/signup")
	public String signup(Cache cache){
		data.set("page", "/pages/signup.jsp");
		return "/designs/guest.jsp";
	}

	@Get("/signout")
	public String signout(HttpRequest req,
						  Cache cache){
		signout();
		cache.set("message", "Successfully signed out");
		req.getSession().setAttribute("username", "");
		req.getSession().setAttribute("userId", "");
		return "[redirect]/";
	}

	@Get("/{{shop}}/signout")
	public String shopSignout(HttpRequest req,
							  Cache cache,
							  @RouteComponent String shopUri){
		signout();
		cache.set("message", "Successfully signed out");
		req.getSession().setAttribute("username", "");
		req.getSession().setAttribute("userId", "");
		return "[redirect]/" + shopUri;
	}

}