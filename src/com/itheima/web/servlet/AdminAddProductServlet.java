package com.itheima.web.servlet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.itheima.domain.Category;
import com.itheima.domain.Product;
import com.itheima.service.AdminService;
import com.itheima.service.impl.AdminServiceImpl;
import com.itheima.utils.BeanFactory;
import com.itheima.utils.CommonUtils;

public class AdminAddProductServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 目的：收集表单数据，并封装成一个实体Product，存到数据库 将上传图片存到服务器磁盘上
		Product product = new Product();
		
		//收集表单数据的容器
		Map<String, Object> map = new HashMap<>();
		
		try {
			// 1.创建磁盘文件项
			DiskFileItemFactory factory = new DiskFileItemFactory();

			// 2.创建文件上传的核心对象
			ServletFileUpload upload = new ServletFileUpload(factory);

			// 3.解析request获得文件项对象集合
			List<FileItem> parseRequest = upload.parseRequest(request);

			for (FileItem item : parseRequest) {
				// 判断是否是普通表单
				boolean formField = item.isFormField();
				if (formField) {
					// 普通表单项，获得表单输入的数据，封装到实体中
					String fieldName = item.getFieldName();
					String fieldValue = item.getString("UTF-8");
					map.put(fieldName, fieldValue);

				} else {
					// 文件上传项，获得文件名称，获得文件内容
					String fileName = item.getName();
					String path = request.getServletContext().getRealPath("upload");
					InputStream in = item.getInputStream();
					OutputStream out = new FileOutputStream(path + "/" + fileName);
					IOUtils.copy(in, out);
					in.close();
					out.close();
					
					//将图片地址封进去
					map.put("pimage", "upload/" + fileName);
				}
			}
			
			BeanUtils.populate(product, map);
			//封装其余的数据
			//pid、pdate、pflag、Category
			product.setPid(CommonUtils.getUUID());
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String pdate = format.format(new Date());
			product.setPdate(pdate);
			
			product.setPflag(0);
			
			Category category = new Category();
			category.setCid(map.get("cid").toString());
			product.setCategory(category);
			
			//将封装好的product传递给service层
			AdminService service = (AdminService) BeanFactory.getBean("adminService");
			service.saveProduct(product);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}