package giga.router;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import giga.service.BusinessService;
import giga.service.SeaService;
import giga.service.SiteService;
import net.plsar.RouteAttributes;
import net.plsar.annotations.Component;
import net.plsar.annotations.HttpRouter;
import net.plsar.annotations.Inject;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.Cache;
import net.plsar.model.FileComponent;
import net.plsar.model.HttpRequest;
import net.plsar.model.RequestComponent;
import net.plsar.security.SecurityManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@HttpRouter
public class ItemRouter {

    @Inject
    ItemRepo itemRepo;

    @Inject
    AssetRepo assetRepo;

    @Inject
    UserRepo userRepo;

    @Inject
    CategoryRepo categoryRepo;

    @Inject
    DesignRepo designRepo;

    @Inject
    BusinessRepo businessRepo;

    BusinessService businessService;

    public ItemRouter(){
        this.businessService = new BusinessService();
    }

    @Get("/query/{businessId}")
    public String configure(Cache cache,
                            HttpRequest req,
                            SecurityManager security,
                            @Component Long id){
        String q = req.getValue("q");

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        Business business = businessRepo.get(id);
        List<Item> items = itemRepo.q(q, id);
        cache.set("q", q);
        cache.set("items", items);
        cache.set("siteService", siteService);
        cache.set("business", business);
        cache.set("items", items);
        cache.set("request", req);
        return "/pages/item/q.jsp";
    }


    @Get("/{business}/items/{id}")
    public String getItem(Cache cache,
                          HttpRequest req,
                          SecurityManager security,
                          @Component String businessUri,
                          @Component Long id){
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        Item item = itemRepo.get(id, business.getId());
        if(item == null){
            return "[redirect]/" + businessUri;
        }

        setData(id, cache);

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);

        cache.set("item", item);
        cache.set("business", business);
        cache.set("request", req);
        cache.set("siteService", siteService);

