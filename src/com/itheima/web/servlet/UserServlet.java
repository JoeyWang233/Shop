package com.itheima.web.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.itheima.domain.User;
import com.itheima.service.UserService;
import com.itheima.utils.BeanFactory;

public class UserServlet extends BaseServlet {

	// 用户登陆
	public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		UserService service = (UserService) BeanFactory.getBean("userService");
		User user = null;
		try {
			user = service.login(username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 判断用户账号及密码是否正确
		if (user != null) {
			// 登陆成功
			session.setAttribute("user", user);
			response.sendRedirect(request.getContextPath() + "/index.jsp");
		} else {
			request.setAttribute("loginError", "用户名或密码错误");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}

	}

	// 用户注销
	public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		//从session中将user删除
		session.removeAttribute("user");
		
		//如果有自动登陆的功能，不仅要将session中的user删除掉，还要将存储在客户端的cookie（username,password）删除掉。此处就不做了
		
		response.sendRedirect(request.getContextPath() + "/login.jsp");
	}

}