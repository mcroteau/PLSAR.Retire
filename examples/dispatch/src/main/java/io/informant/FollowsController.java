package io.informant;

import io.informant.model.Permission;
import io.informant.model.User;
import io.informant.model.UserFollow;
import io.informant.repo.UserRepo;
import net.plsar.annotations.Bind;
import net.plsar.annotations.Component;
import net.plsar.annotations.Controller;
import net.plsar.annotations.http.Get;
import net.plsar.model.NetworkRequest;
import net.plsar.model.PageCache;
import net.plsar.security.SecurityManager;

@Controller
public class FollowsController {

    @Bind
    UserRepo userRepo;

    @Get("/users/follow/{id}")
    public String follow(PageCache cache, NetworkRequest req, SecurityManager securityManager, @Component Long id){
        if(!securityManager.isAuthenticated(req)){
            cache.set("message", "authentication required.");
            return "redirect:/";
        }

        String credential = securityManager.getUser(req);
        User authUser = userRepo.getPhone(credential);

        UserFollow userFollow = new UserFollow(authUser.getId(), id);

        UserFollow existingFollow = userRepo.getFollow(authUser.getId(), id);
        if(existingFollow == null){
            userRepo.follow(userFollow);
        }

        UserFollow storedFollow = userRepo.getFollow(authUser.getId(), id);
        String permission = "follows:maintenance:" + storedFollow.getId();
        userRepo.savePermission(authUser.getId(), permission);

        cache.set("message", "success following.");
        return "redirect:/users/identity/" + id;
    }

    @Get("/users/unfollow/{id}")
    public String unfollow(PageCache cache, NetworkRequest req, SecurityManager securityManager, @Component Long id){
        if(!securityManager.isAuthenticated(req)){
            cache.set("message", "authentication required.");
            return "redirect:/";
        }

        String credential = securityManager.getUser(req);
        User authUser = userRepo.getPhone(credential);

        UserFollow userFollow = new UserFollow(authUser.getId(), id);

        UserFollow storedFollow = userRepo.getFollow(authUser.getId(), id);
        if(storedFollow != null){

            userRepo.unfollow(userFollow);

            String permission = "follows:maintenance:" + storedFollow.getId();
            Permission userPermission = userRepo.getPermission(authUser.getId(), permission);
            userRepo.removePermission(userPermission.getId());
        }

        cache.set("message", "unfollowing." );
        return "redirect:/users/identity/" + id;
    }
}
