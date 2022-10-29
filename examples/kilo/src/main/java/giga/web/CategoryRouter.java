package giga.web;

import giga.service.CategoryService;
import net.plsar.annotations.Component;
import net.plsar.annotations.Design;
import net.plsar.annotations.Controller;
import net.plsar.annotations.Bind;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;
import net.plsar.model.NetworkRequest;
import net.plsar.security.SecurityManager;

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
