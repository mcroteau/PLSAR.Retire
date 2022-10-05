package io.web;

import io.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import qio.annotate.HttpHandler;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.ResponseData;

@HttpHandler
public class CategoryHandler {

    @Inject
    CategoryService categoryService;

    @Get("/{{business}}/{{category}}/items")
    public String getPage(HttpServletRequest req,
                          ResponseData data,
                          @Variable String business,
                          @Variable String category){
        return categoryService.getItems(business.toLowerCase(), category.toLowerCase(), data, req);
    }

    @Get("/categories/new/{{businessId}}")
    public String configure(ResponseData data,
                            @Variable Long businessId){
        return categoryService.create(businessId, data);
    }

    @Get("/categories/{{businessId}}")
    public String list(ResponseData data,
                       @Variable Long businessId) throws Exception{
        return categoryService.list(businessId, data);
    }

    @Post("/categories/save")
    public String save(HttpServletRequest req){
        return categoryService.save(req);
    }

    @Get("/categories/edit/{{businessId}}/{{id}}")
    public String showcase(ResponseData data,
                           @Variable Long businessId,
                           @Variable Long id) throws Exception {
        return categoryService.edit(id, businessId, data);
    }

    @Post("/categories/update/{{businessId}}/{{id}}")
    public String update(HttpServletRequest req,
                         ResponseData data,
                         @Variable Long businessId,
                         @Variable Long id){
        return categoryService.update(id, businessId, data, req);
    }

    @Post("/categories/delete/{{businessId}}/{{id}}")
    public String delete(ResponseData data,
                         @Variable Long businessId,
                         @Variable Long id){
        return categoryService.delete(id, businessId, data);
    }
}
