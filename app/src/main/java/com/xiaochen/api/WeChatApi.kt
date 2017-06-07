package com.xiaochen.api

import com.xiaochen.blade.blade.kit.base.DateKit
import com.xiaochen.robot.wechat.Constant
import com.xiaochen.robot.wechat.model.ContactResponse
import com.xiaochen.robot.wechat.model.WxInitResponse
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

    @POST
    fun wxInit(
            @Url url: String,
            @Body params: String,
            @Header("Cookie") cookie: String,
            @Header("Content-Type") contentType: String,
            @Query("r") r: String = DateKit.getCurrentUnixTime().toString(),
            @Query("pass_ticket") pass_ticket: String,
            @Query("skey") skey: String


    ): Observable<WxInitResponse>

    @POST
    fun openNotify(
            @Url url: String,
            @Body params: String,
            @Header("Cookie") cookie: String,
            @Header("Content-Type") contentType: String,
            @Query("lang") r: String = "zh_CN",
            @Query("pass_ticket") pass_ticket: String


    ): Observable<String>

    @POST
    fun getContacts(
            @Url url: String,
            @Body params: String,
            @Header("Cookie") cookie: String,
            @Header("Content-Type") contentType: String,
            @Query("r") r: String = DateKit.getCurrentUnixTime().toString(),
            @Query("pass_ticket") pass_ticket: String,
            @Query("skey") skey: String
    ): Observable<ContactResponse>



    @POST
    fun sendMessage(
            @Url url: String,
            @Body params: String,
            @Header("Cookie") cookie: String,
            @Header("Content-Type") contentType: String,
            @Query("r") r: String = DateKit.getCurrentUnixTime().toString(),
            @Query("pass_ticket") pass_ticket: String,
            @Query("skey") skey: String
    ): Observable<ContactResponse>







    @GET
    fun request(@Url url: String): Observable<String>


}
