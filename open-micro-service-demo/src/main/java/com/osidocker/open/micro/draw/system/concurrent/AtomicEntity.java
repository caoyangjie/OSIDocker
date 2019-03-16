package com.osidocker.open.micro.draw.system.concurrent;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月12日 17:42
 * @Copyright: © 麓山云
 */
public abstract class AtomicEntity<T> implements Serializable {

    private AtomicBoolean changeFlag = new AtomicBoolean(false);

    public boolean isUpdated() {
        return changeFlag.get();
    }

    public void setChangeFlag(boolean change) {
        this.changeFlag.set(change);
    }

    /**
     * 获取原子更新的实例对象
     * @return
     */
    public abstract T getInstance(String... key);

    /**
     * 获取并发包装对象
     * @return
     */
    public <D extends AtomicEntity<T>> D getAtomicEntity(){
        return (D) this;
    }
}
