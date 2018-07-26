/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.controllers;

import com.osidocker.open.micro.security.service.UserService;
import com.osidocker.open.micro.security.vos.SecurityUser;
import com.osidocker.open.micro.vo.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 14:50 2018/7/26
 * @修改说明：
 * @修改日期： 14:50 2018/7/26
 * @版本号： V1.0.0
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/update")
    @ApiOperation("根据手机号码发送短信验证码")
    public Response update(@RequestBody SecurityUser user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if( !authentication.getName().equals(user.getUsername()) ){
            return new Response("000001","系统暂时只允许更新个人信息");
        }
        // 执行用户信息更新
        if( userService.updateUser(user) ){

        }
        return null;
    }

}
