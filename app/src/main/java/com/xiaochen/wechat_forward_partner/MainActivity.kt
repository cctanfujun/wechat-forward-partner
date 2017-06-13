package com.xiaochen.wechat_forward_partner

import android.app.Activity
import android.os.Bundle
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.xiaochen.App
import com.xiaochen.App.Companion.wechatModel
import com.xiaochen.api.WeChatApi
import com.xiaochen.blade.blade.kit.base.DateKit
import com.xiaochen.blade.blade.kit.base.StringKit
import com.xiaochen.robot.wechat.model.ContactResponse
import com.xiaochen.robot.wechat.util.CookieUtil
import com.xiaochen.robot.wechat.util.Matchers
import com.xiaochen.robot.wechat.util.MyCookieCache
import com.xiaochen.robot.wechat.util.MyCookieJar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val imageView = findViewById(R.id.qr_img) as ImageView

        val cookieJar: CookieJar = MyCookieJar(MyCookieCache(), SharedPrefsCookiePersistor(applicationContext))


        val gson = GsonBuilder().setLenient().create()


        val cookieretrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("https://login.weixin.qq.com/")
                .client(OkHttpClient.Builder()
                        .addNetworkInterceptor(StethoInterceptor())
                        .cookieJar(cookieJar)
                        .build()
                )
                .build()

        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("https://login.weixin.qq.com/")
                .client(OkHttpClient.Builder()
                        .addNetworkInterceptor(StethoInterceptor())
                        .build()
                )
                .build()

        val cookieApi = cookieretrofit.create(WeChatApi::class.java)
        val api = retrofit.create(WeChatApi::class.java)


        api.getUUid(wechatModel.requestHeader)
                .subscribeOn(Schedulers.io())
                .map {
                    resStr ->
                    Matchers.match("window.QRLogin.uuid = \"(.*)\";", resStr)
                }
                .flatMap {
                    uuid ->
                    wechatModel.uuid = uuid
                    api.getQRIMG(wechatModel.requestHeader,uuid)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }
                .map {
                    response ->
                    writeResponseBodyToDisk(response.byteStream())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    file ->
                    Glide.with(this@MainActivity)
                            .load(file)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(imageView)

                }
                .subscribe()


        login.setOnClickListener {
            api.login(0, wechatModel.uuid)
                    .subscribeOn(Schedulers.io())
                    .filter { res ->
                        test(res)
                        val code = Matchers.match("window.code=(\\d+);", res)
                        code == "200";
                    }
                    .flatMap {
                        res ->
                        val pm = Matchers.match("window.redirect_uri=\"(\\S+?)\";", res)
                        val redirect_uri = pm + "&fun=new"
                        wechatModel.redirect_uri = redirect_uri;
                        wechatModel.baseUrl = redirect_uri.substring(0, redirect_uri.lastIndexOf("/"))
                        cookieApi.request(redirect_uri)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                    }
                    .flatMap { res ->
                        test(res)

                        val cookie = CookieUtil.getCookie(cookieJar as MyCookieJar)
                        wechatModel.cookie = cookie

                        wechatModel.param.BaseRequest.Skey = Matchers.match("<skey>(\\S+)</skey>", res)
                        wechatModel.param.BaseRequest.Sid = Matchers.match("<wxsid>(\\S+)</wxsid>", res)
                        wechatModel.param.BaseRequest.Uin = Matchers.match("<wxuin>(\\S+)</wxuin>", res)
                        wechatModel.param.BaseRequest.DeviceID = "e" + DateKit.getCurrentUnixTime()
                        wechatModel.pass_ticket = Matchers.match("<pass_ticket>(\\S+)</pass_ticket>", res)

                        val gson = Gson()
                        val stringParams = gson.toJson(wechatModel.param)


                        val initurl = wechatModel.baseUrl + "/webwxinit"
                        api.wxInit(
                                initurl,
                                stringParams,
                                wechatModel.cookie,
                                "application/json;charset=utf-8",
                                DateKit.getCurrentUnixTime().toString(),
                                wechatModel.pass_ticket,
                                wechatModel.param.BaseRequest.Skey
                        )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())

                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap {
                        response ->

                        wechatModel.userName = response.User.UserName
                        test(Gson().toJson(response).toString())
                        val openNotifyurl = wechatModel.baseUrl + "/webwxstatusnotify"

                        val gson = Gson()
                        val stringParams = gson.toJson(wechatModel.param)

                        val body = JSONObject()
                        body.put("BaseRequest", stringParams)
                        body.put("Code", 3)
                        body.put("FromUserName", response.User.UserName);
                        body.put("ToUserName", response.User.UserName);
                        body.put("ClientMsgId", DateKit.getCurrentUnixTime());

                        test(body.toString())

                        api.openNotify(
                                openNotifyurl,
                                body.toString(),
                                wechatModel.cookie,
                                "application/json;charset=utf-8",
                                "zh_CN",
                                wechatModel.pass_ticket
                        )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                    }
                    .subscribe(
                            { res ->

                            },
                            {
                                error ->
                                error.message?.let { it1 -> test(it1) }

                            }

                    );

            testsend.setOnClickListener {
                Observable.just("测试信息")
                        .flatMap<Pair<String, ContactResponse>> {
                            message ->
                            val contactsUrl = wechatModel.baseUrl + "/webwxgetcontact"
                            val gson = Gson()
                            val stringParams = gson.toJson(wechatModel.param)

                            val contactsConbine = api.getContacts(
                                    contactsUrl,
                                    stringParams,
                                    wechatModel.cookie,
                                    "application/json;charset=utf-8",
                                    DateKit.getCurrentUnixTime().toString(),
                                    wechatModel.pass_ticket,
                                    wechatModel.param.BaseRequest.Skey
                            )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())

                            Observable.combineLatest(
                                    Observable.just(message),
                                    contactsConbine,
                                    BiFunction<String, ContactResponse, Pair<String, ContactResponse>> {
                                        s, integer ->
                                        Pair(s, integer)
                                    })

                        }
                        .flatMap {
                            pair ->
                            val message = pair.first
                            val contactresponse = pair.second
                            Observable.fromIterable(contactresponse.MemberList)
                                    .filter { it.NickName == "晓_晨DEV" }
                                    .take(1)
                                    .flatMap {
                                        val sendMessageUrl = wechatModel.baseUrl + "/webwxsendmsg"
                                        val gson = Gson()
                                        val baseRequest = gson.toJson(wechatModel.param)
                                        val body = JSONObject()
                                        val clientMsgId = DateKit.getCurrentUnixTime().toString() + StringKit.getRandomNumber(5)
                                        val Msg = JSONObject()
                                        Msg.put("Type", 1)
                                        Msg.put("Content", message)
                                        Msg.put("FromUserName", wechatModel.userName)
                                        Msg.put("ToUserName", it.UserName)
                                        Msg.put("LocalID", clientMsgId)
                                        Msg.put("ClientMsgId", clientMsgId)
                                        body.put("BaseRequest", baseRequest)
                                        body.put("Msg", Msg)
                                        api.getContacts(
                                                sendMessageUrl,
                                                body.toString(),
                                                wechatModel.cookie,
                                                "application/json;charset=utf-8",
                                                DateKit.getCurrentUnixTime().toString(),
                                                wechatModel.pass_ticket,
                                                wechatModel.param.BaseRequest.Skey
                                        )
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                    }


                        }.subscribe()


            }

            App.smsOb
                    .flatMap<Pair<String, ContactResponse>> {
                        message ->
                        val contactsUrl = wechatModel.baseUrl + "/webwxgetcontact"
                        val gson = Gson()
                        val stringParams = gson.toJson(wechatModel.param)

                        val contactsConbine = api.getContacts(
                                contactsUrl,
                                stringParams,
                                wechatModel.cookie,
                                "application/json;charset=utf-8",
                                DateKit.getCurrentUnixTime().toString(),
                                wechatModel.pass_ticket,
                                wechatModel.param.BaseRequest.Skey
                        )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())

                        Observable.combineLatest(
                                Observable.just(message),
                                contactsConbine,
                                BiFunction<String, ContactResponse, Pair<String, ContactResponse>> {
                                    s, integer ->
                                    Pair(s, integer)
                                })

                    }
                    .flatMap {
                        pair ->
                        val message = pair.first
                        val contactresponse = pair.second
                        Observable.fromIterable(contactresponse.MemberList)
                                .filter { it.NickName == "晓_晨DEV" }
                                .take(1)
                                .flatMap {
                                    val sendMessageUrl = wechatModel.baseUrl + "/webwxsendmsg"
                                    val gson = Gson()
                                    val baseRequest = gson.toJson(wechatModel.param)
                                    val body = JSONObject()
                                    val clientMsgId = DateKit.getCurrentUnixTime().toString() + StringKit.getRandomNumber(5)
                                    val Msg = JSONObject()
                                    Msg.put("Type", 1)
                                    Msg.put("Content", message)
                                    Msg.put("FromUserName", wechatModel.userName)
                                    Msg.put("ToUserName", it.UserName)
                                    Msg.put("LocalID", clientMsgId)
                                    Msg.put("ClientMsgId", clientMsgId)
                                    body.put("BaseRequest", baseRequest)
                                    body.put("Msg", Msg)
                                    api.getContacts(
                                            sendMessageUrl,
                                            body.toString(),
                                            wechatModel.cookie,
                                            "application/json;charset=utf-8",
                                            DateKit.getCurrentUnixTime().toString(),
                                            wechatModel.pass_ticket,
                                            wechatModel.param.BaseRequest.Skey
                                    )
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                }


                    }.subscribe()

        }


    }

    private fun writeResponseBodyToDisk(inputStream1: InputStream): File? {
        val file = File(getCacheDir(), "temp.jpg");
        val output = FileOutputStream(file)

        val buffer = ByteArray(40 * 1024)
        var read = 0;
        while (read != -1) {
            read = inputStream1.read(buffer)
            if (read != -1) {
                output.write(buffer, 0, read)
            }
        }
        output.flush()
        return file

    }

}

fun Activity.test(message: String) {
    Log.e("tan", message)
}