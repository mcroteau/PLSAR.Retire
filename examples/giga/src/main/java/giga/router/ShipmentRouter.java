package giga.router;

import giga.service.ShipmentService;
import jakarta.servlet.http.HttpRequest;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

@HttpHandler
public class ShipmentHandler {

    @Inject
    ShipmentService shipmentService;

    @Get("{{business}}/shipment")
    public String begin(HttpRequest req,
                           Cache cache,
                           @RouteComponent String businessUri){
        return shipmentService.begin(businessUri.toLowerCase(), cache, req);
    }

    @Get("{{business}}/shipment/rates")
    public String redirect(@RouteComponent String businessUri){
        return "[redirect]/" + businessUri + "/shipment";
    }

    @Post("{{business}}/shipment/rates")
    public String getRates(HttpRequest req,
                           Cache cache,
                           @RouteComponent String businessUri){
        return shipmentService.getRates(businessUri.toLowerCase(), cache, req);
    }

    @Post("{{business}}/shipment/add")
    public String select(HttpRequest req,
                        Cache cache,
                        @RouteComponent String businessUri){
        return shipmentService.select(businessUri.toLowerCase(), cache, req);
    }

    @Get("{{business}}/shipment/create")
    public String createShipment(HttpRequest req,
                           Cache cache,
                           @RouteComponent String businessUri){
        return shipmentService.createShipment(businessUri.toLowerCase(), cache, req);
    }

    @Post("{{business}}/shipment/save")
    public String save(HttpRequest req,
                           Cache cache,
                           @RouteComponent String businessUri){
        return shipmentService.save(businessUri.toLowerCase(), cache, req);
    }
}
