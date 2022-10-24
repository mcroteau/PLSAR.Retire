package io.amadeus.repo;

import io.amadeus.model.Permission;
import io.amadeus.model.UserFollow;
import io.kakai.Kakai;
import io.kakai.annotate.Bind;
import io.amadeus.model.User;
import io.kakai.annotate.Repo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repo
public class UserRepo {

    @Bind
    Kakai kakai;

    public User getSaved() {
        String idSql = "select max(id) from users";
        long id = kakai.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getId() {
        String sql = "select max(id) from users";
        long id = kakai.getLong(sql, new Object[]{});
        return id;
    }

    public long getCount() {
        String sql = "select count(*) from users";
        Long count = kakai.getLong(sql, new Object[] { });
        return count;
    }

    public User get(long id) {
        String sql = "select * from users where id = [+]";
        User user = (User) kakai.get(sql, new Object[] { id }, User.class);

        if(user == null) user = new User();
        return user;
    }

    public User getPhone(String phone) {
        String sql = "select * from users where phone = '[+]'";
        User user = (User) kakai.get(sql, new Object[] { phone }, User.class);
        return user;
    }

    public User getEmail(String email) {
        String sql = "select * from users where phone = '[+]'";
        User user = (User) kakai.get(sql, new Object[] { email }, User.class);
        return user;
    }

    public User getUserCode(String id) {
        String sql = "select * from users where code = '[+]'";
        User user = (User) kakai.get(sql, new Object[] { id }, User.class);
        return user;
    }

    public List<User> getList() {
        String sql = "select * from users";
        List<User> users = (ArrayList) kakai.getList(sql, new Object[]{}, User.class);
        return users;
    }

    public boolean save(User user) {
        String sql = "insert into users (code, name, phone, password, date_created) values ('[+]','[+]','[+]','[+]',[+])";
        kakai.save(sql, new Object[]{
                user.getCode(),
                user.getName(),
                user.getPhone(),
                user.getPassword(),
                user.getTimeCreated()
        });
        return true;
    }

    public boolean update(User user) {
        String sql = "update users set code = '[+]', name = '[+]', phone = '[+]', email = '[+]', password = '[+]' where id = [+]";
        kakai.update(sql, new Object[]{
                user.getCode(),
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                user.getPassword(),
                user.getId()
        });
        return true;
    }

    public boolean updatePassword(User user) {
        String sql = "update users set password = '[+]' where id = [+]";
        kakai.update(sql, new Object[]{
                user.getPassword(),
                user.getId()
        });
        return true;
    }

    public User getReset(String username, String uuid){
        String sql = "select * from users where username = '[+]' and uuid = '[+]'";
        User user = (User) kakai.get(sql, new Object[] { username, uuid }, User.class);
        return user;
    }

    public boolean delete(long id) {
        String sql = "delete from users where id = [+]";
        kakai.update(sql, new Object[] { id });
        return true;
    }

    public String getUserPassword(String phone) {
        User user = getPhone(phone);
        return user.getPassword();
    }

    public boolean savePermission(long userId, String permission){
        String sql = "insert into user_permissions (user_id, permission) values ([+], '[+]')";
        kakai.save(sql, new Object[]{ userId,  permission });
        return true;
    }

    public Set<String> getUserPermissions(long id) {
        String sql = "select permission from user_permissions where user_id = [+]";
        List<Permission> permissionsList = (ArrayList) kakai.getList(sql, new Object[]{ id }, Permission.class);
        Set<String> permissions = new HashSet<>();
        for(Permission permission: permissionsList){
            permissions.add(permission.getPermission());
        }
        return permissions;
    }

    public List<UserFollow> getFollows(Long id) {
        String sql = "select * from user_follows where user_id = [+]";
        List<UserFollow> userFollows = (ArrayList) kakai.getList(sql, new Object[]{ id }, UserFollow.class);
        return userFollows;
    }

    public Boolean saveFollow(UserFollow userFollow){
        String sql = "insert into user_follows (user_id, following_id) values ([+],[+])";
        kakai.save(sql, new Object[] {
                userFollow.getUserId(),
                userFollow.getFollowingId()
        });
        return true;
    }

}