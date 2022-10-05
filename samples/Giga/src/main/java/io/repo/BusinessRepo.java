package io.repo;

import io.model.*;
import qio.Qio;
import qio.annotate.DataStore;
import qio.annotate.Inject;

import java.util.ArrayList;
import java.util.List;

@DataStore
public class BusinessRepo {

    @Inject
    Qio qio;

    public Business getSaved() {
        String idSql = "select max(id) from businesses";
        long id = qio.getLong(idSql, new Object[]{});
        return get(id);
    }


    public BusinessRequest getSavedRequest() {
        String idSql = "select max(id) from business_requests";
        long id = qio.getLong(idSql, new Object[]{});
        return getRequest(id);
    }

    public long getCount() {
        String sql = "select count(*) from businesses";
        Long count = (Long) qio.getLong(sql, new Object[] { });
        return count;
    }

    public Business get(long id){
        String sql = "select * from businesses where id = [+]";
        Business business = (Business) qio.get(sql, new Object[] { id }, Business.class);
        return business;
    }

    public Business get(String uri){
        String sql = "select * from businesses where lower(uri) = '[+]'";
        Business business = (Business) qio.get(sql, new Object[] { uri }, Business.class);
        return business;
    }

    public List<Business> getList(){
        String sql = "select * from businesses order by id desc";
        List<Business> businesses = (ArrayList) qio.getList(sql, new Object[]{}, Business.class);
        return businesses;
    }

    public List<Business> getListPrimary(){
        String sql = "select * from businesses where affiliate = false order by id desc";
        List<Business> businesses = (ArrayList) qio.getList(sql, new Object[]{}, Business.class);
        return businesses;
    }

    public List<Business> getListAffiliate(Long id){
        String sql = "select * from businesses where primary_id = [+] and affiliate = true order by id desc";
        List<Business> businesses = (ArrayList) qio.getList(sql, new Object[]{ id }, Business.class);
        return businesses;
    }

    public List<Business> getList(Long id){
        String sql = "select * from businesses where user_id = [+] order by id desc";
        List<Business> businesses = (ArrayList) qio.getList(sql, new Object[]{ id }, Business.class);
        return businesses;
    }

    public List<UserBusiness> getListUsers(Long id){
        String sql = "select * from business_users where user_id = [+] order by id desc";
        List<UserBusiness> businesses = (ArrayList) qio.getList(sql, new Object[]{id}, UserBusiness.class);
        return businesses;
    }

    public Boolean save(Business business){
        String sql = "insert into businesses (name, uri, user_id, initial) values ('[+]','[+]',[+], true)";
        qio.save(sql, new Object[] {
                business.getName(),
                business.getUri(),
                business.getUserId()
        });
        return true;
    }

    public Boolean update(Business business){
        String sql = "update businesses set user_id = [+], name = '[+]', phone = '[+]', email = '[+]', " +
                "stripe_id = '[+]', primary_id = [+], flat_shipping = [+], live = [+], " +
                "uri = '[+]', street = '[+]', street_dos = '[+]', city = '[+]', state = '[+]', " +
                "zip = '[+]', country = '[+]', shipping = [+], initial = [+], affiliate = [+], " +
                "base_commission = [+], activation_complete = [+], owner = '[+]' where id = [+]";
        qio.update(sql, new Object[] {
                business.getUserId(),
                business.getName(),
                business.getPhone(),
                business.getEmail(),
                business.getStripeId(),
                business.getPrimaryId(),
                business.getFlatShipping(),
                business.getLive(),
                business.getUri(),
                business.getStreet(),
                business.getStreetDos(),
                business.getCity(),
                business.getState(),
                business.getZip(),
                business.getCountry(),
                business.getShipping(),
                business.getInitial(),
                business.getAffiliate(),
                business.getBaseCommission(),
                business.getActivationComplete(),
                business.getOwner(),
                business.getId()
        });
        return true;
    }

    public boolean delete(long id){
        System.out.println("delete " + id);
        String sql = "delete from businesses where id = [+]";
        qio.delete(sql, new Object[] { id });
        return true;
    }

    public List<User> getUsers(Long businessId) {
        String sql = "select * from users where business_id = [+] order by id desc";
        List<User> users = (ArrayList) qio.getList(sql, new Object[]{ businessId }, User.class);
        return users;
    }

    public boolean saveUser(UserBusiness userBusiness) {
        String sql = "insert into business_users (user_id, business_id) values ([+], [+])";
        qio.save(sql, new Object[] {
                userBusiness.getUserId(),
                userBusiness.getBusinessId()
        });
        return true;
    }

    public boolean saveRequest(BusinessRequest businessRequest) {
        String sql = "insert into business_requests (business_id, business_name, guid, name, phone, email, notes, approved, denied, pending) values ([+],'[+]','[+]','[+]','[+]','[+]','[+]',false,false,true)";
        qio.save(sql, new Object[] {
                businessRequest.getBusinessId(),
                businessRequest.getBusinessName(),
                businessRequest.getGuid(),
                businessRequest.getName(),
                businessRequest.getPhone(),
                businessRequest.getEmail(),
                businessRequest.getNotes()
        });
        return true;
    }

    public BusinessRequest getRequest(Long id){
        String sql = "select * from business_requests where id = '[+]'";
        BusinessRequest businessRequest = (BusinessRequest) qio.get(sql, new Object[] { id }, BusinessRequest.class);
        return businessRequest;
    }

    public BusinessRequest getRequest(String guid){
        String sql = "select * from business_requests where lower(guid) = '[+]'";
        BusinessRequest businessRequest = (BusinessRequest) qio.get(sql, new Object[] { guid }, BusinessRequest.class);
        return businessRequest;
    }

    public boolean updateRequest(BusinessRequest businessRequest) {
        /**
         * Testing
         */
        String sql = "update business_requests set denied = [+], approved = [+], pending = [+] where id = [+]";
        qio.update(sql, new Object[] {
                businessRequest.getDenied(),
                businessRequest.getApproved(),
                businessRequest.getPending(),
                businessRequest.getId()
        });
        return true;
    }

    public List<BusinessRequest> getRequests(Long id) {
        String sql = "select * from business_requests where business_id = [+] order by id desc";
        List<BusinessRequest> businessRequests = (ArrayList) qio.getList(sql, new Object[]{ id }, BusinessRequest.class);
        return businessRequests;
    }

}
