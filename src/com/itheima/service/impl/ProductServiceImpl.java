package com.itheima.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.itheima.dao.ProductDao;
import com.itheima.domain.Category;
import com.itheima.domain.Order;
import com.itheima.domain.PageBean;
import com.itheima.domain.Product;
import com.itheima.service.ProductService;
import com.itheima.utils.BeanFactory;
import com.itheima.utils.DataSourceUtils;

public class ProductServiceImpl implements ProductService{

	// 获得热门商品
		public List<Product> findHotProductList() {

			ProductDao dao = (ProductDao) BeanFactory.getBean("productDao");
			List<Product> hotProductList = null;
			try {
				hotProductList = dao.findHotProductList();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return hotProductList;
		}

		// 获得最新商品
		public List<Product> findNewProductList() {

			ProductDao dao = (ProductDao) BeanFactory.getBean("productDao");
			List<Product> newProductList = null;

			try {
				newProductList = dao.findNewProductList();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return newProductList;
		}

		// 获取商品的种类列表
		public List<Category> findAllCategory() {
			ProductDao dao = (ProductDao) BeanFactory.getBean("productDao");

			List<Category> categoryList = null;
			try {
				categoryList = dao.findAllCategory();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return categoryList;
		}

		public PageBean findProductListByCid(String cid, int currentPage, int currentCount) {
			// 这里的工作：封装一个PageBean 返回web层

			ProductDao dao = (ProductDao) BeanFactory.getBean("productDao");

			PageBean<Product> pageBean = new PageBean<>();

			// 1.封装当前页
			pageBean.setCurrentPage(currentPage);

			// 2.封装每页显示的条数
			pageBean.setCurrentCount(currentCount);

			// 3.封装总条数
			int totalCount = 0;
			try {
				totalCount = dao.getCount(cid);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			pageBean.setTotalCount(totalCount);

			// 4.封装总页数
			int totalPage = (int) Math.ceil(1.0 * totalCount / currentCount);
			pageBean.setTotalPage(totalPage);

			// 5.当前页显示的产品数据
			// 当前页与起始索引间的关系
			int index = (currentPage - 1) * currentCount;
			List<Product> list = null;
			try {
				list = dao.findProductByPage(cid, index, currentCount);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			pageBean.setList(list);
			// System.out.println("--------");
			// list.toString();
			// System.out.println(pageBean.toString());

			return pageBean;
		}

		public Product findProductByPid(String pid) {
			
			ProductDao dao = (ProductDao) BeanFactory.getBean("productDao");
			Product product = null;
			try {
				product = dao.findProductByPid(pid);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return product;
		}

		//提交订单，将订单数据和订单项的数据存到数据库中
		public void submitOrder(Order order) {
			ProductDao dao = (ProductDao) BeanFactory.getBean("productDao");
			
			try {
				//1.开启事务
				DataSourceUtils.startTransaction();
				//2.存储业务
				dao.addOrders(order);
				dao.addOrderItem(order.getOrderItems());
				
			} catch (SQLException e) {
				//3.回滚
				try {
					DataSourceUtils.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}finally {
				//4.提交事务
				try {
					DataSourceUtils.commitAndRelease();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}

		//更新订单信息（地址、收货人、电话等）
		public void updateOrderInfo(Order order) {
			ProductDao dao = (ProductDao) BeanFactory.getBean("productDao");
			try {
				dao.updateOrderInfo(order);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		//更新订单的支付状态（此时已付款成功，将state由  0-->1  ）
		public void updateOrderState(String r6_Order) {
			ProductDao dao = (ProductDao) BeanFactory.getBean("productDao");
			try {
				dao.updateOrderState(r6_Order);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// 获得指定用户的订单集合
		public List<Order> findAllOrders(String uid) {
			ProductDao dao = (ProductDao) BeanFactory.getBean("productDao");
			List<Order> orderList = null;
			try {
				orderList = dao.findAllOrders(uid);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return orderList;
		}

		public List<Map<String, Object>> findAllOrderItemByOid(String oid) {
			
			ProductDao dao = (ProductDao) BeanFactory.getBean("productDao");
			List<Map<String, Object>> mapList = null;
			try {
				mapList = dao.findAllOrderItemByOid(oid);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return mapList;
			
		}
}
