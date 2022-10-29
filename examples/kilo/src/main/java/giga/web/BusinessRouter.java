package giga.web;

import com.google.gson.Gson;
import giga.Giga;
import giga.model.Business;
import giga.model.Role;
import giga.model.User;
import giga.repo.BusinessRepo;
import giga.repo.RoleRepo;
import giga.repo.UserRepo;
import giga.service.BusinessService;
import net.plsar.annotations.*;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;
import net.plsar.model.NetworkRequest;
import net.plsar.model.NetworkResponse;
import net.plsar.security.SecurityManager;

@Controller
public class BusinessRouter {

    Gson gson = new Gson();

    @Bind
    UserRepo userRepo;

    @Bind
    RoleRepo roleRepo;

    @Bind
    BusinessRepo businessRepo;

    @Bind
    BusinessService businessService;

    @Post("/business/signup")
    public String businessSignup(Cache cache,
                                 NetworkRequest req,
                                 NetworkResponse resp,
                                 SecurityManager security) throws Exception {
        return businessService.businessSignup(cache, req, resp, security);
    }

    @Design("/designs/auth.jsp")
    @Get("/businesses/setup")
    public String setup(Cache cache,
                        NetworkRequest req,
                        SecurityManager security){
        return businessService.setup(cache, req, security);
    }


    @JsonOutput
    @Get("/{business}/oops")
    public String oops(Cache cache){
        cache.set("message", "sorry, something went wrong");
        return gson.toJson(cache);
    }

    @Design("/designs/auth.jsp")
    @Get("/snapshot/{id}")
    public String snapshot(Cache cache,
                           NetworkRequest req,
                           SecurityManager security,
                           @Component Long id){
        return businessService.snapshot(id, cache, req, security);
    }

    @Design("/designs/auth.jsp")
    @Get("/businesses/new/{id}")
    public String create(Cache cache,
                         NetworkRequest req,
                         SecurityManager security,
                         @Component Long id){
        return businessService.create(id, cache, req, security);
    }

    @Post("/businesses/save")
    public String save(NetworkRequest req,
                       SecurityManager security) throws Exception {
        return businessService.save(req, security);
    }

    //registration -> setup complete
    @Get("/businesses/signup/complete/{id}")
    public String signupComplete(Cache cache,
                                 NetworkRequest req,
                                 SecurityManager security,
                                 @Component Long id){
        return businessService.signupComplete(id, cache, req, security);
    }

    @Get("/businesses/settings/{id}")
    public String showSettings(Cache cache,
                               NetworkRequest req,
                               SecurityManager security,
                               @Component Long id){
        return businessService.showSettings(id, cache, req, security);
    }

    @Design("/designs/auth.jsp")
    @Post("/businesses/settings/save/{id}")
    public String saveSettings(Cache cache,
                               NetworkRequest req,
                               NetworkResponse resp,
                               SecurityManager security,
                               @Component Long id){
        return businessService.saveSettings(id, cache, req, resp, security);
    }

    @Get("/businesses/settings/save/{id}")
    public String showSettingsDos(Cache cache,
                                  NetworkRequest req,
                                  SecurityManager security,
                                  @Component Long id){
        return businessService.showSettings(id, cache, req, security);
    }

    @Post("/businesses/delete/{current}/{id}")
    public String delete(Cache cache,
                         NetworkRequest req,
                         SecurityManager security,
                         @Component Long currentId,
                         @Component Long id){
        return businessService.delete(currentId, id, cache, req, security);
    }

    @Get("/stripe/onboarding/setup/{id}")
    public String activateStripe(Cache cache,
                                 NetworkRequest req,
                                 NetworkResponse resp,
                                 SecurityManager security,
                                 @Component Long id){
        return businessService.activateStripe(id, cache, req, resp, security);
    }


    @Post("/{shop}/register")
    public String shopRegister(Cache cache,
                               NetworkRequest req,
                               NetworkResponse resp,
                               SecurityManager security,
                               @Component String shopUri){
        Business business = businessRepo.get(shopUri);
        if(business == null)return "redirect:/";

        User user = (User) req.inflect(User.class);

        if(user.getName() == null ||
                user.getName().equals("")){
            cache.set("message", "Help, could you give us your name?");
            return "redirect:/" + shopUri + "/signup";
        }

        if(user.getEmail() == null ||
                user.getEmail().equals("")){
            cache.set("message", "Please enter a valid email address.");
            return "redirect:/" + shopUri + "/signup";
        }

        User existingUser = userRepo.get(user.getEmail());
        if(existingUser != null){
            cache.set("message", "User exists with same email. Maybe its a mistake? ");
            return "redirect:/" + shopUri + "/signup";
        }

        if(user.getPassword() == null ||
                user.getPassword().equals("")) {
            cache.set("message", "Please enter a valid password 6 characters long at least.");
            return "redirect:/" + shopUri + "/signup";
        }

        if(user.getPassword().length() < 7){
            cache.set("message", "Please enter a valid password 6 characters long at least.");
            return "redirect:/" + shopUri + "/signup";
        }

        if(user.getPhone() != null){
            user.setPhone(Giga.getPhone(user.getPhone()));
        }

        String password = security.hash(user.getPassword());
        user.setPassword(password);
        user.setDateJoined(Giga.getDate());
        userRepo.save(user);

        User savedUser = userRepo.getSaved();

        Role savedRole = roleRepo.find(Giga.CUSTOMER_ROLE);
        userRepo.saveUserRole(savedUser.getId(), savedRole.getId());

        String permission = Giga.USER_MAINTENANCE + savedUser.getId();
        userRepo.savePermission(savedUser.getId(), permission);

        cache.set("message", "Awesome, welcome as a valued member. Happy shopping!");
        return "redirect:/" + shopUri;
    }

    @Get("/stripe/onboarding/refresh")
    public String noop(Cache cache){
        return "";
    }

    @Get("/stripe/onboarding/complete/{id}")
    public String onboardingComplete(Cache cache,
                                     NetworkRequest req,
                                     SecurityManager security,
                                     @Component Long id){
        return businessService.onboardingComplete(id, cache, req, security);
    }


}
