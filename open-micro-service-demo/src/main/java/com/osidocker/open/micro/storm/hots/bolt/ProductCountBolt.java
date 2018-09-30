package com.osidocker.open.micro.storm.hots.bolt;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSONArray;
import com.osidocker.open.micro.prewarm.CachePrewarmThread;
import com.osidocker.open.micro.storm.hots.AbsHotConfig;
import com.osidocker.open.micro.utils.HttpClientUtils;
import com.osidocker.open.micro.utils.JsonTools;
import com.osidocker.open.micro.zk.ZooKeeperHotsSession;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.trident.util.LRUMap;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.Utils;

/**
 * 商品访问次数统计bolt
 * @author Administrator
 *
 */
public class ProductCountBolt extends BaseRichBolt {

	private static final long serialVersionUID = -8761807561458126413L;

	private LRUMap<Long, Long> productCountMap = new LRUMap<Long, Long>(1000);
    private ZooKeeperHotsSession zkSession;
    private int taskid;
    private AbsHotConfig config;

    /**
     * 构建统计bolt对象
     * @param config    配置信息
     */
    public ProductCountBolt(AbsHotConfig config){
        this.config = config;
    }

	@Override
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.zkSession = ZooKeeperHotsSession.getInstance();
        this.taskid = context.getThisTaskId();
		new Thread(new ProductCountThread()).start();
		new Thread(new HotProductThread()).start();
		initTaskId(this.taskid);
	}

    private void initTaskId(int taskid) {
        // ProductCountBolt所有的task启动的时候， 都会将自己的taskid写到同一个node的值中
        // 格式就是逗号分隔，拼接成一个列表
        // 111,211,355

        zkSession.acquireDistributedLock();

        String taskidList = zkSession.getNodeData();
        if(!"".equals(taskidList)) {
            taskidList += "," + taskid;
        } else {
            taskidList += taskid;
        }

        zkSession.setNodeData(CachePrewarmThread.HOT_TASK_ID_LIST, taskidList);

        zkSession.releaseDistributedLock();
    }


    private class HotProductThread implements Runnable {
        List<Long> hotProductLongList = new ArrayList<Long>();
        List<Map.Entry<Long, Long>> hotProductList = new ArrayList<Map.Entry<Long, Long>>();

        @Override
        public void run() {

            while(true) {
                hotProductLongList.clear();
                hotProductList.clear();

                hotProductList = changeCountMapToList().collect(Collectors.toList());

                Long countAvg = getCountAverage();
                hotProductList.stream().limit(Long.valueOf(""+(hotProductList.size()*0.95))).forEach( hotProduct->{
                    if( hotProduct.getValue() > config.getHotMultiple() * countAvg ){
                        //将缓存热点反向推送到流量分发的nginx中
                        doDistributeNginx(hotProduct);
                        //将缓存热点，那个商品对应的完整的缓存数据，发送请求到缓存服务去获取，反向推送到所有的后端应用nginx服务器上去
                        doDispatchDataToCache(hotProduct);
                    }
                });
                Utils.sleep(6000);
            }
        }

        /**
         * 将热点数据获取后推送到所有的nginx服务器上去
         * @param hotProduct
         */
        private void doDispatchDataToCache(Map.Entry<Long,Long> hotProduct) {
            String productInfo = HttpClientUtils.sendGetRequest(config.getCacheUri()+"?productId="+hotProduct.getKey());
            config.getNginxClusters().stream().parallel().forEach(uri->{
                HttpClientUtils.sendPostRequest(uri,JsonTools.jsonStr2Map(productInfo));
            });
        }

        /**
         * 将获取的突发热点数据推送的nginx上进行突发流量处理逻辑
         * @param hotProduct
         */
        private void doDistributeNginx(Map.Entry<Long,Long> hotProduct) {
            HttpClientUtils.sendGetRequest(config.getHotNginxUri()+"?productId="+hotProduct.getKey());
        }

        /**
         * 获取所有访问数据的95%的数据的平均访问数(倒序是为了排除掉在最前面的非常高点击的数据)
         * @return
         */
        private Long getCountAverage() {
            hotProductLongList = productCountMap.values().stream().sorted(new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {
                    //倒序排列
                    return o2.compareTo(o1);
                }
            }).collect(Collectors.toList());
            int calculateCount = (int)Math.floor(hotProductLongList.size() * 0.95);
            Double avgCount = hotProductLongList.stream().limit(calculateCount).mapToLong(v->v).average().getAsDouble();
            return Long.getLong(avgCount.toString());
        }

    }

	private class ProductCountThread implements Runnable {

	    @Override
		public void run() {
			List<Map.Entry<Long, Long>> topnProductList = new ArrayList<Map.Entry<Long, Long>>();

			while(true) {
				topnProductList.clear();
				topnProductList = changeCountMapToList().limit(config.getHotTopX()).collect(Collectors.toList());
				zkSession.setNodeData(CachePrewarmThread.TASK_HOT_LIST+taskid,JSONArray.toJSONString(topnProductList));
				Utils.sleep(60000);
			}
		}

	}

    private Stream<Map.Entry<Long, Long>> changeCountMapToList() {
        return productCountMap.entrySet().stream().sorted(new Comparator<Map.Entry<Long, Long>>() {
            @Override
            public int compare(Map.Entry<Long, Long> o1, Map.Entry<Long, Long> o2) {
                return o1.getValue()>o2.getValue()?1:-1;
            }
        });
    }

    @Override
	public void execute(Tuple tuple) {
		Long productId = tuple.getLongByField("productId"); 
		
		Long count = productCountMap.get(productId);
		if(count == null) {
			count = 0L;
		}
		count++;
		
		productCountMap.put(productId, count);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}

}
