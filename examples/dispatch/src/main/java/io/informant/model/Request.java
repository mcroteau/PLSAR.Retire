package io.informant.model;

public class Request {
    public Request(Long userId, Long requestUserId, Boolean approved) {
        this.userId = userId;
        this.requestUserId = requestUserId;
        this.approved = approved;
    }

    Long id;
    Long userId;
    Long requestUserId;
    Boolean approved;

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

    public Long getRequestUserId() {
        return requestUserId;
    }

    public void setRequestUserId(Long requestUserId) {
        this.requestUserId = requestUserId;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
}
