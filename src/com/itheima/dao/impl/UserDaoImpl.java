package com.itheima.dao.impl;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.itheima.dao.UserDao;
import com.itheima.domain.User;
import com.itheima.utils.DataSourceUtils;

public class UserDaoImpl implements UserDao{

	public Integer register(User user) throws SQLException {

		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "insert into user values(?,?,?,?,?,?,?,?,?,?)";

		String uid = user.getUid();
		String username = user.getUsername();
		String password = user.getPassword();
		String name = user.getName();
		String email = user.getEmail();
		String telephone = user.getTelephone();
		String birthday = user.getBirthday();
		String sex = user.getSex();
		int state = user.getState();
		String code = user.getCode();

		int update = runner.update(sql, uid, username, password, name, email, telephone, birthday, sex, state, code);

		return update;
	}

	public void activate(String activateCode) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "update user set state=1 where code=?";
		runner.update(sql, activateCode);
	}
	
	//校验用户名是否存在
	public Long checkUsername(String username) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select count(*) from user where username=?";
		Long query = (Long) runner.query(sql, new ScalarHandler(), username);
		System.out.println("--");
		System.out.println(query);
		return query;
	}

	public User login(String username, String password) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from user where username=? and password=?";
		return runner.query(sql, new BeanHandler<User>(User.class), username,password);
	}
}
