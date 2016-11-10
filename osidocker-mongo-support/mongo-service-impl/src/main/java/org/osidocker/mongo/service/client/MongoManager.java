package org.osidocker.mongo.service.client;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoDatabase;

/**
 * 
 * mongodb管理
 * 
 * <功能详细描述>
 * 
 * @author  zhouyinjun
 * @version  [版本号, 2016-6-1]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Configuration
@PropertySource(value = "classpath:/mongo.properties")
public class MongoManager implements InitializingBean {
	@Value("${mongo.datasource.connectTimeOut}")
    private int connectTimeOut = 5000;
     
	@Value("${mongo.datasource.socketTimeOut}")
    private int socketTimeOut = 5000;
     
	@Value("${mongo.datasource.maxConnections}")
    private int maxConnections = 100;
     
	@Value("${mongo.datasource.maxWaitTime}")
    private int maxWaitTime = 1000 * 60 * 2;
     
	@Value("${mongo.datasource.blockSize}")
    private int blockSize = 100;
     
	@Value("${mongo.datasource.host}")
    private String host = "10.0.2.82";
     
	@Value("${mongo.datasource.port}")
    private int port = 27017;
     
	@Value("${mongo.datasource.dbName}")
    private String dbName = "test";

	private MongoClient client;
	

    private  MongoClientOptions getConfOptions() {
    	return new MongoClientOptions.Builder().socketKeepAlive(true) // 是否保持长链接
			.connectTimeout(connectTimeOut) // 链接超时时间
			.socketTimeout(socketTimeOut) // read数据超时时间
			.readPreference(ReadPreference.primary()) // 最近优先策略
			.connectionsPerHost(maxConnections) // 每个地址最大请求数
			.maxWaitTime(maxWaitTime) // 长链接的最大等待时间
			.threadsAllowedToBlockForConnectionMultiplier(blockSize) // 一个socket最大的等待请求数
			.writeConcern(WriteConcern.NORMAL).build();
    }
      
    private void init()
    {
    	ServerAddress serverAddress = new ServerAddress(host,port);
		MongoClientOptions options =getConfOptions();
		client = new MongoClient(serverAddress, options);
    }
	  
    /**
     *  获取mongodb
     * <功能详细描述>
     * @param dbName
     * @return [参数说明]
     * 
     * @return MongoDatabase [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
	public  MongoDatabase getDB(String dbName) 
	{  
	    MongoDatabase db=client.getDatabase(dbName);
	    return db;
	}
	  
	public MongoDatabase getDefaultDB()
	{
		return  getDB(dbName);
	}
	
	public int getSocketTimeOut() {
		return socketTimeOut;
	}
	
	public void setSocketTimeOut(int socketTimeOut) {
		this.socketTimeOut = socketTimeOut;
	}
	
	@Override
	  public void afterPropertiesSet() throws Exception {
			init();
	}
	
	
	public int getConnectTimeOut() {
		return connectTimeOut;
	}
	
	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}
	
	public int getMaxConnections() {
		return maxConnections;
	}
	
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}
	
	public int getMaxWaitTime() {
		return maxWaitTime;
	}
	
	public void setMaxWaitTime(int maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}
	
	public int getBlockSize() {
		return blockSize;
	}
	
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getDbName() {
		return dbName;
	}
	
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	public MongoClient getClient() {
		return client;
	}
	
	public void setClient(MongoClient client) {
		this.client = client;
	}
   
}
