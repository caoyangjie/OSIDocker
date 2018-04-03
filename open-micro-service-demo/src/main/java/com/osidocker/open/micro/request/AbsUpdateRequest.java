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
 * @创建日期： 创建于13:59 2018/3/8
 * @修改说明：
 * @修改日期： 修改于13:59 2018/3/8
 * @版本号： V1.0.0
 */
public abstract class AbsUpdateRequest<ResponseEntity> implements IRequest{
    /**
     * 商品库存重新加载服务对象
     */
    private IDataOperateService<ResponseEntity> viewObjectService;
    /**
     * 请求更新对象实体
     */
    private ResponseEntity resultEntity;

    public AbsUpdateRequest(ResponseEntity resultEntity, IDataOperateService<ResponseEntity> viewObjectService) {
        this.viewObjectService = viewObjectService;
        this.resultEntity = resultEntity;
    }

    @Override
    public void handler() {
        //删除redis上的缓存
        viewObjectService.removeResponseEntityCache(resultEntity);
        //修改数据库中的库存
        viewObjectService.updateResponseEntity(resultEntity);
    }

    @Override
    public boolean isForceRefresh() {
        return false;
    }
}
