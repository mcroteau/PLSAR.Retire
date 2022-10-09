package giga.router;

import chico.Chico;
import com.easypost.EasyPost;
import com.easypost.model.Address;
import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import giga.Giga;
import giga.model.*;
import giga.repo.*;
import jakarta.servlet.http.HttpRequest;
import giga.service.BusinessService;
import jakarta.servlet.http.HttpServletResponse;
import qio.Qio;
import qio.annotate.*;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@HttpRouter
public class BusinessRouter {

    Gson gson = new Gson();

    @Property("host")
    String host;

    @Property("stripe.key")
    String stripeKey;

    @Property("easypost.key")
    String easypostKey;

    @Inject
    CartRepo cartRepo;

    @Inject
    RoleRepo roleRepo;

    @Inject
    SaleRepo saleRepo;

    @Inject
    ItemRepo itemRepo;

    @Inject
    CategoryRepo categoryRepo;

    @Inject
    UserRepo userRepo;

    @Inject
    PageRepo pageRepo;

    @Inject
    DesignRepo designRepo;

    @Inject
    BusinessRepo businessRepo;

    @Inject
    BusinessService businessService;

    @Post("/business/signup")
    public String businessSignup(HttpRequest req,
                                 Cache cache) throws Exception {

        Business business = (Business) Qio.get(request, Business.class);

        if(business.getName() == null ||
                business.getName().equals("")){
            cache.set("message", "You got to give your business a name, ha! Come on now.");
            return "[redirect]/signup";
        }

        if(business.getPhone() == null ||
                business.getPhone().equals("")){
            cache.set("message", "Please enter a valid cell phone number!");
            return "[redirect]/signup";
        }

        if(business.getEmail() == null ||
                business.getEmail().equals("")){
            cache.set("message", "Please enter a valid email address.");
            return "[redirect]/signup";
        }

        String uri = Giga.getUri(business.getName());
        business.setUri(uri);
        Business storedBusiness = businessRepo.get(business.getUri());
        if(storedBusiness != null){
            cache.set("message", "Business exists with same name. Please try a different business name...");
            return "[redirect]/signup";
        }

        String email = Giga.getSpaces(business.getEmail());
        business.setEmail(email);
        User storedUser = userRepo.get(email);
        if(storedUser != null){
            cache.set("message", "User exists with same email. Maybe its a mistake? ");
            return "[redirect]/signup";
        }

        if(business.getPassword() == null ||
                business.getPassword().equals("")) {
            cache.set("message", "Please enter a valid password 6 characters long at least.");
            return "[redirect]/signup";
        }

        if(business.getPassword().length() < 7){
            cache.set("message", "Please enter a valid password 6 characters long at least.");
            return "[redirect]/signup";
        }

        User user = new User();
        user.setPhone(Giga.getSpaces(business.getPhone()));
        user.setUsername(Giga.getSpaces(business.getEmail()));
        user.setPassword(Chico.dirty(business.getPassword()));
        user.setDateJoined(Giga.getDate());
        userRepo.save(user);

        User savedUser = userRepo.getSaved();

        Role savedRole = roleRepo.find(Giga.CUSTOMER_ROLE);
        userRepo.saveUserRole(savedUser.getId(), savedRole.getId());

        Role businessRole = roleRepo.get(Giga.BUSINESS_ROLE);
        userRepo.saveUserRole(savedUser.getId(), businessRole.getId());

        String permission = Giga.USER_MAINTENANCE + savedUser.getId();
        userRepo.savePermission(savedUser.getId(), permission);

        business.setUserId(savedUser.getId());
        businessRepo.save(business);

        Business savedBusiness = businessRepo.getSaved();

        String businessPermission = Giga.BUSINESS_MAINTENANCE + savedBusiness.getId();
        userRepo.savePermission(savedUser.getId(), businessPermission);


        UserBusiness userBusiness = new UserBusiness(savedUser.getId(), savedBusiness.getId());
        businessRepo.saveUser(userBusiness);

        configure(savedBusiness, true);

        userRepo.update(savedUser);

        if(!authService.signin(business.getEmail(), business.getPassword())){
            cache.set("message", "Rock on! Signin to continue...");
            return "[redirect]/signin";
        }

        User authUser = authService.getUser();

        cache.set("message", "Go Rock! Good luck!");
        request.getSession().setAttribute("username", business.getEmail());
        request.getSession().setAttribute("userId", authUser.getId());

        smsService.send(business.getPhone(), "Giga >_ Welcome! If you have any problems please don't hesitate to send a text to (907)987-8652. My name is Mike, Im here to help!");


        return "[redirect]/businesses/signup/complete/" + savedBusiness.getId();
    }

