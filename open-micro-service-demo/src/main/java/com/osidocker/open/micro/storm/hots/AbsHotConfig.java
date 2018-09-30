/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.storm.hots;

import java.util.List;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 17:35 2018/9/8
 * @修改说明：
 * @修改日期： 17:35 2018/9/8
 * @版本号： V1.0.0
 */
public abstract class AbsHotConfig {
    /**
     * 获取热门数据TopX条
     * @return
     */
    public abstract int getHotTopX();

    /**
     * 突发热点数据 是热门数据的 Multiple 倍,则认为是突然数据
     * @return
     */
    public abstract int getHotMultiple();

    /**
     * 突发热点数据 nginx 请求地址
     * @return
     */
    public abstract String getHotNginxUri();

    /**
     * 缓存热点数据uri地址
     * @return
     */
    public abstract String getCacheUri();

    /**
     * nginx 集群服务List
     * @return
     */
    public abstract List<String> getNginxClusters();
}
