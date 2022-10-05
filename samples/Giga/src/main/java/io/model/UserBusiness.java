package io.model;

public class UserBusiness {

    public UserBusiness(){}

    public UserBusiness(Long userId, Long businessId){
        this.userId = userId;
        this.businessId = businessId;
    }

    Long id;
    Long userId;
    Long businessId;

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

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }
}
