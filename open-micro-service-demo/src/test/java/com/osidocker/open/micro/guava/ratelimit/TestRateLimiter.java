package com.osidocker.open.micro.guava.ratelimit;

import com.google.common.util.concurrent.RateLimiter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestRateLimiter {
  
    public static void main(String[] args) {  
        //0.5代表一秒最多多少个  
        RateLimiter rateLimiter = RateLimiter.create(5);
        List<Runnable> tasks = new ArrayList<Runnable>();
        for (int i = 0; i < 50; i++) {
            tasks.add(new UserRequest(i));  
        }  
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (Runnable runnable : tasks) {  
            System.out.println("等待时间：" + rateLimiter.acquire());  
            threadPool.execute(runnable);  
        }  
    }  
  
    private static class UserRequest implements Runnable {  
        private int id;  
  
        public UserRequest(int id) {  
            this.id = id;  
        }  
  
        public void run() {  
            System.out.println(id);  
        }  
    }  
  
}  