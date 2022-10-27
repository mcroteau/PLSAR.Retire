package giga.model;

import java.util.List;

public class ItemGroup {

    Long id;
    String name;
    Long ingestId;
    Long businessId;
    Long designId;
    String imageUri;
    String pricingHeader;
    String qHeader;

    List<GroupModel> groupModels;
    List<PricingOption> pricingOptions;
    List<GroupOption> groupOptions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getIngestId() {
        return ingestId;
    }

    public void setIngestId(Long ingestId) {
        this.ingestId = ingestId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getDesignId() {
        return designId;
    }

    public void setDesignId(Long designId) {
        this.designId = designId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getPricingHeader() {
        return pricingHeader;
    }

    public void setPricingHeader(String pricingHeader) {
        this.pricingHeader = pricingHeader;
    }

    public String getqHeader() {
        return qHeader;
    }

    public void setqHeader(String qHeader) {
        this.qHeader = qHeader;
    }

    public List<GroupModel> getGroupModels() {
        return groupModels;
    }

    public void setGroupModels(List<GroupModel> groupModels) {
        this.groupModels = groupModels;
    }

    public List<PricingOption> getPricingOptions() {
        return pricingOptions;
    }

    public void setPricingOptions(List<PricingOption> pricingOptions) {
        this.pricingOptions = pricingOptions;
    }

    public List<GroupOption> getGroupOptions() {
        return groupOptions;
    }

    public void setGroupOptions(List<GroupOption> groupOptions) {
        this.groupOptions = groupOptions;
    }
}
