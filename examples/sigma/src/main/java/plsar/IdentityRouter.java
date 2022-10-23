package plsar;

import net.plsar.annotations.Inject;
import net.plsar.model.NetworkResponse;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.NetworkRequest;
import net.plsar.model.Cache;
import net.plsar.security.SecurityManager;

@HttpRouter
public class IdentityRouter {

    @Inject
    UserRepo userRepo;

    @Get("/")
    public String signin(Cache cache, NetworkRequest networkRequest) {
        cache.set("instructions", "effort.");
        return "/index.htm";
    }

    @Post("/authenticate")
    public String authenticate(Cache cache, NetworkRequest networkRequest, NetworkResponse networkResponse, SecurityManager securityManager) {
        String user = networkRequest.getValue("user");
        String pass = networkRequest.getValue("pass");

        if(securityManager.signin(user, pass, networkRequest, networkResponse)){
            networkRequest.getSession(true).set("user", user);
            return "[redirect]/secret";
        }
        cache.set("message", "authentication failed.");
        return "[redirect]/";
    }

    @Get("/secret")
    public String secret(Cache cache, NetworkRequest networkRequest, SecurityManager securityManager) {
        if(securityManager.isAuthenticated(networkRequest)){
            return "/secret.html";
        }
        cache.set("message", "authenticate please.");
        return "[redirect]/";
    }

    @Get("/signout")
    public String signout(NetworkRequest networkRequest, NetworkResponse networkResponse, SecurityManager securityManager) {
        securityManager.signout(networkRequest, networkResponse);
        return "[redirect]/";
    }

}