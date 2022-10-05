package io.service;

import io.Giga;
import io.model.*;
import io.repo.*;
import jakarta.servlet.http.HttpServletRequest;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Service;
import qio.model.web.ResponseData;

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



    public String getItems(String businessUri, String categoryUri, ResponseData data, HttpServletRequest req) {
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
        data.set("siteService", siteService);
        data.set("business", business);
        data.set("category", category);
        data.set("items", items);
        data.set("request", req);

        return "/pages/category/index.jsp";
    }

    public String create(Long businessId, ResponseData data){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, data);

        List<Category> categories = categoryRepo.getListAll(businessId);
        data.set("categories", categories);

        List<Design> designs = designRepo.getList(businessId);
        data.set("designs", designs);

        List<Asset> assets = assetRepo.getList(businessId);
        data.set("assets", assets);

        data.set("page", "/pages/category/new.jsp");
        return "/designs/auth.jsp";
    }


    public String save(HttpServletRequest req) {
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


    public String list(Long businessId, ResponseData data) throws Exception {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, data);

        List<Category> categories = categoryRepo.getListAll(businessId);
        data.set("categories", categories);
        data.set("page", "/pages/category/list.jsp");
        return "/designs/auth.jsp";
    }

    public String edit(Long id, Long businessId, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.CATEGORY_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to edit this category.");
            return "[redirect]/";
        }
        Category category = categoryRepo.get(id);
        data.set("category", category);

        List<Category> categoriesPre = categoryRepo.getListAll(businessId);
        List<Category> categories = new ArrayList<>();
        for(Category activeCategory : categoriesPre){
            if(activeCategory.getId() != category.getId()){
                categories.add(activeCategory);
            }
        }
        data.set("categories", categories);

        List<Design> designs = designRepo.getList(businessId);
        data.set("designs", designs);

        List<Asset> assets = assetRepo.getList(businessId);
        data.set("assets", assets);

        businessService.setData(businessId, data);
        data.set("page", "/pages/category/edit.jsp");
        return "/designs/auth.jsp";
    }


    public String update(Long id, Long businessId, ResponseData data, HttpServletRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.CATEGORY_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to edit this design.");
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



    public String delete(Long id, Long businessId, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.CATEGORY_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to delete this category.");
            return "[redirect]/";
        }

        categoryRepo.delete(id);
        data.set("message", "Successfully deleted category.");

        return "[redirect]/categories/" + businessId;
    }
}
