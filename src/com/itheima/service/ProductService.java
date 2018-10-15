package com.itheima.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.itheima.dao.ProductDao;
import com.itheima.domain.Category;
import com.itheima.domain.Order;
import com.itheima.domain.OrderItem;
import com.itheima.domain.PageBean;
import com.itheima.domain.Product;
import com.itheima.utils.DataSourceUtils;

public interface ProductService {

	// 获得热门商品
	public List<Product> findHotProductList();

	// 获得最新商品
	public List<Product> findNewProductList();

	// 获取商品的种类列表
	public List<Category> findAllCategory();
	
	public PageBean findProductListByCid(String cid, int currentPage, int currentCount);

	public Product findProductByPid(String pid);

	//提交订单，将订单数据和订单项的数据存到数据库中
	public void submitOrder(Order order);

	//更新订单信息（地址、收货人、电话等）
	public void updateOrderInfo(Order order);

	//更新订单的支付状态（此时已付款成功，将state由  0-->1  ）
	public void updateOrderState(String r6_Order);
	// 获得指定用户的订单集合
	public List<Order> findAllOrders(String uid);

	public List<Map<String, Object>> findAllOrderItemByOid(String oid);

}
