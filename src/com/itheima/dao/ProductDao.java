package com.itheima.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.itheima.domain.Category;
import com.itheima.domain.Order;
import com.itheima.domain.OrderItem;
import com.itheima.domain.Product;
import com.itheima.utils.DataSourceUtils;

public interface ProductDao {

	public List<Product> findHotProductList() throws SQLException;

	public List<Product> findNewProductList() throws SQLException;

	public List<Category> findAllCategory() throws SQLException;

	// 获得产品总条数
	public int getCount(String cid) throws SQLException;

	public List<Product> findProductByPage(String cid, int index, int currentCount) throws SQLException;

	public Product findProductByPid(String pid) throws SQLException;

	// 向orders表中插入数据
	public void addOrders(Order order) throws SQLException;

	// 向orderitem表中插入数据
	public void addOrderItem(List<OrderItem> orderItems) throws SQLException;

	public void updateOrderInfo(Order order) throws SQLException;

	public void updateOrderState(String r6_Order) throws SQLException;

	public List<Order> findAllOrders(String uid) throws SQLException;

	public List<Map<String, Object>> findAllOrderItemByOid(String oid) throws SQLException;

}
