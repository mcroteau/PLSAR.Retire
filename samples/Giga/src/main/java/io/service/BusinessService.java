package io.service;

import chico.Chico;
import com.easypost.EasyPost;
import com.easypost.model.Address;
import com.stripe.Stripe;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import io.Giga;
import io.model.*;
import io.repo.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Property;
import qio.annotate.Service;
import qio.model.web.ResponseData;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BusinessService {


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
    AuthService authService;

    @Inject
    SiteService siteService;

    @Inject
    SmsService smsService;

    public String businessSignup(ResponseData data, HttpServletRequest request) throws Exception {

        Business business = (Business) Qio.get(request, Business.class);

        if(business.getName() == null ||
                business.getName().equals("")){
            data.set("message", "You got to give your business a name, ha! Come on now.");
            return "[redirect]/signup";
        }

        if(business.getPhone() == null ||
                business.getPhone().equals("")){
            data.set("message", "Please enter a valid cell phone number!");
            return "[redirect]/signup";
        }

        if(business.getEmail() == null ||
                business.getEmail().equals("")){
            data.set("message", "Please enter a valid email address.");
            return "[redirect]/signup";
        }

        String uri = Giga.getUri(business.getName());
        business.setUri(uri);
        Business storedBusiness = businessRepo.get(business.getUri());
        if(storedBusiness != null){
            data.set("message", "Business exists with same name. Please try a different business name...");
            return "[redirect]/signup";
        }

        String email = Giga.getSpaces(business.getEmail());
        business.setEmail(email);
        User storedUser = userRepo.get(email);
        if(storedUser != null){
            data.set("message", "User exists with same email. Maybe its a mistake? ");
            return "[redirect]/signup";
        }

        if(business.getPassword() == null ||
                business.getPassword().equals("")) {
            data.set("message", "Please enter a valid password 6 characters long at least.");
            return "[redirect]/signup";
        }

        if(business.getPassword().length() < 7){
            data.set("message", "Please enter a valid password 6 characters long at least.");
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
            data.set("message", "Rock on! Signin to continue...");
            return "[redirect]/signin";
        }

        User authUser = authService.getUser();

        data.set("message", "Go Rock! Good luck!");
        request.getSession().setAttribute("username", business.getEmail());
        request.getSession().setAttribute("userId", authUser.getId());

        smsService.send(business.getPhone(), "Giga >_ Welcome! If you have any problems please don't hesitate to send a text to (907)987-8652. My name is Mike, Im here to help!");


        return "[redirect]/businesses/signup/complete/" + savedBusiness.getId();
    }


    public String setup(ResponseData data){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        User authdUser = authService.getUser();
        data.set("authUser", authdUser);
        return "/pages/business/setup.jsp";
    }

    public String snapshot(Long id, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Whoa! Not authorized to view this business.");
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
            data.set("conversionRate", conversionRate);
            data.set("salesCarts", sales.size());
            data.set("totalCarts", totalCarts);
        }
        data.set("salesTotal", salesTotal);
        data.set("commissionTotal", commissionTotal);

        setData(id, data);

        data.set("page", "/pages/business/snapshot.jsp");
        return "/designs/auth.jsp";
    }

    public String create(Long id, ResponseData data){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        setData(id, data);

        data.set("page", "/pages/business/new.jsp");
        return "/designs/auth.jsp";
    }

    public String save(HttpServletRequest req) throws Exception {
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


    public String signupComplete(Long id, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to edit this business.");
            return "[redirect]/";
        }

        setData(id, data);
        data.set("page", "/pages/business/start.jsp");
        return "/designs/auth.jsp";
    }

//    public String list(Long id, ResponseData data) throws Exception {
//        System.out.println("get list");
//        if(!authService.isAuthenticated()){
//            System.out.println("not authenticated");
//            return "[redirect]/";
//        }
//        setData(id, data);
//
//        User authUser = authService.getUser();
//        List<Business> businesses = businessRepo.getList(authUser.getId());
//        data.set("businesses", businesses);
//
//        data.set("page", "/pages/business/list.jsp");
//        return "/designs/auth.jsp";
//    }

