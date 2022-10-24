package plsar;

import dev.blueocean.annotations.Component;
import dev.blueocean.annotations.HttpRouter;
import dev.blueocean.annotations.Inject;
import dev.blueocean.annotations.http.Get;
import dev.blueocean.model.Cache;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.security.SecurityManager;
import plsar.model.User;

//todo:5:40pm : Thu 13 Oct
@HttpRouter
public class EffortRouter {

    @Inject
    UserRepo userRepo;

    @Inject
    EffortRepo effortRepo;

    @Get("/efforts/{id}")
    public String efforts(Cache cache, @Component Long id, NetworkRequest req, SecurityManager security){
        if(!security.isAuthenticated(req)){
            cache.set("message", "please sign in.");
            return "[redirect:]/";
        }

        String credential = security.getUser(req);
        User user = userRepo.getPhone(credential);

        List<Effort> efforts = effortRepo.getList(id);



    }
}
