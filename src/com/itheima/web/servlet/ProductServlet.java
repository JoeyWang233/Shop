package com.itheima.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import com.google.gson.Gson;
import com.itheima.domain.Cart;
import com.itheima.domain.CartItem;
import com.itheima.domain.Category;
import com.itheima.domain.Order;
import com.itheima.domain.OrderItem;
import com.itheima.domain.PageBean;
import com.itheima.domain.Product;
import com.itheima.domain.User;
import com.itheima.service.ProductService;
import com.itheima.utils.BeanFactory;
import com.itheima.utils.CommonUtils;
import com.itheima.utils.JedisPoolUtils;
import com.itheima.utils.PaymentUtil;

import redis.clients.jedis.Jedis;

public class ProductServlet extends BaseServlet {

	/*
	 * public void doGet(HttpServletRequest request, HttpServletResponse response)
	 * throws ServletException, IOException {
	 * 
	 * 
	 * 
	 * //获得请求的method参数值，来判断具体执行哪一个方法 String methodname =
	 * request.getParameter("method"); if("productList".equals(methodname)) {
	 * productList(request,response); }else if ("categoryList".equals(methodname)) {
	 * categoryList(request,response); }else if ("index".equals(methodname)) {
	 * index(request,response); }else if ("productInfo".equals(methodname)) {
	 * productInfo(request,response); } }
	 * 
	 * public void doPost(HttpServletRequest request, HttpServletResponse response)
	 * throws ServletException, IOException { doGet(request, response); }
	 */

	// 模块中的功能是通过方法进行区分的

	// 显示商品的类别的功能
	public void categoryList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ProductService service = (ProductService) BeanFactory.getBean("productService");

