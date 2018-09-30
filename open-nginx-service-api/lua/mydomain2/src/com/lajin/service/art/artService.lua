local artService = Class("com.lajin.service.art.artService")
local str_util = require("luastar.util.str")
local table_util = require("luastar.util.table")
local beanFactory = luastar_context.getBeanFactory()
local cjson = require("cjson")
local underscore = require("underscore")

-- 获取歌曲详情
function artService:init(redis_util)
    self.redis_util = redis_util
end

function artService:getArtInfo(aid)
	local redis = self.redis_util:getConnect()
	local art_table = table_util.array_to_hash(redis:hgetall('art:info:'..aid))

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
	
	artInfo.music_info = {mid = mid,song_name= music.songname,song_url=music.songurl}
	artInfo.tplid = art_table.tplid
	artInfo.title = art_table.title
	artInfo.is_public = art_table.is_public
	artInfo.status = art_table.status
	artInfo.created_time = art_table.created_time
	local shareurl = redis:hget("tpl:info:"..art_table.tplid,'art_url')
	artInfo.share_url = shareurl
	
	
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
	-- local tplpx = luastar_config.getConfig("tplpicpx")["tplid_"..art_table.tplid]
	local pics = kvmap(pic_table,function(k,v)
			if 'p_type' ~= k and 'created_time' ~= k and  'updated_time' ~= k then
				local imgInfo = table_util.array_to_hash(redis:hgetall('pub:image:'..(v or '')))
				local picUrl = imgInfo.pic_url or ''
				--[[
				if not str_util.isNil(picUrl) and not str_util.isNil(tplpx) and not str_util.isNil(tplpx[k])  then 
					picUrl = picUrl..tplpx[k]
				end
				--]]
				return {key = k ,picurl= picUrl}
			else
				return nil
			end
		end
		)
	self.redis_util:close(redis)
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


return artService