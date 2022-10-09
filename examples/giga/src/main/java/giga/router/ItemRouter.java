package giga.router;

import giga.service.ItemService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpRequest;
import qio.annotate.HttpRouter;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

import java.io.IOException;

@Router
public class ItemRouter {

    @Inject
    ItemService itemService;

    @Get("/query/{{businessId}}")
    public String configure(HttpRequest req,
                            Cache cache,
                            @RouteComponent Long id){
        return itemService.query(id, cache, req);
    }


    @Get("/{{business}}/items/{{id}}")
    public String getItem(HttpRequest req,
                          Cache cache,
                          @RouteComponent String business,
                          @RouteComponent Long id){
        return itemService.getItem(id, business, cache, req);
    }

    @Get("/{{business}}/items/{{categoryId}}/{{id}}")
    public String getItem(HttpRequest req,
                          Cache cache,
                          @RouteComponent String business,
                          @RouteComponent Long categoryId,
                          @RouteComponent Long id){
        return itemService.getItemCategory(id, categoryId, business, cache, req);
    }

    @Get("/items/new/{{businessId}}")
    public String configure(Cache cache,
                            @RouteComponent Long businessId){
        return itemService.create(businessId, data);
    }

    @Get("/items/{{businessId}}")
    public String list(Cache cache,
                       @RouteComponent Long businessId){
        return itemService.list(businessId, data);
    }

    @Get("/items/inactive/{{businessId}}")
    public String getListInactive(Cache cache,
                       @RouteComponent Long businessId){
        return itemService.getListInactive(businessId, data);
    }

    @Get("/items/grid/{{businessId}}")
    public String grid(Cache cache,
                       @RouteComponent Long businessId){
        return itemService.grid(businessId, data);
    }

    @Post("/items/save/{{businessId}}")
    public String save(HttpRequest req,
                        @RouteComponent Long businessId) throws IOException, ServletException {
        return itemService.save(businessId, req);
    }

    @Get("/items/edit/{{businessId}}/{{id}}")
    public String showcase(Cache cache,
                           @RouteComponent Long businessId,
                           @RouteComponent Long id) throws Exception {
        return itemService.edit(id, businessId, data);
    }

    @Post("/items/update/{{businessId}}/{{id}}")
    public String update(HttpRequest req,
                         Cache cache,
                         @RouteComponent Long businessId,
                         @RouteComponent Long id) throws IOException, ServletException{
        return itemService.update(id, businessId, false, cache, req);
    }

    @Post("/items/grid/update/{{businessId}}/{{id}}")
    public String gridUpdate(HttpRequest req,
                         Cache cache,
                         @RouteComponent Long businessId,
                         @RouteComponent Long id) throws IOException, ServletException{
        return itemService.update(id, businessId, true, cache, req);
    }

    @Post("/items/delete/{{businessId}}/{{id}}")
    public String delete(Cache cache,
                         @RouteComponent Long businessId,
                         @RouteComponent Long id){
        return itemService.delete(id, businessId, data);
    }

    @Get("/items/options/{{businessId}}/{{id}}")
    public String options(Cache cache,
                           @RouteComponent Long businessId,
                           @RouteComponent Long id) throws Exception {
        return itemService.options(id, businessId, data);
    }

    @Get("/items/options/save/{{businessId}}/{{id}}")
    public String getOptions(Cache cache,
                          @RouteComponent Long businessId,
                          @RouteComponent Long id) throws Exception {
        return itemService.options(id, businessId, data);
    }

    @Post("/items/options/save/{{businessId}}/{{id}}")
    public String saveOption(HttpRequest req,
                             Cache cache,
                             @RouteComponent Long businessId,
                             @RouteComponent Long id){
        return itemService.saveOption(id, businessId, cache, req);
    }

    @Post("/items/options/delete/{{businessId}}/{{optionId}}/{{id}}")
    public String deleteOption(HttpRequest req,
                             Cache cache,
                             @RouteComponent Long businessId,
                             @RouteComponent Long optionId,
                             @RouteComponent Long id){
        return itemService.deleteOption(id, optionId, businessId, cache, req);
    }

    @Get("/items/options/delete/{{businessId}}/{{optionId}}/{{id}}")
    public String deleteOption(Cache cache,
                               @RouteComponent Long businessId,
                               @RouteComponent Long id){
        return itemService.options(id, businessId, data);
    }

    @Post("/items/options/values/save/{{businessId}}/{{id}}")
    public String saveValue(HttpRequest req,
                            Cache cache,
                             @RouteComponent Long businessId,
                             @RouteComponent Long id){
        return itemService.saveValue(id, businessId, cache, req);
    }


    @Get("/items/options/values/save/{{businessId}}/{{id}}")
    public String getValues(Cache cache,
                            @RouteComponent Long businessId,
                            @RouteComponent Long id){
        return itemService.options(id, businessId, data);
    }

    @Post("/items/options/values/delete/{{businessId}}/{{valueId}}/{{id}}")
    public String deleteValue(HttpRequest req,
                             Cache cache,
                             @RouteComponent Long businessId,
                             @RouteComponent Long valueId,
                             @RouteComponent Long id){
        return itemService.deleteValue(id, valueId, businessId, cache, req);
    }

    @Get("/items/options/values/delete/{{businessId}}/{{valueId}}/{{id}}")
    public String deleteValue(Cache cache,
                              @RouteComponent Long businessId,
                              @RouteComponent Long id){
        return itemService.options(id, businessId, data);
    }

}
