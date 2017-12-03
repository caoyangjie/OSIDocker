package com.osidocker.open.micro.request.productInventory;

import com.osidocker.open.micro.entity.ProductInventory;
import com.osidocker.open.micro.request.IRequest;
import com.osidocker.open.micro.service.IProductInventoryService;

/**
 * 数据更新请求处理类
 *
 * cache aside pattern
 *  (1) 删除缓存
 *  (2) 更新数据库
 *
 * @author Administrator
 * @creato 2017-12-02 15:40
 */
public class ProductInventoryUpdateRequest implements IRequest {

    //商品库存
    private ProductInventory productInventory;

    //操作商品库存的服务对象
    private IProductInventoryService productInventoryService;

    public ProductInventoryUpdateRequest(ProductInventory productInventory, IProductInventoryService productInventoryService) {
        this.productInventory = productInventory;
        this.productInventoryService = productInventoryService;
    }

    @Override
    public void handler() {
        //删除redis上的缓存
        productInventoryService.removeProductInventoryCache(productInventory);
        //修改数据库中的库存
        productInventoryService.updateProductInventory(productInventory);
    }

    @Override
    public String getHashKey() {
        return String.valueOf(productInventory.getProductId());
    }

    @Override
    public boolean isForceRefresh() {
        return false;
    }
}
