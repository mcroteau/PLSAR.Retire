package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.AssetRepo;
import giga.repo.BusinessRepo;
import giga.repo.DesignRepo;
import giga.repo.UserRepo;
import dev.blueocean.annotations.Bind;
import dev.blueocean.annotations.Service;
import dev.blueocean.model.Cache;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.security.SecurityManager;

import java.util.List;

@Service
public class DesignService {

    @Bind
    AssetRepo assetRepo;

    @Bind
    UserRepo userRepo;

    @Bind
    DesignRepo designRepo;

    @Bind
    BusinessRepo businessRepo;


    BusinessService businessService;

    public DesignService(){
        this.businessService = new BusinessService();
    }

    public String configure(Long id, Cache cache, NetworkRequest req, SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        businessService.setData(id, cache, userRepo, businessRepo, req, security);

        List<Asset> assets = assetRepo.getList(id);
        cache.set("assets", assets);

        return "/pages/design/new.jsp";
    }

    public String list(Long id, Cache cache, NetworkRequest req, SecurityManager security) throws Exception {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        businessService.setData(id, cache, userRepo, businessRepo, req, security);

        List<Design> designs = designRepo.getList(id);
        cache.set("designs", designs);
        return "/pages/design/list.jsp";
    }

    public String save(NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.getPhone(credential);
        if(authUser == null){
            authUser = userRepo.getEmail(credential);
        }

        Design design = (Design) req.inflect(Design.class);
        designRepo.save(design);

        Design savedDesign = designRepo.getSaved();
        String permission = Giga.DESIGN_MAINTENANCE + savedDesign.getId();
        userRepo.savePermission(authUser.getId(), permission);

        return "redirect:/designs/" + savedDesign.getBusinessId();
    }


    public String edit(Long id, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.DESIGN_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this design.");
            return "redirect:/";
        }
        Design design = designRepo.get(id);
        cache.set("design", design);

        List<Asset> assets = assetRepo.getList(id);
        cache.set("assets", assets);

        businessService.setData(design.getBusinessId(), cache, userRepo, businessRepo, req, security);
        return "/pages/design/edit.jsp";
    }


    public String update(Long id, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.DESIGN_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this design.");
            return "redirect:/";
        }

        Design design = (Design) req.inflect(Design.class);
        designRepo.update(design);

        return "redirect:/designs/edit/" + id;
    }

    public String delete(Long id, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.DESIGN_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this design.");
            return "redirect:/";
        }

        Design design = designRepo.get(id);
        if(design.getBaseDesign()){
            cache.set("message", "The base design is important. It is used everywhere.");
            return "redirect:/designs/" + design.getBusinessId();
        }

        designRepo.delete(id);
        cache.set("message", "Successfully deleted design.");

        return "redirect:/designs/" + design.getBusinessId();
    }

}
