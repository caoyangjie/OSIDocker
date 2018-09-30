local ltn12 = require "ltn12"

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

local gen_request = function(t)
  local boundary = gen_boundary()
  local s = encode(t, boundary)
  return {
    method = "POST",
    source = ltn12.source.string(s),
    headers = {
      ["content-length"] = #s,
      ["content-type"] = fmt("multipart/form-data; boundary=%s", boundary),
    },
  }
end

local gen_post_file_req = function(t)
  local boundary = gen_boundary()
  local s = encode(t, boundary)
  local  source = ltn12.source.string(s)
  local headers  = {
      ["content-length"] = #s,
      ["content-type"] = fmt("multipart/form-data; boundary=%s", boundary),
    } 
  return source,headers
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
	local body=""
	local t_arr = _.map(t, function(k, v) return k .. "=" .. v end)
    table.concat(t_arr, "&")
end

local gen_post_info1 = function(t)
  local boundary = gen_boundary()
  local s = encode(t, boundary)
  local  source = ltn12.source.string(s)
  local headers  = {
      ["content-length"] = #s,
      ["content-type"] = fmt("multipart/form-data; boundary=%s", boundary),
    } 
  return s,headers
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

local lua_http_post = function(post_url,data)
   local http = require("socket.http")
   local source1,header = gen_post_file_req(data)
   local resUpload = {}   
   http.request({
    method = "POST",
    url = post_url,
    headers = header,
    source = source1,
    sink = ltn12.sink.table(resUpload)
    })
   -- logger:info(cjson.encode(resUpload))
      return resUpload[1]   
end

return {
  encode = encode,
  gen_request = gen_request,
  gen_post_file_req = gen_post_file_req,
  lua_http_post  = lua_http_post,
  gen_post_info = gen_post_info,
  gen_post_info1 = gen_post_info1   
}
