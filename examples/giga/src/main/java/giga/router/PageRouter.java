package giga.router;

import giga.Giga;
import giga.model.Business;
import giga.model.Design;
import giga.model.Page;
import giga.model.User;
import giga.service.PageService;
import jakarta.servlet.http.HttpRequest;
import qio.annotate.HttpRouter;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

import java.util.List;

@HttpRouter
public class PageRouter {

    @Inject
    PageService pageService;

    @Get("/{{business}}")
    public String getHome(HttpRequest req,
                          Cache cache,
                          @RouteComponent String business){
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]";
        }

        Page activePage = pageRepo.get(business.getId(), page);
        if(activePage == null){
            return "[redirect]/" + businessUri + "";
        }

        cache.set("request", req);
        cache.set("siteService", siteService);
        cache.set("business", business);
        cache.set("page", activePage);
        return "/pages/page/index.jsp";
    }

    @Get("/{{business}}/asset/{{page}}")
    public String getPage(HttpRequest req,
                          Cache cache,
                          @RouteComponent String business,
                          @RouteComponent String page){
        System.out.println("get page");
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]";
        }

        Page activePage = pageRepo.get(business.getId(), page);
        if(activePage == null){
            return "[redirect]/" + businessUri + "";
        }

        cache.set("request", req);
        cache.set("siteService", siteService);
        cache.set("business", business);
        cache.set("page", activePage);
        return "/pages/page/index.jsp";
    }

    @Get("/pages/new/{{businessId}}")
    public String configure(Cache cache,
                            @RouteComponent Long businessId){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);
        List<Design> designs = designRepo.getList(businessId);
        cache.set("designs", designs);
        cache.set("page", "/pages/page/new.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/pages/{{businessId}}")
    public String list(Cache cache,
                       @RouteComponent Long businessId){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

        List<Page> pages = pageRepo.getList(businessId);
        cache.set("pages", pages);
        cache.set("page", "/pages/page/list.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/pages/save")
    public String save(HttpRequest req) throws Exception {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        User authUser = authService.getUser();

        Page page = (Page) Qio.get(req, Page.class);
        pageRepo.save(page);
        Page savedAsset = pageRepo.getSaved();
        String permission = Giga.PAGE_MAINTENANCE + savedAsset.getId();
        userRepo.savePermission(authUser.getId(), permission);

        return "[redirect]/pages/" + savedAsset.getBusinessId();
    }

    @Post("/pages/delete/{{businessId}}/{{id}}")
    public String delete(Cache cache,
                         @RouteComponent Long businessId,
                         @RouteComponent Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.PAGE_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Whoa, people might be using this. Lol, this isn't yours.");
            return "[redirect]/";
        }

        pageRepo.delete(id);
        cache.set("message", "Successfully deleted asset.");

        return "[redirect]/pages/" + businessId;
    }

}
