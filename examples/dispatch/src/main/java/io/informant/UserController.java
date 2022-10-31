package io.informant;

import io.informant.model.*;
import io.informant.repo.FollowsRepo;
import io.informant.repo.RequestRepo;
import net.plsar.annotations.Bind;
import net.plsar.annotations.Component;
import net.plsar.annotations.Controller;
import net.plsar.annotations.Design;
import net.plsar.annotations.http.Get;
import net.plsar.model.PageCache;
import net.plsar.model.NetworkRequest;
import net.plsar.security.SecurityManager;
import io.informant.repo.UserRepo;

import java.text.ParseException;
import java.util.List;


@Controller
public class UserController {

    Informant informant;

    public UserController(){
        this.informant = new Informant();
    }

    @Bind
    UserRepo userRepo;

    @Bind
    FollowsRepo followsRepo;

    @Bind
    RequestRepo requestRepo;

    @Bind
    ControllerHelper controllerHelper;


    @Design("/designs/guest.jsp")
    @Get("/users/guid/{guid}")
    public String getCode(PageCache pageCache, @Component String guid){
        User user = userRepo.getUserCode(guid);
        if(user == null){
            pageCache.set("message", "user cannot be found with id: " + guid);
            return "redirect:/";
        }
        return "/pages/user/code.jsp";
    }

    @Design("/designs/sheets.jsp")
    @Get("/users/identity/{id}")
    public String getUser(PageCache cache, NetworkRequest req, SecurityManager securityManager, @Component Long id) throws ParseException {
        if(!securityManager.isAuthenticated(req)){
            cache.set("message", "authentication required.");
            return "redirect:/";
        }

        User authUser = controllerHelper.getUser(req, securityManager);

        User user = userRepo.get(id);
        if(user == null){
            cache.set("message", "user cannot be found with id: " + id);
            return "redirect:/";
        }

        Long startTime = informant.getDate(7);
        Long endTime = informant.getDate(0);
        List<Paper> papers = controllerHelper.getPapers(startTime, endTime, 0, authUser);

        UserFollow storedFollow = followsRepo.get(authUser.getId(), id);

        cache.set("following", false);

        if(storedFollow != null){
            cache.set("following", true);
        }

        Request storedRequest = requestRepo.get(authUser.getId(), id);

        cache.set("requested", false);

        if(storedRequest != null){
            cache.set("requested", true);
        }


        cache.set("user", user);
        cache.set("papers", papers);

        return "/pages/user/id.jsp";
    }


}
