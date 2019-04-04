package com.osidocker.open.micro.draw.system.impl;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 重建缓存的内存队列
 * @author Administrator
 *
 */
public class CacheSuccessDataQueue<CacheData> {

	private ArrayBlockingQueue<CacheData> queue = new ArrayBlockingQueue<CacheData>(2000);
	
	public void put(CacheData cacheData) {
		try {
			queue.put(cacheData);
		} catch (InterruptedException e) {
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

	public boolean isEmpty(){
		return queue.isEmpty();
	}
	
	/**
	 * 内部单例类
	 * @author Administrator
	 *
	 */
	private static class Singleton {
		
		private static CacheSuccessDataQueue instance;
		
		static {
			instance = new CacheSuccessDataQueue();
		}
		
		public static CacheSuccessDataQueue getInstance() {
			return instance;
		}
		
	}
	
	public static CacheSuccessDataQueue getInstance() {
		return Singleton.getInstance();
	}
}