    @Get("/businesses/setup")
    public String setup(Cache cache){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        User authdUser = authService.getUser();
        cache.set("authUser", authdUser);
        return "/pages/business/setup.jsp";
    }


    @JsonOutput
    @Get("/{{business}}/oops")
    public String oops(Cache cache){
        data.set("message", "sorry, something went wrong");
        return gson.toJson(data);
    }

    @Get("/snapshot/{{id}}")
    public String snapshot(Cache cache,
                           @Component Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Whoa! Not authorized to view this business.");
            return "[redirect]/";
        }

        Business business = businessRepo.get(id);
        System.out.println("b, " + business);
        List<Sale> sales = new ArrayList<>();
        if(business.getAffiliate() != null &&
                business.getAffiliate()) {
            sales = saleRepo.getListAffiliate(id);
        }else{
            sales = saleRepo.getListPrimary(id);
        }

        BigDecimal commissionTotal = new BigDecimal(0);
        BigDecimal salesTotal = new BigDecimal(0);
        for(Sale sale: sales){
            BigDecimal commission = BigDecimal.valueOf(sale.getAffiliateAmount()).movePointLeft(2);
            commissionTotal = commissionTotal.add(commission);
            salesTotal = salesTotal.add(sale.getAmount());
        }

        if(sales.size() > 0) {
            Long totalCarts = cartRepo.getTotal(id);
            BigDecimal conversionRate = new BigDecimal(sales.size()).divide(new BigDecimal(totalCarts), 3, RoundingMode.HALF_UP).movePointRight(2);
            cache.set("conversionRate", conversionRate);
            cache.set("salesCarts", sales.size());
            cache.set("totalCarts", totalCarts);
        }
        cache.set("salesTotal", salesTotal);
        cache.set("commissionTotal", commissionTotal);

        setData(id, cache);

        cache.set("page", "/pages/business/snapshot.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/businesses/new/{{id}}")
    public String create(Cache cache,
                        @Component Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        setData(id, cache);

        cache.set("page", "/pages/business/new.jsp");
        return "/designs/auth.jsp";
    }

//    @Get("/businesses/{{id}}")
//    public String list(Cache cache,
//                       @Component Long id) throws Exception{
//        return businessService.list(id, data);
//    }

    @Post("/businesses/save")
    public String save(HttpRequest req) throws Exception {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        Business business = (Business) Qio.get(req, Business.class);
        businessRepo.save(business);

        User authUser = authService.getUser();
        Business savedBusiness = businessRepo.getSaved();
        String permission = Giga.BUSINESS_MAINTENANCE + savedBusiness.getId();
        userRepo.savePermission(authUser.getId(), permission);

        List<Design> designs = designRepo.getList(savedBusiness.getId());

        Boolean isBaseDesign = true;
        if(designs.size() > 0) {
            isBaseDesign = false;
        }

        configure(savedBusiness, isBaseDesign);

        return "[redirect]/businesses/settings/" + savedBusiness.getId();
    }

    //registration -> setup complete
    @Get("/businesses/signup/complete/{{id}}")
    public String signupComplete(Cache cache,
                                  @Component Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to edit this business.");
            return "[redirect]/";
        }

        setData(id, cache);
        cache.set("page", "/pages/business/start.jsp");
        return "/designs/auth.jsp";
    }

