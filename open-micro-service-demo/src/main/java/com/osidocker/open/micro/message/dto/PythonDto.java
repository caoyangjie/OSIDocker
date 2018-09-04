/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.message.dto;

import com.alibaba.fastjson.JSONObject;
import com.osidocker.open.micro.vo.BaseMessage;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 9:11 2018/8/31
 * @修改说明：
 * @修改日期： 9:11 2018/8/31
 * @版本号： V1.0.0
 */
public class PythonDto extends BaseMessage<JSONObject> {

    public String getFlowNo() {
        return getMessage().getString("flowNo");
    }

    public String getResult() {
        return getMessage().getString("result");
    }

    public String getCode() {
        return getMessage().getString("code");
    }
}
