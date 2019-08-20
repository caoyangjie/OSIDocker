package com.osidocker.open.micro.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * ZooKeeperSession
 * @author Administrator
 *
 */
public class ZooKeeperConnectSession {

    public static final String LOCK_CACHE = "/lock-cache";
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

	private ZooKeeper zookeeper;

	public ZooKeeperConnectSession() {
		// 去连接zookeeper server，创建会话的时候，是异步去进行的
		// 所以要给一个监听器，说告诉我们什么时候才是真正完成了跟zk server的连接
		try {
			this.zookeeper = new ZooKeeper(
					"192.168.31.181:2181",
					50000,
					new ZooKeeperWatcher());
			// 给一个状态CONNECTING，连接中
			System.out.println(zookeeper.getState());

			try {
				// CountDownLatch
				// java多线程并发同步的一个工具类
				// 会传递进去一些数字，比如说1,2 ，3 都可以
				// 然后await()，如果数字不是0，那么久卡住，等待

				// 其他的线程可以调用coutnDown()，减1
				// 如果数字减到0，那么之前所有在await的线程，都会逃出阻塞的状态
				// 继续向下运行

				connectedSemaphore.await();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("ZooKeeper session established......");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取分布式锁
	 * @param lockKey
	 */
	public void acquireDistributedLock(String lockKey) {
		String path = LOCK_CACHE + lockKey;
		try {
			zookeeper.create(path, "".getBytes(),
					Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			System.out.println("success to acquire lock for cacheData[lockKey=" + lockKey + "]");
		} catch (Exception e) {
			// 如果那个商品对应的锁的node，已经存在了，就是已经被别人加锁了，那么就这里就会报错
			// NodeExistsException
			int count = 0;
			while(true) {
				try {
					Thread.sleep(1000);
					zookeeper.create(path, "".getBytes(),
							Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				} catch (Exception e2) {
					count++;
					System.out.println("this is the " + count + " times try to acquire lock for product[id=" + lockKey + "]......");
					continue;
				}
				System.out.println("success to acquire lock for cacheData[lockKey=" + lockKey + "] after " + count + " times try......");
				break;
			}
		}
	}


    /**
     * 获取分布式锁快速成功失败
     * @param path 快速获取路径
     */
    public boolean acquireFastFailedDistributedLock(String path) {
        try {
            zookeeper.create(path, "".getBytes(),
                    Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success to acquire lock for " + path);
            return true;
        } catch (Exception e) {
            System.out.println("fail to acquire lock for " + path);
        }
        return false;
    }

	/**
	 * 释放掉一个分布式锁
	 * @param lockKey
	 */
	public void releaseDistributedLock(String lockKey) {
		String path = LOCK_CACHE + lockKey;
		try {
			zookeeper.delete(path, -1);
			System.out.println("release the lock for cacheData[lockKey=" + lockKey + "]......");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * 获取节点数据
     * @param path  路径
     * @return
     */
    public String getNodeData(String path) {
        try {
            return new String(zookeeper.getData(path, false, new Stat()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 设置节点数据
     * @param path  路径
     * @param data  数据
     */
    public void setNodeData(String path, String data) {
        try {
            zookeeper.setData(path, data.getBytes(), -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建节点数据
     * @param path  路径
     */
    public void createNode(String path) {
        try {
            zookeeper.create(path, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (Exception e) {

        }
    }

	/**
	 * 建立zk session的watcher
	 * @author Administrator
	 *
	 */
	private class ZooKeeperWatcher implements Watcher {
		@Override
		public void process(WatchedEvent event) {
			System.out.println("Receive watched event: " + event.getState());
			if(KeeperState.SyncConnected == event.getState()) {
				connectedSemaphore.countDown();
			}
		}
	}

	/**
	 * 封装单例的静态内部类
	 * @author Administrator
	 *
	 */
	private static class Singleton {

		private static ZooKeeperConnectSession instance;

		static {
			instance = new ZooKeeperConnectSession();
		}

		public static ZooKeeperConnectSession getInstance() {
			return instance;
		}

	}

	/**
	 * 获取单例
	 * @return
	 */
	public static ZooKeeperConnectSession getInstance() {
		return Singleton.getInstance();
	}
}
