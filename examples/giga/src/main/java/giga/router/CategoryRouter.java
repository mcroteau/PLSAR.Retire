package giga.router;

import giga.service.CategoryService;
import jakarta.servlet.http.HttpRequest;
import qio.annotate.HttpRouter;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

@HttpRouter
public class CategoryRouter {

    @Inject
    CategoryService categoryService;

    @Get("/{{business}}/{{category}}/items")
    public String getPage(HttpRequest req,
                          Cache cache,
                          @RouteComponent String business,
                          @RouteComponent String category){
        return categoryService.getItems(business.toLowerCase(), category.toLowerCase(), cache, req);
    }

    @Get("/categories/new/{{businessId}}")
    public String configure(Cache cache,
                            @RouteComponent Long businessId){
        return categoryService.create(businessId, data);
    }

    @Get("/categories/{{businessId}}")
    public String list(Cache cache,
                       @RouteComponent Long businessId) throws Exception{
        return categoryService.list(businessId, data);
    }

    @Post("/categories/save")
    public String save(HttpRequest req){
        return categoryService.save(req);
    }

    @Get("/categories/edit/{{businessId}}/{{id}}")
    public String showcase(Cache cache,
                           @RouteComponent Long businessId,
                           @RouteComponent Long id) throws Exception {
        return categoryService.edit(id, businessId, data);
    }

    @Post("/categories/update/{{businessId}}/{{id}}")
    public String update(HttpRequest req,
                         Cache cache,
                         @RouteComponent Long businessId,
                         @RouteComponent Long id){
        return categoryService.update(id, businessId, cache, req);
    }

    @Post("/categories/delete/{{businessId}}/{{id}}")
    public String delete(Cache cache,
                         @RouteComponent Long businessId,
                         @RouteComponent Long id){
        return categoryService.delete(id, businessId, data);
    }
}
