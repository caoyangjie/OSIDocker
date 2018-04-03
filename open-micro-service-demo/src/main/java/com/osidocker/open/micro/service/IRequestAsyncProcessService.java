package com.osidocker.open.micro.service;

import com.osidocker.open.micro.request.IRequest;

/**
 * 请求异步处理的service实现
 *
 * @author Administrator
 * @creato 2017-12-02 16:39
 */
public interface IRequestAsyncProcessService {

    void process(IRequest request);
}
