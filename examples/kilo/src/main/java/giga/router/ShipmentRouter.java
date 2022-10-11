package giga.router;

import com.easypost.EasyPost;
import com.easypost.exception.EasyPostException;
import com.easypost.model.Address;
import com.easypost.model.Rate;
import com.easypost.model.Shipment;
import giga.Giga;
import giga.model.*;
import giga.repo.*;
import giga.service.CartService;
import giga.service.SiteService;
import net.plsar.RouteAttributes;
import net.plsar.annotations.Component;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Inject;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;
import net.plsar.model.HttpRequest;
import net.plsar.security.SecurityManager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@HttpRouter
public class ShipmentRouter {

    @Inject
    CartRepo cartRepo;

    @Inject
    UserRepo userRepo;

    @Inject
    ItemRepo itemRepo;

    @Inject
    CategoryRepo categoryRepo;

    @Inject
    DesignRepo designRepo;

    @Inject
    BusinessRepo businessRepo;


    @Get("{{business}}/shipment")
    public String begin(Cache cache,
                        HttpRequest req,
                        SecurityManager security,
                        @Component String businessUri){
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        CartService cartService = new CartService();
        Cart cart = cartService.getCart(business, cartRepo, req);
        List<CartItem> cartItems = cartRepo.getListItems(cart.getId());
        if(cartItems.size() == 0) {
            return "[redirect]/" + businessUri + "/cart";
        }

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        cartService.setData(cart, business, cache, itemRepo, designRepo, cartRepo, req, siteService);

        return "/pages/shipment/new.jsp";
    }

    @Get("{{business}}/shipment/rates")
    public String redirect(@Component String businessUri){
        return "[redirect]/" + businessUri + "/shipment";
    }

    @Post("{{business}}/shipment/rates")
    public String getRates(Cache cache,
                           HttpRequest req,
                           SecurityManager security,
                           @Component String businessUri){
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        CartService cartService = new CartService();
        Cart cart = cartService.getCart(business, cartRepo, req);//quarkus just copied. copyright infringement

        User shipUser = (User) req.inflect(User.class);

        String phone = "";
        if(shipUser.getPhone() != null) phone = Giga.getPhone(shipUser.getPhone());

        User storedPhone = userRepo.getPhone(phone);
        if(storedPhone == null){
            User storedUsername = userRepo.get(shipUser.getUsername());
            if(storedUsername == null){
                shipUser.setDateJoined(Giga.getDate());
                shipUser.setPassword(security.hash("gigabeat"));
                userRepo.save(shipUser);
                storedPhone = userRepo.getSaved();
            }
        }

        cart.setShipName(shipUser.getName());
        cart.setShipPhone(shipUser.getPhone());
        cart.setShipEmail(shipUser.getUsername());
        cart.setShipStreet(shipUser.getShipStreet());
        cart.setShipStreetDos(shipUser.getShipStreetDos());
        cart.setShipCity(shipUser.getShipCity());
        cart.setShipState(shipUser.getShipState());
        cart.setShipZip(shipUser.getShipZip());
        cart.setShipCountry(shipUser.getShipCountry());
        cart.setUserId(storedPhone.getId());

        cartRepo.update(cart);

        cart.setUser(shipUser);

        BigDecimal weight = new BigDecimal(0);
        List<CartItem> cartItems = cartRepo.getListItems(cart.getId());
        for(CartItem cartItem : cartItems){
            Item item = itemRepo.get(cartItem.getItemId());
            if(item.getWeight() != null) {
                weight = weight.add(item.getWeight().multiply(cartItem.getQuantity()));
            }
        }

        cache.set("weight", weight);

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        cartService.setData(cart, business, cache, itemRepo, designRepo, cartRepo, req, siteService);

        RouteAttributes routeAttributes = req.getRouteAttributes();
        EasyPost.apiKey = (String) routeAttributes.get("easypost.key");

        Map<String, Object> toAddress = new HashMap<>();
        toAddress.put("name", shipUser.getName());
        toAddress.put("street1", shipUser.getShipStreet());
        toAddress.put("street2", shipUser.getShipStreetDos());
        toAddress.put("city", shipUser.getShipCity());
        toAddress.put("state", shipUser.getShipState());
        toAddress.put("country", shipUser.getShipCountry());
        toAddress.put("zip", shipUser.getShipZip());

        try{

            Address.create(toAddress);
            cart.setValidAddress(true);
            cartRepo.update(cart);

            Map<String, Object> fromAddress = new HashMap<>();
            fromAddress.put("street1", business.getStreet());
            fromAddress.put("street2", business.getStreetDos());
            fromAddress.put("city", business.getCity());
            fromAddress.put("state", business.getState());
            fromAddress.put("zip", business.getZip());
            fromAddress.put("country", business.getCountry());
            fromAddress.put("company", business.getName());
            fromAddress.put("phone", business.getPhone());

            Map<String, Object> parcelMap = new HashMap<>();
            parcelMap.put("weight", weight);

            Map<String, Object> customsInfoMap = new HashMap<>();

            Map<String, Object> shipmentMap = new HashMap<>();
            shipmentMap.put("to_address", toAddress);
            shipmentMap.put("from_address", fromAddress);
            shipmentMap.put("parcel", parcelMap);
            shipmentMap.put("customs_info", customsInfoMap);

            Shipment shipment = Shipment.create(shipmentMap);

            System.out.println("shipment " + shipment.getRates().size());
            System.out.println("shipment q: "  + shipment.toString());


            List<Rate> rates = shipment.getRates();
            Giga.sortRates(rates);

            cache.set("message", "Shipping calculated!");
            cache.set("shipment", shipment);
            cache.set("rates", rates);

        }catch(EasyPostException ex){
            System.out.println("exception : " + ex.getMessage());
            cache.set("message", "your address doesn't add up. will you try again? ");
            return "/pages/shipment/new.jsp";
        }

        return "/pages/shipment/choose.jsp";
    }

