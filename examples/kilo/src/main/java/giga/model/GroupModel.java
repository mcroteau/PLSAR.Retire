package giga.model;

import java.math.BigDecimal;
import java.util.List;

public class GroupModel {
    Long id;
    Long ingestId;
    Long groupId;
    Long businessId;
    String modelNumber;
    BigDecimal weight;
    BigDecimal quantity;
    String perBoxQuantity;

    List<GroupOptionValue> groupValues;
    List<PricingValue> pricingValues;

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

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getPerBoxQuantity() {
        return perBoxQuantity;
    }

    public void setPerBoxQuantity(String perBoxQuantity) {
        this.perBoxQuantity = perBoxQuantity;
    }

    public List<GroupOptionValue> getGroupValues() {
        return groupValues;
    }

    public void setGroupValues(List<GroupOptionValue> groupValues) {
        this.groupValues = groupValues;
    }

    public List<PricingValue> getPricingValues() {
        return pricingValues;
    }

    public void setPricingValues(List<PricingValue> pricingValues) {
        this.pricingValues = pricingValues;
    }
}
