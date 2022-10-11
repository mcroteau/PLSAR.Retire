package giga.router;

import giga.Giga;
import giga.model.Business;
import giga.model.Design;
import giga.model.Page;
import giga.model.User;
import giga.repo.*;
import giga.service.BusinessService;
import giga.service.SiteService;
import net.plsar.annotations.Component;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Inject;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;
import net.plsar.model.HttpRequest;
import net.plsar.security.SecurityManager;
import java.util.List;

@HttpRouter
public class PageRouter {

    @Inject
    UserRepo userRepo;

    @Inject
    PageRepo pageRepo;

    @Inject
    DesignRepo designRepo;

    @Inject
    CategoryRepo categoryRepo;

    @Inject
    BusinessRepo businessRepo;

    BusinessService businessService;

    public PageRouter(){
        this.businessService = new BusinessService();
    }

    @Get("/{{business}}")
    public String getHome(Cache cache,
                          HttpRequest req,
                          SecurityManager security,
                          @Component String businessUri){
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]";
        }

        Page activePage = pageRepo.get(business.getId(), "home");
        if(activePage == null){
            return "[redirect]/" + businessUri + "";
        }

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);

        cache.set("request", req);
        cache.set("siteService", siteService);
        cache.set("business", business);
        cache.set("page", activePage);
        return "/pages/page/index.jsp";
    }

    @Get("/{{businessUri}}/asset/{{page}}")
    public String getPage(Cache cache,
                          HttpRequest req,
                          SecurityManager security,
                          @Component String businessUri,
                          @Component String page){

        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]";
        }

        Page activePage = pageRepo.get(business.getId(), page);
        if(activePage == null){
            return "[redirect]/" + businessUri + "";
        }

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);

        cache.set("request", req);
        cache.set("siteService", siteService);
        cache.set("business", business);
        cache.set("page", activePage);
        return "/pages/page/index.jsp";
    }

    @Get("/pages/new/{{businessId}}")
    public String configure(Cache cache,
                            HttpRequest req,
                            SecurityManager security,
                            @Component Long businessId){
        if(!security.isAuthenticated(req)){
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
                       HttpRequest req,
                       SecurityManager security,
                       @Component Long businessId){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

        List<Page> pages = pageRepo.getList(businessId);
        cache.set("pages", pages);
        cache.set("page", "/pages/page/list.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/pages/save")
    public String save(Cache cache,
                       HttpRequest req,
                       SecurityManager security) throws Exception {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        Page page = (Page) req.inflect(Page.class);
        pageRepo.save(page);
        Page savedAsset = pageRepo.getSaved();
        String permission = Giga.PAGE_MAINTENANCE + savedAsset.getId();
        userRepo.savePermission(authUser.getId(), permission);

        return "[redirect]/pages/" + savedAsset.getBusinessId();
    }

    @Post("/pages/delete/{{businessId}}/{{id}}")
    public String delete(Cache cache,
                         HttpRequest req,
                         SecurityManager security,
                         @Component Long businessId,
                         @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.PAGE_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Whoa, people might be using this. Lol, this isn't yours.");
            return "[redirect]/";
        }

        pageRepo.delete(id);
        cache.set("message", "Successfully deleted asset.");

        return "[redirect]/pages/" + businessId;
    }

}
