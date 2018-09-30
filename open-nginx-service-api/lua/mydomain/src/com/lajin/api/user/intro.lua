--
-- 获取大咖个人简介
--
module(..., package.seeall)

local logger = luastar_log.getLog()
local json_util = require("com.lajin.common.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()

function intro(request, response)
	local check = beanFactory:getBean("check")
	local isHead = check:head()
	--[[ head参数校验--]]
	if not isHead then
		logger:info(" Head  args is null")
		response:writeln(json_util.illegal_argument())
		return
	end
	
	--[[ 私有参数校验 --]]
	local param = {}
	param["utoken"] = request:get_arg("utoken");
	param["uid"] = request:get_arg("uid");
	param["ouid"] = request:get_arg("ouid");

	--[[ 参数检查--]]
	if str_util.isnull(param["utoken"]) or str_util.isnull(param["uid"]) then
		logger:info("utoken or uid is null")
		response:writeln(json_util.illegal_argument())
		return
	end

	--[[ 验证sign --]]
	local isSign = check:sign(param)
	if not isSign then
		logger:info(" isSign error")
		response:writeln(json_util.fail("sign error"))
		return
	end

	--[[ 获得redis链接--]]
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()

	--[[ 校验token--]]
	local loginProcess = beanFactory:getBean("loginProcess")
	local ok ,emsg = loginProcess:checktoken(param,redis)
	if not ok then
		redis_util:close(redis)
		response:writeln(json_util.illegal_token(emsg))
		return
	end

    -- 业务代码start
    local uid = param["ouid"] or param["uid"]
    local data = {}
	data.username = ""
	data.picurl = ""
	data.homepic = ""
	data.desc = ""
	data.opus = ""
	data.honor = ""
    local userInfo = table_util.array_to_hash(redis:hgetall("user:info:"..uid))
    if str_util.isnull(userInfo) then
        logger:info("userInfo is nil,uid="..uid)
    else
        data.username = userInfo.username or ""
        data.picurl = userInfo.picurl or ""
        data.homepic = userInfo.homepic or ""
        data.desc = userInfo.desc or ""
    end
    local misiInfo = table_util.array_to_hash(redis:hgetall("musician:info:"..uid))
    if str_util.isnull(misiInfo) then
        logger:info("musician is nil,uid="..uid)
    else
        data.opus = misiInfo.opus or ""
        data.honor = misiInfo.honor or ""
    end
    local result_json = json_util.formatesuccess(data)
    -- 关闭redis
    redis_util:close(redis)
    response:writeln(result_json)
end

