package plsar;

import net.plsar.annotations.Inject;
import net.plsar.model.HttpResponse;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.HttpRequest;
import net.plsar.model.Cache;
import net.plsar.security.SecurityManager;
import plsar.model.User;

@HttpRouter
public class IdentityRouter {

    @Inject
    UserRepo userRepo;

    @Get("/")
    public String signin(Cache cache, HttpRequest httpRequest, SecurityManager securityManager) {
        cache.set("instructions", "effort.");
        cache.set("cache", cache);
        cache.set("resp", cache);
        cache.set("request", httpRequest);
        cache.set("securityManager", securityManager);
        cache.set("fooService", new FooService());
        return "/index.htm";
    }

    @Post("/authenticate")
    public String authenticate(Cache cache, HttpRequest httpRequest, HttpResponse httpResponse, SecurityManager securityManager) {
        String user = httpRequest.value("user");
        String pass = httpRequest.value("pass");

        if(securityManager.signin(user, pass, httpRequest, httpResponse)){
            httpRequest.getSession(true).set("user", user);
            return "[redirect]/secret";
        }
        cache.set("message", "authentication failed.");
        return "[redirect]/";
    }

    @Get("/secret")
    public String secret(Cache cache, HttpRequest httpRequest, SecurityManager securityManager) {
        if(securityManager.userIsAuthenticated(httpRequest)){
            return "/secret.html";
        }
        cache.set("message", "authenticate please.");
        return "[redirect]/";
    }

    @Get("/signout")
    public String signout(HttpRequest httpRequest, HttpResponse httpResponse, SecurityManager securityManager) {
        securityManager.signout(httpRequest, httpResponse);
        return "[redirect]/";
    }

}