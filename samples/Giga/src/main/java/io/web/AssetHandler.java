package io.web;

import io.service.AssetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import qio.annotate.*;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.ResponseData;

@HttpHandler
public class AssetHandler {

    @Inject
    AssetService assetService;

    @Text
    @Get("/go/{{n}}")
    public String getAsset(HttpServletResponse resp,
                         @Variable String meta) throws Exception {
        System.out.println("get.");
        return assetService.getAsset(meta, resp);
    }

    @Media
    @Get("/media/{{n}}")
    public String getMedia(HttpServletResponse resp,
                           @Variable String meta) throws Exception {
        System.out.println("media.");
        return assetService.getMedia(meta, resp);
    }

    @Get("/assets/new/{{businessId}}")
    public String configure(ResponseData data,
                            @Variable Long businessId){
        return assetService.create(businessId, data);
    }

    @Get("/assets/{{businessId}}")
    public String list(ResponseData data,
                       @Variable Long businessId){
        System.out.println("assets.");
        return assetService.list(businessId, data);
    }

    @Post("/assets/save")
    public String save(HttpServletRequest req) throws Exception {
        return assetService.save(req);
    }

    @Post("/assets/delete/{{businessId}}/{{id}}")
    public String delete(ResponseData data,
                         @Variable Long businessId,
                         @Variable Long id){
        return assetService.delete(id, businessId, data);
    }
}
