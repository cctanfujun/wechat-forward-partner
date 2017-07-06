package com.xiaochen.robot.wechat.model

/**
 * Created by tanfujun on 5/27/17.
 */

data class WeChatModel(
        val requestHeader: String = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
        var uuid: String = "",
        var redirect_uri: String = "",
        var pass_ticket :String = "",
        var param: Param = Param(),
        var baseUrl:String = "",
        var cookie:String = "",
        var userName:String = ""
)
