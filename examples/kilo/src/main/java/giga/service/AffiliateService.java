package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import net.plsar.annotations.Bind;
import net.plsar.annotations.Service;
import net.plsar.model.Cache;
import net.plsar.model.NetworkRequest;
import net.plsar.security.SecurityManager;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AffiliateService {

    @Bind
    UserRepo userRepo;
    
    @Bind
    ItemRepo itemRepo;

    @Bind
    RoleRepo roleRepo;

    @Bind
    SaleRepo saleRepo;

    @Bind
    DesignRepo designRepo;

    @Bind
    CategoryRepo categoryRepo;

    @Bind
    BusinessRepo businessRepo;


    BusinessService businessService;

    public AffiliateService(){
        this.businessService = new BusinessService();
    }

    public String getAffiliates(Long id, Cache cache, NetworkRequest req, SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            return "redirect:/";
        }

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
        businessService.setData(id, cache, userRepo, businessRepo, req, security);
        return "/pages/affiliate/list.jsp";
    }

    public String getOnboarding(Cache cache, NetworkRequest req) {
        List<Business> businesses = businessRepo.getListPrimary();
        cache.set("businesses", businesses);
        cache.set("title", "Kilo! Partners Signup");
        return "/pages/affiliate/onboarding.jsp";
    }

    public String getRequests(Long id, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        List<BusinessRequest> businessRequests = businessRepo.getRequests(id);
        cache.set("businessRequests", businessRequests);
        businessService.setData(id, cache, userRepo, businessRepo, req, security);
        return "/pages/affiliate/requests.jsp";
    }

    public String status(String guid, Cache cache) {
        BusinessRequest businessRequest = businessRepo.getRequest(guid);
        Business business = businessRepo.get(businessRequest.getBusinessId());
        User user = userRepo.get(business.getUserId());
        cache.set("user", user);
        cache.set("business", business);
        cache.set("businessRequest", businessRequest);
        return "/pages/affiliate/status.jsp";
    }

    public String begin(Cache cache, NetworkRequest req) {
        BusinessRequest businessRequest = (BusinessRequest) req.inflect(BusinessRequest.class);
        businessRequest.setGuid(Giga.getString(7));
        businessRepo.saveRequest(businessRequest);

        Business business = businessRepo.get(businessRequest.getBusinessId());
        User user = userRepo.get(business.getUserId());
        BusinessRequest savedRequest = businessRepo.getSavedRequest();
        String requestPermission = Giga.REQUEST_MAINTENANCE + savedRequest.getId();
        userRepo.savePermission(user.getId(), requestPermission);

        cache.set("message", "Successfully submitted your application to become an business partner!");
        return "redirect:/affiliates/onboarding/status/" + businessRequest.getGuid();
    }

    public String deny(Long id, Cache cache, NetworkRequest req, SecurityManager security) {
        BusinessRequest businessRequest = businessRepo.getRequest(id);
        cache.set("businessRequest", businessRequest);

        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.REQUEST_MAINTENANCE + businessRequest.getId();
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            return "redirect:/";
        }

        businessRequest.setDenied(true);
        businessRequest.setApproved(false);
        businessRequest.setPending(false);
        businessRepo.updateRequest(businessRequest);

        return "redirect:/affiliates/onboarding/status/" + businessRequest.getGuid();

    }

    public String approve(Long id, Cache cache, NetworkRequest req, SecurityManager security) {
        BusinessRequest businessRequest = businessRepo.getRequest(id);
        cache.set("businessRequest", businessRequest);

        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.REQUEST_MAINTENANCE + businessRequest.getId();
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            return "redirect:/";
        }

        businessRequest.setApproved(true);
        businessRequest.setDenied(false);
        businessRequest.setPending(false);
        businessRepo.updateRequest(businessRequest);

        return "redirect:/affiliates/onboarding/status/" + businessRequest.getGuid();
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

        return "redirect:/affiliates/onboarding/finalize/" + savedBusiness.getId();
    }


    public String finalizeOnboarding(Long id, Cache cache) {
        Business business = businessRepo.get(id);
        cache.set("business", business);
        return "/pages/affiliate/finalize.jsp";
    }


    public String finalizeOnboarding(Cache cache,
                                     NetworkRequest request,
                                     SecurityManager security) {
        Business business = (Business) request.inflect(Business.class);

        try {

            User storedUser = userRepo.get(business.getEmail());
            if (storedUser != null) {
                cache.set("message", "Did you forget where you placed your keys? " +
                        "A user already exists with this email. Please use a different email. " +
                        "Please contact us if you have any questions.");
                return "redirect:/affiliates/onboarding/finalize/" + business.getId();
            }

            User user = new User();
            user.setDateJoined(Giga.getDate());
            user.setPhone(Giga.getPhone(business.getPhone()));
            user.setEmail(Giga.getSpaces(business.getEmail()));
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

//variety is the ultimate sex


            List<Item> items = itemRepo.getList(business.getPrimaryId());
            for(Item item: items){
                List<ItemCategory> itemCategories = categoryRepo.getCategoryItems(item.getId());

                Item copiedItem = item;
                copiedItem.setId(null);
                copiedItem.setBusinessId(business.getId());
                copiedItem.setDesignId(savedDesign.getId());
                itemRepo.save(item);
                Item savedItem = itemRepo.getSaved();
                String itemPermission = Giga.ITEM_MAINTENANCE + savedItem.getId();
                userRepo.savePermission(savedUser.getId(), itemPermission);

                for(ItemCategory itemCategory : itemCategories){
                    Category category = categoryRepo.get(itemCategory.getCategoryId());
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

                    ItemCategory itemCategoryDos = new ItemCategory();
                    itemCategoryDos.setItemId(savedItem.getId());
                    itemCategoryDos.setCategoryId(storedCategory.getId());
                    itemCategoryDos.setBusinessId(business.getId());
                    categoryRepo.saveItem(itemCategoryDos);
                }
            }

//add permissions

            cache.set("message", "You excited? Signin with your new credentials! Good luck!");

        }catch(Exception ex){
            ex.printStackTrace();
            return "redirect:/" + business.getUri();
        }

        cache.set("congratulations", "you did it!");
        return "redirect:/signin";
    }

}
