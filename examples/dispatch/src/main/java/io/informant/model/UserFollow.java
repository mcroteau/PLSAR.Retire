package io.informant.model;

public class UserFollow {

    public UserFollow(){}

    public UserFollow(User user, User following){
        this.userId = user.getId();
        this.followingId = following.getId();
    }

    Long userId;
    Long followingId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }
}
