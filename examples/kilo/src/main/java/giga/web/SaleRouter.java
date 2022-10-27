package giga.web;

import giga.service.SaleService;
import dev.blueocean.annotations.Component;
import dev.blueocean.annotations.Design;
import dev.blueocean.annotations.Controller;
import dev.blueocean.annotations.Bind;
import dev.blueocean.annotations.http.Get;
import dev.blueocean.annotations.http.Post;
import dev.blueocean.model.Cache;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.security.SecurityManager;

@Controller
public class SaleRouter {

    @Bind
    SaleService saleService;

    @Design("/designs/auth.jsp")
    @Get("/sales/{businessId}")
    public String list(Cache cache,
                       NetworkRequest req,
                       SecurityManager security,
                       @Component Long businessId) throws Exception{
        return saleService.list(businessId, cache, req, security);
    }

    @Post("/{business}/sale/{id}")
    public String processSale(Cache cache,
                              NetworkRequest req,
                              @Component String businessUri,
                              @Component Long id){
        return saleService.processSale(id, businessUri.toLowerCase(), req);
    }

    @Get("/{business}/sale/{id}")
    public String getSale(Cache cache,
                          NetworkRequest req,
                          SecurityManager security,
                          @Component String businessUri,
                          @Component Long id){
        return saleService.getSale(id, businessUri.toLowerCase(), cache, req, security);
    }

}
