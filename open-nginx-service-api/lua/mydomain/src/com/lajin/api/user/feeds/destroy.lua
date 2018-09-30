module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local cjson = require("cjson")
local underscore = require("underscore")


function destroy(request, response)
	-- local head = {}
	-- local datakey = request:get_header("datakey")
	-- local sign = request:get_header("sign")
	-- local ip = request.remote_addr
	-- check param and header
	local beanFactory = luastar_context.getBeanFactory()
	local check = beanFactory:getBean("check")
	
	local is_ok = check:head()
	logger:info("head  is ok "..tostring(is_ok))
	if not is_ok then
		response:writeln(json_util.illegal_argument())
		return
	end
	
    -- get param
	local utoken = request:get_arg("utoken")
	local uid = tonumber(request:get_arg("uid"))
    -- check sign
	local param = get_param(request)
	-- check param is nil
	is_ok = _.any(_.pick(param,"utoken","uid","fid"),function(v) if v ==nil or ""==v then return  true end   end)
	logger:info(" praised feed  param is ok2 "..tostring(is_ok))
	if is_ok then
		response:writeln(json_util.illegal_argument())
		return
	end
	
	is_ok = check:sign(param)
	logger:info(" praised feed  sign "..tostring(is_ok))
	if not is_ok then
		response:writeln(json_util.illegal_argument())
		return
	end
	
	-- check utoken
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()	
	
	local loginProcess = beanFactory:getBean("loginProcess")
	local ok ,emsg = loginProcess:checktoken(param,redis)
	if not ok then
		redis_util:close(redis)
		response:writeln(json_util.illegal_token(emsg))
		return
	end	
	local feedinfo =  table_util.array_to_hash(redis:hgetall("feed:info:"..param["fid"]))
	if nil == feedinfo or  type(feedinfo) == "userdata" or  _.size(feedinfo) == 0 then 
		redis_util:close(redis)
		response:writeln(json_util.not_found("feed不存在"))
		return
	end
	
	
	local is_del_ok ,err2 = redis:zrem("feed:outbox:"..param["uid"],param["fid"])
	is_del_ok ,err2 = redis:zrem("feed:inbox:"..param["uid"],param["fid"])
	local fans,err1 = redis:zrange("user:followers:"..param["uid"],0,-1) -- SMEMBERS
	redis:init_pipeline()
	underscore.each(fans,function(v) redis:zrem("feed:inbox:"..v,param["fid"])  end)
	local res, err = redis:commit_pipeline()
	
	is_del_ok ,err = redis:del("feed:info:"..param["fid"])
	redis_util:close(redis)
	response:writeln(json_util.success()) 
end


function get_param(request)
	local param = {}
	param["utoken"] =request:get_arg("utoken")  or ""
	param["uid"] =request:get_arg("uid")  or ""
	param["fid"] = request:get_arg("fid")  or ""
	logger:info(cjson.encode(param))
	return param
end