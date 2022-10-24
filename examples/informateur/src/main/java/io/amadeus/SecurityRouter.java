package io.amadeus;

import io.amadeus.model.User;
import io.amadeus.repo.UserRepo;
import io.kakai.annotate.Bind;
import io.kakai.annotate.Design;
import io.kakai.annotate.Router;
import io.kakai.annotate.http.Get;
import io.kakai.annotate.http.Post;
import io.kakai.model.web.HttpRequest;
import io.kakai.model.web.HttpResponse;
import io.kakai.security.SecurityManager;

@Router
public class SecurityRouter {

    @Bind
    UserRepo userRepo;

    @Post("/authenticate")
    public String authenticate(HttpRequest req,
                               HttpResponse resp){
        String phone = Main.getPhone(req.value("credential"));
        String email = Main.getEmail(req.value("credential"));
        String password = req.value("password");
        if(!signin(phone, password) || !signin(email, password)){
            resp.set("message", "Wrong phone or email and password");
            return "[redirect]/signin";
        }

        User authdUser = userRepo.getPhone(phone);
        req.getSession().set("userId", authdUser.getId());
        req.getSession().set("photo", authdUser.getPhoto());

        return "[redirect]/";
    }

    @Get("/signin")
    @Design("/mystery.jsp")
    public String signin(HttpResponse resp){
        return "/signin.jsp";
    }

    @Get("/signup")
    @Design("/mystery.jsp")
    public String signup(HttpResponse resp){
        return "/signup.jsp";
    }

    @Get("/signout")
    public String signout(HttpRequest req,
                          HttpResponse resp){
        SecurityManager.signout();
        return "[redirect]/";
    }

    @Get("/unauthorized")
    @Design("/mystery.jsp")
    public String unauthorized(HttpResponse resp){
        return "/401.jsp";
    }

    public boolean signin(String credential, String password){
        User user = userRepo.getPhone(credential);
        if(user == null) user = userRepo.getEmail(credential);
        if(user == null) {
            return false;
        }
        return SecurityManager.signin(credential, password);
    }

    public User getUser(){
        String phone = SecurityManager.getUser();
        User user = userRepo.getPhone(phone);
        return user;
    }
}