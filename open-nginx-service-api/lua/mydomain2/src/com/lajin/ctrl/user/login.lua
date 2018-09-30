#!/usr/bin/env lua
--[[
	用户登录
--]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local httpclient = require("luastar.util.httpclient")
local beanFactory = luastar_context.getBeanFactory()

local function thirdLoginReg(param)
    -- 调用内网接口
    ngx.log(logger.i("第三方登录注册开始，参数为："), cjson.encode(param))
    local innerUrl = luastar_config.getConfig("apihost")["inner_url"] .. "/inner/app/user/thirdLogin"
    local ok, code, resheaders, status, resbody = httpclient.request(innerUrl, "POST", param, 1500000, {})
    if not ok or _.isEmpty(resbody) then
        return false, "第三方登录注册失败."
    end
    local saveResult = cjson.decode(resbody)
    if _.isEmpty(saveResult) then
        return false, "第三方登录注册失败."
    end
    if saveResult["code"] ~= "A000000" then
        return false, saveResult["message"]
    end
    return true, "第三方登录注册成功.", saveResult["data"]["uid"]
end

function login(request, response)
    local param = {}
    param["phone"] = request:get_arg("phone");
    param["passwd"] = request:get_arg("passwd");
    -- 验证必填参数
    if _.isEmpty(param["phone"]) or _.isEmpty(param["passwd"]) then
        response:writeln(json_util.illegal_argument())
        return
    end
    -- 验证sign
    local paramService = beanFactory:getBean("paramService")
    local check_ok = paramService:checkSign(param)
    if not check_ok then
        response:writeln(json_util.illegal_argument())
        return
    end
    -- 验证用户是否存在
    local userService = beanFactory:getBean("userService")
    local userStatus = userService:getUserStatusByPhone(param["phone"])
    if not userStatus["exist"] then
        response:writeln(json_util.fail('该账号不存在!'))
        return
    end
    -- 验证用户是否禁用
    if userStatus["status"] == 4 then
        response:writeln(json_util.fail('该账号已禁用!'))
        return
    end
    local userinfo = userService:getUserInfo(userStatus["uid"])
    if _.isEmpty(userinfo) then
        response:writeln(json_util.fail('该账号缓存数据异常!'))
        return
    end
    -- 验证密码是否正确
    if param["passwd"] ~= userinfo["passwd"] then
        response:writeln(json_util.fail('账号密码错误!'))
        return
    end
    -- 登录后续操作（保存token及time）
    local loginService = beanFactory:getBean("loginService")
    local ok, err, utoken = loginService:loginAfter(userStatus["uid"], userinfo)
    if not ok then
        response:writeln(json_util.fail('登陆失败'))
        return
    end
    -- 返回结果
    userinfo = _.pick(userinfo, "uid", "utoken", "phone", "username", "picurl", "resume", "status")
    userinfo = _.defaults(userinfo, userService:getUserDefault())
    response:writeln(json_util.success(userinfo, true))
end

function thirdlogin(request, response)
    -- 参数校验
    local param = {}
    param["thirdsrc"] = request:get_arg("thirdsrc")
    param["thirduid"] = request:get_arg("thirduid")
    param["username"] = request:get_arg("username")
    param["picurl"] = request:get_arg("picurl")
    ngx.log(logger.i("第三方登录参数为：", cjson.encode(param)))
    if _.isEmpty(param["thirdsrc"]) or _.isEmpty(param["thirduid"]) then
        response:writeln(json_util.illegal_argument())
        return
    end
    -- 验证sign
    local paramService = beanFactory:getBean("paramService")
    local check_ok = paramService:checkSign(param)
    if not check_ok then
        response:writeln(json_util.illegal_argument())
        return
    end
    -- 获取第三方账号的uid
    local userService = beanFactory:getBean("userService")
    local uid = userService:getThirdUid(param["thirdsrc"], param["thirduid"])
    if _.isEmpty(uid) then
        -- 注册
        local reg_ok, reg_msg, r_uid = thirdLoginReg(param)
		uid = r_uid
        if not reg_ok then
            response:writeln(json_util.fail('数据处理异常'))
            return
        end
    end
    -- 登录后续操作（保存token及time）
    local userinfo = userService:getUserInfo(uid)
    if _.isEmpty(userinfo) then
        response:writeln(json_util.fail('该账号缓存数据异常!'))
        return
    end
    local loginService = beanFactory:getBean("loginService")
    local ok, err, utoken = loginService:loginAfter(uid, userinfo)
    if not ok then
        response:writeln(json_util.fail('数据处理异常'))
        return
    end
    -- 返回结果
    userinfo = _.pick(userinfo, "uid", "utoken", "phone", "username", "picurl", "resume", "status")
    userinfo = _.defaults(userinfo, userService:getUserDefault())
    response:writeln(json_util.success(userinfo, true))
end