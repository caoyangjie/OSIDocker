package com.osidocker.open.micro;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;

/**
 * HttpClient工具类
 * @author lixuerui
 *
 */
@SuppressWarnings("deprecation")
public class HttpClientRequests {

    private static final String SESSION = "90727d96-29fd-4956-a3f5-108cae662f91";

    /**
	 * 发送post请求
	 * @param url URL
     * @param sets 请求参数
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "resource" })
	public static String sendPostRequest(String url, Set<String> sets,JSONObject json){
		HttpClient httpClient = null;  
        HttpPost httpPost = null;  
        String result = null;  
        
        try{
			SSLContext sslContext = SSLContext.getInstance("TLS");
			// 初始化SSL上下文
			sslContext.init(null, new TrustManager[] { tm }, null);
			// SSL套接字连接工厂,NoopHostnameVerifier为信任所有服务器
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setMaxConnTotal(50)
					.setMaxConnPerRoute(50).setDefaultRequestConfig(RequestConfig.custom()
							.setConnectionRequestTimeout(60000).setConnectTimeout(60000).setSocketTimeout(60000).build())
					.build();
            httpPost = new HttpPost(url);
            JSONArray array = new JSONArray();
            sets.forEach(val->{
                array.add(val);
            });
            StringEntity s;
            if( json!=null ){
                s = new StringEntity(json.toString());
            }else{
                s =  new StringEntity(array.toJSONString());
            }
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");//发送json数据需要设置contentType
            httpPost.setHeader("SESSION",SESSION);
            httpPost.setHeader("Host","www.szeiv.com");
            httpPost.setHeader("Origin","https://www.szeiv.com");
            httpPost.setHeader("Referer","https://www.szeiv.com/role.html");
            httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
            httpPost.setHeader("Content-Type","application/json;charset=UTF-8");
            httpPost.setHeader("Connection","keep-alive");
            httpPost.setEntity(s);
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity, "utf-8");    
                }  
            }  
        } catch(Exception ex){  
            ex.printStackTrace();  
        } finally {
        	
        }
        
        return result;  
    }


    /**
     * 发送post请求
     * @param url URL
     * @param map 参数Map
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked", "resource" })
    public static String sendPostRequest(String url, Map<String,Object> map){
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;

        try{
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // 初始化SSL上下文
            sslContext.init(null, new TrustManager[] { tm }, null);
            // SSL套接字连接工厂,NoopHostnameVerifier为信任所有服务器
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setMaxConnTotal(50)
                    .setMaxConnPerRoute(50).setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectionRequestTimeout(60000).setConnectTimeout(60000).setSocketTimeout(60000).build())
                    .build();
            httpPost = new HttpPost(url);

            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while(iterator.hasNext()){
                Entry<String,String> elem = (Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if(list.size() > 0){
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
                httpPost.setEntity(entity);
                httpPost.setHeader("SESSION",SESSION);
                httpPost.setHeader("Host","www.szeiv.com");
                httpPost.setHeader("Origin","https://www.szeiv.com");
                httpPost.setHeader("Referer","https://www.szeiv.com/admin.html");
                httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
                httpPost.setHeader("Connection","keep-alive");
            }

            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity, "utf-8");
                }
            }
        } catch(Exception ex){
            ex.printStackTrace();
        } finally {

        }

        return result;
    }

    private static X509TrustManager tm = new X509TrustManager() {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};
}