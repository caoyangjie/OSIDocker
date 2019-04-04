package com.osidocker.open.micro.draw.system.concurrent;

import com.osidocker.open.micro.draw.model.ActivePartake;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月12日 16:42
 * @Copyright: © Caoyj
 */
public class LocalDayPartakeCount extends AtomicEntity<ActivePartake> {

    private ActivePartake activePartake;
    /**
     * 并发访问次数更新器
     */
    private AtomicIntegerFieldUpdater<LocalDayPartakeCount> updater = AtomicIntegerFieldUpdater.newUpdater(LocalDayPartakeCount.class,"accessCount");

    /**
     * 更新基数对象
     */
    public volatile int accessCount;

    public Integer getVisit() {
        return updater.get(this);
    }

    /**
     * 获取访问次数
     * @return
     */
    public Integer incrementAndGet(){
        setChangeFlag(true);
        return updater.incrementAndGet(this);
    }

    public LocalDayPartakeCount(ActivePartake activePartake) {
        updater.set(this,activePartake.getVisit());
        this.activePartake = activePartake;
    }

    @Override
    public ActivePartake getInstance(String... key) {
        activePartake.setVisit(getVisit());
        return activePartake;
    }
}
