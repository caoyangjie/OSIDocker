/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.service.exceptions;

import com.osidocker.open.micro.vo.CoreException;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 18:27 2018/7/25
 * @修改说明：
 * @修改日期： 18:27 2018/7/25
 * @版本号： V1.0.0
 */
public class SmsSendException extends CoreException {

    public SmsSendException(int code,Object message){
        this.setCode(code);
        this.setData(message);
    }

}
