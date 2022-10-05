package io.service;

import io.Giga;
import io.model.*;
import io.repo.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import qio.Qio;
import qio.annotate.Inject;
import qio.annotate.Service;
import qio.model.web.ResponseData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetService {

    @Inject
    AssetRepo assetRepo;

    @Inject
    UserRepo userRepo;

    @Inject
    SeaService seaService;

    @Inject
    AuthService authService;

    @Inject
    BusinessService businessService;


    public String getAsset(String meta, HttpServletResponse resp) throws Exception {
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

    public String getMedia(String meta, HttpServletResponse resp) throws Exception {
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



    public String create(Long businessId, ResponseData data){
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, data);
        data.set("page", "/pages/asset/new.jsp");
        return "/designs/auth.jsp";
    }


    public String save(HttpServletRequest req) throws Exception {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        User authUser = authService.getUser();

        Asset asset = (Asset) Qio.get(req, Asset.class);
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


    public String list(Long businessId, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }
        businessService.setData(businessId, data);

        List<Asset> assets = assetRepo.getList(businessId);
        data.set("assets", assets);
        data.set("page", "/pages/asset/list.jsp");
        return "/designs/auth.jsp";
    }

    public String delete(Long id, Long businessId, ResponseData data) {
        if(!authService.isAuthenticated()){
            return "[redirect]/";
        }

        String permission = Giga.ASSET_MAINTENANCE + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            data.set("message", "Whoa, people might be using this. Lol, this isn't yours.");
            return "[redirect]/";
        }

        assetRepo.delete(id);
        data.set("message", "Successfully deleted asset.");

        return "[redirect]/assets/" + businessId;
    }

}
