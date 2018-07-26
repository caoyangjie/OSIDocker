/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.service;

import com.osidocker.open.micro.service.exceptions.SmsSendException;
import com.osidocker.open.micro.vo.Response;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 18:20 2018/7/25
 * @修改说明：
 * @修改日期： 18:20 2018/7/25
 * @版本号： V1.0.0
 */
public interface SmsSendService<Request,Response> {

    /**
     * 短消息发送服务类
     * @param request   发送请求数据对象
     * @return  请求发送后返回对象
     */
    Response sendMessage(Request request) throws SmsSendException;
}
