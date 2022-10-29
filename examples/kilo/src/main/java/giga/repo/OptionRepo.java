package giga.repo;

import giga.model.*;
import giga.model.GroupOption;
import dev.blueocean.Dao;
import net.plsar.annotations.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OptionRepo {

    Dao dao;

    public OptionRepo(Dao dao){
        this.dao = dao;
    }

    public GroupOption getSaved() {
        String idSql = "select max(id) from group_options";
        long id = dao.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getCount() {
        String sql = "select count(*) from group_options";
        Long count = (Long) dao.getLong(sql, new Object[] { });
        return count;
    }

    public GroupOption get(long id){
        String sql = "select * from group_options where id = [+]";
        GroupOption itemGroup = (GroupOption) dao.get(sql, new Object[] { id }, GroupOption.class);
        return itemGroup;
    }

    public GroupOption get(String name){
        String sql = "select * from group_options where name = '[+]'";
        GroupOption itemGroup = (GroupOption) dao.get(sql, new Object[] { name }, GroupOption.class);
        return itemGroup;
    }

    public GroupOption get(long id, long businessId){
        String sql = "select * from group_options where id = [+] and business_id = [+]";
        GroupOption itemGroup = (GroupOption) dao.get(sql, new Object[] { id, businessId }, GroupOption.class);
        return itemGroup;
    }

    public List<GroupOption> getList(){
        String sql = "select * from group_options order by id desc";
        List<GroupOption> itemGroupOptions = (ArrayList) dao.getList(sql, new Object[]{}, GroupOption.class);
        return itemGroupOptions;
    }

    public List<GroupOption> getListOptions(long id){
        String sql = "select * from group_options where group_id = [+] order by id asc";
        List<GroupOption> itemGroupOptions = (ArrayList) dao.getList(sql, new Object[]{ id }, GroupOption.class);
        return itemGroupOptions;
    }

    public List<GroupOptionValue> getListValues(long id){
        String sql = "select * from group_option_values where model_id = [+] order by id asc";
        List<GroupOptionValue> groupOptionValues = (ArrayList) dao.getList(sql, new Object[]{ id }, GroupOptionValue.class);
        return groupOptionValues;
    }

    public Boolean saveOption(GroupOption groupOption){
        String sql = "insert into group_options (group_id, ingest_id, business_id, title) values ([+],[+],[+],'[+]')";
        dao.save(sql, new Object[] {
                groupOption.getGroupId(),
                groupOption.getIngestId(),
                groupOption.getBusinessId(),
                groupOption.getTitle()
        });
        return true;
    }

    public Boolean saveValue(GroupOptionValue groupValue){
        String sql = "insert into group_option_values (ingest_id, model_id, business_id, group_id, option_value) values ([+],[+],[+],[+],'[+]')";
        dao.save(sql, new Object[] {
                groupValue.getIngestId(),
                groupValue.getModelId(),
                groupValue.getBusinessId(),
                groupValue.getGroupId(),
                groupValue.getOptionValue()
        });
        return true;
    }

    public boolean updateValue(GroupModel storedModel) {
        String sql = "update group_option_values set quantity = [+] where id = [+]";
        dao.update(sql, new Object[] {
                storedModel.getWeight(),
                storedModel.getQuantity(),
                storedModel.getId()
        });
        return true;
    }

    public boolean delete(long id){
        String sql = "delete from group_options where id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteOptions(Long ingestId) {
        String sql = "delete from group_options where ingest_id = [+]";
        dao.delete(sql, new Object[] { ingestId });
        return true;
    }

    public boolean deleteValuesModel(Long modelId) {
        String sql = "delete from group_option_values where model_id = [+]";
        dao.delete(sql, new Object[] { modelId });
        return true;
    }

    public boolean deleteValues(Long ingestId) {
        String sql = "delete from group_option_values where ingest_id = [+]";
        dao.delete(sql, new Object[] { ingestId });
        return true;
    }

    public boolean deleteOptionsGroup(Long groupId) {
        String sql = "delete from group_options where group_id = [+]";
        dao.delete(sql, new Object[] { groupId });
        return true;
    }

    public boolean deleteValuesGroup(Long groupId) {
        String sql = "delete from group_option_values where group_id = [+]";
        dao.delete(sql, new Object[] { groupId });
        return true;
    }
}
