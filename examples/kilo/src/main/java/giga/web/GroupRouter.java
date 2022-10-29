package giga.web;

import giga.service.GroupService;
import net.plsar.annotations.Component;
import net.plsar.annotations.Design;
import net.plsar.annotations.Controller;
import net.plsar.annotations.Bind;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;
import net.plsar.model.NetworkRequest;
import net.plsar.security.SecurityManager;

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
