package giga.model;

import java.util.List;

public class Ingest {
    Long id;
    Long businessId;
    Long dateIngest;
    List<ItemGroup> itemGroups;

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

    public Long getDateIngest() {
        return dateIngest;
    }

    public void setDateIngest(Long dateIngest) {
        this.dateIngest = dateIngest;
    }

    public List<ItemGroup> getItemGroups() {
        return itemGroups;
    }

    public void setItemGroups(List<ItemGroup> itemGroups) {
        this.itemGroups = itemGroups;
    }
}
