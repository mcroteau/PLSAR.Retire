package giga.router;

import giga.Giga;
import giga.model.*;
import giga.repo.BusinessRepo;
import giga.repo.CartRepo;
import giga.repo.ItemRepo;
import net.plsar.annotations.Component;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Inject;
import net.plsar.annotations.http.Get;
import net.plsar.model.Cache;
import net.plsar.model.HttpRequest;
import net.plsar.security.SecurityManager;

import java.math.BigDecimal;
import java.util.List;

@HttpRouter
public class CartRouter {

    @Inject
    ItemRepo itemRepo;

    @Inject
    CartRepo cartRepo;

    @Inject
    BusinessRepo businessRepo;


    @Get("/{{business}}/cart")
    public String view(Cache cache,
                       HttpRequest req,
                       SecurityManager security,
                       @Component String businessUri){
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        Cart cart = getCart(business, req, security);
        setData(cart, business, cache, req);

        String oops = req.getValue("oops");
        if(oops != null && oops.equals("true")){
            cache.set("message", "Something went wrong while processing your order.");
        }

        return "/pages/cart/index.jsp";
    }

    @Get("/{{business}}/checkout")
    public String viewCheckout(Cache cache,
                               HttpRequest req,
                               SecurityManager security,
                               @Component String businessUri){
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        Cart cart = getCart(business, req, security);
        setData(cart, business, cache, req);

        return "/pages/cart/checkout.jsp";
    }

    @Post("/{{business}}/cart/add/{{id}}")
    public String add(HttpRequest req,
                      Cache cache,
                      @Component String businessUri,
                      @Component Long id){
        System.out.println("\n\n////////////////////////////////");

        List<Business> businesses = businessRepo.getList();
        for(Business item: businesses){
            System.out.println("zzq, " + item.getUri());
        }

        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        Item activeItem = itemRepo.get(id);
        if(activeItem == null){
            return "[redirect]/" + businessUri + "/home";
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

        String[] optionIds = req.("optionId");
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

        setData(cart, business, cache, req);

        if(shipmentService.validateAddress(cart, business)){
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
            return "[redirect]/" + businessUri + "/checkout";
        }
        return "[redirect]/" + businessUri + "/cart";
    }

    @Post("/{{business}}/cart/minus/{{id}}")
    public String minus(Cache cache,
                      @Component String businessUri,
                      @Component Long id){
        System.out.println("minus");
        CartItem cartItem = cartRepo.getItem(id);
        cartRepo.deleteOption(id);
        cartRepo.deleteItem(id);
        Cart cart = cartRepo.get(cartItem.getCartId());
        cartRepo.update(cart);
        cache.set("message", "Removed item from cart.");
        return "[redirect]/" + businessUri + "/cart";
    }

    public Cart getCart(Business business, HttpRequest req, SecurityManager security){
        Cart cart;
        User user = null;
        String sessionId = req.getSession().getId();

        if(authService.isAuthenticated()){
            user = authService.getUser();
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

        if(authService.isAuthenticated()){
            cart.setShipName(user.getName());
            cart.setShipPhone(user.getPhone());
            cart.setShipEmail(user.getUsername());
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


    public void setData(Cart cart, Business business, Cache cache, HttpRequest req){
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

        cache.set("design", design);
        cache.set("request", req);
        cache.set("business", business);
        cache.set("cart", cart);
        cache.set("items", cartItems);
        cache.set("siteService", siteService);
    }

}
