/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.exceptions;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于16:38 2017/12/13
 * @修改说明：
 * @修改日期： 修改于16:38 2017/12/13
 * @版本号： V1.0.0
 */
public class PayException extends RuntimeException{

    public PayException(String message) {
        super(message);
    }
}
