package io.informant.repo;

import io.informant.model.IntelAccess;
import io.informant.model.Request;
import net.plsar.Dao;
import net.plsar.annotations.Repository;

@Repository
public class RequestRepo {

    Dao dao;

    public RequestRepo(Dao dao){
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

    public void allow(IntelAccess intelAccess) {
        String sql = "insert into intel_access (user_id, access_user_id) values ([+],[+])";
        dao.save(sql, new Object[]{
            intelAccess.getUserId(),
            intelAccess.getAccessUserId()
        });
    }

    public void revoke(IntelAccess intelAccess) {
        String sql = "delete from intelAccess where user_id = [+] and access_user_id = [+]";
        dao.update(sql, new Object[] { intelAccess.getUserId(), intelAccess.getAccessUserId() });
    }

}
