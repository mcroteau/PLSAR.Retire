package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import jakarta.servlet.http.HttpRequest;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Service;
import qio.model.web.Cache;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

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
    AuthService authService;

    @Inject
    SiteService siteService;

    @Inject
    BusinessService businessService;

    @Inject
    AssetRepo assetRepo;



    public String getItems(String businessUri, String categoryUri, Cache cache, HttpRequest req) {
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

    public String create(Long businessId, Cache cache){
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


    public String save(HttpRequest req) {
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


    public String list(Long businessId, Cache cache) throws Exception {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

        List<Category> categories = categoryRepo.getListAll(businessId);
        cache.set("categories", categories);
        cache.set("page", "/pages/category/list.jsp");
        return "/designs/auth.jsp";
    }

    public String edit(Long id, Long businessId, Cache cache) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.CATEGORY_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
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


    public String update(Long id, Long businessId, Cache cache, HttpRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.CATEGORY_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to edit this design.");
            return "[redirect]/";
        }

        Category category = (Category) Qio.get(req, Category.class);
        if(category.getCategoryId() == null) {
            category.setTopLevel(true);
        }else{
            category.setTopLevel(false);
        }
        categoryRepo.update(category);

        return "[redirect]/categories/edit/" + businessId + "/" + id;
    }



    public String delete(Long id, Long businessId, Cache cache) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.CATEGORY_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to delete this category.");
            return "[redirect]/";
        }

        categoryRepo.delete(id);
        cache.set("message", "Successfully deleted category.");

        return "[redirect]/categories/" + businessId;
    }
}
