package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import dev.blueocean.annotations.Bind;
import dev.blueocean.annotations.Service;
import dev.blueocean.model.Cache;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.security.SecurityManager;

import java.util.List;

@Service
public class PageService {

    @Bind
    UserRepo userRepo;

    @Bind
    PageRepo pageRepo;

    @Bind
    DesignRepo designRepo;

    @Bind
    CategoryRepo categoryRepo;

    @Bind
    BusinessRepo businessRepo;

    BusinessService businessService;

    public PageService(){
        this.businessService = new BusinessService();
    }

    public String getPage(String businessUri, String page, Cache cache, NetworkRequest req, SecurityManager security) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "redirect:/";
        }

        Page activePage = pageRepo.get(business.getId(), page);
        if(activePage == null){
            return "redirect:/" + businessUri + "";
        }

        SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);

        cache.set("request", req);
        cache.set("siteService", siteService);
        cache.set("business", business);
        cache.set("page", activePage);
        return "/pages/page/index.jsp";
    }


    public String create(Long businessId, Cache cache, NetworkRequest req, SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);
        List<Design> designs = designRepo.getList(businessId);
        cache.set("designs", designs);
        return "/pages/page/new.jsp";
    }


    public String save(NetworkRequest req, SecurityManager security) throws Exception {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.getPhone(credential);
        if(authUser == null){
            authUser = userRepo.getEmail(credential);
        }

        Page page = (Page) req.inflect(Page.class);
        pageRepo.save(page);
        Page savedAsset = pageRepo.getSaved();
        String permission = Giga.PAGE_MAINTENANCE + savedAsset.getId();
        userRepo.savePermission(authUser.getId(), permission);

        return "redirect:/pages/" + savedAsset.getBusinessId();
    }


    public String list(Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        List<Page> pages = pageRepo.getList(businessId);
        cache.set("pages", pages);
        return "/pages/page/list.jsp";
    }

    public String delete(Long id, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.PAGE_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Whoa, people might be using this. Lol, this isn't yours.");
            return "redirect:/";
        }

        pageRepo.delete(id);
        cache.set("message", "Successfully deleted asset.");

        return "redirect:/pages/" + businessId;
    }
}
