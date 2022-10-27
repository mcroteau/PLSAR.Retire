package giga.web;

import giga.service.DataService;
import dev.blueocean.annotations.Component;
import dev.blueocean.annotations.Design;
import dev.blueocean.annotations.Controller;
import dev.blueocean.annotations.Bind;
import dev.blueocean.annotations.http.Get;
import dev.blueocean.annotations.http.Post;
import dev.blueocean.model.Cache;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.security.SecurityManager;

@Controller
public class DataRouter {

    @Bind
    DataService dataService;

    @Design("/designs/auth.jsp")
    @Get("/import/media/{businessId}")
    public String viewImport(Cache cache,
                             NetworkRequest req,
                             SecurityManager security,
                             @Component Long businessId) {
        return dataService.viewImportMedia(businessId, cache, req, security);
    }

    @Post("/import/media/{businessId}")
    public String importMedia(Cache cache,
                              NetworkRequest req,
                              SecurityManager security,
                              @Component Long businessId) throws Exception {
        return dataService.importMedia(businessId, cache, req, security);
    }

    @Design("/designs/auth.jsp")
    @Get("/imports/media/{businessId}")
    public String viewImports(Cache cache,
                              NetworkRequest req,
                              SecurityManager security,
                              @Component Long businessId) {
        return dataService.viewImportsMedia(businessId, cache, req, security);
    }

    @Design("/designs/auth.jsp")
    @Get("/imports/media/{businessId}/{importId}")
    public String viewImports(Cache cache,
                              NetworkRequest req,
                              SecurityManager security,
                              @Component Long businessId,
                              @Component Long importId) {
        return dataService.viewMedias(businessId, importId, cache, req, security);
    }

    @Post("/import/media/update/{businessId}/{importId}")
    public String importMedia(Cache cache,
                              NetworkRequest req,
                              SecurityManager security,
                              @Component Long businessId,
                              @Component Long importId){
        return dataService.updateMedia(businessId, importId, cache, req, security);
    }


    @Post("/import/media/convert/{businessId}/{importId}")
    public String convertItems(Cache cache,
                               NetworkRequest req,
                               SecurityManager security,
                               @Component Long businessId,
                               @Component Long importId){
        return dataService.convertItems(businessId, importId, cache, req, security);
    }

    @Post("/import/media/delete/{businessId}/{importId}")
    public String deleteImport(Cache cache,
                               NetworkRequest req,
                               SecurityManager security,
                               @Component Long businessId,
                               @Component Long importId){
        return dataService.deleteImport(businessId, importId, cache, req, security);
    }

    @Design("/designs/auth.jsp")
    @Get("/imports/item_groups/new/{businessId}")
    public String viewItemGroupImport(Cache cache,
                                      NetworkRequest req,
                                      SecurityManager security,
                                      @Component Long businessId) {
        return dataService.viewItemGroupImport(businessId, cache, req, security);
    }

    @Design("/designs/auth.jsp")
    @Get("/imports/item_groups/{businessId}")
    public String itemGroups(Cache cache,
                             NetworkRequest req,
                             SecurityManager security,
                             @Component Long businessId) {
        return dataService.viewItemGroups(businessId, cache, req, security);
    }

    @Post("/imports/item_groups/{businessId}")
    public String ingest(Cache cache,
                         NetworkRequest req,
                         SecurityManager security,
                         @Component Long businessId) {
        return dataService.ingest(businessId, cache, req, security);
    }

    @Post("/imports/item_groups/delete/{businessId}/{id}")
    public String deleteGroupImport(Cache cache,
                                    NetworkRequest req,
                                    SecurityManager security,
                                    @Component Long businessId,
                                    @Component Long ingestId) {
        return dataService.deleteGroupImport(ingestId, businessId, cache, req, security);
    }

    @Post("/imports/item_groups/group/delete/{businessId}/{id}")
    public String deleteGroups(Cache cache,
                               NetworkRequest req,
                               SecurityManager security,
                               @Component Long businessId,
                               @Component Long groupId) {
        return dataService.deleteImportedGroup(groupId, businessId, cache, req, security);
    }
}
