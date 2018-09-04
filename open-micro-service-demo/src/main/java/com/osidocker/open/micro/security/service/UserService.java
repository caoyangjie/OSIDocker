/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.security.service;

import com.osidocker.open.micro.model.SystemUser;
import com.osidocker.open.micro.security.vos.SecurityUser;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 20:02 2018/7/25
 * @修改说明：
 * @修改日期： 20:02 2018/7/25
 * @版本号： V1.0.0
 */
public interface UserService {
    /**
     * 根据电话号码获取用户信息
     * @param telephone
     * @return  返回用户的信息
     */
    SecurityUser findUserByTelephone(String telephone);

    /**
     * 根据电话号码注册用户
     * @param telephone
     * @return  是否注册成功标识
     */
    boolean registerUser(String telephone);

    /**
     * 根据用户信息
     * @param user
     * @return
     */
    boolean updateUser(SystemUser user);
}
