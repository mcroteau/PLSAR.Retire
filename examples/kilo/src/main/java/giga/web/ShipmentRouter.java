package giga.web;

import giga.service.ShipmentService;
import dev.blueocean.annotations.Component;
import dev.blueocean.annotations.Controller;
import dev.blueocean.annotations.Bind;
import dev.blueocean.annotations.http.Get;
import dev.blueocean.annotations.http.Post;
import dev.blueocean.model.Cache;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.security.SecurityManager;

@Controller
public class ShipmentRouter {

    @Bind
    ShipmentService shipmentService;

    @Get("{business}/shipment")
    public String begin(Cache cache,
                        NetworkRequest req,
                        SecurityManager security,
                        @Component String businessUri){
        return shipmentService.begin(businessUri.toLowerCase(), cache, req, security);
    }

    @Get("{business}/shipment/rates")
    public String redirect(@Component String businessUri){
        return "redirect:/" + businessUri + "/shipment";
    }

    @Post("{business}/shipment/rates")
    public String getRates(Cache cache,
                           NetworkRequest req,
                           SecurityManager security,
                           @Component String businessUri){
        return shipmentService.getRates(businessUri.toLowerCase(), cache, req, security);
    }

    @Post("{business}/shipment/add")
    public String select(Cache cache,
                         NetworkRequest req,
                         @Component String businessUri){
        return shipmentService.select(businessUri.toLowerCase(), cache, req);
    }

    @Get("{business}/shipment/create")
    public String createShipment(Cache cache,
                                 NetworkRequest req,
                                 SecurityManager security,
                                 @Component String businessUri){
        return shipmentService.createShipment(businessUri.toLowerCase(), cache, req, security);
    }

    @Post("{business}/shipment/save")
    public String save(Cache cache,
                       NetworkRequest req,
                       SecurityManager security,
                       @Component String businessUri){
        return shipmentService.save(businessUri.toLowerCase(), cache, req, security);
    }
}
