#!/usr/bin/env lua
--[[

--]]
local user = {}
function user:new()
	local o = {}
	o["uid"]=''
	o["utoken"]=''
	o["phone"]=''
	o["username"]=''
	o["namereal"]=''
	o["sex"]=''
	o["birthday"]=''
	o["height"]=''
	o["bloodtype"]=''
	o["country"]=''
	o["province"]=''
	o["city"]=''
	o["picurl"]=''
	o["homepic"]=''
	o["usertype"]=''
	o["userrole"]=''
	o["thirdsrc"]=''
	o["thirduid"]=''
	o["lastlogintime"]=''
	setmetatable(o,self)
	self.__index=self
	return o
end
return user
