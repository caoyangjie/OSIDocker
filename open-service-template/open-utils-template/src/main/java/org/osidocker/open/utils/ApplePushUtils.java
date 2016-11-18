package org.osidocker.open.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.osidocker.open.callback.AppleNotificationBackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

/**
 * 通过此类可以放Ios设备推送消息
 * @author caoyangjie
 *
 */
public class ApplePushUtils {
	private static Logger logger = LoggerFactory.getLogger(ApplePushUtils.class);
	
	public static void pushMessage(String tokens,String message,int numOnIcon,String sound,Map<String,Object> pushArgs,AppleNotificationBackHandler handler) throws JSONException{
		List<String> tokenList = new ArrayList<String>();
		tokenList.add(tokens);
		pushMessage(tokenList, message, numOnIcon, sound, pushArgs, handler);
	}
	
	public static void pushMessage(List<String> tokens, String message,int numOnIcon, String sound,Map<String,Object> pushArgs,AppleNotificationBackHandler handler) throws JSONException {
    	String webPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String path = webPath + "PushJava.p12";
        String password = "123456";
        pushMessage(tokens, getPayLoad(message, sound, numOnIcon, pushArgs),handler,path, password);
    }
	
	private static PushNotificationPayload getPayLoad(String message,String sound,int numOnIcon,Map<String,?> map) throws JSONException {
		logger.info("----------------message:" + message);
		PushNotificationPayload payLoad = new PushNotificationPayload();
        payLoad.addAlert(message); // 消息内容
        payLoad.addBadge(numOnIcon); // iphone应用图标上小红圈上的数值
        payLoad.addSound(sound);// 铃音
        for (String k : map.keySet()) {
        	payLoad.addCustomDictionary(k, map.get(k));
		}
        return payLoad;
	}
	
	private static void pushMessage(List<String> tokens,PushNotificationPayload payLoad,AppleNotificationBackHandler handler, String certificatePath, String certificatePassword) {
        try {
            PushNotificationManager pushManager = new PushNotificationManager();
            // true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
            pushManager.initializeConnection(new AppleNotificationServerBasicImpl(certificatePath, certificatePassword, true));

            List<PushedNotification> notifications = new ArrayList<PushedNotification>();
            // 发送push消息
            if (tokens != null) {
                if (tokens.size() == 1) {
                	logger.info("------------单条推送---------------");
                    Device device = new BasicDevice();
                    device.setToken(tokens.get(0));
                    PushedNotification notification = pushManager.sendNotification(device, payLoad, true);
                    notifications.add(notification);
                } else if (tokens.size() > 1) {
                	logger.info("------------多条推送---------------");
                    List<Device> device = new ArrayList<Device>();
                    for (String token : tokens) {
                        device.add(new BasicDevice(token));
                    }
                    notifications = pushManager.sendNotifications(payLoad, device);
                }
            }
            List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
            List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);

            if (failedNotifications != null && failedNotifications.size() > 0) {
            	logger.info("失败条数=" + failedNotifications.size());
                for (PushedNotification failedNotification : failedNotifications) {
                    Device d = failedNotification.getDevice();
                    logger.info("deviceId=" + d.getDeviceId()  + d.getLastRegister() + "; token=" + d.getToken());
                }
                if(handler!=null){
                	handler.failHandler(failedNotifications);
                }
            }
            
            if (successfulNotifications != null && successfulNotifications.size() > 0) {
            	logger.info("成功条数=" + successfulNotifications.size());
                for (PushedNotification successfulNotification : successfulNotifications) {
                    Device d = successfulNotification.getDevice();
                    logger.info("deviceId=" + d.getDeviceId()  + d.getLastRegister() + "; token=" + d.getToken());
                }
                if(handler!=null){
                	handler.succHandler(successfulNotifications);
                }
            }
        } catch (Exception e) {
        	logger.error("消息推送异常...");
            e.printStackTrace(System.err);
            if(handler!=null){
            	handler.errorHandler(e);
            }
        } finally{
        	if(handler!=null){
        		handler.doneHandler();
        	}
        }
    }
}
