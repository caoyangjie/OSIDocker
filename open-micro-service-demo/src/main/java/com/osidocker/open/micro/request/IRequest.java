package com.osidocker.open.micro.request;

/**
 * 接受前端请求对象类
 *
 * @author Administrator
 * @creato 2017-12-02 14:46
 */
public interface IRequest {

    void handler();

    String getHashKey();

    boolean isForceRefresh();
}
