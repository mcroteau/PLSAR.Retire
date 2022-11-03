package foo;

import com.google.gson.Gson;
import foo.model.User;
import foo.repo.UserRepo;
import net.plsar.annotations.Bind;
import net.plsar.annotations.Controller;
import net.plsar.annotations.JsonOutput;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.NetworkRequest;
import net.plsar.model.NetworkResponse;
import net.plsar.model.PageCache;
import net.plsar.security.SecurityManager;

import java.util.Arrays;

@Controller
public class IdentityController {

    public IdentityController(){
        this.sigma = new Sigma();
    }

    Sigma sigma;

    Gson gson = new Gson();

    @Bind
    UserRepo userRepo;

    @JsonOutput
    @Get("/q")
    public String q(){
        String[] array = new String[]{"a, b, c"};
        return gson.toJson(Arrays.asList(array));
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
        return "redirect:/";
    }

    @JsonOutput
    @Post("/authenticate")
    public String authenticate(PageCache pageCache,
                               NetworkRequest req,
                               NetworkResponse resp,
                               SecurityManager securityManager){

        String credential = sigma.getPhone(req.getValue("credential"));
        String password = req.getValue("password");

        if(!securityManager.signin(credential, password, req, resp)){
            pageCache.set("message", "Wrong phone or email and password");
            return "redirect:/signin";
        }

        User authUser = userRepo.getPhone(credential);
        if(authUser == null) authUser = userRepo.getPhone(credential);

        req.getSession(true).set("userId", authUser.getId());
        req.getSession(true).set("userPhoto", authUser.getPhoto());

        return "redirect:/";
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