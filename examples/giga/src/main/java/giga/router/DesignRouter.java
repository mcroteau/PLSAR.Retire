package giga.router;

import giga.service.DesignService;
import jakarta.servlet.http.HttpServletRequest;
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
    public String configure(Cache data,
                            @Variable Long id){
        return designService.configure(id, data);
    }

    @Get("/designs/{{id}}")
    public String list(Cache data,
                       @Variable Long id) throws Exception{
        return designService.list(id, data);
    }

    @Post("/designs/save")
    public String save(HttpServletRequest req){
        return designService.save(req);
    }

    @Get("/designs/edit/{{id}}")
    public String showcase(Cache data,
                           @Variable Long id) throws Exception {
        return designService.edit(id, data);
    }

    @Post("/designs/update/{{id}}")
    public String update(HttpServletRequest req,
                         Cache data,
                         @Variable Long id){
        return designService.update(id, data, req);
    }

    @Post("/designs/delete/{{id}}")
    public String delete(Cache data,
                         @Variable Long id){
        return designService.delete(id, data);
    }

}
