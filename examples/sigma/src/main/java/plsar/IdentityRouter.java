package plsar;

import dev.blueocean.annotations.Bind;
import dev.blueocean.model.NetworkResponse;
import dev.blueocean.annotations.Controller;
import dev.blueocean.annotations.http.Get;
import dev.blueocean.annotations.http.Post;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.model.Cache;
import dev.blueocean.security.SecurityManager;

@Controller
public class IdentityRouter {

    @Bind
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