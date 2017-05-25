package com.xiaochen.wechat_forward_partner

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.xiaochen.App
import com.xiaochen.api.WeChatApi
import com.xiaochen.blade.blade.kit.base.DateKit
import com.xiaochen.blade.blade.kit.http.HttpRequest
import com.xiaochen.robot.wechat.Constant
import com.xiaochen.robot.wechat.util.Matchers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File


class MainActivity : AppCompatActivity() {


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
                    val url = Constant.QRCODE_URL + uuid
                    val output = File(cacheDir, "temp.jpg")
                    HttpRequest.post(url, true, "t", "webwx", "_", DateKit.getCurrentUnixTime()).receive(output)
                    output
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    file ->
                    Glide.with(this@MainActivity).load(file).into(imageView)

                }
                .subscribe()



    }

}
