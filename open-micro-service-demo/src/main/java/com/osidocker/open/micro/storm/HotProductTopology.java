package com.osidocker.open.micro.storm;

import com.osidocker.open.micro.storm.hots.AbsHotConfig;
import com.osidocker.open.micro.storm.hots.bolt.LogParseBolt;
import com.osidocker.open.micro.storm.hots.bolt.ProductCountBolt;
import com.osidocker.open.micro.storm.hots.spout.AccessLogKafkaSpout;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 热数据统计拓扑
 * @author Administrator
 *
 */
public class HotProductTopology {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		int topN = 10;
		int hotMultiple = 10;
		String nginxUri = "http://192.168.188.221/hots";
		builder.setSpout("AccessLogKafkaSpout", new AccessLogKafkaSpout(), 1);
		builder.setBolt("LogParseBolt", new LogParseBolt(), 5)
				.setNumTasks(5)
				.shuffleGrouping("AccessLogKafkaSpout");  
		builder.setBolt("ProductCountBolt", new ProductCountBolt(new HotProductConfig()), 5)
				.setNumTasks(10)
				.fieldsGrouping("LogParseBolt", new Fields("productId"));  
		
		Config config = new Config();
		
		if(args != null && args.length > 1) {
			config.setNumWorkers(3);  
			try {
				StormSubmitter.submitTopology(args[0], config, builder.createTopology());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("HotProductTopology", config, builder.createTopology());  
			Utils.sleep(30000); 
			cluster.shutdown();
		}
	}

	public static class HotProductConfig extends AbsHotConfig{

		@Override
		public int getHotTopX() {
			return 10;
		}

		@Override
		public int getHotMultiple() {
			return 20;
		}

		@Override
		public String getHotNginxUri() {
			return "http://192.168.188.177/hot";
		}

		@Override
		public String getCacheUri() {
			return "http://192.168.188.177/cacheProduct";
		}

		@Override
		public List<String> getNginxClusters() {
			return Stream.of("http://192.168.188.177/hotCache","http://192.168.188.212/hotCache")
					.collect(Collectors.toList());
		}
	}
}
