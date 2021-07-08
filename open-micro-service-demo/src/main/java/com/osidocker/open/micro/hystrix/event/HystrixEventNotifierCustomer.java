package com.osidocker.open.micro.hystrix.event;

import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;

public class HystrixEventNotifierCustomer extends HystrixEventNotifier {

    @Override
    public void markEvent(HystrixEventType eventType, HystrixCommandKey key) {
        //TODO 在此方法中可以做一些 关于 Hystrix Command 操作处理通知事件相关的上报工作
        super.markEvent(eventType, key);
    }
}
