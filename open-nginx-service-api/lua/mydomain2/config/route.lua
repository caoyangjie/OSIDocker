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
    { "/api/test/test1", "com.lajin.ctrl.test.test", "test1" },
    { "/api/common/appstart", "com.lajin.ctrl.common.appstart", "start" },
    { "/api/common/smscode", "com.lajin.ctrl.common.smscode", "smscode" },
    { "/api/config/service", "com.lajin.ctrl.config.service", "service" },
    { "/api/config/privacy", "com.lajin.ctrl.config.privacy", "privacy" },
    { "/api/config/upgrade", "com.lajin.ctrl.config.upgrade", "upgrade" },
    { "/api/config/about", "com.lajin.ctrl.config.about", "about" },
    { "/api/config/carousel", "com.lajin.ctrl.config.carousel", "carousel" },
	{ "/api/v2/config/carousel", "com.lajin.ctrl.config.carousel", "recCarousel" },
	{ "/api/v2/nav", "com.lajin.ctrl.nav.nav", "nav" },
	{ "/api/v2/rec", "com.lajin.ctrl.rec.homerec", "home_rec" },
    { "/api/config/feedback", "com.lajin.ctrl.config.feedback", "feedback" },
    { "/api/user/register", "com.lajin.ctrl.user.register", "register" },
    { "/api/user/login", "com.lajin.ctrl.user.login", "login" },
    { "/api/user/third_login", "com.lajin.ctrl.user.login", "thirdlogin" },
    { "/api/user/get_info", "com.lajin.ctrl.user.userinfo", "getinfo" },
    { "/api/user/edit_info", "com.lajin.ctrl.user.userinfo", "editinfo" },
    { "/api/user/edit_passwd", "com.lajin.ctrl.user.userinfo", "editpasswd" },
    { "/api/music/search", "com.lajin.ctrl.music.search", "search" },
    { "/api/music/info", "com.lajin.ctrl.music.musicinfo", "detail" },
    { "/api/music/lyrics", "com.lajin.ctrl.music.lyrics", "lyrics" },
    { "/api/music/lyrics/file", "com.lajin.ctrl.music.lyrics", "lyrics_file" },
    { "/api/music/rec_list", "com.lajin.ctrl.rec.music", "recmusic" },
    { "/api/art/publish", "com.lajin.ctrl.art.publish", "publish" },
    { "/api/art/url", "com.lajin.ctrl.art.shareurl", "shareurl" },
    { "/api/art/del", "com.lajin.ctrl.art.delArt", "delArt" },
    { "/api/art/edit", "com.lajin.ctrl.art.edit", "edit" },
    { "/api/art/opus", "com.lajin.ctrl.art.opus", "opus" },
    { "/api/art/info", "com.lajin.ctrl.art.show", "show" },
    { "/api/art/comment", "com.lajin.ctrl.art.comment", "create" },
    { "/api/art/comment/list", "com.lajin.ctrl.art.comment", "list" },
    { "/api/art/hot/add", "com.lajin.ctrl.art.hot", "add" },
    { "/api/tpl/word/tag", "com.lajin.ctrl.rec.tag.word", "tplwordtag" },
    { "/api/tpl/pic/tag", "com.lajin.ctrl.rec.tag.pic", "tplpictag" },
	{ "/api/v2/rec/tag", "com.lajin.ctrl.rec.tag.findtag", "find_tag" },
	{ "/api/v2/rec/music_custom", "com.lajin.ctrl.rec.music_custom", "music_custom" },
	{ "/api/v2/rec/musicians", "com.lajin.ctrl.rec.music_custom", "music_custom" },
    { "/api/tpl/word", "com.lajin.ctrl.rec.tplword", "tplword" },
    { "/api/tpl/pic", "com.lajin.ctrl.rec.tplpic", "tplpic" },
    { "/api/rec/art/hot", "com.lajin.ctrl.rec.art.hot", "art_hot" },
    { "/api/rec/art/new", "com.lajin.ctrl.rec.art.new", "art_new" },
    { "/api/weixin/userinfo", "com.lajin.ctrl.weixin.userinfo", "userinfo" },
    { "/api/weixin/sdkconfig", "com.lajin.ctrl.weixin.sdktoken", "sdkconfig" }
}

interceptor = {
    {
        url = "/api",
        class = "com.lajin.interceptor.common"
    }
}