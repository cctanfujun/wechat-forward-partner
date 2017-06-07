package com.xiaochen.robot.wechat.model

/**
 * Created by tanfujun on 5/27/17.
 */

data class WeChatModel(
        val requestHeader: String = "Mozilla/5.0 (iPad; CPU OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
        var uuid: String = "",
        var redirect_uri: String = "",
        var pass_ticket :String = "",
        var param: Param = Param(),
        var baseUrl:String = "",
        var cookie:String = "",
        var userName:String = ""




)
