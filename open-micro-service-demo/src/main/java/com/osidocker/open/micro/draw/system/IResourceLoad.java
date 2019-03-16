package com.osidocker.open.micro.draw.system;

import com.osidocker.open.micro.vo.CoreException;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月12日 8:42
 * @Copyright: © 麓山云
 */
public interface IResourceLoad<RequestContent,ResponseContent> {

    /**
     * 加载资源
     * @param ctx 加载资源的请求数据上下文对象
     * @return
     * @throws CoreException 外抛异常
     */
    ResponseContent getResource(RequestContent ctx) throws CoreException;

    /**
     * //TODO 是否可以强制刷新
     * 是否进行强制刷新操作
     * @return
     */
    boolean isForceRefresh();

    /**
     * 释放资源
     */
    void close();
}
