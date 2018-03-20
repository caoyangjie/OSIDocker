package com.osidocker.open.micro.prewarm;

import com.alibaba.fastjson.JSONArray;
import com.osidocker.open.micro.entity.BaseCacheEntity;
import com.osidocker.open.micro.service.IDataOperateService;
import com.osidocker.open.micro.zk.ZooKeeperConnectSession;

/**
 * 缓存预热线程
 * @author Administrator
 *
 */
public abstract class CachePrewarmThread<ResponseEntity extends BaseCacheEntity> extends Thread {

	/**
	 * 热点数据路径名称
	 */
	public static final String HOT_TASK_ID_LIST = "/hot-taskId-list";
    public static final String TASK_ID_LOCK = "/taskId-lock-";
    public static final String TASK_ID_STATUS_LOCK = "/taskId-status-lock-";
    public static final String TASK_ID_STATUS = "/taskId-status-";
    public static final String TASK_HOT_LIST = "/task-hot-list-";
    public static final String SUCCESS = "success";

    @Override
	public void run() {
		ZooKeeperConnectSession zkSession = ZooKeeperConnectSession.getInstance();
		IDataOperateService<ResponseEntity> dataOperateService = getDataOperateService();
		
		// 获取storm taskId列表
		String taskIdList = zkSession.getNodeData(HOT_TASK_ID_LIST);
		
		System.out.println("【CachePreWarmThread获取到taskId列表】hotTaskIdList=" + taskIdList);
		
		if(taskIdList != null && !"".equals(taskIdList)) {
			String[] taskIdListSplit = taskIdList.split(",");
			for(String taskId : taskIdListSplit) {
				String taskIdLockPath = TASK_ID_LOCK + taskId;
				
				boolean result = zkSession.acquireFastFailedDistributedLock(taskIdLockPath);
				if(!result) {
					continue;
				}
				
				String taskIdStatusLockPath = TASK_ID_STATUS_LOCK + taskId;
				zkSession.acquireDistributedLock(taskIdStatusLockPath);
				
				String taskIdStatus = zkSession.getNodeData(TASK_ID_STATUS + taskId);
				System.out.println("【CachePreWarmThread获取task的预热状态】taskId=" + taskId + ", status=" + taskIdStatus);
				
				if("".equals(taskIdStatus)) {
					String productIdList = zkSession.getNodeData(TASK_HOT_LIST + taskId);
					System.out.println("【CachePreWarmThread获取到task的热门商品列表】productIdList=" + productIdList);
					JSONArray hotIdJSONArray = JSONArray.parseArray(productIdList);
					for(int i = 0; i < hotIdJSONArray.size(); i++) {
						Long hotId = hotIdJSONArray.getLong(i);
						ResponseEntity responseEntity = dataOperateService.findResponseEntity(hotId,null);
						dataOperateService.setResponseEntity(responseEntity);
						System.out.println("【CachePrWarmThread将商品数据设置到redis缓存中】productInfo=" + responseEntity);
					}
					
					zkSession.createNode(TASK_ID_STATUS + taskId);
					zkSession.setNodeData(TASK_ID_STATUS + taskId, SUCCESS);
				}
				
				zkSession.releaseDistributedLock(taskIdStatusLockPath);
				
				zkSession.releaseDistributedLock(taskIdLockPath);
			}
		}
	}

	/**
	 * 获取一个数据操作对象
	 * @return	返回数据操作对象
	 */
	protected abstract IDataOperateService<ResponseEntity> getDataOperateService();

}
