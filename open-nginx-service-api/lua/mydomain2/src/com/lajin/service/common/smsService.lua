#!  /usr/bin/env lua
--[[

--]]
local smsService = Class("com.lajin.service.common.smsService")

local table_util = require("luastar.util.table")
local httpclient = require("luastar.util.httpclient")

function smsService:init(redis_util)
    self.redis_util = redis_util
end

--[[
-- 云片网短信接口
--]]
function smsService:sendMsg(phone, code)
    local res_ok, res_code, res_headers, res_status, res_body = httpclient.request_http({
        url = luastar_config.getConfig("apihost")["sms_url"],
        method = "POST",
        params = {
            apikey = "a6bb2f5d1bf12896c121f16abeb06174",
            mobile = phone,
            text = "【火喵App】您的验证码是:" .. code
        }
    })
    if not res_ok or _.isEmpty(res_body) then
        return false, "短信发送失败。"
    end
    local res_body_table = cjson.decode(res_body)
    if tonumber(res_body_table["code"]) ~= 0 then
        return false, "短信发送失败。"
    end
    return true, "短信发送成功。"
end

--[[
-- 校验验证码
--]]
function smsService:checkSmscode(phone, code)
    local redis = self.redis_util:getConnect()
    local data = table_util.array_to_hash(redis:hgetall("user:smscode:"..phone))
    self.redis_util:close(redis)
    ngx.log(logger.i("校验验证码：",cjson.encode(data)))
    if _.isEmpty(data["code"]) or _.isEmpty(data["timeout"]) then
       return false, "验证码不存在!"
    end
    if data["timeout"] - os.time() <= 0 then
        return false, "验证码失效!"
    end
    if data["code"] ~= code then
        return false, "验证码错误!"
    end
    return true, "验证码正确！"
end

--[[
-- 保存手机验证码
--]]
function smsService:saveSmscode(phone, code)
    local config_timeout = luastar_config.getConfig("smscode")["timeout"]
    local data = {
        code = code,
        timeout = os.time() + config_timeout
    }
    local redis = self.redis_util:getConnect()
    redis:hmset("user:smscode:" .. phone, data)
    self.redis_util:close(redis)
end

return smsService
