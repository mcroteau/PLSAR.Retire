package giga.router;

import giga.Giga;
import giga.model.Asset;
import giga.model.Design;
import giga.model.User;
import giga.repo.AssetRepo;
import giga.repo.DesignRepo;
import giga.repo.UserRepo;
import giga.service.BusinessService;
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
public class DesignRouter {

    @Inject
    AssetRepo assetRepo;

    @Inject
    UserRepo userRepo;

    @Inject
    DesignRepo designRepo;

    BusinessService businessService;

    public DesignRouter(){
        this.businessService = new BusinessService();
    }
    
    @Get("/designs/new/{id}")
    public String configure(Cache cache,
                            HttpRequest req,
                            SecurityManager security,
                            @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }
        businessService.setData(id, cache);

        List<Asset> assets = assetRepo.getList(id);
        cache.set("assets", assets);

        cache.set("page", "/pages/design/new.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/designs/{id}")
    public String list(Cache cache,
                       HttpRequest req,
                       SecurityManager security,
                       @Component Long id) throws Exception{
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }
        businessService.setData(id, cache);

        List<Design> designs = designRepo.getList(id);
        cache.set("designs", designs);
        cache.set("page", "/pages/design/list.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/designs/save")
    public String save(HttpRequest req,
                       SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        Design design = (Design) req.inflect(Design.class);
        designRepo.save(design);

        Design savedDesign = designRepo.getSaved();
        String permission = Giga.DESIGN_MAINTENANCE + savedDesign.getId();
        userRepo.savePermission(authUser.getId(), permission);

        return "[redirect]/designs/" + savedDesign.getBusinessId();
    }

    @Get("/designs/edit/{id}")
    public String showcase(Cache cache,
                           HttpRequest req,
                           SecurityManager security,
                           @Component Long id) throws Exception {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.DESIGN_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
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

    @Post("/designs/update/{id}")
    public String update(Cache cache,
                         HttpRequest req,
                         SecurityManager security,
                         @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.DESIGN_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this design.");
            return "[redirect]/";
        }

        Design design = (Design) req.inflect(Design.class);
        designRepo.update(design);

        return "[redirect]/designs/edit/" + id;
    }

    @Post("/designs/delete/{id}")
    public String delete(Cache cache,
                         HttpRequest req,
                         SecurityManager security,
                         @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.DESIGN_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
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
