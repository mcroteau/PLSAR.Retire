package io.web;

import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;
import io.Giga;
import io.model.Business;
import io.model.User;
import io.model.UserBusiness;
import io.repo.BusinessRepo;
import io.service.AuthService;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Property;
import qio.annotate.verbs.Get;
import qio.model.web.ResponseData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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