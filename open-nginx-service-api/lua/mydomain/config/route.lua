--[[
应用路由配置
route = {
  {"url1","file1","method"},
  {"url2","file2","method"}
}
拦截器配置，注：拦截器必须实现beforeHandle和afterHandle方法
interceptor = {
  {url="url1", class="file"},
  {url="url2", class="file", excludes={"url1","url2"}}
}
--]]
route = {
    { "/api/app/test/test1", "com.lajin.api.test.test", "test1" },
    { "/api/app/user/login", "com.lajin.api.user.login", "login" },
    { "/api/app/sms/checkcode", "com.lajin.api.sms.checkcode", "checkcode" },
    { "/api/app/common/upgrade", "com.lajin.api.common.upgrade", "upgrade" },
    { "/api/app/common/privacy", "com.lajin.api.common.privacy", "privacy" },
    { "/api/app/common/service", "com.lajin.api.common.service", "service" },
    { "/api/app/common/copy/sms", "com.lajin.api.common.copy.sms", "sms" },
    { "/api/app/common/copy/weixin", "com.lajin.api.common.copy.weixin", "weixin" },
    { "/api/app/common/problem", "com.lajin.api.common.problem", "problem" },
    { "/api/app/common/about", "com.lajin.api.common.about", "about" },
    { "/api/app/common/aboutinfo", "com.lajin.api.common.aboutinfo", "aboutinfo" },
    { "/api/app/start/imglist", "com.lajin.api.start.imglist", "imglist" },
    { "/api/app/register/imglist", "com.lajin.api.register.imglist", "imglist" },
    { "/api/app/user/feeds/pub", "com.lajin.api.user.feed", "pub" },
    { "/api/app/user/register", "com.lajin.api.user.register", "register" },
    { "/api/app/user/activitys", "com.lajin.api.user.activitys", "activitys" },
    { "/api/app/user/opus", "com.lajin.api.user.opus", "opus" },
    { "/api/app/user/songs", "com.lajin.api.user.songs", "songs" },
    { "/api/app/user/register/check", "com.lajin.api.user.register.check", "check" },
    { "/api/app/user/friendships/addrbook", "com.lajin.api.user.friendships.addrbook", "addrbook" },
    { "/api/app/user/friendships/friends", "com.lajin.api.user.friendships.friends", "friends" },
    { "/api/app/user/friendships/followers", "com.lajin.api.user.friendships.friends", "followers" },
    { "/api/app/user/friendships/followings", "com.lajin.api.user.friendships.friends", "followings" },
    { "/api/app/user/feeds", "com.lajin.api.user.feed", "feeds" },
    { "/api/app/user/interest/friends", "com.lajin.api.user.interest.friends", "friends" },
    { "/api/app/user/friendships/addfriend", "com.lajin.api.user.friendships.addfriend", "addfriend" },
    { "/api/app/user/show", "com.lajin.api.user.show", "show" },
    { "/api/app/user/edit", "com.lajin.api.user.edit", "edit" },
    { "/api/app/user/friendships/addrbook/higherups", "com.lajin.api.user.friendships.addrbook.higherups", "higherups" },
    { "/api/app/user/forgotpwd", "com.lajin.api.user.forgotpwd", "forgotpwd" },
    { "/api/app/user/auth", "com.lajin.api.user.auth", "auth" },
    { "/api/app/user/friendships/create", "com.lajin.api.user.friendships.create", "create" },
    { "/api/app/user/domain_show", "com.lajin.api.user.domainshow", "domainshow" },
    { "/api/app/user/activitys/show", "com.lajin.api.user.activitys.show", "show" },
    { "/api/app/user/activitys/participants", "com.lajin.api.user.activitys.participants", "participants" },
    { "/api/app/user/activitys/friendslist", "com.lajin.api.user.activitys.friendslist", "friendslist" },
    { "/api/app/user/auth/confirm", "com.lajin.api.user.auth.confirm", "confirm" },
    { "/api/app/user/comments/praised", "com.lajin.api.user.comments.praised", "praised" },
    { "/api/app/user/comments/create", "com.lajin.api.user.comments.create", "create" },
	{ "/api/app/user/comments/destroy", "com.lajin.api.user.comments.destroy", "destroy" },
	{ "/api/app/user/feeds/destroy", "com.lajin.api.user.feeds.destroy", "destroy" },
	{ "/api/app/user/activitys/apply", "com.lajin.api.user.activitys.apply", "apply" },
	{ "/api/app/user/feeds/show", "com.lajin.api.user.feeds.show", "show" },
	{ "/api/app/user/feeds/comments", "com.lajin.api.user.feeds.comments", "comments" },
	{ "/api/app/user/feeds/praiseds", "com.lajin.api.user.feeds.praiseds", "praiseds" },
	{ "/api/bce/tc/callback", "com.lajin.api.music.tccallback", "tccallback" },
    { "/api/app/user/msg/num", "com.lajin.api.user.msg.num", "num" },
    { "/api/app/user/msg/import", "com.lajin.api.user.msg.list", "import" },
    { "/api/app/user/msg/ordinary", "com.lajin.api.user.msg.list", "ordinary" },
    { "/api/app/user/msg/recommend", "com.lajin.api.user.msg.list", "recommend" },
    { "/api/app/user/msg/auth", "com.lajin.api.user.msg.list", "auth" },
    { "/api/app/user/intro", "com.lajin.api.user.intro", "intro" },
    { "/api/app/music/info", "com.lajin.api.music.musicinfo", "info" },
    { "/api/app/music/batchinfo", "com.lajin.api.music.musicinfo", "batchinfo" },
    { "/api/app/music/lyrics", "com.lajin.api.music.lyrics", "lyrics" },
	{ "/api/app/report/channel", "com.lajin.api.report.channel", "channel" }
}

interceptor = {
	{
        url = "/api/app",
        class = "com.lajin.common.interceptor.exception"
    },
    {
        url = "/api/app/user/friendships/addrbook",
        class = "com.lajin.common.interceptor.rbookCheckParam"
    },

    {
        url = "/api/app/user/interest/friends",
        class = "com.lajin.common.interceptor.rbookCheckParam"
    }
}