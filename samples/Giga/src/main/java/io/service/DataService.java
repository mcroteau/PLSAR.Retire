package io.service;

import io.Giga;
import io.model.*;
import io.repo.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Media;
import qio.annotate.Service;
import qio.model.web.ResponseData;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataService {

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

    @Inject
    AuthService authService;

    @Inject
    SeaService seaService;

    @Inject
    BusinessService businessService;

    public String viewImportMedia(Long businessId, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, data);
        data.set("page", "/pages/data/media_import.jsp");
        return "/designs/auth.jsp";//shes gone.
    }

    public String importMedia(Long businessId, ResponseData data, HttpServletRequest req) throws Exception{
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, data);

        List<Part> fileParts = req.getParts()
                .stream()
                .filter(part -> "media".equals(part.getName()) && part.getSize() > 0)
                .collect(Collectors.toList());

        User authUser = authService.getUser();

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

            for (Part part : fileParts) {
                String original = Paths.get(part.getSubmittedFileName()).getFileName().toString();
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

                InputStream is = part.getInputStream();
                String ext = Giga.getExt(original);
                String name = Giga.getString(9) + "." + ext;
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
            data.set("message", "Sorry, had issue with " + activeMedia + " please try again. " + issue);
            return "[redirect]/imports/media/"+ businessId;
        }

        data.set("message", "All set! Everything imported. Check out the grid by clicking Power Grid.");
        return "[redirect]/imports/media/" + businessId;
    }

    public String viewImportsMedia(Long businessId, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, data);

        List<DataImport> dataImports = dataRepo.getList(businessId, "media");
        data.set("dataImports", dataImports);
        data.set("page", "/pages/data/data_imports_list.jsp");
        return "/designs/auth.jsp";
    }

    public String viewMedias(Long businessId, Long importId, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, data);

        DataImport dataImport = dataRepo.get(importId);
        data.set("dataImport", dataImport);

        List<MediaImport> mediaImports = dataRepo.getListMedia(importId);
        data.set("mediaImports", mediaImports);

        List<Category> categories = categoryRepo.getListAll(businessId);
        data.set("categories", categories);

        data.set("page", "/pages/data/media_imports_list.jsp");
        return "/designs/auth.jsp";
    }

    public String updateMedia(Long businessId, Long importId, ResponseData data, HttpServletRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        MediaImport mediaImport = (MediaImport) Qio.get(req, MediaImport.class);
        String permission = Giga.MEDIA_IMPORT_MAINTENANCE + mediaImport.getId();
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Unauthorized to edit this category.");
            return "[redirect]/";
        }

        dataRepo.updateMedia(mediaImport);
        data.set("message", "Successfully updated " + mediaImport.getName() + "!");
        return "[redirect]/imports/media/" + businessId + "/" + importId;
    }

    public String deleteImport(Long businessId, Long importId, ResponseData data, HttpServletRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.DATA_IMPORT_MAINTENANCE + importId;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "You don't have access to delete this import.");
            return "[redirect]/";
        }

        itemRepo.deleteItemImports(importId);
        dataRepo.deleteMediaImports(importId);
        dataRepo.delete(importId);

        data.set("message", "Successfully deleted the import and removed all items");
        return "[redirect]/imports/media/" + businessId;
    }

    public String convertItems(Long businessId, Long importId, ResponseData data, HttpServletRequest req) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.DATA_IMPORT_MAINTENANCE + importId;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "You don't have access to convert this import.");
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
            User authUser = authService.getUser();

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

        data.set("message", "Successfully converted all media imports into items!");
        return "[redirect]/imports/media/" + businessId + "/" + importId;
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
