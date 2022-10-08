package giga.router;

import giga.service.DataService;
import jakarta.servlet.http.HttpRequest;
import jakarta.servlet.http.HttpServletResponse;
import qio.annotate.*;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

@HttpHandler
public class DataHandler {

    @Inject
    DataService dataService;

    @Get("/import/media/{{businessId}}")
    public String viewImport(Cache cache,
                             @RouteComponent Long businessId) {
        return dataService.viewImportMedia(businessId, data);
    }

    @Post("/import/media/{{businessId}}")
    public String importMedia(HttpRequest req,
                              Cache cache,
                              @RouteComponent Long businessId) throws Exception {
        return dataService.importMedia(businessId, cache, req);
    }

    @Get("/imports/media/{{businessId}}")
    public String viewImports(Cache cache,
                             @RouteComponent Long businessId) {
        return dataService.viewImportsMedia(businessId, data);
    }

    @Get("/imports/media/{{businessId}}/{{importId}}")
    public String viewImports(Cache cache,
                              @RouteComponent Long businessId,
                              @RouteComponent Long importId) {
        return dataService.viewMedias(businessId, importId, data);
    }

    @Post("/import/media/update/{{businessId}}/{{importId}}")
    public String importMedia(HttpRequest req,
                              Cache cache,
                              @RouteComponent Long businessId,
                              @RouteComponent Long importId) throws Exception {
        return dataService.updateMedia(businessId, importId, cache, req);
    }


    @Post("/import/media/convert/{{businessId}}/{{importId}}")
    public String convertItems(HttpRequest req,
                               Cache cache,
                               @RouteComponent Long businessId,
                               @RouteComponent Long importId){
        return dataService.convertItems(businessId, importId, cache, req);
    }

    @Post("/import/media/delete/{{businessId}}/{{importId}}")
    public String deleteImport(HttpRequest req,
                              Cache cache,
                              @RouteComponent Long businessId,
                              @RouteComponent Long importId){
        return dataService.deleteImport(businessId, importId, cache, req);
    }
}
