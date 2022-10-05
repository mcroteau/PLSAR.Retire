package io.model;

import java.math.BigDecimal;

public class ShipmentRate {
    Long cartId;
    String carrier;
    BigDecimal rate;
    Long deliveryDays;

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Long getDeliveryDays() {
        return deliveryDays;
    }

    public void setDeliveryDays(Long deliveryDays) {
        this.deliveryDays = deliveryDays;
    }
}
