package giga.web;

import giga.service.CategoryService;
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
public class CategoryRouter {

    @Bind
    CategoryService categoryService;

    @Get("/{business}/{category}/items")
    public String getPage(Cache cache,
                          NetworkRequest req,
                          SecurityManager security,
                          @Component String business,
                          @Component String category){
        return categoryService.getItems(business.toLowerCase(), category.toLowerCase(), cache, req, security);
    }

    @Get("/categories/new/{businessId}")
    public String configure(Cache cache,
                            NetworkRequest req,
                            SecurityManager security,
                            @Component Long businessId){
        return categoryService.create(businessId, cache, req, security);
    }

    @Get("/categories/{businessId}")
    public String list(Cache cache,
                       NetworkRequest req,
                       SecurityManager security,
                       @Component Long businessId) throws Exception{
        return categoryService.list(businessId, cache, req, security);
    }

    @Post("/categories/save")
    public String save(NetworkRequest req,
                       SecurityManager security){
        return categoryService.save(req, security);
    }

    @Design("/designs/auth.jsp")
    @Get("/categories/edit/{businessId}/{id}")
    public String showcase(Cache cache,
                           NetworkRequest req,
                           SecurityManager security,
                           @Component Long businessId,
                           @Component Long id){
        return categoryService.edit(id, businessId, cache, req, security);
    }

    @Post("/categories/update/{businessId}/{id}")
    public String update(Cache cache,
                         NetworkRequest req,
                         SecurityManager security,
                         @Component Long businessId,
                         @Component Long id){
        return categoryService.update(id, businessId, cache, req, security);
    }

    @Post("/categories/delete/{businessId}/{id}")
    public String delete(Cache cache,
                         NetworkRequest req,
                         SecurityManager security,
                         @Component Long businessId,
                         @Component Long id){
        return categoryService.delete(id, businessId, cache, req, security);
    }
}
