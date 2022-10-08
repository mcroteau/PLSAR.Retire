package giga.web;

import giga.model.Business;
import giga.model.User;
import giga.model.UserBusiness;
import giga.repo.BusinessRepo;
import giga.service.AuthService;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Property;
import qio.annotate.verbs.Get;
import qio.model.web.ResponseData;

import java.util.List;

@HttpHandler
public class BasicHandler {

	@Property("stripe.key")
	String stripeKey;

	@Inject
	AuthService authService;

	@Inject
	BusinessRepo businessRepo;

	@Get("/")
	public String index(){
		if(authService.isAuthenticated()){
			User authUser = authService.getUser();
			List<UserBusiness> userBusinesses = businessRepo.getListUsers(authUser.getId());
			if (userBusinesses.size() > 0) {
				Business business = null;
				for (UserBusiness userBusiness : userBusinesses) {
					business = businessRepo.get(userBusiness.getBusinessId());
					break;
				}
				return "[redirect]/snapshot/" + business.getId();
			} else {
				return "[redirect]/businesses/setup";
			}
		}
		return "[redirect]/home";
	}

	@Get("/home")
	public String home(ResponseData data){
		List<Business> businesses = businessRepo.getList();
		data.put("businesses", businesses);
		data.set("title", "A Marketplace for Marketplaces.");
		data.set("page", "/pages/index.jsp");
		return "/designs/guest.jsp";
	}

}