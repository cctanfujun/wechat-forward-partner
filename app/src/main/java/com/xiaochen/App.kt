package com.xiaochen

import android.app.Application
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * Created by tanfujun on 5/25/17.
 */

class App : Application() {

    companion object {
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("https://login.weixin.qq.com/")
                .client(OkHttpClient.Builder()
                        .addNetworkInterceptor(StethoInterceptor())
                        .build()
                )
                .build()

        val cookieretrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("https://login.weixin.qq.com/")
                .client(OkHttpClient.Builder()
                        .addNetworkInterceptor(StethoInterceptor())
                        .cookieJar(object : CookieJar {

                            private val map = HashMap<String, MutableList<Cookie>>()

                            override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
                                map[url.host()] = cookies
                            }

                            override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
                                return map[url.host()] ?: ArrayList<Cookie>()

                            }
                        })
                        .build()
                )
                .build()

    }


    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}
