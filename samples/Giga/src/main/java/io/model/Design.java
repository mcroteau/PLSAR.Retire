package io.model;

public class Design {
    Long id;
    Long businessId;
    String name;
    String design;
    String css;
    String javascript;

    Boolean baseDesign;

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

    public String getDesign() {
        return design;
    }

    public void setDesign(String design) {
        this.design = design;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getJavascript() {
        return javascript;
    }

    public void setJavascript(String javascript) {
        this.javascript = javascript;
    }

    public Boolean getBaseDesign() {
        return baseDesign;
    }

    public void setBaseDesign(Boolean baseDesign) {
        this.baseDesign = baseDesign;
    }
}
