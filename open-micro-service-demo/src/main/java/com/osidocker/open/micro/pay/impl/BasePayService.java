/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.impl;

import com.osidocker.open.micro.pay.vos.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author caoyangjie
 * @创建日期： 创建于18:22 2017/7/7
 * @修改说明：
 * @修改日期： 修改于18:22 2017/7/7
 * @版本号： V1.0.0
 */
public class BasePayService{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, Object> rspData;

    public final ApiResponse buildSuccMap(Map<String,Object> map)
    {
        return buildSuccess().initData(map);
    }

    public final ApiResponse buildSuccess()
    {
        return ApiResponse.generator("000000","SUCCESS_CODE");
    }

    public final ApiResponse buildFail(String apiMessage)
    {
        return ApiResponse.generator("999999",apiMessage);
    }


    public String serviceName() {
        return "pay";
    }
}
