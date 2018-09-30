#!  /usr/bin/env lua
--[[
    升级服务
--]]
local upgradeService = Class("com.lajin.service.config.upgradeService")

local table_util = require("luastar.util.table")

function upgradeService:init(redis_util)
    self.redis_util = redis_util
end

--[[
-- 根据appkey查找pkgid
--]]
function upgradeService:getPkgId(appkey)
    if _.isEmpty(appkey) then
        return nil
    end
    local redis = self.redis_util:getConnect()
    local pkgid = redis:get("up:pkg:key:" .. appkey)
    self.redis_util:close(redis)
    return pkgid
end

--[[
-- 查找最新的2个版本
--]]
function upgradeService:getLastVerAry(pkgid)
    if _.isEmpty(pkgid) then
        return nil
    end
    local redis = self.redis_util:getConnect()
    local last_ver_ary = redis:zrange("up:pkg:ver:" .. pkgid, -2, -1, "WITHSCORES")
    self.redis_util:close(redis)
    return last_ver_ary
end

--[[
-- 获取版本详细信息
--]]
function upgradeService:getVerInfo(verid)
    if _.isEmpty(verid) then
        return nil
    end
    local redis = self.redis_util:getConnect()
    local verinfo = table_util.array_to_hash(redis:hgetall("up:ver:info:" .. verid))
    self.redis_util:close(redis)
    return verinfo
end

--[[
-- 获取版本规则列表
--]]
function upgradeService:getVerRuleAry(verid)
    if _.isEmpty(verid) then
        return nil
    end
    local redis = self.redis_util:getConnect()
    local ver_rule_ary = redis:smembers("up:ver:rule:list:" .. verid)
    self.redis_util:close(redis)
    return ver_rule_ary
end

--[[
-- 获取版本规则详细信息
--]]
function upgradeService:getVerRuleInfo(ver_rule_id)
    if _.isEmpty(ver_rule_id) then
        return nil
    end
    local redis = self.redis_util:getConnect()
    local ver_rule_info = table_util.array_to_hash(redis:hgetall("up:ver:rule:info:" .. ver_rule_id))
    self.redis_util:close(redis)
    return ver_rule_info
end

--[[
-- 获取规则详细信息
--]]
function upgradeService:getRuleInfo(rule_id)
    if _.isEmpty(rule_id) then
        return nil
    end
    local redis = self.redis_util:getConnect()
    local rule_info = table_util.array_to_hash(redis:hgetall("up:rule:" .. rule_id))
    self.redis_util:close(redis)
    return rule_info
end

--[[
-- 获取版本规则条件ID列表
--]]
function upgradeService:getVerRuleConAry(ver_rule_id)
    if _.isEmpty(ver_rule_id) then
        return nil
    end
    local redis = self.redis_util:getConnect()
    local ver_rule_con_ary = redis:smembers("up:ver:rule:conlist:" .. ver_rule_id)
    self.redis_util:close(redis)
    return ver_rule_con_ary
end

--[[
-- 获取条件详细信息
--]]
function upgradeService:getConInfo(con_id)
    if _.isEmpty(con_id) then
        return nil
    end
    local redis = self.redis_util:getConnect()
    local con_info = table_util.array_to_hash(redis:hgetall("up:ver:rule:coninfo:" .. con_id))
    self.redis_util:close(redis)
    return con_info
end

return upgradeService
