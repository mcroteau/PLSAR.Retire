package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.BusinessRepo;
import chico.Chico;
import giga.repo.SaleRepo;
import jakarta.servlet.http.HttpRequest;
import giga.repo.UserRepo;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Service;
import qio.model.web.Cache;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {


    @Inject
    SaleRepo saleRepo;

    @Inject
    UserRepo userRepo;

    @Inject
    SiteService siteService;

    @Inject
    AuthService authService;

    @Inject
    BusinessService businessService;

    @Inject
    BusinessRepo businessRepo;

    @Inject
    SmsService smsService;

    private String getPermission(String id){
        return Giga.USER_MAINTENANCE + id;
    }

    public String getEdit(Long id, Long businessId, Cache cache){
        String permission = getPermission(Long.toString(id));
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "[redirect]/";
        }

        businessService.setData(businessId, cache);

        User user = userRepo.get(id);
        cache.set("user", user);

        cache.set("page", "/pages/user/edit.jsp");
        return "/designs/auth.jsp";
    }


    public String update(Long id, Long businessId, Cache cache, HttpRequest req) {

        User user = (User) Qio.get(req, User.class);

        String permission = getPermission(Long.toString(user.getId()));
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "[redirect]/";
        }

        user.setPhone(Giga.getSpaces(user.getPhone()));
        userRepo.update(user);

        cache.set("message", "Your account was successfully updated");
        return "[redirect]/users/edit/" + businessId + "/" + id;
    }


//    public String send(Cache cache, HttpRequest req) {
//
//        try {
//            String phone = req.getParameter("phone");
//            if(phone != null) phone = Giga.getPhone(phone);
//            User user = userRepo.getPhone(phone);
//            if (user == null) {
//                data.put("message", "Unable to find user with cell phone " + phone + ". Please try again or if the problem persists, contact me Mike and I will reset your password for you. croteau.mike@gmail.com.");
//                return ("[redirect]/users/reset");
//            }
//
//            String guid = Giga.getString(4);
//            user.setPassword(Chico.dirty(guid));
//            userRepo.updatePassword(user);
//
//            String message = "Giga >_ Your temporary password : "    + guid;
//            smsService.send(phone, message);
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//
//        data.put("message", "Successfully sent you your reset instructions");
//        return "[redirect]/signin";
//    }

//    public String resetPassword(Long id, Cache cache, HttpRequest req) {
//
//        User user = userRepo.get(id);
//        User reqUser = (User) Qio.get(req, User.class);
//
//        if(reqUser.getPassword().length() < 7){
//            data.put("message", "Passwords must be at least 7 characters long.");
//            return "[redirect]/users/confirm?phone=" + reqUser.getPhone() + "&uuid=" + reqUser.getUuid();
//        }
//
//        if(!reqUser.getPassword().equals("")){
//            String password = Chico.dirty(reqUser.getPassword());
//            user.setPassword(password);
//            userRepo.updatePassword(user);
//        }
//
//        authService.signout();
//
//        data.put("message", "Password successfully updated! You can continue now!");
//        return "[redirect]/signin";
//    }

//    public String clients(Long businessId, Cache cache){
//        if(!authService.isAuthenticated()){
//            return "[redirect]/snapshot/" + businessId;
//        }
//
//        String businessPermission = Giga.BUSINESS_MAINTENANCE + businessId;
//        if(!authService.hasPermission(businessPermission)){
//            cache.set("message", "whaoo... ");
//            return "[redirect]/snapshot/" + businessId;
//        }
//
//        List<User> clients = new ArrayList<>();
//        List<User> users = businessRepo.getUsers(businessId);
//        for(User client : users){
//            List<Sale> sales = saleRepo.getUserSales(client.getId());
//
//            BigDecimal salesTotal = new BigDecimal(0);
//            for(Sale sale : sales){
//                salesTotal = salesTotal.add(sale.getAmount());
//            }
//            client.setSalesTotal(salesTotal);
//            client.setSalesCount(Long.valueOf(sales.size()));
//            clients.add(client);
//
//        }
//
//        cache.set("siteService", siteService);
//        cache.set("clients", clients);
//        cache.set("page", "/pages/user/list.jsp");
//
//        businessService.setData(businessId, cache);
//        return "/designs/auth.jsp";
//    }

}
