package giga.service;

import giga.Giga;
import giga.ingest.ItemGroupIngest;
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
import java.math.BigDecimal;
import java.util.*;

@Service
public class DataService {

    public DataService(){
        this.ingests = new HashMap<>();
        this.businessService = new BusinessService();
    }

    Map<String, ItemGroupIngest> ingests;

    @Bind
    ItemRepo itemRepo;

    @Bind
    DesignRepo designRepo;

    @Bind
    UserRepo userRepo;

    @Bind
    CategoryRepo categoryRepo;

    @Bind
    DataRepo dataRepo;

    @Bind
    ModelRepo modelRepo;

    @Bind
    GroupRepo groupRepo;

    @Bind
    OptionRepo optionRepo;

    @Bind
    IngestRepo ingestRepo;

    @Bind
    PriceRepo priceRepo;

    @Bind
    BusinessRepo businessRepo;


    BusinessService businessService;



    public String viewImportMedia(Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        String permission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "You don't have access to import for this business.");
            return "redirect:/";
        }

        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);
        return "/pages/data/media_import.jsp";
    }

    public String importMedia(Long businessId, Cache cache, NetworkRequest req, SecurityManager security) throws Exception{
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        String businessPermission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(businessPermission, req)){
            cache.set("message", "You don't have access to import for this business.");
            return "redirect:/";
        }

        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        RequestComponent requestComponent = req.getRequestComponent("asset");
        List<FileComponent> fileComponents = requestComponent.getFileComponents();

        RouteAttributes routeAttributes = req.getRouteAttributes();
        String key = (String) routeAttributes.get("cloud.key");
        String secret = (String) routeAttributes.get("cloud.secret");
        SeaService seaService = new SeaService();

        String credential = security.getUser(req);
        User authUser = userRepo.getPhone(credential);
        if(authUser == null){
            authUser = userRepo.getEmail(credential);
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

            for (FileComponent file : fileComponents) {
                String original = file.getFileName();
                activeMedia = original;

                List<String> mediaBits = Arrays.asList(original.split("\\."));

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

                InputStream is = new ByteArrayInputStream(file.getFileBytes());
                String ext = Giga.getExt(original);
                String name = Giga.getString(9) + "." + ext;
                seaService.send(key, secret, name, is);

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
            return "redirect:/imports/media/"+ businessId;
        }

        cache.set("message", "All set! Everything imported. Check out the grid by clicking Power Grid.");
        return "redirect:/imports/media/" + businessId;
    }

    public String viewImportsMedia(Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        String permission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "You don't have access to import for this business.");
            return "redirect:/";
        }

        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        List<DataImport> dataImports = dataRepo.getList(businessId, "media");
        cache.set("dataImports", dataImports);
        return "/pages/data/data_imports_list.jsp";
    }

    public String viewMedias(Long businessId, Long importId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        String permission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "You don't have access to import for this business.");
            return "redirect:/";
        }
        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        DataImport dataImport = dataRepo.get(importId);
        cache.set("dataImport", dataImport);

        List<MediaImport> mediaImports = dataRepo.getListMedia(importId);
        cache.set("mediaImports", mediaImports);

        List<Category> categories = categoryRepo.getListAll(businessId);
        cache.set("categories", categories);

        return "/pages/data/media_imports_list.jsp";
    }

    public String updateMedia(Long businessId, Long importId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        String businessPermission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(businessPermission, req)){
            cache.set("message", "You don't have access to import for this business.");
            return "redirect:/";
        }

        MediaImport mediaImport = (MediaImport) req.inflect(MediaImport.class);
        String permission = Giga.MEDIA_IMPORT_MAINTENANCE + mediaImport.getId();
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Unauthorized to edit this category.");
            return "redirect:/";
        }

        dataRepo.updateMedia(mediaImport);
        cache.set("message", "Successfully updated " + mediaImport.getName() + "!");
        return "redirect:/imports/media/" + businessId + "/" + importId;
    }

    public String deleteImport(Long businessId, Long importId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.DATA_IMPORT_MAINTENANCE + importId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "You don't have access to delete this import.");
            return "redirect:/";
        }

        itemRepo.deleteItemImports(importId);
        dataRepo.deleteMediaImports(importId);
        dataRepo.delete(importId);

        cache.set("message", "Successfully deleted the import and removed all items");
        return "redirect:/imports/media/" + businessId;
    }

    public String convertItems(Long businessId, Long importId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.DATA_IMPORT_MAINTENANCE + importId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "You don't have access to convert this import.");
            return "redirect:/";
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
            User authUser = userRepo.getPhone(credential);
            if(authUser == null){
                authUser = userRepo.getEmail(credential);
            }

            String itemPermission = Giga.ITEM_MAINTENANCE + savedItem.getId();
            userRepo.savePermission(authUser.getId(), itemPermission);

            Category category = categoryRepo.get(mediaImport.getCategoryId());
            if(category == null) category = categoryRepo.getOne(businessId);

            ItemCategory itemCategory = new ItemCategory();
            itemCategory.setItemId(savedItem.getId());
            itemCategory.setCategoryId(category.getId());

            categoryRepo.saveItem(itemCategory);

            if(category.getCategoryId() != null) {
                saveGuidanceCategory(savedItem, category);
            }
        }

        cache.set("message", "Successfully converted all media imports into items!");
        return "redirect:/imports/media/" + businessId + "/" + importId;
    }

    private void saveGuidanceCategory(Item savedItem, Category category){
        Category guidanceCategory = categoryRepo.get(category.getCategoryId());

        if(guidanceCategory != null) {
            ItemCategory itemCategory = new ItemCategory();
            itemCategory.setItemId(savedItem.getId());
            itemCategory.setCategoryId(guidanceCategory.getId());

            if (category.getCategoryId() != null) {
                saveGuidanceCategory(savedItem, guidanceCategory);
            }
        }
    }

    public String viewItemGroupImport(Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        String permission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "You don't have access to import for this business.");
            return "redirect:/";
        }
        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);
        return "/pages/data/item_group_import.jsp";
    }

    public String viewItemGroups(Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "You don't have access to import for this business.");
            return "redirect:/";
        }

        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);
        List<Ingest> ingests = ingestRepo.getList(businessId);
        for(Ingest ingest: ingests){
            List<ItemGroup> itemGroups = groupRepo.getList(ingest.getId());
            for(ItemGroup itemGroup: itemGroups){
                List<GroupOption> groupOptions = optionRepo.getListOptions(itemGroup.getId());
                itemGroup.setGroupOptions(groupOptions);

                List<GroupModel> groupModels = modelRepo.getList(itemGroup.getId());
                for(GroupModel groupModel : groupModels){
                    List<GroupOptionValue> groupValues = optionRepo.getListValues(groupModel.getId());
                    List<PricingValue> pricingValues = priceRepo.getListValues(groupModel.getId());
                    groupModel.setGroupValues(groupValues);
                    groupModel.setPricingValues(pricingValues);
                }
                itemGroup.setGroupModels(groupModels);

                List<PricingOption> pricingOptions = priceRepo.getListOptions(itemGroup.getId());
                itemGroup.setPricingOptions(pricingOptions);
            }
            ingest.setItemGroups(itemGroups);
        }

        cache.set("ingests", ingests);

        return "/pages/data/item_group_import_list.jsp";
    }


