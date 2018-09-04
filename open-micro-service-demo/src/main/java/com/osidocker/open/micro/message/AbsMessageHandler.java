/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.message;

import com.osidocker.open.micro.service.GenerateService;
import com.osidocker.open.micro.spring.SpringContext;
import com.osidocker.open.micro.vo.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于15:08 2018/3/8
 * @修改说明：
 * @修改日期： 修改于15:08 2018/3/8
 * @版本号： V1.0.0
 */
public abstract class AbsMessageHandler<T extends BaseMessage> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 执行消息处理
     * @param message   请求参数
     */
    public abstract void execute(T message);

    /**
     * 执行消息转换类型
     * @return
     */
    public abstract Class<T> messageClass();

    public GenerateService getGenerateService(String beanName){
        return (GenerateService) SpringContext.getApplicationContext().getBean(beanName);
    }
}
