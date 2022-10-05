package io.web;

import io.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.ResponseData;

@HttpHandler
public class CartHandler {

    @Inject
    CartService cartService;

    @Get("/{{business}}/cart")
    public String view(HttpServletRequest req,
                      ResponseData data,
                      @Variable String businessUri){
        return cartService.view(businessUri.toLowerCase(), data, req);
    }

    @Get("/{{business}}/checkout")
    public String viewCheckout(HttpServletRequest req,
                               ResponseData data,
                               @Variable String businessUri){
        return cartService.viewCheckout(businessUri.toLowerCase(), data, req);
    }

    @Post("/{{business}}/cart/add/{{id}}")
    public String add(HttpServletRequest req,
                      ResponseData data,
                      @Variable String businessUri,
                      @Variable Long id){
        System.out.println("\n\n////////////////////////////////");
        return cartService.add(id, businessUri.toLowerCase(), data, req);
    }

    @Post("/{{business}}/cart/minus/{{id}}")
    public String minus(ResponseData data,
                      @Variable String businessUri,
                      @Variable Long id){
        System.out.println("minus");
        return cartService.minus(id, businessUri.toLowerCase(), data);
    }


}
