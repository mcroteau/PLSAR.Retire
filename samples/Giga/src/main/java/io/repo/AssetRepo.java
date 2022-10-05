package io.repo;

import io.model.Asset;
import qio.Qio;
import qio.annotate.DataStore;
import qio.annotate.Inject;

import java.util.ArrayList;
import java.util.List;

@DataStore
public class AssetRepo {

    @Inject
    Qio qio;

    public Asset getSaved() {
        String idSql = "select max(id) from assets";
        long id = qio.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getCount() {
        String sql = "select count(*) from assets";
        Long count = (Long) qio.get(sql, new Object[] { }, Long.class);
        return count;
    }

    public Asset get(long id){
        String sql = "select * from assets where id = [+]";
        Asset asset = (Asset) qio.get(sql, new Object[] { id }, Asset.class);
        return asset;
    }

    public Asset get(String meta){
        String sql = "select * from assets where lower(meta) = '[+]'";
        Asset asset = (Asset) qio.get(sql, new Object[] { meta }, Asset.class);
        return asset;
    }

    public List<Asset> getList(){
        String sql = "select * from assets order by id desc";
        List<Asset> assets = (ArrayList) qio.getList(sql, new Object[]{}, Asset.class);
        return assets;
    }

    public List<Asset> getList(long id){
        String sql = "select * from assets where business_id = [+] order by id desc";
        List<Asset> assets = (ArrayList) qio.getList(sql, new Object[]{ id }, Asset.class);
        return assets;
    }

    public Boolean save(Asset asset){
        String sql = "insert into assets (name, meta, uri, user_id, business_id, type, date_added) values ('[+]','[+]','[+]',[+],[+],'[+]',[+])";
        qio.save(sql, new Object[] {
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
        qio.delete(sql, new Object[] { id });
        return true;
    }

}
