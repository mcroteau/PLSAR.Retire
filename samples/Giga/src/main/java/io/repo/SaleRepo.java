package io.repo;

import io.model.Item;
import io.model.Sale;
import qio.Qio;
import qio.annotate.DataStore;
import qio.annotate.Inject;

import java.util.ArrayList;
import java.util.List;

@DataStore
public class SaleRepo {

    @Inject
    Qio qio;

    public Sale getSaved() {
        String idSql = "select max(id) from sales";
        long id = qio.getLong(idSql, new Object[]{});
        return get(id);
    }

    public Sale get(long id){
        String sql = "select * from sales where id = [+]";
        Sale sale = (Sale) qio.get(sql, new Object[] { id }, Sale.class);
        return sale;
    }

    public Sale getPrimary(long id, long businessId){
        String sql = "select * from sales where id = [+] and primary_id = [+]";
        Sale sale = (Sale) qio.get(sql, new Object[] { id, businessId }, Sale.class);
        return sale;
    }
    public Sale getAffiliate(long id, long businessId){
        String sql = "select * from sales where id = [+] and affiliate_id = [+]";
        Sale sale = (Sale) qio.get(sql, new Object[] { id, businessId }, Sale.class);
        return sale;
    }

    public List<Sale> getList(){
        String sql = "select * from sales order by id desc";
        List<Sale> sales = (ArrayList) qio.getList(sql, new Object[]{}, Sale.class);
        return sales;
    }

    public List<Sale> getListPrimary(Long id){
        String sql = "select * from sales where primary_id = [+] order by id desc";
        List<Sale> sales = (ArrayList) qio.getList(sql, new Object[]{ id }, Sale.class);
        return sales;
    }

    public List<Sale> getListAffiliate(Long id){
        String sql = "select * from sales where affiliate_id = [+] order by id desc";
        List<Sale> sales = (ArrayList) qio.getList(sql, new Object[]{ id }, Sale.class);
        return sales;
    }

    public boolean save(Sale sale) {
        String sql = "insert into sales (amount, user_id, cart_id, sales_date) values ([+],[+],[+],[+])";
        qio.save(sql, new Object[] {
                sale.getAmount(),
                sale.getUserId(),
                sale.getCartId(),
                sale.getSalesDate()
        });
        return true;
    }


    public boolean updatePrimary(Sale sale) {
        String sql = "update sales set primary_id = [+], primary_amount = [+],  " +
                "stripe_primary_customer_id = '[+]', stripe_primary_charge_id = '[+]' where id = [+]";
        qio.update(sql, new Object[] {
                sale.getPrimaryId(),
                sale.getPrimaryAmount(),
                sale.getStripePrimaryCustomerId(),
                sale.getStripePrimaryChargeId(),
                sale.getId()
        });
        return true;
    }

    public boolean updateAffiliate(Sale sale) {
        String sql = "update sales set affiliate_id = [+], primary_id = [+], affiliate_amount = [+], primary_amount = [+],  " +
                "stripe_application_customer_id = '[+]', stripe_primary_customer_id = '[+]', " +
                "stripe_application_charge_id = '[+]', stripe_primary_charge_id = '[+]' where id = [+]";
        qio.update(sql, new Object[] {
                sale.getAffiliateId(),
                sale.getPrimaryId(),
                sale.getAffiliateAmount(),
                sale.getPrimaryAmount(),
                sale.getStripeApplicationCustomerId(),
                sale.getStripePrimaryCustomerId(),
                sale.getStripeApplicationChargeId(),
                sale.getStripePrimaryChargeId(),
                sale.getId()
        });
        return true;
    }

    public List<Sale> getUserSales(Long id){
        String sql = "select * from sales where user_id = [+] order by id desc";
        List<Sale> sales = (ArrayList) qio.getList(sql, new Object[]{ id }, Sale.class);
        return sales;
    }

    public boolean delete(Long id) {
        String sql = "delete from sales where cart_id = [+]";
        qio.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteSales(Long id) {
        String sql = "delete from sales where business_id = [+]";
        qio.delete(sql, new Object[] { id });
        return true;
    }
}
