#!/usr/bin/env lua
--[[

--]]
local userService = Class("com.lajin.service.user.userService")
local table_util = require("luastar.util.table")

function userService:init(redis_util)
    self.redis_util = redis_util
end

--[[
-- 根据uid获取用户状态
--]]
function userService:getUserStatusByUid(uid)
    local result = {
        exist = false,
        status = 0
    }
    if not _.isEmpty(uid) then
        result["exist"] = true
        local redis = self.redis_util:getConnect()
        result["status"] = redis:hget("user:info:" .. uid, "status")
        self.redis_util:close(redis)
        if _.isEmpty(result["status"]) then
            result["status"] = 0
        end
    end
    result["status"] = tonumber(result["status"])
    return result
end

--[[
-- 根据phone获取用户状态
--]]
function userService:getUserStatusByPhone(phone)
    local result = {
        exist = false,
        uid = nil,
        status = 1
    }
    local redis = self.redis_util:getConnect()
    local uid = redis:get("user:phone:" .. phone)
    if not _.isEmpty(uid) then
        result["exist"] = true
        result["uid"] = uid
        result["status"] = redis:hget("user:info:" .. uid, "status")
        if _.isEmpty(result["status"]) then
            result["status"] = 1
        end
    end
    self.redis_util:close(redis)
    result["status"] = tonumber(result["status"])
    ngx.log(logger.i("根据手机号获取用户状态：", cjson.encode(result)))
    return result
end

--[[
-- 根据uid获取用户信息
--]]
function userService:getUserInfo(uid)
    if _.isEmpty(uid) then
        return nil
    end
    local redis = self.redis_util:getConnect()
    local userinfo = table_util.array_to_hash(redis:hgetall("user:info:" .. uid))
    self.redis_util:close(redis)
    if _.isEmpty(userinfo) then
        ngx.log(logger.e("userinfo is empty, uid=", uid))
        return nil
    end
    local pic_url = userinfo['picurl'] or luastar_config.getConfig("default_value")["user_icon"]
    local faviconpx = luastar_config.getConfig("tplpicpx")["headfavicon"]
    if not _.isEmpty(pic_url) and not _.isEmpty(faviconpx) then
        userinfo['picurl'] = pic_url .. faviconpx
    end
    ngx.log(logger.i(cjson.encode(userinfo)))
    return userinfo
end

--[[
-- 获取第三方账号uid
--]]
function userService:getThirdUid(thirdsrc, thirduid)
    if _.isEmpty(thirdsrc) or _.isEmpty(thirduid) then
        return nil
    end
    local redis = self.redis_util:getConnect()
    local uid = redis:get("user:thirdid:" .. thirdsrc .. ":" .. thirduid)
    self.redis_util:close(redis)
    return uid
end

--[[
-- 获取用户默认值
--]]
function userService:getUserDefault()
    local user = {
        uid = "",
        utoken = "",
        phone = "",
        username = "",
        picurl = "",
        resume = "",
        status = 1
    }
    return user
end

return userService
