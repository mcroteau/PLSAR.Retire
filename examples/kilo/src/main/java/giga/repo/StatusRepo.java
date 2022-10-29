package giga.repo;

import giga.model.Status;
import dev.blueocean.Dao;
import net.plsar.annotations.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StatusRepo {

    Dao dao;

    public StatusRepo(Dao dao){
        this.dao = dao;
    }

    public long getId() {
        String sql = "select max(id) from statuses";
        long id = dao.getLong(sql, new Object[]{});
        return id;
    }

    public long getCount() {
        String sql = "select count(*) from statuses";
        Long count = dao.getLong(sql, new Object[] { });
        return count;
    }

    public Status get(long id){
        String sql = "select * from statuses where id = [+]";
        Status status = (Status) dao.get(sql, new Object[]{ id }, Status.class);
        return status;
    }

    public List<Status> getList(){
        String sql = "select * from statuses";
        List<Status> statuses = (ArrayList) dao.getList(sql, new Object[]{}, Status.class);
        return statuses;
    }

    public Status save(Status status){
        String sql = "insert into statuses (name) values ('[+]')";
        dao.update(sql, new Object[] {
                status.getName()
        });

        Long id = getId();
        Status savedStatus = get(id);
        return savedStatus;
    }

    public boolean update(Status status) {
        String sql = "update statuses set name = '[+]' where id = [+]";
        dao.update(sql, new Object[] {
                status.getName(),
                status.getId()
        });
        return true;
    }

    public boolean delete(long id){
        String sql = "delete from statuses where id = [+]";
        dao.delete(sql, new Object[] { id });
        return true;
    }

}
