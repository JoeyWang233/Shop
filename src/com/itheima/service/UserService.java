package com.itheima.service;

import java.sql.SQLException;

import com.itheima.dao.UserDao;
import com.itheima.domain.User;

public interface UserService {

	public boolean register(User user) throws SQLException;

	public void activate(String activateCode) throws SQLException;

	//校验用户名是否存在
	public boolean checkUsername(String username);

	public User login(String username, String password) throws SQLException;

}
