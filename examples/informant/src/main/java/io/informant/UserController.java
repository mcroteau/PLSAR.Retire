package io.informant;

import dev.blueocean.annotations.Bind;
import dev.blueocean.annotations.Component;
import dev.blueocean.annotations.Controller;
import dev.blueocean.annotations.Design;
import dev.blueocean.annotations.http.Get;
import dev.blueocean.model.Cache;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.security.SecurityManager;
import io.informant.model.User;
import io.informant.model.response.UserResponse;
import io.informant.repo.UserRepo;

@Controller
public class UserController {

    @Bind
    UserRepo userRepo;

    @Design("/designs/guest.jsp")
    @Get("/users/{guid}")
    public String getCode(Cache cache, @Component String guid){
        User user = userRepo.getUserCode(guid);
        if(user == null){
            cache.set("message", "user cannot be found with id: " + guid);
            return "redirect:/";
        }
        return "/pages/user/code.jsp";
    }

    @Design("/designs/base.jsp")
    @Get("/users/{id}")
    public String getUser(Cache cache, NetworkRequest req, SecurityManager securityManager, @Component Long id){
        if(!securityManager.isAuthenticated(req)){
            cache.set("message", "authentication required.");
            return "redirect:/";
        }

        User user = userRepo.get(id);
        if(user == null){
            cache.set("message", "user cannot be found with id: " + id);
            return "redirect:/";
        }
        return "/pages/user/id.jsp";
    }

}
