package com.itheima.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;

import com.itheima.domain.User;

import com.itheima.service.UserService;
import com.itheima.utils.BeanFactory;
import com.itheima.utils.CommonUtils;
import com.itheima.utils.MailUtils;

public class RegisterServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 上来先解决中文编码问题
		request.setCharacterEncoding("UTF-8");

		// 获得表单的数据
		Map<String, String[]> properties = request.getParameterMap();

		// 将表单数据封装到实体（表单中仅含一部分数据）
		User user = new User();

		try {
			BeanUtils.populate(user, properties);
		} catch (IllegalAccessException | InvocationTargetException e1) {
			e1.printStackTrace();
		}

		// 将表单中不存在的数据继续封装
		// uid
		user.setUid(CommonUtils.getUUID());
		// telephone
		user.setTelephone("12345");
		// state
		user.setState(0);
		// code
		String activateCode = CommonUtils.getUUID();
		user.setCode(activateCode);

		boolean isRegisterSuccess = false;
		// 将user传递给service层
		UserService service = (UserService) BeanFactory.getBean("userService");
		try {
			isRegisterSuccess = service.register(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// 根据是否注册成功进行判断
		if (isRegisterSuccess) {
			// 注册成功
			// 发送激活邮件
			String emailMsg = "恭喜您注册成功，请点击下面的链接激活账户<a href='http://localhost:8080/HeimaShop/activate?activateCode="
					+ activateCode + "'>点击激活</a>";
			try {
				MailUtils.sendMail(user.getEmail(), emailMsg);
			} catch (MessagingException e) {
				e.printStackTrace();
			}

			// 跳转到"registerSuccess.jsp"页面，提示用户去激活
			response.sendRedirect(request.getContextPath() + "/registerSuccess.jsp");
		} else {
			// 注册失败，跳转到"注册失败"页面
			response.sendRedirect(request.getContextPath() + "/registerFail.jsp");
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}