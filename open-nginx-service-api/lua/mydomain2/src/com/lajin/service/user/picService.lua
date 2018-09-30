#!   /usr/bin/env lua
--[[

--]]
local picService = Class("com.lajin.service.user.picService")

local table_util = require("luastar.util.table")

function picService:init(redis_util)
    self.redis_util = redis_util
end

--[[
-- 根据uuid获取图片信息
--]]
function picService:getPicInfo(pic_uuid)
    if _.isEmpty(pic_uuid) then
        return nil
    end
    local redis = self.redis_util:getConnect()
    local pic_info = table_util.array_to_hash(redis:hgetall("pub:image:" .. pic_uuid))
    self.redis_util:close(redis)
    return pic_info
end

--[[
-- 根据uuid获取图片信息，如果不存在或已删除，则返回默认值
--]]
function picService:getPicInfoNotNull(pic_uuid)
    if _.isEmpty(pic_uuid) then
        return self:getDefaultPicInfo()
    end
    local redis = self.redis_util:getConnect()
    local pic_info = table_util.array_to_hash(redis:hgetall("pub:image:" .. pic_uuid))
    self.redis_util:close(redis)
    if _.isEmpty(pic_info) or tonumber(pic_info["status"]) ~= 1 then
        pic_info = {}
    end
    return _.defaults(pic_info, self:getDefaultPicInfo())
end

--[[
-- 图片默认信息
--]]
function picService:getDefaultPicInfo()
    return {
        id = "",
        pic_name = "",
        pic_url = "",
        status = 1,
        created_time = "",
        updated_time = ""
    }
end

return picService
