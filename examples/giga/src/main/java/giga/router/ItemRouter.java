package giga.router;

import giga.Giga;
import giga.model.*;
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
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Router
public class ItemRouter {

    @Inject
    ItemService itemService;

    @Get("/query/{{businessId}}")
    public String configure(HttpRequest req,
                            Cache cache,
                            @RouteComponent Long id){
        String q = req.getParameter("q");
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


    @Get("/{{business}}/items/{{id}}")
    public String getItem(HttpRequest req,
                          Cache cache,
                          @RouteComponent String business,
                          @RouteComponent Long id){
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        Item item = itemRepo.get(id, business.getId());
        if(item == null){
            return "[redirect]/" + businessUri;
        }

        setData(id, cache);

        cache.set("item", item);
        cache.set("business", business);
        cache.set("request", req);
        cache.set("siteService", siteService);

        return "/pages/item/index.jsp";
    }

    @Get("/{{business}}/items/{{categoryId}}/{{id}}")
    public String getItem(HttpRequest req,
                          Cache cache,
                          @RouteComponent String business,
                          @RouteComponent Long categoryId,
                          @RouteComponent Long id){
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

        cache.set("item", item);
        cache.set("category", category);
        cache.set("business", business);
        cache.set("request", req);
        cache.set("siteService", siteService);

        return "/pages/item/index.jsp";
    }

    @Get("/items/new/{{businessId}}")
    public String configure(Cache cache,
                            @RouteComponent Long businessId){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

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

    @Get("/items/{{businessId}}")
    public String list(Cache cache,
                       @RouteComponent Long businessId){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

        List<Item> items = itemRepo.getList(businessId);
        cache.set("items", items);
        cache.set("title", "Active Items");
        cache.set("siteService", siteService);
        cache.set("page", "/pages/item/list.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/items/inactive/{{businessId}}")
    public String getListInactive(Cache cache,
                       @RouteComponent Long businessId){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

        List<Item> items = itemRepo.getList(businessId, false);
        cache.set("items", items);
        cache.set("title", "Inactive Items");
        cache.set("siteService", siteService);
        cache.set("page", "/pages/item/list.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/items/grid/{{businessId}}")
    public String grid(Cache cache,
                       @RouteComponent Long businessId){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

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
                        @RouteComponent Long businessId) throws IOException, ServletException {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        User authUser = authService.getUser();

        Item item = (Item) Qio.get(req, Item.class);

        List<Part> fileParts = req.getParts()
                .stream()
                .filter(part -> "media".equals(part.getName()) && part.getSize() > 0)
                .collect(Collectors.toList());

        for (Part part : fileParts) {
            String original = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            InputStream is = part.getInputStream();
            String ext = Giga.getExt(original);
            String name = Giga.getString(9) + "." + ext;
            seaService.send(name, is);
            item.setImageUri(Giga.OCEAN_ENDPOINT + name);
        }

        if(item.getImageUri() == null){
            return "[redirect]/items/new/" + businessId;
        }

        item.setAffiliatePrice(item.getPrice());
        itemRepo.save(item);
        Item savedItem = itemRepo.getSaved();

        String[] categories = req.getParameterValues("categories");
        System.out.println("a " + req.getParameterValues("categories") + " : " + categories);
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
                           @RouteComponent Long businessId,
                           @RouteComponent Long id) throws Exception {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to edit this item.");
            return "[redirect]/";
        }

        businessService.setData(businessId, cache);

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
    public String update(HttpRequest req,
                         Cache cache,
                         @RouteComponent Long businessId,
                         @RouteComponent Long id) throws IOException, ServletException{
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        Business business = businessRepo.get(businessId);

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to edit this design.");
            return "[redirect]/";
        }

        Item item = (Item) Qio.get(req, Item.class);

        if(req.getParameter("media") != null){
            List<Part> fileParts = req.getParts()
                    .stream()
                    .filter(part -> "media".equals(part.getName()) && part.getSize() > 0)
                    .collect(Collectors.toList());

            for (Part part : fileParts) {
                String original = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                InputStream is = part.getInputStream();
                String ext = Giga.getExt(original);
                String name = Giga.getString(9) + "." + ext;
                seaService.send(name, is);
                item.setImageUri(Giga.OCEAN_ENDPOINT + name);
            }
        }

        categoryRepo.deleteCategoryItems(id);

        String[] categories = req.getParameterValues("categories");
        if(categories != null) {
            System.out.println("a " + req.getParameterValues("categories") + " : " + categories);
            for (String categoryId : categories) {
                CategoryItem categoryItem = new CategoryItem(item.getId(), Long.valueOf(categoryId.trim()), businessId);
                categoryRepo.saveItem(categoryItem);
            }
        }

        if(business.getAffiliate() != null &&
                business.getAffiliate() &&
                (item.getAffiliatePrice().compareTo(item.getPrice()) == 1)){
            cache.set("message", "Your price may not be lower than the business owners. I get it, you want to buy their products at a lower price. You're a genius. ; )");
            if(onGrid){
                return "[redirect]/items/grid/" + businessId;
            }
            return "[redirect]/items/edit/" + businessId + "/" + id;
        }

        itemRepo.update(item);
        cache.set("message", "Successfully updated item");

        if(onGrid){
            return "[redirect]/items/grid/" + businessId;
        }
        return "[redirect]/items/edit/" + businessId + "/" + id;
    }

    @Post("/items/grid/update/{{businessId}}/{{id}}")
    public String gridUpdate(HttpRequest req,
                         Cache cache,
                         @RouteComponent Long businessId,
                         @RouteComponent Long id) throws IOException, ServletException{
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        Business business = businessRepo.get(businessId);

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to edit this design.");
            return "[redirect]/";
        }

        Item item = (Item) Qio.get(req, Item.class);

        if(req.getParameter("media") != null){
            List<Part> fileParts = req.getParts()
                    .stream()
                    .filter(part -> "media".equals(part.getName()) && part.getSize() > 0)
                    .collect(Collectors.toList());

            for (Part part : fileParts) {
                String original = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                InputStream is = part.getInputStream();
                String ext = Giga.getExt(original);
                String name = Giga.getString(9) + "." + ext;
                seaService.send(name, is);
                item.setImageUri(Giga.OCEAN_ENDPOINT + name);
            }
        }

        categoryRepo.deleteCategoryItems(id);

        String[] categories = req.getParameterValues("categories");
        if(categories != null) {
            System.out.println("a " + req.getParameterValues("categories") + " : " + categories);
            for (String categoryId : categories) {
                CategoryItem categoryItem = new CategoryItem(item.getId(), Long.valueOf(categoryId.trim()), businessId);
                categoryRepo.saveItem(categoryItem);
            }
        }

        if(business.getAffiliate() != null &&
                business.getAffiliate() &&
                (item.getAffiliatePrice().compareTo(item.getPrice()) == 1)){
            cache.set("message", "Your price may not be lower than the business owners. I get it, you want to buy their products at a lower price. You're a genius. ; )");
            if(onGrid){
                return "[redirect]/items/grid/" + businessId;
            }
            return "[redirect]/items/edit/" + businessId + "/" + id;
        }

        itemRepo.update(item);
        cache.set("message", "Successfully updated item");

        if(onGrid){
            return "[redirect]/items/grid/" + businessId;
        }
        return "[redirect]/items/edit/" + businessId + "/" + id;
    }

    @Post("/items/delete/{{businessId}}/{{id}}")
    public String delete(Cache cache,
                         @RouteComponent Long businessId,
                         @RouteComponent Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
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
                           @RouteComponent Long businessId,
                           @RouteComponent Long id) throws Exception {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        setData(id, cache);
        businessService.setData(businessId, cache);

        cache.set("page", "/pages/item/options.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/items/options/save/{{businessId}}/{{id}}")
    public String getOptions(Cache cache,
                          @RouteComponent Long businessId,
                          @RouteComponent Long id) throws Exception {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        setData(id, cache);
        businessService.setData(businessId, cache);

        cache.set("page", "/pages/item/options.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/items/options/save/{{businessId}}/{{id}}")
    public String saveOption(HttpRequest req,
                             Cache cache,
                             @RouteComponent Long businessId,
                             @RouteComponent Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        ItemOption itemOption = (ItemOption) Qio.get(req, ItemOption.class);
        itemRepo.saveOption(itemOption);

        return "[redirect]/items/options/" + businessId + "/" + id;
    }

    @Post("/items/options/delete/{{businessId}}/{{optionId}}/{{id}}")
    public String deleteOption(HttpRequest req,
                             Cache cache,
                             @RouteComponent Long businessId,
                             @RouteComponent Long optionId,
                             @RouteComponent Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
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
                               @RouteComponent Long businessId,
                               @RouteComponent Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        setData(id, cache);
        businessService.setData(businessId, cache);

        cache.set("page", "/pages/item/options.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/items/options/values/save/{{businessId}}/{{id}}")
    public String saveValue(HttpRequest req,
                            Cache cache,
                             @RouteComponent Long businessId,
                             @RouteComponent Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        OptionValue optionValue = (OptionValue) Qio.get(req, OptionValue.class);
        itemRepo.saveValue(optionValue);

        return "[redirect]/items/options/" + businessId + "/" + id;
    }


    @Get("/items/options/values/save/{{businessId}}/{{id}}")
    public String getValues(Cache cache,
                            @RouteComponent Long businessId,
                            @RouteComponent Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        setData(id, cache);
        businessService.setData(businessId, cache);

        cache.set("page", "/pages/item/options.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/items/options/values/delete/{{businessId}}/{{valueId}}/{{id}}")
    public String deleteValue(HttpRequest req,
                             Cache cache,
                             @RouteComponent Long businessId,
                             @RouteComponent Long valueId,
                             @RouteComponent Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        System.out.println("delete option value ");
        itemRepo.deleteValue(valueId);

        setData(id, cache);
        businessService.setData(businessId, cache);

        return "[redirect]/items/options/" + businessId + "/" + id;
    }

    @Get("/items/options/values/delete/{{businessId}}/{{valueId}}/{{id}}")
    public String deleteValue(Cache cache,
                              @RouteComponent Long businessId,
                              @RouteComponent Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        setData(id, cache);
        businessService.setData(businessId, cache);

        cache.set("page", "/pages/item/options.jsp");
        return "/designs/auth.jsp";
    }

}
