/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.hystrix.showuser;

import com.osidocker.open.micro.config.HystrixCommandConfig;
import com.osidocker.open.micro.entity.ShowUserEntity;
import com.osidocker.open.micro.hystrix.AbsHystrixCommand;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 18:45 2018/9/8
 * @修改说明：
 * @修改日期： 18:45 2018/9/8
 * @版本号： V1.0.0
 */
public class ShowUserHystrixCommand extends AbsHystrixCommand<ShowUserEntity,ShowUserEntity> {

    public ShowUserHystrixCommand(ShowUserEntity showUserEntity, HystrixCommandConfig config) {
        super(showUserEntity, config);
    }

    @Override
    protected ShowUserEntity firstLevelHandler(ShowUserEntity showUserEntity) {
        return null;
    }

    @Override
    protected ShowUserEntity secondLevelHandler(ShowUserEntity showUserEntity) {
        return null;
    }

    @Override
    protected ShowUserEntity fallBackHandler(ShowUserEntity showUserEntity) {
        return null;
    }
}