        return "/pages/item/index.jsp";
    }

    @Get("/{{business}}/items/{{categoryId}}/{{id}}")
    public String getItem(Cache cache,
                          HttpRequest req,
                          SecurityManager security,
                          @Component String businessUri,
                          @Component Long categoryId,
                          @Component Long id){
        System.out.println(id + " : " + categoryId + " : " + businessUri);
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        Item item = itemRepo.get(id, business.getId());
        if(item == null){
            return "[redirect]/" + businessUri;
        }

        Category category = categoryRepo.get(categoryId);
        if(category == null){
            return "[redirect]/" + businessUri;
        }

        setData(id, cache);
        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);

        cache.set("item", item);
        cache.set("category", category);
        cache.set("business", business);
        cache.set("request", req);
        cache.set("siteService", siteService);

        return "/pages/item/index.jsp";
    }

    @Get("/items/new/{businessId}")
    public String configureSuper(Cache cache,
                                HttpRequest req,
                                SecurityManager security,
                                @Component Long businessId){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        businessService.setData(businessId, cache, authUser, businessRepo, siteService);

        List<Category> categories = categoryRepo.getListAll(businessId);
        if(categories.size() == 0){
            cache.set("message", "You have to walk before you can run... you need to create a category before you can continue.");
            return "[redirect]/categories/new/" + businessId;
        }
        cache.set("categories", categories);

        List<Design> designs = designRepo.getList(businessId);
        cache.set("designs", designs);

        List<Asset> assets = assetRepo.getList(businessId);
        cache.set("assets", assets);

        cache.set("page", "/pages/item/new.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/items/{businessId}")
    public String list(Cache cache,
                       HttpRequest req,
                       SecurityManager security,
                       @Component Long businessId){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }


        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        businessService.setData(businessId, cache, authUser, businessRepo, siteService);

        List<Item> items = itemRepo.getList(businessId);

        cache.set("items", items);
        cache.set("title", "Active Items");
        cache.set("siteService", siteService);
        cache.set("page", "/pages/item/list.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/items/inactive/{{businessId}}")
    public String getListInactive(Cache cache,
                                  HttpRequest req,
                                  SecurityManager security,
                                  @Component Long businessId){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }


        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        businessService.setData(businessId, cache, authUser, businessRepo, siteService);

        List<Item> items = itemRepo.getList(businessId, false);

        cache.set("items", items);
        cache.set("title", "Inactive Items");
        cache.set("siteService", siteService);
        cache.set("page", "/pages/item/list.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/items/grid/{{businessId}}")
    public String grid(Cache cache,
                       HttpRequest req,
                       SecurityManager security,
                       @Component Long businessId){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        businessService.setData(businessId, cache, authUser, businessRepo, siteService);

        List<Item> items = itemRepo.getList(businessId);
        for(Item item : items){
            List<Category> categories = new ArrayList<>();
            List<CategoryItem> categoryItems = categoryRepo.getCategoryItems(item.getId());
            for(CategoryItem categoryItem : categoryItems){
                Category category = categoryRepo.get(categoryItem.getCategoryId());
                categories.add(category);
            }
            item.setCategories(categories);
        }
        List<Design> designs = designRepo.getList(businessId);
        List<Category> categories = categoryRepo.getListAll(businessId);

        cache.set("designs", designs);
        cache.set("categories", categories);
        cache.set("items", items);
        cache.set("siteService", siteService);
        cache.set("page", "/pages/item/grid.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/items/save/{{businessId}}")
    public String save(HttpRequest req,
                       SecurityManager security,
                       @Component Long businessId) throws IOException {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        Item item = (Item) req.inflect(Item.class);
        RequestComponent requestComponent = req.getRequestComponent("media");
        List<FileComponent> fileComponents = requestComponent.getFileComponents();

        SeaService seaService = new SeaService();
        RouteAttributes routeAttributes = req.getRouteAttributes();
        String key = (String) routeAttributes .get("storage.key");
        String secret = (String) routeAttributes .get("storage.secret");

        for (FileComponent fileComponent : fileComponents) {
            String original = fileComponent.getFileName();
            InputStream is = new ByteArrayInputStream(fileComponent.getFileBytes());
            String ext = Giga.getExt(original);
            String name = Giga.getString(9) + "." + ext;
            seaService.send(key, secret, name, is);
            item.setImageUri(Giga.OCEAN_ENDPOINT + name);
        }

        if(item.getImageUri() == null){
            return "[redirect]/items/new/" + businessId;
        }

        item.setAffiliatePrice(item.getPrice());
        itemRepo.save(item);
        Item savedItem = itemRepo.getSaved();

        RequestComponent categoriesComponent = req.getRequestComponent("categories");
        List<String> categories = categoriesComponent.getValues();

        System.out.println("a: " + categories);
        for(String id : categories){
            CategoryItem categoryItem = new CategoryItem(savedItem.getId(), Long.valueOf(id.trim()), businessId);
            categoryRepo.saveItem(categoryItem);
        }

        String permission = Giga.ITEM_MAINTENANCE + savedItem.getId();
        userRepo.savePermission(authUser.getId(), permission);

        return "[redirect]/items/" + savedItem.getBusinessId();
    }

    @Get("/items/edit/{{businessId}}/{{id}}")
    public String showcase(Cache cache,
                           HttpRequest req,
                           SecurityManager security,
                           @Component Long businessId,
                           @Component Long id) {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this item.");
            return "[redirect]/";
        }


        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        businessService.setData(businessId, cache, authUser, businessRepo, siteService);

        Item item = itemRepo.get(id);

        cache.set("item", item);

        List<Category> categories = categoryRepo.getListAll(businessId);
        cache.set("categories", categories);

        List<CategoryItem> categoryItems = categoryRepo.getCategoryItems(item.getId());
        List<Category> activeCategories = new ArrayList<>();
        for(CategoryItem categoryItem : categoryItems){
            Category category = categoryRepo.get(categoryItem.getCategoryId());
            activeCategories.add(category);
        }
        cache.set("activeCategories", activeCategories);

        if(categoryItems != null &&
                categoryItems.size() > 0){
            CategoryItem categoryItem = categoryItems.get(0);
            cache.set("categoryId", categoryItem.getCategoryId());
        }

        List<Design> designs = designRepo.getList(businessId);
        cache.set("designs", designs);

        List<Asset> assets = assetRepo.getList(businessId);
        cache.set("assets", assets);

        cache.set("page", "/pages/item/edit.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/items/update/{{businessId}}/{{id}}")
    public String update(Cache cache,
                         HttpRequest req,
                         SecurityManager security,
                         @Component Long businessId,
                         @Component Long id) throws IOException{
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        Business business = businessRepo.get(businessId);

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this design.");
            return "[redirect]/";
        }

        Item item = (Item) req.inflect(Item.class);

        if(req.getValue("media") != null){
            RequestComponent requestComponent = req.getRequestComponent("media");
            List<FileComponent> fileComponents = requestComponent.getFileComponents();

            SeaService seaService = new SeaService();
            RouteAttributes routeAttributes = req.getRouteAttributes();
            String key = (String) routeAttributes .get("storage.key");
            String secret = (String) routeAttributes .get("storage.secret");

            for (FileComponent fileComponent : fileComponents) {
                String original = fileComponent.getFileName();
                InputStream is = new ByteArrayInputStream(fileComponent.getFileBytes());
                String ext = Giga.getExt(original);
                String name = Giga.getString(9) + "." + ext;
                seaService.send(key, secret, name, is);
                item.setImageUri(Giga.OCEAN_ENDPOINT + name);
            }
        }

        categoryRepo.deleteCategoryItems(id);

        RequestComponent categoriesComponent = req.getRequestComponent("categories");
        List<String> categories = categoriesComponent.getValues();
        if(categories != null) {
            for (String categoryId : categories) {
                CategoryItem categoryItem = new CategoryItem(item.getId(), Long.valueOf(categoryId.trim()), businessId);
                categoryRepo.saveItem(categoryItem);
            }
        }

        if(business.getAffiliate() != null &&
                business.getAffiliate() &&
                (item.getAffiliatePrice().compareTo(item.getPrice()) == 1)){
            cache.set("message", "Your price may not be lower than the business owners.");
            return "[redirect]/items/edit/" + businessId + "/" + id;
        }

        itemRepo.update(item);
        cache.set("message", "Successfully updated item");
        return "[redirect]/items/edit/" + businessId + "/" + id;
    }

    @Post("/items/grid/update/{{businessId}}/{{id}}")
    public String gridUpdate(Cache cache,
                             HttpRequest req,
                             SecurityManager security,
                             @Component Long businessId,
                             @Component Long id) {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        Business business = businessRepo.get(businessId);

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this design.");
            return "[redirect]/";
        }

        Item item = (Item) req.inflect(Item.class);

        if(req.getValue("media") != null){
            RequestComponent requestComponent = req.getRequestComponent("media");
            List<FileComponent> fileComponents = requestComponent.getFileComponents();

            SeaService seaService = new SeaService();
            RouteAttributes routeAttributes = req.getRouteAttributes();
            String key = (String) routeAttributes .get("storage.key");
            String secret = (String) routeAttributes .get("storage.secret");

            for (FileComponent fileComponent : fileComponents) {
                String original = fileComponent.getFileName();
                InputStream is = new ByteArrayInputStream(fileComponent.getFileBytes());
                String ext = Giga.getExt(original);
                String name = Giga.getString(9) + "." + ext;
                seaService.send(key, secret, name, is);
                item.setImageUri(Giga.OCEAN_ENDPOINT + name);
            }
        }

        categoryRepo.deleteCategoryItems(id);


        RequestComponent categoriesComponent = req.getRequestComponent("categories");
        List<String> categories = categoriesComponent.getValues();
        if(categories != null) {
            for (String categoryId : categories) {
                CategoryItem categoryItem = new CategoryItem(item.getId(), Long.valueOf(categoryId.trim()), businessId);
                categoryRepo.saveItem(categoryItem);
            }
        }

        if(business.getAffiliate() != null &&
                business.getAffiliate() &&
                (item.getAffiliatePrice().compareTo(item.getPrice()) == 1)){
            cache.set("message", "Your price may not be lower than the business owners.");
            return "[redirect]/items/grid/" + businessId;
        }

        itemRepo.update(item);
        cache.set("message", "Successfully updated item");

        return "[redirect]/items/grid/" + businessId;
    }

    @Post("/items/delete/{{businessId}}/{{id}}")
    public String delete(Cache cache,
                         HttpRequest req,
                         SecurityManager security,
                         @Component Long businessId,
                         @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        categoryRepo.deleteCategoryItems(id);
        List<ItemOption> itemOptions = itemRepo.getOptions(id);
        for(ItemOption itemOption : itemOptions){
            itemRepo.deleteValues(itemOption.getId());
        }
        itemRepo.deleteOptions(id);
        itemRepo.delete(id);
        cache.set("message", "Successfully deleted item.");

        return "[redirect]/items/" + businessId;
    }

    @Get("/items/options/{{businessId}}/{{id}}")
    public String options(Cache cache,
                          HttpRequest req,
                          SecurityManager security,
                          @Component Long businessId,
                          @Component Long id) throws Exception {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        setData(id, cache);

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        businessService.setData(businessId, cache, authUser, businessRepo, siteService);

        cache.set("page", "/pages/item/options.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/items/options/save/{{businessId}}/{{id}}")
    public String getOptions(Cache cache,
                             HttpRequest req,
                             SecurityManager security,
                             @Component Long businessId,
                             @Component Long id) throws Exception {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        setData(id, cache);

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        businessService.setData(businessId, cache, authUser, businessRepo, siteService);

        cache.set("page", "/pages/item/options.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/items/options/save/{{businessId}}/{{id}}")
    public String saveOption(Cache cache,
                             HttpRequest req,
                             SecurityManager security,
                             @Component Long businessId,
                             @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        ItemOption itemOption = (ItemOption) req.inflect(ItemOption.class);
        itemRepo.saveOption(itemOption);

        return "[redirect]/items/options/" + businessId + "/" + id;
    }

    @Post("/items/options/delete/{{businessId}}/{{optionId}}/{{id}}")
    public String deleteOption(Cache cache,
                               HttpRequest req,
                               SecurityManager security,
                               @Component Long businessId,
                               @Component Long optionId,
                               @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        System.out.println("delete option ");
        itemRepo.deleteValues(optionId);
        itemRepo.deleteOption(optionId);

        return "[redirect]/items/options/" + businessId + "/" + id;
    }

    @Get("/items/options/delete/{{businessId}}/{{optionId}}/{{id}}")
    public String deleteOption(Cache cache,
                               HttpRequest req,
                               SecurityManager security,
                               @Component Long businessId,
                               @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        setData(id, cache);

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        businessService.setData(businessId, cache, authUser, businessRepo, siteService);

        cache.set("page", "/pages/item/options.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/items/options/values/save/{{businessId}}/{{id}}")
    public String saveValue(Cache cache,
                            HttpRequest req,
                            SecurityManager security,
                            @Component Long businessId,
                            @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        OptionValue optionValue = (OptionValue) req.inflect(OptionValue.class);
        itemRepo.saveValue(optionValue);

        return "[redirect]/items/options/" + businessId + "/" + id;
    }


    @Get("/items/options/values/save/{{businessId}}/{{id}}")
    public String getValues(Cache cache,
                            HttpRequest req,
                            SecurityManager security,
                            @Component Long businessId,
                            @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        setData(id, cache);

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        businessService.setData(businessId, cache, authUser, businessRepo, siteService);

        cache.set("page", "/pages/item/options.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/items/options/values/delete/{{businessId}}/{{valueId}}/{{id}}")
    public String deleteValue(Cache cache,
                              HttpRequest req,
                              SecurityManager security,
                              @Component Long businessId,
                              @Component Long valueId,
                              @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        System.out.println("delete option value ");
        itemRepo.deleteValue(valueId);

        setData(id, cache);

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        businessService.setData(businessId, cache, authUser, businessRepo, siteService);

        return "[redirect]/items/options/" + businessId + "/" + id;
    }

    @Get("/items/options/values/delete/{{businessId}}/{{valueId}}/{{id}}")
    public String deleteValue(Cache cache,
                              HttpRequest req,
                              SecurityManager security,
                              @Component Long businessId,
                              @Component Long id){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        setData(id, cache);

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        SiteService siteService = new SiteService(security, designRepo, userRepo, categoryRepo);
        businessService.setData(businessId, cache, authUser, businessRepo, siteService);

        cache.set("page", "/pages/item/options.jsp");
        return "/designs/auth.jsp";
    }


    public void setData(Long id, Cache cache){
        Item item = itemRepo.get(id);
        List<ItemOption> itemOptions = itemRepo.getOptions(item.getId());
        for(ItemOption itemOption : itemOptions){
            List<OptionValue> optionValues = itemRepo.getValues(itemOption.getId());
            itemOption.setOptionValues(optionValues);
        }
        cache.set("item", item);
        cache.set("itemOptions", itemOptions);
    }

}
