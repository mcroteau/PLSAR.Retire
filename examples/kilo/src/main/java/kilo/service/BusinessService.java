package kilo.service;

import kilo.Giga;
import kilo.model.*;
import kilo.repo.*;
import net.plsar.model.Cache;
import net.plsar.model.HttpRequest;

import java.math.BigDecimal;
import java.util.List;

public class BusinessService {

//    public String businessSignup(Cache cache, HttpRequest request) throws Exception {
//
//        Business business = (Business) Qio.get(request, Business.class);
//
//        if(business.getName() == null ||
//                business.getName().equals("")){
//            cache.set("message", "You got to give your business a name, ha! Come on now.");
//            return "[redirect]/signup";
//        }
//
//        if(business.getPhone() == null ||
//                business.getPhone().equals("")){
//            cache.set("message", "Please enter a valid cell phone number!");
//            return "[redirect]/signup";
//        }
//
//        if(business.getEmail() == null ||
//                business.getEmail().equals("")){
//            cache.set("message", "Please enter a valid email address.");
//            return "[redirect]/signup";
//        }
//
//        String uri = Giga.getUri(business.getName());
//        business.setUri(uri);
//        Business storedBusiness = businessRepo.get(business.getUri());
//        if(storedBusiness != null){
//            cache.set("message", "Business exists with same name. Please try a different business name...");
//            return "[redirect]/signup";
//        }
//
//        String email = Giga.getSpaces(business.getEmail());
//        business.setEmail(email);
//        User storedUser = userRepo.get(email);
//        if(storedUser != null){
//            cache.set("message", "User exists with same email. Maybe its a mistake? ");
//            return "[redirect]/signup";
//        }
//
//        if(business.getPassword() == null ||
//                business.getPassword().equals("")) {
//            cache.set("message", "Please enter a valid password 6 characters long at least.");
//            return "[redirect]/signup";
//        }
//
//        if(business.getPassword().length() < 7){
//            cache.set("message", "Please enter a valid password 6 characters long at least.");
//            return "[redirect]/signup";
//        }
//
//        User user = new User();
//        user.setPhone(Giga.getSpaces(business.getPhone()));
//        user.setUsername(Giga.getSpaces(business.getEmail()));
//        user.setPassword(Chico.dirty(business.getPassword()));
//        user.setDateJoined(Giga.getDate());
//        userRepo.save(user);
//
//        User savedUser = userRepo.getSaved();
//
//        Role savedRole = roleRepo.find(Giga.CUSTOMER_ROLE);
//        userRepo.saveUserRole(savedUser.getId(), savedRole.getId());
//
//        Role businessRole = roleRepo.get(Giga.BUSINESS_ROLE);
//        userRepo.saveUserRole(savedUser.getId(), businessRole.getId());
//
//        String permission = Giga.USER_MAINTENANCE + savedUser.getId();
//        userRepo.savePermission(savedUser.getId(), permission);
//
//        business.setUserId(savedUser.getId());
//        businessRepo.save(business);
//
//        Business savedBusiness = businessRepo.getSaved();
//
//        String businessPermission = Giga.BUSINESS_MAINTENANCE + savedBusiness.getId();
//        userRepo.savePermission(savedUser.getId(), businessPermission);
//
//
//        UserBusiness userBusiness = new UserBusiness(savedUser.getId(), savedBusiness.getId());
//        businessRepo.saveUser(userBusiness);
//
//        configure(savedBusiness, true);
//
//        userRepo.update(savedUser);
//
//        if(!authService.signin(business.getEmail(), business.getPassword())){
//            cache.set("message", "Rock on! Signin to continue...");
//            return "[redirect]/signin";
//        }
//
//        User authUser = authService.getUser();
//
//        cache.set("message", "Go Rock! Good luck!");
//        request.getSession().setAttribute("username", business.getEmail());
//        request.getSession().setAttribute("userId", authUser.getId());
//
//        smsService.send(business.getPhone(), "Giga >_ Welcome! If you have any problems please don't hesitate to send a text to (907)987-8652. My name is Mike, Im here to help!");
//
//
//        return "[redirect]/businesses/signup/complete/" + savedBusiness.getId();
//    }
//
//
//    public String setup(Cache cache){
//        if(!authService.isAuthenticated()){
//            return "[redirect]/";
//        }
//        User authdUser = authService.getUser();
//        cache.set("authUser", authdUser);
//        return "/pages/business/setup.jsp";
//    }
//
//    public String snapshot(Long id, Cache cache) {
//        if(!authService.isAuthenticated()){
//            return "[redirect]/";
//        }
//
//        String permission = Giga.BUSINESS_MAINTENANCE + id;
//        if(!authService.isAdministrator() &&
//                !authService.hasPermission(permission)){
//            cache.set("message", "Whoa! Not authorized to view this business.");
//            return "[redirect]/";
//        }
//
//        Business business = businessRepo.get(id);
//        System.out.println("b, " + business);
//        List<Sale> sales = new ArrayList<>();
//        if(business.getAffiliate() != null &&
//                business.getAffiliate()) {
//            sales = saleRepo.getListAffiliate(id);
//        }else{
//            sales = saleRepo.getListPrimary(id);
//        }
//
//        BigDecimal commissionTotal = new BigDecimal(0);
//        BigDecimal salesTotal = new BigDecimal(0);
//        for(Sale sale: sales){
//            BigDecimal commission = BigDecimal.valueOf(sale.getAffiliateAmount()).movePointLeft(2);
//            commissionTotal = commissionTotal.add(commission);
//            salesTotal = salesTotal.add(sale.getAmount());
//        }
//
//        if(sales.size() > 0) {
//            Long totalCarts = cartRepo.getTotal(id);
//            BigDecimal conversionRate = new BigDecimal(sales.size()).divide(new BigDecimal(totalCarts), 3, RoundingMode.HALF_UP).movePointRight(2);
//            cache.set("conversionRate", conversionRate);
//            cache.set("salesCarts", sales.size());
//            cache.set("totalCarts", totalCarts);
//        }
//        cache.set("salesTotal", salesTotal);
//        cache.set("commissionTotal", commissionTotal);
//
//        setData(id, cache);
//
//        cache.set("page", "/pages/business/snapshot.jsp");
//        return "/designs/auth.jsp";
//    }
//
//    public String create(Long id, Cache cache){
//        if(!authService.isAuthenticated()){
//            return "[redirect]/";
//        }
//        setData(id, cache);
//
//        cache.set("page", "/pages/business/new.jsp");
//        return "/designs/auth.jsp";
//    }
//
//    public String save(HttpRequest req) throws Exception {
//        if(!authService.isAuthenticated()){
//            return "[redirect]/";
//        }
//
//        Business business = (Business) Qio.get(req, Business.class);
//        businessRepo.save(business);
//
//        User authUser = authService.getUser();
//        Business savedBusiness = businessRepo.getSaved();
//        String permission = Giga.BUSINESS_MAINTENANCE + savedBusiness.getId();
//        userRepo.savePermission(authUser.getId(), permission);
//
//        List<Design> designs = designRepo.getList(savedBusiness.getId());
//
//        Boolean isBaseDesign = true;
//        if(designs.size() > 0) {
//            isBaseDesign = false;
//        }
//
//        configure(savedBusiness, isBaseDesign);
//
//        return "[redirect]/businesses/settings/" + savedBusiness.getId();
//    }
//
//
//    public String signupComplete(Long id, Cache cache) {
//        if(!authService.isAuthenticated()){
//            return "[redirect]/";
//        }
//
//        String permission = Giga.BUSINESS_MAINTENANCE + id;
//        if(!authService.isAdministrator() &&
//                !authService.hasPermission(permission)){
//            cache.set("message", "Unauthorized to edit this business.");
//            return "[redirect]/";
//        }
//
//        setData(id, cache);
//        cache.set("page", "/pages/business/start.jsp");
//        return "/designs/auth.jsp";
//    }
//
////    public String list(Long id, Cache cache) throws Exception {
////        System.out.println("get list");
////        if(!authService.isAuthenticated()){
////            System.out.println("not authenticated");
////            return "[redirect]/";
////        }
////        setData(id, cache);
////
////        User authUser = authService.getUser();
////        List<Business> businesses = businessRepo.getList(authUser.getId());
////        cache.set("businesses", businesses);
////
////        cache.set("page", "/pages/business/list.jsp");
////        return "/designs/auth.jsp";
////    }
//
////    public String edit(Long id, Cache cache) throws Exception {
////        if(!authService.isAuthenticated()){
////            return "[redirect]/";
////        }
////
////        Business business = businessRepo.get(id);
////        cache.set("editBusiness", business);
////
////        setData(id, cache);
////
////        cache.set("page", "/pages/business/edit.jsp");
////        return "/designs/auth.jsp";
////    }
//
//
////    public String update(Long id, Cache cache, HttpRequest req) {
////        if(!authService.isAuthenticated()){
////            return "[redirect]/";
////        }
////
////        String permission = Giga.BUSINESS_MAINTENANCE + id;
////        if(!authService.isAdministrator() &&
////                !authService.hasPermission(permission)){
////            cache.set("message", "Unauthorized to edit this business.");
////            return "[redirect]/";
////        }
////
////        Business business = (Business) Qio.get(req, Business.class);
////        businessRepo.update(business);
////
////        return "[redirect]/businesses/edit/" + id;
////    }
//
//    public String showSettings(Long id, Cache cache) {
//        if(!authService.isAuthenticated()){
//            return "[redirect]/";
//        }
//
//        String permission = Giga.BUSINESS_MAINTENANCE + id;
//        if(!authService.isAdministrator() &&
//                !authService.hasPermission(permission)){
//            cache.set("message", "Unauthorized to edit this business.");
//            return "[redirect]/";
//        }
//        setData(id, cache);
//        cache.set("page", "/pages/business/settings.jsp");
//        return "/designs/auth.jsp";
//    }
//
//    public String saveSettings(Long id, Cache cache, HttpRequest req) {
//        if(!authService.isAuthenticated()){
//            return "[redirect]/";
//        }
//
//        String permission = Giga.BUSINESS_MAINTENANCE + id;
//        if(!authService.isAdministrator() &&
//                !authService.hasPermission(permission)){
//            cache.set("message", "Unauthorized to edit this business.");
//            return "[redirect]/";
//        }
//
//        Business business = (Business) Qio.get(req, Business.class);
//        business.setUri(Giga.getUri(business.getUri()));
//
//        EasyPost.apiKey = easypostKey;
//
//        Map<String, Object> addressHash = new HashMap<>();
//
//        addressHash.put("street1", business.getStreet());
//        addressHash.put("street2", business.getStreetDos());
//        addressHash.put("city", business.getCity());
//        addressHash.put("state", business.getState());
//        addressHash.put("zip", business.getZip());
//        addressHash.put("country", business.getCountry());
//        addressHash.put("company", business.getName());
//        addressHash.put("phone", business.getPhone());
//
//        List<String> verificationList = new ArrayList<>();
//        verificationList.add("delivery");
//        addressHash.put("verify_strict", verificationList);
//
//        setData(id, cache);
//
//        try {
//            Address.createAndVerify(addressHash);
//        }catch (com.easypost.exception.EasyPostException e) {
//            cache.set("message", "Address ain't right! Please make sure you enter a valid business address.");
//            cache.set("page", "/pages/business/settings.jsp");
//            return "/designs/auth.jsp";
//        }
//
//        business.setInitial(false);
//        businessRepo.update(business);
//        setData(business.getId(), cache);
//
//        cache.set("message", "Updated business settings.");
//        cache.set("page", "/pages/business/settings.jsp");
//        return "/designs/auth.jsp";
//    }
//
//
//    public String delete(Long currentId, Long id, Cache Cache) {
//        if(!authService.isAuthenticated()){
//            return "[redirect]/";
//        }
//
//        String permission = Giga.BUSINESS_MAINTENANCE + id;
//        if(!authService.isAdministrator() &&
//                !authService.hasPermission(permission)){
//            Cache.put("message", "This business doesn't belong to you, you cannot delete this business.");
//            return "[redirect]/";
//        }
//
//        itemRepo.deleteItems(id);
//        categoryRepo.deleteCategories(id);
//        cartRepo.deleteCarts(id);
//        saleRepo.deleteSales(id);
//        pageRepo.deletePages(id);
//        designRepo.deleteDesigns(id);
//
//        businessRepo.delete(id);
//        Cache.put("message", "Successfully deleted business.");
//
//        if(id != currentId){
//            return "[redirect]/businesses/" + currentId;
//        }else{
//            return "[redirect]/";
//        }
//    }
//
//    public String activateStripe(Long id, Cache cache, HttpServletResponse resp) {
//        if(!authService.isAuthenticated()){
//            return "[redirect]/";
//        }
//
//        String permission = Giga.BUSINESS_MAINTENANCE + id;
//        if(!authService.isAdministrator() &&
//                !authService.hasPermission(permission)){
//            cache.set("message", "not your account buddy...");
//            return "[redirect]/";
//        }
//
//        AccountCreateParams accountParams =
//                AccountCreateParams.builder()
//                        .setType(AccountCreateParams.Type.STANDARD)
//                        .build();
//
//        try {
//
//            String refreshUrl = "http://" + host + "/stripe/onboarding/refresh";
//            String returnUrl = "http://" + host + "/stripe/onboarding/complete/" + id;
//
//            Stripe.apiKey = stripeKey;
//
//            Account account = Account.create(accountParams);
//            AccountLinkCreateParams linkParams =
//                    AccountLinkCreateParams.builder()
//                            .setAccount(account.getId())
//                            .setRefreshUrl(refreshUrl)
//                            .setReturnUrl(returnUrl)
//                            .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
//                            .build();
//
//            AccountLink accountLink = AccountLink.create(linkParams);
//
//            Business business = businessRepo.get(id);
//            business.setStripeId(account.getId());
//            businessRepo.update(business);
//
//            PrintWriter pw = resp.getWriter();
//            resp.sendRedirect(accountLink.getUrl());
//            pw.close();
//
//
//        }catch(Exception ex){
//            ex.printStackTrace();
//            cache.set("message", "Something went wrong, will you contact us and let us know?");
//            return "[redirect]/";
//        }
//
//        return "";
//    }
//
////    public String onboardingComplete(Long id, Cache cache) {
////        if(!authService.isAuthenticated()){
////            return "[redirect]/";
////        }
////
////        String permission = Giga.BUSINESS_MAINTENANCE + id;
////        if(!authService.isAdministrator() &&
////                !authService.hasPermission(permission)){
////            cache.set("message", "not your account buddy...");
////            return "[redirect]/";
////        }
////
////        Business business = businessRepo.get(id);
////        business.setActivationComplete(true);
////        businessRepo.update(business);
////
////        setData(id, cache);
////
////        cache.set("message", "Successfully configured your Stripe account! <br/>Congratulations. Good times!");
////        return "[redirect]/snapshot/" + id;
////    }

