/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.controllers;

import com.osidocker.open.micro.pay.exceptions.PayException;
import com.osidocker.open.micro.pay.vos.APIResponse;
import com.osidocker.open.micro.pay.vos.TransDataBaseVo;
import com.osidocker.open.micro.spring.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：所有的Contoller类的基类
 * @类修改者： 曹杨杰
 * @author caoyangjie
 * @创建日期： 创建于15:02 2017/3/11
 * @修改说明：
 * @修改日期： 修改于15:02 2017/3/11
 * @版本号： V1.0.0
 */
public class CoreController {
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String UTF_8 = "UTF-8";
    protected Logger logger = LoggerFactory.getLogger(this.getClass());


    public APIResponse getTryCatchExceptions(Exception exp) {
        return APIResponse.generator("999999",exp.getMessage());
    }

    public APIResponse getDefaultApiRosponse()
    {
        return APIResponse.generator("000000","操作成功!");
    }

    /**
     *  根据请求参数获取对应的服务实现对象
     * @param serviceName       服务名称
     * @param serviceClazz      服务接口类
     * @param version           服务版本
     * @param <T>               服务的实现对象
     * @return                  返回服务的实现对象
     */
    public <T> T getServiceBy(String serviceName,Class serviceClazz,String version)
    {
        // 这里通过自定义根据版本号获取Service服务
        T t = (T) SpringContext.getApplicationContext().getBean(serviceName);
        return t;
    }

    public String invokeISOtoUTF8(String values){
        String returnStr = null;
        try {
            returnStr = new String(values.getBytes(ISO_8859_1), UTF_8);
        } catch (UnsupportedEncodingException e) {
        }
        return returnStr;
    }

    public String version()
    {
        return "V1.0.0";
    }
}
