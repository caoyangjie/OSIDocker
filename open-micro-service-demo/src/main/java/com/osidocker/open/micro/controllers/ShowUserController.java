/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.controllers;

import com.osidocker.open.micro.entity.ShowUserEntity;
import com.osidocker.open.micro.request.AbsCacheReloadRequest;
import com.osidocker.open.micro.request.AbsUpdateRequest;
import com.osidocker.open.micro.service.AbsDataOperateService;
import com.osidocker.open.micro.service.IDataOperateService;
import com.osidocker.open.micro.service.IRequestAsyncProcessService;
import com.osidocker.open.micro.service.impl.RequestAsyncProcessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 9:04 2018/9/8
 * @修改说明：
 * @修改日期： 9:04 2018/9/8
 * @版本号： V1.0.0
 */
@RestController
@RequestMapping("/showUser")
public class ShowUserController extends AbsConcurrenceController<ShowUserEntity,ShowUserEntity>{

    @Override
    protected AbsCacheReloadRequest getCacheRequestInstance(Long id, ShowUserEntity showUserEntity, boolean forceRefresh, IDataOperateService<ShowUserEntity> viewObjectService) {
        return new AbsCacheReloadRequest<ShowUserEntity,ShowUserEntity>(id,forceRefresh,showUserEntity,viewObjectService){
            @Override
            public String getHashKey() {
                return String.valueOf(id);
            }
        };
    }

    @Override
    protected AbsUpdateRequest getUpdateRequestInstance(ShowUserEntity showUserEntity, IDataOperateService<ShowUserEntity> viewObjectService) {
        return new AbsUpdateRequest<ShowUserEntity>(showUserEntity,viewObjectService) {
            @Override
            public String getHashKey() {
                return showUserEntity.getUserId();
            }
        };
    }

    @Override
    protected AbsDataOperateService<ShowUserEntity> dataOperateService() {
        return null;
    }

    @Override
    protected ShowUserEntity defaultResponseEntity(Long id, ShowUserEntity showUserEntity) {
        return showUserEntity;
    }
}
