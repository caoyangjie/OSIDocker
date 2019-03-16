package com.osidocker.open.micro.lock;

import java.util.concurrent.locks.Lock;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月12日 9:45
 * @Copyright: © 麓山云
 */
public interface ILockResource {

    /**
     * 尝试获取锁
     * @param lockKey   锁资源名称
     * @return
     * @throws GunsLockException 获取锁异常
     */
    Lock tryGetLock(String lockKey) throws GunsLockException;

    /**
     * 是否锁资源
     * @param lockKey
     */
    void unlock(String lockKey);
}
