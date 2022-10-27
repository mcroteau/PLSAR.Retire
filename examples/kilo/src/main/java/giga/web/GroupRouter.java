package giga.web;

import giga.service.GroupService;
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
public class GroupRouter {

    @Bind
    GroupService groupService;

    @Design("/designs/auth.jsp")
    @Get("/{businessId}/groups/options/create")
    public String createOptions(Cache cache,
                                NetworkRequest req,
                                SecurityManager security,
                                @Component Long businessId){
        return groupService.createOptions(businessId, cache, req, security);
    }

    @Post("/{businessId}/groups/options/save")
    public String saveOption(Cache cache,
                             NetworkRequest req,
                             SecurityManager security,
                             @Component Long businessId){
        return groupService.saveOption(businessId, cache, req, security);
    }

}
