package cn.geowind.takeout.servlet;

import static cn.geowind.takeout.util.ApiUtil.API_VERSION;
import static cn.geowind.takeout.util.ApiUtil.BASE_URL;
import static cn.geowind.takeout.util.ApiUtil.CLASSES;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.geowind.takeout.util.HttpsUtil;
import cn.geowind.takeout.util.Utils;

/**
 * 
 * 
 * @author 朱霜
 * @school University of South China
 * @date 2014.03
 */
public class AppServlet extends HttpServlet {
	private static final long serialVersionUID = 2817787995641311004L;

	private void process(HttpServletRequest request,
			HttpServletResponse response) throws IOException, JSONException {
		String uri = request.getRequestURI();
		int index = uri.lastIndexOf("/");
		String action = uri.substring(index + 1);
		if (action.equals("app")) {
			System.out.println("app:");
			response.sendRedirect("http://pager.u.qiniudn.com/TakeOut.apk");
		} else if (action.equals("sms")) {
			System.out.println("sms:");
			String tel = request.getParameter("tel");
			/**
			 * 计算出短信验证码
			 */
			String code = String.valueOf(Utils.genCheckCode(tel));

			String url = BASE_URL + API_VERSION + CLASSES + "/Verify";
			String where = "?where={\"tel\":\"" + tel + "\"}";
			String result4get = HttpsUtil.get(url + where, "utf-8");
			System.out.println("get result:" + result4get);

			JSONArray array = new JSONObject(result4get)
					.getJSONArray("results");
			if (array.length() == 0) {
				/**
				 * Verify表中不存在该手机号码，则发Post请求去创建
				 */
				JSONObject obj = new JSONObject();
				obj.put("tel", tel);
				obj.put("verifyCode", code);
				obj.put("time", System.currentTimeMillis());
				System.out.println("data:" + obj.toString(4));
				String result4post = HttpsUtil.post(url, obj.toString());
				System.out.println("post json:" + result4post);
			} else {
				/**
				 * Verify表中存在该手机号码，则发Put请求去更新
				 */
				String id = array.getJSONObject(0).getString("objectId");
				JSONObject data = new JSONObject();
				data.put("verifyCode", code);
				data.put("time", System.currentTimeMillis());
				String result4put = HttpsUtil.put(url + "/" + id, data
						.toString());
				System.out.println("put json:" + result4put);
			}

			try {
				Utils.sendSMS(tel, code);
			} catch (KeyManagementException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.write("{\"code\":\"200\"}");
			out
					.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
			out.println("<HTML>");
			out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
			out.println("  <BODY> ");
			// out.println(obj.toString(4));
			out.println(" <br/>");
			// out.print(json);
			out.println(" <br/>");
			// out.print(r);
			out.println("  </BODY>");
			out.println("</HTML>");
			out.flush();
			out.close();
		} else if (action.equals("voice")) {
			System.out.println("voice:");
			String tel = request.getParameter("tel");
			int temp = Integer.parseInt(tel.substring(4, 8));
			String verifyCode = String.valueOf((temp * 8) % 10000);
			String url = BASE_URL + API_VERSION + CLASSES + "/" + "Verify";
			JSONObject obj = new JSONObject();
			obj.put("tel", Long.parseLong(request.getParameter("tel")));
			obj.put("verifyCode", verifyCode);
			obj.put("time", System.currentTimeMillis());
			System.out.println("data:" + obj.toString(4));
			String json = HttpsUtil.post(url, obj.toString());
			System.out.println("json:" + json);
			// try {
			// Utils.sendVoiceVerifyCall(tel, verifyCode);
			// } catch (KeyManagementException e) {
			// e.printStackTrace();
			// } catch (NoSuchAlgorithmException e) {
			// e.printStackTrace();
			// }
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out
					.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
			out.println("<HTML>");
			out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
			out.println("  <BODY> ");
			out.println(obj.toString(4));
			out.println(" <br/>");
			out.print(json);
			out.println(" <br/>");
			out.print("Success");
			out.println("  </BODY>");
			out.println("</HTML>");
			out.flush();
			out.close();
		}
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			process(request, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			process(request, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out
				.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

}
