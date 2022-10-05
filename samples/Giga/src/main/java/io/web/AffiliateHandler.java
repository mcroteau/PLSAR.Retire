package io.web;

import io.service.AffiliateService;
import jakarta.servlet.http.HttpServletRequest;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.ResponseData;

@HttpHandler
public class AffiliateHandler {

    @Inject
    AffiliateService affiliateService;

    @Get("/affiliates/{{id}}")
    public String getAffiliates(ResponseData data,
                                @Variable Long id){
        return affiliateService.getAffiliates(id, data);
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
