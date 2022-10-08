package giga.router;

import giga.model.Business;
import giga.model.Design;
import giga.repo.BusinessRepo;
import giga.repo.DesignRepo;
import giga.service.SiteService;
import jakarta.servlet.http.HttpRequest;
import giga.service.AuthService;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

@HttpHandler
public class AuthHandler {

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
							   Cache data){
		return authService.authenticate(cache, req);
	}

	@Get("/signin")
	public String signin(Cache data){
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
	public String signup(Cache data){
		data.set("page", "/pages/signup.jsp");
		return "/designs/guest.jsp";
	}

	@Get("/signout")
	public String signout(HttpRequest req,
						  Cache data){
		return authService.deAuthenticate(cache, req);
	}

	@Get("/{{shop}}/signout")
	public String shopSignout(HttpRequest req,
							  Cache cache,
							  @RouteComponent String shopUri){
		authService.deAuthenticate(cache, req);
		return "[redirect]/" + shopUri;
	}

}