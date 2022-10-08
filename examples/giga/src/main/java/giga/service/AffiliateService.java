package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import net.plsar.annotations.Inject;
import net.plsar.model.Cache;
import net.plsar.model.HttpRequest;
import net.plsar.security.SecurityManager;

import java.util.List;

public class AffiliateService {

    @Inject
    UserRepo userRepo;

    @Inject
    RoleRepo roleRepo;

    @Inject
    SaleRepo saleRepo;

    @Inject
    DesignRepo designRepo;

    @Inject
    ItemRepo itemRepo;

    @Inject
    CategoryRepo categoryRepo;

    @Inject
    BusinessRepo businessRepo;

    BusinessService businessService;

    public AffiliateService(){
        this.businessService = new BusinessService();
    }

    public String getOnboarding(Cache cache, HttpRequest req) {
        List<Business> businesses = businessRepo.getListPrimary();
        cache.set("businesses", businesses);
        cache.set("title", "Giga! Partners Signup");
        cache.set("page", "/pages/affiliate/onboarding.jsp");
        return "/designs/guest.jsp";
    }

    public String getRequests(Long id, Cache cache, HttpRequest httpRequest, SecurityManager security) {
        if(!security.isAuthenticated(httpRequest)){
            return "[redirect]/";
        }
        List<BusinessRequest> businessRequests = businessRepo.getRequests(id);
        cache.set("businessRequests", businessRequests);
        businessService.setData(id, cache);
        cache.set("page", "/pages/affiliate/requests.jsp");
        return "/designs/auth.jsp";
    }

    public String status(String guid, Cache cache) {
        BusinessRequest businessRequest = businessRepo.getRequest(guid);
        Business business = businessRepo.get(businessRequest.getBusinessId());
        User user = userRepo.get(business.getUserId());
        cache.set("user", user);
        cache.set("business", business);
        cache.set("businessRequest", businessRequest);
        cache.set("page", "/pages/affiliate/status.jsp");
        return "/designs/partners.jsp";
    }

    public String begin(Cache cache, HttpRequest req) {
        BusinessRequest businessRequest = (BusinessRequest) Qio.get(req, BusinessRequest.class);
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

    public String approve(Long id, Cache cache, HttpRequest httpRequest, SecurityManager security) {
        BusinessRequest businessRequest = businessRepo.getRequest(id);
        cache.set("businessRequest", businessRequest);

        if(!security.userIsAuthenticated(httpRequest)){
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

    public String deny(Long id, Cache cache, HttpRequest httpRequest, SecurityManager security) {
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

    public String setupAffiliate(Long id) {
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


    public String finalizeOnboarding(Long id, Cache cache) {
        Business business = businessRepo.get(id);
        cache.set("business", business);

        cache.set("page","/pages/affiliate/finalize.jsp");
        return "/designs/partners.jsp";
    }


    public String finalizeOnboarding(Cache cache, HttpRequest req, SecurityManager securityManager) {
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
            user.setPassword(security.dirty(business.getPassword()));
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
