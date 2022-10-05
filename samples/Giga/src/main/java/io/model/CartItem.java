package io.model;

import java.math.BigDecimal;
import java.util.List;

public class CartItem {

    public CartItem(){}

    Long id;
    Long cartId;
    Long itemId;
    Long businessId;
    BigDecimal price;
    BigDecimal quantity;
    BigDecimal itemTotal;

    Item item;
    List<CartOption> cartOptions;

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

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public BigDecimal getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(BigDecimal itemTotal) {
        this.itemTotal = itemTotal;
    }

    public List<CartOption> getCartOptions() {
        return cartOptions;
    }

    public void setCartOptions(List<CartOption> cartOptions) {
        this.cartOptions = cartOptions;
    }
}
