package com.osidocker.open.micro.draw.system;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月28日 12:10
 * @Copyright: © Caoyj
 */
public interface IMessageQueue<T> {

    void push(T t);
}
