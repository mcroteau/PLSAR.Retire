package plsar;

import net.plsar.Persistence;
import net.plsar.model.HttpResponse;
import net.plsar.annotations.Router;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.HttpRequest;
import net.plsar.implement.PersistenceRouter;
import net.plsar.model.Cache;
import net.plsar.security.SecurityManager;

@Router
public class IdentityRouter implements PersistenceRouter {

    Persistence persistence;

    @Get("/")
    public String signin(Cache cache) {
        cache.set("instructions", "effort.");
        return "/signin.html";
    }

    @Post("/authenticate")
    public String authenticate(Cache cache, HttpRequest httpRequest, HttpResponse httpResponse, SecurityManager security) {
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
    public String secret(Cache cache, HttpRequest httpRequest, SecurityManager security) {
        if(security.userIsAuthenticated(httpRequest)){
            return "/secret.html";
        }
        cache.set("message", "authenticate please.");
        return "[redirect]/";
    }

    @Get("/signout")
    public String signout(HttpRequest httpRequest, HttpResponse httpResponse, SecurityManager security) {
        security.signout(httpRequest, httpResponse);
        return "[redirect]/";
    }

    @Override
    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }
}