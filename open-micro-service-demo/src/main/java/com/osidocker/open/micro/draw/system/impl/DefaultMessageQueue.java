package com.osidocker.open.micro.draw.system.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.osidocker.open.micro.draw.system.IMessageQueue;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月28日 12:39
 * @Copyright: © Caoyj
 */
@Service(DefaultMessageQueue.DEFAULT_MESSAGE_QUEUE)
public class DefaultMessageQueue implements IMessageQueue<DrawResponseContext> {
    public static final String DEFAULT_MESSAGE_QUEUE = "defaultMessageQueue";
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void push(DrawResponseContext drawResponseContext) {
        logger.info(JSONUtils.toJSONString(drawResponseContext.getStatisticsMap()));
    }
}
