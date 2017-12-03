package com.osidocker.open.micro.controllers;

import com.osidocker.open.micro.entity.ProductInventory;
import com.osidocker.open.micro.request.IRequest;
import com.osidocker.open.micro.request.productInventory.ProductInventoryCacheReloadRequest;
import com.osidocker.open.micro.request.productInventory.ProductInventoryUpdateRequest;
import com.osidocker.open.micro.service.IProductInventoryService;
import com.osidocker.open.micro.service.IRequestAsyncProcessService;
import com.osidocker.open.micro.vo.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/product")
public class ProductInventoryController {

    @Resource
    private IProductInventoryService productInventoryService;
    @Resource
    private IRequestAsyncProcessService requestAsyncProcessService;

    @GetMapping("/get/{productId}")
    public ProductInventory getProductInventory(@PathVariable("productId") Long productId){
        ProductInventory productInventory = null;
        try{
            IRequest request = new ProductInventoryCacheReloadRequest(productId,productInventoryService,false);
            requestAsyncProcessService.process(request);

            //将请求扔给异步队列去处理后,需要等待数据获取
            //去尝试等待前面有商品库存更新的操作,同时缓存刷新的操作
            long startTime = System.currentTimeMillis();
            long endTime = 0L;
            long waitTime = 0L;
            while( true ){
                if(waitTime>200L){
                    break;
                }
                productInventory = productInventoryService.getProductInventoryCache(productId);
                if(productInventory!=null){
                    return productInventory;
                }else{
                    Thread.sleep(20);
                    endTime = System.currentTimeMillis();
                    waitTime = endTime - startTime;
                }
            }
            //等待200毫秒后,没有数据,则尝试数据库中加载数据
            productInventory = productInventoryService.findInventory(productId);
            if(productInventory!=null){
                requestAsyncProcessService.process(new ProductInventoryCacheReloadRequest(productId,productInventoryService,true));
                return productInventory;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return new ProductInventory(productId,-1L);
    }

    @GetMapping("/update")
    public Response updateProductInventory(ProductInventory productInventory){
        try{
            IRequest request = new ProductInventoryUpdateRequest(productInventory,productInventoryService);
            requestAsyncProcessService.process(request);
        }catch (Exception e){
            e.printStackTrace();
            return new Response(Response.FAILURE,e.getMessage());
        }
        return new Response(Response.SUCCESS);
    }
}
