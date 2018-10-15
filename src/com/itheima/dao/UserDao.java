package com.itheima.dao;

import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.itheima.domain.User;
import com.itheima.utils.DataSourceUtils;

public interface UserDao {

	public Integer register(User user) throws SQLException;

	public void activate(String activateCode) throws SQLException;
	
	//校验用户名是否存在
	public Long checkUsername(String username) throws SQLException;

	public User login(String username, String password) throws SQLException;

}
