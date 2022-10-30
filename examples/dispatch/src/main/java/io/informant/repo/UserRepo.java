package io.informant.repo;

import net.plsar.Dao;
import net.plsar.annotations.Repository;
import io.informant.model.Permission;
import io.informant.model.UserFollow;
import io.informant.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserRepo {

    Dao dao;

    public UserRepo(Dao dao) {
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
        return user;
    }

    public User getPhone(String phone) {
        String sql = "select * from users where phone = '[+]'";
        User user = (User) dao.get(sql, new Object[] { phone }, User.class);
        return user;
    }

    public User getEmail(String email) {
        String sql = "select * from users where phone = '[+]'";
        User user = (User) dao.get(sql, new Object[] { email }, User.class);
        return user;
    }

    public User getUserCode(String id) {
        String sql = "select * from users where code = '[+]'";
        User user = (User) dao.get(sql, new Object[] { id }, User.class);
        return user;
    }

    public List<User> getList() {
        String sql = "select * from users";
        List<User> users = (ArrayList) dao.getList(sql, new Object[]{}, User.class);
        return users;
    }

    public boolean save(User user) {
        String sql = "insert into users (code, name, phone, password, date_created) values ('[+]','[+]','[+]','[+]',[+])";
        dao.save(sql, new Object[]{
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
        dao.update(sql, new Object[]{
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

    public void savePermission(long userId, String permission){
        String sql = "insert into user_permissions (user_id, permission) values ([+], '[+]')";
        dao.save(sql, new Object[]{ userId,  permission });
    }

    public Permission getPermission(Long userId, String permission) {
        String sql = "select * from user_permissions where user_id = [+] and permission = '[+]'";
        Permission userPermission = (Permission) dao.get(sql, new Object[] { userId, permission }, Permission.class);
        return userPermission;
    }

    public void deletePermission(Long id) {
        String sql = "delete from user_permissions where id = [+]";
        dao.update(sql, new Object[] { id });
    }

    public UserFollow getFollow(Long userId, Long followingId) {
        String sql = "select * from user_follows where user_id = [+] and following_id = [+]";
        UserFollow userFollow = (UserFollow) dao.get(sql, new Object[] { userId, followingId }, UserFollow.class);
        return userFollow;
    }

    public List<UserFollow> getFollows(Long id) {
        String sql = "select * from user_follows where user_id = [+]";
        List<UserFollow> userFollows = (ArrayList) dao.getList(sql, new Object[]{ id }, UserFollow.class);
        return userFollows;
    }

    public void follow(UserFollow userFollow){
        String sql = "insert into user_follows (user_id, following_id) values ([+],[+])";
        dao.save(sql, new Object[] {
                userFollow.getUserId(),
                userFollow.getFollowingId()
        });
    }

    public void unfollow(UserFollow userFollow) {
        String sql = "delete from user_follows where user_id = [+] and following_id = [+]";
        dao.update(sql, new Object[] { userFollow.getUserId(), userFollow.getFollowingId() });
    }

}