		// 先从缓存中查询categoryList 如果有直接使用，如果没有从数据库中查询，并存到缓存中
		// 1.获得jedis对象，连接redis数据库
		Jedis jedis = JedisPoolUtils.getJedis();
		String categoryListJson = jedis.get("categoryListJson");
		// 2.判断categoryListJson是否为空
		if (categoryListJson == null) {
			// 从数据库中查询，并存到缓存中
			System.out.println("缓存没有数据 查询数据库");
			List<Category> categoryList = service.findAllCategory();
			Gson gson = new Gson();
			categoryListJson = gson.toJson(categoryList);
			jedis.set("categoryListJson", categoryListJson);
		}

		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(categoryListJson);
	}

	// 显示首页的功能
	public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductService service = (ProductService) BeanFactory.getBean("productService");

		// 准备热门商品--List<Product>
		List<Product> hotProductList = service.findHotProductList();

		// 准备最新商品--List<Product>
		List<Product> newProductList = service.findNewProductList();

		// 准备商品类别数据
		List<Category> categoryList = service.findAllCategory();

		request.setAttribute("hotProductList", hotProductList);
		request.setAttribute("newProductList", newProductList);
		request.setAttribute("categoryList", categoryList);

		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	// 显示商品的详细信息的功能
	public void productInfo(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pid = request.getParameter("pid");

		ProductService service = (ProductService) BeanFactory.getBean("productService");
		Product product = service.findProductByPid(pid);

		request.setAttribute("product", product);

		// 获得客户端携带的cookie---获得名字是pids的cookie
		String pids = pid;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("pids".equals(cookie.getName())) {
					// 找到名为"pids"的cookie
					pids = cookie.getValue();
					// 1-3-2本次商品访问的是8---->8-1-3-2
					// 1-3-2本次商品访问的是3---->3-1-2
					// 1-3-2本次商品访问的是2---->2-1-3
					// 将pids拆成一个数组
					String[] split = pids.split("-");
					List<String> asList = Arrays.asList(split);
					LinkedList<String> list = new LinkedList<>(asList);
					// 判断集合中是否存在当前的Pid
					if (list.contains(pid)) {
						// 包含当前查看商品的Pid
						list.remove(pid);

					}
					list.addFirst(pid);
					// 将包含cookie的集合转成串
					// [3,1,2]转成3-1-2
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < list.size() && i < 7; i++) {
						sb.append(list.get(i));
						sb.append("-");
					}
					// 去掉最后一个"-"
					pids = sb.substring(0, sb.length() - 1);

				}
			}
		}

		Cookie cookie_pids = new Cookie("pids", pids);
		response.addCookie(cookie_pids);

		request.getRequestDispatcher("/product_info.jsp").forward(request, response);
	}

	// 根据商品的类别获取商品的列表功能
	public void productList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 获得cid
		String cid = request.getParameter("cid");

		String currentPageStr = request.getParameter("currentPage");
		if (currentPageStr == null)
			currentPageStr = "1";
		int currentPage = Integer.parseInt(currentPageStr);
		int currentCount = 12;

		ProductService service = (ProductService) BeanFactory.getBean("productService");
		PageBean pageBean = service.findProductListByCid(cid, currentPage, currentCount);

		request.setAttribute("pageBean", pageBean);
		request.setAttribute("cid", cid);

		// 定义一个集合，用来存放根据来自cookie的pid所查到历史记录的Product
		List<Product> historyProductList = new ArrayList<>();

		// 为了显示"浏览过的商品"，在转发之前要获得客户端携带的cookie--pids
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("pids".equals(cookie.getName())) {
					String pids = cookie.getValue();
					// 目前获取到pid组成的字符串（3-2-1）,将其拆开
					String[] split = pids.split("-");
					// 根据pid,查到商品，存放到session域中
					for (String pid : split) {
						Product pro = service.findProductByPid(pid);
						historyProductList.add(pro);
					}
				}
			}
		}

		// 将历史记录的集合存放到域中
		request.setAttribute("historyProductList", historyProductList);

		request.getRequestDispatcher("/product_list.jsp").forward(request, response);
	}

	// addProductToCart
	// 商品添加到购物车功能
	public void addProductToCart(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();

		ProductService service = (ProductService) BeanFactory.getBean("productService");

		// 获得要放到购物车的产品pid
		String pid = request.getParameter("pid");
		// 获得该商品的购买数量
		int buyNum = Integer.parseInt(request.getParameter("buyNum"));

		// 获得product对象
		Product product = service.findProductByPid(pid);
		// 计算小计
		double subtotal = product.getShop_price() * buyNum;

		// 封装CartItem
		CartItem item = new CartItem();
		item.setBuyNum(buyNum);
		item.setProduct(product);
		item.setSubtotal(subtotal);

		// 获得购物车--判断是否已经在存在购物车
		Cart cart = (Cart) session.getAttribute("cart");
		if (cart == null) {
			// session域中没有cart，所以要创建一个
			cart = new Cart();
		}

		// 获得购物车中的两个属性：cartItems、total
		Map<String, CartItem> cartItems = cart.getCartItems();
		double total = cart.getTotal();

		// 将购物项放入购物车中：分两种情况：1、购物车中该商品已存在。2、购物车中该商品不存在
		// 且private Map<String, CartItem> cartItems = new HashMap<>();
		// 所以，购物车中肯定有数据
		if (cartItems.containsKey(pid)) {
			// 购物车中已存在该商品，则更新商品数量，更新商品小计
			CartItem cartItem = cartItems.get(pid);

			// 计算商品总额
			total += product.getShop_price() * buyNum;

			// 更新数量
			buyNum += cartItem.getBuyNum();
			cartItem.setBuyNum(buyNum);

			// 更改小计
			subtotal = product.getShop_price() * buyNum;
			cartItem.setSubtotal(subtotal);

			// 下面这句其实是多余的，因为集合中存放的是对象的引用
			cart.setCartItems(cartItems);

		} else {
			// 购物车中不存在该商品，则商品添加到购物车中
			cartItems.put(pid, item);

			// 计算商品总额
			total += product.getShop_price() * buyNum;
		}

		// 将总额封装到购物车
		cart.setTotal(total);

		// 将购物车再次放到session域中
		session.setAttribute("cart", cart);

		// 直接跳转到购物车界面
		// request.getRequestDispatcher("/cart.jsp").forward(request, response);
		response.sendRedirect(request.getContextPath() + "/cart.jsp");
	}

	// 在购物车中删除指定的商品项
	public void delProFromCart(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 删除 session域-->cart对象-->cartItems-->与pid对应的entry项，删除之后还要再更改total
		String pid = request.getParameter("pid");
		HttpSession session = request.getSession();

		Cart cart = (Cart) session.getAttribute("cart");
		// 注意：cart可能为空，用户停在购物车页面后离座，半小时后回来，此时session域已经被清空了，如果不进行判断，会报空指针错误
		if (cart != null) {
			// 删除Map中对应的entry项，返回值是该entry项的"value"部分
			CartItem removedCartItem = cart.getCartItems().remove(pid);
			// 更改total
			double total = cart.getTotal();
			double subtotal = removedCartItem.getSubtotal();
			total -= subtotal;
			cart.setTotal(total);
		}

		session.setAttribute("cart", cart);

		// 跳转回cart.jsp
		response.sendRedirect(request.getContextPath() + "/cart.jsp");

	}

	// 清空购物车
	public void clearCart(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");

		// 删除 session域-->cart对象-->cartItams Map中的所有键值对
		cart.getCartItems().clear();

		session.setAttribute("cart", cart);
		response.sendRedirect(request.getContextPath() + "/cart.jsp");

	}

	// 提交订单
	public void submitOrder(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			// 未登录，跳转到登陆页面
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}

		
		// 目的：封装好一个Order对象，传递给service层
		Order order = new Order();

		// 1.oid--该订单的订单号
		String oid = CommonUtils.getUUID();
		order.setOid(oid);

		// 2.ordertime--下单时间
		// Date date = new Date();
		// Timestamp timestamp = new Timestamp(date.getTime());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String orderTime = df.format(new Date());
		order.setOrdertime(orderTime);

		// 3.total--订单的总金额
		Cart cart = (Cart) session.getAttribute("cart");
		if (cart != null) {
			order.setTotal(cart.getTotal());
		}

		// 4.state--支付状态:1代表已付款，0代表未付款；当前界面肯定还没有付款
		order.setState(0);

		// 5.address--收货人地址，这部分是从“确认订单”界面获得的，此处还没有，置bull
		order.setAddress(null);

		// 6.name--收货人
		order.setName(null);

		// 7.telephone--收货人电话
		order.setTelephone(null);

		// 8.User user--该订单属于哪个用户
		order.setUser(user);

		// 9.List<OrderItem> orderItems--该订单中的订单项
		Map<String, CartItem> cartItems = cart.getCartItems();
		for (Map.Entry<String, CartItem> entry : cartItems.entrySet()) {
			OrderItem orderItem = new OrderItem();
			CartItem cartItem = entry.getValue();
			// 封装orderItem
			orderItem.setItemid(CommonUtils.getUUID());
			orderItem.setCount(cartItem.getBuyNum());
			orderItem.setSubtotal(cartItem.getSubtotal());
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setOrder(order);

			// 将封装好的orderItem存放到orderItems列表中去
			order.getOrderItems().add(orderItem);
		}

		ProductService service = (ProductService) BeanFactory.getBean("productService");
		service.submitOrder(order);

		session.setAttribute("order", order);

		// 页面跳转
		response.sendRedirect(request.getContextPath() + "/order_info.jsp");

	}

	// 确认订单---更新收货人信息+在线支付
	public void orderForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 1.更新收货人信息
		Map<String, String[]> parameterMap = request.getParameterMap();
		Order order = new Order();
		try {
			BeanUtils.populate(order, parameterMap);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		ProductService service = (ProductService) BeanFactory.getBean("productService");
		service.updateOrderInfo(order);

		// 2.在线支付

		// 获得 支付必须基本数据
		String orderid = request.getParameter("oid");
		String money = order.getTotal() + "";
		// 银行
		String pd_FrpId = request.getParameter("pd_FrpId");

		// 发给支付公司需要哪些数据
		String p0_Cmd = "Buy";
		String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
		String p2_Order = orderid;
		String p3_Amt = money;
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		// 支付成功回调地址 ---- 第三方支付公司会访问、用户访问
		// 第三方支付可以访问网址
		String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
		String p9_SAF = "";
		String pa_MP = "";
		String pr_NeedResponse = "1";
		// 加密hmac 需要密钥
		String keyValue = ResourceBundle.getBundle("merchantInfo").getString("keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc,
				p8_Url, p9_SAF, pa_MP, pd_FrpId, pr_NeedResponse, keyValue);

		String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId=" + pd_FrpId + "&p0_Cmd=" + p0_Cmd
				+ "&p1_MerId=" + p1_MerId + "&p2_Order=" + p2_Order + "&p3_Amt=" + p3_Amt + "&p4_Cur=" + p4_Cur
				+ "&p5_Pid=" + p5_Pid + "&p6_Pcat=" + p6_Pcat + "&p7_Pdesc=" + p7_Pdesc + "&p8_Url=" + p8_Url
				+ "&p9_SAF=" + p9_SAF + "&pa_MP=" + pa_MP + "&pr_NeedResponse=" + pr_NeedResponse + "&hmac=" + hmac;

		// 重定向到第三方支付平台
		response.sendRedirect(url);

	}

	// 获取当前用户所有订单
	public void myOrders(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			// 未登录，跳转到登陆页面
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}


		// 1.查询该用户的所有的订单信息（单表查询orders）
		ProductService service = (ProductService) BeanFactory.getBean("productService");
		// 集合中的每一个Order对象的数据是不完整的，缺少订单项，其他的无所谓，因为页面显示时不需要，关键字段：oid
		List<Order> orderList = service.findAllOrders(user.getUid());

		// 2.循环每个订单，为每个订单项填充订单项集合信息
		if (orderList != null) {
			// 用户有订单项，此处要判断一下，否则可能会报空指针异常
			for (Order order : orderList) {
				/*
				 * // 新建一个List<orderItem>用以存放orderItem List<OrderItem> orderItems = new
				 * ArrayList<>();
				 */
				// 此时不需要像上面这样新建一个对象，然后封装进order中，其实每一个order中已经有orderItems对象了，只不过对象为空而已。

				// 根据oid进行联表查询--List封装的是多个订单项，Map封装的是每一条记录内 字段-value 键值对
				List<Map<String, Object>> mapList = service.findAllOrderItemByOid(order.getOid());

				for (Map<String, Object> map : mapList) {

					try {

						// 从map中取出pimage pname shop_price 封装到product中
						Product product = new Product();
						BeanUtils.populate(product, map);

						// 从map中取出count subtotal 及
						// 封装好的product封装到orderItem中(product不是map中的数据，不能通过BeanUtils封装，需要手动封装)
						OrderItem orderItem = new OrderItem();
						BeanUtils.populate(orderItem, map);

						orderItem.setProduct(product);

						// 将orderItem封装到order中的orderItemList中去(可以直接get，因为集合中存放的是引用)
						order.getOrderItems().add(orderItem);

					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}

				}
			}
		}
		
		// orderList所有数据已经封装完成
		request.setAttribute("orderList", orderList);
		
		request.getRequestDispatcher("/order_list.jsp").forward(request, response);

	}

}