//    public String edit(Long id, ResponseData data) throws Exception {
//        if(!authService.isAuthenticated()){
//            return "[redirect]/";
//        }
//
//        Business business = businessRepo.get(id);
//        data.set("editBusiness", business);
//
//        setData(id, data);
//
//        data.set("page", "/pages/business/edit.jsp");
//        return "/designs/auth.jsp";
//    }


//    public String update(Long id, ResponseData data, HttpServletRequest req) {
//        if(!authService.isAuthenticated()){
//            return "[redirect]/";
//        }
//
//        String permission = Giga.BUSINESS_MAINTENANCE + id;
//        if(!authService.isAdministrator() &&
//                !authService.hasPermission(permission)){
//            data.set("message", "Unauthorized to edit this business.");
//            return "[redirect]/";
//        }
//
//        Business business = (Business) Qio.get(req, Business.class);
//        businessRepo.update(business);
//
//        return "[redirect]/businesses/edit/" + id;
//    }

    public String showSettings(Long id, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to edit this business.");
            return "[redirect]/";
        }
        setData(id, data);
        data.set("page", "/pages/business/settings.jsp");
        return "/designs/auth.jsp";
    }

    public String saveSettings(Long id, ResponseData data, HttpServletRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to edit this business.");
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

        setData(id, data);

        try {
            Address.createAndVerify(addressHash);
        }catch (com.easypost.exception.EasyPostException e) {
            data.set("message", "Address ain't right! Please make sure you enter a valid business address.");
            data.set("page", "/pages/business/settings.jsp");
            return "/designs/auth.jsp";
        }

        business.setInitial(false);
        businessRepo.update(business);
        setData(business.getId(), data);

        data.set("message", "Updated business settings.");
        data.set("page", "/pages/business/settings.jsp");
        return "/designs/auth.jsp";
    }


    public String delete(Long currentId, Long id, ResponseData responseData) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            responseData.put("message", "This business doesn't belong to you, you cannot delete this business.");
            return "[redirect]/";
        }

        itemRepo.deleteItems(id);
        categoryRepo.deleteCategories(id);
        cartRepo.deleteCarts(id);
        saleRepo.deleteSales(id);
        pageRepo.deletePages(id);
        designRepo.deleteDesigns(id);

        businessRepo.delete(id);
        responseData.put("message", "Successfully deleted business.");

        if(id != currentId){
            return "[redirect]/businesses/" + currentId;
        }else{
            return "[redirect]/";
        }
    }

    public String activateStripe(Long id, ResponseData data, HttpServletResponse resp) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "not your account buddy...");
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
            data.set("message", "Something went wrong, will you contact us and let us know?");
            return "[redirect]/";
        }

        return "";
    }

    public String onboardingComplete(Long id, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "not your account buddy...");
            return "[redirect]/";
        }

        Business business = businessRepo.get(id);
        business.setActivationComplete(true);
        businessRepo.update(business);

        setData(id, data);

        data.set("message", "Successfully configured your Stripe account! <br/>Congratulations. Good times!");
        return "[redirect]/snapshot/" + id;
    }

    public void setData(Long id, ResponseData data){
        User authdUser = authService.getUser();
        Business currentBusiness = businessRepo.get(id);
        Business primaryBusiness = businessRepo.get(currentBusiness.getPrimaryId());
        currentBusiness.setPrimary(primaryBusiness);
        List<Business> businesses = businessRepo.getList(authdUser.getId());
        Giga.sort(businesses);

        data.set("authUser", authdUser);
        data.set("business", currentBusiness);
        data.set("businessOptions", businesses);
        data.set("siteService", siteService);
    }

    public String get(String businessUri, ResponseData data) {
        Business business = businessRepo.get(businessUri);
        data.set("business", business);
        return "/pages/business/index.jsp";
    }

    public String getPage(Long id, String pageName){
        Page page = pageRepo.get(id, pageName);
        System.out.println("home c: " + page.getContent());
        return page.getContent();
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

        return true;
    }


}
