package io.model;

import java.math.BigDecimal;
import java.util.List;

public class Cart {

    Long id;
    Long userId;
    String sessionId;
    Long businessId;

    Long dateCreated;

    Boolean sale;
    Boolean active;

    BigDecimal subtotal;
    BigDecimal total;

    BigDecimal shipping;

    String shipName;
    String shipPhone;
    String shipEmail;
    String shipStreet;
    String shipStreetDos;
    String shipCity;
    String shipState;
    String shipZip;
    String shipCountry;

    Boolean validAddress;
    User user;

    List<CartItem> cartItems;

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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Boolean isSale() {
        return sale;
    }

    public void setSale(Boolean sale) {
        this.sale = sale;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getShipping() {
        return shipping;
    }

    public void setShipping(BigDecimal shipping) {
        this.shipping = shipping;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getShipPhone() {
        if(this.shipPhone != null &&
                !this.shipPhone.equals("")){
            return this.shipPhone;
        }
        return "";
    }

    public void setShipPhone(String shipPhone) {
        this.shipPhone = shipPhone;
    }

    public String getShipEmail() {
        return shipEmail;
    }

    public void setShipEmail(String shipEmail) {
        this.shipEmail = shipEmail;
    }

    public String getShipStreet() {
        return shipStreet;
    }

    public void setShipStreet(String shipStreet) {
        this.shipStreet = shipStreet;
    }

    public String getShipStreetDos() {
        return shipStreetDos;
    }

    public void setShipStreetDos(String shipStreetDos) {
        this.shipStreetDos = shipStreetDos;
    }

    public String getShipCity() {
        return shipCity;
    }

    public void setShipCity(String shipCity) {
        this.shipCity = shipCity;
    }

    public String getShipState() {
        return shipState;
    }

    public void setShipState(String shipState) {
        this.shipState = shipState;
    }

    public String getShipZip() {
        return shipZip;
    }

    public void setShipZip(String shipZip) {
        this.shipZip = shipZip;
    }

    public String getShipCountry() {
        return shipCountry;
    }

    public void setShipCountry(String shipCountry) {
        this.shipCountry = shipCountry;
    }

    public Boolean getValidAddress() {
        return validAddress;
    }

    public void setValidAddress(Boolean validAddress) {
        this.validAddress = validAddress;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public String get(String value){
        if(value != null &&
                !value.equals("null")){
            return value;
        }
        return "";
    }

}
