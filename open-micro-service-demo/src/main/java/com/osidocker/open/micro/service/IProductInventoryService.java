package com.osidocker.open.micro.service;

import com.osidocker.open.micro.entity.ProductInventory;

/**
 * 商品库存服务类对象
 *
 * @author Administrator
 * @creato 2017-12-02 15:55
 */
public interface IProductInventoryService {

    void updateProductInventory(ProductInventory productInventory);

    void removeProductInventoryCache(ProductInventory productInventory);

    ProductInventory findInventory(Long productId);

    void setProductInventory(ProductInventory productInventory);

    ProductInventory getProductInventoryCache(Long productId);
}
