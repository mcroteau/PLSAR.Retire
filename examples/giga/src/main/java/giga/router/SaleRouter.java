package giga.router;

import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;
import giga.Giga;
import giga.model.*;
import giga.repo.*;
import giga.service.BusinessService;
import giga.service.SiteService;
import giga.service.SmsService;
import net.plsar.RouteAttributes;
import net.plsar.annotations.Component;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Inject;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;
import net.plsar.model.HttpRequest;
import net.plsar.security.SecurityManager;
import org.ocpsoft.prettytime.PrettyTime;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@HttpRouter
public class SaleRouter {

    @Inject
    ItemRepo itemRepo;

    @Inject
    UserRepo userRepo;

    @Inject
    CategoryRepo categoryRepo;

    @Inject
    SaleRepo saleRepo;

    @Inject
    CartRepo cartRepo;

    @Inject
    DesignRepo designRepo;

    @Inject
    BusinessRepo businessRepo;

    BusinessService businessService;

    SmsService smsService;

    public SaleRouter(){
        this.smsService = new SmsService();
        this.businessService = new BusinessService();
    }

    @Get("/sales/{{businessId}}")
    public String list(Cache cache,
                       HttpRequest req,
                       SecurityManager security,
                       @Component Long businessId) throws Exception{
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        Business business = businessRepo.get(businessId);

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }
        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        businessService.setData(businessId, cache, authUser, businessRepo, siteService);

        List<Sale> sales;
        if(business.getAffiliate() == null ||
                !business.getAffiliate()) {
            sales = saleRepo.getListPrimary(businessId);
        }else{
            sales = saleRepo.getListAffiliate(businessId);
        }

        setSaleData(sales);
        cache.set("sales", sales);

