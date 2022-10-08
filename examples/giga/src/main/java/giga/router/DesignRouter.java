package giga.router;

import giga.service.DesignService;
import jakarta.servlet.http.HttpRequest;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

@HttpHandler
public class DesignHandler {

    @Inject
    DesignService designService;
    
    @Get("/designs/new/{{id}}")
    public String configure(Cache cache,
                            @RouteComponent Long id){
        return designService.configure(id, data);
    }

    @Get("/designs/{{id}}")
    public String list(Cache cache,
                       @RouteComponent Long id) throws Exception{
        return designService.list(id, data);
    }

    @Post("/designs/save")
    public String save(HttpRequest req){
        return designService.save(req);
    }

    @Get("/designs/edit/{{id}}")
    public String showcase(Cache cache,
                           @RouteComponent Long id) throws Exception {
        return designService.edit(id, data);
    }

    @Post("/designs/update/{{id}}")
    public String update(HttpRequest req,
                         Cache cache,
                         @RouteComponent Long id){
        return designService.update(id, cache, req);
    }

    @Post("/designs/delete/{{id}}")
    public String delete(Cache cache,
                         @RouteComponent Long id){
        return designService.delete(id, data);
    }

}
