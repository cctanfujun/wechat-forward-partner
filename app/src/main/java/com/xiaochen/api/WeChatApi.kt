package com.xiaochen.api

import com.xiaochen.blade.blade.kit.base.DateKit
import com.xiaochen.robot.wechat.Constant
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by tanfujun on 5/25/17.
 */

interface WeChatApi {


    @GET(Constant.JS_LOGIN_URL)
    fun getUUid(
            @Query("appid") appid: String = "wx782c26e4c19acffb",
            @Query("fun") funp: String = "new",
            @Query("lang") lang: String = "zh_CN",
            @Query("_") time: String = DateKit.getCurrentUnixTime().toString()

    ): Observable<String>

    @FormUrlEncoded
    @POST("https://login.weixin.qq.com/qrcode/{uuid}")
    fun getQRIMG(
            @Path("uuid") uuid: String,
            @Field("t") t: String = "webwx",
            @Field("_") time: String = DateKit.getCurrentUnixTime().toString()
    ): Observable<ResponseBody>

    @GET("https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login")
    fun login(
            @Header("User-Agent") header: String,
            @Query("tip") tip: Int = 0,
            @Query("uuid", encoded = false) uuid: String,
            @Query("_") time: String = DateKit.getCurrentUnixTime().toString()
    ): Observable<String>

    @FormUrlEncoded
    @POST("https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxinit")
    fun wxInit(@Field("params") t: String = "webwx",)

    @GET
    fun request(@Url url: String):Observable<String>




}
