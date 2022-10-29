package giga.web;

import giga.service.ShipmentService;
import net.plsar.annotations.Component;
import net.plsar.annotations.Controller;
import net.plsar.annotations.Bind;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;
import net.plsar.model.NetworkRequest;
import net.plsar.security.SecurityManager;

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