//    public String viewIngest(Long businessId, Cache cache, NetworkRequest req, SecurityManager security){
//        if(!security.isAuthenticated(req)){
//            return "redirect:/";
//        }
//
//        String permission = Kilo.BUSINESS_MAINTENANCE + businessId;
//        if(!security.hasRole(Kilo.SUPER_ROLE, req) &&
//                !security.hasPermission(permission, req)){
//            cache.set("message", "You don't have access to import for this business.");
//            return "redirect:/";
//        }
//
//        return "/pages/data/item_group_import.jsp";
//    }



    public String ingest(Long businessId, Cache cache, NetworkRequest req, SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "You don't have access to import for this business.");
            return "redirect:/";
        }

        Business business = businessRepo.get(businessId);
        String key = business.getUri();
        if(!ingests.containsKey(key)){
            ingests.remove(key);
        }

        ItemGroupIngest ingest = null;
        try{

            Design design = designRepo.getBase(businessId);

            ingest = new ItemGroupIngest.Builder()
                    .withDesignId(design.getId())
                    .withBusinessId(businessId)
                    .withModelRepo(modelRepo)
                    .withGroupRepo(groupRepo)
                    .withOptionRepo(optionRepo)
                    .withPriceRepo(priceRepo)
                    .withIngestRepo(ingestRepo)
                    .withRequest(req)
                    .build()
                    .ingest();

            ingests.put(key, ingest);

        }catch (IOException ex){
            ex.printStackTrace();
            cache.set("message", "Oh no! Will you try again, it may be something small with your data.");
            return "redirect:/";
        }

        IngestStats stats = getStats(businessId);
        Integer count = 0;
        if(stats != null)count = stats.getProcessed();

        if(count != null){
            cache.set("message", "Successfully imported " + count + " item models");
        }else{
            cache.set("message", "Nothing was imported because the Item Group name exists");
        }

        return "redirect:/imports/item_groups/" + businessId;
    }


    public IngestStats getStats(Long businessId){
        Business business = businessRepo.get(businessId);
        if(business == null){
            return null;
        }
        String key = business.getUri();
        if(!ingests.containsKey(key)){
            return null;
        }
        ItemGroupIngest ingest = ingests.get(key);
        IngestStats ingestStats = ingest.getStats();
        return ingestStats;
    }

    public String deleteImportedGroup(Long groupId, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "You don't have access to import for this business.");
            return "redirect:/";
        }
        /*
            delete pricing options
            delete pricing values
            delete group option values
            delete group options
            delete models
            delete item groups
            delete ingest
         */
        optionRepo.deleteOptionsGroup(groupId);
        optionRepo.deleteValuesGroup(groupId);
        priceRepo.deleteOptionsGroup(groupId);
        priceRepo.deleteValuesGroup(groupId);
        modelRepo.deleteGroup(groupId);
        groupRepo.delete(groupId);

        cache.set("message", "Successfully removed item group");
        return "redirect:/imports/item_groups/" + businessId;
    }

    public String deleteGroupImport(Long ingestId, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "You don't have access to import for this business.");
            return "redirect:/";
        }
        /*
            delete pricing options
            delete pricing values
            delete group option values
            delete group options
            delete models
            delete item groups
            delete ingest
         */
        optionRepo.deleteOptions(ingestId);
        optionRepo.deleteValues(ingestId);
        priceRepo.deleteOptions(ingestId);
        priceRepo.deleteValues(ingestId);
        modelRepo.deleteIngest(ingestId);
        groupRepo.deleteIngest(ingestId);
        ingestRepo.delete(ingestId);

        cache.set("message", "Successfully removed item group");
        return "redirect:/imports/item_groups/" + businessId;
    }


    /**
     *
     *
     * Corina
     * Beautiful Indian woman
     * Heidi
     * Holly
     * Jamie
     * Seeyaa
     *
     * Started with
     * 2 others
     *
     */







