#! /usr/bin/env lua
--[[

--]]
local logger = luastar_log.getLog()

local Response = Class("luastar.core.Response")

function Response:init()
    logger:info("[Response:init] start.")
    self.headers = ngx.header
    self._output = {}
    self._cookies = {}
    self._eof = false
end

function Response:write(content)
    if self._eof then
        logger:warn("response has been explicitly finished before.")
        return
    end
    table.insert(self._output, content)
end

function Response:writeln(content)
    if self._eof then
        logger:warn("response has been explicitly finished before.")
        return
    end
    table.insert(self._output, content)
    table.insert(self._output, "\r\n")
end

function Response:redirect(url, status)
    ngx.redirect(url, status)
end

function Response:set_cookie(key, value, encrypt, duration, path)
    local cookie = self:_set_cookie(key, value, encrypt, duration, path)
    self._cookies[key] = cookie
    ngx.header["Set-Cookie"] = _.values(self._cookies)
end

function Response:_set_cookie(key, value, encrypt, duration, path)
    if not value then return nil end
    if not key or key == "" or not value then
        return
    end
    if not duration or duration <= 0 then
        duration = 604800 -- 7 days, 7*24*60*60 seconds
    end
    if not path or path == "" then
        path = "/"
    end
    if value and value ~= "" and encrypt == true then
        value = ndk.set_var.set_encrypt_session(value)
        value = ndk.set_var.set_encode_base64(value)
    end
    local expiretime = ngx.time() + duration
    expiretime = ngx.cookie_time(expiretime)
    return table_concat({ key, "=", value, "; expires=", expiretime, "; path=", path })
end

function Response:error(info)
    if self._eof then
        logger:error("response has been explicitly finished before.")
        return
    end
    ngx.status = 500
    self.headers['Content-Type'] = 'text/html; charset=utf-8'
    self:write(info)
end

function Response:finish()
    if self._eof then
        return
    end
    self._eof = true
    ngx.print(self._output)
    self._output = nil
    local ok, ret = pcall(ngx.eof)
    if not ok then
        logger:error("ngx.eof() error:" .. ret)
    end
end

return Response