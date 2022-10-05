package io.support;

import chico.support.DbSecurityAccess;
import io.Giga;
import io.model.User;
import io.repo.UserRepo;
import qio.annotate.Element;
import qio.annotate.Inject;

import java.util.Set;

@Element
public class DbAccess implements DbSecurityAccess {

    @Inject
    UserRepo userRepo;

    public String getPassword(String credential){
        credential = Giga.getSpaces(credential);
        User user = userRepo.get(credential);
        if(user == null){
            credential = Giga.getPhone(credential);
            user = userRepo.getPhone(credential);
        }
        return user.getPassword();
    }

    public Set<String> getRoles(String credential){
        credential = Giga.getSpaces(credential);
        User user = userRepo.get(credential);
        if(user == null){
            credential = Giga.getPhone(credential);
            user = userRepo.getPhone(credential);
        }
        Set<String> roles = userRepo.getUserRoles(user.getId());
        return roles;
    }

    public Set<String> getPermissions(String credential){
        credential = Giga.getSpaces(credential);
        User user = userRepo.get(credential);
        if(user == null){
            credential = Giga.getPhone(credential);
            user = userRepo.getPhone(credential);
        }
        Set<String> permissions = userRepo.getUserPermissions(user.getId());
        return permissions;
    }

}