    public void setData(Long id, Cache cache, User authUser, BusinessRepo businessRepo, SiteService siteService){
        Business currentBusiness = businessRepo.get(id);
        Business primaryBusiness = businessRepo.get(currentBusiness.getPrimaryId());
        currentBusiness.setPrimary(primaryBusiness);
        List<Business> businesses = businessRepo.getList(authUser.getId());
        Giga.sort(businesses);

        cache.set("authUser", authUser);
        cache.set("business", currentBusiness);
        cache.set("businessOptions", businesses);
        cache.set("siteService", siteService);
    }
//
//    public String get(String businessUri, Cache cache) {
//        Business business = businessRepo.get(businessUri);
//        cache.set("business", business);
//        return "/pages/business/index.jsp";
//    }
//
//    public String getPage(Long id, String pageName){
//        Page page = pageRepo.get(id, pageName);
//        System.out.println("home c: " + page.getContent());
//        return page.getContent();
//    }
//
//    public boolean configure(Business business, Boolean isBaseDesign) throws Exception {
//
//        File standardBlueprint = new File(Paths.get("src", "main", "resources", "blueprint.html").toString());
//        InputStream is = new FileInputStream(standardBlueprint);
//        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
//
//        Design design = new Design();
//        design.setBaseDesign(isBaseDesign);
//        design.setName("Base Design");
//        design.setDesign(content);
//        design.setCss("body{background:#efefef;}");
//        design.setJavascript("console.info('Giga!')");
//        design.setBusinessId(business.getId());
//        designRepo.save(design);
//
//        Design baseDesign = designRepo.get(business.getId());
//        String designPermission = Giga.DESIGN_MAINTENANCE + baseDesign.getId();
//        userRepo.savePermission(business.getUserId(), designPermission);
//
//        String[] pages = {"Home", "About", "Contact"};
//
//        //actually, no... sorry. it wont work.
//
//        for(String name : pages) {
//            String uri = Giga.getUri(name);
//            Page page = new Page();
//            page.setName(name);
//            page.setUri(uri);
//            page.setContent("<h1>" + name + "</h1>");
//            page.setBusinessId(business.getId());
//            page.setDesignId(baseDesign.getId());
//            pageRepo.save(page);
//
//            Page savedPage = pageRepo.getSaved();
//            String pagePermission = Giga.PAGE_MAINTENANCE + savedPage.getId();
//            userRepo.savePermission(business.getUserId(), pagePermission);
//        }
//
//        return true;
//    }
//

