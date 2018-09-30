#!/usr/bin/env lua
--[[
id = {
  class = "",
  arg = {
    {value/ref = ""}
  },
  property = {
    {name = "",value/ref = ""}
  },
  init_method = "",
  single = 0  -- default 1
}
--]]
mysql = {
  class = "luastar.db.mysql",
  arg = {
    {value = "${mysql}"}
  }
}
redis = {
  class = "luastar.db.redis",
  arg = {
    {value = "${redis}"}
  }
}  
  
loginProcess = {
  class = "com.lajin.api.process.loginProcess",
  arg = {
  }    
}

friendProcess = {
  class = "com.lajin.api.process.friendProcess",
  arg = {
  }    
}

recomProcess = {
  class = "com.lajin.api.process.recomProcess",
  arg = {
  }    
}

logDevInfor = {
  class = "com.lajin.api.start.logDevInfor",
  arg = {
  }    
}

check = {
  class = "com.lajin.common.util.check",
  arg = {
  }     
}