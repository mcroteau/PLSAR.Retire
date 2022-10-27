package giga.model;

public class ItemCategory {

    public ItemCategory(){}

    public ItemCategory(Long itemId, Long categoryId, Long businessId){
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.businessId = businessId;
    }

    Long itemId;
    Long categoryId;
    Long businessId;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
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
