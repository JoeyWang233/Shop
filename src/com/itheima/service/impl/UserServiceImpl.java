package com.itheima.service.impl;

import java.sql.SQLException;

import com.itheima.dao.UserDao;
import com.itheima.domain.User;
import com.itheima.service.UserService;
import com.itheima.utils.BeanFactory;

public class UserServiceImpl implements UserService{

	public boolean register(User user) throws SQLException {

		UserDao dao = (UserDao) BeanFactory.getBean("userDao");
		Integer row = dao.register(user);
//		if (row.equals(0))
//			return false;
//		else
//			return true;
		
		return row.equals(0)? false:true;
	}

	public void activate(String activateCode) throws SQLException {
		UserDao dao = (UserDao) BeanFactory.getBean("userDao");
		dao.activate(activateCode);
	}

	//校验用户名是否存在
	public boolean checkUsername(String username) {
		UserDao dao = (UserDao) BeanFactory.getBean("userDao");
		Long isExist = 0L;
		try {
			isExist = dao.checkUsername(username);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return isExist>0? true:false;
	}

	public User login(String username, String password) throws SQLException {
		UserDao dao = (UserDao) BeanFactory.getBean("userDao");
		return dao.login(username, password);
	}
}
