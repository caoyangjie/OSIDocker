package com.osidocker.open.micro.lock;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description:    //TODO 实现 单机 锁资源功能
 * @author: caoyj
 * @date: 2019年03月12日 10:02
 * @Copyright: © Caoyj
 */
public class LocalLockResource implements ILockResource{
    /**
     * 所资源对象存储的Map
     */
    private ConcurrentHashMap<String, Lock> lockResourceMap = new ConcurrentHashMap();

    @Override
    public Lock tryGetLock( String lockKey ) throws GunsLockException {
        Optional.ofNullable(lockKey).orElseThrow(()->new GunsLockException(new Exception("所资源名称不允许为空!")));
        Lock lock = lockResourceMap.getOrDefault(lockKey,new ReentrantLock());
        if( !lockResourceMap.containsKey(lockKey) ){
            if( lockResourceMap.putIfAbsent(lockKey,lock)!=null ){
                lock = lockResourceMap.get(lockKey);
            }
        }else{
            lock = lockResourceMap.get(lockKey);
        }
        return lock;
    }

    @Override
    public synchronized void unlock(String lockKey) {
        lockResourceMap.get(lockKey).unlock();
    }
}
