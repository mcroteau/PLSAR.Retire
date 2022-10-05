package io.web;

import io.service.ItemService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.ResponseData;

import java.io.IOException;

@HttpHandler
public class ItemHandler {

    @Inject
    ItemService itemService;


    @Get("/query/{{businessId}}")
    public String configure(HttpServletRequest req,
                            ResponseData data,
                            @Variable Long id){
        return itemService.query(id, data, req);
    }


    @Get("/{{business}}/items/{{id}}")
    public String getItem(HttpServletRequest req,
                          ResponseData data,
                          @Variable String business,
                          @Variable Long id){
        return itemService.getItem(id, business, data, req);
    }

    @Get("/{{business}}/items/{{categoryId}}/{{id}}")
    public String getItem(HttpServletRequest req,
                          ResponseData data,
                          @Variable String business,
                          @Variable Long categoryId,
                          @Variable Long id){
        return itemService.getItemCategory(id, categoryId, business, data, req);
    }

    @Get("/items/new/{{businessId}}")
    public String configure(ResponseData data,
                            @Variable Long businessId){
        return itemService.create(businessId, data);
    }

    @Get("/items/{{businessId}}")
    public String list(ResponseData data,
                       @Variable Long businessId){
        return itemService.list(businessId, data);
    }

    @Get("/items/inactive/{{businessId}}")
    public String getListInactive(ResponseData data,
                       @Variable Long businessId){
        return itemService.getListInactive(businessId, data);
    }

    @Get("/items/grid/{{businessId}}")
    public String grid(ResponseData data,
                       @Variable Long businessId){
        return itemService.grid(businessId, data);
    }

    @Post("/items/save/{{businessId}}")
    public String save(HttpServletRequest req,
                        @Variable Long businessId) throws IOException, ServletException {
        return itemService.save(businessId, req);
    }

    @Get("/items/edit/{{businessId}}/{{id}}")
    public String showcase(ResponseData data,
                           @Variable Long businessId,
                           @Variable Long id) throws Exception {
        return itemService.edit(id, businessId, data);
    }

    @Post("/items/update/{{businessId}}/{{id}}")
    public String update(HttpServletRequest req,
                         ResponseData data,
                         @Variable Long businessId,
                         @Variable Long id) throws IOException, ServletException{
        return itemService.update(id, businessId, false, data, req);
    }

    @Post("/items/grid/update/{{businessId}}/{{id}}")
    public String gridUpdate(HttpServletRequest req,
                         ResponseData data,
                         @Variable Long businessId,
                         @Variable Long id) throws IOException, ServletException{
        return itemService.update(id, businessId, true, data, req);
    }

    @Post("/items/delete/{{businessId}}/{{id}}")
    public String delete(ResponseData data,
                         @Variable Long businessId,
                         @Variable Long id){
        return itemService.delete(id, businessId, data);
    }

    @Get("/items/options/{{businessId}}/{{id}}")
    public String options(ResponseData data,
                           @Variable Long businessId,
                           @Variable Long id) throws Exception {
        return itemService.options(id, businessId, data);
    }

    @Get("/items/options/save/{{businessId}}/{{id}}")
    public String getOptions(ResponseData data,
                          @Variable Long businessId,
                          @Variable Long id) throws Exception {
        return itemService.options(id, businessId, data);
    }

    @Post("/items/options/save/{{businessId}}/{{id}}")
    public String saveOption(HttpServletRequest req,
                             ResponseData data,
                             @Variable Long businessId,
                             @Variable Long id){
        return itemService.saveOption(id, businessId, data, req);
    }

    @Post("/items/options/delete/{{businessId}}/{{optionId}}/{{id}}")
    public String deleteOption(HttpServletRequest req,
                             ResponseData data,
                             @Variable Long businessId,
                             @Variable Long optionId,
                             @Variable Long id){
        return itemService.deleteOption(id, optionId, businessId, data, req);
    }

    @Get("/items/options/delete/{{businessId}}/{{optionId}}/{{id}}")
    public String deleteOption(ResponseData data,
                               @Variable Long businessId,
                               @Variable Long id){
        return itemService.options(id, businessId, data);
    }

    @Post("/items/options/values/save/{{businessId}}/{{id}}")
    public String saveValue(HttpServletRequest req,
                            ResponseData data,
                             @Variable Long businessId,
                             @Variable Long id){
        return itemService.saveValue(id, businessId, data, req);
    }


    @Get("/items/options/values/save/{{businessId}}/{{id}}")
    public String getValues(ResponseData data,
                            @Variable Long businessId,
                            @Variable Long id){
        return itemService.options(id, businessId, data);
    }

    @Post("/items/options/values/delete/{{businessId}}/{{valueId}}/{{id}}")
    public String deleteValue(HttpServletRequest req,
                             ResponseData data,
                             @Variable Long businessId,
                             @Variable Long valueId,
                             @Variable Long id){
        return itemService.deleteValue(id, valueId, businessId, data, req);
    }

    @Get("/items/options/values/delete/{{businessId}}/{{valueId}}/{{id}}")
    public String deleteValue(ResponseData data,
                              @Variable Long businessId,
                              @Variable Long id){
        return itemService.options(id, businessId, data);
    }

}
