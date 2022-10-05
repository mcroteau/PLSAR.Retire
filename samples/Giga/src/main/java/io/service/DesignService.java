package io.service;

import io.Giga;
import io.model.*;
import io.repo.AssetRepo;
import io.repo.DesignRepo;
import io.repo.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Service;
import qio.model.web.ResponseData;

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

    public String configure(Long id, ResponseData data){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(id, data);

        List<Asset> assets = assetRepo.getList(id);
        data.set("assets", assets);

        data.set("page", "/pages/design/new.jsp");
        return "/designs/auth.jsp";
    }

    public String list(Long id, ResponseData data) throws Exception {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(id, data);

        List<Design> designs = designRepo.getList(id);
        data.set("designs", designs);
        data.set("page", "/pages/design/list.jsp");
        return "/designs/auth.jsp";
    }

    public String save(HttpServletRequest req) {
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


    public String edit(Long id, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.DESIGN_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to edit this design.");
            return "[redirect]/";
        }
        Design design = designRepo.get(id);
        data.set("design", design);

        List<Asset> assets = assetRepo.getList(id);
        data.set("assets", assets);

        businessService.setData(design.getBusinessId(), data);
        data.set("page", "/pages/design/edit.jsp");
        return "/designs/auth.jsp";
    }


    public String update(Long id, ResponseData data, HttpServletRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.DESIGN_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to edit this design.");
            return "[redirect]/";
        }

        Design design = (Design) Qio.get(req, Design.class);
        designRepo.update(design);

        return "[redirect]/designs/edit/" + id;
    }

    public String delete(Long id, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.DESIGN_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to delete this design.");
            return "[redirect]/";
        }

        Design design = designRepo.get(id);
        if(design.getBaseDesign()){
            data.set("message", "The base design is important. It is used everywhere.");
            return "[redirect]/designs/" + design.getBusinessId();
        }

        designRepo.delete(id);
        data.set("message", "Successfully deleted design.");

        return "[redirect]/designs/" + design.getBusinessId();
    }

}
