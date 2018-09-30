#!   /usr/bin/env lua
--[[
	用户注册
--]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local util_str = require("luastar.util.str")
local httpclient = require("luastar.util.httpclient")
local beanFactory = luastar_context.getBeanFactory()

function register(request, response)
    -- 参数校验
    local param = {}
    param["phone"] = request:get_arg("phone", "")
    param["passwd"] = request:get_arg("passwd", "")
    param["smscode"] = request:get_arg("smscode", "")
    param["username"] = request:get_arg("username")
    param["resume"] = request:get_arg("resume")
    ngx.log(logger.i("用户注册输入参数：", cjson.encode(param)))
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
    if userStatus["exist"] then
        if userStatus["status"] == 4 then
            response:writeln(json_util.fail("该账号已被限制注册!"))
            return
        end
        response:writeln(json_util.fail("该账号已注册，请登录!"))
        return
    end
    -- 检查短信验证码
    local smsService = beanFactory:getBean("smsService")
    local check_ok, check_msg = smsService:checkSmscode(param["phone"], param["smscode"])
    if not check_ok then
        response:writeln(json_util.fail(check_msg))
        return
    end
    -- 用户图片
    local picfile = request:get_arg("picfile")
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
    local uid = saveResult["data"]["uid"]
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


