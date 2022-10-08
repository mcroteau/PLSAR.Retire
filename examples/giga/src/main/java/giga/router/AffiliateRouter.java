package giga.router;

import giga.Giga;
import giga.model.Business;
import giga.model.Sale;
import giga.model.User;
import giga.repo.*;
import giga.service.AffiliateService;
import giga.service.BusinessService;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Inject;
import net.plsar.annotations.Meta;
import net.plsar.annotations.Component;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
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
                                @Component Long id,
                                SecurityManager security){
        if(!security.isAuthenticated(httpRequest)){
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

    @Get("/affiliates/onboarding")
    public String getOnboarding(HttpRequest req,
                                Cache data){
        return affiliateService.getOnboarding(cache, req);
    }

    @Get("/affiliates/requests/{{id}}")
    public String getRequests(@Component Long id,
                                Cache cache,
                                HttpRequest httpRequest,
                                SecurityManager security){
        return affiliateService.getRequests(id, cache, httpRequest, security);
    }

    @Get("/affiliates/onboarding/status/{{guid}}")
    public String status(Cache cache,
                        @Component String guid){
        return affiliateService.status(guid, data);
    }

    @Post("/affiliates/onboarding/begin")
    public String begin(HttpRequest req,
                       Cache data){
        return affiliateService.begin(cache, req);
    }

    @Post("/affiliates/onboarding/approve/{id}")
    public String approve(Cache cache,
                          @Component Long id){
        return affiliateService.approve(id, data);
    }

    @Post("/affiliates/onboarding/pass/{guid}")
    public String deny(Cache cache,
                       @Component Long id){
        return affiliateService.deny(id, data);
    }

    @Post("/affiliate/setup/{id}")
    public String setupAffiliate(@Component Long id){
        return affiliateService.setupAffiliate(id);
    }

    @Get("/affiliates/onboarding/finalize/{id}")
    public String finalizeOnboarding(Cache cache,
                                    @Component Long id){
        return affiliateService.finalizeOnboarding(id, data);
    }

    @Post("/affiliates/onboarding/finalize/{id}")
    public String finalizeOnboarding(Cache cache, HttpRequest req, SecurityManager security) throws Exception {
        return affiliateService.finalizeOnboarding(cache, req, security);
    }

}
