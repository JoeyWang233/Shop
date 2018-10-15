package com.itheima.web.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import com.google.gson.Gson;
import com.itheima.domain.Category;
import com.itheima.domain.Order;
import com.itheima.service.AdminService;
import com.itheima.service.impl.AdminServiceImpl;
import com.itheima.utils.BeanFactory;

public class AdminServlet extends BaseServlet {

	public void findAllCategory(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 提供一个List<Category> 转成json字符串
		AdminService service = (AdminService) BeanFactory.getBean("adminService");
		List<Category> categoryList = service.findAllCategory();
		
		Gson gson = new Gson();
		String json = gson.toJson(categoryList);
		
//		response.setCharacterEncoding("text/html;charset=UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(json);
	}
	
	//获得所有的订单
	public void findAllOrders(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		AdminService service = (AdminService) BeanFactory.getBean("adminService");
		List<Order> orderList = service.findAllOrders();
		
		request.setAttribute("orderList", orderList);
		request.getRequestDispatcher("/admin/order/list.jsp").forward(request, response);
	}
	
	
	public void findOrderInfoByOid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//线程睡5s模拟加载过程
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String oid = request.getParameter("oid");
		
		//用解耦和的方式进行编码---解web层与service 层的耦合
		//使用工厂+反射+配置文件
		AdminService service = (AdminService) BeanFactory.getBean("adminService");
		
		
		List<Map<String, Object>> mapList = service.findOrderInfoByOid(oid);
		
		Gson gson = new Gson();
		String json = gson.toJson(mapList);
		
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(json);
	}

}