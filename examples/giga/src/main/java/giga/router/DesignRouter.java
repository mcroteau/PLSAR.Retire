package giga.router;

import giga.Giga;
import giga.model.Asset;
import giga.model.Design;
import giga.model.User;
import giga.service.DesignService;
import jakarta.servlet.http.HttpRequest;
import qio.annotate.HttpRouter;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

import java.util.List;

@HttpRouter
public class DesignRouter {

    @Inject
    DesignService designService;
    
    @Get("/designs/new/{{id}}")
    public String configure(Cache cache,
                            @Component Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(id, cache);

        List<Asset> assets = assetRepo.getList(id);
        cache.set("assets", assets);

        cache.set("page", "/pages/design/new.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/designs/{{id}}")
    public String list(Cache cache,
                       @Component Long id) throws Exception{
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(id, cache);

        List<Design> designs = designRepo.getList(id);
        cache.set("designs", designs);
        cache.set("page", "/pages/design/list.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/designs/save")
    public String save(HttpRequest req){
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

    @Get("/designs/edit/{{id}}")
    public String showcase(Cache cache,
                           @Component Long id) throws Exception {
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

    @Post("/designs/update/{{id}}")
    public String update(HttpRequest req,
                         Cache cache,
                         @Component Long id){
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

    @Post("/designs/delete/{{id}}")
    public String delete(Cache cache,
                         @Component Long id){
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
