package giga.web;

import giga.service.CartService;
import dev.blueocean.annotations.Component;
import dev.blueocean.annotations.Controller;
import dev.blueocean.annotations.Bind;
import dev.blueocean.annotations.http.Get;
import dev.blueocean.annotations.http.Post;
import dev.blueocean.model.Cache;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.security.SecurityManager;

@Controller
public class CartRouter {

    @Bind
    CartService cartService;

    @Get("/{business}/cart")
    public String view(Cache cache,
                       NetworkRequest req,
                       SecurityManager security,
                       @Component String businessUri){
        return cartService.view(businessUri.toLowerCase(), cache, req, security);
    }

    @Get("/{business}/checkout")
    public String viewCheckout(Cache cache,
                               NetworkRequest req,
                               SecurityManager security,
                               @Component String businessUri){
        return cartService.viewCheckout(businessUri.toLowerCase(), cache, req, security);
    }

    @Post("/{business}/cart/add/{id}")
    public String add(Cache cache,
                      NetworkRequest req,
                      SecurityManager security,
                      @Component String businessUri,
                      @Component Long id){
        System.out.println("\n\n////////////////////////////////");
        return cartService.add(id, businessUri.toLowerCase(), cache, req, security);
    }

    @Post("/{business}/cart/minus/{id}")
    public String minus(Cache cache,
                      @Component String businessUri,
                      @Component Long id){
        System.out.println("minus");
        return cartService.minus(id, businessUri.toLowerCase(), cache);
    }


}
