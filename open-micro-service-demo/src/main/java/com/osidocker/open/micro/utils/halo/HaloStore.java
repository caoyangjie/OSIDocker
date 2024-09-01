package com.osidocker.open.micro.utils.halo;

import cn.hutool.core.text.StrFormatter;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class HaloStore {
    public String put(String uri, TokenInfo server, Supplier<String> postData, Function<String,String> callback) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // 创建HttpPut请求
            HttpPut put = new HttpPut(uri);
            // 设置请求头
            put.setHeader("Content-Type", "application/json; charset=UTF-8");
            put.setHeader("X-Xsrf-Token", server.getToken());
            put.setHeader("Cookie", server.getCookie());
            put.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            StringEntity requestJson = new StringEntity(postData.get(),"UTF-8");
            put.setEntity(requestJson);
            // 发送请求
            try (CloseableHttpResponse response = client.execute(put)) {
                // 获取响应状态
                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
                if( response.getStatusLine().getStatusCode()>=299 ) {
                    System.err.println(StrFormatter.format("发送请求{}响应码异常", response.getStatusLine().getStatusCode()));
                    return callback.apply("");
                }
                // 获取响应内容
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Response Body: " + responseBody);
                return callback.apply(responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String post(String uri, TokenInfo server, Supplier<String> postData, Function<String,String> callback) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // 创建HttpPost请求
            HttpPost post = new HttpPost(uri);
            // 设置请求头
            post.setHeader("Content-Type", "application/json; charset=UTF-8");
            post.setHeader("X-Xsrf-Token", server.getToken());
            post.setHeader("Cookie", server.getCookie());
            post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            StringEntity requestJson = new StringEntity(postData.get(),"UTF-8");
            post.setEntity(requestJson);
            // 发送请求
            try (CloseableHttpResponse response = client.execute(post)) {
                // 获取响应状态
                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
                if( response.getStatusLine().getStatusCode()>=299 ) {
                    System.err.println(StrFormatter.format("发送请求{}响应码异常", response.getStatusLine().getStatusCode()));
                    return callback.apply("");
                }
                // 获取响应内容
                String responseBody = EntityUtils.toString(response.getEntity());
                return callback.apply(responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public <T> T list(String url, TokenInfo server, Function<String, T> callback) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建 GET 请求
            HttpGet getRequest = new HttpGet(url);
            getRequest.setHeader("X-Xsrf-Token", server.getToken());
            getRequest.setHeader("Cookie", server.getCookie());
            getRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            // 执行请求
            HttpResponse response = httpClient.execute(getRequest);
            // 处理响应
            if( response.getStatusLine().getStatusCode()>=299 ) {
                System.err.println(StrFormatter.format("发送请求{}响应码异常", response.getStatusLine().getStatusCode()));
                return callback.apply("");
            }
            String responseBody = EntityUtils.toString(response.getEntity());
            return callback.apply(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
