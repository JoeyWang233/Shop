package com.itheima.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.itheima.dao.AdminDao;
import com.itheima.domain.Category;
import com.itheima.domain.Order;
import com.itheima.domain.Product;
import com.itheima.service.AdminService;
import com.itheima.utils.BeanFactory;

public class AdminServiceImpl implements AdminService{
	
	public List<Category> findAllCategory() {
		AdminDao dao = (AdminDao) BeanFactory.getBean("adminDao");
		try {
			return dao.findAllCategory();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void saveProduct(Product product) throws SQLException {
		AdminDao dao = (AdminDao) BeanFactory.getBean("adminDao");
		dao.saveProduct(product);

	}

	public List<Order> findAllOrders() {
		AdminDao dao = (AdminDao) BeanFactory.getBean("adminDao");
		try {
			return dao.findAllOrders();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Map<String, Object>> findOrderInfoByOid(String oid) {
		AdminDao dao = (AdminDao) BeanFactory.getBean("adminDao");
		try {
			return dao.findOrderInfoByOid(oid);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
