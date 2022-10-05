package io.repo;

import io.model.Status;
import qio.Qio;
import qio.annotate.DataStore;
import qio.annotate.Inject;

import java.util.ArrayList;
import java.util.List;

@DataStore
public class StatusRepo {

    @Inject
    Qio qio;

    public long getId() {
        String sql = "select max(id) from statuses";
        long id = qio.getLong(sql, new Object[]{});
        return id;
    }

    public long getCount() {
        String sql = "select count(*) from statuses";
        Long count = qio.getLong(sql, new Object[] { });
        return count;
    }

    public Status get(long id){
        String sql = "select * from statuses where id = [+]";
        Status status = (Status) qio.get(sql, new Object[]{ id }, Status.class);
        return status;
    }

    public List<Status> getList(){
        String sql = "select * from statuses";
        List<Status> statuses = (ArrayList) qio.getList(sql, new Object[]{}, Status.class);
        return statuses;
    }

    public Status save(Status status){
        String sql = "insert into statuses (name) values ('[+]')";
        qio.update(sql, new Object[] {
                status.getName()
        });

        Long id = getId();
        Status savedStatus = get(id);
        return savedStatus;
    }

    public boolean update(Status status) {
        String sql = "update statuses set name = '[+]' where id = [+]";
        qio.update(sql, new Object[] {
                status.getName(),
                status.getId()
        });
        return true;
    }

    public boolean delete(long id){
        String sql = "delete from statuses where id = [+]";
        qio.delete(sql, new Object[] { id });
        return true;
    }

}
