package io.informant;

import net.plsar.annotations.Bind;
import net.plsar.annotations.Component;
import net.plsar.annotations.Controller;
import net.plsar.annotations.Design;
import net.plsar.annotations.http.Get;
import net.plsar.model.PageCache;
import net.plsar.model.NetworkRequest;
import net.plsar.security.SecurityManager;
import io.informant.model.User;
import io.informant.repo.UserRepo;


@Controller
public class UserController {

    @Bind
    UserRepo userRepo;

    @Design("/designs/guest.jsp")
    @Get("/users/{guid}")
    public String getCode(PageCache pageCache, @Component String guid){
        User user = userRepo.getUserCode(guid);
        if(user == null){
            pageCache.set("message", "user cannot be found with id: " + guid);
            return "redirect:/";
        }
        return "/pages/user/code.jsp";
    }

    @Design("/designs/base.jsp")
    @Get("/users/{id}")
    public String getUser(PageCache pageCache, NetworkRequest req, SecurityManager securityManager, @Component Long id){
        if(!securityManager.isAuthenticated(req)){
            pageCache.set("message", "authentication required.");
            return "redirect:/";
        }

        User user = userRepo.get(id);
        if(user == null){
            pageCache.set("message", "user cannot be found with id: " + id);
            return "redirect:/";
        }
        return "/pages/user/id.jsp";
    }

}
