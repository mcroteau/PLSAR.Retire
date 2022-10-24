package io.amadeus.assets;

import io.amadeus.Main;
import io.amadeus.model.Paper;
import io.amadeus.model.User;
import io.amadeus.model.UserFollow;
import io.amadeus.repo.PaperRepo;
import io.amadeus.repo.UserRepo;
import io.kakai.annotate.Bind;
import io.kakai.annotate.Service;
import io.kakai.security.SecurityManager;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class Facilitator {

    @Bind
    UserRepo userRepo;

    @Bind
    PaperRepo paperRepo;

    public User getUser(){
        String credential = SecurityManager.getUser();
        User authdUser = userRepo.getPhone(credential);
        if(authdUser == null)authdUser = userRepo.getEmail(credential);
        return authdUser;
    }

    public List<Paper> getPapers(Long startTime, Long endTime, Integer offset, User authdUser) throws ParseException {

        List<UserFollow> userFollows = userRepo.getFollows(authdUser.getId());
        List<Paper> papers = paperRepo.getList(offset, startTime, endTime, authdUser, userFollows);
        List<Paper> papersGo = getPopulatePapersMetaData(papers);

        return papersGo;
    }

    public List<Paper> getPapersQuery(String column, String query, long startTime, long endTime, Integer offset) throws ParseException {
        List<Paper> papers = paperRepo.getListQuery(offset, column, query, startTime, endTime);
        List<Paper> papersGo = getPopulatePapersMetaData(papers);
        return papersGo;
    }

    List<Paper> getPopulatePapersMetaData(List<Paper> papers) throws ParseException {
        List<Paper> papersGo = new ArrayList<>();
        for(Paper paper : papers){
            SimpleDateFormat format = new SimpleDateFormat(Main.DATE_FORMAT);
            Date postedDate = format.parse(Long.toString(paper.getTimeCreated()));

            PrettyTime prettyTime = new PrettyTime();
            paper.setTimeAgo(prettyTime.format(postedDate));

            if(paper.getVideo() != null && !paper.getVideo().equals(""))paper.setHasVideo(true);

            if(!paper.getSixtyFour().equals("")) {
                List<String> photos = Arrays.asList(paper.getSixtyFour().split(Main.DELIMITER));
                paper.setPhotos(photos);
            }
            papersGo.add(paper);
        }
        return papersGo;
    }
}
