package cn.geowind.takeout.verify;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * <p>
 * Title: RESTAPI.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: http://www.cloopen.com
 * </p>
 * 
 * @author JorstinChan
 * @date 2013-11-19
 * @version 2.4
 */
public abstract class RestApi {

	/**
	 * 
	 * @param accountSid
	 * @param authToken
	 * @param subAccountSid
	 * @param appId
	 * @param to
	 * @param body
	 * @param msgType
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public abstract String sendTemplateSMS(String accountSid, String authToken,
			String appId, String to, String code)
			throws NoSuchAlgorithmException, KeyManagementException;

}
