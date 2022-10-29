package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import dev.blueocean.RouteAttributes;
import net.plsar.annotations.Bind;
import net.plsar.annotations.Service;
import net.plsar.model.Cache;
import net.plsar.model.NetworkRequest;
import net.plsar.model.NetworkResponse;
import net.plsar.security.SecurityManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Bind
    SaleRepo saleRepo;

    @Bind
    UserRepo userRepo;

    @Bind
    DesignRepo designRepo;

    @Bind
    CategoryRepo categoryRepo;

    @Bind
    BusinessRepo businessRepo;

    BusinessService businessService;

    public UserService(){
        this.businessService = new BusinessService();
    }

    private String getPermission(String id){
        return Giga.USER_MAINTENANCE + id;
    }

    public String getEdit(Long id, Long businessId, Cache cache, NetworkRequest req, SecurityManager security){
        String permission = getPermission(Long.toString(id));
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            return "redirect:/";
        }

        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        User user = userRepo.get(id);
        cache.set("user", user);

        return "/pages/user/edit.jsp";
    }


    public String update(Long id, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {

        User user = (User) req.inflect(User.class);

        String permission = getPermission(Long.toString(user.getId()));
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            return "redirect:/";
        }

        user.setPhone(Giga.getSpaces(user.getPhone()));
        userRepo.update(user);

        cache.set("message", "Your account was successfully updated");
        return "redirect:/users/edit/" + businessId + "/" + id;
    }


    public String send(Cache cache, NetworkRequest req, SecurityManager security) {

        try {
            String phone = req.getValue("phone");
            if(phone != null) phone = Giga.getPhone(phone);
            User user = userRepo.getPhone(phone);
            if (user == null) {
                cache.set("message", "Unable to find user with cell phone " + phone + ". Please try again or if the problem persists, contact me Mike and I will reset your password for you. croteau.mike@gmail.com.");
                return ("redirect:/users/reset");
            }

            String guid = Giga.getString(4);
            user.setPassword(security.hash(guid));
            userRepo.updatePassword(user);

            String message = "Kilo >_ Your temporary password : "    + guid;
            SmsService smsService = new SmsService();

            RouteAttributes routeAttributes = req.getRouteAttributes();
            String smsKey = (String) routeAttributes.get("sms.key");

            smsService.send(smsKey, phone, message);

        }catch(Exception e){
            e.printStackTrace();
        }

        cache.set("message", "Successfully sent you your reset instructions");
        return "redirect:/signin";
    }

    public String resetPassword(Long id, Cache cache, NetworkRequest req, NetworkResponse resp, SecurityManager security) {

        User user = userRepo.get(id);
        User reqUser = (User) req.inflect(User.class);

        if(reqUser.getPassword().length() < 7){
            cache.set("message", "Passwords must be at least 7 characters long.");
            return "redirect:/users/confirm?phone=" + reqUser.getPhone() + "&uuid=" + reqUser.getUuid();
        }

        if(!reqUser.getPassword().equals("")){
            String password = security.hash(reqUser.getPassword());
            user.setPassword(password);
            userRepo.updatePassword(user);
        }

        security.signout(req, resp);

        cache.set("message", "Password successfully updated! You can continue now!");
        return "redirect:/signin";
    }

    public String clients(Long businessId, Cache cache, NetworkRequest req, SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "redirect:/snapshot/" + businessId;
        }

        String businessPermission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasPermission(businessPermission, req)){
            cache.set("message", "whaoo... ");
            return "redirect:/snapshot/" + businessId;
        }

        List<User> clients = new ArrayList<>();
        List<User> users = businessRepo.getUsers(businessId);
        for(User client : users){
            List<Sale> sales = saleRepo.getUserSales(client.getId());

            BigDecimal salesTotal = new BigDecimal(0);
            for(Sale sale : sales){
                salesTotal = salesTotal.add(sale.getAmount());
            }
            client.setSalesTotal(salesTotal);
            client.setSalesCount(Long.valueOf(sales.size()));
            clients.add(client);

        }

        SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);
        cache.set("siteService", siteService);
        cache.set("clients", clients);

        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);
        return "/pages/user/list.jsp";
    }

}
