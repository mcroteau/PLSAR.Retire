package io.informant;

import dev.blueocean.annotations.Bind;
import dev.blueocean.annotations.Controller;
import dev.blueocean.annotations.http.Get;
import dev.blueocean.annotations.http.Post;
import dev.blueocean.model.Cache;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.model.NetworkResponse;
import dev.blueocean.security.SecurityManager;
import io.informant.model.User;
import io.informant.repo.UserRepo;

@Controller
public class IdentityController {

    @Bind
    UserRepo userRepo;

    @Post("/authenticate")
    public String authenticate(Cache cache,
                               NetworkRequest req,
                               NetworkResponse resp,
                               SecurityManager securityManager){
        String phone = Informant.getPhone(req.getValue("credential"));
        String email = Informant.getEmail(req.getValue("credential"));
        String password = req.getValue("password");

        if(!securityManager.signin(phone, password, req, resp) || !securityManager.signin(email, password, req, resp)){
            cache.set("message", "Wrong phone or email and password");
            return "[redirect]/signin";
        }

        User authdUser = userRepo.getPhone(phone);
        req.getSession(true).set("userId", authdUser.getId());
        req.getSession(true).set("photo", authdUser.getPhoto());

        return "[redirect]/";
    }

    @Get("/signin")
    public String signin(){
        return "/signin.jsp";
    }

    @Get("/signup")
    public String signup(){
        return "/signup.jsp";
    }

    @Get("/signout")
    public String signout(NetworkRequest req, NetworkResponse resp, SecurityManager security){
        security.signout(req, resp);
        return "[redirect]/";
    }

    public boolean signin(String credential, String password, NetworkRequest req, NetworkResponse resp, SecurityManager securityManager){
        User user = userRepo.getPhone(credential);
        if(user == null) user = userRepo.getEmail(credential);
        if(user == null) {
            return false;
        }
        return securityManager.signin(credential, password, req, resp);
    }

}