#! /usr/bin/env lua
--[[

]]
local loginService = Class("com.lajin.service.user.loginService")

local table_util = require("luastar.util.table")
local random = require("resty.random")

function loginService:init(redis_util, userService)
    self.redis_util = redis_util
    self.userService = userService
    self.outTime = 2592000
end

-- 登录后续操作（生成token，记录最后登录时间）
function loginService:loginAfter(uid, data)
    -- 创建token
    local token = random.token(32)
    -- 保存token
    local ok, err = self:saveToken(uid, token)
    if not ok then
        return false, err
    end
    -- 记录最后登陆时间
    self:saveLastlogintime(uid)
    -- 设置数据
    if data ~= nil and _.isTable(data) then
        data["uid"] = uid
        data["utoken"] = token
    end
    return true, "用户登录成功！", token
end

--[[
   保存用户token
--]]
function loginService:saveToken(uid, token)
    if _.isEmpty(uid) or _.isEmpty(token) then
        return false, "参数错误!"
    end
    local tokenHash = {
        token = token,
        timeout = os.time() + self.outTime
    }
    local redis = self.redis_util:getConnect()
    local ok, err = redis:hmset("user:token:" .. uid, tokenHash)
    self.redis_util:close(redis)
    ngx.log(logger.i("save token:", cjson.encode({
        ok = ok,
        err = err
    })))
    return ok, err
end

--[[
-- 更新用户最后登陆时间
--]]
function loginService:saveLastlogintime(uid)
    local redis = self.redis_util:getConnect()
    local number = redis:hlen('user:info:' .. uid)
    if number > 0 then
        redis:hset('user:info:' .. uid, 'lastlogintime', os.time())
    end
    self.redis_util:close(redis)
end

--[[
-- 校验token
--]]
function loginService:checkToken(uid, token)
    -- 获取用户token
    local redis = self.redis_util:getConnect()
    local utoken = table_util.array_to_hash(redis:hgetall("user:token:" .. uid))
    self.redis_util:close(redis)
    if _.isEmpty(utoken) then
        return false, '请重新登录!'
    end
    -- 检查用户状态
    local userStatus = self.userService:getUserStatusByUid(uid)
    if not userStatus["exist"] or userStatus["status"] == 4 then
        return false, '该账号不存在或已禁用，请重新登录!'
    end
    -- 检查是否失效
    if os.time() - utoken["timeout"] > 0 then
        return false, '该账号已过期，请重新登录!'
    end
    -- 对比token是否一致
    if utoken['token'] ~= token then
        return false, '该账号已在其他设备登录，请重新登录!'
    end
    return true, "token正确！"
end

return loginService
