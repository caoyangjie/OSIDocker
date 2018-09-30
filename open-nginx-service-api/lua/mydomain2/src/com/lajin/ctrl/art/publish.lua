--
-- Created by vim.
-- User: fuhao
-- Date: 15/10/14
-- Time: 上午9:56
-- To change this template use File | Settings | File Templates.
-- 发布歌曲
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()
local cjson = require("cjson")
local underscore = require("underscore")
-- 获取歌曲详情
function publish(request, response)

    --[[ 私有参数校验 --]]
    local param = {}
    param["utoken"] = request:get_arg("utoken")
    param["uid"] = request:get_arg("uid")
    param["tplid"] = request:get_arg("tplid")
    param["title"] = request:get_arg("title")
    param["worddesc"] = request:get_arg("worddesc")
    param["picdesc"] = request:get_arg("picdesc")
    param["mid"] = request:get_arg("mid")
    param["p_type"] = request:get_arg("p_type")
	param["is_public"] = request:get_arg("is_public")
	param["status"] = request:get_arg("status")
	param["songname"] = request:get_arg("songname")
	param["singer"] = request:get_arg("singer")
	ngx.log(logger.i("publish param :", cjson.encode(param)))
    --[[ 验证sign --]]
	local paramService = beanFactory:getBean("paramService")
	local check_ok = paramService:checkSign(param)
	if not check_ok then
		response:writeln(json_util.fail("参数错误"))
		return
	end

    --[[ 参数检查--]]
    if str_util.isNil(param["utoken"]) or str_util.isNil(param["uid"]) or str_util.isNil(param["tplid"]) or str_util.isNil(param["title"]) or str_util.isNil(param["mid"]) or str_util.isNil(param["is_public"]) or str_util.isNil(param["status"]) then
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
	local saveResult = saveArtInfo(request)
	if str_util.isNil(saveResult) or _.isEmpty(saveResult) then
		response:writeln(json_util.fail('数据保存失败'))
		return
	end
	
    if saveResult["code"] ~= "A000000" then
        response:writeln(json_util.fail('数据保存失败'))
        return
    end

	local id = saveResult['data']['aid']
	local share_url = saveResult['data']['art_url'] or ''	
	local thum_url = saveResult['data']['thum_url'] or ''
	local thumbnailpx = luastar_config.getConfig("tplpicpx")["thumbnail"]
	if not str_util.isNil(thum_url) and not str_util.isNil(thumbnailpx)  then 
		thum_url = thum_url..thumbnailpx
	end
	
	local share_pic_url = saveResult['data']['thum_url'] or ''
	local share_pic_url_px = luastar_config.getConfig("tplpicpx")["share_pic"]
	if not str_util.isNil(share_pic_url) and not str_util.isNil(share_pic_url_px)  then 
		share_pic_url = share_pic_url..share_pic_url_px
	end
	
	local result_json = json_util.success({aid = id,url = share_url,thum_url = thum_url,share_pic_url=share_pic_url})
	response:writeln(result_json)
end


function saveArtInfo(request)
	local artInfo = {}
	artInfo["utoken"] = request:get_arg("utoken");
    artInfo["uid"] = request:get_arg("uid");
    artInfo["tplid"] = request:get_arg("tplid");
    artInfo["title"] = request:get_arg("title");
    artInfo["worddesc"] = request:get_arg("worddesc");
    artInfo["picdesc"] = request:get_arg("picdesc");
    artInfo["mid"] = request:get_arg("mid");
    artInfo["p_type"] = request:get_arg("p_type");
	artInfo["is_public"] = request:get_arg("is_public")
	artInfo["status"] = request:get_arg("status")
	artInfo["audio"] = request:get_arg("audio")
	artInfo["songname"] = request:get_arg("songname")
	artInfo["singer"] = request:get_arg("singer")
	if not str_util.isNil(artInfo["picdesc"]) then
		ngx.log(logger.i("\n\n\n\n-------picdesc--------------------\n"))
		ngx.log(logger.i(artInfo["picdesc"]))
	    local picdesc = cjson.decode(artInfo["picdesc"])
		_.each(picdesc['pics'],function(k,v)
				local picfile = request:get_arg(v.key)
				ngx.log(logger.i(cjson.encode(picfile)))
				if not _.isEmpty(picfile) then
					if _.isString(picfile) then
						picfile = str_util.encode_url(picfile)
					end
				end
				artInfo[v.key] = picfile
		    end
	    )
	end
	
	if not str_util.isNil(artInfo["worddesc"]) then
		ngx.log(logger.i("\n\n\n\n------worddesc---------------------\n"))
		ngx.log(logger.i(artInfo["worddesc"]))
	    local worddesc = cjson.decode(artInfo["worddesc"])
		_.each(worddesc['words'],function(k,v)				
				   artInfo[v.key] = request:get_arg(v.key)
		    end
	    )
	end

	ngx.log(logger.i(cjson.encode(artInfo)))
	local httpclient = require("luastar.util.httpclient")
	local artSaveUrl = luastar_config.getConfig("apihost")["inner_url"] .. "/inner/app/art/publish"
	ngx.log(logger.i(artSaveUrl))
	local ok, code, headers, status, resbody = httpclient.request(artSaveUrl,"POST",artInfo,1500000,{})
	ngx.log(logger.i(resbody))
	if tonumber(code) ~= 200 or str_util.isNil(resbody) then
		-- response:writeln(json_util.fail('http request error'))
		return
	end
	
	local resbody_table = cjson.decode(resbody)
	return resbody_table
end
