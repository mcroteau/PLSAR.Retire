package io.informant.model;

public class UserFollow {

    public UserFollow(){}

    public UserFollow(User user, User following){
        this.userId = user.getId();
        this.followingId = following.getId();
    }

    public UserFollow(Long userId, Long followingId) {
        this.userId = userId;
        this.followingId = followingId;
    }

    Long userId;
    Long followingId;
    Long id;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
