/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.api;

import com.osidocker.open.micro.pay.exceptions.PayException;
import com.osidocker.open.micro.pay.vos.APIResponse;
import com.osidocker.open.micro.pay.vos.TransOrderBase;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @创建日期： 创建于9:40 2017/7/7
 * @修改说明：
 * @修改日期： 修改于9:40 2017/7/7
 * @版本号： V1.0.0
 */
public interface ApiPayGateway {

    public String getPayName();

    public APIResponse execute(TransOrderBase orderInfo) throws PayException;

    public Map<String,Object> payAgian(String orderNo) throws PayException;

    public  Map<String, String> queryOrder(String orderNo) throws PayException;

    public boolean noticeOrder(Map<String, String> context) throws PayException;

    public  Map<String, String> payResultNotice(HttpServletRequest request) throws PayException;
}
