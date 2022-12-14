package giga.repo;

import giga.Giga;
import giga.model.*;
import dev.blueocean.Dao;
import net.plsar.annotations.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserRepo {

	Dao dao;

	public UserRepo(Dao dao){
		this.dao = dao;
	}

	public User getSaved() {
		String idSql = "select max(id) from users";
		long id = dao.getLong(idSql, new Object[]{});
		String sql = "select * from users where id = [+]";
		User user = (User) dao.get(sql, new Object[] { id }, User.class);

		if(user == null) user = new User();
		return user;
	}

	public long getCount() {
		String sql = "select count(*) from users";
		Long count = (Long) dao.getLong(sql, new Object[] { });
	 	return count;
	}

	public User get(long id) {
		String sql = "select * from users where id = [+]";
		User user = (User) dao.get(sql, new Object[] { id }, User.class);
		
		if(user == null) user = new User();
		return user;
	}

	public User get(String email) {
		String sql = "select * from users where email = '[+]'";
		User user = (User) dao.get(sql, new Object[] { email }, User.class);
		return user;
	}

	public User getPhone(String phone) {
		String sql = "select * from users where phone = '[+]'";
		User user = (User) dao.get(sql, new Object[] { phone }, User.class);
		return user;
	}

	public User getEmail(String email) {
		String sql = "select * from users where email = '[+]'";
		User user = (User) dao.get(sql, new Object[] { email }, User.class);
		return user;
	}

	public List<User> findAll() {
		String sql = "select * from users";
		List<User> users = (ArrayList) dao.getList(sql, new Object[]{}, User.class);
		return users;
	}

	public boolean save(User user) {
		String sql = "insert into users (business_id, name, phone, email, password, fresh_password, date_joined) values ([+],'[+]','[+]','[+]','[+]','[+]',[+])";
		dao.save(sql, new Object[]{
				user.getBusinessId(),
				user.getName(),
				user.getPhone(),
				user.getEmail(),
				user.getPassword(),
				user.getFreshPassword(),
				user.getDateJoined()
		});

		return true;
	}

	public User saveAdministrator(User user) {
		String sql = "insert into users (email, password, date_joined) values ('[+]','[+]',[+])";
		dao.save(sql, new Object[]{
			user.getEmail(),
			user.getPassword(),
			user.getDateJoined()
		});

		String savedSql = "select * from users order by id desc limit 1 ";
		User savedUser = (User) dao.get(savedSql, new Object[]{}, User.class);

		checkSaveSuperRole(savedUser.getId());
		checkSaveDefaultUserPermission(savedUser.getId());

		return savedUser;
	}

	public boolean update(User user) {
		String sql = "update users set name = '[+]', phone = '[+]', email = '[+]', password = '[+]', fresh_password = '[+]', " +
				"payment_street = '[+]', payment_street_dos = '[+]', payment_city = '[+]', payment_state = '[+]', payment_zip = '[+]', payment_country = '[+]', " +
				"ship_street = '[+]', ship_street_dos = '[+]', ship_city = '[+]', ship_state = '[+]', ship_zip = '[+]', ship_country = '[+]', notes = '[+]', mining = [+] where id = [+]";
		dao.save(sql, new Object[]{
				user.getName(),
				user.getPhone(),
				user.getEmail(),
				user.getPassword(),
				user.getFreshPassword(),
				user.getPaymentStreet(),
				user.getPaymentStreetDos(),
				user.getPaymentCity(),
				user.getPaymentState(),
				user.getPaymentZip(),
				user.getPaymentCountry(),
				user.getShipStreet(),
				user.getShipStreetDos(),
				user.getShipCity(),
				user.getShipState(),
				user.getShipZip(),
				user.getShipCountry(),
				user.getNotes(),
				user.getMining(),
				user.getId()
		});

		return true;
	}

	public boolean updateUuid(User user) {
		String sql = "update users set uuid = '[+]' where id = [+]";
		dao.update(sql, new Object[]{
				user.getUuid(),
				user.getId()
		});
		return true;
	}
	
	public boolean updatePassword(User user) {
		String sql = "update users set password = '[+]' where id = [+]";
		dao.update(sql, new Object[]{
				user.getPassword(),
				user.getId()
		});
		return true;
	}

	public User getReset(String phone, String uuid){
		String sql = "select * from users where phone = '[+]' and uuid = '[+]'";
		User user = (User) dao.get(sql, new Object[] { phone, uuid }, User.class);
		return user;
	}
	
	public boolean delete(long id) {
		String sql = "delete from users where id = [+]";
		dao.update(sql, new Object[] { id });
		return true;
	}

	public String getUserPassword(String username) {
		User user = get(username);
		return user.getPassword();
	}

	public boolean checkSaveSuperRole(long userId){
		String sql = "select * from roles where role = " + Giga.SUPER_ROLE;
		Role role = (Role) dao.get(sql, new Object[]{ }, UserRole.class);
		UserRole existing = getUserRole(userId, role.getId());
		if(existing == null){
			saveUserRole(userId, role.getId());
		}
		return true;
	}

	public UserRole getUserRole(long userId, long roleId){
		String sql = "select * from user_roles where user_id = [+] and role_id = [+]";
		UserRole userRole = null;
		try {
			userRole = (UserRole) dao.get(sql, new Object[]{ userId, roleId}, UserRole.class);
		}catch(Exception e){
		}
		return null;
	}

	public boolean checkSaveDefaultUserPermission(long userId){
		String permission = Giga.USER_MAINTENANCE + userId;
		UserPermission existing = getUserPermission(userId, permission);
		if(existing == null){
			savePermission(userId, permission);
		}
		return true;
	}

	public UserPermission getUserPermission(long userId, String permission){
		String sql = "select * from user_permissions where user_id = [+] and permission = '[+]'";
		UserPermission userPermission = null;
		try {
			userPermission = (UserPermission) dao.get(sql, new Object[]{ userId, permission}, UserPermission.class);
		}catch(Exception ex){ }
		return userPermission;
	}

	public boolean saveUserRole(long userId, long roleId){
		String sql = "insert into user_roles (role_id, user_id) values ([+], [+])";
		dao.save(sql, new Object[]{roleId, userId});
		return true;
	}
	
	public boolean savePermission(long accountId, String permission){
		String sql = "insert into user_permissions (user_id, permission) values ([+], '[+]')";
		dao.save(sql, new Object[]{
				accountId,
				permission.replaceAll("'", "''")} );
		return true;
	}
	
	public boolean deleteUserRoles(long userId){
		String sql = "delete from user_roles where user_id = [+]";
		dao.delete(sql, new Object[]{ userId });
		return true;
	}
	
	public boolean deleteUserPermissions(long userId){
		String sql = "delete from user_permissions where user_id = [+]";
		dao.delete(sql, new Object[] { userId });
		return true;
	}

	public Set<String> getUserRoles(long id) {
		String sql = "select r.name as name from user_roles ur inner join roles r on r.id = ur.role_id where ur.user_id = [+]";
		List<UserRole> rolesList = (ArrayList) dao.getList(sql, new Object[]{ id }, UserRole.class);
		Set<String> roles = new HashSet<>();
		for(UserRole role: rolesList){
			roles.add(role.getName());
		}
		return roles;
	}

	public Set<String> getUserPermissions(long id) {
		String sql = "select permission from user_permissions where user_id = [+]";
		List<UserPermission> permissionsList = (ArrayList) dao.getList(sql, new Object[]{ id }, UserPermission.class);
		Set<String> permissions = new HashSet<>();
		for(UserPermission permission: permissionsList){
			permissions.add(permission.getPermission());
		}
		return permissions;
	}

}