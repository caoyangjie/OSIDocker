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
function show(request, response)

    --[[ 私有参数校验 --]]
    local param = {}
    param["aid"] = request:get_arg("aid")
    param["utoken"] = request:get_arg("utoken")
    param["uid"] = request:get_arg("uid")
	param["callback"]= request:get_arg("callback");
	
    --[[ 验证sign --]]
	local paramService = beanFactory:getBean("paramService")
	local check_ok = paramService:checkSign(param)
	if not check_ok then
		response:writeln(json_util.fail("参数错误"))
		return
	end

    --[[ 参数检查--]]
    if  str_util.isNil(param["aid"])    then
        response:writeln(json_util.illegal_argument())
        return
    end
	
	--[[ 校验token--]]
	if not str_util.isNil(param["utoken"]) and not str_util.isNil(param["uid"]) then
		local loginService = beanFactory:getBean("loginService")
		local ok ,emsg = loginService:checkToken(param["uid"], param["utoken"])
		if not ok then
			response:writeln(json_util.illegal_token(emsg))
			return
		end
	end
	
	
    
	local artInfo = getArtInfo(request)
	if str_util.isNil(artInfo) or _.isEmpty(artInfo) then
		response:writeln(json_util.fail('作品不存在'))
		return
	end
	
	local result_json = json_util.success(artInfo)
	if nil ~= param["callback"] and ""~= param["callback"] then
		result_json = param["callback"] .."("..result_json..")"
	end
    response:writeln(result_json)
end


function getArtInfo(request)
	local aid = request:get_arg("aid");
	local redis_util = beanFactory:getBean("redis")
	local redis = redis_util:getConnect()
	local art_table = table_util.array_to_hash(redis:hgetall('art:info:'..aid))
	if  str_util.isNil(request:get_arg("utoken"))  or  str_util.isNil(request:get_arg("uid")) then
		if tonumber(art_table.is_public) == 0 then  -- 未公开的作品在非app下 不显示 or tonumber(art_table.status) ~=2 
			return {}
		end
	end
	
	if str_util.isNil(art_table) then
		return {}
	end

	local artInfo = {}
	artInfo.aid = aid
	local uid = art_table.uid
	local userService =  beanFactory:getBean("userService")
	local userInfo = userService:getUserInfo(uid)
	artInfo.pubuserinfo = {uid =art_table.uid,username =userInfo.username ,picurl =userInfo.picurl  }	
	
	local mid = art_table.mid
	local musicService = beanFactory:getBean("musicService")
	local music = musicService:get_music_detail_by_id(mid)
	print(cjson.encode(music))
	
	artInfo.music_info = {mid = mid,song_name= music.songname,song_url=music.songurl,singer=music.singer}
	artInfo.tplid = art_table.tplid
	artInfo.title = art_table.title
	artInfo.is_public = art_table.is_public
	artInfo.status = art_table.status
	artInfo.created_time = art_table.created_time
	local shareurl = redis:hget("tpl:info:"..art_table.tplid,'art_url')
	artInfo.share_url = shareurl .. "?aid=" .. aid
	-- local audio_id = art_table.audio_id or ''
	local audioInfo = table_util.array_to_hash(redis:hgetall('audio:info:'..(art_table.audio_id or '')))
	
	local paramService = beanFactory:getBean("paramService")
	local headParam = paramService:getHeadParam()
	artInfo.audio_url = audioInfo.trans_url or ''
	if 2==tonumber(headParam['ostype']) then
		artInfo.audio_url = audioInfo.play_url or ''
	end
	
	local thumbImgInfo = table_util.array_to_hash(redis:hgetall('pub:image:'..(art_table.thumbnail_id or '')))
	local thumbnailpx = luastar_config.getConfig("tplpicpx")["thumbnail"]
	local thumbnail = thumbImgInfo.pic_url or ''
	if not str_util.isNil(thumbnail) and not str_util.isNil(thumbnailpx)  then 
		thumbnail = thumbnail..thumbnailpx
	end
	artInfo.thumbnail = thumbnail
	
	local share_pic_url = thumbImgInfo.pic_url or ''
	local share_pic_url_px = luastar_config.getConfig("tplpicpx")["share_pic"]
	if not str_util.isNil(share_pic_url) and not str_util.isNil(share_pic_url_px)  then 
		share_pic_url = share_pic_url..share_pic_url_px
	end
	
	artInfo.share_pic_url = share_pic_url
	
	local words_table = table_util.array_to_hash(redis:hgetall('art:info:word:'..aid))
	local words = kvmap(words_table,function(k,v)
			if 'w_type' ~= k and 'created_time' ~= k and  'updated_time' ~= k then
				return {key = k ,word=v}
			else
				return nil
			end
		end
		)
	artInfo.words= words
	
	local pic_table = table_util.array_to_hash(redis:hgetall('art:info:pic:'..aid))
	local tplpx = luastar_config.getConfig("tplpicpx")["tplid_"..art_table.tplid]
	local pics = kvmap(pic_table,function(k,v)
			if 'p_type' ~= k and 'created_time' ~= k and  'updated_time' ~= k then
				local imgInfo = table_util.array_to_hash(redis:hgetall('pub:image:'..(v or '')))
				local picUrl = imgInfo.pic_url or ''
				if not str_util.isNil(picUrl) and not str_util.isNil(tplpx) and not str_util.isNil(tplpx[k])  then 
					picUrl = picUrl..tplpx[k]
				end
				return {key = k ,picurl= picUrl}
			else
				return nil
			end
		end
		)
	redis_util:close(redis)
	artInfo.pics= pics
	
	
	return artInfo
end


function  kvmap(list_or_iter,f)
	local t = {}
	for index,value in pairs(list_or_iter) do
		t[#t +1 ] = f(index,value)
	end
	return t
end