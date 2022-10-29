package giga.repo;

import giga.model.Asset;
import dev.blueocean.Dao;
import net.plsar.annotations.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AssetRepo {

    Dao dao;

    public AssetRepo(Dao dao){
        this.dao = dao;
    }

    public Asset getSaved() {
        String idSql = "select max(id) from assets";
        long id = dao.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getCount() {
        String sql = "select count(*) from assets";
        Long count = (Long) dao.get(sql, new Object[] { }, Long.class);
        return count;
    }

    public Asset get(long id){
        String sql = "select * from assets where id = [+]";
        Asset asset = (Asset) dao.get(sql, new Object[] { id }, Asset.class);
        return asset;
    }

    public Asset get(String meta){
        String sql = "select * from assets where lower(meta) = '[+]'";
        Asset asset = (Asset) dao.get(sql, new Object[] { meta }, Asset.class);
        return asset;
    }

    public List<Asset> getList(){
        String sql = "select * from assets order by id desc";
        List<Asset> assets = (ArrayList) dao.getList(sql, new Object[]{}, Asset.class);
        return assets;
    }

    public List<Asset> getList(long id){
        String sql = "select * from assets where business_id = [+] order by id desc";
        List<Asset> assets = (ArrayList) dao.getList(sql, new Object[]{ id }, Asset.class);
        return assets;
    }

    public Boolean save(Asset asset){
        String sql = "insert into assets (name, meta, uri, user_id, business_id, type, date_added) values ('[+]','[+]','[+]',[+],[+],'[+]',[+])";
        dao.save(sql, new Object[] {
                asset.getName(),
                asset.getMeta(),
                asset.getUri(),
                asset.getUserId(),
                asset.getBusinessId(),
                asset.getType(),
                asset.getDateAdded()
        });
        return true;
    }

    public boolean delete(long id){
        String sql = "delete from assets where id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

}
