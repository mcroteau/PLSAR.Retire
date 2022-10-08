package giga.router;

import giga.Giga;
import giga.model.Business;
import giga.model.Sale;
import giga.model.User;
import giga.repo.*;
import giga.service.AffiliateService;
import giga.service.AuthService;
import giga.service.BusinessService;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Inject;
import net.plsar.annotations.Meta;
import net.plsar.annotations.RouteComponent;
import net.plsar.annotations.http.Get;
import net.plsar.model.Cache;

import java.math.BigDecimal;
import java.util.List;

import net.plsar.model.HttpRequest;
import net.plsar.security.SecurityManager;

@HttpRouter
public class AffiliateRouter {

    @Inject
    UserRepo userRepo;

    @Inject
    SaleRepo saleRepo;

    @Inject
    BusinessRepo businessRepo;

    AffiliateService affiliateService;

    public AffiliateRouter(){
        this.affiliateService = new AffiliateService();
    }


    @Meta(design="/designs/auth.jsp")
    @Get("/affiliates/{id}")
    public String getAffiliates(Cache cache,
                                HttpRequest httpRequest,
                                @RouteComponent Long id,
                                SecurityManager security){
        if(!security.userIsAuthenticated(httpRequest)){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, httpRequest) &&
                !security.hasPermission(permission, httpRequest)){
            return "[redirect]/";
        }

        BusinessService businessService = new BusinessService();
        Business business = businessRepo.get(id);
        User user = userRepo.get(business.getUserId());
        List<Business> affiliates = businessRepo.getListAffiliate(id);
        for(Business affiliate : affiliates){
            List<Sale> sales = saleRepo.getListAffiliate(affiliate.getId());
            BigDecimal salesTotal = new BigDecimal(0);
            for(Sale sale: sales){
                BigDecimal affiliateAmount = new BigDecimal(sale.getAffiliateAmount());
                affiliateAmount = affiliateAmount.movePointLeft(2);
                System.out.println("am " + affiliateAmount);
                salesTotal.add(affiliateAmount);
            }
            affiliate.setSalesTotal(salesTotal);
        }

        cache.set("affiliates", affiliates);
        businessService.setData(id, cache);
        return "/pages/affiliate/list.jsp";
    }

    @Meta(design="/designs/guest.jsp")
    @Get("/affiliates/onboarding")
    public String getOnboarding(HttpRequest httpRequest,
                                Cache data){
        List<Business> businesses = businessRepo.getListPrimary();
        data.set("businesses", businesses);
        data.set("title", "Giga! Partners Signup");
        return "/pages/affiliate/onboarding.jsp";
    }

    @Get("/affiliates/requests/{{id}}")
    public String getRequests(Cache data,
                                @Variable Long id){
        return affiliateService.getRequests(id, data);
    }

    @Get("/affiliates/onboarding/status/{{guid}}")
    public String status(Cache data,
                        @Variable String guid){
        return affiliateService.status(guid, data);
    }

    @Post("/affiliates/onboarding/begin")
    public String begin(HttpServletRequest req,
                       Cache data){
        return affiliateService.begin(data, req);
    }

    @Post("/affiliates/onboarding/approve/{{id}}")
    public String approve(Cache data,
                          @Variable Long id){
        return affiliateService.approve(id, data);
    }

    @Post("/affiliates/onboarding/pass/{{guid}}")
    public String deny(Cache data,
                       @Variable Long id){
        return affiliateService.deny(id, data);
    }

    @Post("/affiliate/setup/{{id}}")
    public String setupAffiliate(Cache data,
                                 @Variable Long id){
        return affiliateService.setupAffiliate(id, data);
    }

    @Get("/affiliates/onboarding/finalize/{{id}}")
    public String finalizeOnboarding(Cache data,
                                    @Variable Long id){
        return affiliateService.finalizeOnboarding(id, data);
    }

    @Post("/affiliates/onboarding/finalize/{{id}}")
    public String finalizeOnboarding(HttpServletRequest req,
                                     Cache data,
                                     @Variable Long id) throws Exception {
        return affiliateService.finalizeOnboarding(data, req);
    }

}
