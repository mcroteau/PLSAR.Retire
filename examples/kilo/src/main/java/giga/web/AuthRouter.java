package giga.web;

import giga.model.Business;
import giga.model.Design;
import giga.repo.BusinessRepo;
import giga.repo.CategoryRepo;
import giga.repo.DesignRepo;
import giga.repo.UserRepo;
import giga.service.SiteService;
import net.plsar.annotations.Component;
import net.plsar.annotations.Controller;
import net.plsar.annotations.Bind;
import net.plsar.annotations.Meta;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;
import net.plsar.model.NetworkRequest;
import net.plsar.model.NetworkResponse;
import net.plsar.security.SecurityManager;

@Controller
public class AuthRouter {

	@Bind
	UserRepo userRepo;

	@Bind
	DesignRepo designRepo;

	@Bind
	CategoryRepo categoryRepo;

	@Bind
	BusinessRepo businessRepo;


	@net.plsar.annotations.Design("/designs/guest.jsp")
	@Get("/signin")
	public String signin(Cache cache){
		return "/pages/signin.jsp";
	}

	@Post("/authenticate")
	public String signin(Cache cache,
			             NetworkRequest req,
						 NetworkResponse resp,
						 SecurityManager security){

		String credential = req.getValue("username");
		String password = req.getValue("password");

		if(!security.signin(credential, password, req, resp)){
			cache.set("message", "incorrect email, phone & password combination");
			return "redirect:/signin";
		}
		return "redirect:/";
	}

	@Get("/{shop}/signin")
	public String shopSignin(Cache cache,
							 NetworkRequest req,
							 SecurityManager security,
							 @Component String shopUri){

		SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);
		Business business = businessRepo.get(shopUri);
		if(business == null)return "redirect:/";
		Design design = designRepo.getBase(business.getId());
		cache.set("design", design);
		cache.set("business", business);
		cache.set("siteService", siteService);
		cache.set("request", req);
		return "/pages/business/signin.jsp";
	}

	@Post("/{shop}/signin")
	public String shopAuthenticate(NetworkRequest req,
								   Cache cache,
								   @Component String shopUri){

		Business business = businessRepo.get(shopUri);
		if(business == null)return "redirect:/";
		return "redirect:/" + shopUri;
	}

	@Meta(design = "/designs/guest.jsp")
	@Get("/signup")
	public String signup(Cache cache){
		return "/pages/signup.jsp";
	}

	@Get("/signout")
	public String signout(NetworkRequest req,
						  NetworkResponse resp,
						  SecurityManager security){
		security.signout(req, resp);
		return "redirect:/";
	}

	@Get("/{shop}/signout")
	public String shopSignout(Cache cache,
							  NetworkRequest req,
							  NetworkResponse resp,
							  SecurityManager security,
							  @Component String shopUri){
		security.signout(req, resp);
		return "redirect:/" + shopUri;
	}

}