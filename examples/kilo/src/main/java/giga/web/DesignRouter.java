package giga.web;

import giga.service.DesignService;
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
public class DesignRouter {

    @Bind
    DesignService designService;

    @Design("/designs/auth.jsp")
    @Get("/designs/new/{id}")
    public String configure(Cache cache,
                            NetworkRequest req,
                            SecurityManager security,
                            @Component Long id){
        return designService.configure(id, cache, req, security);
    }

    @Design("/designs/auth.jsp")
    @Get("/designs/{id}")
    public String list(Cache cache,
                       NetworkRequest req,
                       SecurityManager security,
                       @Component Long id) throws Exception{
        return designService.list(id, cache, req, security);
    }

    @Post("/designs/save")
    public String save(NetworkRequest req,
                       SecurityManager security){
        return designService.save(req, security);
    }

    @Design("/designs/auth.jsp")
    @Get("/designs/edit/{id}")
    public String showcase(Cache cache,
                           NetworkRequest req,
                           SecurityManager security,
                           @Component Long id) throws Exception {
        return designService.edit(id, cache, req, security);
    }

    @Post("/designs/update/{id}")
    public String update(Cache cache,
                         NetworkRequest req,
                         SecurityManager security,
                         @Component Long id){
        return designService.update(id, cache, req, security);
    }

    @Post("/designs/delete/{id}")
    public String delete(Cache cache,
                         NetworkRequest req,
                         SecurityManager security,
                         @Component Long id){
        return designService.delete(id, cache, req, security);
    }

}
