/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于11:38 2018/3/15
 * @修改说明：
 * @修改日期： 修改于11:38 2018/3/15
 * @版本号： V1.0.0
 */
public abstract class AbsMessageProducer implements IProducer{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());


    protected void success(Object msg){
        logger.info("kafka send消息成功:", msg);
    }

    protected void fail(Throwable throwable){
        logger.error("kafka send消息失败:", throwable);
    }
}
