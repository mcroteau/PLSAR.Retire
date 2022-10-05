package io.service;

import chico.Chico;
import com.easypost.EasyPost;
import com.easypost.exception.EasyPostException;
import com.easypost.model.Address;
import com.easypost.model.Rate;
import com.easypost.model.Shipment;
import jakarta.servlet.http.HttpServletRequest;
import io.Giga;
import io.model.*;
import io.repo.*;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Property;
import qio.annotate.Service;
import qio.model.web.ResponseData;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShipmentService {
    
    @Property("easypost.key")
    String easypostKey;

    @Inject
    CartRepo cartRepo;

    @Inject
    UserRepo userRepo;

    @Inject
    ItemRepo itemRepo;

    @Inject
    BusinessRepo businessRepo;

    @Inject
    CartService cartService;


    public String begin(String businessUri, ResponseData data, HttpServletRequest req) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        Cart cart = cartService.getCart(business, req);
        List<CartItem> cartItems = cartRepo.getListItems(cart.getId());
        if(cartItems.size() == 0) {
            return "[redirect]/" + businessUri + "/cart";
        }

        cartService.setData(cart, business, data, req);

        return "/pages/shipment/new.jsp";

    }

    public String getRates(String businessUri, ResponseData data, HttpServletRequest req) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        Cart cart = cartService.getCart(business, req);

        User shipUser = (User) Qio.get(req, User.class);

        String phone = "";
        if(shipUser.getPhone() != null) phone = Giga.getPhone(shipUser.getPhone());

        User storedPhone = userRepo.getPhone(phone);
        if(storedPhone == null){
            User storedUsername = userRepo.get(shipUser.getUsername());
            if(storedUsername == null){
                shipUser.setDateJoined(Giga.getDate());
                shipUser.setPassword(Chico.dirty("gigabeat"));
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

        data.set("weight", weight);

        cartService.setData(cart, business, data, req);

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
            System.out.println("shipment q: "  + shipment.toString());


            List<Rate> rates = shipment.getRates();
            Giga.sortRates(rates);

            data.set("message", "Shipping calculated!");
            data.set("shipment", shipment);
            data.set("rates", rates);

        }catch(EasyPostException ex){
            System.out.println("exception : " + ex.getMessage());
            data.set("message", "your address doesn't add up. will you try again? ");
            return "/pages/shipment/new.jsp";
        }

        return "/pages/shipment/choose.jsp";
    }

    public String select(String businessUri, ResponseData data, HttpServletRequest req) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        ShipmentRate shipmentRate = (ShipmentRate) Qio.get(req, ShipmentRate.class);
        cartRepo.deleteRate(shipmentRate.getCartId());
        cartRepo.saveRate(shipmentRate);

        return "[redirect]/" + businessUri + "/cart";
    }

    public Boolean validateAddress(User shipUser, Business business){

        try {

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

    public Boolean validateAddress(Cart cart, Business business){

        try {

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


    public String createShipment(String businessUri, ResponseData data, HttpServletRequest req) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        Cart cart = cartService.getCart(business, req);
        cartService.setData(cart, business, data, req);

        return "/pages/shipment/create.jsp";
    }

    public String save(String businessUri, ResponseData data, HttpServletRequest req) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        User user = (User) Qio.get(req, User.class);
        if(!user.valid(user.getName()) ||
                !user.valid(user.getShipStreet()) ||
                    !user.valid(user.getShipCity()) ||
                        !user.valid(user.getShipState()) ||
                            !user.valid(user.getShipZip())){
            data.set("message", "address is missing information!");
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
                user.setPassword(Chico.dirty("gigabeat"));
                userRepo.save(user);
                storedUsername = userRepo.getSaved();
            }else{
                storedUsername = storedPhone;
            }
        }

        Cart cart = cartService.getCart(business, req);
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

        data.set("message", "Successfully set your address, you're ready to go!");
        return "[redirect]/" + businessUri + "/checkout";
    }

}
