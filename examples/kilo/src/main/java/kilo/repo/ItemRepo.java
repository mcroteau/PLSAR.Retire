package kilo.repo;

import kilo.model.*;
import net.plsar.Dao;
import net.plsar.annotations.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ItemRepo {

    Dao dao;

    public ItemRepo(Dao dao){
        this.dao = dao;
    }

    public Item getSaved() {
        String idSql = "select max(id) from items";
        long id = dao.getLong(idSql, new Object[]{});
        return get(id);
    }

    public ItemOption getSavedOption() {
        String idSql = "select max(id) from item_options";
        long id = dao.getLong(idSql, new Object[]{});
        return getOption(id);
    }

    public long getCount() {
        String sql = "select count(*) from items";
        Long count = (Long) dao.getLong(sql, new Object[] { });
        return count;
    }

    public Item get(long id){
        String sql = "select * from items where id = [+]";
        Item item = (Item) dao.get(sql, new Object[] { id }, Item.class);
        return item;
    }

    public Item get(long id, long businessId){
        String sql = "select * from items where id = [+] and business_id = [+]";
        Item item = (Item) dao.get(sql, new Object[] { id, businessId }, Item.class);
        return item;
    }

    public List<Item> getList(){
        String sql = "select * from items order by id desc";
        List<Item> items = (ArrayList) dao.getList(sql, new Object[]{}, Item.class);
        return items;
    }

    public List<Item> getList(long id){
        String sql = "select * from items where business_id = [+] and active = true order by id desc";
        List<Item> items = (ArrayList) dao.getList(sql, new Object[]{ id }, Item.class);
        return items;
    }

    public List<Item> getList(Long id, Boolean active){
        String sql = "select * from items where business_id = [+] and active = [+] order by id desc";
        List<Item> items = (ArrayList) dao.getList(sql, new Object[]{ id, active }, Item.class);
        return items;
    }

    public List<CategoryItem> getListItems(long id, long businessId){
        String sql = "select * from category_items where category_id = [+] and business_id = [+] order by item_id desc";
        List<CategoryItem> items = (ArrayList) dao.getList(sql, new Object[]{ id, businessId }, CategoryItem.class);
        return items;
    }

    public Boolean save(Item item){
        String sql = "insert into items (name, description, price, affiliate_price, quantity, image_uri, weight, cost, business_id, design_id) values ('[+]','[+]',[+],[+],[+],'[+]',[+],[+],[+],[+])";
        dao.save(sql, new Object[] {
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getAffiliatePrice(),
                item.getQuantity(),
                item.getImageUri(),
                item.getWeight(),
                item.getCost(),
                item.getBusinessId(),
                item.getDesignId(),
        });
        return true;
    }

    public Boolean update(Item item){
        String sql = "update items set name = '[+]', description = '[+]', price = [+], affiliate_price = [+], quantity = [+], image_uri = '[+]', weight = [+], cost = [+], design_id = [+], active = [+] where id = [+]";
        dao.update(sql, new Object[] {
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getAffiliatePrice(),
                item.getQuantity(),
                item.getImageUri(),
                item.getWeight(),
                item.getCost(),
                item.getDesignId(),
                item.getActive(),
                item.getId()
        });
        return true;
    }

    public boolean delete(long id){
        String sql = "delete from items where id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteItems(long id){
        String sql = "delete from items where business_id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteItemImports(long id){
        String sql = "delete from items where import_id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

    public ItemOption getOption(long id){
        String sql = "select * from item_options where id = [+]";
        ItemOption itemOption = (ItemOption) dao.get(sql, new Object[] { id }, ItemOption.class);
        return itemOption;
    }

    public List<ItemOption> getOptions(long id){
        String sql = "select * from item_options where item_id = [+] order by id asc";
        List<ItemOption> itemOptions = (ArrayList) dao.getList(sql, new Object[]{ id }, ItemOption.class);
        return itemOptions;
    }

    public Boolean saveOption(ItemOption itemOption){
        String sql = "insert into item_options (item_id, name) values ([+],'[+]')";
        dao.save(sql, new Object[] {
                itemOption.getItemId(),
                itemOption.getName()
        });
        return true;
    }

    public boolean deleteOption(long id){
        String sql = "delete from item_options where id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteOptions(long id){
        String sql = "delete from item_options where item_id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteValues(long id){
        String sql = "delete from option_values where item_option_id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

    public OptionValue getValue(long id){
        String sql = "select * from option_values where id = [+]";
        OptionValue optionValue = (OptionValue) dao.get(sql, new Object[] { id }, OptionValue.class);
        return optionValue;
    }

    public List<OptionValue> getValues(long id){
        String sql = "select * from option_values where item_option_id = [+] order by id asc";
        List<OptionValue> optionValues = (ArrayList) dao.getList(sql, new Object[]{ id }, OptionValue.class);
        return optionValues;
    }

    public Boolean saveValue(OptionValue optionValue){
        String sql = "insert into option_values (item_option_id, value, price, quantity) values ([+],'[+]',[+],[+])";
        dao.save(sql, new Object[] {
                optionValue.getItemOptionId(),
                optionValue.getValue(),
                optionValue.getPrice(),
                optionValue.getQuantity()
        });
        return true;
    }

    public boolean deleteValue(long id){
        String sql = "delete from option_values where id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

    public List<Item> q(String query, Long id) {
        String sql = "select * from items where name like '%[+]%' and business_id = [+] order by id asc";
        List<Item> items = (ArrayList) dao.getList(sql, new Object[]{query, id}, Item.class);
        return items;
    }
}
