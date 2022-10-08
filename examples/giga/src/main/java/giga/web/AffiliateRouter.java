package giga.web;

import giga.repo.*;
import giga.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import net.plsar.annotations.HttpRouter;
import net.plsar.model.Cache;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.ResponseData;

@HttpRouter
public class AffiliateRouter {

    @Bind
    UserRepo userRepo;

    @Bind
    RoleRepo roleRepo;

    @Bind
    SaleRepo saleRepo;

    @Bind
    ItemRepo itemRepo;

    @Bind
    DesignRepo designRepo;

    @Bind
    CategoryRepo categoryRepo;

    @Bind
    AuthService authService;

    @Bind
    BusinessRepo businessRepo;


    @Get("/affiliates/{id}")
    public String getAffiliates(Cache cache,
                                @Variable Long id){

    }

    @Get("/affiliates/onboarding")
    public String getOnboarding(HttpServletRequest req,
                                ResponseData data){
        return affiliateService.getOnboarding(data, req);
    }

    @Get("/affiliates/requests/{{id}}")
    public String getRequests(ResponseData data,
                                @Variable Long id){
        return affiliateService.getRequests(id, data);
    }

    @Get("/affiliates/onboarding/status/{{guid}}")
    public String status(ResponseData data,
                        @Variable String guid){
        return affiliateService.status(guid, data);
    }

    @Post("/affiliates/onboarding/begin")
    public String begin(HttpServletRequest req,
                       ResponseData data){
        return affiliateService.begin(data, req);
    }

    @Post("/affiliates/onboarding/approve/{{id}}")
    public String approve(ResponseData data,
                          @Variable Long id){
        return affiliateService.approve(id, data);
    }

    @Post("/affiliates/onboarding/pass/{{guid}}")
    public String deny(ResponseData data,
                       @Variable Long id){
        return affiliateService.deny(id, data);
    }

    @Post("/affiliate/setup/{{id}}")
    public String setupAffiliate(ResponseData data,
                                 @Variable Long id){
        return affiliateService.setupAffiliate(id, data);
    }

    @Get("/affiliates/onboarding/finalize/{{id}}")
    public String finalizeOnboarding(ResponseData data,
                                    @Variable Long id){
        return affiliateService.finalizeOnboarding(id, data);
    }

    @Post("/affiliates/onboarding/finalize/{{id}}")
    public String finalizeOnboarding(HttpServletRequest req,
                                     ResponseData data,
                                     @Variable Long id) throws Exception {
        return affiliateService.finalizeOnboarding(data, req);
    }

}
