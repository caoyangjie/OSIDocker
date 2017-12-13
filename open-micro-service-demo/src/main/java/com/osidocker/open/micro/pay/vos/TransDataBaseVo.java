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

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @创建日期： 创建于10:19 2017/3/11
 * @修改说明：
 * @修改日期： 修改于10:19 2017/3/11
 * @版本号： V1.0.0
 */
public abstract class TransDataBaseVo implements Serializable {
    /**
     * 用来校验传入参数是否符合接口要求
     * @return true|false
     */
    public abstract boolean checkTransData();
    /**
     * 定义请求接口实体类版本号
     */
    public abstract String version();
}
