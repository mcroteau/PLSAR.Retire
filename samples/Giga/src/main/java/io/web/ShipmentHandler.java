package io.web;

import io.service.ShipmentService;
import jakarta.servlet.http.HttpServletRequest;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.ResponseData;

@HttpHandler
public class ShipmentHandler {

    @Inject
    ShipmentService shipmentService;

    @Get("{{business}}/shipment")
    public String begin(HttpServletRequest req,
                           ResponseData data,
                           @Variable String businessUri){
        return shipmentService.begin(businessUri.toLowerCase(), data, req);
    }

    @Get("{{business}}/shipment/rates")
    public String redirect(@Variable String businessUri){
        return "[redirect]/" + businessUri + "/shipment";
    }

    @Post("{{business}}/shipment/rates")
    public String getRates(HttpServletRequest req,
                           ResponseData data,
                           @Variable String businessUri){
        return shipmentService.getRates(businessUri.toLowerCase(), data, req);
    }

    @Post("{{business}}/shipment/add")
    public String select(HttpServletRequest req,
                        ResponseData data,
                        @Variable String businessUri){
        return shipmentService.select(businessUri.toLowerCase(), data, req);
    }

    @Get("{{business}}/shipment/create")
    public String createShipment(HttpServletRequest req,
                           ResponseData data,
                           @Variable String businessUri){
        return shipmentService.createShipment(businessUri.toLowerCase(), data, req);
    }

    @Post("{{business}}/shipment/save")
    public String save(HttpServletRequest req,
                           ResponseData data,
                           @Variable String businessUri){
        return shipmentService.save(businessUri.toLowerCase(), data, req);
    }
}