    @Post("{{business}}/shipment/add")
    public String select(HttpRequest req,
                         @Component String businessUri){
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        ShipmentRate shipmentRate = (ShipmentRate) req.inflect(ShipmentRate.class);
        cartRepo.deleteRate(shipmentRate.getCartId());
        cartRepo.saveRate(shipmentRate);

        return "[redirect]/" + businessUri + "/cart";
    }

    @Get("{{business}}/shipment/create")
    public String createShipment(Cache cache,
                                 HttpRequest req,
                                 SecurityManager security,
                                 @Component String businessUri){
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        CartService cartService = new CartService();
        Cart cart = cartService.getCart(business, cartRepo, req);

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        cartService.setData(cart, business, cache, itemRepo, designRepo, cartRepo, req, siteService);

        return "/pages/shipment/create.jsp";
    }

    @Post("{{business}}/shipment/save")
    public String save(Cache cache,
                       HttpRequest req,
                       SecurityManager security,
                       @Component String businessUri){
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        User user = (User) req.inflect(User.class);
        if(!user.valid(user.getName()) ||
                !user.valid(user.getShipStreet()) ||
                !user.valid(user.getShipCity()) ||
                !user.valid(user.getShipState()) ||
                !user.valid(user.getShipZip())){
            cache.set("message", "address is missing information!");
            return "[redirect]/" + businessUri + "/shipment/create";
        }


        System.out.println("\n\n\\\\\\\\\\\\\\\\\\\\\\\\");
        System.out.println("user " + user.getBusinessId());
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\n\n");

        String phone = "";
        if(user.getPhone() != null) phone = Giga.getPhone(user.getPhone());

        String username = "";
        if(user.getUsername() != null){
            username = Giga.getSpaces(user.getUsername());
        }

        User storedUsername = userRepo.get(username);
        if (storedUsername == null) {
            User storedPhone = userRepo.getPhone(phone);
            if(storedPhone == null){
                user.setDateJoined(Giga.getDate());
                user.setPassword(security.hash("gigabeat"));
                userRepo.save(user);
                storedUsername = userRepo.getSaved();
            }else{
                storedUsername = storedPhone;
            }
        }

        CartService cartService = new CartService();
        Cart cart = cartService.getCart(business, cartRepo, req);
        cart.setShipName(user.getName());
        cart.setShipPhone(user.getPhone());
        cart.setShipEmail(user.getUsername());
        cart.setShipStreet(user.getShipStreet());
        cart.setShipStreetDos(user.getShipStreetDos());
        cart.setShipCity(user.getShipCity());
        cart.setShipState(user.getShipState());
        cart.setShipCountry(user.getShipCountry());
        cart.setShipZip(user.getShipZip());
        cart.setUserId(storedUsername.getId());
        cartRepo.update(cart);

        storedUsername.setName(user.getName());
        storedUsername.setShipStreet(user.getShipStreet());
        storedUsername.setShipStreetDos(user.getShipStreetDos());
        storedUsername.setShipCity(user.getShipCity());
        storedUsername.setShipState(user.getShipState());
        storedUsername.setShipCountry(user.getShipCountry());
        storedUsername.setShipZip(user.getShipZip());
        userRepo.update(storedUsername);

        cache.set("message", "Successfully set your address, you're ready to go!");
        return "[redirect]/" + businessUri + "/checkout";
    }
}
