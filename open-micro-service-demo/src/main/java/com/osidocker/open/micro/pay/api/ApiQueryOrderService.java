/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.pay.api;

import com.osidocker.open.micro.pay.vos.ApiResponse;
import com.osidocker.open.micro.pay.vos.QueryOrder;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @author caoyangjie
 * @创建日期： 创建于 2017/8/9 18:23
 * @修改说明：
 * @修改日期： 修改于 2017/8/9 18:23
 * @版本号： V1.0.0
 */
public interface ApiQueryOrderService {
    public ApiResponse getQueryOrder(QueryOrder queryOrder);

    public ApiResponse getQueryOrderSuccess(QueryOrder queryOrder);
}
