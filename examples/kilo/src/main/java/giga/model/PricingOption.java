package giga.model;

public class PricingOption {
    Long id;
    Long ingestId;
    Long groupId;
    Long businessId;
    String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIngestId() {
        return ingestId;
    }

    public void setIngestId(Long ingestId) {
        this.ingestId = ingestId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
