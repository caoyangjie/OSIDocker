package com.osidocker.open.micro.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.osidocker.open.micro.entity.ProductInventory;
import com.osidocker.open.micro.model.ReceiveInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存的操作mapper
 *
 * @author Administrator
 * @creato 2017-12-02 15:51
 */
public interface ProductInventoryMapper extends BaseMapper<ReceiveInfo> {

    void updateProductInventory(ProductInventory productInventory);

    ProductInventory findProductInventory(@Param("productId") Long productId);
}
