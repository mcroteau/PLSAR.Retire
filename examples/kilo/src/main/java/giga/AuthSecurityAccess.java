package giga;

import giga.model.User;
import giga.model.UserPermission;
import giga.model.UserRole;
import dev.blueocean.Dao;
import dev.blueocean.security.SecurityAccess;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthSecurityAccess implements SecurityAccess {

    Dao dao;

    @Override
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    @Override
    public String getPassword(String credential){
        User user = getUser(credential);
        return user.getPassword();
    }

    @Override
    public Set<String> getRoles(String credential){
        User user = getUser(credential);
        String sql = "select r.name as name from user_roles ur inner join roles r on r.id = ur.role_id where ur.user_id = [+]";
        List<UserRole> rolesList = (ArrayList) dao.getList(sql, new Object[]{ user.getId() }, UserRole.class);
        Set<String> roles = new HashSet<>();
        for(UserRole role: rolesList){
            roles.add(role.getName());
        }
        return roles;
    }

    @Override
    public Set<String> getPermissions(String credential){
        User user = getUser(credential);
        String sql = "select permission from user_permissions where user_id = [+]";
        List<UserPermission> permissionsList = (ArrayList) dao.getList(sql, new Object[]{ user.getId() }, UserPermission.class);
        Set<String> permissions = new HashSet<>();
        for(UserPermission permission: permissionsList){
            permissions.add(permission.getPermission());
        }
        return permissions;
    }

    public User getUser(String credential){
        String phonesql = "select * from users where phone = '[+]'";
        User user = (User) dao.get(phonesql, new Object[] { credential }, User.class);
        if(user == null){
            String emailsql = "select * from users where email = '[+]'";
            user = (User) dao.get(emailsql, new Object[] { credential }, User.class);
        }
        return user;
    }


}
