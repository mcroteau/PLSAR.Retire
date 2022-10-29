package giga.repo;

import giga.model.Role;
import dev.blueocean.Dao;
import net.plsar.annotations.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RoleRepo {

	Dao dao;

	public RoleRepo(Dao dao){
		this.dao = dao;
	}

	public int count() {
		String sql = "select count(*) from roles";
		int count = (Integer) dao.get(sql, new Object[] { }, Integer.class);
	 	return count; 
	}

	public Role get(int id) {
		String sql = "select * from roles where id = [+]";
		Role role = (Role) dao.get(sql, new Object[] { id },Role.class);
		return role;
	}

	public Role get(String name) {
		String sql = "select * from roles where name = '[+]'";
		Role role = (Role) dao.get(sql, new Object[] { name },Role.class);
		return role;
	}

	public Role find(String name) {
		Role role = null; 
		try{
			String sql = "select * from roles where name = '[+]'";
			role = (Role) dao.get(sql, new Object[] { name }, Role.class);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return role;
	}

	public List<Role> findAll() {
		String sql = "select * from roles";
		List<Role> roles = (ArrayList) dao.getList(sql, new Object[]{}, Role.class);
		return roles;
	}
	
	public void save(Role role) {
		String sql = "insert into roles (name) values('[+]')";
		dao.save(sql, new Object[]{
				role.getName()
		});
	}
	
}