package io.model;

import io.Giga;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Sale {

    public Sale(){}

    Long id;
    Long userId;
    Long cartId;
    Long affiliateId;
    Long primaryId;
    Long salesDate;
    BigDecimal amount;

    Cart cart;

    Long affiliateAmount;
    Long primaryAmount;

    String stripeApplicationCustomerId;
    String stripePrimaryCustomerId;

    String stripeApplicationChargeId;
    String stripePrimaryChargeId;

    String prettyDate;

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

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getAffiliateId() {
        return affiliateId;
    }

    public void setAffiliateId(Long affiliateId) {
        this.affiliateId = affiliateId;
    }

    public Long getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(Long primaryId) {
        this.primaryId = primaryId;
    }

    public Long getSalesDate() {
        return salesDate;
    }

    public void setSalesDate(Long salesDate) {
        this.salesDate = salesDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Long getAffiliateAmount() {
        return affiliateAmount;
    }

    public void setAffiliateAmount(Long affiliateAmount) {
        this.affiliateAmount = affiliateAmount;
    }

    public Long getPrimaryAmount() {
        return primaryAmount;
    }

    public void setPrimaryAmount(Long primaryAmount) {
        this.primaryAmount = primaryAmount;
    }

    public String getStripeApplicationCustomerId() {
        return stripeApplicationCustomerId;
    }

    public void setStripeApplicationCustomerId(String stripeApplicationCustomerId) {
        this.stripeApplicationCustomerId = stripeApplicationCustomerId;
    }

    public String getStripePrimaryCustomerId() {
        return stripePrimaryCustomerId;
    }

    public void setStripePrimaryCustomerId(String stripePrimaryCustomerId) {
        this.stripePrimaryCustomerId = stripePrimaryCustomerId;
    }

    public String getStripeApplicationChargeId() {
        return stripeApplicationChargeId;
    }

    public void setStripeApplicationChargeId(String stripeApplicationChargeId) {
        this.stripeApplicationChargeId = stripeApplicationChargeId;
    }

    public String getStripePrimaryChargeId() {
        return stripePrimaryChargeId;
    }

    public void setStripePrimaryChargeId(String stripePrimaryChargeId) {
        this.stripePrimaryChargeId = stripePrimaryChargeId;
    }

    public String getPrettyDate() {
        return prettyDate;
    }

    public void setPrettyDate(String prettyDate) {
        this.prettyDate = prettyDate;
    }

    public String convert(Long v){
        BigDecimal pre = BigDecimal.valueOf(v);
        pre = pre.movePointLeft(2);
        pre = pre.setScale(2, RoundingMode.HALF_DOWN);
        String post = pre.toPlainString();
        String[] bits = post.split("\\.");
        String decimal = bits[1];
        BigDecimal fat = new BigDecimal(bits[0]);
        DecimalFormat dc = new DecimalFormat("###,###");
        String value = dc.format(fat);
        return value.concat(".<span class=\"decimal\">" + decimal + "</span>");
    }
}
