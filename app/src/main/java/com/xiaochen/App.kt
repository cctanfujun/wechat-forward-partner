package com.xiaochen

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.xiaochen.robot.wechat.model.WeChatModel
import io.reactivex.subjects.PublishSubject


/**
 * Created by tanfujun on 5/25/17.
 */

class App : Application() {

    companion object {
        @JvmField
        var context: Context? = null
        val wechatModel = WeChatModel()
        val  smsOb = PublishSubject.create<String>()

    }



    override fun onCreate() {
        super.onCreate()
        App.context = this
        Stetho.initializeWithDefaults(this)

    }


}
