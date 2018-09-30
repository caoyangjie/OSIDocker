/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.controllers;

import com.osidocker.open.micro.entity.IsDegrade;
import com.osidocker.open.micro.request.AbsCacheReloadRequest;
import com.osidocker.open.micro.request.AbsUpdateRequest;
import com.osidocker.open.micro.request.IRequest;
import com.osidocker.open.micro.service.AbsDataOperateService;
import com.osidocker.open.micro.service.IRequestAsyncProcessService;
import com.osidocker.open.micro.service.IDataOperateService;
import com.osidocker.open.micro.service.impl.RequestAsyncProcessServiceImpl;
import com.osidocker.open.micro.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：  用来提升吞吐量,高并发的基础controller
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于10:23 2018/3/8
 * @修改说明：
 * @修改日期： 修改于10:23 2018/3/8
 * @版本号： V1.0.0
 */
public abstract class AbsConcurrenceController<ResponseEntity,RequestEntity> extends CoreController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier(RequestAsyncProcessServiceImpl.REQUEST_ASYN_PROCESS_SERVICE_IMPL)
    private IRequestAsyncProcessService requestAsyncProcessService;

    @GetMapping("/get/{id}")
    public ResponseEntity get(@PathVariable("id") Long id, @RequestParam("forceRefresh")boolean forceRefresh, @RequestBody RequestEntity requestEntity){
        ResponseEntity viewObject;
        IRequest request = getCacheRequestInstance(id, requestEntity, forceRefresh, dataOperateService());
        Optional<IRequestAsyncProcessService> processServiceOptional = Optional.ofNullable(requestAsyncProcessService());
        Optional<IDataOperateService<ResponseEntity>> dataOperateService = Optional.ofNullable(dataOperateService());
        //将请求扔给异步队列去处理后,需要等待数据获取
        //去尝试等待前面有商品库存更新的操作,同时缓存刷新的操作
        if( processServiceOptional.isPresent() ){
            processServiceOptional.get().process(request);
        }else{
            throw new RuntimeException("必须设置requestAsyncProcessService()的服务对象!");
        }
        if( !dataOperateService.isPresent() ){
            throw new RuntimeException("必须设置viewObjectService()的服务对象!");
        }

        try{
            long startTime = System.currentTimeMillis();
            long endTime = 0L;
            long waitTime = 0L;
            while( true ){
                if(waitTime>200L){
                    break;
                }
                viewObject = dataOperateService.get().getResponseEntityCache(id);
                if(viewObject!=null){
                    return viewObject;
                }else{
                    Thread.sleep(30);
                    endTime = System.currentTimeMillis();
                    waitTime = endTime - startTime;
                }
            }
            viewObject = dataOperateService.get().findResponseEntity(id,requestEntity);
            if( viewObject!=null ){
                processServiceOptional.get().process(getCacheRequestInstance(id, requestEntity, true, dataOperateService()));
                return viewObject;
            }
        }catch(Exception e){
            logger.error(e.getMessage());
        }
        return defaultResponseEntity(id, requestEntity);
    }

    @PostMapping("/update")
    public Response updateViewObject(RequestEntity requestEntity){
        try{
            IRequest request = getUpdateRequestInstance(requestEntity, dataOperateService());
            Optional<IRequestAsyncProcessService> processServiceOptional = Optional.ofNullable(requestAsyncProcessService());
            if( processServiceOptional.isPresent() ){
                processServiceOptional.get().process(request);
            }else{
                throw new RuntimeException("必须设置requestAsyncProcessService()的服务对象!");
            }
        }catch (Exception e){
            e.printStackTrace();
            return new Response(Response.FAILURE,e.getMessage());
        }
        return new Response(Response.SUCCESS);
    }

    @RequestMapping("/isDegrade")
    @ResponseBody
    public String isDegrade(boolean degrade) {
        IsDegrade.setDegrade(degrade);
        return "success";
    }

    /**
     * 根据请求参数获取请求数据处理对象
     * @param id            获取数据id
     * @param requestEntity 其他请求参数
     * @param forceRefresh  是否强制刷新
     * @param viewObjectService 数据处理服务对象
     * @return
     */
    protected abstract AbsCacheReloadRequest getCacheRequestInstance(Long id, RequestEntity requestEntity, boolean forceRefresh, IDataOperateService<ResponseEntity> viewObjectService);

    /**
     * 根据请求参数获取更新处理对象
     * @param requestEntity     请求实体
     * @param viewObjectService 数据操作实体对象
     * @return
     */
    protected abstract AbsUpdateRequest getUpdateRequestInstance(RequestEntity requestEntity, IDataOperateService<ResponseEntity> viewObjectService);

    /**
     * 设置当前处理请求service
     * @return
     */
    protected IRequestAsyncProcessService requestAsyncProcessService(){
        return requestAsyncProcessService;
    }

    /**
     * 设置当前数据获取后的处理service
     * @return
     */
    protected abstract AbsDataOperateService<ResponseEntity> dataOperateService();

    /**
     * 数据为获取成功后的默认返回数据方法
     * @param id            请求数据id
     * @param requestEntity 请求数据对象
     * @return
     */
    protected abstract ResponseEntity defaultResponseEntity(Long id, RequestEntity requestEntity);
}
