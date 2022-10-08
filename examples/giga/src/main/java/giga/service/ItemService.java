package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Service;
import qio.model.web.ResponseData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

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

    @Inject
    SiteService siteService;

    @Inject
    BusinessService businessService;

    @Inject
    SeaService seaService;

    @Inject
    AuthService authService;


    public String query(Long id, ResponseData data, HttpServletRequest req){
        String q = req.getParameter("q");
        Business business = businessRepo.get(id);
        List<Item> items = itemRepo.q(q, id);
        data.set("q", q);
        data.set("items", items);
        data.set("siteService", siteService);
        data.set("business", business);
        data.set("items", items);
        data.set("request", req);
        return "/pages/item/q.jsp";
    }

    public String getItem(Long id, String businessUri, ResponseData data, HttpServletRequest req) {
        Business business = businessRepo.get(businessUri);
        if(business == null){
            return "[redirect]/home";
        }

        Item item = itemRepo.get(id, business.getId());
        if(item == null){
            return "[redirect]/" + businessUri;
        }

        setData(id, data);

        data.set("item", item);
        data.set("business", business);
        data.set("request", req);
        data.set("siteService", siteService);

        return "/pages/item/index.jsp";
    }

    public String getItemCategory(Long id, Long categoryId, String businessUri, ResponseData data, HttpServletRequest req) {
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

        setData(id, data);

        data.set("item", item);
        data.set("category", category);
        data.set("business", business);
        data.set("request", req);
        data.set("siteService", siteService);

        return "/pages/item/index.jsp";
    }


    public String create(Long businessId, ResponseData data){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, data);

        List<Category> categories = categoryRepo.getListAll(businessId);
        if(categories.size() == 0){
            data.set("message", "You have to walk before you can run... you need to create a category before you can continue.");
            return "[redirect]/categories/new/" + businessId;
        }
        data.set("categories", categories);

        List<Design> designs = designRepo.getList(businessId);
        data.set("designs", designs);

        List<Asset> assets = assetRepo.getList(businessId);
        data.set("assets", assets);

        data.set("page", "/pages/item/new.jsp");
        return "/designs/auth.jsp";
    }


    public String save(Long businessId, HttpServletRequest req) throws IOException, ServletException {
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

    public String list(Long businessId, ResponseData data){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, data);

        List<Item> items = itemRepo.getList(businessId);
        data.set("items", items);
        data.set("title", "Active Items");
        data.set("siteService", siteService);
        data.set("page", "/pages/item/list.jsp");
        return "/designs/auth.jsp";
    }


    public String getListInactive(Long businessId, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, data);

        List<Item> items = itemRepo.getList(businessId, false);
        data.set("items", items);
        data.set("title", "Inactive Items");
        data.set("siteService", siteService);
        data.set("page", "/pages/item/list.jsp");
        return "/designs/auth.jsp";
    }

    public String grid(Long businessId, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, data);

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
        data.set("designs", designs);
        data.set("categories", categories);
        data.set("items", items);
        data.set("siteService", siteService);
        data.set("page", "/pages/item/grid.jsp");
        return "/designs/auth.jsp";
    }

    public String edit(Long id, Long businessId, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to edit this item.");
            return "[redirect]/";
        }

        businessService.setData(businessId, data);

        Item item = itemRepo.get(id);

        data.set("item", item);

        List<Category> categories = categoryRepo.getListAll(businessId);
        data.set("categories", categories);

        List<CategoryItem> categoryItems = categoryRepo.getCategoryItems(item.getId());
        List<Category> activeCategories = new ArrayList<>();
        for(CategoryItem categoryItem : categoryItems){
            Category category = categoryRepo.get(categoryItem.getCategoryId());
            activeCategories.add(category);
        }
        data.set("activeCategories", activeCategories);

        if(categoryItems != null &&
                categoryItems.size() > 0){
            CategoryItem categoryItem = categoryItems.get(0);
            data.set("categoryId", categoryItem.getCategoryId());
        }

        List<Design> designs = designRepo.getList(businessId);
        data.set("designs", designs);

        List<Asset> assets = assetRepo.getList(businessId);
        data.set("assets", assets);

        data.set("page", "/pages/item/edit.jsp");
        return "/designs/auth.jsp";
    }


    public String update(Long id, Long businessId, Boolean onGrid, ResponseData data, HttpServletRequest req) throws IOException, ServletException {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        Business business = businessRepo.get(businessId);

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to edit this design.");
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
            data.set("message", "Your price may not be lower than the business owners. I get it, you want to buy their products at a lower price. You're a genius. ; )");
            if(onGrid){
                return "[redirect]/items/grid/" + businessId;
            }
            return "[redirect]/items/edit/" + businessId + "/" + id;
        }

        itemRepo.update(item);
        data.set("message", "Successfully updated item");

        if(onGrid){
            return "[redirect]/items/grid/" + businessId;
        }
        return "[redirect]/items/edit/" + businessId + "/" + id;
    }

    public String delete(Long id, Long businessId, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        categoryRepo.deleteCategoryItems(id);
        List<ItemOption> itemOptions = itemRepo.getOptions(id);
        for(ItemOption itemOption : itemOptions){
            itemRepo.deleteValues(itemOption.getId());
        }
        itemRepo.deleteOptions(id);
        itemRepo.delete(id);
        data.set("message", "Successfully deleted item.");

        return "[redirect]/items/" + businessId;
    }

    public String options(Long id, Long businessId, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        setData(id, data);
        businessService.setData(businessId, data);

        data.set("page", "/pages/item/options.jsp");
        return "/designs/auth.jsp";
    }

    public String saveOption(Long id, Long businessId, ResponseData data, HttpServletRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        ItemOption itemOption = (ItemOption) Qio.get(req, ItemOption.class);
        itemRepo.saveOption(itemOption);

        return "[redirect]/items/options/" + businessId + "/" + id;
    }


    public String deleteOption(Long id, Long optionId, Long businessId, ResponseData data, HttpServletRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        System.out.println("delete option ");
        itemRepo.deleteValues(optionId);
        itemRepo.deleteOption(optionId);

        return "[redirect]/items/options/" + businessId + "/" + id;
    }

    public String saveValue(Long id, Long businessId, ResponseData data, HttpServletRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        OptionValue optionValue = (OptionValue) Qio.get(req, OptionValue.class);
        itemRepo.saveValue(optionValue);

        return "[redirect]/items/options/" + businessId + "/" + id;
    }

    public String deleteValue(Long id, Long valueId, Long businessId, ResponseData data, HttpServletRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ITEM_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to delete this item.");
            return "[redirect]/";
        }

        System.out.println("delete option value ");
        itemRepo.deleteValue(valueId);

        setData(id, data);
        businessService.setData(businessId, data);

        return "[redirect]/items/options/" + businessId + "/" + id;
    }

    public void setData(Long id, ResponseData data){
        Item item = itemRepo.get(id);
        List<ItemOption> itemOptions = itemRepo.getOptions(item.getId());
        for(ItemOption itemOption : itemOptions){
            List<OptionValue> optionValues = itemRepo.getValues(itemOption.getId());
            itemOption.setOptionValues(optionValues);
        }
        data.set("item", item);
        data.set("itemOptions", itemOptions);
    }

}
