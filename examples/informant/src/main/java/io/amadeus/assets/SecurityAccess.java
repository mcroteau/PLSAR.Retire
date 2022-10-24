package io.amadeus.assets;

import io.amadeus.model.User;
import io.amadeus.repo.UserRepo;
import io.kakai.annotate.Bind;
import io.kakai.annotate.Element;
import io.kakai.security.DatabaseAccess;

import java.util.HashSet;
import java.util.Set;

@Element
public class SecurityAccess implements DatabaseAccess {

    @Bind
    UserRepo userRepo;

    public String getPassword(String credential){
        User user = userRepo.getPhone(credential);
        if(user == null){
            user = userRepo.getEmail(credential);
        }
        if(user != null){
            return user.getPassword();
        }
        return "";
    }

    public Set<String> getRoles(String credential){
        return new HashSet<>();
    }

    public Set<String> getPermissions(String credential){
        User user = userRepo.getPhone(credential);
        if(user == null){
            user = userRepo.getEmail(credential);
        }
        Set<String> permissions = userRepo.getUserPermissions(user.getId());
        return permissions;
    }

}
