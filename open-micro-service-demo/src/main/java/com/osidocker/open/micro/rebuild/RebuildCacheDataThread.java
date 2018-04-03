package com.osidocker.open.micro.rebuild;

import com.osidocker.open.micro.entity.BaseCacheEntity;
import com.osidocker.open.micro.model.ProductInfo;
import com.osidocker.open.micro.service.ICacheService;
import com.osidocker.open.micro.service.IDataOperateService;
import com.osidocker.open.micro.spring.SpringContext;
import com.osidocker.open.micro.zk.ZooKeeperConnectSession;
import com.osidocker.open.micro.zk.ZooKeeperSession;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 缓存重建线程
 * @author Administrator
 *
 */
public abstract class RebuildCacheDataThread<CacheData extends BaseCacheEntity> implements Runnable {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public void run() {
		RebuildCacheDataQueue<CacheData> rebuildCacheQueue = RebuildCacheDataQueue.getInstance();
		ZooKeeperConnectSession zkSession = ZooKeeperConnectSession.getInstance();
        IDataOperateService<CacheData> dataOperateService = getDataOperateService();

		while(true) {
			CacheData cacheData = rebuildCacheQueue.take();
			
			zkSession.acquireDistributedLock(cacheData.getLockKey());
			
			CacheData existedCacheData = dataOperateService.getResponseEntityCache(cacheData.getCacheId());
			
			if(existedCacheData != null) {
				// 比较当前数据的时间版本比已有数据的时间版本是新还是旧
				try {
					Date date = cacheData.getEditTime();
					Date existedDate = existedCacheData.getEditTime();
					
					if(date.before(existedDate)) {
						System.out.println("current date[" + cacheData.getEditor() + "] is before existed date[" + existedCacheData.getEditTime() + "]");
						continue;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("current date[" + cacheData.getEditTime() + "] is after existed date[" + existedCacheData.getEditTime() + "]");
			} else {
				System.out.println("existed product info is null......");   
			}
			
            dataOperateService.setResponseEntity(cacheData);

			zkSession.releaseDistributedLock(cacheData.getLockKey());
		}
	}

    /**
     *
     * @return
     */
    protected abstract IDataOperateService<CacheData> getDataOperateService();

}
