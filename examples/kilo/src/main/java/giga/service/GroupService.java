package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import dev.blueocean.RouteAttributes;
import net.plsar.annotations.Bind;
import net.plsar.annotations.Service;
import net.plsar.model.Cache;
import net.plsar.model.FileComponent;
import net.plsar.model.NetworkRequest;
import net.plsar.model.RequestComponent;
import net.plsar.security.SecurityManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@Service
public class GroupService {

    @Bind
    GroupRepo groupRepo;

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

    public GroupService(){
        this.businessService = new BusinessService();
    }

    public String create(Long businessId, Cache cache, NetworkRequest req, SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        String permission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Whoa! Not authorized to view this business.");
            return "redirect:/";
        }

        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        List<GroupOption> groupOptions = groupRepo.getListOptions(businessId);
        cache.set("groupOptions", groupOptions);

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

        return "/pages/group/create.jsp";
    }

    public String save(Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        String permission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Whoa! Not authorized to view this business.");
            return "redirect:/";
        }

        ItemGroup itemGroup = (ItemGroup) req.inflect(ItemGroup.class);

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
            itemGroup.setImageUri(Giga.OCEAN_ENDPOINT + name);
        }

        groupRepo.save(itemGroup);
        ItemGroup savedItemGroup = groupRepo.getSaved();

        cache.set("message", "Successfully started Item ItemGroup!");
        return "redirect:/" + businessId + "/groups/edit/" + savedItemGroup.getId();
    }

    public String edit(Long id, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        String permission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Whoa! Not authorized to view this business.");
            return "redirect:/";
        }

        List<GroupOption> groupOptions = groupRepo.getListOptions(businessId);
        cache.set("groupOptions", groupOptions);

        ItemGroup itemGroup = groupRepo.get(id);
        cache.set("itemGroup", itemGroup);

        if(req.getValue("optionsCount") != null)cache.set("optionsCount", req.getValue("optionsCount"));
        if(req.getValue("pricesCount") != null)cache.set("pricesCount", req.getValue("pricesCount"));

        return "/pages/itemGroup/edit.jsp";
    }

    public String createOptions(Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        String permission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Whoa! Not authorized to view this business.");
            return "redirect:/";
        }

        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        List<GroupOption> groupOptions = groupRepo.getListOptions(businessId);
        cache.set("groupOptions", groupOptions);

        return "/pages/group/create_options.jsp";
    }

    public String saveOption(Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        String permission = Giga.BUSINESS_MAINTENANCE + businessId;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Whoa! Not authorized to view this business.");
            return "redirect:/";
        }

//        GroupOption groupOption = (GroupOption) req.inflect(GroupOption.class);
//        optionRepo.saveOption(groupOption);//todo:

        cache.set("message", "Successfully saved group option.");

        return "redirect:/" + businessId + "/groups/options/create";

    }

}