//    public String ingestOld(Long businessId, Cache cache, HttpRequest req){
//        if(!security.isAuthenticated()){
//            return "redirect:/";
//        }
//
//        String permission = Kilo.BUSINESS_MAINTENANCE + businessId;
//        if(!security.isAdministrator() &&
//                !security.hasPermission(permission)){
//            cache.set("message", "You don't have access to import for this business.");
//            return "redirect:/";
//        }
//
//        try {
//            ItemGroupIngest itemGroupImport = (ItemGroupIngest)qio.set(req, ItemGroupIngest.class);
//            if(itemGroupImport.getSpreadsheetId().equals("") ||
//                    itemGroupImport.getStartCell().equals("") ||
//                    itemGroupImport.getEndCell().equals("")){
//                cache.set("itemGroupImport", itemGroupImport);
//                cache.set("message", "Please make sure all fields are complete, we are sorry.");
//                return "redirect:/" + businessId + "/imports/group_items";
//            }
//
//            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//            final String range = "Class Data!" + itemGroupImport.getStartCell() + ":" + itemGroupImport.getEndCell();
//
//            Sheets service = null;
//
//
//            service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                    .setApplicationName(APPLICATION_NAME)
//                    .build();
//
//            ValueRange response = service.spreadsheets().values()
//                    .get(itemGroupImport.getSpreadsheetId(), range)
//                    .execute();
//
//            List<List<Object>> values = response.getValues();
//
//            if (values != null &&
//                    !values.isEmpty()) {
//                for (int z = 0; z < values.size(); z++) {
//                    List<Object> entries = values.get(z);
//                    for(Object entry : entries){
//                        System.out.print(entry);
//                    }
//                    System.out.println("");
//                }
//            }
//            if (values == null ||
//                    values.isEmpty()) {
//                cache.set("itemGroupImport", itemGroupImport);
//                cache.set("message", "Whao nelly, something aint right. Please make sure your data is correct.");
//                return "redirect:/" + businessId + "/imports/group_items";
//            }
//
//            System.out.println("Name, Major");
//
//        } catch (IOException | GeneralSecurityException ex) {
//            ex.printStackTrace();
//            cache.set("message", "Oh no! Unable to locate authentication credentials. Please contact someone!");
//            return "redirect:/";
//        }
//
//        cache.set("message", "Successfully retrieved and saved spreadsheet data.");
//        return "redirect:/" + businessId + "/imports/group_items";
//    }


//    public Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException, GeneralSecurityException {
//        InputStream in = DataService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
//        if (in == null) {
//            throw new FileNotFoundException("creds not found : " + CREDENTIALS_FILE_PATH);
//        }
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
//                .setAccessType("offline")
//                .build();
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
//        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//
////        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
////        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, scopes).setDataStoreFactory(new MemoryDataStoreFactory())
////                .setAccessType("offline").build();
////        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("117778828736287500064");
//
//    }

}
