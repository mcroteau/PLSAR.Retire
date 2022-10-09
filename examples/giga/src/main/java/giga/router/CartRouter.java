package giga.router;

import giga.model.*;
import giga.service.CartService;
import net.plsar.annotations.HttpRouter;

import java.math.BigDecimal;
import java.util.List;

@HttpRouter
public class CartRouter {

    @Inject
    CartService cartService;

    @Get("/{{business}}/cart")
    public String view(HttpRequest req,
                      Cache cache,
                      @RouteComponent String businessUri){
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        Cart cart = getCart(business, req);
        setData(cart, business, cache, req);

        String oops = req.getParameter("oops");
        if(oops != null && oops.equals("true")){
            cache.set("message", "Something went wrong while processing your order.");
        }

        return "/pages/cart/index.jsp";
    }

    @Get("/{{business}}/checkout")
    public String viewCheckout(HttpRequest req,
                               Cache cache,
                               @RouteComponent String businessUri){
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        Cart cart = getCart(business, req);
        setData(cart, business, cache, req);

        return "/pages/cart/checkout.jsp";
    }

    @Post("/{{business}}/cart/add/{{id}}")
    public String add(HttpRequest req,
                      Cache cache,
                      @RouteComponent String businessUri,
                      @RouteComponent Long id){
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
            quantity = new BigDecimal(req.getParameter("quantity"));
            if(quantity.compareTo(new BigDecimal(0)) == 0){
                quantity = new BigDecimal(1);
            }
        }catch(Exception ex){}

        Cart cart = getCart(business, req);
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

        String[] optionIds = req.getParameterValues("optionId");
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
                      @RouteComponent String businessUri,
                      @RouteComponent Long id){
        System.out.println("minus");
        CartItem cartItem = cartRepo.getItem(id);
        cartRepo.deleteOption(id);
        cartRepo.deleteItem(id);
        Cart cart = cartRepo.get(cartItem.getCartId());
        cartRepo.update(cart);
        cache.set("message", "Removed item from cart.");
        return "[redirect]/" + businessUri + "/cart";
    }


}
