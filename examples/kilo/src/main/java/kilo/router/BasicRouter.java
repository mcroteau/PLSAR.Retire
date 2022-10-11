package kilo.router;

import kilo.model.Business;
import kilo.model.User;
import kilo.model.UserBusiness;
import kilo.repo.BusinessRepo;
import kilo.repo.UserRepo;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Inject;
import net.plsar.annotations.http.Get;
import net.plsar.model.Cache;
import net.plsar.model.HttpRequest;
import net.plsar.model.HttpResponse;
import net.plsar.security.SecurityManager;

import java.util.List;

@HttpRouter
public class BasicRouter {

	@Inject
	UserRepo userRepo;

	@Inject
	BusinessRepo businessRepo;

	@Get("/")
	public String index(Cache cache,
						HttpRequest req,
						HttpResponse resp,
						SecurityManager security){
		if(security.isAuthenticated(req)){
			String credential = security.getUser(req);
			User authUser = userRepo.get(credential);
			if(authUser == null){
				authUser = userRepo.getPhone(credential);
			}
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