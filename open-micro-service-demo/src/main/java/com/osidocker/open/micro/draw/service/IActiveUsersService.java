package com.osidocker.open.micro.draw.service;

import com.osidocker.open.micro.draw.model.ActiveUsers;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 参与用户表 服务类
 * </p>
 *
 * @author stylefeng123
 * @since 2019-03-28
 */
public interface IActiveUsersService extends IService<ActiveUsers> {
    ActiveUsers findByArgs(Integer type, Integer uid, Integer activeId);
}
