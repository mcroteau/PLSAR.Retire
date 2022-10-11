package giga;

import net.plsar.Dao;
import net.plsar.annotations.Repository;
import plsar.model.User;
import plsar.model.UserPermission;
import plsar.model.UserRole;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserRepo {

    Dao dao;

    public UserRepo(Dao dao){
        this.dao = dao;
    }

    public User getSaved() {
        String idSql = "select max(id) from users";
        long id = dao.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getId() {
        String sql = "select max(id) from users";
        long id = dao.getLong(sql, new Object[]{});
        return id;
    }

    public long getCount() {
        String sql = "select count(*) from users";
        Long count = dao.getLong(sql, new Object[] { });
        return count;
    }

    public User get(long id) {
        String sql = "select * from users where id = [+]";
        User user = (User) dao.get(sql, new Object[] { id }, User.class);

        if(user == null) user = new User();
        return user;
    }

    public User getPhone(String phone) {
        String sql = "select * from users where phone = '[+]'";
        User user = (User) dao.get(sql, new Object[] { phone }, User.class);
        return user;
    }

    public User getEmail(String email) {
        String sql = "select * from users where email = '[+]'";
        User user = (User) dao.get(sql, new Object[] { email }, User.class);
        return user;
    }

    public List<User> getList() {
        String sql = "select * from users";
        List<User> users = (ArrayList) dao.getList(sql, new Object[]{}, User.class);
        return users;
    }

    public Boolean save(User user) {
        String sql = "insert into users (phone, password) values ('[+]','[+]')";
        dao.save(sql, new Object[]{
            user.getPhone(),
            user.getPassword()
        });
        return true;
    }

    public boolean update(User user) {
        String sql = "update users set phone = '[+]', password = '[+]' where id = [+]";
        dao.update(sql, new Object[]{
                user.getPhone(),
                user.getPassword(),
                user.getId()
        });
        return true;
    }

    public boolean updatePassword(User user) {
        String sql = "update users set password = '[+]' where id = [+]";
        dao.update(sql, new Object[]{
                user.getPassword(),
                user.getId()
        });
        return true;
    }

    public User getReset(String username, String uuid){
        String sql = "select * from users where username = '[+]' and uuid = '[+]'";
        User user = (User) dao.get(sql, new Object[] { username, uuid }, User.class);
        return user;
    }

    public boolean delete(long id) {
        String sql = "delete from users where id = [+]";
        dao.update(sql, new Object[] { id });
        return true;
    }

    public String getUserPassword(String phone) {
        User user = getPhone(phone);
        return user.getPassword();
    }

    public boolean savePermission(long userId, String permission){
        String sql = "insert into user_permissions (user_id, permission) values ([+], '[+]')";
        dao.save(sql, new Object[]{ userId,  permission });
        return true;
    }

    public Set<String> getUserPermissions(long id) {
        String sql = "select permission from user_permissions where user_id = [+]";
        List<UserPermission> permissionsList = (ArrayList) dao.getList(sql, new Object[]{ id }, UserPermission.class);
        Set<String> permissions = new HashSet<>();
        for(UserPermission permission: permissionsList){
            permissions.add(permission.getPermission());
        }
        return permissions;
    }

}