package giga.router;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import giga.service.BusinessService;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Inject;
import net.plsar.annotations.Meta;
import net.plsar.annotations.Component;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;

import java.math.BigDecimal;
import java.util.List;

import net.plsar.model.HttpRequest;
import net.plsar.security.SecurityManager;

@HttpRouter
public class AffiliateRouter {

    @Inject
    UserRepo userRepo;

    @Inject
    RoleRepo roleRepo;

    @Inject
    ItemRepo itemRepo;

    @Inject
    CategoryRepo categoryRepo;

    @Inject
    SaleRepo saleRepo;

    @Inject
    DesignRepo designRepo;

    @Inject
    BusinessRepo businessRepo;

    BusinessService businessService;

    public AffiliateRouter(){
        this.businessService = new BusinessService();
    }

    @Meta(design="/designs/auth.jsp")
    @Get("/affiliates/{id}")
    public String getAffiliates(Cache cache,
                                HttpRequest httpRequest,
                                @Component Long id,
                                SecurityManager security){
        if(!security.isAuthenticated(httpRequest)){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, httpRequest) &&
                !security.hasPermission(permission, httpRequest)){
            return "[redirect]/";
        }

        BusinessService businessService = new BusinessService();
        Business business = businessRepo.get(id);
        List<Business> affiliates = businessRepo.getListAffiliate(id);
        for(Business affiliate : affiliates){
            List<Sale> sales = saleRepo.getListAffiliate(affiliate.getId());
            BigDecimal salesTotal = new BigDecimal(0);
            for(Sale sale: sales){
                BigDecimal affiliateAmount = new BigDecimal(sale.getAffiliateAmount());
                affiliateAmount = affiliateAmount.movePointLeft(2);
                System.out.println("am " + affiliateAmount);
                salesTotal.add(affiliateAmount);
            }
            affiliate.setSalesTotal(salesTotal);
        }

