package example;

import oceanblue.model.HttpResponse;
import example.assist.AuthAccess;
import oceanblue.annotations.Router;
import oceanblue.annotations.http.Get;
import oceanblue.annotations.http.Post;
import oceanblue.model.HttpRequest;
import oceanblue.Persistence;
import oceanblue.implement.PersistenceRouter;
import oceanblue.model.Cache;
import oceanblue.security.DatabaseAccess;
import oceanblue.security.EarthlingSecurityManager;

@Router
public class IdentityRouter implements PersistenceRouter {

    EarthlingSecurityManager security;

    @Get("/")
    public String signin(Cache cache) {
        cache.set("instructions", "effort.");
        return "/signin.html";
    }

    @Post("/authenticate")
    public String authenticate(Cache cache, HttpRequest httpRequest, HttpResponse httpResponse) {
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
    public String secret(Cache cache, HttpRequest httpRequest) {
        if(security.userIsAuthenticated(httpRequest)){
            return "/secret.html";
        }
        cache.set("message", "authenticate please.");
        return "[redirect]/";
    }

    @Get("/signout")
    public String signout(HttpRequest httpRequest, HttpResponse httpResponse) {
        security.signout(httpRequest, httpResponse);
        return "[redirect]/";
    }

    @Override
    public void setPersistence(Persistence persistence) {
        DatabaseAccess authAccess = new AuthAccess(new UserRepo(persistence));
        this.security = new EarthlingSecurityManager(authAccess);
    }
}