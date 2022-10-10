package giga.router;

import giga.model.Business;
import giga.model.User;
import giga.model.UserBusiness;
import giga.repo.BusinessRepo;
import giga.service.AuthService;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Inject;
import net.plsar.annotations.http.Get;
import net.plsar.model.Cache;

import java.util.List;

@HttpRouter
public class BasicRouter {

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
	public String home(Cache cache){
		List<Business> businesses = businessRepo.getList();
		cache.set("businesses", businesses);
		cache.set("title", "A Marketplace for Marketplaces.");
		cache.set("page", "/pages/index.jsp");
		return "/designs/guest.jsp";
	}

}