        cache.set("affiliates", affiliates);
        businessService.setData(id, cache);
        return "/pages/affiliate/list.jsp";
    }

    @Get("/affiliates/onboarding")
    public String getOnboarding(Cache cache){
        List<Business> businesses = businessRepo.getListPrimary();
        cache.set("businesses", businesses);
        cache.set("title", "Giga! Partners Signup");
        cache.set("page", "/pages/affiliate/onboarding.jsp");
        return "/designs/guest.jsp";
    }

    @Get("/affiliates/requests/{{id}}")
    public String getRequests(@Component Long id,
                                Cache cache,
                                HttpRequest httpRequest,
                                SecurityManager security){
        if(!security.isAuthenticated(httpRequest)){
            return "[redirect]/";
        }
        List<BusinessRequest> businessRequests = businessRepo.getRequests(id);
        cache.set("businessRequests", businessRequests);
        businessService.setData(id, cache);
        cache.set("page", "/pages/affiliate/requests.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/affiliates/onboarding/status/{{guid}}")
    public String status(Cache cache,
                        @Component String guid){
        BusinessRequest businessRequest = businessRepo.getRequest(guid);
        Business business = businessRepo.get(businessRequest.getBusinessId());
        User user = userRepo.get(business.getUserId());
        cache.set("user", user);
        cache.set("business", business);
        cache.set("businessRequest", businessRequest);
        cache.set("page", "/pages/affiliate/status.jsp");
        return "/designs/partners.jsp";
    }

    @Post("/affiliates/onboarding/begin")
    public String begin(HttpRequest req,
                       Cache cache){
        BusinessRequest businessRequest = (BusinessRequest) req.inflect(req, BusinessRequest.class);
        businessRequest.setGuid(Giga.getString(7));
        businessRepo.saveRequest(businessRequest);

        Business business = businessRepo.get(businessRequest.getBusinessId());
        User user = userRepo.get(business.getUserId());
        BusinessRequest savedRequest = businessRepo.getSavedRequest();
        String requestPermission = Giga.REQUEST_MAINTENANCE + savedRequest.getId();
        userRepo.savePermission(user.getId(), requestPermission);

        cache.set("message", "Successfully submitted your application to become an business partner!");
        return "[redirect]/affiliates/onboarding/status/" + businessRequest.getGuid();
    }

    @Post("/affiliates/onboarding/approve/{id}")
    public String approve(@Component Long id,
                          Cache cache,
                          HttpRequest httpRequest,
                          SecurityManager security){
        BusinessRequest businessRequest = businessRepo.getRequest(id);
        cache.set("businessRequest", businessRequest);

        if(!security.isAuthenticated(httpRequest)){
            return "[redirect]/";
        }

        String permission = Giga.REQUEST_MAINTENANCE + businessRequest.getId();
        if(!security.hasRole(Giga.SUPER_ROLE, httpRequest) &&
                !security.hasPermission(permission, httpRequest)){
            return "[redirect]/";
        }

        businessRequest.setApproved(true);
        businessRequest.setDenied(false);
        businessRequest.setPending(false);
        businessRepo.updateRequest(businessRequest);

        return "[redirect]/affiliates/onboarding/status/" + businessRequest.getGuid();
    }

    @Post("/affiliates/onboarding/pass/{guid}")
    public String deny(@Component Long id,
                       Cache cache,
                       HttpRequest httpRequest,
                       SecurityManager security){
        BusinessRequest businessRequest = businessRepo.getRequest(id);
        cache.set("businessRequest", businessRequest);

        if(!security.isAuthenticated(httpRequest)){
            return "[redirect]/";
        }

        String permission = Giga.REQUEST_MAINTENANCE + businessRequest.getId();
        if(!security.hasRole(Giga.SUPER_ROLE, httpRequest) &&
                !security.hasPermission(permission, httpRequest)){
            return "[redirect]/";
        }

        businessRequest.setDenied(true);
        businessRequest.setApproved(false);
        businessRequest.setPending(false);
        businessRepo.updateRequest(businessRequest);

        return "[redirect]/affiliates/onboarding/status/" + businessRequest.getGuid();
    }

    @Post("/affiliate/setup/{id}")
    public String setupAffiliate(@Component Long id){
        BusinessRequest businessRequest = businessRepo.getRequest(id);
        Business primaryBusiness = businessRepo.get(businessRequest.getBusinessId());

        Business business = new Business();
        business.setName(businessRequest.getBusinessName());
        business.setUri(Giga.getUri(businessRequest.getBusinessName()));
        businessRepo.save(business);

        Business savedBusiness = businessRepo.getSaved();

        savedBusiness.setPrimaryId(primaryBusiness.getId());
        savedBusiness.setBaseCommission(primaryBusiness.getBaseCommission());
        savedBusiness.setAffiliate(true);
        savedBusiness.setOwner(businessRequest.getName());
        savedBusiness.setEmail(businessRequest.getEmail());

        businessRepo.update(savedBusiness);

        Business updatedBusiness = businessRepo.get(savedBusiness.getId());
        System.out.println("phone email and such " + updatedBusiness.getEmail() + " : " + updatedBusiness.getPhone());

        return "[redirect]/affiliates/onboarding/finalize/" + savedBusiness.getId();
    }

    @Get("/affiliates/onboarding/finalize/{id}")
    public String finalizeOnboarding(Cache cache,
                                    @Component Long id){
        Business business = businessRepo.get(id);
        cache.set("business", business);

        cache.set("page","/pages/affiliate/finalize.jsp");
        return "/designs/partners.jsp";
    }

    @Post("/affiliates/onboarding/finalize/{id}")
    public String finalizeOnboarding(Cache cache, HttpRequest req, SecurityManager security) throws Exception {
        Business business = (Business) req.inflect(req, Business.class);

        try {

            User storedUser = userRepo.get(business.getEmail());
            if (storedUser != null) {
                cache.set("message", "Did you forget where you placed your keys? " +
                        "A user already exists with this email. Please use a different email. " +
                        "Please contact us if you have any questions.");
                return "[redirect]/affiliates/onboarding/finalize/" + business.getId();
            }

            User user = new User();
            user.setDateJoined(Giga.getDate());
            user.setPhone(Giga.getPhone(business.getPhone()));
            user.setUsername(Giga.getSpaces(business.getEmail()));
            user.setName(business.getOwner());
            user.setPassword(security.hash(business.getPassword()));
            userRepo.save(user);

            User savedUser = userRepo.getSaved();

            business.setAffiliate(true);
            business.setUserId(savedUser.getId());
            businessRepo.update(business);

            String userPermission = Giga.USER_MAINTENANCE + savedUser.getId();
            userRepo.savePermission(savedUser.getId(), userPermission);

            Role businessRole = roleRepo.get(Giga.BUSINESS_ROLE);
            userRepo.saveUserRole(savedUser.getId(), businessRole.getId());
            Role clientRole = roleRepo.get(Giga.CUSTOMER_ROLE);
            userRepo.saveUserRole(savedUser.getId(), clientRole.getId());

            String businessPermission = Giga.BUSINESS_MAINTENANCE + business.getId();
            userRepo.savePermission(savedUser.getId(), businessPermission);

            UserBusiness userBusiness = new UserBusiness();
            userBusiness.setBusinessId(business.getId());
            userBusiness.setUserId(savedUser.getId());
            businessRepo.saveUser(userBusiness);

            businessService.configure(business, true);
            Design savedDesign = designRepo.getSaved();

//variety is the ultimate aphrodisiac


            List<Item> items = itemRepo.getList(business.getPrimaryId());
            for(Item item: items){
                List<CategoryItem> categoryItems = categoryRepo.getCategoryItems(item.getId());

                Item copiedItem = item;
                copiedItem.setId(null);
                copiedItem.setBusinessId(business.getId());
                copiedItem.setDesignId(savedDesign.getId());
                itemRepo.save(item);
                Item savedItem = itemRepo.getSaved();
                String itemPermission = Giga.ITEM_MAINTENANCE + savedItem.getId();
                userRepo.savePermission(savedUser.getId(), itemPermission);

                for(CategoryItem categoryItem : categoryItems){
                    Category category = categoryRepo.get(categoryItem.getCategoryId());
                    Category categoryDos = category;
                    categoryDos.setId(null);
                    categoryDos.setDesignId(savedDesign.getId());
                    categoryDos.setBusinessId(business.getId());

                    Category storedCategory = categoryRepo.get(categoryDos.getName(), categoryDos.getUri(), business.getId());
                    if(storedCategory == null){
                        categoryRepo.save(categoryDos);
                        storedCategory = categoryRepo.getSaved();

                        String categoryPermission = Giga.ITEM_MAINTENANCE + storedCategory.getId();
                        userRepo.savePermission(savedUser.getId(), categoryPermission);
                    }

                    CategoryItem categoryItemDos = new CategoryItem();
                    categoryItemDos.setItemId(savedItem.getId());
                    categoryItemDos.setCategoryId(storedCategory.getId());
                    categoryItemDos.setBusinessId(business.getId());
                    categoryRepo.saveItem(categoryItemDos);
                }
            }

//add permissions

            cache.set("message", "You excited? Signin with your new credentials! Good luck!");

        }catch(Exception ex){
            ex.printStackTrace();
            return "[redirect]/" + business.getUri();
        }

        cache.set("congratulations", "you did it!");
        return "[redirect]/signin";
    }

}
