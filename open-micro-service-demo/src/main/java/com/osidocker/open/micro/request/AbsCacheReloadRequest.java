/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.request;

import com.osidocker.open.micro.service.IDataOperateService;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于12:00 2018/3/8
 * @修改说明：
 * @修改日期： 修改于12:00 2018/3/8
 * @版本号： V1.0.0
 */
public abstract class AbsCacheReloadRequest<ResponseEntity,RequestEntity> implements IRequest{
    /**
     * 对象Id
     */
    private Long id;
    /**
     * 商品库存重新加载服务对象
     */
    private IDataOperateService<ResponseEntity> viewObjectService;
    /**
     * 是否强制刷新缓存标识
     */
    private boolean forceRefresh = false;
    /**
     * 请求更新对象实体
     */
    private RequestEntity requestEntity;

    public AbsCacheReloadRequest(Long id, boolean forceRefresh, RequestEntity requestEntity, IDataOperateService<ResponseEntity> viewObjectService) {
        this.id = id;
        this.viewObjectService = viewObjectService;
        this.forceRefresh = forceRefresh;
        this.requestEntity = requestEntity;
    }

    @Override
    public void handler() {
        ResponseEntity responseEntity = viewObjectService.findResponseEntity(this.id, requestEntity);
        viewObjectService.setResponseEntity(responseEntity);
    }

    @Override
    public boolean isForceRefresh() {
        return forceRefresh;
    }
}