//    @Get("/businesses/edit/{{id}}")
//    public String edit(Cache cache,
//                       @Component Long id) throws Exception {
//        return businessService.edit(id, data);
//    }
//
//    @Post("/businesses/update/{{id}}")
//    public String update(HttpRequest req,
//                         Cache cache,
//                         @Component Long id) throws Exception {
//        return businessService.update(id, cache, req);
//    }

    @Get("/businesses/settings/{{id}}")
    public String showSettings(Cache cache,
                           @Component Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to edit this business.");
            return "[redirect]/";
        }
        setData(id, cache);
        cache.set("page", "/pages/business/settings.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/businesses/settings/save/{{id}}")
    public String saveSettings(HttpRequest req,
                               Cache cache,
                               @Component Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to edit this business.");
            return "[redirect]/";
        }

        Business business = (Business) Qio.get(req, Business.class);
        business.setUri(Giga.getUri(business.getUri()));

        EasyPost.apiKey = easypostKey;

        Map<String, Object> addressHash = new HashMap<>();

        addressHash.put("street1", business.getStreet());
        addressHash.put("street2", business.getStreetDos());
        addressHash.put("city", business.getCity());
        addressHash.put("state", business.getState());
        addressHash.put("zip", business.getZip());
        addressHash.put("country", business.getCountry());
        addressHash.put("company", business.getName());
        addressHash.put("phone", business.getPhone());

        List<String> verificationList = new ArrayList<>();
        verificationList.add("delivery");
        addressHash.put("verify_strict", verificationList);

        setData(id, cache);

        try {
            Address.createAndVerify(addressHash);
        }catch (com.easypost.exception.EasyPostException e) {
            cache.set("message", "Address ain't right! Please make sure you enter a valid business address.");
            cache.set("page", "/pages/business/settings.jsp");
            return "/designs/auth.jsp";
        }

        business.setInitial(false);
        businessRepo.update(business);
        setData(business.getId(), cache);

        cache.set("message", "Updated business settings.");
        cache.set("page", "/pages/business/settings.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/businesses/settings/save/{{id}}")
    public String showSettingsDos(Cache cache,
                               @Component Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to edit this business.");
            return "[redirect]/";
        }
        setData(id, cache);
        cache.set("page", "/pages/business/settings.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/businesses/delete/{{current}}/{{id}}")
    public String delete(Cache cache,
                         @Component Long currentId,
                         @Component Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            Cache.put("message", "This business doesn't belong to you, you cannot delete this business.");
            return "[redirect]/";
        }

        itemRepo.deleteItems(id);
        categoryRepo.deleteCategories(id);
        cartRepo.deleteCarts(id);
        saleRepo.deleteSales(id);
        pageRepo.deletePages(id);
        designRepo.deleteDesigns(id);

        businessRepo.delete(id);
        Cache.put("message", "Successfully deleted business.");

        if(id != currentId){
            return "[redirect]/businesses/" + currentId;
        }else{
            return "[redirect]/";
        }
    }

    @Text
    @Get("/stripe/onboarding/setup/{{id}}")
    public String activateStripe(HttpServletResponse resp,
                              Cache cache,
                              @Component Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "not your account buddy...");
            return "[redirect]/";
        }

        AccountCreateParams accountParams =
                AccountCreateParams.builder()
                        .setType(AccountCreateParams.Type.STANDARD)
                        .build();

        try {

            String refreshUrl = "http://" + host + "/stripe/onboarding/refresh";
            String returnUrl = "http://" + host + "/stripe/onboarding/complete/" + id;

            Stripe.apiKey = stripeKey;

            Account account = Account.create(accountParams);
            AccountLinkCreateParams linkParams =
                    AccountLinkCreateParams.builder()
                            .setAccount(account.getId())
                            .setRefreshUrl(refreshUrl)
                            .setReturnUrl(returnUrl)
                            .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                            .build();

            AccountLink accountLink = AccountLink.create(linkParams);

            Business business = businessRepo.get(id);
            business.setStripeId(account.getId());
            businessRepo.update(business);

            PrintWriter pw = resp.getWriter();
            resp.sendRedirect(accountLink.getUrl());
            pw.close();


        }catch(Exception ex){
            ex.printStackTrace();
            cache.set("message", "Something went wrong, will you contact us and let us know?");
            return "[redirect]/";
        }

        return "";
    }


    @Post("/{{shop}}/register")
    public String shopRegister(HttpRequest req,
                                   Cache cache,
                                   @Component String shopUri){
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
    public String noop(Cache cache){
        return "";
    }

    @Get("/stripe/onboarding/complete/{{id}}")
    public String onboardingComplete(Cache cache,
                                     @Component Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "not your account buddy...");
            return "[redirect]/";
        }

        Business business = businessRepo.get(id);
        business.setActivationComplete(true);
        businessRepo.update(business);

        setData(id, cache);

        cache.set("message", "Successfully configured your Stripe account! <br/>Congratulations. Good times!");
        return "[redirect]/snapshot/" + id;
    }


}
