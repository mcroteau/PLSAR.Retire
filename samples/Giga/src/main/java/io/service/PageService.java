package io.service;

import io.Giga;
import io.model.*;
import io.repo.BusinessRepo;
import io.repo.DesignRepo;
import io.repo.PageRepo;
import io.repo.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Service;
import qio.model.web.ResponseData;

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

    public String getPage(String businessUri, String page, ResponseData data, HttpServletRequest req) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]";
        }

        Page activePage = pageRepo.get(business.getId(), page);
        if(activePage == null){
            return "[redirect]/" + businessUri + "";
        }

        data.set("request", req);
        data.set("siteService", siteService);
        data.set("business", business);
        data.set("page", activePage);
        return "/pages/page/index.jsp";
    }


    public String create(Long businessId, ResponseData data){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, data);
        List<Design> designs = designRepo.getList(businessId);
        data.set("designs", designs);
        data.set("page", "/pages/page/new.jsp");
        return "/designs/auth.jsp";
    }


    public String save(HttpServletRequest req) throws Exception {
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


    public String list(Long businessId, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, data);

        List<Page> pages = pageRepo.getList(businessId);
        data.set("pages", pages);
        data.set("page", "/pages/page/list.jsp");
        return "/designs/auth.jsp";
    }

    public String delete(Long id, Long businessId, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.PAGE_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Whoa, people might be using this. Lol, this isn't yours.");
            return "[redirect]/";
        }

        pageRepo.delete(id);
        data.set("message", "Successfully deleted asset.");

        return "[redirect]/pages/" + businessId;
    }
}
