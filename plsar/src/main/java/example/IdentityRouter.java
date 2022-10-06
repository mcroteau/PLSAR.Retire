package example;

import oceanblue.Persistence;
import oceanblue.model.HttpResponse;
import oceanblue.annotations.Router;
import oceanblue.annotations.http.Get;
import oceanblue.annotations.http.Post;
import oceanblue.model.HttpRequest;
import oceanblue.implement.PersistenceRouter;
import oceanblue.model.Cache;
import oceanblue.security.PlsarSecurityManager;

@Router
public class IdentityRouter implements PersistenceRouter {

    Persistence persistence;

    @Get("/")
    public String signin(Cache cache) {
        cache.set("instructions", "effort.");
        return "/signin.html";
    }

    @Post("/authenticate")
    public String authenticate(Cache cache, HttpRequest httpRequest, HttpResponse httpResponse, PlsarSecurityManager security) {
        String user = httpRequest.value("user");
        String pass = httpRequest.value("pass");

        if(security.signin(user, pass, httpRequest, httpResponse)){
            httpRequest.getSession(true).set("user", user);
            return "[redirect]/secret";
        }
        cache.set("message", "authentication failed.");
        return "[redirect]/";
    }

    @Get("/secret")
    public String secret(Cache cache, HttpRequest httpRequest, PlsarSecurityManager security) {
        if(security.userIsAuthenticated(httpRequest)){
            return "/secret.html";
        }
        cache.set("message", "authenticate please.");
        return "[redirect]/";
    }

    @Get("/signout")
    public String signout(HttpRequest httpRequest, HttpResponse httpResponse, PlsarSecurityManager security) {
        security.signout(httpRequest, httpResponse);
        return "[redirect]/";
    }

    @Override
    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }
}