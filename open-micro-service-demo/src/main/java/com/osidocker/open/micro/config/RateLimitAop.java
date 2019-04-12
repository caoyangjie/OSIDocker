package com.osidocker.open.micro.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import com.osidocker.open.micro.annotation.RateLimitAspect;
import com.osidocker.open.micro.utils.JsonTools;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope
@Aspect
public class RateLimitAop {

    @Autowired
    private HttpServletResponse response;

    /**
     * 比如说，我这里设置"并发数"为5
     */
    private ConcurrentHashMap<String, RateLimiter> map = new ConcurrentHashMap<>();

    private RateLimiter rateLimiter;

    @Pointcut("@annotation(com.osidocker.open.micro.annotation.RateLimitAspect)")
    public void serviceLimit() {

    }

    @Around("serviceLimit()")
    public Object around(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        Object obj = null;
        //获取拦截的方法名
        Signature sig = joinPoint.getSignature();
        //获取拦截的方法名
        MethodSignature msig = (MethodSignature) sig;
        //返回被织入增加处理目标对象
        Object target = joinPoint.getTarget();
        //为了获取注解信息
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        //获取注解信息
        RateLimitAspect annotation = currentMethod.getAnnotation(RateLimitAspect.class);
        //获取注解每秒加入桶中的token
        double limitNum = annotation.limitNum();
        // 注解所在方法名区分不同的限流策略
        String functionName = msig.getName();

        //获取rateLimiter
        if(map.containsKey(functionName)){
            rateLimiter = map.get(functionName);
        }else {
            map.put(functionName, RateLimiter.create(limitNum));
            rateLimiter = map.get(functionName);
        }
        Boolean flag = rateLimiter.tryAcquire();
        try {
            if (flag) {
                obj = joinPoint.proceed();
            } else {
                String result = JsonTools.toJson("");
                output(response, result);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("flag=" + flag + ",obj=" + obj);
        return obj;
    }

    public void output(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(msg.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }
}
