package com.osidocker.open.micro.rebuild;

import com.osidocker.open.micro.entity.BaseCacheEntity;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 重建缓存的内存队列
 * @author Administrator
 *
 */
public class RebuildCacheDataQueue<CacheData extends BaseCacheEntity> {

	private ArrayBlockingQueue<CacheData> queue = new ArrayBlockingQueue<CacheData>(1000);
	
	public void put(CacheData cacheData) {
		try {
			queue.put(cacheData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public CacheData take() {
		try {
			return queue.take();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 内部单例类
	 * @author Administrator
	 *
	 */
	private static class Singleton {
		
		private static RebuildCacheDataQueue instance;
		
		static {
			instance = new RebuildCacheDataQueue();
		}
		
		public static RebuildCacheDataQueue getInstance() {
			return instance;
		}
		
	}
	
	public static RebuildCacheDataQueue getInstance() {
		return Singleton.getInstance();
	}
	
	public static void init() {
		getInstance();
	}
	
}
