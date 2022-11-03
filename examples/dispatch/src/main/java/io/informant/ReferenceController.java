package io.informant;

import io.informant.model.Reference;
import io.informant.model.Permission;
import io.informant.model.Request;
import io.informant.model.User;
import io.informant.repo.ReferenceRepo;
import io.informant.repo.UserRepo;
import net.plsar.annotations.Bind;
import net.plsar.annotations.Component;
import net.plsar.annotations.Controller;
import net.plsar.annotations.http.Get;
import net.plsar.model.NetworkRequest;
import net.plsar.model.PageCache;
import net.plsar.security.SecurityManager;

@Controller
public class ReferenceController {

    @Bind
    UserRepo userRepo;

    @Bind
    ReferenceRepo referenceRepo;

    @Get("/users/request/{id}")
    public String request(PageCache cache, NetworkRequest req, SecurityManager securityManager, @Component Long id){
        if(!securityManager.isAuthenticated(req)){
            cache.set("message", "authentication required.");
            return "redirect:/";
        }

        String credential = securityManager.getUser(req);
        User authUser = userRepo.getPhone(credential);

        Request request = new Request(authUser.getId(), id);

        Request existingRequest = referenceRepo.get(authUser.getId(), id);
        if(existingRequest == null){
            referenceRepo.save(request);
        }

        Request storedRequest = referenceRepo.get(authUser.getId(), id);
        String permission = "requests:maintenance:" + storedRequest.getId();
        userRepo.savePermission(authUser.getId(), permission);
        userRepo.savePermission(id, permission);


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

        Request storedRequest = referenceRepo.get(authUser.getId(), id);
        String permission = "requests:maintenance:" + storedRequest.getId();

        if(!securityManager.hasPermission(permission, req)){
            cache.set("message", "permission required.");
            return "redirect:/users/identity/" + id;
        }

        Request request = new Request(authUser.getId(), id);

        if(storedRequest != null){
            referenceRepo.cancel(request);
            Permission userPermission = userRepo.getPermission(authUser.getId(), permission);
            userRepo.deletePermission(userPermission.getId());
        }

        cache.set("message", "cancelled." );
        return "redirect:/users/identity/" + id;
    }

    @Get("/users/requests/approve/{id}")
    public String approve(PageCache cache, NetworkRequest req, SecurityManager securityManager, @Component Long id){
        if(!securityManager.isAuthenticated(req)){
            cache.set("message", "authentication required.");
            return "redirect:/";
        }

        String credential = securityManager.getUser(req);
        User authUser = userRepo.getPhone(credential);

        Request storedRequest = referenceRepo.get(authUser.getId(), id);
        String permission = "requests:maintenance:" + storedRequest.getId();

        if(!securityManager.hasPermission(permission, req)){
            cache.set("message", "permission required.");
            return "redirect:/users/identity/" + id;
        }

        referenceRepo.approve(storedRequest);
        Reference reference = new Reference(authUser.getId(), id);
        referenceRepo.allow(reference);

        cache.set("message", "approved.");
        return "redirect:/users/identity/" + id;
    }

    @Get("/users/request/remove/{id}")
    public String remove(PageCache cache, NetworkRequest req, SecurityManager securityManager, @Component Long id){
        if(!securityManager.isAuthenticated(req)){
            cache.set("message", "authentication required.");
            return "redirect:/";
        }

        String credential = securityManager.getUser(req);
        User authUser = userRepo.getPhone(credential);

        Request storedRequest = referenceRepo.get(authUser.getId(), id);
        String permission = "requests:maintenance:" + storedRequest.getId();

        if(!securityManager.hasPermission(permission, req)){
            cache.set("message", "permission required.");
            return "redirect:/users/identity/" + id;
        }

        referenceRepo.cancel(storedRequest);
        Reference reference = new Reference(authUser.getId(), id);
        referenceRepo.remove(reference);

        cache.set("message", "access removed.");
        return "redirect:/users/identity/" + id;
    }
}
