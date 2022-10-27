package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import dev.blueocean.RouteAttributes;
import dev.blueocean.annotations.Bind;
import dev.blueocean.annotations.Service;
import dev.blueocean.model.Cache;
import dev.blueocean.model.FileComponent;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.model.RequestComponent;
import dev.blueocean.security.SecurityManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    @Bind
    ItemRepo itemRepo;

    @Bind
    AssetRepo assetRepo;

    @Bind
    UserRepo userRepo;

    @Bind
    CategoryRepo categoryRepo;

    @Bind
    DesignRepo designRepo;

    @Bind
    BusinessRepo businessRepo;

    BusinessService businessService;

    public ItemService(){
        this.businessService = new BusinessService();
    }

    public String query(Long id, Cache cache, NetworkRequest req, SecurityManager security){
        String q = req.getValue("q");
        Business business = businessRepo.get(id);
        List<Item> items = itemRepo.query(q, id);

        SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);

        cache.set("q", q);
        cache.set("items", items);
        cache.set("siteService", siteService);
        cache.set("business", business);
        cache.set("items", items);
        cache.set("request", req);

        return "/pages/item/q.jsp";
    }

    public String getItem(Long id, String businessUri, Cache cache, NetworkRequest req, SecurityManager security) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "redirect:/home";
        }

        Item item = itemRepo.get(id, business.getId());
        if(item == null){
            return "redirect:/" + businessUri;
        }

        SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);

        setData(id, cache);

        cache.set("item", item);
        cache.set("business", business);
        cache.set("request", req);
        cache.set("siteService", siteService);

        return "/pages/item/index.jsp";
    }

    public String getItemCategory(Long id, Long categoryId, String businessUri, Cache cache, NetworkRequest req, SecurityManager security) {
        System.out.println(id + " : " + categoryId + " : " + businessUri);
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "redirect:/home";
        }

        Item item = itemRepo.get(id, business.getId());
        if(item == null){
            return "redirect:/" + businessUri;
        }

        Category category = categoryRepo.get(categoryId);
        if(category == null){
            return "redirect:/" + businessUri;
        }

        SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);

        setData(id, cache);

        cache.set("item", item);
        cache.set("category", category);
        cache.set("business", business);
        cache.set("request", req);
        cache.set("siteService", siteService);

        return "/pages/item/index.jsp";
    }


    public String create(Long businessId, Cache cache, NetworkRequest req, SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        List<Category> categories = categoryRepo.getListAll(businessId);
        if(categories.size() == 0){
            cache.set("message", "You have to walk before you can run... you need to create a category before you can continue.");
            return "redirect:/categories/new/" + businessId;
        }
        cache.set("categories", categories);

        List<Design> designs = designRepo.getList(businessId);
        cache.set("designs", designs);

        List<Asset> assets = assetRepo.getList(businessId);
        cache.set("assets", assets);

        return "/pages/item/new.jsp";
    }


    public String save(Long businessId, NetworkRequest req, SecurityManager security) throws IOException {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.getPhone(credential);
        if(authUser == null){
            authUser = userRepo.getEmail(credential);
        }

        Item item = (Item) req.inflect(Item.class);

        RequestComponent requestComponent = req.getRequestComponent("media");
        List<FileComponent> fileComponents = requestComponent.getFileComponents();

        RouteAttributes routeAttributes = req.getRouteAttributes();
        String key = (String) routeAttributes.get("cloud.key");
        String secret = (String) routeAttributes.get("cloud.secret");
        SeaService seaService = new SeaService();

        for (FileComponent fileComponent : fileComponents) {
            String original = fileComponent.getFileName();
            InputStream is = new ByteArrayInputStream(fileComponent.getFileBytes());
            String ext = Giga.getExt(original);
            String name = Giga.getString(9) + "." + ext;
            seaService.send(key, secret, name, is);
            item.setImageUri(Giga.OCEAN_ENDPOINT + name);
        }

        if(item.getImageUri() == null){
            return "redirect:/items/new/" + businessId;
        }

        item.setAffiliatePrice(item.getPrice());
        itemRepo.save(item);
        Item savedItem = itemRepo.getSaved();

        List<String> categories = req.getRequestComponent("categories").getValues();
        for(String id : categories){
            ItemCategory itemCategory = new ItemCategory(savedItem.getId(), Long.valueOf(id.trim()), businessId);
            categoryRepo.saveItem(itemCategory);
        }

        String permission = Giga.ITEM_MAINTENANCE + savedItem.getId();
        userRepo.savePermission(authUser.getId(), permission);

        return "redirect:/items/" + savedItem.getBusinessId();
    }

    public String list(Long businessId, Cache cache, NetworkRequest req, SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);
        SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);

        List<Item> items = itemRepo.getList(businessId);
        cache.set("items", items);
        cache.set("title", "Active Items");
        cache.set("siteService", siteService);
        return "/pages/item/list.jsp";
    }


    public String getListInactive(Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);
        SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);

        List<Item> items = itemRepo.getList(businessId, false);
        cache.set("items", items);
        cache.set("title", "Inactive Items");
        cache.set("siteService", siteService);
        return "/pages/item/list.jsp";
    }

    public String grid(Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        List<Item> items = itemRepo.getList(businessId);
        for(Item item : items){
            List<Category> categories = new ArrayList<>();
            List<ItemCategory> itemCategories = categoryRepo.getCategoryItems(item.getId());
            for(ItemCategory itemCategory : itemCategories){
                Category category = categoryRepo.get(itemCategory.getCategoryId());
                categories.add(category);
            }
            item.setCategories(categories);
        }
        List<Design> designs = designRepo.getList(businessId);
        List<Category> categories = categoryRepo.getListAll(businessId);

        SiteService siteService = new SiteService(userRepo, designRepo, categoryRepo, security);

        cache.set("designs", designs);
        cache.set("categories", categories);
        cache.set("items", items);
        cache.set("siteService", siteService);
        return "/pages/item/grid.jsp";
    }

    public String edit(Long id, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this item.");
            return "redirect:/";
        }

        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        Item item = itemRepo.get(id);

        cache.set("item", item);

        List<Category> categories = categoryRepo.getListAll(businessId);
        cache.set("categories", categories);

        List<ItemCategory> itemCategories = categoryRepo.getCategoryItems(item.getId());
        List<Category> activeCategories = new ArrayList<>();
        for(ItemCategory itemCategory : itemCategories){
            Category category = categoryRepo.get(itemCategory.getCategoryId());
            activeCategories.add(category);
        }
        cache.set("activeCategories", activeCategories);

        if(itemCategories != null &&
                itemCategories.size() > 0){
            ItemCategory itemCategory = itemCategories.get(0);
            cache.set("categoryId", itemCategory.getCategoryId());
        }

        List<Design> designs = designRepo.getList(businessId);
        cache.set("designs", designs);

        List<Asset> assets = assetRepo.getList(businessId);
        cache.set("assets", assets);

        return "/pages/item/edit.jsp";
    }


    public String update(Long id, Long businessId, Boolean onGrid, Cache cache, NetworkRequest req, SecurityManager security) throws IOException {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        Business business = businessRepo.get(businessId);

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this design.");
            return "redirect:/";
        }

        Item item = (Item) req.inflect(Item.class);

        if(req.getValue("media") != null){
            RequestComponent requestComponent = req.getRequestComponent("media");
            List<FileComponent> fileComponents = requestComponent.getFileComponents();

            RouteAttributes routeAttributes = req.getRouteAttributes();
            String key = (String) routeAttributes.get("cloud.key");
            String secret = (String) routeAttributes.get("cloud.secret");
            SeaService seaService = new SeaService();

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

        List<String> categories = req.getRequestComponent("categories").getValues();
        if(categories != null) {
            System.out.println("a " + req.getValue("categories") + " : " + categories);
            for (String categoryId : categories) {
                ItemCategory itemCategory = new ItemCategory(item.getId(), Long.valueOf(categoryId.trim()), businessId);
                categoryRepo.saveItem(itemCategory);
            }
        }

        if(business.getAffiliate() != null &&
                business.getAffiliate() &&
                (item.getAffiliatePrice().compareTo(item.getPrice()) == 1)){
            cache.set("message", "Your price may not be lower than the business owners. I get it, you want to buy their products at a lower price. You're a genius. ; )");
            if(onGrid){
                return "redirect:/items/grid/" + businessId;
            }
            return "redirect:/items/edit/" + businessId + "/" + id;
        }

        itemRepo.update(item);
        cache.set("message", "Successfully updated item");

        if(onGrid){
            return "redirect:/items/grid/" + businessId;
        }
        return "redirect:/items/edit/" + businessId + "/" + id;
    }

    public String delete(Long id, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "redirect:/";
        }

        categoryRepo.deleteCategoryItems(id);
        List<ItemOption> itemOptions = itemRepo.getOptions(id);
        for(ItemOption itemOption : itemOptions){
            itemRepo.deleteValues(itemOption.getId());
        }
        itemRepo.deleteOptions(id);
        itemRepo.delete(id);
        cache.set("message", "Successfully deleted item.");

        return "redirect:/items/" + businessId;
    }

    public String options(Long id, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "redirect:/";
        }

        setData(id, cache);
        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        return "/pages/item/options.jsp";
    }

    public String saveOption(Long id, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "redirect:/";
        }

        ItemOption itemOption = (ItemOption) req.inflect(ItemOption.class);
        itemRepo.saveOption(itemOption);

        return "redirect:/items/options/" + businessId + "/" + id;
    }


    public String deleteOption(Long id, Long optionId, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "redirect:/";
        }

        System.out.println("delete option ");
        itemRepo.deleteValues(optionId);
        itemRepo.deleteOption(optionId);

        return "redirect:/items/options/" + businessId + "/" + id;
    }

    public String saveValue(Long id, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "redirect:/";
        }

        OptionValue optionValue = (OptionValue) req.inflect(OptionValue.class);
        itemRepo.saveValue(optionValue);

        return "redirect:/items/options/" + businessId + "/" + id;
    }

    public String deleteValue(Long id, Long valueId, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to delete this item.");
            return "redirect:/";
        }

        System.out.println("delete option value ");
        itemRepo.deleteValue(valueId);

        setData(id, cache);
        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        return "redirect:/items/options/" + businessId + "/" + id;
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
