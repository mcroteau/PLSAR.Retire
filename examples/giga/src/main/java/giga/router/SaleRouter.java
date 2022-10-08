package giga.router;

import giga.service.SaleService;
import jakarta.servlet.http.HttpRequest;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

@HttpHandler
public class SaleHandler {

    @Inject
    SaleService saleService;

    @Get("/sales/{{businessId}}")
    public String list(Cache cache,
                       @RouteComponent Long businessId) throws Exception{
        return saleService.list(businessId, data);
    }

    @Post("/{{business}}/sale/{{id}}")
    public String processSale(HttpRequest req,
                          @RouteComponent String businessUri,
                          @RouteComponent Long id){
        return saleService.processSale(id, businessUri.toLowerCase(), req);
    }

    @Get("/{{business}}/sale/{{id}}")
    public String getSale(HttpRequest req,
                          Cache cache,
                          @RouteComponent String businessUri,
                          @RouteComponent Long id){
        return saleService.getSale(id, businessUri.toLowerCase(), cache, req);
    }

}
