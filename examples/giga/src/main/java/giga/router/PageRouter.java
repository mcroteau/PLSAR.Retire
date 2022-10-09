package giga.router;

import giga.service.PageService;
import jakarta.servlet.http.HttpRequest;
import qio.annotate.HttpRouter;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

@HttpRouter
public class PageRouter {

    @Inject
    PageService pageService;

    @Get("/{{business}}")
    public String getHome(HttpRequest req,
                          Cache cache,
                          @RouteComponent String business){
        return pageService.getPage(business.toLowerCase(), "home", cache, req);
    }

    @Get("/{{business}}/asset/{{page}}")
    public String getPage(HttpRequest req,
                          Cache cache,
                          @RouteComponent String business,
                          @RouteComponent String page){
        System.out.println("get page");
        return pageService.getPage(business.toLowerCase(), page.toLowerCase(), cache, req);
    }

    @Get("/pages/new/{{businessId}}")
    public String configure(Cache cache,
                            @RouteComponent Long businessId){
        return pageService.create(businessId, data);
    }

    @Get("/pages/{{businessId}}")
    public String list(Cache cache,
                       @RouteComponent Long businessId){
        return pageService.list(businessId, data);
    }

    @Post("/pages/save")
    public String save(HttpRequest req) throws Exception {
        return pageService.save(req);
    }

    @Post("/pages/delete/{{businessId}}/{{id}}")
    public String delete(Cache cache,
                         @RouteComponent Long businessId,
                         @RouteComponent Long id){
        return pageService.delete(id, businessId, data);
    }

}
