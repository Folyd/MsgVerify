package cn.geowind.takeout.util;

import static cn.geowind.takeout.util.ApiUtil.*;
import static cn.geowind.takeout.util.ApiUtil.ACCOUNTS;
import static cn.geowind.takeout.util.ApiUtil.SMS;
import static cn.geowind.takeout.util.ApiUtil.SMS_APP_ID;
import static cn.geowind.takeout.util.ApiUtil.SMS_VERSION;
import static cn.geowind.takeout.util.ApiUtil.TEMPLATE_SMS;
import static cn.geowind.takeout.util.ApiUtil.TEST_ACCOUNT_SID;
import static cn.geowind.takeout.util.ApiUtil.TEST_AUTH_TOKEN;
import static cn.geowind.takeout.util.ApiUtil.TEST_SMS_APP_ID;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import cn.geowind.takeout.verify.JsonRestApi;

public class Utils {

	public static final int MAX = Integer.MAX_VALUE;
	public static final int MIN = (int) MAX / 2;
	public static final int TEN_MINUTE = 10 * 60;

	/**
	 * 发送语音验证码
	 * 
	 * @param tel
	 * @param verifyCode
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public static void sendVoiceVerifyCall(String tel, String verifyCode)
			throws KeyManagementException, NoSuchAlgorithmException {
		String accountSid = TEST_ACCOUNT_SID;// 主账户Id
		String authToken = TEST_AUTH_TOKEN;// 主账户授权令牌
		String appId = TEST_SMS_APP_ID;// 应用Id

		// 应用内容参数
		String to = tel;// 短信接收端手机号

		// 调用发送短信接口
		String result = new JsonRestApi().sendCall(accountSid, authToken,
				appId, to, verifyCode);
		// 打印发送短信响应体
		System.out.println("Response content is: " + result);
	}

	public static void sendSMS(String tel, String code)
			throws KeyManagementException, NoSuchAlgorithmException {
		String accountSid = ACCOUNT_SID;// 主账户Id
		String authToken = AUTH_TOKEN;// 主账户授权令牌
		String appId = SMS_APP_ID;// 应用Id

		// 应用内容参数
		String to = tel;// 短信接收端手机号

		// 调用发送短信接口
		String result = new JsonRestApi().sendTemplateSMS(accountSid,
				authToken, appId, to, code);
		// 打印发送短信响应体
		System.out.println("Response content is: " + result);
	}

	/**
	 * Cloopen Cloud发送短信</br> {@link http
	 * ://docs.cloopen.com/index.php/Rest%E4%BB%8B%E7%BB%8D}</br> {@link http
	 * ://docs.cloopen.com/index.php/%E6%A8%A1%E6%9D%BF%E7%9F%AD%E4%BF%A1}
	 * 
	 * @param tel
	 * @throws Exception
	 */
	public static String sendSmsCode(String tel) throws Exception {
		// 构造请求URL内容
		String timestamp = DateUtil.dateToStr(new Date(),
				DateUtil.DATE_TIME_NO_SLASH);
		// md5(主账户Id + 主账户授权令牌 + 时间戳)
		String origin = ACCOUNT_SID + AUTH_TOKEN + timestamp;
		// MD5加密
		EncryptUtil eu = new EncryptUtil();
		String sigParameter = eu.md5Digest(origin);
		String auth = ACCOUNT_SID + ":" + timestamp;
		String authorization = eu.base64Encoder(auth);
		String url = SMS_BASE_URL + SMS_VERSION + ACCOUNTS + ACCOUNT_SID + SMS
				+ TEMPLATE_SMS + "?sig=" + sigParameter;
		JSONObject obj = new JSONObject();
		obj.put("to", tel);
		obj.put("appId", SMS_APP_ID);
		obj.put("templateId", "1");
		String data = obj.toString();
		System.out.println("data:" + data);
		return HttpsUtil.postForSms(url, data, authorization);
	}

	/**
	 * 生成验证码
	 * 
	 * @param tel
	 * @return
	 */
	public static int genCheckCode(String tel) {
		int i = Integer.parseInt(tel.substring(3, 8));
		int r = Calendar.getInstance().get(Calendar.MINUTE) % 10;
		r = r == 0 ? 3 : r;
		return i * r % 100000;

	}

	/**
	 * t is very important to keep sendNo unique.
	 * 
	 * @return sendNo
	 */
	public static int getRandomSendNo() {
		return (int) (MIN + Math.random() * (MAX - MIN));
	}

}
