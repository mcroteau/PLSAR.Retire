package giga.router;

import chico.Chico;
import com.google.gson.Gson;
import giga.Giga;
import giga.model.Business;
import giga.model.Role;
import giga.model.User;
import giga.repo.BusinessRepo;
import giga.repo.RoleRepo;
import giga.repo.UserRepo;
import jakarta.servlet.http.HttpRequest;
import giga.service.BusinessService;
import jakarta.servlet.http.HttpServletResponse;
import qio.Qio;
import qio.annotate.*;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

@HttpHandler
public class BusinessHandler {

    Gson gson = new Gson();

    @Inject
    Qio qio;

    @Inject
    UserRepo userRepo;

    @Inject
    RoleRepo roleRepo;

    @Inject
    BusinessRepo businessRepo;

    @Inject
    BusinessService businessService;

    @Post("/business/signup")
    public String businessSignup(HttpRequest req,
                                 Cache data) throws Exception {
        return businessService.businessSignup(cache, req);
    }

    @Get("/businesses/setup")
    public String setup(Cache data){
        return businessService.setup(data);
    }


    @JsonOutput
    @Get("/{{business}}/oops")
    public String oops(Cache data){
        data.set("message", "sorry, something went wrong");
        return gson.toJson(data);
    }

    @Get("/snapshot/{{id}}")
    public String snapshot(Cache cache,
                           @RouteComponent Long id){
        return businessService.snapshot(id, data);
    }

    @Get("/businesses/new/{{id}}")
    public String create(Cache cache,
                        @RouteComponent Long id){
        return businessService.create(id, data);
    }

//    @Get("/businesses/{{id}}")
//    public String list(Cache cache,
//                       @RouteComponent Long id) throws Exception{
//        return businessService.list(id, data);
//    }

    @Post("/businesses/save")
    public String save(HttpRequest req) throws Exception {
        return businessService.save(req);
    }

    //registration -> setup complete
    @Get("/businesses/signup/complete/{{id}}")
    public String signupComplete(Cache cache,
                                  @RouteComponent Long id){
        return businessService.signupComplete(id, data);
    }

//    @Get("/businesses/edit/{{id}}")
//    public String edit(Cache cache,
//                       @RouteComponent Long id) throws Exception {
//        return businessService.edit(id, data);
//    }
//
//    @Post("/businesses/update/{{id}}")
//    public String update(HttpRequest req,
//                         Cache cache,
//                         @RouteComponent Long id) throws Exception {
//        return businessService.update(id, cache, req);
//    }

    @Get("/businesses/settings/{{id}}")
    public String showSettings(Cache cache,
                           @RouteComponent Long id){
        return businessService.showSettings(id, data);
    }

    @Post("/businesses/settings/save/{{id}}")
    public String saveSettings(HttpRequest req,
                               Cache cache,
                               @RouteComponent Long id){
        return businessService.saveSettings(id, cache, req);
    }

    @Get("/businesses/settings/save/{{id}}")
    public String showSettingsDos(Cache cache,
                               @RouteComponent Long id){
        return businessService.showSettings(id, data);
    }

    @Post("/businesses/delete/{{current}}/{{id}}")
    public String delete(Cache cache,
                         @RouteComponent Long currentId,
                         @RouteComponent Long id){
        return businessService.delete(currentId, id, data);
    }

    @Text
    @Get("/stripe/onboarding/setup/{{id}}")
    public String activateStripe(HttpServletResponse resp,
                              Cache cache,
                              @RouteComponent Long id){
        return businessService.activateStripe(id, cache, resp);
    }


    @Post("/{{shop}}/register")
    public String shopRegister(HttpRequest req,
                                   Cache cache,
                                   @RouteComponent String shopUri){
        Business business = businessRepo.get(shopUri);
        if(business == null)return "[redirect]/";

        User user = (User) qio.set(req, User.class);

        if(user.getName() == null ||
                user.getName().equals("")){
            data.set("message", "Help, could you give us your name?");
            return "[redirect]/" + shopUri + "/signup";
        }

        if(user.getUsername() == null ||
                user.getUsername().equals("")){
            data.set("message", "Please enter a valid email address.");
            return "[redirect]/" + shopUri + "/signup";
        }

        User existingUser = userRepo.get(user.getUsername());
        if(existingUser != null){
            data.set("message", "User exists with same email. Maybe its a mistake? ");
            return "[redirect]/" + shopUri + "/signup";
        }

        if(user.getPassword() == null ||
                user.getPassword().equals("")) {
            data.set("message", "Please enter a valid password 6 characters long at least.");
            return "[redirect]/" + shopUri + "/signup";
        }

        if(user.getPassword().length() < 7){
            data.set("message", "Please enter a valid password 6 characters long at least.");
            return "[redirect]/" + shopUri + "/signup";
        }

        if(user.getPhone() != null){
            user.setPhone(Giga.getPhone(user.getPhone()));
        }

        String password = Chico.dirty(user.getPassword());
        user.setPassword(password);
        user.setDateJoined(Giga.getDate());
        userRepo.save(user);

        User savedUser = userRepo.getSaved();

        Role savedRole = roleRepo.find(Giga.CUSTOMER_ROLE);
        userRepo.saveUserRole(savedUser.getId(), savedRole.getId());

        String permission = Giga.USER_MAINTENANCE + savedUser.getId();
        userRepo.savePermission(savedUser.getId(), permission);

        data.put("message", "Awesome, welcome as a valued member. Happy shopping!");
        return "[redirect]/" + shopUri;
    }

    @Get("/stripe/onboarding/refresh")
    public String noop(Cache data){
        return "";
    }

    @Get("/stripe/onboarding/complete/{{id}}")
    public String onboardingComplete(Cache cache,
                                     @RouteComponent Long id){
        return businessService.onboardingComplete(id, data);
    }


}
