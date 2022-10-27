package giga.web;

import giga.service.PageService;
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
public class PageRouter {

    @Bind
    PageService pageService;

    @Get("/{business}")
    public String getHome(Cache cache,
                          NetworkRequest req,
                          SecurityManager security,
                          @Component String business){
        return pageService.getPage(business.toLowerCase(), "home", cache, req, security);
    }

    @Get("/{business}/asset/{page}")
    public String getPage(Cache cache,
                          NetworkRequest req,
                          SecurityManager security,
                          @Component String business,
                          @Component String page){
        System.out.println("get page");
        return pageService.getPage(business.toLowerCase(), page.toLowerCase(), cache, req, security);
    }

    @Design("/designs/auth.jsp")
    @Get("/pages/new/{businessId}")
    public String configure(Cache cache,
                            NetworkRequest req,
                            SecurityManager security,
                            @Component Long businessId){
        return pageService.create(businessId, cache, req, security);
    }

    @Design("/designs/auth.jsp")
    @Get("/pages/{businessId}")
    public String list(Cache cache,
                       NetworkRequest req,
                       SecurityManager security,
                       @Component Long businessId){
        return pageService.list(businessId, cache, req, security);
    }

    @Post("/pages/save")
    public String save(Cache cache,
                       NetworkRequest req,
                       SecurityManager security) throws Exception {
        return pageService.save(req, security);
    }

    @Post("/pages/delete/{businessId}/{id}")
    public String delete(Cache cache,
                         NetworkRequest req,
                         SecurityManager security,
                         @Component Long businessId,
                         @Component Long id){
        return pageService.delete(id, businessId, cache, req, security);
    }

}
