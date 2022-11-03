package io.informant.repo;

import io.informant.model.Reference;
import io.informant.model.Request;
import io.informant.model.User;
import net.plsar.Dao;
import net.plsar.annotations.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ReferenceRepo {

    Dao dao;

    public ReferenceRepo(Dao dao){
        this.dao = dao;
    }

    public void save(Request request) {
        String sql = "insert into requests (user_id, request_user_id) values ([+],[+])";
        dao.save(sql, new Object[]{
            request.getUserId(),
            request.getRequestUserId()
        });
    }

    public Request get(Long userId, Long requestUserId) {
        String sql = "select * from requests where user_id = [+] and request_user_id = [+]";
        Request request = (Request) dao.get(sql, new Object[] { userId, requestUserId }, Request.class);
        return request;
    }

    public void cancel(Request request) {
        String sql = "delete from requests where user_id = [+] and request_user_id = [+]";
        dao.update(sql, new Object[] { request.getUserId(), request.getRequestUserId() });
    }

    public void approve(Request request) {
        String sql = "update user_requests set approved = [+] where id = [+]";
        dao.update(sql, new Object[]{ true, request.getId() });
    }

    public void deny(Request request) {
        String sql = "update user_requests set approved = [+] where id = [+]";
        dao.update(sql, new Object[]{ false, request.getId() });
    }

    public void allow(Reference reference) {
        String sql = "insert into info_access (user_id, access_user_id) values ([+],[+])";
        dao.save(sql, new Object[]{
            reference.getUserId(),
            reference.getAccessUserId()
        });
    }

    public void remove(Reference reference) {
        String sql = "delete from info_access where user_id = [+] and access_user_id = [+]";
        dao.update(sql, new Object[] { reference.getUserId(), reference.getAccessUserId() });
    }

    public List<Request> getRequests(Long id) {
        String sql = "select * from requests where user_id";
        List<Request> references = (ArrayList) dao.getList(sql, new Object[]{ id }, Request.class);
        return references;
    }

    public List<Reference> getReferences(Long id) {
        String sql = "select * from references where user_id";
        List<Reference> references = (ArrayList) dao.getList(sql, new Object[]{ id }, Reference.class);
        return references;
    }
}
