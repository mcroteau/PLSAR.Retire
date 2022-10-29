package giga.repo;

import giga.model.Ingest;
import dev.blueocean.Dao;
import net.plsar.annotations.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class IngestRepo {

    Dao dao;

    public IngestRepo(Dao dao){
        this.dao = dao;
    }

    public Ingest getSaved() {
        String idSql = "select max(id) from ingests";
        long id = dao.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getCount() {
        String sql = "select count(*) from ingests";
        Long count = (Long) dao.get(sql, new Object[] { }, Long.class);
        return count;
    }

    public Ingest get(long id){
        String sql = "select * from ingests where id = [+]";
        Ingest groupIngest = (Ingest) dao.get(sql, new Object[] { id }, Ingest.class);
        return groupIngest;
    }

    public Ingest get(String modelNumber){
        String sql = "select * from ingests where model_number = '[+]'";
        Ingest groupIngest = (Ingest) dao.get(sql, new Object[] { modelNumber }, Ingest.class);
        return groupIngest;
    }

    public List<Ingest> getList(long id){
        String sql = "select * from ingests where business_id = [+] order by id desc";
        List<Ingest> groupIngests = (ArrayList) dao.getList(sql, new Object[]{ id }, Ingest.class);
        return groupIngests;
    }

    public Boolean save(Ingest ingest){
        String sql = "insert into ingests (business_id, date_ingest) values ([+],[+])";
        dao.save(sql, new Object[] {
                ingest.getBusinessId(),
                ingest.getDateIngest()
        });
        return true;
    }

    public boolean update(Ingest ingest) {
        String sql = "update ingests set quantity = [+] where id = [+]";
        dao.update(sql, new Object[] {

        });
        return true;
    }

    public boolean delete(long id){
        String sql = "delete from ingests where id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

}
