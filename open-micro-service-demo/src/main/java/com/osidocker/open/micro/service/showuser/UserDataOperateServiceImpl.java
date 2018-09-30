/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.service.showuser;

import com.osidocker.open.micro.entity.ShowUserEntity;
import com.osidocker.open.micro.service.AbsDataOperateService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 9:51 2018/9/8
 * @修改说明：
 * @修改日期： 9:51 2018/9/8
 * @版本号： V1.0.0
 */
@Service("userDataOperateServiceImpl")
public class UserDataOperateServiceImpl extends AbsDataOperateService<ShowUserEntity> {

    @Resource
    @Qualifier("redisTemplate")
    private RedisTemplate redisDao;

    @Override
    public void updateResponseEntity(ShowUserEntity showUserEntity) {

    }

    @Override
    public void removeResponseEntityCache(ShowUserEntity showUserEntity) {
        redisDao.delete("showUserEntity:userId:"+showUserEntity.getUserId());
    }

    @Override
    public <RequestEntity> ShowUserEntity findResponseEntity(Long id, RequestEntity requestEntity) {
        return null;
    }

    @Override
    public void setResponseEntity(ShowUserEntity showUserEntity) {
        redisDao.opsForValue().set("showUserEntity:userId:"+showUserEntity.getUserId(),showUserEntity);
    }

    @Override
    public ShowUserEntity getResponseEntityCache(Long id) {
        return (ShowUserEntity) redisDao.opsForValue().get("showUserEntity:userId:"+id);
    }
}
