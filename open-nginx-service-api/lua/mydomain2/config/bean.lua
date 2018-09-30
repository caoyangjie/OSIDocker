#!     /usr/bin/env lua
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
        { value = "${mysql}" }
    }
}
redis = {
    class = "luastar.db.redis",
    arg = {
        { value = "${redis}" }
    }
}
paramService = {
    class = "com.lajin.service.common.paramService"
}
smsService = {
    class = "com.lajin.service.common.smsService",
    arg = { { ref = "redis" } }
}
upgradeService = {
    class = "com.lajin.service.config.upgradeService",
    arg = { { ref = "redis" } }
}
userService = {
    class = "com.lajin.service.user.userService",
    arg = { { ref = "redis" } }
}
loginService = {
    class = "com.lajin.service.user.loginService",
    arg = { { ref = "redis" }, { ref = "userService" } }
}
picService = {
    class = "com.lajin.service.user.picService",
    arg = { { ref = "redis" } }
}

musicService = {
    class = "com.lajin.service.music.musicService",
    arg = { { ref = "redis" } }
}
artService = {
    class = "com.lajin.service.art.artService",
    arg = { { ref = "redis" } }
}


