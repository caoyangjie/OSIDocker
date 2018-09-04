/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.service;

import com.alibaba.fastjson.JSONObject;
import com.osidocker.open.micro.pay.vos.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 14:00 2018/9/4
 * @修改说明：
 * @修改日期： 14:00 2018/9/4
 * @版本号： V1.0.0
 */
public abstract class AbsGenerateService  implements GenerateService<JSONObject,ApiResponse>{
    public static final String FLOW_NO = "flowNo";
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     *
     * @param jsonObject
     * @return
     */
    protected String[] getFlowNos(JSONObject jsonObject) {
        return jsonObject.getString(FLOW_NO).split("#");
    }

    public static ThreadPoolExecutor getThreadPoolTaskExecutor(){
        return new ThreadPoolExecutor(3, 4, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
    }
}
