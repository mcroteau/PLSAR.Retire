package io.model;

import java.math.BigDecimal;

public class OptionValue {
    Long id;
    Long itemOptionId;
    String value;
    BigDecimal price;
    BigDecimal quantity;

    ItemOption itemOption;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemOptionId() {
        return itemOptionId;
    }

    public void setItemOptionId(Long itemOptionId) {
        this.itemOptionId = itemOptionId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public ItemOption getItemOption() {
        return itemOption;
    }

    public void setItemOption(ItemOption itemOption) {
        this.itemOption = itemOption;
    }
}
