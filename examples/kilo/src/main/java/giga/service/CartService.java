package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import net.plsar.annotations.Bind;
import net.plsar.annotations.Service;
import net.plsar.model.Cache;
import net.plsar.model.NetworkRequest;
import net.plsar.model.RequestComponent;
import net.plsar.security.SecurityManager;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    @Bind
    ItemRepo itemRepo;

    @Bind
    UserRepo userRepo;

    @Bind
    CartRepo cartRepo;

    @Bind
    CategoryRepo categoryRepo;

    @Bind
    DesignRepo designRepo;

    @Bind
    BusinessRepo businessRepo;


    public String view(String businessUri, Cache cache, NetworkRequest req, SecurityManager security) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "redirect:/home";
        }

        Cart cart = getCart(business, req, security);
        setData(cart, business, cache, designRepo, itemRepo, cartRepo, req, security);

        String oops = req.getValue("oops");
        if(oops != null && oops.equals("true")){
            cache.set("message", "Something went wrong while processing your order.");
        }

        return "/pages/cart/index.jsp";
    }

    public String viewCheckout(String businessUri, Cache cache, NetworkRequest req, SecurityManager security) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "redirect:/home";
        }

        Cart cart = getCart(business, req, security);
        setData(cart, business, cache, designRepo, itemRepo, cartRepo, req, security);

        return "/pages/cart/checkout.jsp";
    }

    public String add(Long id, String businessUri, Cache cache, NetworkRequest req, SecurityManager security) {

        List<Business> businesses = businessRepo.getList();
        for(Business item: businesses){
            System.out.println("zzq, " + item.getUri());
        }

        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "redirect:/home";
        }

        Item activeItem = itemRepo.get(id);
        if(activeItem == null){
            return "redirect:/" + businessUri + "/home";
        }

        BigDecimal quantity = new BigDecimal(1);
        try {
            quantity = new BigDecimal(req.getValue("quantity"));
            if(quantity.compareTo(new BigDecimal(0)) == 0){
                quantity = new BigDecimal(1);
            }
        }catch(Exception ex){}

        Cart cart = getCart(business, req, security);
        System.out.println("z " + cart);

        CartItem cartItem = new CartItem();
        cartItem.setBusinessId(business.getId());
        cartItem.setCartId(cart.getId());
        cartItem.setItemId(activeItem.getId());
        cartItem.setQuantity(quantity);
        cartItem.setPrice(activeItem.getPrice());
        cartRepo.saveItem(cartItem);

        CartItem savedCartItem = cartRepo.getSavedItem();
        System.out.println("z item: " + savedCartItem.getId());

        RequestComponent requestComponent = req.getRequestComponent("optionId");
        List<String> optionIds = requestComponent.getValues();
        if(optionIds != null) {
            for (String optionId : optionIds) {
                OptionValue optionValue = itemRepo.getValue(Long.valueOf(optionId));
                CartOption cartOption = new CartOption();
                cartOption.setCartId(cart.getId());
                cartOption.setCartItemId(savedCartItem.getId());
                cartOption.setItemOptionId(optionValue.getItemOptionId());
                cartOption.setOptionValueId(optionValue.getId());
                cartOption.setPrice(optionValue.getPrice());
                cartOption.setQuantity(quantity);
                cartRepo.saveOption(cartOption);

                BigDecimal itemTotal = cartOption.getPrice().multiply(quantity);
                savedCartItem.setItemTotal(itemTotal);
                cartRepo.updateItem(savedCartItem);
            }
        }

        activeItem.setQuantity(activeItem.getQuantity().subtract(quantity));
        itemRepo.update(activeItem);

        cartRepo.deleteRate(cart.getId());

        setData(cart, business, cache, designRepo, itemRepo, cartRepo, req, security);

        ShipmentService shipmentService = new ShipmentService();
        if(shipmentService.validateAddress(cart, business, req)){
            cart.setValidAddress(true);
        }else{
            cart.setValidAddress(false);
        }

        cartRepo.update(cart);

        System.out.println("added");
        System.out.println("////////////////////////////////\n\n");

        cache.set("message", "Added " + activeItem.getName() + " to Kart");

        if(cart.getValidAddress() != null &&
                cart.getValidAddress()){
            return "redirect:/" + businessUri + "/checkout";
        }
        return "redirect:/" + businessUri + "/cart";
    }

    public Cart getCart(Business business, NetworkRequest req, SecurityManager security){
        Cart cart;
        User user = null;
        String sessionId = req.getSession(true).getGuid();

        if(security.isAuthenticated(req)){
            String credential = security.getUser(req);
            user  = userRepo.getPhone(credential);
            if(user == null){
                user = userRepo.getEmail(credential);
            }
            cart = cartRepo.getActive(user.getId(), business.getId());
        }else{
            cart = cartRepo.getActive(sessionId, business.getId());
        }

        if(cart == null) {
            cart = new Cart();

            cart.setDateCreated(Giga.getDate());
            cart.setBusinessId(business.getId());

            if (user != null) {
                cart.setUserId(user.getId());
            } else {
                System.out.println("session " + sessionId);
                cart.setSessionId(sessionId);
            }

            cart.setActive(true);
            cartRepo.save(cart);
            cart = cartRepo.getSaved();

            cart.setTotal(new BigDecimal(0));
            cart.setSubtotal(new BigDecimal(0));
            cart.setShipping(new BigDecimal(0));
        }

        if(security.isAuthenticated(req)){
            cart.setShipName(user.getName());
            cart.setShipPhone(user.getPhone());
            cart.setShipEmail(user.getEmail());
            cart.setShipStreet(user.getShipStreet());
            cart.setShipStreetDos(user.getShipStreetDos());
            cart.setShipCity(user.getShipCity());
            cart.setShipState(user.getShipState());
            cart.setShipCountry(user.getShipCountry());
            cart.setShipZip(user.getShipZip());

            //->
            // setting to valid address regardless for now.
            // mail service integration is easy, this is already
            // setup with one.
            cart.setValidAddress(true);

//            if(shipmentService.validateAddress(user, business) &&
//                    shipmentService.validateBusinessAddress(business)){
//                ///-> mail integration done here.
//            }

            cartRepo.update(cart);
        }

        return cart;
    }


    public void setData(Cart cart, Business business, Cache cache, DesignRepo designRepo, ItemRepo itemRepo, CartRepo cartRepo, NetworkRequest req, SecurityManager security){
        BigDecimal subtotal = new BigDecimal(0);
        List<CartItem> cartItems = cartRepo.getListItems(cart.getId());
        System.out.println("ci " + cartItems.size());
        System.out.println("z");
        for(CartItem cartItem : cartItems){
            Item item = itemRepo.get(cartItem.getItemId());
            cartItem.setItem(item);

            BigDecimal itemTotal = cartItem.getPrice().multiply(cartItem.getQuantity());
            List<CartOption> cartOptions = cartRepo.getOptions(cartItem.getId());
            for(CartOption cartOption : cartOptions){
                ItemOption itemOption = itemRepo.getOption(cartOption.getItemOptionId());
                OptionValue optionValue = itemRepo.getValue(cartOption.getOptionValueId());
                cartOption.setItemOption(itemOption);
                cartOption.setOptionValue(optionValue);
                if(cartOption.getPrice() != null){
                    itemTotal = itemTotal.add(cartOption.getPrice().multiply(cartItem.getQuantity()));
                }
            }
            cartItem.setItemTotal(itemTotal);

            subtotal = subtotal.add(itemTotal);
            cart.setSubtotal(subtotal);

            cartItem.setCartOptions(cartOptions);

        }

        cart.setCartItems(cartItems);
        if(business.getFlatShipping()){
            BigDecimal total = cart.getSubtotal();
            if(business.getShipping() != null){
                total = total.add(business.getShipping());
            }
            cart.setShipping(business.getShipping());
            cart.setTotal(total);
            cartRepo.update(cart);
        }

        if(!business.getFlatShipping()){
            ShipmentRate rate = cartRepo.getRate(cart.getId());
            if(rate != null) {
                BigDecimal total = rate.getRate().add(cart.getSubtotal());
                cart.setShipping(rate.getRate());
                cart.setTotal(total);
                cart.setValidAddress(true);
                cartRepo.update(cart);
                cache.set("rate", rate);
            }
        }

        System.out.println("kilo : subtotal " + cart);
        Design design = designRepo.getBase(business.getId());
        System.out.println("business: " + business);
        System.out.println("design: " + design);

        SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);

        cache.set("design", design);
        cache.set("request", req);
        cache.set("business", business);
        cache.set("cart", cart);
        cache.set("items", cartItems);
        cache.set("siteService", siteService);
    }

    public String minus(Long id, String businessUri, Cache cache) {
        CartItem cartItem = cartRepo.getItem(id);
        cartRepo.deleteOption(id);
        cartRepo.deleteItem(id);
        Cart cart = cartRepo.get(cartItem.getCartId());
        cartRepo.update(cart);
        cache.set("message", "Removed item from cart.");
        return "redirect:/" + businessUri + "/cart";
    }

}
