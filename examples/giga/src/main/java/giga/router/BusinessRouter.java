package giga.router;

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
import giga.service.BusinessService;
import giga.service.SiteService;
import giga.service.SmsService;
import net.plsar.RouteAttributes;
import net.plsar.annotations.*;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;
import net.plsar.model.HttpRequest;
import net.plsar.model.HttpResponse;
import net.plsar.security.SecurityManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@HttpRouter
public class BusinessRouter {

    Gson gson = new Gson();

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

    BusinessService businessService;

    public BusinessRouter(){
        this.businessService = new BusinessService();
    }

    @Post("/business/signup")
    public String businessSignup(Cache cache,
                                 HttpRequest req,
                                 HttpResponse resp,
                                 SecurityManager security) throws Exception {

        Business business = (Business) req.inflect(Business.class);

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
        user.setPassword(security.hash(business.getPassword()));
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

        if(!security.signin(business.getEmail(), business.getPassword(), req, resp)){
            cache.set("message", "Rock on! Signin to continue...");
            return "[redirect]/signin";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        cache.set("message", "Go Rock! Good luck!");
        req.getSession(true).set("username", business.getEmail());
        req.getSession(true).set("userId", authUser.getId());

        RouteAttributes routeAttributes = req.getRouteAttributes();
        String key = (String) routeAttributes.get("sms.key");

        SmsService smsService = new SmsService();
        smsService.send(key, business.getPhone(), "Giga >_ Welcome! If you have any problems please don't hesitate to send a text to (907) 987-8652. My name is Mike, Im here to help!");

        return "[redirect]/businesses/signup/complete/" + savedBusiness.getId();
    }

    @Get("/businesses/setup")
    public String setup(Cache cache,
                        HttpRequest req,
                        SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }
        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }
        cache.set("authUser", authUser);
        return "/pages/business/setup.jsp";
    }


    @Json
    @Get("/{{business}}/oops")
    public String oops(Cache cache){
        cache.set("message", "sorry, something went wrong");
        return gson.toJson(cache);
    }

    @Get("/snapshot/{{id}}")
    public String snapshot(Cache cache,
                           HttpRequest req,
                           SecurityManager security,
                           @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
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

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }
        setData(id, authUser, cache, security);

        cache.set("page", "/pages/business/snapshot.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/businesses/new/{{id}}")
    public String create(Cache cache,
                        HttpRequest req,
                        SecurityManager security,
                        @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }
        setData(id, authUser, cache, security);

        cache.set("page", "/pages/business/new.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/businesses/save")
    public String save(HttpRequest req,
                       SecurityManager security) throws Exception {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        Business business = (Business) req.inflect(Business.class);
        businessRepo.save(business);

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }
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
                                 HttpRequest req,
                                 HttpResponse resp,
                                 SecurityManager security,
                                 @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this business.");
            return "[redirect]/";
        }
        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        setData(id, authUser, cache, security);
        cache.set("page", "/pages/business/start.jsp");
        return "/designs/auth.jsp";
    }
    
    @Get("/businesses/settings/{{id}}")
    public String showSettings(Cache cache,
                               HttpRequest req,
                               HttpResponse resp,
                               SecurityManager security,
                               @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this business.");
            return "[redirect]/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        setData(id, authUser, cache, security);
        cache.set("page", "/pages/business/settings.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/businesses/settings/save/{{id}}")
    public String saveSettings(Cache cache,
                               HttpRequest req,
                               HttpResponse resp,
                               SecurityManager security,
                               @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this business.");
            return "[redirect]/";
        }

        Business business = (Business) req.inflect(Business.class);
        business.setUri(Giga.getUri(business.getUri()));

        RouteAttributes routeAttributes = req.getRouteAttributes();
        EasyPost.apiKey = (String) routeAttributes.get("easypost.key");

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



        try {
            Address.createAndVerify(addressHash);
        }catch (com.easypost.exception.EasyPostException e) {
            cache.set("message", "Address ain't right! Please make sure you enter a valid business address.");
            cache.set("page", "/pages/business/settings.jsp");
            return "/designs/auth.jsp";
        }

        business.setInitial(false);
        businessRepo.update(business);

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        setData(business.getId(), authUser, cache, security);

        cache.set("message", "Updated business settings.");
        cache.set("page", "/pages/business/settings.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/businesses/settings/save/{{id}}")
    public String showSettingsDos(Cache cache,
                                  HttpRequest req,
                                  HttpResponse resp,
                                  SecurityManager security,
                                  @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this business.");
            return "[redirect]/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        setData(id, authUser, cache, security);
        cache.set("page", "/pages/business/settings.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/businesses/delete/{{current}}/{{id}}")
    public String delete(Cache cache,
                         HttpRequest req,
                         HttpResponse resp,
                         SecurityManager security,
                         @Component Long currentId,
                         @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "This business doesn't belong to you, you cannot delete this business.");
            return "[redirect]/";
        }

        itemRepo.deleteItems(id);
        categoryRepo.deleteCategories(id);
        cartRepo.deleteCarts(id);
        saleRepo.deleteSales(id);
        pageRepo.deletePages(id);
        designRepo.deleteDesigns(id);

        businessRepo.delete(id);
        cache.set("message", "Successfully deleted business.");

        if(id != currentId){
            return "[redirect]/businesses/" + currentId;
        }else{
            return "[redirect]/";
        }
    }

    @Text
    @Get("/stripe/onboarding/setup/{{id}}")
    public String activateStripe(Cache cache,
                                 HttpRequest req,
                                 HttpResponse resp,
                                 SecurityManager security,
                                 @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "not your account buddy...");
            return "[redirect]/";
        }

        AccountCreateParams accountParams =
                AccountCreateParams.builder()
                        .setType(AccountCreateParams.Type.STANDARD)
                        .build();

        try {

            RouteAttributes routeAttributes = req.getRouteAttributes();
            String host = (String) routeAttributes.get("host");
            String stripeKey = (String) routeAttributes.get("stripe.key");

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


            /*
            resp.sendRedirect(accountLink.getUrl());
            pw.close();
            */

        }catch(Exception ex){
            ex.printStackTrace();
            cache.set("message", "Something went wrong, will you contact us and let us know?");
            return "[redirect]/";
        }

        return "";
    }


    @Post("/{{shop}}/register")
    public String shopRegister(Cache cache,
                               HttpRequest req,
                               HttpResponse resp,
                               SecurityManager security,
                               @Component String shopUri){
        Business business = businessRepo.get(shopUri);
        if(business == null)return "[redirect]/";

        User user = (User) req.inflect(User.class);

        if(user.getName() == null ||
                user.getName().equals("")){
            cache.set("message", "Help, could you give us your name?");
            return "[redirect]/" + shopUri + "/signup";
        }

        if(user.getUsername() == null ||
                user.getUsername().equals("")){
            cache.set("message", "Please enter a valid email address.");
            return "[redirect]/" + shopUri + "/signup";
        }

        User existingUser = userRepo.get(user.getUsername());
        if(existingUser != null){
            cache.set("message", "User exists with same email. Maybe its a mistake? ");
            return "[redirect]/" + shopUri + "/signup";
        }

        if(user.getPassword() == null ||
                user.getPassword().equals("")) {
            cache.set("message", "Please enter a valid password 6 characters long at least.");
            return "[redirect]/" + shopUri + "/signup";
        }

        if(user.getPassword().length() < 7){
            cache.set("message", "Please enter a valid password 6 characters long at least.");
            return "[redirect]/" + shopUri + "/signup";
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
        return "[redirect]/" + shopUri;
    }

    @Get("/stripe/onboarding/refresh")
    public String noop(Cache cache){
        return "";
    }

    @Get("/stripe/onboarding/complete/{{id}}")
    public String onboardingComplete(Cache cache,
                                     HttpRequest req,
                                     SecurityManager security,
                                     @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "not your account buddy...");
            return "[redirect]/";
        }

        Business business = businessRepo.get(id);
        business.setActivationComplete(true);
        businessRepo.update(business);

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        setData(id, authUser, cache, security);

        cache.set("message", "Successfully configured your Stripe account! <br/>Congratulations. Good times!");
        return "[redirect]/snapshot/" + id;
    }

    public void configure(Business business, Boolean isBaseDesign) throws Exception {

        Path path = Paths.get("src", "main", "resources", "blueprint.html");
        File standardBlueprint = new File(path.toString());
        InputStream is = new FileInputStream(standardBlueprint);
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        Design design = new Design();
        design.setBaseDesign(isBaseDesign);
        design.setName("Base Design");
        design.setDesign(content);
        design.setCss("body{background:#efefef;}");
        design.setJavascript("console.info('Giga!')");
        design.setBusinessId(business.getId());
        designRepo.save(design);

        Design baseDesign = designRepo.get(business.getId());
        String designPermission = Giga.DESIGN_MAINTENANCE + baseDesign.getId();
        userRepo.savePermission(business.getUserId(), designPermission);

        String[] pages = {"Home", "About", "Contact"};

        //actually, no... sorry. it wont work.

        for(String name : pages) {
            String uri = Giga.getUri(name);
            Page page = new Page();
            page.setName(name);
            page.setUri(uri);
            page.setContent("<h1>" + name + "</h1>");
            page.setBusinessId(business.getId());
            page.setDesignId(baseDesign.getId());
            pageRepo.save(page);

            Page savedPage = pageRepo.getSaved();
            String pagePermission = Giga.PAGE_MAINTENANCE + savedPage.getId();
            userRepo.savePermission(business.getUserId(), pagePermission);
        }

    }

    public void setData(Long id, User authUser, Cache cache, SecurityManager security){
        Business currentBusiness = businessRepo.get(id);
        Business primaryBusiness = businessRepo.get(currentBusiness.getPrimaryId());
        currentBusiness.setPrimary(primaryBusiness);
        List<Business> businesses = businessRepo.getList(authUser.getId());
        Giga.sort(businesses);

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);

        cache.set("authUser", authUser);
        cache.set("business", currentBusiness);
        cache.set("businessOptions", businesses);
        cache.set("siteService", siteService);
    }

}
