package com.itheima.utils;

import org.dom4j.Document;

import org.dom4j.Element;

import org.dom4j.io.SAXReader;

public class BeanFactory {

	public static Object getBean(String id) {

		// 生产对象
		// 根据清单(配置文件)生产--将每一个bean对象的生产细节配置到文件中
		// 使用dom4j的xml解析技术

		try {
			// 1.创建解析器
			SAXReader reader = new SAXReader();
			// 2.解析文档（通过反射技术来获取文档bean.xml--在src下）
			String path = BeanFactory.class.getClassLoader().getResource("bean.xml").getPath();
			Document doc = reader.read(path);
			// 3.获得元素--参数是xpath规则
			Element element = (Element) doc.selectSingleNode("//bean[@id='" + id + "']");
			String className = element.attributeValue("class");
			// com.itheima.service.impl.AdminServiceImpl
			// 利用反射创建对象
			Class clazz = Class.forName(className);
			Object object = clazz.newInstance();

			return object;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
