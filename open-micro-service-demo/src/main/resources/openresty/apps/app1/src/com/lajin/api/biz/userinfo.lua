
module(..., package.seeall)

local logger = luastar_log.getLog()
local cjson = require("cjson")
local str_util = require("luastar.util.str")
function get_user_info(uid,redis)
	local beanFactory = luastar_context.getBeanFactory()
	local redis_util = beanFactory:getBean("redis")
	if  str_util.isnull(redis) then
		redis = redis_util:getConnect()
	end
	local table_util = require("luastar.util.table")
	local userinfo = table_util.array_to_hash(redis:hgetall("user:info:"..uid))
	logger:info(cjson.encode(userinfo))
	--redis_util:close(redis)
	return userinfo
end
