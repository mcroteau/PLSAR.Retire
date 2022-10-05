package io.repo;

import io.model.Page;
import qio.Qio;
import qio.annotate.DataStore;
import qio.annotate.Inject;

import java.util.ArrayList;
import java.util.List;

@DataStore
public class PageRepo {

    @Inject
    Qio qio;

    public Page getSaved() {
        String idSql = "select max(id) from pages";
        long id = qio.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getCount() {
        String sql = "select count(*) from pages";
        Long count = (Long) qio.getLong(sql, new Object[] { });
        return count;
    }

    public Page get(long id){
        String sql = "select * from pages where id = [+]";
        Page page = (Page) qio.get(sql, new Object[] { id }, Page.class);
        return page;
    }

    public Page get(long id, long businessId){
        String sql = "select * from pages where id = [+] and business_id = [+]";
        Page page = (Page) qio.get(sql, new Object[] { id, businessId }, Page.class);
        return page;
    }

    public Page get(long id, String uri){
        String sql = "select * from pages where business_id = [+] and uri = '[+]'";
        Page page = (Page) qio.get(sql, new Object[] { id, uri }, Page.class);
        return page;
    }

    public List<Page> getList(){
        String sql = "select * from pages order by id desc";
        List<Page> pages = (ArrayList) qio.getList(sql, new Object[]{}, Page.class);
        return pages;
    }

    public List<Page> getList(long id){
        String sql = "select * from pages where business_id = [+] order by id desc";
        List<Page> pages = (ArrayList) qio.getList(sql, new Object[]{ id }, Page.class);
        return pages;
    }

    public Boolean save(Page page){
        String sql = "insert into pages (name, uri, content, design_id, business_id) values ('[+]','[+]','[+]',[+],[+])";
        qio.save(sql, new Object[] {
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
        qio.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deletePages(long id){
        String sql = "delete from pages where business_id = [+]";
        qio.delete(sql, new Object[] { id });
        return true;
    }
}
