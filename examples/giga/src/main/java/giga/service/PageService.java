package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.BusinessRepo;
import giga.repo.DesignRepo;
import giga.repo.PageRepo;
import giga.repo.UserRepo;
import jakarta.servlet.http.HttpRequest;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Service;
import qio.model.web.Cache;

import java.util.List;

@Service
public class PageService {

    @Inject
    UserRepo userRepo;

    @Inject
    PageRepo pageRepo;

    @Inject
    DesignRepo designRepo;

    @Inject
    BusinessRepo businessRepo;

    @Inject
    BusinessService businessService;

    @Inject
    AuthService authService;

    @Inject
    SiteService siteService;

    public String getPage(String businessUri, String page, Cache cache, HttpRequest req) {
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


    public String create(Long businessId, Cache cache){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);
        List<Design> designs = designRepo.getList(businessId);
        cache.set("designs", designs);
        cache.set("page", "/pages/page/new.jsp");
        return "/designs/auth.jsp";
    }


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


    public String list(Long businessId, Cache cache) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

        List<Page> pages = pageRepo.getList(businessId);
        cache.set("pages", pages);
        cache.set("page", "/pages/page/list.jsp");
        return "/designs/auth.jsp";
    }

    public String delete(Long id, Long businessId, Cache cache) {
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
