#! /usr/bin/env lua
--[[
	用户信息维护
--]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local util_str = require("luastar.util.str")
local httpclient = require("luastar.util.httpclient")
local beanFactory = luastar_context.getBeanFactory()

--[[
    获取用户信息
--]]
function getinfo(request, response)
    -- 私有参数校验
    local param = {}
    param["uid"] = request:get_arg("uid");
    param["utoken"] = request:get_arg("utoken");
    if _.isEmpty(param["uid"]) or _.isEmpty(param["utoken"]) then
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
    -- 校验token
    local loginService = beanFactory:getBean("loginService")
    local check_ok, check_msg = loginService:checkToken(param["uid"], param["utoken"])
    if not check_ok then
        response:writeln(json_util.illegal_token(check_msg))
        return
    end
    local userService = beanFactory:getBean("userService")
    local userinfo = userService:getUserInfo(param["uid"])
    if _.isEmpty(userinfo) then
        response:writeln(json_util.fail("用户不存在"))
        return
    end
    userinfo["uid"] = param["uid"]
    userinfo["utoken"] = param["utoken"]
    userinfo = _.pick(userinfo, "uid", "utoken", "phone", "username", "picurl", "resume", "status")
    userinfo = _.defaults(userinfo, userService:getUserDefault())
    response:writeln(json_util.success(userinfo, true))
end

--[[
    编辑用户信息
--]]
function editinfo(request, response)
    -- 私有参数校验
    local param = {}
    param["uid"] = request:get_arg("uid");
    param["utoken"] = request:get_arg("utoken");
    param["username"] = request:get_arg("username")
    param["resume"] = request:get_arg("resume")
    if _.isEmpty(param["uid"]) or _.isEmpty(param["utoken"]) then
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
    -- 校验token
    local loginService = beanFactory:getBean("loginService")
    local check_ok, check_msg = loginService:checkToken(param["uid"], param["utoken"])
    if not check_ok then
        response:writeln(json_util.illegal_token(check_msg))
        return
    end
    -- 用户图片
    local picfile = request:get_upload_arg("picfile")
    if not _.isEmpty(picfile) then
        if _.isString(picfile) then
            picfile = util_str.encode_url(picfile)
        end
        param["iconfile"] = picfile
    end
    -- 调用内网接口保存用户
    local userRegUrl = luastar_config.getConfig("apihost")["inner_url"] .. "/inner/app/user/save"
    local ok, code, resheaders, status, resbody = httpclient.request(userRegUrl, "POST", param, 1500000, {})
    if not ok or _.isEmpty(resbody) then
        response:writeln(json_util.fail("用户保存失败"))
        return
    end
    local saveResult = cjson.decode(resbody)
    if _.isEmpty(saveResult) then
        response:writeln(json_util.fail("用户保存失败"))
        return
    end
    if saveResult["code"] ~= "A000000" then
        response:writeln(json_util.fail('用户保存失败'))
        return
    end
    -- 返回用户信息
    local userService = beanFactory:getBean("userService")
    local userinfo = userService:getUserInfo(param["uid"])
    userinfo["uid"] = param["uid"]
    userinfo["utoken"] = param["utoken"]
    userinfo = _.pick(userinfo, "uid", "utoken", "phone", "username", "picurl", "resume", "status")
    userinfo = _.defaults(userinfo, userService:getUserDefault())
    response:writeln(json_util.success(userinfo, true))
end

function editpasswd(request, response)
    -- 私有参数校验
    local param = {}
    param["phone"] = request:get_arg("phone");
    param["passwd"] = request:get_arg("passwd");
    param["smscode"] = request:get_arg("smscode");
    local is_ok = _.any(_.pick(param, "phone", "passwd", "smscode"), function(v) if _.isEmpty(v) then return true end end)
    if is_ok then
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
    -- 检查用户状态
    local userService = beanFactory:getBean("userService")
    local userStatus = userService:getUserStatusByPhone(param["phone"])
    if not userStatus["exist"] then
        response:writeln(json_util.fail("该账号不存在!"))
        return
    end
    if userStatus["status"] == 4 then
        response:writeln(json_util.fail("该账号已被禁用!"))
        return
    end
    -- 检查短信验证码
    local smsService = beanFactory:getBean("smsService")
    local check_ok, check_msg = smsService:checkSmscode(param["phone"], param["smscode"])
    if not check_ok then
        response:writeln(json_util.fail(check_msg))
        return
    end
    -- 调用内网接口修改密码
    local chgPasswdUrl = luastar_config.getConfig("apihost")["inner_url"] .. "/inner/app/user/pwdsave"
    local chgParam = { uid = userStatus["uid"], newpw = param["passwd"] }
    local ok, code, resheaders, status, resbody = httpclient.request(chgPasswdUrl, "POST", chgParam, 1500000, {})
    if not ok or _.isEmpty(resbody) then
        response:writeln(json_util.fail("修改密码失败。"))
        return
    end
    local saveResult = cjson.decode(resbody)
    if _.isEmpty(saveResult) then
        response:writeln(json_util.fail("修改密码失败。"))
        return
    end
    if saveResult["code"] ~= "A000000" then
        response:writeln(json_util.fail("修改密码失败。"))
        return
    end
    response:writeln(json_util.success())
end
