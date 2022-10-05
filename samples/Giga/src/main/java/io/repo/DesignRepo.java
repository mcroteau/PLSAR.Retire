package io.repo;

import io.model.Design;
import qio.Qio;
import qio.annotate.DataStore;
import qio.annotate.Inject;

import java.util.ArrayList;
import java.util.List;

@DataStore
public class DesignRepo {

    @Inject
    Qio qio;

    public Design getSaved() {
        String idSql = "select max(id) from designs";
        long id = qio.getLong(idSql, new Object[]{});
        return get(id);
    }

    public long getCount() {
        String sql = "select count(*) from designs";
        Long count = (Long) qio.getLong(sql, new Object[] { });
        return count;
    }

    public Design get(long id){
        String sql = "select * from designs where id = [+]";
        Design design = (Design) qio.get(sql, new Object[] { id }, Design.class);
        return design;
    }

    public Design getBase(long id ){
        String sql = "select * from designs where business_id = [+] and base_design = true";
        Design design = (Design) qio.get(sql, new Object[] { id }, Design.class);
        return design;
    }

    public List<Design> getList(){
        String sql = "select * from designs order by id desc";
        List<Design> designs = (ArrayList) qio.getList(sql, new Object[]{}, Design.class);
        return designs;
    }

    public List<Design> getList(long id){
        String sql = "select * from designs where business_id = [+] order by id desc";
        List<Design> designs = (ArrayList) qio.getList(sql, new Object[]{ id }, Design.class);
        return designs;
    }

    public Boolean save(Design design){
        String sql = "insert into designs (business_id, base_design, name, design, css, javascript) values ([+],[+],'[+]','[+]','[+]','[+]')";
        qio.save(sql, new Object[] {
                design.getBusinessId(),
                design.getBaseDesign(),
                design.getName(),
                design.getDesign(),
                design.getCss(),
                design.getJavascript()
        });
        return true;
    }

    public Boolean update(Design design){
        String sql = "update designs set name = '[+]', design = '[+]', css = '[+]', javascript = '[+]' where id = [+]";
        qio.update(sql, new Object[] {
                design.getName(),
                design.getDesign(),
                design.getCss(),
                design.getJavascript(),
                design.getId()
        });
        return true;
    }

    public boolean delete(long id){
        String sql = "delete from designs where id = [+]";
        qio.delete(sql, new Object[] { id });
        return true;
    }

    public boolean deleteDesigns(long id){
        String sql = "delete from designs where business_id = [+]";
        qio.delete(sql, new Object[] { id });
        return true;
    }

}
