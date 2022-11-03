package io.informant.model;

public class Reference {
    public Reference(Long userId, Long accessUserId) {
        this.userId = userId;
        this.accessUserId = accessUserId;
    }

    public Reference(){}

    Long id;
    Long userId;
    Long accessUserId;

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

    public Long getAccessUserId() {
        return accessUserId;
    }

    public void setAccessUserId(Long accessUserId) {
        this.accessUserId = accessUserId;
    }
}