        cache.set("page", "/pages/sale/list.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/{{businessUri}}/sale/{{cartId}}")
    public String processSale(HttpRequest req,
                          @Component String businessUri,
                          @Component Long cartId){
        Business business = businessRepo.get(businessUri);
        Business primaryBusiness = businessRepo.get(business.getPrimaryId());
        System.out.println("set sale business " + business);
        if(business == null){
            return "[redirect]/home";
        }

        if(primaryBusiness != null &&
                primaryBusiness.getStripeId() == null){
            return "[redirect]/" + businessUri + "/oops";
        }

        Cart cart = cartRepo.get(cartId);
        System.out.println("set sale cart " + cart);
        if(cart == null){
            return "[redirect]/" + businessUri + "/cart";
        }

        Sale sale = new Sale();
        sale.setCartId(cart.getId());
        sale.setSalesDate(Giga.getDate());
        sale.setUserId(cart.getUserId());
        sale.setAmount(cart.getTotal());
        System.out.println("cart sum, " + cart.getTotal());

        saleRepo.save(sale);
        sale = saleRepo.getSaved();

        System.out.println("last .");

        try {
            Card card = (Card) req.inflect(Card.class);

            RouteAttributes routeAttributes = req.getRouteAttributes();
            Stripe.apiKey = (String) routeAttributes.get("stripe.key");

            Map<String, Object> cardMap = new HashMap<>();
            cardMap.put("number", card.getCard());
            cardMap.put("exp_month", card.getExpMonth());
            cardMap.put("exp_year", card.getExpYear());
            cardMap.put("cvc", card.getCvc());

            Map<String, Object> cardParams = new HashMap<>();
            cardParams.put("card", cardMap);
            Token cardToken = Token.create(cardParams);

            Map<String, Object> customerParams = new HashMap<>();
            customerParams.put("name", cart.getShipName());
            customerParams.put("email", cart.getShipEmail());
            customerParams.put("phone", cart.getShipPhone());
            customerParams.put("source", cardToken.getId());
            Customer customer = Customer.create(customerParams);
            sale.setStripeApplicationCustomerId(customer.getId());

            BigDecimal chargeAmount = card.getChargeAmount();

            if (business.getAffiliate() == null ||
                    !business.getAffiliate()) {

                Long primarysAmount = chargeAmount.movePointRight(2).longValueExact();
                System.out.println("fees ans shit " + ", " + primarysAmount);

                RequestOptions tokenRequestOptions = RequestOptions.builder()
                        .setStripeAccount(business.getStripeId())
                        .build();

                Token primarysToken = Token.create(cardParams, tokenRequestOptions);

                Map<String, Object> primaryCustomerParams = new HashMap<>();
                primaryCustomerParams.put("email", cart.getShipEmail());
                primaryCustomerParams.put("source", primarysToken.getId());


                RequestOptions primaryCustomerRequestOptions = RequestOptions.builder()
                        .setStripeAccount(business.getStripeId())
                        .build();

                Customer primaryCustomer = Customer.create(primaryCustomerParams, primaryCustomerRequestOptions);

                Map<String, Object> chargeParams = new HashMap<>();
                chargeParams.put("amount", primarysAmount);
                chargeParams.put("customer", primaryCustomer.getId());
                chargeParams.put("card", primarysToken.getCard().getId());
                chargeParams.put("currency", "usd");

                RequestOptions primaryChargeRequestOptions = RequestOptions.builder().setStripeAccount(business.getStripeId()).build();
                Charge charge = Charge.create(chargeParams, primaryChargeRequestOptions);

                sale.setPrimaryId(business.getId());
                sale.setStripePrimaryCustomerId(primaryCustomer.getId());
                sale.setPrimaryAmount(primarysAmount);
                sale.setStripePrimaryChargeId(charge.getId());

                saleRepo.updatePrimary(sale);
            }


            if(business.getAffiliate() != null &&
                    business.getAffiliate()){

                BigDecimal commissionRate = primaryBusiness.getBaseCommission().divide(new BigDecimal(100), 3, RoundingMode.HALF_UP);
                BigDecimal affiliateAmountPre = chargeAmount.multiply(commissionRate, new MathContext(3, RoundingMode.HALF_DOWN));
                BigDecimal primaryAmountPre = chargeAmount.subtract(affiliateAmountPre);

                System.out.println("fees ans shit! aa:" + affiliateAmountPre);

                Long affiliateAmount = affiliateAmountPre.movePointRight(2).longValueExact();
                Long primaryAmount = primaryAmountPre.movePointRight(2).longValueExact();

                System.out.println("fees ans shit! pa" + primaryAmount + ", aa" + affiliateAmount);

                Map<String, Object> chargeParamsDos = new HashMap<>();
                chargeParamsDos.put("amount", affiliateAmount);
                chargeParamsDos.put("customer", customer.getId());
                chargeParamsDos.put("card", cardToken.getCard().getId());
                chargeParamsDos.put("currency", "usd");
                Charge.create(chargeParamsDos);


                RequestOptions tokenRequestOptions = RequestOptions.builder()
                        .setStripeAccount(primaryBusiness.getStripeId())
                        .build();

                Token token = Token.create(cardParams, tokenRequestOptions);
                Map<String, Object> customerParamsTres = new HashMap<>();
                customerParamsTres.put("email", cart.getShipEmail());
                customerParamsTres.put("source", token.getId());


                RequestOptions customerRequestOptions = RequestOptions.builder()
                        .setStripeAccount(primaryBusiness.getStripeId())
                        .build();

                Customer customerTres = Customer.create(customerParamsTres, customerRequestOptions);

                Map<String, Object> chargeParams = new HashMap<>();
                chargeParams.put("amount", primaryAmount);
                chargeParams.put("customer", customerTres.getId());
                chargeParams.put("card", token.getCard().getId());
                chargeParams.put("currency", "usd");

                RequestOptions chargeRequestOptions = RequestOptions.builder().setStripeAccount(primaryBusiness.getStripeId()).build();
                Charge.create(chargeParams, chargeRequestOptions);


                sale.setPrimaryId(primaryBusiness.getId());
                sale.setAffiliateId(business.getId());
                sale.setAffiliateAmount(affiliateAmount);
                sale.setPrimaryAmount(primaryAmount);
                saleRepo.updateAffiliate(sale);
            }


        }catch(Exception ex){
            ex.printStackTrace();
            saleRepo.delete(sale.getId());
            return "[redirect]/" + businessUri + "/checkout";
        }

        cart.setActive(false);
        cart.setSale(true);

        cartRepo.update(cart);


        RouteAttributes routeAttributes = req.getRouteAttributes();
        String key = (String) routeAttributes.get("sms.key");

        smsService.send(key, "9079878652", "Giga >_ An order has been placed!");

        if(!business.getPhone().equals("")) {
            smsService.send(key, business.getPhone(), "Giga >_ Congratulations, an order has been placed!");
        }

        if(!cart.getShipPhone().equals("")) {
            smsService.send(key, cart.getShipPhone(), "Thank you! Your order has been placed!");
        }

        return "[redirect]/" + businessUri + "/sale/" + sale.getId();
    }

