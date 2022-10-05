package io.web;

import io.service.PageService;
import jakarta.servlet.http.HttpServletRequest;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.ResponseData;

@HttpHandler
public class PageHandler {

    @Inject
    PageService pageService;

    @Get("/{{business}}")
    public String getHome(HttpServletRequest req,
                          ResponseData data,
                          @Variable String business){
        return pageService.getPage(business.toLowerCase(), "home", data, req);
    }

    @Get("/{{business}}/asset/{{page}}")
    public String getPage(HttpServletRequest req,
                          ResponseData data,
                          @Variable String business,
                          @Variable String page){
        System.out.println("get page");
        return pageService.getPage(business.toLowerCase(), page.toLowerCase(), data, req);
    }

    @Get("/pages/new/{{businessId}}")
    public String configure(ResponseData data,
                            @Variable Long businessId){
        return pageService.create(businessId, data);
    }

    @Get("/pages/{{businessId}}")
    public String list(ResponseData data,
                       @Variable Long businessId){
        return pageService.list(businessId, data);
    }

    @Post("/pages/save")
    public String save(HttpServletRequest req) throws Exception {
        return pageService.save(req);
    }

    @Post("/pages/delete/{{businessId}}/{{id}}")
    public String delete(ResponseData data,
                         @Variable Long businessId,
                         @Variable Long id){
        return pageService.delete(id, businessId, data);
    }

}
