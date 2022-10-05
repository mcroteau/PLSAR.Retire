package example.assist;

import example.UserRepo;
import example.model.User;
import oceanblue.security.DatabaseAccess;

import java.util.Set;

public class AuthAccess implements DatabaseAccess {

    UserRepo userRepo;

    public User getUser(String credential){
        User user = userRepo.getPhone(credential);
        if(user == null){
            user = userRepo.getEmail(credential);
        }
        return user;
    }

    public String getPassword(String credential){
        System.out.println("repo:" + userRepo);
        User user = getUser(credential);
        if(user != null) return user.getPassword();
        return "";
    }

    public Set<String> getRoles(String credential){
        User user = getUser(credential);
        Set<String> roles = userRepo.getUserRoles(user.getId());
        return roles;
    }

    public Set<String> getPermissions(String credential){
        User user = getUser(credential);
        Set<String> permissions = userRepo.getUserPermissions(user.getId());
        return permissions;
    }

    public AuthAccess(UserRepo userRepo){
        this.userRepo = userRepo;
    }

}
