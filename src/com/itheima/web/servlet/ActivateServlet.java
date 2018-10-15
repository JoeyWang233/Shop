package com.itheima.web.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itheima.service.UserService;
import com.itheima.utils.BeanFactory;

public class ActivateServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获得激活码
		String activateCode = request.getParameter("activateCode");
		
		//激活
		UserService service = (UserService) BeanFactory.getBean("userService");
		try {
			service.activate(activateCode);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//激活成功，跳转到登陆页面
		response.sendRedirect(request.getContextPath() + "/login.jsp");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}