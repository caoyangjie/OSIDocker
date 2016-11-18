package org.osidocker.open.utils;

import java.util.HashMap;
import java.util.Set;

import org.osidocker.open.callback.SmsNotificationBackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloopen.rest.sdk.CCPRestSmsSDK;

public class SmsPushUtils{
	
	private static final String OSIDOCKER_SMS_APPID = "osidocker.sms.appid";
	private static final String OSIDOCKER_SMS_ACCOUNT_TOKEN = "osidocker.sms.account.token";
	private static final String OSIDOCKER_SMS_ACOUNT_SID = "osidocker.sms.acount.sid";
	private static final String OSIDOCKER_SMS_PORT = "osidocker.sms.port";
	private static final String OSIDOCKER_SMS_URL = "osidocker.sms.url";
	protected static Logger logger = LoggerFactory.getLogger(SmsPushUtils.class);
	
	private static CCPRestSmsSDK restAPI;
	
	public static void sendTemplateSMS(String to, String templateId, String[] datas,SmsNotificationBackHandler handler)
	{
		if(restAPI==null){
			init();
		}
		HashMap<String, Object> result = null;
		//初始化SDK
		result = restAPI.sendTemplateSMS(to,templateId ,datas);
		HashMap<String,Object> data = null;
		if("000000".equals(result.get("statusCode"))){
			//正常返回输出data包体信息（map）
			data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				logger.debug(key +" = "+object);
			}
			if(handler!=null){
				handler.succHandler(data);
			}
		}else{
			//异常返回输出错误码和错误信息
			logger.debug("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
			if(handler!=null){
				handler.failHandler(data);
			}
		}
		if(handler!=null){
			handler.doneHandler(data);
		}
	}
	
	protected static void init(){
		//******************************注释*********************************************
		//*初始化服务器地址和端口                                                       *
		//*沙盒环境（用于应用开发调试）：restAPI.init("sandboxapp.cloopen.com", "8883");*
		//*生产环境（用户应用上线使用）：restAPI.init("app.cloopen.com", "8883");       *
		//*******************************************************************************
		restAPI = new CCPRestSmsSDK();
		restAPI.init(
			GlobalConfig.getProperty(OSIDOCKER_SMS_URL), 
			GlobalConfig.getProperty(OSIDOCKER_SMS_PORT)
		);
		restAPI.setAccount(
			GlobalConfig.getProperty(OSIDOCKER_SMS_ACOUNT_SID),
			GlobalConfig.getProperty(OSIDOCKER_SMS_ACCOUNT_TOKEN)
		);
		restAPI.setAppId(GlobalConfig.getProperty(OSIDOCKER_SMS_APPID));
	}  
}
