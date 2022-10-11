package giga;

import giga.model.Permission;
import giga.model.User;
import net.plsar.Dao;
import net.plsar.security.SecurityAccess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthSecurityAccess implements SecurityAccess {

    Dao dao;

    public String getPassword(String credential){
        User user = getUser(credential);
        return user.getPassword();
    }

    @Override
    public Set<String> getRoles(String user) {
        return null;
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

    public Set<String> getPermissions(String credential){
        User user = getUser(credential);
        String sql = "select permission from user_permissions where user_id = [+]";
        List<Permission> permissionsList = (ArrayList) dao.getList(sql, new Object[]{ user.getId() }, Permission.class);
        Set<String> permissions = new HashSet<>();
        for(Permission permission: permissionsList){
            permissions.add(permission.getPermission());
        }
        return permissions;
    }

    public void setDao(Dao dao){
        this.dao = dao;
    }

}
