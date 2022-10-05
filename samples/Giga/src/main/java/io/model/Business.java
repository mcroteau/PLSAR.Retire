package io.model;

import java.math.BigDecimal;

public class Business {
    Long id;
    Long userId;
    String name;
    String phone;
    String email;
    String uri;
    Long primaryId;

    String street;
    String streetDos;
    String city;
    String state;
    String zip;
    String country;

    Boolean flatShipping;
    BigDecimal shipping;
    Boolean initial;
    Boolean activationComplete;

    String stripeId;

    BigDecimal baseCommission;
    String owner;
    Boolean affiliate;

    String password;

    Business primary;
    BigDecimal salesTotal;

    Boolean live;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Long getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(Long primaryId) {
        this.primaryId = primaryId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetDos() {
        return streetDos;
    }

    public void setStreetDos(String streetDos) {
        this.streetDos = streetDos;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getFlatShipping() {
        return flatShipping;
    }

    public void setFlatShipping(Boolean flatShipping) {
        this.flatShipping = flatShipping;
    }

    public BigDecimal getShipping() {
        return shipping;
    }

    public void setShipping(BigDecimal shipping) {
        this.shipping = shipping;
    }

    public Boolean getInitial() {
        return initial;
    }

    public void setInitial(Boolean initial) {
        this.initial = initial;
    }

    public Boolean getActivationComplete() {
        return activationComplete;
    }

    public void setActivationComplete(Boolean activationComplete) {
        this.activationComplete = activationComplete;
    }

    public String getStripeId() {
        return stripeId;
    }

    public void setStripeId(String stripeId) {
        this.stripeId = stripeId;
    }

    public BigDecimal getBaseCommission() {
        return baseCommission;
    }

    public void setBaseCommission(BigDecimal baseCommission) {
        this.baseCommission = baseCommission;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Boolean getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(Boolean affiliate) {
        this.affiliate = affiliate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Business getPrimary() {
        return primary;
    }

    public void setPrimary(Business primary) {
        this.primary = primary;
    }

    public BigDecimal getSalesTotal() {
        return salesTotal;
    }

    public void setSalesTotal(BigDecimal salesTotal) {
        this.salesTotal = salesTotal;
    }

    public Boolean getLive() {
        return live;
    }

    public void setLive(Boolean live) {
        this.live = live;
    }

    public String get(String value){
        if(value != null &&
                !value.equals("null")){
            return value;
        }
        return "";
    }

}
