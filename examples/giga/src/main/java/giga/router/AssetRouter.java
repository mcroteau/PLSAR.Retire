package giga.router;

import giga.Giga;
import giga.model.Asset;
import giga.model.User;
import giga.repo.AssetRepo;
import giga.repo.UserRepo;
import giga.service.AssetService;
import jakarta.servlet.http.HttpRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.plsar.model.HttpRequest;
import qio.annotate.*;
import qio.annotate.verbs.Get;
import qio.annotate.verbs.Post;
import qio.model.web.Cache;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Router
public class AssetRouter {

    @Inject
    AssetRepo assetRepo;

    @Inject
    UserRepo userRepo;

    AssetService assetService;

    @Text
    @Get("/go/{n}")
    public String getAsset(HttpServletResponse resp,
                         @RouteComponent String meta) throws Exception {
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
    public String getMedia(HttpServletResponse resp,
                           @RouteComponent String meta) throws Exception {
        String q = meta.toLowerCase();
        Asset asset = assetRepo.get(q);

        URL url = new URL(asset.getUri());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        OutputStream o = resp.getOutputStream();
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

    @Get("/assets/new/{businessId}")
    public String configure(Cache cache,
                            @RouteComponent Long businessId){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);
        cache.set("page", "/pages/asset/new.jsp");
        return "/designs/auth.jsp";
    }

    @Get("/assets/{businessId}")
    public String list(Cache cache,
                       @RouteComponent Long businessId){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, cache);

        List<Asset> assets = assetRepo.getList(businessId);
        cache.set("assets", assets);
        cache.set("page", "/pages/asset/list.jsp");
        return "/designs/auth.jsp";
    }

    @Post("/assets/save")
    public String save(HttpRequest req) throws Exception {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        User authUser = authService.getUser();

        Asset asset = (Asset) req.inflect(req, Asset.class);
        asset.setDateAdded(Giga.getDate());
        asset.setUserId(authUser.getId());

        List<Part> fileParts = req.getParts()
                .stream()
                .filter(part -> "asset".equals(part.getName()) && part.getSize() > 0)
                .collect(Collectors.toList());

        for (Part part : fileParts) {
            String original = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            InputStream is = part.getInputStream();
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
                         @RouteComponent Long businessId,
                         @RouteComponent Long id){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ASSET_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            cache.set("message", "Whoa, people might be using this. Lol, this isn't yours.");
            return "[redirect]/";
        }

        assetRepo.delete(id);
        cache.set("message", "Successfully deleted asset.");

        return "[redirect]/assets/" + businessId;
    }
}
