package giga.service;

import com.easypost.EasyPost;
import com.easypost.exception.EasyPostException;
import com.easypost.model.Address;
import com.easypost.model.Rate;
import com.easypost.model.Shipment;
import giga.Giga;
import giga.model.*;
import giga.repo.*;
import dev.blueocean.RouteAttributes;
import dev.blueocean.annotations.Bind;
import dev.blueocean.annotations.Service;
import dev.blueocean.model.Cache;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.security.SecurityManager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShipmentService {

    @Bind
    ItemRepo itemRepo;

    @Bind
    UserRepo userRepo;

    @Bind
    CartRepo cartRepo;

    @Bind
    DesignRepo designRepo;

    @Bind
    BusinessRepo businessRepo;


    public String begin(String businessUri, Cache cache, NetworkRequest req, SecurityManager security) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "redirect:/home";
        }

        CartService cartService = new CartService();
        Cart cart = cartService.getCart(business, req, security);
        List<CartItem> cartItems = cartRepo.getListItems(cart.getId());
        if(cartItems.size() == 0) {
            return "redirect:/" + businessUri + "/cart";
        }

        cartService.setData(cart, business, cache, designRepo, itemRepo, cartRepo, req, security);

        return "/pages/shipment/new.jsp";

    }

    public String getRates(String businessUri, Cache cache, NetworkRequest req, SecurityManager security) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "redirect:/home";
        }

        CartService cartService = new CartService();
        Cart cart = cartService.getCart(business, req, security);

        User shipUser = (User) req.inflect(User.class);

        String phone = "";
        if(shipUser.getPhone() != null) phone = Giga.getPhone(shipUser.getPhone());

        User storedPhone = userRepo.getPhone(phone);
        if(storedPhone == null){
            User storedUsername = userRepo.get(shipUser.getEmail());
            if(storedUsername == null){
                shipUser.setDateJoined(Giga.getDate());
                shipUser.setPassword(security.hash("gigabeat"));
                userRepo.save(shipUser);
                storedPhone = userRepo.getSaved();
            }
        }

        cart.setShipName(shipUser.getName());
        cart.setShipPhone(shipUser.getPhone());
        cart.setShipEmail(shipUser.getEmail());
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

        cartService.setData(cart, business, cache, designRepo, itemRepo, cartRepo, req, security);

        RouteAttributes routeAttributes = req.getRouteAttributes();
        String easypostKey = (String) routeAttributes.get("easypost.key");
        EasyPost.apiKey = easypostKey;

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
            System.out.println("shipment q: "  + shipment);


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

    public String select(String businessUri, Cache cache, NetworkRequest req) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "redirect:/home";
        }

        ShipmentRate shipmentRate = (ShipmentRate) req.inflect(ShipmentRate.class);
        cartRepo.deleteRate(shipmentRate.getCartId());
        cartRepo.saveRate(shipmentRate);

        return "redirect:/" + businessUri + "/cart";
    }

    public Boolean validateAddress(User shipUser, Business business, NetworkRequest req){

        try {

            RouteAttributes routeAttributes = req.getRouteAttributes();
            String easypostKey = (String) routeAttributes.get("easypost.key");
            EasyPost.apiKey = easypostKey;

            Map<String, Object> toAddress = new HashMap<>();
            toAddress.put("name", shipUser.getName());
            toAddress.put("street1", shipUser.getShipStreet());
            toAddress.put("street2", shipUser.getShipStreetDos());
            toAddress.put("city", shipUser.getShipCity());
            toAddress.put("state", shipUser.getShipState());
            toAddress.put("zip", shipUser.getShipZip());

            Address.createAndVerify(toAddress);

        }catch (EasyPostException epx){
            epx.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean validateAddress(Cart cart, Business business, NetworkRequest req){

        try {

            RouteAttributes routeAttributes = req.getRouteAttributes();
            String easypostKey = (String) routeAttributes.get("easypost.key");
            EasyPost.apiKey = easypostKey;

            Map<String, Object> toAddress = new HashMap<>();
            toAddress.put("name", cart.getShipName());
            toAddress.put("street1", cart.getShipStreet());
            toAddress.put("street2", cart.getShipStreetDos());
            toAddress.put("city", cart.getShipCity());
            toAddress.put("state", cart.getShipState());
            toAddress.put("country", cart.getShipCountry());
            toAddress.put("zip", cart.getShipZip());

            Address.createAndVerify(toAddress);

        }catch (EasyPostException epx){
            return false;
        }

        return true;

    }

    public Boolean validateBusinessAddress(Business business){
        try {

            Map<String, Object> fromAddress = new HashMap<>();

            fromAddress.put("street1", business.getStreet());
            fromAddress.put("street2", business.getStreetDos());
            fromAddress.put("city", business.getCity());
            fromAddress.put("state", business.getState());
            fromAddress.put("zip", business.getZip());
            fromAddress.put("country", business.getCountry());
            fromAddress.put("company", business.getName());
            fromAddress.put("phone", business.getPhone());

            Address.createAndVerify(fromAddress);

        } catch (EasyPostException epx) {
            return false;
        }
        return true;
    }


    public String createShipment(String businessUri, Cache cache, NetworkRequest req, SecurityManager security) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "redirect:/home";
        }

        CartService cartService = new CartService();
        Cart cart = cartService.getCart(business, req, security);
        cartService.setData(cart, business, cache, designRepo, itemRepo, cartRepo, req, security);

        return "/pages/shipment/create.jsp";
    }

    public String save(String businessUri, Cache cache, NetworkRequest req, SecurityManager security) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "redirect:/home";
        }

        User user = (User) req.inflect(User.class);
        if(!user.valid(user.getName()) ||
                !user.valid(user.getShipStreet()) ||
                    !user.valid(user.getShipCity()) ||
                        !user.valid(user.getShipState()) ||
                            !user.valid(user.getShipZip())){
            cache.set("message", "address is missing information!");
            return "redirect:/" + businessUri + "/shipment/create";
        }


        System.out.println("\n\n\\\\\\\\\\\\\\\\\\\\\\\\");
        System.out.println("user " + user.getBusinessId());
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\n\n");

        String phone = "";
        if(user.getPhone() != null) phone = Giga.getPhone(user.getPhone());

        String username = "";
        if(user.getEmail() != null){
            username = Giga.getSpaces(user.getEmail());
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
        Cart cart = cartService.getCart(business, req, security);
        cart.setShipName(user.getName());
        cart.setShipPhone(user.getPhone());
        cart.setShipEmail(user.getEmail());
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
        return "redirect:/" + businessUri + "/checkout";
    }

}
