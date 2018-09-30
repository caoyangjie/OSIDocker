--
-- User:fuhao
-- Date: 15/7/13
-- Time: 下午4:05
-- To change this template use File | Settings | File Templates.
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")

function shareurl(request, response)
	 local param = {}

    param["utoken"] = request:get_arg("utoken")
    param["uid"] = request:get_arg("uid")
    param["tplid"] = request:get_arg("tplid")
	
	--[[ 验证sign --]]
	local paramService = beanFactory:getBean("paramService")
	local check_ok = paramService:checkSign(param)
	if not check_ok then
		response:writeln(json_util.fail("参数错误"))
		return
	end

    --[[ 参数检查--]]
    if str_util.isNil(param["utoken"]) or str_util.isNil(param["uid"]) or str_util.isNil(param["tplid"])   then
        response:writeln(json_util.illegal_argument())
        return
    end
	
    --[[ 校验token--]]
    local loginService = beanFactory:getBean("loginService")
    local ok ,emsg = loginService:checkToken(param["uid"], param["utoken"])
    if not ok then
        response:writeln(json_util.illegal_token(emsg))
        return
    end
	
	
    -- 获取redis
    local redis_util = beanFactory:getBean("redis")
    local redis = redis_util:getConnect()

    local data = {}
    local shareurl = redis:hget("tpl:info:"..param["tplid"],'art_url')
    if str_util.isNil(shareurl) then
        response:writeln(json_util.not_found("分享url未配置"))
		return
    else
        data.url = shareurl  or ""
    end

    local result_json = json_util.success(data)
    -- 关闭redis
    redis_util:close(redis)
    response:writeln(result_json)
end