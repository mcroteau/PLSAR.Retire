package giga.service;

import giga.Giga;
import giga.model.*;
import giga.repo.*;
import dev.blueocean.RouteAttributes;
import dev.blueocean.annotations.Bind;
import dev.blueocean.annotations.Service;
import dev.blueocean.model.*;
import dev.blueocean.security.SecurityManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
public class AssetService {

    @Bind
    AssetRepo assetRepo;

    @Bind
    UserRepo userRepo;

    @Bind
    BusinessRepo businessRepo;

    BusinessService businessService;

    public AssetService(){
        this.businessService = new BusinessService();
    }


    public String getAsset(String meta, NetworkResponse resp) throws Exception {
        String q = meta.toLowerCase();
        Asset asset = assetRepo.get(q);

        if(asset.getType().equals("media")){
            return "";
        }

        URL url = new URL(asset.getUri());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty("Accept-Charset", "UTF-8");
        InputStream is = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        //Test
        String line = "";
        do {
            sb.append(line);
        }while((line = reader.readLine()) != null);
        return sb.toString();
    }

    public String getMedia(String meta, NetworkResponse resp) throws Exception {
        String q = meta.toLowerCase();
        Asset asset = assetRepo.get(q);

        URL url = new URL(asset.getUri());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        OutputStream o = resp.getResponseStream();
        InputStream is = connection.getInputStream();
        OutputStream os = new BufferedOutputStream(o);

        int bit =0; ;
        while((bit=is.read())!=-1){
            os.write(bit);
        }

        os.close();
        is.close();
        o.close();

        return "q";

    }



    public String create(Long businessId, Cache cache, NetworkRequest req, SecurityManager security){
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);
        return "/pages/asset/new.jsp";
    }


    public String save(NetworkRequest req, SecurityManager security) throws Exception {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.getPhone(credential);
        if(authUser == null){
            authUser = userRepo.getEmail(credential);
        }

        Asset asset = (Asset) req.inflect(Asset.class);
        asset.setDateAdded(Giga.getDate());
        asset.setUserId(authUser.getId());

        RequestComponent requestComponent = req.getRequestComponent("asset");
        List<FileComponent> fileComponents = requestComponent.getFileComponents();

        RouteAttributes routeAttributes = req.getRouteAttributes();
        String key = (String) routeAttributes.get("cloud.key");
        String secret = (String) routeAttributes.get("cloud.secret");
        SeaService seaService = new SeaService();

        for (FileComponent fileComponent : fileComponents) {
            String original = fileComponent.getFileName();
            InputStream is = new ByteArrayInputStream(fileComponent.getFileBytes());
            String ext = Giga.getExt(original);
            String name = Giga.getString(6) + "." + ext;
            seaService.send(key, secret, name, is);
            asset.setMeta(name);
            asset.setUri(Giga.OCEAN_ENDPOINT + name);
        }

        assetRepo.save(asset);

        Asset savedAsset = assetRepo.getSaved();
        String permission = Giga.ASSET_MAINTENANCE + savedAsset.getId();
        userRepo.savePermission(authUser.getId(), permission);

        return "redirect:/assets/" + savedAsset.getBusinessId();
    }


    public String list(Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }
        businessService.setData(businessId, cache, userRepo, businessRepo, req, security);

        List<Asset> assets = assetRepo.getList(businessId);
        cache.set("assets", assets);
        return "/pages/asset/list.jsp";
    }

    public String delete(Long id, Long businessId, Cache cache, NetworkRequest req, SecurityManager security) {
        if(!security.isAuthenticated(req)){
            return "redirect:/";
        }

        String permission = Giga.ASSET_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, req) &&
                !security.hasPermission(permission, req)){
            cache.set("message", "Whoa, people might be using this. Lol, this isn't yours.");
            return "redirect:/";
        }

        assetRepo.delete(id);
        cache.set("message", "Successfully deleted asset.");

        return "redirect:/assets/" + businessId;
    }

}
