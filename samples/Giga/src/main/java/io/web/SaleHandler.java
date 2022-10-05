package io.web;

import io.repo.SaleRepo;
import io.service.SaleService;
import jakarta.servlet.http.HttpServletRequest;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.ResponseData;

@HttpHandler
public class SaleHandler {

    @Inject
    SaleService saleService;

    @Get("/sales/{{businessId}}")
    public String list(ResponseData data,
                       @Variable Long businessId) throws Exception{
        return saleService.list(businessId, data);
    }

    @Post("/{{business}}/sale/{{id}}")
    public String processSale(HttpServletRequest req,
                          @Variable String businessUri,
                          @Variable Long id){
        return saleService.processSale(id, businessUri.toLowerCase(), req);
    }

    @Get("/{{business}}/sale/{{id}}")
    public String getSale(HttpServletRequest req,
                          ResponseData data,
                          @Variable String businessUri,
                          @Variable Long id){
        return saleService.getSale(id, businessUri.toLowerCase(), data, req);
    }

}
