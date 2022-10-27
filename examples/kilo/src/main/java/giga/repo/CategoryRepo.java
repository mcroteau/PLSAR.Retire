package giga.repo;

import giga.model.Category;
import giga.model.ItemCategory;
import dev.blueocean.Dao;
import dev.blueocean.annotations.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoryRepo {

    Dao dao;

    public CategoryRepo(Dao dao){
        this.dao = dao;
    }

    public Category getSaved() {
        String idSql = "select max(id) from categories";
        long id = dao.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getCount() {
        String sql = "select count(*) from categories";
        Long count = (Long) dao.getLong(sql, new Object[] { });
        return count;
    }

    public Category get(long id){
        String sql = "select * from categories where id = [+]";
        Category category = (Category) dao.get(sql, new Object[] { id }, Category.class);
        return category;
    }

    public Category get(String uri){
        String sql = "select * from categories where uri = '[+]'";
        Category category = (Category) dao.get(sql, new Object[] { uri }, Category.class);
        return category;
    }

    public Category get(String uri, long businessId){
        String sql = "select * from categories where uri = '[+]' and business_id = [+]";
        Category category = (Category) dao.get(sql, new Object[] { uri, businessId }, Category.class);
        return category;
    }

    public Category get(String name, String uri, Long businessId) {
        String sql = "select * from categories where name = '[+]' and uri = '[+]' and business_id = [+]";
        Category category = (Category) dao.get(sql, new Object[]{ name, uri, businessId}, Category.class);
        return category;
    }

    public Category getOne(Long businessId) {
        String sql = "select * from categories where business_id = [+] limit 1";
        Category category = (Category) dao.get(sql, new Object[] { businessId }, Category.class);
        return category;
    }

    public List<Category> getList(){
        String sql = "select * from categories order by id desc";
        List<Category> categories = (ArrayList) dao.getList(sql, new Object[]{}, Category.class);
        return categories;
    }

    public List<Category> getList(long id, long businessId){
        String sql = "select * from categories where category_id = [+] and business_id = [+] order by id desc";
        List<Category> categories = (ArrayList) dao.getList(sql, new Object[]{ id, businessId }, Category.class);
        return categories;
    }

    public List<Category> getList(long id, boolean topLevel){
        String sql = "select * from categories where business_id = [+] and top_level = [+] order by id desc";
        List<Category> categories = (ArrayList) dao.getList(sql, new Object[]{ id, topLevel }, Category.class);
        return categories;
    }

    public List<Category> getListAll(long id){
        String sql = "select * from categories where business_id = [+] order by id desc";
        List<Category> categories = (ArrayList) dao.getList(sql, new Object[]{ id }, Category.class);
        return categories;
    }

    public List<ItemCategory> getItemsBusiness(long id){
        String sql = "select * from item_categories where business_id = [+]";
        List<ItemCategory> itemCategories = (ArrayList) dao.getList(sql, new Object[]{ id }, ItemCategory.class);
        return itemCategories;
    }

    public List<ItemCategory> getItems(long id){
        String sql = "select * from item_categories where category_id = [+]";
        List<ItemCategory> itemCategories = (ArrayList) dao.getList(sql, new Object[]{ id }, ItemCategory.class);
        return itemCategories;
    }

    public List<ItemCategory> getCategoryItems(long id){
        String sql = "select * from item_categories where item_id = [+]";
        List<ItemCategory> itemCategories = (ArrayList) dao.getList(sql, new Object[]{ id }, ItemCategory.class);
        return itemCategories;
    }


    public Boolean save(Category category){
        String sql = "insert into categories (name, uri, header, design_id, business_id, category_id, top_level) values ('[+]','[+]','[+]',[+],[+],[+],[+])";
        dao.save(sql, new Object[] {
                category.getName(),
                category.getUri(),
                category.getHeader(),
                category.getDesignId(),
                category.getBusinessId(),
                category.getCategoryId(),
                category.getTopLevel()
        });
        return true;
    }

    public Boolean saveItem(ItemCategory itemCategory){
        String sql = "insert into item_categories (item_id, category_id, business_id) values ([+],[+],[+])";
        dao.save(sql, new Object[] {
                itemCategory.getItemId(),
                itemCategory.getCategoryId(),
                itemCategory.getBusinessId()
        });
        return true;
    }

    public Boolean update(Category category){
        String sql = "update categories set name = '[+]', uri = '[+]', header = '[+]', design_id = [+], category_id = [+], top_level = [+] where id = [+]";
        dao.update(sql, new Object[] {
                category.getName(),
                category.getUri(),
                category.getHeader(),
                category.getDesignId(),
                category.getCategoryId(),
                category.getTopLevel(),
                category.getId()
        });
        return true;
    }

    public boolean delete(long id){
        String sql = "delete from categories where id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteCategoryItems(long id){
        String sql = "delete from item_categories where item_id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteCategories(long id){
        String sql = "delete from categories where business_id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

}
