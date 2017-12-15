/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.vos;

import java.io.Serializable;
import java.util.Map;
/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @创建日期： 创建于11:01 2017/3/17
 * @修改说明：
 * @修改日期： 修改于11:01 2017/3/17
 * @版本号： V1.0.0
 */
public class ApiResponse<T> implements Serializable{
    private String apiCode;
    private String apiMessage;
    private Map<String,Object> rspData;
    private T rspVo;

    public static ApiResponse generator(String apiCode, String apiMessage)
    {
        ApiResponse rsp = new ApiResponse();
        rsp.apiCode = apiCode;
        rsp.apiMessage = apiMessage;
        return rsp;
    }

    public ApiResponse initData(Map<String,Object> rspData)
    {
        this.rspData = rspData;
        return this;
    }

    public ApiResponse initData(T rspData) {
        this.rspVo = rspData;
        return this;
    }

    public String getApiCode() {
        return apiCode;
    }

    public String getApiMessage() {
        return apiMessage;
    }

    public Map<String, Object> getRspData() {
        return rspData;
    }

    public T getRspVo() {
        return rspVo;
    }
}
