package com.osidocker.open.micro.mapper;

import com.osidocker.open.micro.entity.ProductInventory;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存的操作mapper
 *
 * @author Administrator
 * @creato 2017-12-02 15:51
 */
public interface ProductInventoryMapper {

    void updateProductInventory(ProductInventory productInventory);

    ProductInventory findProductInventory(@Param("productId") Long productId);
}