    public void setData(Cart cart, Business business, Cache cache, HttpRequest req, ItemRepo itemRepo, DesignRepo designRepo, CartRepo cartRepo, SiteService siteService){
        BigDecimal subtotal = new BigDecimal(0);
        List<CartItem> cartItems = cartRepo.getListItems(cart.getId());
        System.out.println("ci " + cartItems.size());
        System.out.println("z");
        for(CartItem cartItem : cartItems){
            Item item = itemRepo.get(cartItem.getItemId());
            cartItem.setItem(item);

            BigDecimal itemTotal = cartItem.getPrice().multiply(cartItem.getQuantity());
            List<CartOption> cartOptions = cartRepo.getOptions(cartItem.getId());
            for(CartOption cartOption : cartOptions){
                ItemOption itemOption = itemRepo.getOption(cartOption.getItemOptionId());
                OptionValue optionValue = itemRepo.getValue(cartOption.getOptionValueId());
                cartOption.setItemOption(itemOption);
                cartOption.setOptionValue(optionValue);
                if(cartOption.getPrice() != null){
                    itemTotal = itemTotal.add(cartOption.getPrice().multiply(cartItem.getQuantity()));
                }
            }
            cartItem.setItemTotal(itemTotal);

            subtotal = subtotal.add(itemTotal);
            cart.setSubtotal(subtotal);

            cartItem.setCartOptions(cartOptions);

        }

        cart.setCartItems(cartItems);
        if(business.getFlatShipping()){
            BigDecimal total = cart.getSubtotal();
            if(business.getShipping() != null){
                total = total.add(business.getShipping());
            }
            cart.setShipping(business.getShipping());
            cart.setTotal(total);
            cartRepo.update(cart);
        }

        if(!business.getFlatShipping()){
            ShipmentRate rate = cartRepo.getRate(cart.getId());
            if(rate != null) {
                BigDecimal total = rate.getRate().add(cart.getSubtotal());
                cart.setShipping(rate.getRate());
                cart.setTotal(total);
                cart.setValidAddress(true);
                cartRepo.update(cart);
                cache.set("rate", rate);
            }
        }

        System.out.println("kilo : subtotal " + cart);
        Design design = designRepo.getBase(business.getId());
        System.out.println("business: " + business);
        System.out.println("design: " + design);

        cache.set("design", design);
        cache.set("request", req);
        cache.set("business", business);
        cache.set("cart", cart);
        cache.set("items", cartItems);
        cache.set("siteService", siteService);
    }

}
