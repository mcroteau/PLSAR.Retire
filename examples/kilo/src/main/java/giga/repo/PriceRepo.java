package giga.repo;

import giga.model.PricingOption;
import giga.model.PricingValue;
import dev.blueocean.Dao;
import net.plsar.annotations.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PriceRepo {

    Dao dao;

    public PriceRepo(Dao dao){
        this.dao = dao;
    }

    public PricingOption getSaved() {
        String idSql = "select max(id) from group_models";
        long id = dao.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getCount() {
        String sql = "select count(*) from group_models";
        Long count = (Long) dao.get(sql, new Object[] { }, Long.class);
        return count;
    }

    public PricingOption get(long id){
        String sql = "select * from group_models where id = [+]";
        PricingOption pricingOption = (PricingOption) dao.get(sql, new Object[] { id }, PricingOption.class);
        return pricingOption;
    }

    public List<PricingOption> getListOptions(long id){
        String sql = "select * from pricing_options where group_id = [+] order by id asc";
        List<PricingOption> pricingOptions = (ArrayList) dao.getList(sql, new Object[]{ id }, PricingOption.class);
        return pricingOptions;
    }

    public List<PricingValue> getListValues(long id){
        String sql = "select * from pricing_values where model_id = [+] order by id asc";
        List<PricingValue> pricingValues = (ArrayList) dao.getList(sql, new Object[]{ id }, PricingValue.class);
        return pricingValues;
    }

    public Boolean saveOption(PricingOption pricingOption){
        String sql = "insert into pricing_options (description, ingest_id, business_id, group_id) values ('[+]',[+],[+],[+])";
        dao.save(sql, new Object[] {
                pricingOption.getDescription(),
                pricingOption.getIngestId(),
                pricingOption.getBusinessId(),
                pricingOption.getGroupId()
        });
        return true;
    }

    public Boolean saveValue(PricingValue pricingValue){
        String sql = "insert into pricing_values (price, ingest_id, model_id, group_id, business_id) values ([+],[+],[+],[+],[+])";
        dao.save(sql, new Object[] {
                pricingValue.getPrice(),
                pricingValue.getIngestId(),
                pricingValue.getModelId(),
                pricingValue.getGroupId(),
                pricingValue.getBusinessId()
        });
        return true;
    }


    public boolean deleteOptions(Long ingestId) {
        String sql = "delete from pricing_options where ingest_id = [+]";
        dao.delete(sql, new Object[] { ingestId });
        return true;
    }

    public boolean deleteValues(Long ingestId) {
        String sql = "delete from pricing_values where ingest_id = [+]";
        dao.delete(sql, new Object[] { ingestId });
        return true;
    }

    public boolean deleteOptionsGroup(Long groupId) {
        String sql = "delete from pricing_options where group_id = [+]";
        dao.delete(sql, new Object[] { groupId });
        return true;
    }

    public boolean deleteValuesGroup(Long groupId) {
        String sql = "delete from pricing_values where group_id = [+]";
        dao.delete(sql, new Object[] { groupId });
        return true;
    }
}
