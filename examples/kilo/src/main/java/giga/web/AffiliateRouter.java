package giga.web;

import giga.service.AffiliateService;
import dev.blueocean.annotations.*;
import dev.blueocean.annotations.http.Get;
import dev.blueocean.annotations.http.Post;
import dev.blueocean.model.Cache;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.security.SecurityManager;

@Controller
public class AffiliateRouter {

    @Bind
    AffiliateService affiliateService;

    @Meta(design = "/designs/auth.jsp")
    @Get("/affiliates/{id}")
    public String getAffiliates(Cache cache,
                                NetworkRequest req,
                                SecurityManager security,
                                @Component Long id){
        return affiliateService.getAffiliates(id, cache, req, security);
    }

    @Design("/designs/guest.jsp")
    @Get("/affiliates/onboarding")
    public String getOnboarding(NetworkRequest req,
                                SecurityManager security,
                                Cache cache){
        return affiliateService.getOnboarding(cache, req);
    }

    @Design("/designs/auth.jsp")
    @Get("/affiliates/requests/{id}")
    public String getRequests(Cache cache,
                              NetworkRequest req,
                              SecurityManager security,
                              @Component Long id){
        return affiliateService.getRequests(id, cache, req, security);
    }

    @Design("/designs/partners.jsp")
    @Get("/affiliates/onboarding/status/{guid}")
    public String status(Cache cache,
                         @Component String guid){
        return affiliateService.status(guid, cache);
    }

    @Post("/affiliates/onboarding/begin")
    public String begin(Cache cache,
                        NetworkRequest req){
        return affiliateService.begin(cache, req);
    }

    @Post("/affiliates/onboarding/approve/{id}")
    public String approve(Cache cache,
                          NetworkRequest req,
                          SecurityManager security,
                          @Component Long id){
        return affiliateService.approve(id, cache, req, security);
    }

    @Post("/affiliates/onboarding/pass/{guid}")
    public String deny(Cache cache,
                       NetworkRequest req,
                       SecurityManager security,
                       @Component Long id){
        return affiliateService.deny(id, cache, req, security);
    }

    @Post("/affiliate/setup/{id}")
    public String setupAffiliate(Cache cache,
                                 NetworkRequest req,
                                 SecurityManager security,
                                 @Component Long id){
        return affiliateService.setupAffiliate(id);
    }

    @Design("/designs/partners.jsp")
    @Get("/affiliates/onboarding/finalize/{id}")
    public String finalizeOnboarding(Cache cache,
                                     @Component Long id){
        return affiliateService.finalizeOnboarding(id, cache);
    }

    @Post("/affiliates/onboarding/finalize/{id}")
    public String finalizeOnboarding(Cache cache,
                                     NetworkRequest req,
                                     SecurityManager security) {
        return affiliateService.finalizeOnboarding(cache, req, security);
    }
}
