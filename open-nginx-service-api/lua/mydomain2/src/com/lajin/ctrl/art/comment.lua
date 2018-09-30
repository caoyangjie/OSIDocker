--
-- Created by vim.
-- User: fuhao
-- Date: 15/10/14
-- Time: 上午9:56
-- To change this template use File | Settings | File Templates.
-- 发布评论
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()
local cjson = require("cjson")
local underscore = require("underscore")
-- 评论
function create(request, response)

    --[[ 私有参数校验 --]]
    local param = {}

    param["wuid"] = request:get_arg("wuid")
    param["wname"] = request:get_arg("wname")
    param["wpic"] = request:get_arg("wpic")
    param["aid"] = request:get_arg("aid")
    param["context"] = request:get_arg("context")
    local callback = request:get_arg("callback")
	
    --[[ 验证sign --]]
	local paramService = beanFactory:getBean("paramService")
	local check_ok = paramService:checkSign(param)
	if not check_ok then
		response:writeln(json_util.jsonp(callback,json_util.fail("参数错误")))
		return
	end

    --[[ 参数检查--]]
    if str_util.isNil(param["wuid"]) or str_util.isNil(param["wpic"]) or str_util.isNil(param["aid"]) or str_util.isNil(param["context"]) then
        response:writeln(json_util.jsonp(callback,json_util.illegal_argument()))
        return
    end
	
	local saveResult = saveCommentInfo(request)
	if str_util.isNil(saveResult) or _.isEmpty(saveResult) then
		response:writeln(json_util.jsonp(callback,json_util.fail('数据保存错误')))
		return
	end
	
    if saveResult["code"] ~= "A000000" then
        response:writeln(json_util.jsonp(callback,json_util.fail('数据保存错误')))
        return
    end
	   
	local result_json = json_util.success({id =  saveResult['data']['id'],position = saveResult['data']['position'],createdTime = saveResult['data']['createdTime']})
	if nil ~= callback and ""~= callback then
		result_json = callback .."("..result_json..")"
	end
	response:writeln(result_json)
end


function saveCommentInfo(request)
	local commentInfo = {}
	commentInfo["wuid"] = request:get_arg("wuid")
    commentInfo["wname"] = request:get_arg("wname")
    commentInfo["wpic"] = request:get_arg("wpic")
    commentInfo["aid"] = request:get_arg("aid")
    commentInfo["context"] = request:get_arg("context")
	--[[
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local position = redis:spop('art:comment:interval:'..commentInfo["aid"]) 
	if nil == position or 'userdata'== type(position) then 
		local count = redis:zcount('art:comment:'..commentInfo["aid"], '-inf', '+inf')
		count = tonumber(count) +1 
		local interval = luastar_config.getConfig("comment_interval")
		print(cjson.encode(interval))
		underscore.each(interval,function(v)
					local min , max = v.min,v.max
					if min <= count and count < max then
						local pos = underscore.range(min,max):to_array()
						print(cjson.encode(pos))
						underscore.each(pos,function(v) 
									redis:sadd('art:comment:interval:'..commentInfo["aid"], v )  
								end
							)
					end
				end
			)
		position = redis:spop('art:comment:interval:'..commentInfo["aid"])
	end
	commentInfo["position"] = position or ''
	--]]
	ngx.log(ngx.INFO,cjson.encode(commentInfo))
	local httpclient = require("luastar.util.httpclient")
	local commentSaveUrl = luastar_config.getConfig("apihost")["inner_url"] .. "/inner/app/art/comment"
	ngx.log(ngx.INFO,commentSaveUrl)
	local ok, code, headers, status, resbody = httpclient.request(commentSaveUrl,"POST",commentInfo,1500000,{})
    ngx.log(ngx.INFO,resbody)
	--[[
	if tonumber(code) ~= 200 or str_util.isNil(resbody) then
		redis:sadd('art:comment:interval:'..commentInfo["aid"], position ) 
		return
	end
	--]]
	
	local resbody_table = cjson.decode(resbody)
	--[[
	if str_util.isNil(saveResult) or _.isEmpty(saveResult) then
		redis:sadd('art:comment:interval:'..commentInfo["aid"], position ) 
		return
	end
	
    if saveResult["code"] ~= "A000000" then
        redis:sadd('art:comment:interval:'..commentInfo["aid"], position ) 
        return
    end
	redis_util:close(redis)
	--]]
	return resbody_table
end




function list(request, response)
--[[ 私有参数校验 --]]
    local param = {}
    param["aid"] = request:get_arg("aid")
	local callback = request:get_arg("callback")
	ngx.log(logger.i("获取评论列表参数：", cjson.encode(param)))
    --[[ 验证sign --]]
	local paramService = beanFactory:getBean("paramService")
	local check_ok = paramService:checkSign(param)
	if not check_ok then
		response:writeln(json_util.jsonp(callback,json_util.fail("参数错误")))
		return
	end

    --[[ 参数检查--]]
    if str_util.isNil(param["aid"]) then
        response:writeln(json_util.jsonp(callback, json_util.illegal_argument()))
        return
    end
	
	--[[ 获得redis链接--]]
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	-- ZRANGEBYSCORE
	local commentid_list = redis:zrangebyscore('art:comment:'..param["aid"],'-inf','+inf','limit',0,12) 
	print(cjson.encode(commentid_list))
	
	local comment_list = underscore.map(commentid_list,function(v)
			    local commentInfo = table_util.array_to_hash(redis:hgetall('art:comment:info:'..v))
				print(cjson.encode(comment))
				if not str_util.isNil(commentInfo) then
					local comment = {}
					comment['aid'] = commentInfo.aid	
					comment['comment_id'] = v
					comment['wuid'] = commentInfo.wuid 
					comment['wname'] = commentInfo.wname or ''
					comment['wpic'] = commentInfo.wpic
					comment['context'] = commentInfo.context or ''
					comment['position'] = commentInfo.position or ''
					comment['pubtime'] = commentInfo.created_time
					return comment
				end
			end
		)
	redis_util:close(redis)
	local res_art = {}
	res_art['comment_list'] = comment_list or {}
    local result_json = json_util.success(res_art)
	result_json = string.gsub(result_json,"{}","[]")
	if nil ~= callback and ""~= callback then
		result_json = callback .."("..result_json..")"
	end
    response:writeln(result_json)
end
