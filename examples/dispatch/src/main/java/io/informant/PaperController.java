package io.informant;

import net.plsar.annotations.*;
import net.plsar.annotations.Component;
import net.plsar.annotations.http.Get;
import net.plsar.annotations.http.Post;
import net.plsar.model.*;
import net.plsar.security.SecurityManager;
import io.informant.model.*;
import io.informant.repo.PaperRepo;
import io.informant.repo.UserRepo;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class PaperController {

    Informant informant;

    public PaperController(){
        this.informant = new Informant();
    }

    @Bind
    UserRepo userRepo;

    @Bind
    PaperRepo paperRepo;

    @Bind
    ControllerHelper controllerHelper;

    @Get("/")
    public String index(NetworkRequest req, SecurityManager securityManager){
        if(!securityManager.isAuthenticated(req)){
            return "redirect:/signin";
        }
        return "redirect:/sheets/0";
    }

    @Design("/designs/sheets.jsp")
    @Get("/sheets/{offset}")
    public String activity(PageCache cache, NetworkRequest req, SecurityManager securityManager, @Component Integer offset) throws ParseException {
        if(!securityManager.isAuthenticated(req)){
            return "redirect:/signin";
        }

        User authUser = controllerHelper.getUser(req, securityManager);

        long startTime = informant.getDate(7);
        long endTime = informant.getDate(0);

        String query = req.getValue("q");
        List<Paper> papers = new ArrayList<>();

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
                    papers = controllerHelper.getPapersQuery("u.name", usersQuery, startTime, endTime, offset);
                }else{
                    return "redirect:/sheets/0";
                }
            }else if(query.contains("activity:")){
                papers = controllerHelper.getPapersQuery("p.content", query, startTime, endTime, offset);
            }else{
                papers = controllerHelper.getPapersQuery("p.content", query, startTime, endTime, offset);
            }
        }else{
            papers = controllerHelper.getPapers(startTime, endTime, offset, authUser);
        }

        cache.set("date", new Date());
        cache.set("papers", papers);
        return "/pages/paper/sheets.jsp";
    }


    @Post("/sheets/save")
    public String save(NetworkRequest req, SecurityManager securityManager) throws ParseException, IOException {
        if(!securityManager.isAuthenticated(req)){
            return "redirect:/signin";
        }

        User authdUser = controllerHelper.getUser(req, securityManager);

        Paper paper = new Paper();
        String content = req.getValue("material");
        paper.setContent(content);
        paper.setUserId(authdUser.getId());

        RequestComponent photoRequestComponent = req.getRequestComponent("material-photos");
        if(photoRequestComponent != null) {
            List<FileComponent> fileComponents = photoRequestComponent.getFileComponents();
            StringBuilder photosBuilder = new StringBuilder();
            int index = 1;
            for (FileComponent fileComponent : fileComponents) {
                photosBuilder.append(informant.getBasePrefix(fileComponent.getFileName()));
                photosBuilder.append(Base64.getEncoder().withoutPadding().encodeToString(fileComponent.getFileBytes()));

                if (index < fileComponents.size()) {
                    photosBuilder.append(informant.getDelimeter());
                }
                index++;
            }

            if (!photosBuilder.toString().equals("")) {
                paper.setSixtyFour(photosBuilder.toString());
            }
        }

//        RequestComponent videoRequestComponent = req.getRequestComponent("material-video");
//        if(videoRequestComponent != null){
//            String key = req.getValue("upload-guid");
//
//            FileComponent fileComponent = videoRequestComponent.getFileComponents().get(0);
//
//            String extension = Main.getExtension(fileComponent.getFileName());
//            String objectKey = Main.getGuid(23) + "."  + extension;
//
//            System.out.println(":" + objectKey);
//
//            paper.setVideo(objectKey);
//            progressResponses.put(key, new ProgressResponse("0", 0L));
//
//            Long totalBytesTransferred = 0L;
//            ProgressTracker progressTracker = new ProgressTracker(objectKey, key, accessKey, secretKey,  totalBytesTransferred, fileComponent, progressResponses);
//            progressTracker.start();
//        }

        paper.setTimeCreated(informant.getDate(0));
        paperRepo.save(paper);

        Long lastId = paperRepo.getId();
        Paper storedPaper = paperRepo.get(lastId);

        String permission = "sheets:maintenance:" + lastId;
        userRepo.savePermission(authdUser.getId(), permission);

        SimpleDateFormat format = new SimpleDateFormat(informant.getDateFormat());
        Date postedDate = format.parse(Long.toString(storedPaper.getTimeCreated()));

        PrettyTime prettyTime = new PrettyTime();
        storedPaper.setTimeAgo(prettyTime.format(postedDate));

        if(!storedPaper.getSixtyFour().equals("")) {
            List<String> photos = Arrays.asList(storedPaper.getSixtyFour().split(informant.getDelimeter()));
            storedPaper.setPhotos(photos);
        }

        return "redirect:/sheets/feature/" + storedPaper.getId();
    }

    @Design("/designs/sheets.jsp")
    @Get("/sheets/feature/{id}")
    public String paper(PageCache pageCache, NetworkRequest req, SecurityManager securityManager, @Component Long id) throws ParseException {
        if(!securityManager.isAuthenticated(req)){
            return "redirect:/signin";
        }

        Paper storedPaper = paperRepo.get(id);

        if(storedPaper == null){
            return "redirect:/";
        }

        SimpleDateFormat format = new SimpleDateFormat(informant.getDelimeter());
        Date postedDate = format.parse(Long.toString(storedPaper.getTimeCreated()));

        PrettyTime prettyTime = new PrettyTime();
        storedPaper.setTimeAgo(prettyTime.format(postedDate));

        if(!storedPaper.getSixtyFour().equals("")) {
            List<String> photos = Arrays.asList(storedPaper.getSixtyFour().split(informant.getDelimeter()));
            storedPaper.setPhotos(photos);
        }

        User authdUser = controllerHelper.getUser(req, securityManager);
        if(authdUser.getId().equals(storedPaper.getUserId())){
            storedPaper.setDeletable(true);
        }

        pageCache.set("storedPaper", storedPaper);
        return "/pages/paper/feature.jsp";
    }

    @Post("/sheets/delete/{id}")
    public String delete(PageCache pageCache, NetworkRequest req, SecurityManager securityManager, @Component Long id){
        if(!securityManager.isAuthenticated(req)){
            //building a tool from a tool for a bunch of good people.
            pageCache.set("message", "authentication required.");
            return "redirect:/sheets/feature/" + id;
        }

        String permission = "sheets:maintenance:" + id;
        if(!securityManager.hasPermission(permission, req)){
            pageCache.set("message", "permission required.");
            return "redirect:/sheets/feature/" + id;
        }

        User authdUser = controllerHelper.getUser(req, securityManager);
        Paper storedPaper = paperRepo.get(id, authdUser.getId());

        if(storedPaper != null) {
            paperRepo.delete(id);
        }
        pageCache.set("message", "successfully deleted.");
        return "redirect:/sheets/0";
    }

    @Post("/sheets/heart/{id}")
    public String heart(PageCache pageCache, NetworkRequest req, SecurityManager securityManager, @Component Long id) throws ParseException {
        if(!securityManager.isAuthenticated(req)){
            pageCache.set("message", "authentication required.");
            return "redirect:/sheets/feature/" + id;
        }

        User authUser = controllerHelper.getUser(req, securityManager);
        Paper storedPaper = paperRepo.get(id);

        if(storedPaper == null){
            pageCache.set("message", "paper with id " + id + " un-discoverable");
            return "redirect:/sheets/feature/" + id;
        }

        Heart storedHeart = paperRepo.getHeart(id, authUser.getId());

        if(storedHeart == null) {

            Heart heart = new Heart();
            heart.setUserId(authUser.getId());
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

        SimpleDateFormat format = new SimpleDateFormat(informant.getDateFormat());
        Date postedDate = format.parse(Long.toString(storedPaper.getTimeCreated()));

        PrettyTime prettyTime = new PrettyTime();
        storedPaper.setTimeAgo(prettyTime.format(postedDate));

        paperRepo.update(storedPaper);

        return "redirect:/sheets/feature/" + storedPaper.getId();
    }

}
