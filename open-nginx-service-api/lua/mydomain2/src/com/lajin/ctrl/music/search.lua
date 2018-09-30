--
-- Created by vim.
-- User: fuhao
-- Date: 15/10/14
-- Time: 上午9:56
-- To change this template use File | Settings | File Templates.
-- 搜索歌曲
module(..., package.seeall)

local json_util = require("com.lajin.util.json")
local table_util = require("luastar.util.table")
local str_util = require("luastar.util.str")
local beanFactory = luastar_context.getBeanFactory()
local cjson = require("cjson")
local underscore = require("underscore")
-- 获取歌曲详情
function search(request, response)

    --[[ 私有参数校验 --]]
    local param = {}
    param["sname"] = request:get_arg("sname");
	param["offset"] = request:get_arg("offset",1);
	param["limit"] = request:get_arg("limit",20);

    --[[ 参数检查--]]
    if str_util.isNil(param["sname"]) then
        response:writeln(json_util.illegal_argument())
        return
    end
    -- 业务代码start

    local sname = param["sname"]
    local music = search_musicinfo_by_name(sname,tonumber(param["offset"]),tonumber(param["limit"]))
    
    if str_util.isNil(music) then
        return response:writeln(json_util.fail('音乐不存在'))
    end
	
	local songs = music['result']['songs']
	if nil == songs then
		return response:writeln(json_util.exp())
	end
	local res_music = underscore.map(songs,function(v)
			local musicinfo = {} 
			-- musicinfo['songurl'] = v['mp3Url']
			musicinfo['mid'] = v['id']
			musicinfo['songname'] = v['name']
			local artists = v['artists']
			musicinfo['singer'] = artists[1]['name']
			musicinfo['album'] = v['album']['name']
			musicinfo['albumid'] = v['album']['id']
			return musicinfo
		end
	)
	
	local result_json = json_util.success(res_music)
    response:writeln(result_json)
end


function search_musicinfo_by_name(sname,offset,limit)
	local httpclient = require("luastar.util.httpclient")
	local s_param = {}
	s_param['s'] = sname
	s_param['type'] = 1
	s_param['offset'] = offset-1 or 0
	s_param['sub'] = 'false'
	s_param['limit'] = limit or 20
	
	local header = {}
	header['Cookie'] = 'appver=2.0.2'
	header['Referer'] = 'http://music.163.com'
	
	local ok, code, headers, status, resbody = httpclient.request(luastar_config.getConfig('wyapi')['search_url'],"POST",s_param,1500000,header)
    ngx.log(ngx.INFO,resbody)
	if tonumber(code) ~= 200 or str_util.isNil(resbody) then
		-- response:writeln(json_util.fail('http request error'))
		return
	end
	
	local resbody_table = cjson.decode(resbody)
	return resbody_table
end

-- search_musicinfo_by_name('寂寞难耐')

function search_xiami(request, response)
	local httpclient = require("luastar.util.httpclient")
	url = 'http://www.xiami.com/search' -- ?key=%E5%8D%81%E5%B9%B4
	sname = '十年'
	local header = {}
	header['User-Agent'] = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36'
	header['Referer'] = 'http://www.xiami.com/'
	local ok, code, headers, status, resbody = httpclient.request(url,"POST",{key=sname},1500000,header)
    -- ngx.log(ngx.INFO,resbody)
	if tonumber(code) ~= 200 or str_util.isNil(resbody) then
		response:writeln(json_util.fail('音乐信息不存在'))
		return
	end
	local cjson = require('cjson')
	cjson.encode_max_depth(9000)
	local html = require "html"
	root = html.parsestr(resbody)
	print(cjson.encode(root))

	--[[
	local htmlparser = require("htmlparser")
	local root = htmlparser.parse(resbody)
	print(type(root))
	local elements = root:select('.track_list')
	
	for _,e in ipairs(elements) do
		print(e.class)
	end
	]]--
	ngx.log(ngx.INFO,'------------------------------------')
	
	response:writeln(resbody)
end