/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.pay.vos;

import com.osidocker.open.micro.utils.StringUtil;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @创建日期： 创建于 2017/8/9 17:28
 * @修改说明：
 * @修改日期： 修改于 2017/8/9 17:28
 * @版本号： V1.0.0
 */
public class QueryOrder extends TransDataBaseVo {
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    @Override
    public boolean checkTransData() {
        if(StringUtil.isEmpty(orderId)){
            return false;
        }
        return true;
    }

    @Override
    public String version() {
        return "V1.0.0";
    }
}
