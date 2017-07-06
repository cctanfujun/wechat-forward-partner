package com.xiaochen

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.GsonBuilder
import com.xiaochen.robot.wechat.model.WeChatModel
import com.xiaochen.robot.wechat.util.MyCookieCache
import com.xiaochen.robot.wechat.util.MyCookieJar
import io.reactivex.subjects.PublishSubject
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


/**
 * Created by tanfujun on 5/25/17.
 */

class App : Application() {

    companion object {
        val wechatModel = WeChatModel()
        val smsPublishSubject: PublishSubject<String> = PublishSubject.create<String>()
        val gson = GsonBuilder().setLenient().create()

        @JvmField
        var context: Context? = null
        var cookieretrofit: Retrofit? = null
        var retrofit: Retrofit? = null
        var cookieJar:MyCookieJar? =null

    }


    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        App.context = this
        cookieJar = MyCookieJar(MyCookieCache(), SharedPrefsCookiePersistor(applicationContext))

        cookieretrofit = Retrofit.Builder()
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

        retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("https://login.weixin.qq.com/")
                .client(OkHttpClient.Builder()
                        .addNetworkInterceptor(StethoInterceptor())
                        .build()
                )
                .build()

    }


}
