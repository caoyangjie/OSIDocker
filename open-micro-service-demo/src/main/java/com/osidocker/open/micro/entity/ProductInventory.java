package com.osidocker.open.micro.entity;

/**
 * 库存实体类对象
 *
 * @author Administrator
 * @creato 2017-12-02 15:48
 */
public class ProductInventory {
    //商品id
    private Long productId;
    //商品库存数量
    private Long inventoryCnt;

    public ProductInventory() {
    }

    public ProductInventory(Long productId, Long inventoryCnt) {
        this.productId = productId;
        this.inventoryCnt = inventoryCnt;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getInventoryCnt() {
        return inventoryCnt;
    }

    public void setInventoryCnt(Long inventoryCnt) {
        this.inventoryCnt = inventoryCnt;
    }
}
