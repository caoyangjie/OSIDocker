#!/usr/bin/env lua
--[[
    获取微信用户信息接口
--]]

module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local table_util = require("luastar.util.table")
local httpclient = require("luastar.util.httpclient")
local beanFactory = luastar_context.getBeanFactory()

local function wx_access_token(code, state)
    local res_ok, res_code, res_headers, res_status, res_body = httpclient.request_http({
        url = luastar_config.getConfig("weixin")["access_token_url"],
        scheme = "https",
        params = {
            appid = luastar_config.getConfig("weixin")["appid"],
            secret = luastar_config.getConfig("weixin")["secret"],
            code = code,
            grant_type = "authorization_code"
        }
    })
    if not res_ok or _.isEmpty(res_body) then
        return false, "获取用户授权信息失败。", nil
    end
    local resObj = cjson.decode(res_body)
    if not _.isEmpty(resObj["errcode"]) then
        return false, "获取用户授权信息失败：" .. resObj["errmsg"], nil
    end
    return true, "ok", resObj
end

local function wx_check_token(openid, access_token)
    local res_ok, res_code, res_headers, res_status, res_body = httpclient.request_http({
        url = luastar_config.getConfig("weixin")["check_token_url"],
        scheme = "https",
        params = {
            access_token = access_token,
            openid = openid
        }
    })
    if not res_ok or _.isEmpty(res_body) then
        return false, "验证token失败。"
    end
    local resObj = cjson.decode(res_body)
    if not _.isEmpty(resObj["errcode"]) and resObj["errcode"] ~= 0 then
        return false, "验证token失败：" .. resObj["errmsg"]
    end
    return true, "ok"
end

local function wx_refresh_token(refresh_token)
    local res_ok, res_code, res_headers, res_status, res_body = httpclient.request_http({
        url = luastar_config.getConfig("weixin")["access_token_url"],
        scheme = "https",
        params = {
            appid = luastar_config.getConfig("weixin")["appid"],
            secret = luastar_config.getConfig("weixin")["secret"],
            grant_type = "refresh_token",
            refresh_token = refresh_token
        }
    })
    if not res_ok or _.isEmpty(res_body) then
        return false, "换取token失败。", nil
    end
    local resObj = cjson.decode(res_body)
    if not _.isEmpty(resObj["errcode"]) then
        return false, "换取token失败：" .. resObj["errmsg"], nil
    end
    return true, "ok", resObj
end

local function wx_userinfo(openid, access_token)
    local res_ok, res_code, res_headers, res_status, res_body = httpclient.request_http({
        url = luastar_config.getConfig("weixin")["userinfo_url"],
        scheme = "https",
        params = {
            openid = openid,
            access_token = access_token,
            lang = "zh_CN"
        }
    })
    if not res_ok or _.isEmpty(res_body) then
        return false, "获取用户基本信息失败。", nil
    end
    local resObj = cjson.decode(res_body)
    if not _.isEmpty(resObj["errcode"]) then
        return false, "获取用户基本信息失败：" .. resObj["errmsg"], nil
    end
    return true, "ok", resObj
end

local function getCacheToken(openid)
    if _.isEmpty(openid) then
        return false, "openid为空。", nil
    end
    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()
    local tokenObj = table_util.array_to_hash(redis:hgetall("wx:user:" .. openid))
    redis_util:close(reids)
    if _.isEmpty(tokenObj) then
        return false, "用户[" .. openid .. "]token缓存为空。", nil
    end
    return true, "ok", tokenObj
end

local function saveCacheToken(openid, tokenObj)
    if _.isEmpty(openid) or _.isEmpty(tokenObj) then
        return false, "openid或token对象为空。"
    end
    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()
    redis:hmset("wx:user:" .. openid, tokenObj)
    redis_util:close(redis)
end

local function getUserInfoByOpenid(openid)
    local ok, msg, tokenObj = getCacheToken(openid)
    if not ok then
        return false, msg, nil
    end
    -- 判断token是否失效
    local ok, msg = wx_check_token(openid, tokenObj["access_token"])
    if not ok then
        -- 失效后刷新token
        ok, msg, tokenObj = wx_refresh_token(tokenObj["refresh_token"])
        -- 保存刷新后的token
        if ok then
            saveCacheToken(openid, tokenObj)
        else
            return false, msg, nil
        end
    end
    -- 获取微信用户基本信息
    local ok, msg, userObj = wx_userinfo(openid, tokenObj["access_token"])
    if not ok then
        return false, msg, nil
    end
    return true, "ok", userObj
end

local function getUserInfoByCode(code, state)
    -- 请求授权接口，获取token
    local ok, msg, tokenObj = wx_access_token(code, state)
    if not ok then
        return false, msg, nil
    end
    -- 保存用户token
    saveCacheToken(tokenObj["openid"], tokenObj)
    -- 获取微信用户基本信息
    local ok, msg, userObj = wx_userinfo(tokenObj["openid"], tokenObj["access_token"])
    if not ok then
        return false, msg, nil
    end
    return true, "ok", userObj
end

function userinfo(request, response)
    -- 参数校验
    local params = {}
    params["callback"] = request:get_arg("callback")
    params["openid"] = request:get_arg("openid")
    params["code"] = request:get_arg("code")
    params["state"] = request:get_arg("state")
    ngx.log(logger.i("获取微信用户信息接口：", cjson.encode(params)))
    if _.isEmpty(params["openid"]) and _.isEmpty(params["code"]) then
        response:writeln(json_util.jsonp(params["callback"], json_util.illegal_argument()))
        return
    end
    local ok, msg, userObj
    if not _.isEmpty(params["code"]) then
        ok, msg, userObj = getUserInfoByCode(params["code"], params["state"])
    else
        ok, msg, userObj = getUserInfoByOpenid(params["openid"])
    end
    if not ok then
        response:writeln(json_util.jsonp(params["callback"], json_util.exp(msg)))
        return
    end
    -- 返回结果
    local rs = {
        openid = userObj["openid"] or "",
        nickname = userObj["nickname"] or "",
        sex = userObj["sex"] or 0,
        headimgurl = userObj["headimgurl"] or ""
    }
    response:writeln(json_util.jsonp(params["callback"], json_util.success(rs)))
end