/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package cn.geowind.takeout.verify;

import static cn.geowind.takeout.util.ApiUtil.*;
import static cn.geowind.takeout.util.ApiUtil.SMS_BASE_URL;
import static cn.geowind.takeout.util.ApiUtil.SMS_VERSION;

import java.io.ByteArrayInputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.geowind.takeout.util.DateUtil;
import cn.geowind.takeout.util.EncryptUtil;

/**
 * 
 * <p>
 * Title: RestAPI.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: http://www.cloopen.com
 * </p>
 * 
 * @author JorstinChan
 * @date 2013-9-27
 * @version 2.4
 */
public class JsonRestApi extends RestApi {

	public String sendCall(String accountSid, String authToken, String appId,
			String to, String verifyCode) throws KeyManagementException,
			NoSuchAlgorithmException {
		String result = "";
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = chc.registerSSL(
				"sandboxapp.cloopen.com", "TLS", 8883, "https");
		try {
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);
			// md5(主账户Id + 主账户授权令牌 + 时间戳)
			String sig = accountSid + authToken + timestamp;
			// MD5加密
			EncryptUtil eu = new EncryptUtil();
			String signature = eu.md5Digest(sig);

			StringBuffer sb = new StringBuffer();
			String url = sb.append(TEST_SMS_BASE_URL).append(SMS_VERSION)
					.append("/Accounts/").append(accountSid).append("/Calls")
					.append("/VoiceVerify").append("?sig=").append(signature)
					.toString();
			// 创建HttpPost
			HttpPost httppost = new HttpPost(url);
			setHttpHeader(httppost);
			String src = accountSid + ":" + timestamp;
			String auth = eu.base64Encoder(src);
			httppost.setHeader("Authorization", auth);// base64(主账户Id + 冒号 +
			// 时间戳)
			JSONObject obj = new JSONObject();
			obj.put("to", to);
			obj.put("appId", appId);
			obj.put("verifyCode", verifyCode);
			obj.put("playTimes", "3");
			String data = obj.toString();

			System.out
					.println("---------------------------- SendSMS for JSON begin----------------------------");
			System.out.println("data: " + data);

			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(data
					.getBytes("UTF-8")));
			requestBody.setContentLength(data.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);

			// 执行客户端请求
			HttpResponse response = httpclient.execute(httppost);

			// 获取响应实体信息
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			// 确保HTTP响应内容全部被读出或者内容流被关闭
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			httpclient.getConnectionManager().shutdown();
		}
		// 返回发送短信的响应体
		return result;
	}

	/**
	 * @brief 发送短信
	 * @param accountSid
	 *            主账号
	 * @param authToken
	 *            主账号令牌
	 * @param appId
	 *            应用id
	 * @param to
	 *            接收短信的电话
	 * @return HttpPost 协议包封装
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public String sendTemplateSMS(String accountSid, String authToken,
			String appId, String to, String code)
			throws NoSuchAlgorithmException, KeyManagementException {
		String result = "";
		// 创建HttpClient
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = chc.registerSSL("app.cloopen.com",
				"TLS", 8883, "https");

		try {
			// 构造请求URL内容
			String timestamp = DateUtil.dateToStr(new Date(),
					DateUtil.DATE_TIME_NO_SLASH);
			// md5(主账户Id + 主账户授权令牌 + 时间戳)
			String sig = accountSid + authToken + timestamp;
			// MD5加密
			EncryptUtil eu = new EncryptUtil();
			String signature = eu.md5Digest(sig);

			StringBuffer sb = new StringBuffer();
			String url = sb.append(SMS_BASE_URL).append(SMS_VERSION).append(
					"/Accounts/").append(accountSid).append(SMS).append(
					TEMPLATE_SMS).append("?sig=").append(signature).toString();
			// 创建HttpPost
			HttpPost httppost = new HttpPost(url);
			setHttpHeader(httppost);
			String src = accountSid + ":" + timestamp;
			String auth = eu.base64Encoder(src);
			httppost.setHeader("Authorization", auth);// base64(主账户Id + 冒号 +
			// 时间戳)
			JSONObject obj = new JSONObject();
			obj.put("to", to);
			obj.put("appId", appId);
			obj.put("templateId", SMS_TEMPLATE_ID);
			JSONArray replace = new JSONArray();
			replace.put(code);
			obj.put("datas", replace);
			String data = obj.toString();

			System.out
					.println("---------------------------- SendSMS for JSON begin----------------------------");
			System.out.println("data: " + data);

			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(data
					.getBytes("UTF-8")));
			requestBody.setContentLength(data.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);

			// 执行客户端请求
			HttpResponse response = httpclient.execute(httppost);

			// 获取响应实体信息
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
			// 确保HTTP响应内容全部被读出或者内容流被关闭
			EntityUtils.consume(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			httpclient.getConnectionManager().shutdown();
		}
		// 返回发送短信的响应体
		return result;
	}

	private void setHttpHeader(AbstractHttpMessage httpMessage) {
		// 构造请求头信息
		httpMessage.setHeader("Accept", "application/json");
		httpMessage.setHeader("Content-Type", "application/json;charset=utf-8");
	}
}
