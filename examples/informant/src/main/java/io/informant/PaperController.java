package io.informant;

import com.google.gson.Gson;
import dev.blueocean.annotations.Bind;
import dev.blueocean.annotations.Component;
import dev.blueocean.annotations.Controller;
import dev.blueocean.annotations.Design;
import dev.blueocean.annotations.http.Get;
import dev.blueocean.model.Cache;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.model.NetworkResponse;
import dev.blueocean.security.SecurityManager;
import io.informant.assets.ProgressTracker;
import io.informant.model.*;
import io.informant.model.response.GenericResponse;
import io.informant.model.response.ProgressResponse;
import io.informant.model.response.SheetsResponse;
import io.informant.model.response.SecurityResponse;
import io.informant.repo.PaperRepo;
import io.informant.repo.UserRepo;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class PaperController {

    Gson gson = new Gson();

    @Bind
    UserRepo userRepo;

    @Bind
    PaperRepo paperRepo;

    @Bind
    ControllerHelper controllerHelper;

    Map<String, ProgressResponse> progressResponses;

    public PaperController(){
        progressResponses = new HashMap();
    }

    @Get("/")
    public String index(NetworkRequest req, SecurityManager securityManager){
        if(!securityManager.isAuthenticated(req)){
            return "[redirect]/signin";
        }
        return "index.jsp";
    }

    @Design("/designs/auth.jsp")
    @Get("/sheets/{offset}")
    public String activity(Cache cache, NetworkRequest req, SecurityManager securityManager, @Component Integer offset) throws ParseException {
        if(!securityManager.isAuthenticated(req)){
            return gson.toJson(new SecurityResponse());
        }

        User authUser = controllerHelper.getUser(req, securityManager);

        long startTime = Main.getDate(4);
        long endTime = Main.getDate(0);

        String query = req.getValue("q");
        SheetsResponse sheetsResponse = new SheetsResponse(0L, new ArrayList());
        if(query != null &&
                !query.equals("")){
            if(query.contains("music:")){

            }else if(query.contains("albums:")){

            }else if(query.contains("genres:")){

            }else if(query.contains("users:")){
                String[] usersQueryParts = query.split("users:");
                if(usersQueryParts.length > 1) {
                    String usersQueryBlah = usersQueryParts[1];
                    String usersQuery = usersQueryBlah.trim();
                    Long papersCount = paperRepo.getCountQuery("u.name", usersQuery, startTime, endTime);
                    List<Paper> papers = controllerHelper.getPapersQuery("u.name", usersQuery, startTime, endTime, offset);
                    sheetsResponse = new SheetsResponse(papersCount, papers);
                }else{
                    return gson.toJson(new SheetsResponse(0L, new ArrayList<>()));
                }
            }else if(query.contains("activity:")){
                Long papersCount = paperRepo.getCountQuery("p.content", query, startTime, endTime);
                List<Paper> papers = controllerHelper.getPapersQuery("p.content", query, startTime, endTime, offset);
                sheetsResponse = new SheetsResponse(papersCount, papers);
            }else{
                Long papersCount = paperRepo.getCountQuery("p.content", query, startTime, endTime);
                List<Paper> papers = controllerHelper.getPapersQuery("p.content", query, startTime, endTime, offset);
                sheetsResponse = new SheetsResponse(papersCount, papers);
            }
        }else{
            Long papersCount = paperRepo.getCount(startTime, endTime);
            List<Paper> papers = controllerHelper.getPapers(startTime, endTime, offset, authUser);
            sheetsResponse = new SheetsResponse(papersCount, papers);
        }

        cache.set("sheetsResponse", sheetsResponse);
        return "/pages/sheets.jsp";
    }


    @Json
    @Post("/sheets/save")
    public String save(NetworkRequest req) throws ParseException, IOException {
        if(!SecurityManager.isAuthenticated()){
            return gson.toJson(new SecurityResponse());
        }

        User authdUser = controllerFacilitator.getUser();

        Paper paper = new Paper();
        String content = req.value("material");
        paper.setContent(content);
        paper.setUserId(authdUser.getId());

        RequestComponent photoRequestComponent = req.getRequestComponent("material-photos");
        if(photoRequestComponent != null) {
            List<FileComponent> fileComponents = photoRequestComponent.getFiles();
            StringBuilder photosBuilder = new StringBuilder();
            int index = 1;
            for (FileComponent fileComponent : fileComponents) {
                photosBuilder.append(Main.getBasePrefix(fileComponent.getFileName()));
                photosBuilder.append(Base64.getEncoder().withoutPadding().encodeToString(fileComponent.getFileBytes()));

                if (index < fileComponents.size()) {
                    photosBuilder.append(Main.DELIMITER);
                }
                index++;
            }

            if (!photosBuilder.toString().equals("")) {
                paper.setSixtyFour(photosBuilder.toString());
            }
        }

        RequestComponent videoRequestComponent = req.getRequestComponent("material-video");
        if(videoRequestComponent != null){
            String key = req.value("upload-guid");

            FileComponent fileComponent = videoRequestComponent.getFiles().get(0);

            String extension = Main.getExtension(fileComponent.getFileName());
            String objectKey = Main.getGuid(23) + "."  + extension;

            System.out.println(":" + objectKey);

            paper.setVideo(objectKey);
            progressResponses.put(key, new ProgressResponse("0", 0L));

            Long totalBytesTransferred = 0L;
            ProgressTracker progressTracker = new ProgressTracker(objectKey, key, accessKey, secretKey,  totalBytesTransferred, fileComponent, progressResponses);
            progressTracker.start();

        }

        paper.setTimeCreated(Main.getDate(0));
        paperRepo.save(paper);

        Long lastId = paperRepo.getId();
        Paper storedPaper = paperRepo.get(lastId);

        String permission = "sheets:maintenance:" + lastId;
        userRepo.savePermission(authdUser.getId(), permission);

        SimpleDateFormat format = new SimpleDateFormat(Main.DATE_FORMAT);
        Date postedDate = format.parse(Long.toString(storedPaper.getTimeCreated()));

        PrettyTime prettyTime = new PrettyTime();
        storedPaper.setTimeAgo(prettyTime.format(postedDate));

        if(!storedPaper.getSixtyFour().equals("")) {
            List<String> photos = Arrays.asList(storedPaper.getSixtyFour().split(Main.DELIMITER));
            storedPaper.setPhotos(photos);
        }

        return gson.toJson(storedPaper);
    }

    @Json
    @Get("/sheets/feature/{id}")
    public String paper(@Variable Long id) throws ParseException {
        if(!SecurityManager.isAuthenticated()){
            return gson.toJson(new SecurityResponse());
        }

        Paper storedPaper = paperRepo.get(id);

        if(storedPaper == null){
            return gson.toJson(new GenericResponse("horizons","paper with id " + id + " cannot un-discoverable"));
        }

        SimpleDateFormat format = new SimpleDateFormat(Main.DATE_FORMAT);
        Date postedDate = format.parse(Long.toString(storedPaper.getTimeCreated()));

        PrettyTime prettyTime = new PrettyTime();
        storedPaper.setTimeAgo(prettyTime.format(postedDate));

        if(!storedPaper.getSixtyFour().equals("")) {
            List<String> photos = Arrays.asList(storedPaper.getSixtyFour().split(Main.DELIMITER));
            storedPaper.setPhotos(photos);
        }

        User authdUser = controllerFacilitator.getUser();
        if(authdUser.getId().equals(storedPaper.getUserId())){
            storedPaper.setDeletable(true);
        }

        return gson.toJson(storedPaper);
    }


    @Json
    @Post("/sheets/delete/{id}")
    public String delete(@Variable Long id){
        if(!SecurityManager.isAuthenticated()){
            return gson.toJson(new SecurityResponse());
        }

        String permission = "sheets:maintenance:" + id;
        if(!SecurityManager.hasPermission(permission)){
            return gson.toJson(new SecurityResponse());
        }

        User authdUser = controllerFacilitator.getUser();
        Paper storedPaper = paperRepo.get(id, authdUser.getId());

        if(storedPaper != null) {
            paperRepo.delete(id);
        }

        return gson.toJson(new GenericResponse("kosher", "successfully deleted."));
    }

    @Json
    @Post("/heart/{id}")
    public String heart(@Variable Long id) throws ParseException {
        if(!SecurityManager.isAuthenticated()){
            return gson.toJson(new SecurityResponse());
        }

        User authdUser = controllerFacilitator.getUser();
        Paper storedPaper = paperRepo.get(id);

        if(storedPaper == null){
            return gson.toJson(new GenericResponse("horizons","paper with id " + id + " cannot un-discoverable"));
        }

        Heart storedHeart = paperRepo.getHeart(id, authdUser.getId());

        if(storedHeart == null) {

            Heart heart = new Heart();
            heart.setUserId(authdUser.getId());
            heart.setPaperId(id);
            paperRepo.heart(heart);

            Integer likesCount = storedPaper.getLikesCount() + 1;
            storedPaper.setLikesCount(likesCount);
        }

        if(storedHeart != null){
            paperRepo.deleteHeart(storedHeart.getId());
            Integer likesCount = storedPaper.getLikesCount() - 1;
            storedPaper.setLikesCount(likesCount);
        }

        SimpleDateFormat format = new SimpleDateFormat(Main.DATE_FORMAT);
        Date postedDate = format.parse(Long.toString(storedPaper.getTimeCreated()));

        PrettyTime prettyTime = new PrettyTime();
        storedPaper.setTimeAgo(prettyTime.format(postedDate));

        if(!storedPaper.getSixtyFour().equals("")) {
            List<String> photos = Arrays.asList(storedPaper.getSixtyFour().split(Main.DELIMITER));
            storedPaper.setPhotos(photos);
        }

        paperRepo.update(storedPaper);

        return gson.toJson(storedPaper);

    }

    @Json
    @Get("/video/{guid}")
    public String getProgress(@Variable String guid){
        if(progressResponses.containsKey(guid)){
            ProgressResponse progressResponse = progressResponses.get(guid);
            return gson.toJson(progressResponse);
        }
        return gson.toJson(new ProgressResponse("0", 0L));
    }


}
