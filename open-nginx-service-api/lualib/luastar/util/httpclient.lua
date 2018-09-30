module(..., package.seeall)

local logger = luastar_log.getLog()
local cjson = require("cjson")
local fmt = function(p, ...)
  if select('#', ...) == 0 then
    return p
  else return string.format(p, ...) end
end

local tprintf = function(t, p, ...)
  t[#t+1] = fmt(p, ...)
end

local append_data = function(r, k, data, extra)
  tprintf(r, "content-disposition: form-data; name=\"%s\"", k)
  if extra.filename then
    tprintf(r, "; filename=\"%s\"", extra.filename)
  end
  if extra.content_type then
    tprintf(r, "\r\ncontent-type: %s", extra.content_type)
  end
  if extra.content_transfer_encoding then
    tprintf(
      r, "\r\ncontent-transfer-encoding: %s",
      extra.content_transfer_encoding
    )
  end
  tprintf(r, "\r\n\r\n")
  tprintf(r, data)
  tprintf(r, "\r\n")
end

local gen_boundary = function()
  local t = {"BOUNDARY-"}
  for i=2,17 do t[i] = string.char(math.random(65, 90)) end
  t[18] = "-BOUNDARY"
  return table.concat(t)
end

local encode = function(t, boundary)
  local r = {}
  local _t
  for k,v in pairs(t) do
    tprintf(r, "--%s\r\n", boundary)
    _t = type(v)
    if _t == "string" then
      append_data(r, k, v, {})
    elseif _t == "table" then
      assert(v.data, "invalid input") 
      local extra = {
        filename = v.filename or v.name,
        content_type = v.content_type or v.mimetype
          or "application/octet-stream",
        content_transfer_encoding = v.content_transfer_encoding or "binary",
      }
      append_data(r, k, v.data, extra)
    else error(string.format("unexpected type %s", _t)) end
  end
  tprintf(r, "--%s--\r\n", boundary)
  return table.concat(r)
end

local hasfile = function(t)
	local is_has_file = false
	for k,v in pairs(t) do
		if type(v)== "table"  then
			is_has_file = true
			break
		end
	end
	return is_has_file
end

local get_common_body = function(t)
	local body={}
	for k,v in pairs(t) do
	    body[#body+1] = k .. "=" .. v
	end
    return table.concat(body, "&")
end

local gen_post_info = function(t)
	local body,contentlength,contenttype
  if hasfile(t) then
	local boundary = gen_boundary()
	body = encode(t, boundary)
	contentlength = #body
	contenttype = fmt("multipart/form-data; boundary=%s", boundary)
  else 
	body = get_common_body(t)
	contentlength = string.len(body)
	contenttype = "application/x-www-form-urlencoded"
	
  end
  
  return body,contentlength,contenttype
end

function request(requrl,reqmethod,reqparam,reqtimeout,reqhead)
	local http = require "resty.http"
    local hc = http:new()
	--local gen_post_info = (require "multipart-post").gen_post_info
	local reqbody,contentlength,contenttype = gen_post_info(reqparam)
	-- logger:info(cjson.encode(reqbody))
	if nil ~= reqhead then
		reqhead["content-length"]=contentlength
		reqhead["content-type"]=contenttype
	else
		reqhead = {
					["content-length"] = contentlength,
					["content-type"] =contenttype,
				} 
	end
	logger:info(cjson.encode(reqhead))
    local ok, code, resheaders, status, resbody  = hc:request {
                url = requrl,
                --- proxy = "http://127.0.0.1:8888",
                timeout =reqtimeout,
                --- scheme = 'https',
                method = reqmethod, -- POST or GET
                -- add post content-type and cookie
                headers = reqhead,
                body = reqbody,
            }
			
	logger:info(ok)
    logger:info(code)
	logger:info(resheaders)
    logger:info(resbody)
	return  ok, code, resheaders, status, resbody
end