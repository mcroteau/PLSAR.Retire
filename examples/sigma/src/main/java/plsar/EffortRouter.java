package plsar;

import net.plsar.annotations.Component;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Inject;
import net.plsar.annotations.http.Get;
import net.plsar.model.Cache;
import net.plsar.model.HttpRequest;
import net.plsar.security.SecurityManager;
import plsar.model.User;

//todo:5:40pm : Thu 13 Oct
@HttpRouter
public class EffortRouter {

    @Inject
    UserRepo userRepo;

    @Inject
    EffortRepo effortRepo;

    @Get("/efforts/{id}")
    public String efforts(Cache cache, @Component Long id, HttpRequest req, SecurityManager security){
        if(!security.isAuthenticated(req)){
            cache.set("message", "please sign in.");
            return "[redirect:]/";
        }

        String credential = security.getUser(req);
        User user = userRepo.getPhone(credential);

        List<Effort> efforts = effortRepo.getList(id);



    }
}
