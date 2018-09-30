#!   /usr/bin/env lua
--[[
	修改作品
--]]
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local httpclient = require("luastar.util.httpclient")
local beanFactory = luastar_context.getBeanFactory()
local str_util = require("luastar.util.str")
local table_util = require("luastar.util.table")

function edit(request, response)
    -- 参数校验
    local param = {}
    param["uid"] = request:get_arg("uid")
    param["utoken"] = request:get_arg("utoken")
    param["aid"] = request:get_arg("aid")
	param["is_public"] = request:get_arg("is_public")
	
   
    ngx.log(logger.i("修改作品输入参数：", cjson.encode(param)))
    local is_ok = _.any(param, function(v) if _.isEmpty(v) then return true end end)
    if is_ok then
        response:writeln(json_util.illegal_argument())
        return
    end
     --[[ 验证sign --]]
	local paramService = beanFactory:getBean("paramService")
	local check_ok = paramService:checkSign(param)
	if not check_ok then
		response:writeln(json_util.fail("参数错误"))
		return
	end
	
	--[[ 校验token--]]
    local loginService = beanFactory:getBean("loginService")
    local ok ,emsg = loginService:checkToken(param["uid"], param["utoken"])
    if not ok then
        response:writeln(json_util.illegal_token(emsg))
        return
    end
	
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local art_table = table_util.array_to_hash(redis:hgetall('art:info:'..param["aid"]))
	if str_util.isNil(art_table)  or  param["uid"] ~= art_table.uid then
		redis_util:close(redis)
		response:writeln(json_util.illegal_argument('作品修改失败'))
        return
	end
	redis_util:close(redis)
	-- 调用内网接口保存用户
    local editArtUrl = luastar_config.getConfig("apihost")["inner_url"] .. "/inner/app/art/edit"
    local ok, code, resheaders, status, resbody = httpclient.request(editArtUrl, "POST", {aid = param["aid"],is_public = param['is_public']}, 1500000, {})
    if not ok or _.isEmpty(resbody) then
        response:writeln(json_util.fail("数据保存失败"))
        return
    end
    local delResult = cjson.decode(resbody)
    if _.isEmpty(delResult) then
        response:writeln(json_util.fail("数据保存失败"))
        return
    end
    if delResult["code"] ~= "A000000" then
        response:writeln(json_util.fail('数据保存失败'))
        return
    end
    response:writeln(json_util.success())
end


