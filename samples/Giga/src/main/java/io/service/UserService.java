package io.service;

import io.Giga;
import io.model.*;
import io.repo.BusinessRepo;
import chico.Chico;
import io.repo.SaleRepo;
import jakarta.servlet.http.HttpServletRequest;
import io.repo.UserRepo;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Service;
import qio.model.web.ResponseData;

import java.math.BigDecimal;
import java.net.URLEncoder;
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

    public String getEdit(Long id, Long businessId, ResponseData data){
        String permission = getPermission(Long.toString(id));
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "[redirect]/";
        }

        businessService.setData(businessId, data);

        User user = userRepo.get(id);
        data.set("user", user);

        data.set("page", "/pages/user/edit.jsp");
        return "/designs/auth.jsp";
    }


    public String update(Long id, Long businessId, ResponseData data, HttpServletRequest req) {

        User user = (User) Qio.get(req, User.class);

        String permission = getPermission(Long.toString(user.getId()));
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "[redirect]/";
        }

        user.setPhone(Giga.getSpaces(user.getPhone()));
        userRepo.update(user);

        data.set("message", "Your account was successfully updated");
        return "[redirect]/users/edit/" + businessId + "/" + id;
    }


    public String send(ResponseData data, HttpServletRequest req) {

        try {
            String phone = req.getParameter("phone");
            if(phone != null) phone = Giga.getPhone(phone);
            User user = userRepo.getPhone(phone);
            if (user == null) {
                data.put("message", "Unable to find user with cell phone " + phone + ". Please try again or if the problem persists, contact me Mike and I will reset your password for you. croteau.mike@gmail.com.");
                return ("[redirect]/users/reset");
            }

            String guid = Giga.getString(4);
            user.setPassword(Chico.dirty(guid));
            userRepo.updatePassword(user);

            String message = "Giga >_ Your temporary password : "    + guid;
            smsService.send(phone, message);

        }catch(Exception e){
            e.printStackTrace();
        }

        data.put("message", "Successfully sent you your reset instructions");
        return "[redirect]/signin";
    }

    public String resetPassword(Long id, ResponseData data, HttpServletRequest req) {

        User user = userRepo.get(id);
        User reqUser = (User) Qio.get(req, User.class);

        if(reqUser.getPassword().length() < 7){
            data.put("message", "Passwords must be at least 7 characters long.");
            return "[redirect]/users/confirm?phone=" + reqUser.getPhone() + "&uuid=" + reqUser.getUuid();
        }

        if(!reqUser.getPassword().equals("")){
            String password = Chico.dirty(reqUser.getPassword());
            user.setPassword(password);
            userRepo.updatePassword(user);
        }

        authService.signout();

        data.put("message", "Password successfully updated! You can continue now!");
        return "[redirect]/signin";
    }

    public String clients(Long businessId, ResponseData data){
        if(!authService.isAuthenticated()){
            return "[redirect]/snapshot/" + businessId;
        }

        String businessPermission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!authService.hasPermission(businessPermission)){
            data.set("message", "whaoo... ");
            return "[redirect]/snapshot/" + businessId;
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

        data.set("siteService", siteService);
        data.set("clients", clients);
        data.set("page", "/pages/user/list.jsp");

        businessService.setData(businessId, data);
        return "/designs/auth.jsp";
    }

}
