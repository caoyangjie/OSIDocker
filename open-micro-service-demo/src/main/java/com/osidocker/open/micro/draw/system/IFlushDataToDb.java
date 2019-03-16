package com.osidocker.open.micro.draw.system;

import com.osidocker.open.micro.vo.CoreException;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月13日 16:11
 * @Copyright: © 麓山云
 */
public interface IFlushDataToDb<R> {

    /**
     * 请求将数据flush到数据库中
     * @param r     请求数据上下文
     * @return
     */
     boolean write(R r) throws CoreException;

}
