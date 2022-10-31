package io.informant.repo;

import io.informant.model.UserFollow;
import net.plsar.Dao;
import net.plsar.annotations.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FollowsRepo {

    Dao dao;

    public FollowsRepo(Dao dao){
        this.dao = dao;
    }

    public UserFollow get(Long userId, Long followingId) {
        String sql = "select * from user_follows where user_id = [+] and following_id = [+]";
        UserFollow userFollow = (UserFollow) dao.get(sql, new Object[] { userId, followingId }, UserFollow.class);
        return userFollow;
    }

    public List<UserFollow> getList(Long id) {
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
