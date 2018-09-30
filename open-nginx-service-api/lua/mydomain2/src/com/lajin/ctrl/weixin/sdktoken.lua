#!/usr/bin/env lua
--[[
    获取微信sdk票据及签名接口
--]]

module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local table_util = require("luastar.util.table")
local date_util = require("luastar.util.date")
local str_util = require("luastar.util.str")
local random_util = require("resty.random")
local httpclient = require("luastar.util.httpclient")
local beanFactory = luastar_context.getBeanFactory()

local function wx_sdk_token()
    local res_ok, res_code, res_headers, res_status, res_body = httpclient.request_http({
        url = luastar_config.getConfig("weixin")["sdk_token_url"],
        scheme = "https",
        params = {
            grant_type = "client_credential",
            appid = luastar_config.getConfig("weixin")["appid"],
            secret = luastar_config.getConfig("weixin")["secret"]
        }
    })
    if not res_ok or _.isEmpty(res_body) then
        return false, "获取sdk token失败。", nil
    end
    local resObj = cjson.decode(res_body)
    if not _.isEmpty(resObj["errcode"]) then
        return false, "获取sdk token失败：" .. resObj["errmsg"], nil
    end
    return true, "ok", resObj
end

local function wx_sdk_ticket(access_token)
    local res_ok, res_code, res_headers, res_status, res_body = httpclient.request_http({
        url = luastar_config.getConfig("weixin")["sdk_ticket_url"],
        scheme = "https",
        params = {
            access_token = access_token,
            type = "jsapi"
        }
    })
    if not res_ok or _.isEmpty(res_body) then
        return false, "换取sdk ticket失败。", nil
    end
    local resObj = cjson.decode(res_body)
    if not _.isEmpty(resObj["errcode"]) and resObj["errcode"] ~= 0 then
        return false, "换取sdk ticket失败：" .. resObj["errmsg"], nil
    end
    return true, "ok", resObj
end

local function getCacheToken()
    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()
    local tokenObj = table_util.array_to_hash(redis:hgetall("wx:sdk:token"))
    redis_util:close(reids)
    if _.isEmpty(tokenObj) then
        return false, "微信sdk token缓存为空。", nil
    end
    return true, "ok", tokenObj
end

local function saveCacheToken(tokenObj)
    if _.isEmpty(tokenObj) then
        return false, "token对象为空。"
    end
    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()
    redis:hmset("wx:sdk:token", tokenObj)
    redis:expire("wx:sdk:token", tokenObj["expires_in"])
    redis_util:close(redis)
end

function sdkconfig(request, response)
    local url = request:get_arg("url")
    local callback = request:get_arg("callback")
    if _.isEmpty(url) then
        response:writeln(json_util.jsonp(callback, json_util.illegal_argument()))
        return
    end
    -- 获取缓存sdk token
    local ok, msg, tokenObj = getCacheToken()
    if not ok then
        tokenObj = {}
        -- 获取access_token
        local ok_sdk_token, msg_sdk_token, obj_sdk_token = wx_sdk_token()
        if not ok_sdk_token then
            response:writeln(json_util.jsonp(callback, json_util.exp(msg_sdk_token)))
            return
        end
        tokenObj["access_token"] = obj_sdk_token["access_token"]
        -- 获取ticket
        local ok_sdk_ticket, msg_sdk_ticket, obj_sdk_ticket = wx_sdk_ticket(tokenObj["access_token"])
        if not ok_sdk_ticket then
            response:writeln(json_util.jsonp(callback, json_util.exp(msg_sdk_ticket)))
            return
        end
        tokenObj["ticket"] = obj_sdk_ticket["ticket"]
        tokenObj["expires_in"] = obj_sdk_ticket["expires_in"]
        -- 保存sdk token
        saveCacheToken(tokenObj)
    end
    local nonceStr = random_util.token(16)
    local timestamp = date_util.get_ngx_time()
    local signature_str = string.format("jsapi_ticket=%s&noncestr=%s&timestamp=%s&url=%s", tokenObj["ticket"], nonceStr, timestamp, str_util.decode_url(url))
    ngx.log(logger.i("获取sdk签名字符串：", signature_str))
    local signature = str_util.sha1(signature_str)
    -- 返回结果
    local rs = {
        appId = luastar_config.getConfig("weixin")["appid"] or "",
        timestamp = timestamp,
        nonceStr = nonceStr,
        signature = signature
    }
    ngx.log(logger.i("获取sdk签名结果：", cjson.encode(rs)))
    response:writeln(json_util.jsonp(callback, json_util.success(rs)))
end