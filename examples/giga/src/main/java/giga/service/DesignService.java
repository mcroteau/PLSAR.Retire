package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.AssetRepo;
import giga.repo.DesignRepo;
import giga.repo.UserRepo;
import jakarta.servlet.http.HttpRequest;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Service;
import qio.model.web.Cache;

import java.util.List;

@Service
public class DesignService {

    @Inject
    AssetRepo assetRepo;

    @Inject
    UserRepo userRepo;

    @Inject
    DesignRepo designRepo;

    @Inject
    AuthService authService;

    @Inject
    BusinessService businessService;

    public String configure(Long id, Cache cache){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(id, cache);

        List<Asset> assets = assetRepo.getList(id);
        cache.set("assets", assets);

        cache.set("page", "/pages/design/new.jsp");
        return "/designs/auth.jsp";
    }

    public String list(Long id, Cache cache) throws Exception {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(id, cache);

        List<Design> designs = designRepo.getList(id);
        cache.set("designs", designs);
        cache.set("page", "/pages/design/list.jsp");
        return "/designs/auth.jsp";
    }

    public String save(HttpRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        User authdUser = authService.getUser();

        Design design = (Design) Qio.get(req, Design.class);
        designRepo.save(design);

        Design savedDesign = designRepo.getSaved();
        String permission = Giga.DESIGN_MAINTENANCE + savedDesign.getId();
        userRepo.savePermission(authdUser.getId(), permission);

        return "[redirect]/designs/" + savedDesign.getBusinessId();
    }


    public String edit(Long id, Cache cache) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.DESIGN_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to edit this design.");
            return "[redirect]/";
        }
        Design design = designRepo.get(id);
        cache.set("design", design);

        List<Asset> assets = assetRepo.getList(id);
        cache.set("assets", assets);

        businessService.setData(design.getBusinessId(), cache);
        cache.set("page", "/pages/design/edit.jsp");
        return "/designs/auth.jsp";
    }


    public String update(Long id, Cache cache, HttpRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.DESIGN_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to edit this design.");
            return "[redirect]/";
        }

        Design design = (Design) Qio.get(req, Design.class);
        designRepo.update(design);

        return "[redirect]/designs/edit/" + id;
    }

    public String delete(Long id, Cache cache) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.DESIGN_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to delete this design.");
            return "[redirect]/";
        }

        Design design = designRepo.get(id);
        if(design.getBaseDesign()){
            cache.set("message", "The base design is important. It is used everywhere.");
            return "[redirect]/designs/" + design.getBusinessId();
        }

        designRepo.delete(id);
        cache.set("message", "Successfully deleted design.");

        return "[redirect]/designs/" + design.getBusinessId();
    }

}
