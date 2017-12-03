package com.osidocker.open.micro.service.impl;

import com.osidocker.open.micro.entity.ProductInventory;
import com.osidocker.open.micro.mapper.ProductInventoryMapper;
import com.osidocker.open.micro.service.IProductInventoryService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 商品库存服务实现类对象
 *
 * @author Administrator
 * @creato 2017-12-02 15:56
 */
@Service("productInventoryServiceImpl")
public class ProductInventoryServiceImpl implements IProductInventoryService {

    @Resource
    private ProductInventoryMapper productInventoryMapper;

    @Resource
    @Qualifier("redisTemplate")
    private RedisTemplate redisDao;

    @Override
    public void updateProductInventory(ProductInventory productInventory) {
        productInventoryMapper.updateProductInventory(productInventory);
    }

    @Override
    public void removeProductInventoryCache(ProductInventory productInventory) {
        redisDao.delete("productInventory:cnt:"+productInventory.getProductId());
    }

    @Override
    public ProductInventory findInventory(Long productId) {
        return productInventoryMapper.findProductInventory(productId);
    }

    @Override
    public void setProductInventory(ProductInventory productInventory) {
        redisDao.opsForValue().set("productInventory:cnt:"+productInventory.getProductId(),productInventory.getInventoryCnt());
    }

    @Override
    public ProductInventory getProductInventoryCache(Long productId) {
        Long cnt = 0L;
        String key = "productInventory:cnt:"+productId;
        Object result = redisDao.opsForValue().get(key);
        if(result!=null && !"".equals(result)){
            try{
                cnt = Long.valueOf(result.toString());
                return new ProductInventory(productId,cnt);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
