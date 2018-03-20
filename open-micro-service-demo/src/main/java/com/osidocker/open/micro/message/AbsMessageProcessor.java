/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.message;

import com.alibaba.fastjson.JSONObject;
import com.osidocker.open.micro.spring.SpringContext;
import com.osidocker.open.micro.vo.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于14:40 2018/3/15
 * @修改说明：
 * @修改日期： 修改于14:40 2018/3/15
 * @版本号： V1.0.0
 */
public abstract class AbsMessageProcessor {
    public static final String SERVICE_ID = "serviceId";
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 处理监听器监听到的指定的Queue的消息
     * @param message
     */
    public void process(String message) {
        // 首先将message转换成json对象
        JSONObject messageJSONObject = JSONObject.parseObject(message);

        // 从这里提取出消息对应的服务的标识
        String serviceId = messageJSONObject.getString(SERVICE_ID);

        // 如果是信息服务
        Optional<AbsMessageHandler<BaseMessage>> optionalHandler = Optional.ofNullable((AbsMessageHandler) SpringContext.getApplicationContext().getBean(serviceId));
        if( optionalHandler.isPresent() ){
            AbsMessageHandler handler = optionalHandler.get();
            handler.execute(JSONObject.parseObject(message, (Class<BaseMessage>) handler.messageClass()));
        }else{
            logger.error("未能找到服务ID为：【"+serviceId+"】的服务!处理消息内容为：【"+messageJSONObject.toJSONString()+"】");
        }
    }
}
