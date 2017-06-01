package com.xiaochen.wechat_forward_partner

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.Gson
import com.xiaochen.api.WeChatApi
import com.xiaochen.blade.blade.kit.base.DateKit
import com.xiaochen.blade.blade.kit.http.HttpRequest
import com.xiaochen.robot.wechat.Constant
import com.xiaochen.robot.wechat.model.WeChatModel
import com.xiaochen.robot.wechat.util.Matchers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File


class MainActivity : AppCompatActivity() {

    val wechatModel = WeChatModel();


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val imageView = findViewById(R.id.qr_img) as ImageView

        var cookieJar: CookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(applicationContext))

        val cookieretrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://login.weixin.qq.com/")
                .client(OkHttpClient.Builder()
                        .addNetworkInterceptor(StethoInterceptor())
                        .cookieJar(cookieJar)
                        .build()
                )
                .build()

        val retrofit = cookieretrofit

        val api = retrofit.create(WeChatApi::class.java)

        api.getUUid()
                .subscribeOn(Schedulers.io())
                .map {
                    resStr ->
                    Matchers.match("window.QRLogin.uuid = \"(.*)\";", resStr)
                }
                .map {
                    uuid ->
                    wechatModel.uuid = uuid
                    val url = Constant.QRCODE_URL + uuid
                    val output = File(cacheDir, "temp.jpg")
                    HttpRequest.post(url, true, "t", "webwx", "_", DateKit.getCurrentUnixTime()).receive(output)
                    output
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
            api.login(wechatModel.requestHeader, 0, wechatModel.uuid)
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

                        api.request(redirect_uri)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                    }
                    .flatMap { res ->
                        test(res)
                        wechatModel.param.BaseRequest.Skey = Matchers.match("<skey>(\\S+)</skey>", res)
                        wechatModel.param.BaseRequest.Sid = Matchers.match("<wxsid>(\\S+)</wxsid>", res)
                        wechatModel.param.BaseRequest.Uin = Matchers.match("<wxuin>(\\S+)</wxuin>", res)
                        wechatModel.param.BaseRequest.DeviceID = "e" + DateKit.getCurrentUnixTime()
                        wechatModel.pass_ticket = Matchers.match("<pass_ticket>(\\S+)</pass_ticket>", res)

                        val gson = Gson();
                        val stringParams = gson.toJson(wechatModel.param)

                        api.wxInit(stringParams,
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
                        test(Gson().toJson(response).toString())

                        val gson = Gson();
                        val stringParams = gson.toJson(wechatModel.param)
                        api.getContacts(stringParams,
                                DateKit.getCurrentUnixTime().toString(),
                                wechatModel.pass_ticket,
                                wechatModel.param.BaseRequest.Skey
                        )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                    }
                    .subscribe {
                        string ->

                        test(string)
                    }

        }

    }

    fun Activity.test(message: String) {

        Log.e("tan", message)
    }
}