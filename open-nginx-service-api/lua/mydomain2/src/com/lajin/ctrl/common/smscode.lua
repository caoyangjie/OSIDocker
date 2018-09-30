#!/usr/bin/env lua
--[[
	发送短信接口
--]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local beanFactory = luastar_context.getBeanFactory()

--[[
-- 获取短信验证码
--]]
local function genCode()
    return tostring(os.time()):reverse():sub(1, 6)
end

function smscode(request, response)
    -- 参数校验
    local param = {}
    param["phone"] = request:get_arg("phone")
    param["type"] = request:get_arg("type")
    if _.isEmpty(param["phone"]) or _.isEmpty(param["type"]) then
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
    -- type相关校验
    param["type"] = tonumber(param["type"])
    local userService = beanFactory:getBean("userService")
    local userStatus = userService:getUserStatusByPhone(param["phone"])
    if param["type"] == 1 then
        -- 1 注册时，如果该用户已注册返回： stauts为4 数据异常  msg：该账号已注册
        if userStatus["exist"] then
            if userStatus["status"] == 4 then
                response:writeln(json_util.fail("该账号已被限制注册!"))
                return
            else
                response:writeln(json_util.fail("该账号已注册，请登录!"))
                return
            end
        end
    elseif param["type"] == 2 then
        -- 2 忘记密码，如果该用户未注册返回：stauts为4 数据异常 msg：该账号不存在
        if not userStatus["exist"] then
            response:writeln(json_util.fail('该账号不存在!'))
            return
        end
    end
    -- 发送短信
    local sms_code = genCode();
    local smsService = beanFactory:getBean("smsService")
    local send_ok, send_msg = smsService:sendMsg(param["phone"], sms_code)
    if not send_ok then
        response:writeln(json_util.fail(send_msg))
        return
    end
    -- 将短信信息写入缓存
    smsService:saveSmscode(param["phone"], sms_code)
    -- 返回结果
    local rs = {
        timeout = luastar_config.getConfig("smscode")["timeout"],
        resend = luastar_config.getConfig("smscode")["resend"]
    }
    response:writeln(json_util.success(rs))
end



