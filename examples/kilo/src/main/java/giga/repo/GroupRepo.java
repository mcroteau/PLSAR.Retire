package giga.repo;

import giga.model.GroupOption;
import giga.model.ItemGroup;
import dev.blueocean.Dao;
import net.plsar.annotations.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GroupRepo {

    Dao dao;

    public GroupRepo(Dao dao){
        this.dao = dao;
    }

    public ItemGroup getSaved() {
        String idSql = "select max(id) from item_groups";
        long id = dao.getLong(idSql, new Object[]{});
        System.out.println("id " + id);
        return get(id);
    }

    public long getCount() {
        String sql = "select count(*) from item_groups";
        Long count = (Long) dao.getLong(sql, new Object[] { });
        return count;
    }

    public ItemGroup get(long id){
        String sql = "select * from item_groups where id = [+]";
        ItemGroup itemGroup = (ItemGroup) dao.get(sql, new Object[] { id }, ItemGroup.class);
        return itemGroup;
    }

    public ItemGroup get(String name){
        String sql = "select * from item_groups where name = '[+]'";
        ItemGroup itemGroup = (ItemGroup) dao.get(sql, new Object[] { name }, ItemGroup.class);
        return itemGroup;
    }

    public ItemGroup get(long id, long businessId){
        String sql = "select * from item_groups where id = [+] and business_id = [+]";
        ItemGroup itemGroup = (ItemGroup) dao.get(sql, new Object[] { id, businessId }, ItemGroup.class);
        return itemGroup;
    }

    public List<ItemGroup> getList(){
        String sql = "select * from item_groups order by id desc";
        List<ItemGroup> itemItemGroups = (ArrayList) dao.getList(sql, new Object[]{}, ItemGroup.class);
        return itemItemGroups;
    }

    public List<ItemGroup> getList(long id){
        String sql = "select * from item_groups where ingest_id = [+] order by id desc";
        List<ItemGroup> itemItemGroups = (ArrayList) dao.getList(sql, new Object[]{ id }, ItemGroup.class);
        return itemItemGroups;
    }

    public Boolean save(ItemGroup itemGroup){
        String sql = "insert into item_groups (name, q_header, pricing_header, image_uri, business_id, design_id, ingest_id) values ('[+]','[+]','[+]','[+]',[+],[+],[+])";
        dao.save(sql, new Object[] {
                itemGroup.getName(),
                itemGroup.getImageUri(),
                itemGroup.getqHeader(),
                itemGroup.getPricingHeader(),
                itemGroup.getBusinessId(),
                itemGroup.getDesignId(),
                itemGroup.getIngestId()
        });
        return true;
    }

    public Boolean update(ItemGroup item){
        String sql = "update item_groups set name = '[+]', design_id = [+] where id = [+]";
        dao.update(sql, new Object[] {
                item.getName(),
                item.getDesignId(),
                item.getId()
        });
        return true;
    }

    public boolean delete(long id){
        String sql = "delete from item_groups where id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteIngest(Long ingestId) {
        String sql = "delete from item_groups where ingest_id = [+]";
        dao.delete(sql, new Object[] { ingestId });
        return true;
    }

    public List<GroupOption> getListOptions(Long id){
        String sql = "select * from group_options where business_id = [+] order by id desc";
        List<GroupOption> groupOptions = (ArrayList) dao.getList(sql, new Object[]{ id }, GroupOption.class);
        return groupOptions;
    }

}
