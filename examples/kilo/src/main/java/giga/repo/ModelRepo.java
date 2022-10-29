package giga.repo;

import giga.model.GroupModel;
import dev.blueocean.Dao;
import net.plsar.annotations.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ModelRepo {

    Dao dao;

    public ModelRepo(Dao dao){
        this.dao = dao;
    }

    public GroupModel getSaved() {
        String idSql = "select max(id) from group_models";
        long id = dao.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getCount() {
        String sql = "select count(*) from group_models";
        Long count = (Long) dao.get(sql, new Object[] { }, Long.class);
        return count;
    }

    public GroupModel get(long id){
        String sql = "select * from group_models where id = [+]";
        GroupModel groupModel = (GroupModel) dao.get(sql, new Object[] { id }, GroupModel.class);
        return groupModel;
    }

    public GroupModel get(String modelNumber){
        String sql = "select * from group_models where model_number = '[+]'";
        GroupModel groupModel = (GroupModel) dao.get(sql, new Object[] { modelNumber }, GroupModel.class);
        return groupModel;
    }

    public List<GroupModel> getList(long id){
        String sql = "select * from group_models where group_id = [+] order by id asc";
        List<GroupModel> groupModels = (ArrayList) dao.getList(sql, new Object[]{ id }, GroupModel.class);
        return groupModels;
    }

    public Boolean save(GroupModel groupModel){
        String sql = "insert into group_models (model_number, ingest_id, group_id, business_id, weight, quantity) values ('[+]',[+],[+],[+],[+],[+])";
        dao.save(sql, new Object[] {
                groupModel.getModelNumber(),
                groupModel.getIngestId(),
                groupModel.getGroupId(),
                groupModel.getBusinessId(),
                groupModel.getWeight(),
                groupModel.getQuantity(),
        });
        return true;
    }

    public boolean update(GroupModel storedModel) {
        String sql = "update group_models set quantity = [+] where id = [+]";
        dao.update(sql, new Object[] {
                storedModel.getWeight(),
                storedModel.getQuantity(),
                storedModel.getId()
        });
        return true;
    }

    public boolean delete(long groupId){
        String sql = "delete from group_models where id = [+]";
        dao.delete(sql, new Object[] { groupId });
        return true;
    }

    public boolean delete(String modelNumber){
        String sql = "delete from group_models where model_number = '[+]'";
        dao.delete(sql, new Object[] { modelNumber });
        return true;
    }

    public boolean deleteIngest(Long ingestId) {
        String sql = "delete from group_models where ingest_id = [+]";
        dao.delete(sql, new Object[] { ingestId });
        return true;
    }

    public boolean deleteGroup(Long groupId) {
        String sql = "delete from group_models where group_id = [+]";
        dao.delete(sql, new Object[] { groupId });
        return true;
    }
}
