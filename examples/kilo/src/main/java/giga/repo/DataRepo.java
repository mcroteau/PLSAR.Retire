package giga.repo;

import giga.model.DataImport;
import giga.model.MediaImport;
import net.plsar.Dao;
import net.plsar.annotations.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DataRepo {

    Dao dao;

    public DataRepo(Dao dao){
        this.dao = dao;
    }

    public DataImport getSaved() {
        String idSql = "select max(id) from data_imports";
        long id = dao.getLong(idSql, new Object[]{});
        return get(id);
    }

    public MediaImport getSavedMedia() {
        String idSql = "select max(id) from media_imports";
        long id = dao.getLong(idSql, new Object[]{});
        return getMedia(id);
    }

    public long getCount() {
        String sql = "select count(*) from data_imports";
        Long count = (Long) dao.getLong(sql, new Object[] { });
        return count;
    }

    public MediaImport getMedia(long id){
        String sql = "select * from media_imports where id = [+]";
        MediaImport mediaImport = (MediaImport) dao.get(sql, new Object[] { id }, MediaImport.class);
        return mediaImport;
    }

    public DataImport get(long id){
        String sql = "select * from data_imports where id = [+]";
        DataImport dataImport = (DataImport) dao.get(sql, new Object[] { id }, DataImport.class);
        return dataImport;
    }

    public List<DataImport> getList(long id, String type){
        String sql = "select * from data_imports where business_id = [+] and type = '[+]' order by id desc";
        List<DataImport> dataImports = (ArrayList) dao.getList(sql, new Object[]{ id, type }, DataImport.class);
        return dataImports;
    }

    public List<MediaImport> getListMedia(long id){
        String sql = "select * from media_imports where import_id = [+] order by id desc";
        List<MediaImport> mediaImports = (ArrayList) dao.getList(sql, new Object[]{ id }, MediaImport.class);
        return mediaImports;
    }

    public Boolean save(DataImport dataImport){
        String sql = "insert into data_imports (user_id, business_id, type, date_import) values ([+],[+],'[+]',[+])";
        dao.save(sql, new Object[] {
                dataImport.getUserId(),
                dataImport.getBusinessId(),
                dataImport.getType(),
                dataImport.getDateImport()
        });
        return true;
    }

    public Boolean saveMedia(MediaImport mediaImport){
        String sql = "insert into media_imports (import_id, name, price, uri, quantity, weight, meta) values ([+],'[+]',[+],'[+]',[+],[+],'[+]')";
        dao.save(sql, new Object[] {
                mediaImport.getImportId(),
                mediaImport.getName(),
                mediaImport.getPrice(),
                mediaImport.getUri(),
                mediaImport.getQuantity(),
                mediaImport.getWeight(),
                mediaImport.getMeta()
        });
        return true;
    }

    public Boolean updateMedia(MediaImport mediaImport){
        String sql = "update media_imports set name = '[+]', price = [+], quantity = [+], weight = [+], category_id = [+] where id = [+]";
        dao.save(sql, new Object[] {
                mediaImport.getName(),
                mediaImport.getPrice(),
                mediaImport.getQuantity(),
                mediaImport.getWeight(),
                mediaImport.getCategoryId(),
                mediaImport.getId()
        });
        return true;
    }

    public boolean delete(long id){
        String sql = "delete from data_imports where id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteMediaImports(long id){
        String sql = "delete from media_imports where import_id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

}
