package giga.router;

import giga.Giga;
import giga.model.*;
import giga.service.CategoryService;
import jakarta.servlet.http.HttpRequest;
import qio.annotate.HttpRouter;
import qio.annotate.Inject;
import qio.annotate.Variable;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

import java.util.ArrayList;
import java.util.List;

@HttpRouter
public class CategoryRouter {

    @Inject
    CategoryService categoryService;

    @Get("/{{business}}/{{category}}/items")
    public String getPage(HttpRequest req,
                          Cache cache,
                          @RouteComponent String business,
                          @RouteComponent String category){
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        Category category = categoryRepo.get(categoryUri, business.getId());
        if(category == null){
            return "[redirect]/" + businessUri + "/asset/home";
        }

        List<Item> items = new ArrayList<>();
        List<CategoryItem> categoryItems = itemRepo.getListItems(category.getId(), business.getId());
        for(CategoryItem categoryItem : categoryItems){
            Item item = itemRepo.get(categoryItem.getItemId());
            items.add(item);
        }

        System.out.println("category " + category);
        cache.set("siteService", siteService);
        cache.set("business", business);
        cache.set("category", category);
        cache.set("items", items);
        cache.set("request", req);

        return "/pages/category/index.jsp";
    }

    @Get("/categories/new/{{businessId}}")
    public String configure(Cache cache,
                            @RouteComponent Long businessId){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

        List<Category> categories = categoryRepo.getListAll(businessId);
        cache.set("categories", categories);

        List<Design> designs = designRepo.getList(businessId);
        cache.set("designs", designs);

        List<Asset> assets = assetRepo.getList(businessId);
        cache.set("assets", assets);

        cache.set("page", "/pages/category/new.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/categories/{{businessId}}")
    public String list(Cache cache,
                       @RouteComponent Long businessId) throws Exception{
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

        List<Category> categories = categoryRepo.getListAll(businessId);
        cache.set("categories", categories);
        cache.set("page", "/pages/category/list.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/categories/save")
    public String save(HttpRequest req){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        User authUser = authService.getUser();

        Category category = (Category) Qio.get(req, Category.class);
        if(category.getCategoryId() == null) {
            category.setTopLevel(true);
        }else{
            category.setTopLevel(false);
        }
        categoryRepo.save(category);

        Category savedCategory = categoryRepo.getSaved();
        String permission = Giga.CATEGORY_MAINTENANCE + savedCategory.getId();
        userRepo.savePermission(authUser.getId(), permission);

        return "[redirect]/categories/" + savedCategory.getBusinessId();
    }

    @Get("/categories/edit/{{businessId}}/{{id}}")
    public String showcase(Cache cache,
                           @RouteComponent Long businessId,
                           @RouteComponent Long id) throws Exception {
        return categoryService.edit(id, businessId, data);
    }

    @Post("/categories/update/{{businessId}}/{{id}}")
    public String update(HttpRequest req,
                         Cache cache,
                         @RouteComponent Long businessId,
                         @RouteComponent Long id){
        return categoryService.update(id, businessId, cache, req);
    }

    @Post("/categories/delete/{{businessId}}/{{id}}")
    public String delete(Cache cache,
                         @RouteComponent Long businessId,
                         @RouteComponent Long id){
        return categoryService.delete(id, businessId, data);
    }
}
