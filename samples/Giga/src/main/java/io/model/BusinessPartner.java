package io.model;

public class BusinessPartner {

    public BusinessPartner(){}

    public BusinessPartner(Long businessId){
        this.businessId = businessId;
    }

    Long id;
    Long businessId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

}
