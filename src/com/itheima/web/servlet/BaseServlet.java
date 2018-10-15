package com.itheima.web.servlet;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		
		req.setCharacterEncoding("UTF-8");
		
		try {
			//1.获得请求的method的名称
			String methodname = req.getParameter("method");
			//2.获得当前被访问的对象的字节码对象(this不带表的是BaseServlet,而是BaseServlet类的子类的对象)
			Class<? extends BaseServlet> clazz = this.getClass();//ProductServlet.class  或者是  UserServlet.class
			//3.获得当前字节码对象中的指定的方法
			Method method = clazz.getMethod(methodname, HttpServletRequest.class, HttpServletResponse.class);
			//4.执行响应的功能方法
			method.invoke(this, req,resp);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}