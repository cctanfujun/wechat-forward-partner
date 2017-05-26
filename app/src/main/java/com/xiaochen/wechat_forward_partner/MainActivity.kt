package com.xiaochen.wechat_forward_partner

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.xiaochen.App
import com.xiaochen.api.WeChatApi
import com.xiaochen.blade.blade.kit.base.DateKit
import com.xiaochen.blade.blade.kit.http.HttpRequest
import com.xiaochen.robot.wechat.Constant
import com.xiaochen.robot.wechat.model.WeChatModel
import com.xiaochen.robot.wechat.util.Matchers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    val wechatModel = WeChatModel();


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val imageView = findViewById(R.id.qr_img) as ImageView

        val retrofit = App.retrofit;

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
                    .observeOn(AndroidSchedulers.mainThread())
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
                    .subscribe(
                            { res ->
                                test(res)
                                wechatModel.param.BaseRequest.Skey = Matchers.match("<skey>(\\S+)</skey>", res)
                                wechatModel.param.BaseRequest.Sid = Matchers.match("<wxsid>(\\S+)</wxsid>", res)
                                wechatModel.param.BaseRequest.Uin = Matchers.match("<wxuin>(\\S+)</wxuin>", res)
                                wechatModel.pass_ticket = Matchers.match("<pass_ticket>(\\S+)</pass_ticket>", res)
                            },
                            { error -> test(error.message.toString()) }
                    )
        }

    }

}

fun Activity.test(message: String) {

    Log.e("tan", message)
}
