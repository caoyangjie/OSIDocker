/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.security.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.osidocker.open.micro.model.SystemUser;
import com.osidocker.open.micro.security.vos.SecurityUser;
import com.osidocker.open.micro.security.vos.User;
import org.apache.ibatis.annotations.Param;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 21:25 2018/7/25
 * @修改说明：
 * @修改日期： 21:25 2018/7/25
 * @版本号： V1.0.0
 */
public interface UserMapper extends BaseMapper<User> {
    SecurityUser findByUsername(@Param("username") String username);

    int addUserByPhone(@Param("telephone") String telephone);

    int updateUser(@Param("user") SystemUser user);
}
