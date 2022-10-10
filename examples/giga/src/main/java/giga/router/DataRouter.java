package giga.router;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import giga.service.BusinessService;
import giga.service.SeaService;
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
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@HttpRouter
public class DataRouter {

    @Inject
    ItemRepo itemRepo;

    @Inject
    DesignRepo designRepo;

    @Inject
    UserRepo userRepo;

    @Inject
    CategoryRepo categoryRepo;

    @Inject
    DataRepo dataRepo;

    BusinessService businessService;

    public DataRouter(){
        this.businessService = new BusinessService();
    }

    @Get("/import/media/{{businessId}}")
    public String viewImport(Cache cache,
                             HttpRequest req,
                             SecurityManager security,
                             @Component Long businessId) {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);
        cache.set("page", "/pages/data/media_import.jsp");
        return "/designs/auth.jsp";//shes gone.
    }

    @Post("/import/media/{{businessId}}")
    public String importMedia(Cache cache,
                              HttpRequest req,
                              SecurityManager security,
                              @Component Long businessId) throws Exception {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

        RequestComponent requestComponent = req.getRequestComponent("media");
        List<FileComponent> fileComponents = requestComponent.getFileComponents();

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        DataImport dataImport = new DataImport();
        dataImport.setBusinessId(businessId);
        dataImport.setUserId(authUser.getId());
        dataImport.setDateImport(Giga.getDate());
        dataImport.setType("media");
        dataRepo.save(dataImport);

        DataImport savedImport = dataRepo.getSaved();
        String permission = Giga.DATA_IMPORT_MAINTENANCE + savedImport.getId();
        userRepo.savePermission(authUser.getId(), permission);

        String activeMedia = "";
        String issue = "";

        try {
            MediaImport mediaImport = new MediaImport();
            mediaImport.setImportId(savedImport.getId());

            for (FileComponent fileComponent : fileComponents) {
                String original = fileComponent.getFileName();
                activeMedia = original;

                List<String> mediaBits = Arrays.asList(original.split("\\."));

                System.out.println("mz " + mediaBits.size() + " o: " + original + " l:" + original.split(".").length);
//Nike-Mens-Shoes.12-01.12.16.jpg

                if (mediaBits.size() > 1) {
                    issue = "Unable to parse name from " + original + ". first bit from name";
                    String itemName = mediaBits.get(0).replace("-", " ");
                    mediaImport.setName(itemName);
                }

                if (mediaBits.size() > 2) {
                    String itemPrice = mediaBits.get(1).replace("-", ".");
                    issue = "Unable to parse price, second bit after first dot. " + itemPrice;
                    mediaImport.setPrice(new BigDecimal(itemPrice));
                }

                if (mediaBits.size() > 3) {
                    String itemQuantity = mediaBits.get(2);
                    issue = "Unable to parse quantity, third bit after second dot. " + itemQuantity;
                    mediaImport.setQuantity(new BigDecimal(itemQuantity));
                }

                if (mediaBits.size() > 4) {
                    String itemWeight = mediaBits.get(3);
                    issue = "Unable to parse weight, fourth bit after third dot. " + itemWeight;
                    mediaImport.setWeight(new BigDecimal(itemWeight));
                }

                InputStream is = new ByteArrayInputStream(fileComponent.getFileBytes());
                String ext = Giga.getExt(original);
                String name = Giga.getString(9) + "." + ext;

                SeaService seaService = new SeaService();
                seaService.send(name, is);

                mediaImport.setMeta(name);
                mediaImport.setUri(Giga.OCEAN_ENDPOINT + name);
                dataRepo.saveMedia(mediaImport);

                MediaImport savedMediaImport = dataRepo.getSavedMedia();
                String mediaImportPermission = Giga.MEDIA_IMPORT_MAINTENANCE + savedMediaImport.getId();
                userRepo.savePermission(authUser.getId(), mediaImportPermission);
            }

        }catch(Exception ex){
            ex.printStackTrace();
            cache.set("message", "Sorry, had issue with " + activeMedia + " please try again. " + issue);
            return "[redirect]/imports/media/"+ businessId;
        }

        cache.set("message", "All set! Everything imported. Check out the grid by clicking Power Grid.");
        return "[redirect]/imports/media/" + businessId;
    }

    @Get("/imports/media/{{businessId}}")
    public String viewImports(Cache cache,
                              HttpRequest req,
                              SecurityManager security,
                              @Component Long businessId) {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

        List<DataImport> dataImports = dataRepo.getList(businessId, "media");
        cache.set("dataImports", dataImports);
        cache.set("page", "/pages/data/data_imports_list.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/imports/media/{{businessId}}/{{importId}}")
    public String viewImports(Cache cache,
                              HttpRequest req,
                              SecurityManager security,
                              @Component Long businessId,
                              @Component Long importId) {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

        DataImport dataImport = dataRepo.get(importId);
        cache.set("dataImport", dataImport);

        List<MediaImport> mediaImports = dataRepo.getListMedia(importId);
        cache.set("mediaImports", mediaImports);

        List<Category> categories = categoryRepo.getListAll(businessId);
        cache.set("categories", categories);

        cache.set("page", "/pages/data/media_imports_list.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/import/media/update/{{businessId}}/{{importId}}")
    public String importMedia(Cache cache,
                              HttpRequest req,
                              SecurityManager security,
                              @Component Long businessId,
                              @Component Long importId) throws Exception {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        MediaImport mediaImport = (MediaImport) req.inflect(MediaImport.class);
        String permission = Giga.MEDIA_IMPORT_MAINTENANCE + mediaImport.getId();
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this category.");
            return "[redirect]/";
        }

        dataRepo.updateMedia(mediaImport);
        cache.set("message", "Successfully updated " + mediaImport.getName() + "!");
        return "[redirect]/imports/media/" + businessId + "/" + importId;
    }


    @Post("/import/media/convert/{{businessId}}/{{importId}}")
    public String convertItems(Cache cache,
                               HttpRequest req,
                               SecurityManager security,
                               @Component Long businessId,
                               @Component Long importId){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.DATA_IMPORT_MAINTENANCE + importId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "You don't have access to convert this import.");
            return "[redirect]/";
        }

        Design design = designRepo.getBase(businessId);
        DataImport dataImport = dataRepo.get(importId);

        List<MediaImport> mediaImports = dataRepo.getListMedia(importId);
        for(MediaImport mediaImport : mediaImports){
            Item item = new Item();
            item.setName(mediaImport.getName());
            item.setPrice(mediaImport.getPrice());
            item.setQuantity(mediaImport.getQuantity());
            item.setWeight(mediaImport.getWeight());
            item.setImageUri(mediaImport.getUri());
            item.setBusinessId(dataImport.getBusinessId());
            item.setDesignId(design.getId());
            itemRepo.save(item);

            Item savedItem = itemRepo.getSaved();
            String credential = security.getUser(req);
            User authUser = userRepo.get(credential);
            if(authUser == null){
                authUser = userRepo.getPhone(credential);
            }

            String itemPermission = Giga.ITEM_MAINTENANCE + savedItem.getId();
            userRepo.savePermission(authUser.getId(), itemPermission);

            Category category = categoryRepo.get(mediaImport.getCategoryId());
            if(category == null) category = categoryRepo.getOne(businessId);

            CategoryItem categoryItem = new CategoryItem();
            categoryItem.setItemId(savedItem.getId());
            categoryItem.setCategoryId(category.getId());

            categoryRepo.saveItem(categoryItem);

            if(category.getCategoryId() != null) {
                saveGuidanceCategory(savedItem, category);
            }
        }

        cache.set("message", "Successfully converted all media imports into items!");
        return "[redirect]/imports/media/" + businessId + "/" + importId;
    }

    @Post("/import/media/delete/{{businessId}}/{{importId}}")
    public String deleteImport(Cache cache,
                               HttpRequest req,
                               SecurityManager security,
                               @Component Long businessId,
                               @Component Long importId){
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String permission = Giga.DATA_IMPORT_MAINTENANCE + importId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "You don't have access to delete this import.");
            return "[redirect]/";
        }

        itemRepo.deleteItemImports(importId);
        dataRepo.deleteMediaImports(importId);
        dataRepo.delete(importId);

        cache.set("message", "Successfully deleted the import and removed all items");
        return "[redirect]/imports/media/" + businessId;
    }

    private void saveGuidanceCategory(Item savedItem, Category category){
        Category guidanceCategory = categoryRepo.get(category.getCategoryId());

        if(guidanceCategory != null) {
            CategoryItem categoryItem = new CategoryItem();
            categoryItem.setItemId(savedItem.getId());
            categoryItem.setCategoryId(guidanceCategory.getId());

            if (category.getCategoryId() != null) {
                saveGuidanceCategory(savedItem, guidanceCategory);
            }
        }
    }
}
