package io.service;

import chico.Chico;
import io.Giga;
import io.model.*;
import io.repo.*;
import jakarta.servlet.http.HttpServletRequest;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Service;
import qio.model.web.ResponseData;

import java.math.BigDecimal;
import java.util.List;

@Service
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
    AuthService authService;

    @Inject
    BusinessRepo businessRepo;

    @Inject
    BusinessService businessService;

    public String getAffiliates(Long id, ResponseData data){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "[redirect]/";
        }

        Business business = businessRepo.get(id);
        User user = userRepo.get(business.getUserId());
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

        data.set("affiliates", affiliates);
        businessService.setData(id, data);
        data.set("page", "/pages/affiliate/list.jsp");
        return "/designs/auth.jsp";
    }


    public String getOnboarding(ResponseData data, HttpServletRequest req) {
        List<Business> businesses = businessRepo.getListPrimary();
        data.set("businesses", businesses);
        data.set("title", "Giga! Partners Signup");
        data.set("page", "/pages/affiliate/onboarding.jsp");
        return "/designs/guest.jsp";
    }

    public String getRequests(Long id, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        List<BusinessRequest> businessRequests = businessRepo.getRequests(id);
        data.set("businessRequests", businessRequests);
        businessService.setData(id, data);
        data.set("page", "/pages/affiliate/requests.jsp");
        return "/designs/auth.jsp";
    }

    public String status(String guid, ResponseData data) {
        BusinessRequest businessRequest = businessRepo.getRequest(guid);
        Business business = businessRepo.get(businessRequest.getBusinessId());
        User user = userRepo.get(business.getUserId());
        data.set("user", user);
        data.set("business", business);
        data.set("businessRequest", businessRequest);
        data.set("page", "/pages/affiliate/status.jsp");
        return "/designs/partners.jsp";
    }

    public String begin(ResponseData data, HttpServletRequest req) {
        BusinessRequest businessRequest = (BusinessRequest) Qio.get(req, BusinessRequest.class);
        businessRequest.setGuid(Giga.getString(7));
        businessRepo.saveRequest(businessRequest);

        Business business = businessRepo.get(businessRequest.getBusinessId());
        User user = userRepo.get(business.getUserId());
        BusinessRequest savedRequest = businessRepo.getSavedRequest();
        String requestPermission = Giga.REQUEST_MAINTENANCE + savedRequest.getId();
        userRepo.savePermission(user.getId(), requestPermission);

        data.set("message", "Successfully submitted your application to become an business partner!");
        return "[redirect]/affiliates/onboarding/status/" + businessRequest.getGuid();
    }

    public String deny(Long id, ResponseData data) {
        BusinessRequest businessRequest = businessRepo.getRequest(id);
        data.set("businessRequest", businessRequest);

        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.REQUEST_MAINTENANCE + businessRequest.getId();
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "[redirect]/";
        }

        businessRequest.setDenied(true);
        businessRequest.setApproved(false);
        businessRequest.setPending(false);
        businessRepo.updateRequest(businessRequest);

        return "[redirect]/affiliates/onboarding/status/" + businessRequest.getGuid();

    }

    public String approve(Long id, ResponseData data) {
        BusinessRequest businessRequest = businessRepo.getRequest(id);
        data.set("businessRequest", businessRequest);

        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.REQUEST_MAINTENANCE + businessRequest.getId();
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "[redirect]/";
        }

        businessRequest.setApproved(true);
        businessRequest.setDenied(false);
        businessRequest.setPending(false);
        businessRepo.updateRequest(businessRequest);

        return "[redirect]/affiliates/onboarding/status/" + businessRequest.getGuid();
    }

    public String setupAffiliate(Long id, ResponseData data) {
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


    public String finalizeOnboarding(Long id, ResponseData data) {
        Business business = businessRepo.get(id);
        data.set("business", business);

        data.set("page","/pages/affiliate/finalize.jsp");
        return "/designs/partners.jsp";
    }


    public String finalizeOnboarding(ResponseData data, HttpServletRequest req) {
        Business business = (Business) Qio.get(req, Business.class);

        try {

            User storedUser = userRepo.get(business.getEmail());
            if (storedUser != null) {
                data.set("message", "Did you forget where you placed your keys? " +
                        "A user already exists with this email. Please use a different email. " +
                        "Please contact us if you have any questions.");
                return "[redirect]/affiliates/onboarding/finalize/" + business.getId();
            }

            User user = new User();
            user.setDateJoined(Giga.getDate());
            user.setPhone(Giga.getPhone(business.getPhone()));
            user.setUsername(Giga.getSpaces(business.getEmail()));
            user.setName(business.getOwner());
            user.setPassword(Chico.dirty(business.getPassword()));
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

            data.set("message", "You excited? Signin with your new credentials! Good luck!");

        }catch(Exception ex){
            ex.printStackTrace();
            return "[redirect]/" + business.getUri();
        }

        data.set("congratulations", "you did it!");
        return "[redirect]/signin";
    }

}
