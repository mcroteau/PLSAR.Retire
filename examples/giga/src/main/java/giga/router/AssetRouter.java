package giga.router;

import com.amazonaws.Request;
import giga.Giga;
import giga.model.Asset;
import giga.model.User;
import giga.repo.AssetRepo;
import giga.repo.UserRepo;
import giga.service.BusinessService;
import giga.service.SeaService;
import net.plsar.annotations.*;
import net.plsar.annotations.Component;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.*;
import net.plsar.security.SecurityManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@HttpRouter
public class AssetRouter {

    @Inject
    AssetRepo assetRepo;

    @Inject
    UserRepo userRepo;

    BusinessService businessService;

    public AssetRouter(){
        this.businessService = new BusinessService();
    }

    @Text
    @Get("/go/{n}")
    public String getAsset(@Component String meta) throws Exception {
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

    @Media
    @Get("/media/{n}")
    public String getMedia(HttpResponse resp,
                           @Component String meta) throws Exception {
        String q = meta.toLowerCase();
        Asset asset = assetRepo.get(q);

        URL url = new URL(asset.getUri());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        OutputStream o = resp.getResponseStream();
        InputStream is = connection.getInputStream();
        OutputStream os = new BufferedOutputStream(o);

        int bit = 0; ;
        while((bit=is.read())!=-1){
            os.write(bit);
        }

        os.close();
        is.close();
        o.close();

        return "q";

    }

    @Get("/assets/new/{businessId}")
    public String configure(Cache cache,
                            HttpRequest httpRequest,
                            SecurityManager security,
                            @Component Long businessId){
        if(!security.isAuthenticated(httpRequest)){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);
        cache.set("page", "/pages/asset/new.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/assets/{businessId}")
    public String list(Cache cache,
                       HttpRequest httpRequest,
                       SecurityManager security,
                       @Component Long businessId){
        if(!security.isAuthenticated(httpRequest)){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

        List<Asset> assets = assetRepo.getList(businessId);
        cache.set("assets", assets);
        cache.set("page", "/pages/asset/list.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/assets/save")
    public String save(HttpRequest req, SecurityManager security) throws Exception {
        if(!security.isAuthenticated(req)){
            return "[redirect]/";
        }

        String credential = security.getUser(req);
        User authUser = userRepo.get(credential);
        if(authUser == null){
            authUser = userRepo.getPhone(credential);
        }

        Asset asset = (Asset) req.inflect(req, Asset.class);
        asset.setDateAdded(Giga.getDate());
        asset.setUserId(authUser.getId());

        RequestComponent requestComponent = new RequestComponent();
        List<FileComponent> fileComponents = requestComponent.getFiles();

        SeaService seaService = new SeaService();

        for (FileComponent fileComponent : fileComponents) {
            String original = fileComponent.getFileName();
            InputStream is = new ByteArrayInputStream(fileComponent.getFileBytes());
            String ext = Giga.getExt(original);
            String name = Giga.getString(6) + "." + ext;
            seaService.send(name, is);
            asset.setMeta(name);
            asset.setUri(Giga.OCEAN_ENDPOINT + name);
        }

        assetRepo.save(asset);

        Asset savedAsset = assetRepo.getSaved();
        String permission = Giga.ASSET_MAINTENANCE + savedAsset.getId();
        userRepo.savePermission(authUser.getId(), permission);

        return "[redirect]/assets/" + savedAsset.getBusinessId();
    }

    @Post("/assets/delete/{{businessId}}/{{id}}")
    public String delete(Cache cache,
                         HttpRequest httpRequest,
                         SecurityManager security,
                         @Component Long businessId,
                         @Component Long id){
        if(!security.isAuthenticated(httpRequest)){
            return "[redirect]/";
        }

        String permission = Giga.ASSET_MAINTENANCE + id;
        if(!security.hasRole(Giga.SUPER_ROLE, httpRequest) &&
                !security.hasPermission(permission, httpRequest)){
            cache.set("message", "Whoa, people might be using this. Lol, this isn't yours.");
            return "[redirect]/";
        }

        assetRepo.delete(id);
        cache.set("message", "Successfully deleted asset.");

        return "[redirect]/assets/" + businessId;
    }
}
