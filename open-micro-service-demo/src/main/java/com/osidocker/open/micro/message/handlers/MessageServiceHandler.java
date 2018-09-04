/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.message.handlers;

import com.osidocker.open.micro.message.AbsMessageHandler;
import com.osidocker.open.micro.vo.MessageEntity;
import org.springframework.stereotype.Service;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于16:20 2018/3/15
 * @修改说明：
 * @修改日期： 修改于16:20 2018/3/15
 * @版本号： V1.0.0
 */
@Service(value = "userMessageHandler")
public class MessageServiceHandler extends AbsMessageHandler<MessageEntity> {

    @Override
    public void execute(MessageEntity message) {
        System.out.println("message="+message);
    }

    @Override
    public Class<MessageEntity> messageClass() {
        return MessageEntity.class;
    }
}
