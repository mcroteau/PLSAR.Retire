package giga.repo;

import giga.model.Page;
import dev.blueocean.Dao;
import net.plsar.annotations.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PageRepo {

    Dao dao;

    public PageRepo(Dao dao){
        this.dao = dao;
    }

    public Page getSaved() {
        String idSql = "select max(id) from pages";
        long id = dao.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getCount() {
        String sql = "select count(*) from pages";
        Long count = (Long) dao.getLong(sql, new Object[] { });
        return count;
    }

    public Page get(long id){
        String sql = "select * from pages where id = [+]";
        Page page = (Page) dao.get(sql, new Object[] { id }, Page.class);
        return page;
    }

    public Page get(long id, long businessId){
        String sql = "select * from pages where id = [+] and business_id = [+]";
        Page page = (Page) dao.get(sql, new Object[] { id, businessId }, Page.class);
        return page;
    }

    public Page get(long id, String uri){
        String sql = "select * from pages where business_id = [+] and uri = '[+]'";
        Page page = (Page) dao.get(sql, new Object[] { id, uri }, Page.class);
        return page;
    }

    public List<Page> getList(){
        String sql = "select * from pages order by id desc";
        List<Page> pages = (ArrayList) dao.getList(sql, new Object[]{}, Page.class);
        return pages;
    }

    public List<Page> getList(long id){
        String sql = "select * from pages where business_id = [+] order by id desc";
        List<Page> pages = (ArrayList) dao.getList(sql, new Object[]{ id }, Page.class);
        return pages;
    }

    public Boolean save(Page page){
        String sql = "insert into pages (name, uri, content, design_id, business_id) values ('[+]','[+]','[+]',[+],[+])";
        dao.save(sql, new Object[] {
                page.getName(),
                page.getUri(),
                page.getContent(),
                page.getDesignId(),
                page.getBusinessId()
        });
        return true;
    }

    public boolean delete(long id){
        String sql = "delete from pages where id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deletePages(long id){
        String sql = "delete from pages where business_id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }
}
