package com.osidocker.open.micro.draw.system.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月14日 17:59
 * @Copyright: © Caoyj
 */
public class LocalAccessCount extends AtomicEntity<AtomicInteger> {

    private AtomicInteger accessCount;

    public LocalAccessCount(AtomicInteger accessCount) {
        this.accessCount = accessCount;
    }

    @Override
    public AtomicInteger getInstance(String... key) {
        return accessCount;
    }
}
