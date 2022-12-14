package io.informant;

import io.informant.model.Permission;
import io.informant.model.User;
import io.informant.model.UserFollow;
import io.informant.repo.FollowsRepo;
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

    @Bind
    FollowsRepo followsRepo;

    @Get("/users/follow/{id}")
    public String follow(PageCache cache, NetworkRequest req, SecurityManager securityManager, @Component Long id){
        if(!securityManager.isAuthenticated(req)){
            cache.set("message", "authentication required.");
            return "redirect:/";
        }

        String credential = securityManager.getUser(req);
        User authUser = userRepo.getPhone(credential);

        UserFollow userFollow = new UserFollow(authUser.getId(), id);

        UserFollow existingFollow = followsRepo.get(authUser.getId(), id);
        if(existingFollow == null){
            followsRepo.follow(userFollow);
        }

        UserFollow storedFollow = followsRepo.get(authUser.getId(), id);
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

        UserFollow storedFollow = followsRepo.get(authUser.getId(), id);
        String permission = "follows:maintenance:" + storedFollow.getId();

        if(!securityManager.hasPermission(permission, req)){
            cache.set("message", "permission required.");
            return "redirect:/users/identity/" + id;
        }

        UserFollow userFollow = new UserFollow(authUser.getId(), id);

        if(storedFollow != null){
            followsRepo.unfollow(userFollow);
            Permission userPermission = userRepo.getPermission(authUser.getId(), permission);
            userRepo.deletePermission(userPermission.getId());
        }

        cache.set("message", "unfollowing." );
        return "redirect:/users/identity/" + id;
    }
}
