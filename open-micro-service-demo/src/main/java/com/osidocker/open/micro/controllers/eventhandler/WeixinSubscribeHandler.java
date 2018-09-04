/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.controllers.eventhandler;

import com.github.sd4324530.fastweixin.handle.EventHandle;
import com.github.sd4324530.fastweixin.message.BaseMsg;
import com.github.sd4324530.fastweixin.message.req.BaseEvent;
import com.github.sd4324530.fastweixin.message.req.EventType;
import com.osidocker.open.micro.config.PayPropertiesConfig;
import com.osidocker.open.micro.pay.api.ApiWexinService;
import com.osidocker.open.micro.pay.entity.AccessToken;
import com.osidocker.open.micro.pay.entity.WeXinUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 17:50 2018/8/28
 * @修改说明：
 * @修改日期： 17:50 2018/8/28
 * @版本号： V1.0.0
 */
@Service("subscribeHandler")
public class WeixinSubscribeHandler implements EventHandle<BaseEvent> {
    private Logger logger = LoggerFactory.getLogger(WeixinSubscribeHandler.class);

    @Autowired
    ApiWexinService apiWexinService;
    @Autowired
    PayPropertiesConfig config;

    @Override
    public BaseMsg handle(BaseEvent event) {
        String openId = event.getFromUserName();
        if( isNotExist(openId) ){
            logger.info("事件 eventType：" + event.getMsgType() + ",openid=" + openId);
            AccessToken accessToken = apiWexinService.getAccessToken(config.getWxAppid(),config.getWxSecret(),"0");
            WeXinUserInfo userInfo = apiWexinService.getUserInfo(accessToken.getAccessToken(),accessToken.getOpenId());
            //存储微信用户信息
            apiWexinService.saveWeXinUserInfo(userInfo);
        }
        return null;
    }

    private boolean isNotExist(String openId) {
        return !Optional.ofNullable(apiWexinService.getUserInfoByOpenId(openId)).isPresent();
    }

    @Override
    public boolean beforeHandle(BaseEvent event) {
        return EventType.SUBSCRIBE.equalsIgnoreCase(event.getEvent());
    }
}