    @Get("/{{business}}/sale/{{id}}")
    public String getSale(Cache cache,
                          HttpRequest req,
                          SecurityManager security,
                          @Component String businessUri,
                          @Component Long id){

        System.out.println("get sale : " + id + ", " + businessUri + " : " + businessRepo);
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        Sale sale = null;
        if(business.getAffiliate() != null &&
                business.getAffiliate()){
            sale = saleRepo.getAffiliate(id, business.getId());
        }else{
            sale = saleRepo.getPrimary(id, business.getId());
        }

        if(sale == null){
            return "[redirect]/" + businessUri + "/cart";
        }

        Cart cart = cartRepo.get(sale.getCartId());
        if(cart == null){
            return "[redirect]/" + businessUri + "/oops";
        }

        cache.set("sale", sale);
        setData(cart, business, cache, req, security);
        return "/pages/sale/index.jsp";
    }

    public void setSaleData(List<Sale> sales) throws ParseException {
        for(Sale sale : sales){
            Cart cart = cartRepo.get(sale.getCartId());
            BigDecimal subtotal = new BigDecimal(0);
            List<CartItem> cartItems = cartRepo.getListItems(cart.getId());
            for(CartItem cartItem : cartItems){
                Item item = itemRepo.get(cartItem.getItemId());
                cartItem.setItem(item);

                BigDecimal itemTotal = item.getPrice().multiply(cartItem.getQuantity());
                List<CartOption> cartOptions = cartRepo.getOptions(cartItem.getId());
                for(CartOption cartOption : cartOptions){
                    ItemOption itemOption = itemRepo.getOption(cartOption.getItemOptionId());
                    OptionValue optionValue = itemRepo.getValue(cartOption.getOptionValueId());
                    cartOption.setItemOption(itemOption);
                    cartOption.setOptionValue(optionValue);
                    if(optionValue.getPrice() != null){
                        itemTotal = itemTotal.add(cartItem.getPrice().multiply(cartItem.getQuantity()));
                    }
                }
                cartItem.setItemTotal(itemTotal);

                subtotal = subtotal.add(itemTotal);
                cart.setSubtotal(subtotal);

                cartItem.setCartOptions(cartOptions);
            }

            SimpleDateFormat format = new SimpleDateFormat(Giga.DATE_FORMAT);
            Date date = format.parse(Long.toString(sale.getSalesDate()));
            PrettyTime p = new PrettyTime();
            String prettyDate = p.format(date);
            sale.setPrettyDate(prettyDate);

            cart.setCartItems(cartItems);
            BigDecimal total = cart.getShipping().add(cart.getSubtotal());
            cart.setTotal(total);
            sale.setCart(cart);
        }
    }

    public void setData(Cart cart, Business business, Cache cache, HttpRequest req, SecurityManager security){
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

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);

        cache.set("design", design);
        cache.set("request", req);
        cache.set("business", business);
        cache.set("cart", cart);
        cache.set("items", cartItems);
        cache.set("siteService", siteService);
    }
}
