package io.informant;

import io.informant.model.Permission;
import io.informant.model.Request;
import io.informant.model.User;
import io.informant.repo.RequestRepo;
import io.informant.repo.UserRepo;
import net.plsar.annotations.Bind;
import net.plsar.annotations.Component;
import net.plsar.annotations.Controller;
import net.plsar.annotations.http.Get;
import net.plsar.model.NetworkRequest;
import net.plsar.model.PageCache;
import net.plsar.security.SecurityManager;

@Controller
public class RequestController {

    @Bind
    UserRepo userRepo;

    @Bind
    RequestRepo requestRepo;

    @Get("/users/request/{id}")
    public String request(PageCache cache, NetworkRequest req, SecurityManager securityManager, @Component Long id){
        if(!securityManager.isAuthenticated(req)){
            cache.set("message", "authentication required.");
            return "redirect:/";
        }

        String credential = securityManager.getUser(req);
        User authUser = userRepo.getPhone(credential);

        Request request = new Request(authUser.getId(), id);

        Request existingRequest = requestRepo.get(authUser.getId(), id);
        if(existingRequest == null){
            requestRepo.save(request);
        }

        Request storedRequest = requestRepo.get(authUser.getId(), id);
        String permission = "requests:maintenance:" + storedRequest.getId();
        userRepo.savePermission(authUser.getId(), permission);

        cache.set("message", "requested.");
        return "redirect:/users/identity/" + id;
    }


    @Get("/users/request/cancel/{id}")
    public String cancel(PageCache cache, NetworkRequest req, SecurityManager securityManager, @Component Long id){
        if(!securityManager.isAuthenticated(req)){
            cache.set("message", "authentication required.");
            return "redirect:/";
        }

        String credential = securityManager.getUser(req);
        User authUser = userRepo.getPhone(credential);

        Request storedRequest = requestRepo.get(authUser.getId(), id);
        String permission = "requests:maintenance:" + storedRequest.getId();

        if(!securityManager.hasPermission(permission, req)){
            cache.set("message", "permission required.");
            return "redirect:/users/identity/" + id;
        }

        Request request = new Request(authUser.getId(), id);

        if(storedRequest != null){
            requestRepo.cancel(request);
            Permission userPermission = userRepo.getPermission(authUser.getId(), permission);
            userRepo.deletePermission(userPermission.getId());
        }

        cache.set("message", "cancelled." );
        return "redirect:/users/identity/" + id;
    }

}
