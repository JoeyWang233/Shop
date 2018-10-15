package com.itheima.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.itheima.domain.Category;
import com.itheima.domain.Order;
import com.itheima.domain.Product;
import com.itheima.utils.DataSourceUtils;

public interface AdminDao {

	public List<Category> findAllCategory() throws SQLException;

	public void saveProduct(Product product) throws SQLException;

	public List<Order> findAllOrders() throws SQLException;

	public List<Map<String, Object>> findOrderInfoByOid(String oid) throws SQLException;

}
