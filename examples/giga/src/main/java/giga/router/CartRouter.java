package giga.router;

import giga.service.CartService;
import net.plsar.annotations.HttpRouter;

@HttpRouter
public class CartRouter {

    @Inject
    CartService cartService;

    @Get("/{{business}}/cart")
    public String view(HttpRequest req,
                      Cache cache,
                      @RouteComponent String businessUri){
        return cartService.view(businessUri.toLowerCase(), cache, req);
    }

    @Get("/{{business}}/checkout")
    public String viewCheckout(HttpRequest req,
                               Cache cache,
                               @RouteComponent String businessUri){
        return cartService.viewCheckout(businessUri.toLowerCase(), cache, req);
    }

    @Post("/{{business}}/cart/add/{{id}}")
    public String add(HttpRequest req,
                      Cache cache,
                      @RouteComponent String businessUri,
                      @RouteComponent Long id){
        System.out.println("\n\n////////////////////////////////");
        return cartService.add(id, businessUri.toLowerCase(), cache, req);
    }

    @Post("/{{business}}/cart/minus/{{id}}")
    public String minus(Cache cache,
                      @RouteComponent String businessUri,
                      @RouteComponent Long id){
        System.out.println("minus");
        return cartService.minus(id, businessUri.toLowerCase(), data);
    }


}
