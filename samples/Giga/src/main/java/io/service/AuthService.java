package io.service;

import chico.Chico;
import io.model.Business;
import io.model.UserBusiness;
import io.model.Role;
import io.repo.BusinessRepo;
import io.repo.RoleRepo;
import jakarta.servlet.http.HttpServletRequest;
import io.Giga;
import io.model.User;
import io.repo.UserRepo;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Service;
import qio.model.web.ResponseData;

@Service
public class AuthService {

    @Inject
    UserRepo userRepo;

    @Inject
    RoleRepo roleRepo;

    @Inject
    BusinessRepo businessRepo;

    @Inject
    BusinessService businessService;


    public boolean signin(String username, String password){
        String credential = Giga.getSpaces(username);
        User user = userRepo.get(credential);
        if(user == null) {
            credential = Giga.getPhone(credential);
            user = userRepo.getPhone(credential);
        }
        if(user == null){
            return false;
        }
        return Chico.signin(username, password);
    }

    public boolean signout(){
        return Chico.signout();
    }

    public boolean isAuthenticated(){
        return Chico.isAuthenticated();
    }

    public boolean isAdministrator(){
        return Chico.hasRole(Giga.SUPER_ROLE);
    }

    public boolean hasPermission(String permission){
        return Chico.hasPermission(permission);
    }

    public boolean hasRole(String role){
        return Chico.hasRole(role);
    }

    public User getUser(){
        String credentials = Chico.getUser();
        User user = userRepo.get(credentials);
        if(user == null){
            user = userRepo.getPhone(credentials);
        }
        return user;
    }

    public String authenticate(ResponseData data, HttpServletRequest req) {

        try{

            signout();

            String credential = req.getParameter("username");
            if(credential != null)credential = Giga.getSpaces(credential);

            String passwordDirty = req.getParameter("password");
            if(!signin(credential, passwordDirty)){
                data.set("message", "Wrong username and password");
                return "[redirect]/signin";
            }

            User authUser = userRepo.get(credential);
            if(authUser == null){
                authUser = userRepo.getPhone(credential);
            }

            req.getSession().setAttribute("username", authUser.getUsername());
            req.getSession().setAttribute("userId", authUser.getId());

        } catch ( Exception e ) {
            e.printStackTrace();
            data.set("message", "Please yell at one of us, something is a little off!");
            return "[redirect]/";
        }

        return "[redirect]/";
    }

    public String deAuthenticate(ResponseData data, HttpServletRequest req) {
        signout();
        data.set("message", "Successfully signed out");
        req.getSession().setAttribute("username", "");
        req.getSession().setAttribute("userId", "");
        return "[redirect]/";
    }

}
