package giga.service;

import com.easypost.EasyPost;
import com.easypost.model.Address;
import com.stripe.Stripe;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import giga.Giga;
import giga.model.*;
import giga.repo.*;
import dev.blueocean.RouteAttributes;
import dev.blueocean.annotations.Bind;
import dev.blueocean.annotations.Service;
import dev.blueocean.model.Cache;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.model.NetworkResponse;
import dev.blueocean.security.SecurityManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BusinessService {

    @Bind
    CartRepo cartRepo;

    @Bind
    RoleRepo roleRepo;

    @Bind
    SaleRepo saleRepo;

    @Bind
    ItemRepo itemRepo;

    @Bind
    CategoryRepo categoryRepo;

    @Bind
    UserRepo userRepo;

    @Bind
    PageRepo pageRepo;

    @Bind
    DesignRepo designRepo;

    @Bind
    BusinessRepo businessRepo;

    public String businessSignup(Cache cache, NetworkRequest req, NetworkResponse resp, SecurityManager security) throws Exception {

        Business business = (Business) req.inflect(Business.class);

        if(business.getName() == null ||
                business.getName().equals("")){
            cache.set("message", "You got to give your business a name, ha! Come on now.");
            return "redirect:/signup";
        }

        if(business.getPhone() == null ||
                business.getPhone().equals("")){
            cache.set("message", "Please enter a valid cell phone number!");
            return "redirect:/signup";
        }

        if(business.getEmail() == null ||
                business.getEmail().equals("")){
            cache.set("message", "Please enter a valid email address.");
            return "redirect:/signup";
        }

        String uri = Giga.getUri(business.getName());
        business.setUri(uri);
        Business storedBusiness = businessRepo.get(business.getUri());
        if(storedBusiness != null){
            cache.set("message", "Business exists with same name. Please try a different business name...");
            return "redirect:/signup";
        }

        String email = Giga.getSpaces(business.getEmail());
        business.setEmail(email);
        User storedUser = userRepo.get(email);
        if(storedUser != null){
            cache.set("message", "User exists with same email. Maybe its a mistake? ");
            return "redirect:/signup";
        }

        if(business.getPassword() == null ||
                business.getPassword().equals("")) {
            cache.set("message", "Please enter a valid password 6 characters long at least.");
            return "redirect:/signup";
        }

        if(business.getPassword().length() < 7){
            cache.set("message", "Please enter a valid password 6 characters long at least.");
            return "redirect:/signup";
        }

        User user = new User();
        user.setPhone(Giga.getSpaces(business.getPhone()));
        user.setEmail(Giga.getSpaces(business.getEmail()));
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
            return "redirect:/signin";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.getPhone(credential);
        if(authUser == null){
            authUser = userRepo.getEmail(credential);
        }

        cache.set("message", "Go Rock! Good luck!");
        req.getSession(true).set("username", business.getEmail());
        req.getSession(true).set("userId", authUser.getId());

        SmsService smsService = new SmsService();
        RouteAttributes routeAttributes = req.getRouteAttributes();
        String smsKey = (String) routeAttributes.get("sms.key");

        smsService.send(smsKey, business.getPhone(), "Kilo >_ Welcome! If you have any problems please don't hesitate to send a text to (907)987-8652. My name is Mike, Im here to help!");

        return "redirect:/businesses/signup/complete/" + savedBusiness.getId();
    }


    public String setup(Cache cache, NetworkRequest req, SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.getPhone(credential);
        if(authUser == null){
            authUser = userRepo.getEmail(credential);
        }

        cache.set("authUser", authUser);
        return "/pages/business/setup.jsp";
    }

    public String snapshot(Long id, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Whoa! Not authorized to view this business.");
            return "redirect:/";
        }

        Business business = businessRepo.get(id);
        System.out.println("b, " + business);
        List<Sale> sales;
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

        setData(id, cache, userRepo, businessRepo, req, security);

        return "/pages/business/snapshot.jsp";
    }

    public String create(Long id, Cache cache, NetworkRequest req, SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        setData(id, cache, userRepo, businessRepo, req, security);

        return "/pages/business/new.jsp";
    }

    public String save(NetworkRequest req, SecurityManager security) throws Exception {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        Business business = (Business) req.inflect(Business.class);
        businessRepo.save(business);

        String credential = security.getUser(req);
        User authUser = userRepo.getPhone(credential);
        if(authUser == null){
            authUser = userRepo.getEmail(credential);
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

        return "redirect:/businesses/settings/" + savedBusiness.getId();
    }


    public String signupComplete(Long id, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this business.");
            return "redirect:/";
        }

        setData(id, cache, userRepo, businessRepo, req, security);
        return "/pages/business/start.jsp";
    }

    public String showSettings(Long id, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this business.");
            return "redirect:/";
        }
        setData(id, cache, userRepo,  businessRepo, req, security);
        return "/pages/business/settings.jsp";
    }

    public String saveSettings(Long id, Cache cache, NetworkRequest req, NetworkResponse resp, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this business.");
            return "redirect:/";
        }

        Business business = (Business) req.inflect(Business.class);
        business.setUri(Giga.getUri(business.getUri()));

        RouteAttributes routeAttributes = req.getRouteAttributes();
        String shipmentKey = (String) routeAttributes.get("shipment.key");

        EasyPost.apiKey = shipmentKey;

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

        setData(id, cache, userRepo, businessRepo, req, security);

        try {
            Address.createAndVerify(addressHash);
        }catch (com.easypost.exception.EasyPostException e) {
            cache.set("message", "Address ain't right! Please make sure you enter a valid business address.");
            return "/pages/business/settings.jsp";
        }

        business.setInitial(false);
        businessRepo.update(business);
        setData(business.getId(), cache, userRepo, businessRepo, req, security);

        cache.set("message", "Updated business settings.");
        return "/pages/business/settings.jsp";
    }


    public String delete(Long currentId, Long id, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "This business doesn't belong to you, you cannot delete this business.");
            return "redirect:/";
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
            return "redirect:/businesses/" + currentId;
        }else{
            return "redirect:/";
        }
    }

    public String activateStripe(Long id, Cache cache, NetworkRequest req, NetworkResponse resp, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "not your account buddy...");
            return "redirect:/";
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

            resp.send(accountLink.getUrl());

        }catch(Exception ex){
            ex.printStackTrace();
            cache.set("message", "Something went wrong, will you contact us and let us know?");
            return "redirect:/";
        }

        return "";
    }

    public String onboardingComplete(Long id, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "not your account buddy...");
            return "redirect:/";
        }

        Business business = businessRepo.get(id);
        business.setActivationComplete(true);
        businessRepo.update(business);

        setData(id, cache, userRepo, businessRepo, req, security);

        cache.set("message", "Successfully configured your Stripe account! <br/>Congratulations. Good times!");
        return "redirect:/snapshot/" + id;
    }

    public void setData(Long id, Cache cache, UserRepo userRepo, BusinessRepo businessRepo, NetworkRequest req, SecurityManager security){

        String credential = security.getUser(req);
        User authUser = userRepo.getPhone(credential);
        if(authUser == null){
            authUser = userRepo.getEmail(credential);
        }

        Business currentBusiness = businessRepo.get(id);
        Business primaryBusiness = businessRepo.get(currentBusiness.getPrimaryId());
        currentBusiness.setPrimary(primaryBusiness);
        List<Business> businesses = businessRepo.getList(authUser.getId());
        Giga.sort(businesses);

        SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);

        cache.set("authUser", authUser);
        cache.set("business", currentBusiness);
        cache.set("businessOptions", businesses);
        cache.set("siteService", siteService);
    }

    public String get(String businessUri, Cache cache) {
        Business business = businessRepo.get(businessUri);
        cache.set("business", business);
        return "/pages/business/index.jsp";
    }

    public String getPage(Long id, String pageName){
        Page page = pageRepo.get(id, pageName);
        System.out.println("home c: " + page.getContent());
        return page.getContent();
    }

    public boolean configure(Business business, Boolean isBaseDesign, UserRepo userRepo, PageRepo pageRepo, DesignRepo designRepo) throws Exception {

        File standardBlueprint = new File(Paths.get("src", "main", "resources", "blueprint.html").toString());
        InputStream is = new FileInputStream(standardBlueprint);
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        Design design = new Design();
        design.setBaseDesign(isBaseDesign);
        design.setName("Base Design");
        design.setDesign(content);
        design.setCss("body{background:#efefef;}");
        design.setJavascript("console.info('Kilo!')");
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

        return true;
    }

    public boolean configure(Business business, Boolean isBaseDesign) throws Exception {

        File standardBlueprint = new File(Paths.get("src", "main", "resources", "blueprint.html").toString());
        InputStream is = new FileInputStream(standardBlueprint);
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        Design design = new Design();
        design.setBaseDesign(isBaseDesign);
        design.setName("Base Design");
        design.setDesign(content);
        design.setCss("body{background:#efefef;}");
        design.setJavascript("console.info('Kilo!')");
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

        return true;
    }


}
