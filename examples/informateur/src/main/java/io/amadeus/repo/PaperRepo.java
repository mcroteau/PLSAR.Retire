package io.amadeus.repo;

import io.amadeus.Main;
import io.amadeus.model.Heart;
import io.amadeus.model.Paper;
import io.amadeus.model.User;
import io.amadeus.model.UserFollow;
import io.kakai.Kakai;
import io.kakai.annotate.Bind;
import io.kakai.annotate.Repo;

import java.util.ArrayList;
import java.util.List;

@Repo
public class PaperRepo {

    //todo:no artists by default

    @Bind
    Kakai kakai;

    public long getId() {
        String sql = "select max(id) from sheets";
        long id = kakai.getLong(sql, new Object[]{});
        return id;
    }

    public Long getCount() {
        String sql = "select count(*) from sheets";
        Long count = kakai.getLong(sql, new Object[]{});
        return count;
    }

    public Long getCountQuery(String column, String query, long startTime, long endTime) {
        String sql = "select p.id, u.name, count(distinct p.id) as count " +
                "from sheets p inner join users u on p.user_id = u.id " +
                "where lower([+]) like lower('%[+]%') and p.time_created between [+] and [+] group by p.id";
        Long count = kakai.getLong(sql, new Object[]{column, query, startTime, endTime});
        return count;
    }

    public Long getCount(long startTime, long endTime) {
        String sql = "select count(*) from sheets where time_created between [+] and [+]";
        Long count = kakai.getLong(sql, new Object[]{startTime, endTime});
        return count;
    }

    public Paper get(long id){
        String sql = "select p.id, p.user_id, p.content, p.time_created," +
                "p.likes_count, p.shares_count, p.sixty_four, p.video, u.name, u.photo " +
                "from sheets p inner join users u on p.user_id = u.id " +
                "where p.id = [+]";

        Paper paper = (Paper) kakai.get(sql, new Object[]{ id }, Paper.class);
        return paper;
    }

    public Paper get(long id, long userId){
        String sql = "select * from sheets where id = [+]";
        Paper paper = (Paper) kakai.get(sql, new Object[]{ id, userId }, Paper.class);
        return paper;
    }

    public List<Paper> getList(){
        String sql = "select * from sheets";
        List<Paper> papers = (ArrayList) kakai.getList(sql, new Object[]{}, Paper.class);
        return papers;
    }

    public List<Paper> getList(Long userId){
        String sql = "select * from sheets where user_id = [+] order by time_created desc";
        List<Paper> papers = (ArrayList) kakai.getList(sql, new Object[]{ userId }, Paper.class);
        return papers;
    }

    public List<Paper> getList(Integer offset, Long startDate, Long endDate, User authdUser, List<UserFollow> userFollows){
        String ids = Main.toIds(authdUser, userFollows);
        String sql = "select p.id, p.user_id, p.content, p.time_created," +
                "p.likes_count, p.shares_count, p.sixty_four, p.video, u.name, u.photo " +
                "from sheets p inner join users u on p.user_id = u.id " +
                " where user_id in ([+]) and p.time_created between [+] and [+] " +
                "order by p.time_created desc limit 3 offset [+]";

        List<Paper> papers = (ArrayList) kakai.getList(sql, new Object[]{ ids, startDate, endDate, offset }, Paper.class);
        return papers;
    }

    public List<Paper> getListQuery(Integer offset, String column, String query, Long startDate, Long endDate) {
        String sql = "select p.id, p.user_id, p.content, p.time_created," +
                "p.likes_count, p.shares_count, p.sixty_four, p.video, u.name, u.photo " +
                "from sheets p inner join users u on p.user_id = u.id " +
                " where lower([+]) like lower('%[+]%') and p.time_created between [+] and [+] " +
                "order by p.time_created desc limit 3 offset [+]";

        List<Paper> papers = (ArrayList) kakai.getList(sql, new Object[]{ column, query, startDate, endDate, offset }, Paper.class);
        return papers;
    }

    public void save(Paper paper){
        String sql = "insert into sheets (user_id, content, sixty_four, video, origin_id, origin_content, time_created) " +
                "values ([+],'[+]','[+]','[+]',[+],'[+]',[+])";
        kakai.save(sql, new Object[] {
                paper.getUserId(),
                paper.getContent(),
                paper.getSixtyFour(),
                paper.getVideo(),
                paper.getOriginId(),
                paper.getOriginContent(),
                paper.getTimeCreated()
        });
    }

    public void update(Paper paper) {
        String sql = "update sheets set likes_count = [+], shares_count = [+] where id = [+]";
        kakai.update(sql, new Object[]{
                paper.getLikesCount(),
                paper.getSharesCount(),
                paper.getId()
        });
    }

    public boolean delete(long id){
        String sql = "delete from sheets where id = [+]";
        kakai.delete(sql, new Object[] { id });
        return true;
    }

    public void heart(Heart heart) {
        String sql = "insert into hearts (user_id, paper_id) values ([+],[+])";
        kakai.save(sql, new Object[] {
                heart.getUserId(),
                heart.getPaperId()
        });
    }

    public Heart getHeart(Long paperId, Long userId) {
        String sql = "select * from hearts where paper_id = [+] and user_id = [+]";
        Heart heart = (Heart) kakai.get(sql, new Object[]{ paperId, userId }, Heart.class);
        return heart;
    }

    public void deleteHeart(Long id) {
        String sql = "delete from hearts where id = [+]";
        kakai.delete(sql, new Object[] { id });
    }

}
