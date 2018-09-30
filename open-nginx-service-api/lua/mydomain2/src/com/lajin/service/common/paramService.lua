#!     /usr/bin/env lua
--[[
    输入参数检验
]]
local paramService = Class("com.lajin.service.common.paramService")

local str_util = require("luastar.util.str")
local table_util = require("luastar.util.table")

function paramService:init()
end

function paramService:getHeadParam()
    local request = ngx.ctx.request
    local param = {}
    param["random"] = request:get_header("random") or ""
    param["appkey"] = request:get_header("appkey") or ""
    param["appversion"] = request:get_header("appversion") or ""
	param["apiversion"] = request:get_header("apiversion") or ""
    param["datakey"] = request:get_header("datakey") or ""
    param["ostype"] = request:get_header("ostype") or self:getOstypeByUA()
    param["osrelease"] = request:get_header("osrelease") or ""
    return param
end

function paramService:getOstypeByUA()
    local userAgent = ngx.ctx.request:get_header("User-Agent")
    if _.isEmpty(userAgent) then
        ngx.log(logger.i("User-Agent为空。"))
        return ""
    end
    ngx.log(logger.i("User-Agent为：", userAgent))
    -- 判断是否是android
    local i, j = string.find(userAgent, "Android")
    if i ~= nil then
        ngx.log(logger.i("User-Agent包含Android关键字，判断为Android。"))
        return "2"
    end
    i, j = string.find(userAgent, "Linux")
    if i ~= nil then
        ngx.log(logger.i("User-Agent包含Linux关键字，判断为Android。"))
        return "2"
    end
    return ""
end

function paramService:checkSign(checkParam)
    -- 输入sign
    local request = ngx.ctx.request
    local sign = request:get_header("sign")
    -- ngx.log(logger.i("sign input is ", sign))
    -- 输入参数（不为空的）
    local param_array = {}
    if type(checkParam) == "string" then
        param_array = str_util.split("&")
    elseif type(checkParam) == "table" then
        param_array = table_util.table2arr(checkParam)
    end
    -- 按参数名排序
    table.sort(param_array)
    -- 拼接密钥
    local param_base = table.concat(param_array, "") .. "UJMpkYFiq4YDMLkEXgqYUltbfWCb7p67"
    -- ngx.log(logger.i("sign str is ", param_base))
    -- url编码
    local param_encode = str_util.encode_url(param_base)
    -- ngx.log(logger.i("sign urlencode is ", param_encode))
    -- 计算md5
    local param_md5 = str_util.md5(param_encode)
    -- ngx.log(logger.i("sign md5 is ", param_md5))
    -- 对比sign
    if sign == param_md5 then
        return true
    end
    -- 暂不校验sign
    -- return false
    return true
end

return paramService
