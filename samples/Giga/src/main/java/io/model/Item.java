package io.model;

import java.math.BigDecimal;
import java.util.List;

public class Item {
    Long id;
    Long designId;
    Long businessId;

    String name;
    String description;
    String imageUri;
    BigDecimal price;
    BigDecimal affiliatePrice;
    BigDecimal quantity;
    BigDecimal cost;

    BigDecimal weight;
    Boolean active;

    List<Category> categories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDesignId() {
        return designId;
    }

    public void setDesignId(Long designId) {
        this.designId = designId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getName() {
        if(this.name != null &&
                !this.name.equals("")){
            return this.name;
        }
        return "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        if(this.description != null &&
                !this.description.equals("")){
           return this.description;
        }
        return "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        if(this.imageUri != null &&
                !this.imageUri.equals("")){
            return this.imageUri;
        }
        return "";
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAffiliatePrice() {
        return affiliatePrice;
    }

    public void setAffiliatePrice(BigDecimal affiliatePrice) {
        this.affiliatePrice = affiliatePrice;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        active = active;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String get(String value){
        if(value != null &&
                !value.equals("null")){
            return value;
        }
        return "";
    }
}
