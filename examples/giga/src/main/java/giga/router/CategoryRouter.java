package giga.router;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import giga.service.BusinessService;
import giga.service.SiteService;
import net.plsar.annotations.Component;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Inject;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;
import net.plsar.model.HttpRequest;
import net.plsar.security.SecurityManager;

import java.util.ArrayList;
import java.util.List;

@HttpRouter
public class CategoryRouter {

    @Inject
    UserRepo userRepo;

    @Inject
    ItemRepo itemRepo;

    @Inject
    DesignRepo designRepo;

    @Inject
    CategoryRepo categoryRepo;

    @Inject
    BusinessRepo businessRepo;

    @Inject
    AssetRepo assetRepo;

    BusinessService businessService;

    public CategoryRouter(){
        this.businessService = new BusinessService();
    }

    @Get("/{{business}}/{{category}}/items")
    public String getPage(Cache cache,
                          HttpRequest req,
                          SecurityManager security,
                          @Component String businessUri,
                          @Component String categoryUri){
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

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);

        cache.set("siteService", siteService);
        cache.set("business", business);
        cache.set("category", category);
        cache.set("items", items);
        cache.set("request", req);

        return "/pages/category/index.jsp";
    }

    @Get("/categories/new/{{businessId}}")
    public String configure(Cache cache,
                            HttpRequest req,
                            SecurityManager security,
                            @Component Long businessId){
        if(!security.isAuthenticated(req)){
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
                       HttpRequest req,
                       SecurityManager security,
                       @Component Long businessId) throws Exception{
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

        List<Category> categories = categoryRepo.getListAll(businessId);
        cache.set("categories", categories);
        cache.set("page", "/pages/category/list.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/categories/save")
    public String save(HttpRequest req,
                       SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        Category category = (Category) req.inflect(req, Category.class);
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
                           HttpRequest req,
                           SecurityManager security,
                           @Component Long businessId,
                           @Component Long id) throws Exception {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.CATEGORY_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this category.");
            return "[redirect]/";
        }
        Category category = categoryRepo.get(id);
        cache.set("category", category);

        List<Category> categoriesPre = categoryRepo.getListAll(businessId);
        List<Category> categories = new ArrayList<>();
        for(Category activeCategory : categoriesPre){
            if(activeCategory.getId() != category.getId()){
                categories.add(activeCategory);
            }
        }
        cache.set("categories", categories);

        List<Design> designs = designRepo.getList(businessId);
        cache.set("designs", designs);

        List<Asset> assets = assetRepo.getList(businessId);
        cache.set("assets", assets);

        businessService.setData(businessId, cache);
        cache.set("page", "/pages/category/edit.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/categories/update/{{businessId}}/{{id}}")
    public String update(Cache cache,
                         HttpRequest req,
                         SecurityManager security,
                         @Component Long businessId,
                         @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.CATEGORY_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this design.");
            return "[redirect]/";
        }

        Category category = (Category) req.inflect(req, Category.class);
        if(category.getCategoryId() == null) {
            category.setTopLevel(true);
        }else{
            category.setTopLevel(false);
        }
        categoryRepo.update(category);

        return "[redirect]/categories/edit/" + businessId + "/" + id;
    }

    @Post("/categories/delete/{{businessId}}/{{id}}")
    public String delete(Cache cache,
                         HttpRequest req,
                         SecurityManager security,
                         @Component Long businessId,
                         @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.CATEGORY_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this category.");
            return "[redirect]/";
        }

        categoryRepo.delete(id);
        cache.set("message", "Successfully deleted category.");

        return "[redirect]/categories/" + businessId;
    }
}
