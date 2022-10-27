package giga.model;

public class GroupOption {
    Long id;
    Long ingestId;
    Long groupId;
    Long businessId;
    String title;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
