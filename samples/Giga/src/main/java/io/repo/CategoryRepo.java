package io.repo;

import io.model.Category;
import io.model.CategoryItem;
import qio.Qio;
import qio.annotate.DataStore;
import qio.annotate.Inject;

import java.util.ArrayList;
import java.util.List;

@DataStore
public class CategoryRepo {

    @Inject
    Qio qio;

    public Category getSaved() {
        String idSql = "select max(id) from categories";
        long id = qio.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getCount() {
        String sql = "select count(*) from categories";
        Long count = (Long) qio.getLong(sql, new Object[] { });
        return count;
    }

    public Category get(long id){
        String sql = "select * from categories where id = [+]";
        Category category = (Category) qio.get(sql, new Object[] { id }, Category.class);
        return category;
    }

    public Category get(String uri){
        String sql = "select * from categories where uri = '[+]'";
        Category category = (Category) qio.get(sql, new Object[] { uri }, Category.class);
        return category;
    }

    public Category get(String uri, long businessId){
        String sql = "select * from categories where uri = '[+]' and business_id = [+]";
        Category category = (Category) qio.get(sql, new Object[] { uri, businessId }, Category.class);
        return category;
    }

    public Category get(String name, String uri, Long businessId) {
        String sql = "select * from categories where name = '[+]' and uri = '[+]' and business_id = [+]";
        Category category = (Category) qio.get(sql, new Object[]{ name, uri, businessId}, Category.class);
        return category;
    }

    public Category getOne(Long businessId) {
        String sql = "select * from categories where business_id = [+] limit 1";
        Category category = (Category) qio.get(sql, new Object[] { businessId }, Category.class);
        return category;
    }

    public List<Category> getList(){
        String sql = "select * from categories order by id desc";
        List<Category> categories = (ArrayList) qio.getList(sql, new Object[]{}, Category.class);
        return categories;
    }

    public List<Category> getList(long id, long businessId){
        String sql = "select * from categories where category_id = [+] and business_id = [+] order by id desc";
        List<Category> categories = (ArrayList) qio.getList(sql, new Object[]{ id, businessId }, Category.class);
        return categories;
    }

    public List<Category> getList(long id, boolean topLevel){
        String sql = "select * from categories where business_id = [+] and top_level = [+] order by id desc";
        List<Category> categories = (ArrayList) qio.getList(sql, new Object[]{ id, topLevel }, Category.class);
        return categories;
    }

    public List<Category> getListAll(long id){
        String sql = "select * from categories where business_id = [+] order by id desc";
        List<Category> categories = (ArrayList) qio.getList(sql, new Object[]{ id }, Category.class);
        return categories;
    }

    public List<CategoryItem> getItemsBusiness(long id){
        String sql = "select * from category_items where business_id = [+]";
        List<CategoryItem> categoryItems = (ArrayList) qio.getList(sql, new Object[]{ id }, CategoryItem.class);
        return categoryItems;
    }

    public List<CategoryItem> getItems(long id){
        String sql = "select * from category_items where category_id = [+]";
        List<CategoryItem> categoryItems = (ArrayList) qio.getList(sql, new Object[]{ id }, CategoryItem.class);
        return categoryItems;
    }

    public List<CategoryItem> getCategoryItems(long id){
        String sql = "select * from category_items where item_id = [+]";
        List<CategoryItem> categoryItems = (ArrayList) qio.getList(sql, new Object[]{ id }, CategoryItem.class);
        return categoryItems;
    }


    public Boolean save(Category category){
        String sql = "insert into categories (name, uri, header, design_id, business_id, category_id, top_level) values ('[+]','[+]','[+]',[+],[+],[+],[+])";
        qio.save(sql, new Object[] {
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

    public Boolean saveItem(CategoryItem categoryItem){
        String sql = "insert into category_items (item_id, category_id, business_id) values ([+],[+],[+])";
        qio.save(sql, new Object[] {
                categoryItem.getItemId(),
                categoryItem.getCategoryId(),
                categoryItem.getBusinessId()
        });
        return true;
    }

    public Boolean update(Category category){
        String sql = "update categories set name = '[+]', uri = '[+]', header = '[+]', design_id = [+], category_id = [+], top_level = [+] where id = [+]";
        qio.update(sql, new Object[] {
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
        qio.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteCategoryItems(long id){
        String sql = "delete from category_items where item_id = [+]";
        qio.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteCategories(long id){
        String sql = "delete from categories where business_id = [+]";
        qio.delete(sql, new Object[] { id });
        return true;
    }

}
