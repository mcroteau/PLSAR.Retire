package io.model;

public class Category {

    public Category(){}

    Long id;
    Long businessId;
    String name;
    String uri;
    String header;
    Long designId;
    Boolean topLevel;
    Long categoryId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Long getDesignId() {
        return designId;
    }

    public void setDesignId(Long designId) {
        this.designId = designId;
    }

    public Boolean getTopLevel() {
        return topLevel;
    }

    public void setTopLevel(Boolean topLevel) {
        this.topLevel = topLevel;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public int hashCode(){
        int hash = (this.id + this.name).hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }

        if (!Category.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Category other = (Category) obj;

        if(this.getName() == null){
            return false;
        }

        if(other.getName() == null){
            return false;
        }

        if(this.getId().equals(other.getId()) &&
                this.getName().equals(other.getName())) {
            return true;
        }

        return false;
    }
}
