package giga.model;

public class GroupCategory {

    public GroupCategory(){}

    public GroupCategory(Long groupId, Long categoryId, Long businessId){
        this.groupId = groupId;
        this.categoryId = categoryId;
        this.businessId = businessId;
    }

    Long groupId;
    Long categoryId;
    Long businessId;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }
}
