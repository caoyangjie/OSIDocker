/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.kafka;

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
public abstract class AbsMessageHandler {
    /**
     * 执行消息处理
     * @param message   请求参数
     */
    public abstract <T> void execute(T message);

    /**
     * 消息类型
     * @return
     */
    public abstract Class messageClass();
}
