package io.model;

import java.util.List;

public class DataImport {
    Long id;
    Long businessId;
    Long userId;
    Long dateImport;
    String type;
    List<MediaImport> mediaImports;


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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDateImport() {
        return dateImport;
    }

    public void setDateImport(Long dateImport) {
        this.dateImport = dateImport;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<MediaImport> getMediaImports() {
        return mediaImports;
    }

    public void setMediaImports(List<MediaImport> mediaImports) {
        this.mediaImports = mediaImports;
    }
}
