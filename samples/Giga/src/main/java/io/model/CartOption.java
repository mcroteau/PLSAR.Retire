package io.model;

import java.math.BigDecimal;

public class CartOption {
    Long id;
    Long cartId;
    Long cartItemId;
    Long itemOptionId;
    Long optionValueId;
    BigDecimal quantity;
    BigDecimal price;

    ItemOption itemOption;
    OptionValue optionValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Long getItemOptionId() {
        return itemOptionId;
    }

    public void setItemOptionId(Long itemOptionId) {
        this.itemOptionId = itemOptionId;
    }

    public Long getOptionValueId() {
        return optionValueId;
    }

    public void setOptionValueId(Long optionValueId) {
        this.optionValueId = optionValueId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ItemOption getItemOption() {
        return itemOption;
    }

    public void setItemOption(ItemOption itemOption) {
        this.itemOption = itemOption;
    }

    public OptionValue getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(OptionValue optionValue) {
        this.optionValue = optionValue;
    }
}
