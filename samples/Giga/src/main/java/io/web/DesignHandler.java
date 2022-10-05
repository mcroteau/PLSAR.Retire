package io.web;

import io.service.DesignService;
import jakarta.servlet.http.HttpServletRequest;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.ResponseData;

@HttpHandler
public class DesignHandler {

    @Inject
    DesignService designService;
    
    @Get("/designs/new/{{id}}")
    public String configure(ResponseData data,
                            @Variable Long id){
        return designService.configure(id, data);
    }

    @Get("/designs/{{id}}")
    public String list(ResponseData data,
                       @Variable Long id) throws Exception{
        return designService.list(id, data);
    }

    @Post("/designs/save")
    public String save(HttpServletRequest req){
        return designService.save(req);
    }

    @Get("/designs/edit/{{id}}")
    public String showcase(ResponseData data,
                           @Variable Long id) throws Exception {
        return designService.edit(id, data);
    }

    @Post("/designs/update/{{id}}")
    public String update(HttpServletRequest req,
                         ResponseData data,
                         @Variable Long id){
        return designService.update(id, data, req);
    }

    @Post("/designs/delete/{{id}}")
    public String delete(ResponseData data,
                         @Variable Long id){
        return designService.delete(id, data);
    }

}
