/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.impl;

import com.osidocker.open.micro.pay.vos.APIResponse;

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

    private Map<String, Object> rspData;

    public final APIResponse buildSuccMap(Map<String,Object> map)
    {
        return buildSuccess().initData(map);
    }

    public final APIResponse buildSuccess()
    {
        return APIResponse.generator("000000","SUCCESS");
    }

    public final APIResponse buildFail(String apiMessage)
    {
        return APIResponse.generator("999999",apiMessage);
    }


    public String serviceName() {
        return "pay";
    }
}
