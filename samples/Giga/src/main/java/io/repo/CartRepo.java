package io.repo;

import io.model.*;
import qio.Qio;
import qio.annotate.DataStore;
import qio.annotate.Inject;

import java.util.ArrayList;
import java.util.List;

@DataStore
public class CartRepo {

    @Inject
    Qio qio;

    public Cart getSaved() {
        String idSql = "select max(id) from carts";
        Long id = qio.getLong(idSql, new Object[]{});
        return get(id);
    }

    public CartItem getSavedItem() {
        String idSql = "select max(id) from cart_items";
        long id = qio.getLong(idSql, new Object[]{});
        return getItem(id);
    }

    public long getCount() {
        String sql = "select count(*) from carts";
        Long count = (Long) qio.getLong(sql, new Object[] { });
        return count;
    }

    public Cart get(long id){
        String sql = "select * from carts where id = [+]";
        Cart cart = (Cart) qio.get(sql, new Object[] { id }, Cart.class);
        return cart;
    }
    
    public Cart get(String sessionId){
        String sql = "select * from carts where session_id = '[+]'";
        Cart cart = (Cart) qio.get(sql, new Object[] { sessionId }, Cart.class);
        return cart;
    }

    public Cart getActive(Long id, Long businessId) {
        String sql = "select * from carts where user_id = [+] and business_id = [+] and active = true";
        Cart cart = (Cart) qio.get(sql, new Object[] { id, businessId }, Cart.class);
        return cart;
    }

    public Cart getActive(String sessionId, Long businessId) {
        String sql = "select * from carts where session_id = '[+]' and business_id = [+] and active = true";
        Cart cart = (Cart) qio.get(sql, new Object[] { sessionId, businessId}, Cart.class);
        return cart;
    }


    public List<Cart> getList(){
        String sql = "select * from carts order by id desc";
        List<Cart> carts = (ArrayList) qio.getList(sql, new Object[]{}, Cart.class);
        return carts;
    }

    public List<Cart> getList(long id){
        String sql = "select * from carts where business_id = [+] order by id desc";
        List<Cart> carts = (ArrayList) qio.getList(sql, new Object[]{ id }, Cart.class);
        return carts;
    }

    public Boolean save(Cart cart){
        String sql = "insert into carts (user_id, session_id, business_id, date_created, active) values ([+],'[+]',[+],[+], true)";
        qio.save(sql, new Object[] {
                cart.getUserId(),
                cart.getSessionId(),
                cart.getBusinessId(),
                cart.getDateCreated()
        });
        return true;
    }

    public boolean update(Cart cart) {
        String sql = "update carts set total = [+], subtotal = [+], shipping = [+], active = [+], sale = [+], user_id = [+], ship_name = '[+]', ship_email = '[+]', ship_phone = '[+]', ship_street = '[+]', ship_street_dos = '[+]', ship_city = '[+]', ship_state = '[+]', ship_zip = '[+]', ship_country = '[+]', valid_address = [+] where id = [+]";
        qio.update(sql, new Object[]{
                cart.getTotal(),
                cart.getSubtotal(),
                cart.getShipping(),
                cart.isActive(),
                cart.isSale(),
                cart.getUserId(),
                cart.getShipName(),
                cart.getShipEmail(),
                cart.getShipPhone(),
                cart.getShipStreet(),
                cart.getShipStreetDos(),
                cart.getShipCity(),
                cart.getShipState(),
                cart.getShipZip(),
                cart.getShipCountry(),
                cart.getValidAddress(),
                cart.getId()
        });

        return true;
    }

    public boolean delete(long id){
        String sql = "delete from carts where id = [+]";
        qio.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteCarts(long id){
        String sql = "delete from carts where business_id = [+]";
        qio.delete(sql, new Object[] { id });
        return true;
    }

    public Boolean saveItem(CartItem cartItem){
        String sql = "insert into cart_items (item_id, cart_id, business_id, quantity, price, item_total) values ([+],[+],[+],[+],[+],[+])";
        qio.save(sql, new Object[] {
                cartItem.getItemId(),
                cartItem.getCartId(),
                cartItem.getBusinessId(),
                cartItem.getQuantity(),
                cartItem.getPrice(),
                cartItem.getItemTotal()
        });
        return true;
    }

    public boolean updateItem(CartItem cartItem) {
        String sql = "update cart_items set item_total = [+] where id = [+]";
        qio.update(sql, new Object[] {
                cartItem.getItemTotal(),
                cartItem.getId()
        });
        return true;
    }

    public List<CartItem> getListItems(Long id) {
        String sql = "select * from cart_items where cart_id = [+] order by id desc";
        List<CartItem> cartItems = (ArrayList) qio.getList(sql, new Object[]{ id }, CartItem.class);
        return cartItems;
    }

    public boolean deleteItem(Long id) {
        String sql = "delete from cart_items where id = [+]";
        qio.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteOption(Long id) {
        String sql = "delete from cart_options where cart_item_id = [+]";
        qio.delete(sql, new Object[] { id });
        return true;
    }

    public CartItem getItem(Long id) {
        String sql = "select * from cart_items where id = [+]";
        CartItem cartItem = (CartItem) qio.get(sql, new Object[] { id }, CartItem.class);
        return cartItem;
    }

    public CartItem getItem(Long itemId, Long cartId) {
        String sql = "select * from cart_items where item_id = [+] and cart_id = [+]";
        CartItem cartItem = (CartItem) qio.get(sql, new Object[] { itemId, cartId }, CartItem.class);
        return cartItem;
    }

    public ShipmentRate getRate(Long cartId) {
        String sql = "select * from shipment_rates where cart_id = [+]";
        ShipmentRate shipmentRate = (ShipmentRate) qio.get(sql, new Object[] { cartId }, ShipmentRate.class);
        return shipmentRate;
    }

    public Boolean saveRate(ShipmentRate shipmentRate){
        String sql = "insert into shipment_rates (cart_id, rate, carrier, delivery_days) values ([+],[+],'[+]',[+])";
        qio.save(sql, new Object[] {
                shipmentRate.getCartId(),
                shipmentRate.getRate(),
                shipmentRate.getCarrier(),
                shipmentRate.getDeliveryDays()
        });
        return true;
    }

    public boolean deleteRate(Long id) {
        String sql = "delete from shipment_rates where cart_id = [+]";
        qio.delete(sql, new Object[] { id });
        return true;
    }


    public boolean saveOption(CartOption cartOption) {
        String sql = "insert into cart_options (cart_id, cart_item_id, item_option_id, option_value_id, price) values ([+],[+],[+],[+],[+])";
        qio.save(sql, new Object[] {
                cartOption.getCartId(),
                cartOption.getCartItemId(),
                cartOption.getItemOptionId(),
                cartOption.getOptionValueId(),
                cartOption.getPrice()
        });
        return true;
    }

    public List<CartOption> getOptions(Long id) {
        String sql = "select * from cart_options where cart_item_id = [+]";
        List<CartOption> cartOptions = (ArrayList) qio.getList(sql, new Object[]{ id }, CartOption.class);
        return cartOptions;
    }

    public Long getTotal(Long id) {
        String sql = "select count(*) from carts where business_id = [+]";
        Long count = (Long) qio.getLong(sql, new Object[] { id });
        return count;
    }

    public Long getSales(Long id) {
        String sql = "select count(*) from carts where business_id = [+] and sale = [+]";
        Long count = (Long) qio.getLong(sql, new Object[] { id });
        return count;
    }
}
