package com.osidocker.open.micro.request.productInventory;

import com.osidocker.open.micro.entity.ProductInventory;
import com.osidocker.open.micro.request.IRequest;
import com.osidocker.open.micro.service.IProductInventoryService;

/**
 * 商品缓存重新加载服务类
 *
 * @author Administrator
 * @creato 2017-12-02 16:09
 */
public class ProductInventoryCacheReloadRequest implements IRequest {

    //商品库存对象
    private Long productId;
    //商品库存重新加载服务对象
    private IProductInventoryService productInventoryService;
    //是否强制刷新缓存标识
    private boolean forceRefresh;

    public ProductInventoryCacheReloadRequest(Long productId, IProductInventoryService productInventoryService, boolean forceRefresh) {
        this.productId = productId;
        this.productInventoryService = productInventoryService;
        this.forceRefresh = forceRefresh;
    }

    @Override
    public void handler() {
        ProductInventory productInventoryInstance = productInventoryService.findInventory(productId);
        productInventoryService.setProductInventory(productInventoryInstance);
    }

    @Override
    public String getHashKey() {
        return String.valueOf(productId);
    }

    public boolean isForceRefresh() {
        return forceRefresh;
    }
}
