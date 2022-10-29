package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import net.plsar.annotations.Bind;
import net.plsar.annotations.Service;
import net.plsar.model.Cache;
import net.plsar.model.NetworkRequest;
import net.plsar.security.SecurityManager;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Bind
    ItemRepo itemRepo;

    @Bind
    UserRepo userRepo;

    @Bind
    DesignRepo designRepo;

    @Bind
    CategoryRepo categoryRepo;

    @Bind
    BusinessRepo businessRepo;

    @Bind
    AssetRepo assetRepo;

    BusinessService businessService;

    public CategoryService(){
        this.businessService = new BusinessService();
    }

    public String getItems(String businessUri, String categoryUri, Cache cache, NetworkRequest req, SecurityManager security) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "redirect:/home";
        }

        Category category = categoryRepo.get(categoryUri, business.getId());
        if(category == null){
            return "redirect:/" + businessUri + "/asset/home";
        }

        List<Item> items = new ArrayList<>();
        List<ItemCategory> itemCategories = itemRepo.getListItems(category.getId(), business.getId());
        for(ItemCategory itemCategory : itemCategories){
            Item item = itemRepo.get(itemCategory.getItemId());
            items.add(item);
        }

        SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);

        cache.set("siteService", siteService);
        cache.set("business", business);
        cache.set("category", category);
        cache.set("items", items);
        cache.set("request", req);

        return "/pages/category/index.jsp";
    }

    public String create(Long businessId, Cache cache, NetworkRequest req, SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        List<Category> categories = categoryRepo.getListAll(businessId);
        cache.set("categories", categories);

        List<Design> designs = designRepo.getList(businessId);
        cache.set("designs", designs);

        List<Asset> assets = assetRepo.getList(businessId);
        cache.set("assets", assets);

        return "/pages/category/new.jsp";
    }


    public String save(NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.getPhone(credential);
        if(authUser == null){
            authUser = userRepo.getEmail(credential);
        }

        Category category = (Category) req.inflect(Category.class);
        if(category.getCategoryId() == null) {
            category.setTopLevel(true);
        }else{
            category.setTopLevel(false);
        }
        categoryRepo.save(category);

        Category savedCategory = categoryRepo.getSaved();
        String permission = Giga.CATEGORY_MAINTENANCE + savedCategory.getId();
        userRepo.savePermission(authUser.getId(), permission);

        return "redirect:/categories/" + savedCategory.getBusinessId();
    }


    public String list(Long businessId, Cache cache, NetworkRequest req, SecurityManager security) throws Exception {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        List<Category> categories = categoryRepo.getListAll(businessId);
        cache.set("categories", categories);
        return "/pages/category/list.jsp";
    }

    public String edit(Long id, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.CATEGORY_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this category.");
            return "redirect:/";
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

        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);
        return "/pages/category/edit.jsp";
    }


    public String update(Long id, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.CATEGORY_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this design.");
            return "redirect:/";
        }

        Category category = (Category) req.inflect(Category.class);
        if(category.getCategoryId() == null) {
            category.setTopLevel(true);
        }else{
            category.setTopLevel(false);
        }
        categoryRepo.update(category);

        return "redirect:/categories/edit/" + businessId + "/" + id;
    }



    public String delete(Long id, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.CATEGORY_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this category.");
            return "redirect:/";
        }

        categoryRepo.delete(id);
        cache.set("message", "Successfully deleted category.");

        return "redirect:/categories/" + businessId;
    }
}
