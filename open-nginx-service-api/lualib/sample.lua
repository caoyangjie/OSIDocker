local _M = {}

function _M:hello(str)
	ngx.say('hello', str)
end

function _M:world(str)
	ngx.say('world', str)
end

return _M
