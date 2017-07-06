package com.xiaochen.wechat_forward_partner.main.business

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.xiaochen.App
import com.xiaochen.App.Companion.wechatModel
import com.xiaochen.api.WeChatApi
import com.xiaochen.blade.blade.kit.base.DateKit
import com.xiaochen.robot.wechat.util.CookieUtil
import com.xiaochen.robot.wechat.util.Matchers
import com.xiaochen.wechat_forward_partner.RecyclerPagerActivity
import com.xiaochen.wechat_forward_partner.contact.view.ContactData
import com.xiaochen.wechat_forward_partner.contact.view.item.Contact
import com.xiaochen.wechat_forward_partner.main.view.MainData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.drakeet.multitype.Items
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Created by tanfujun on 7/5/17.
 */

class MainBusiness(var context: Context) {

    val cookieApi = App.cookieretrofit?.create(WeChatApi::class.java)
    val api = App.retrofit?.create(WeChatApi::class.java)

    var currentItems: Items? = null

    fun getQrImageData() {
        api?.let {
            it.getUUid(wechatModel.requestHeader)
                    .subscribeOn(Schedulers.io())
                    .map {
                        resStr ->
                        Matchers.match("window.QRLogin.uuid = \"(.*)\";", resStr)
                    }
                    .flatMap {
                        uuid ->
                        wechatModel.uuid = uuid
                        it.getQRIMG(wechatModel.requestHeader, uuid)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                    }
                    .map {
                        response ->
                        writeResponseBodyToDisk(response.byteStream())
                    }
                    .map {
                        file ->
                        val items = Items()
                        val mainData = MainData(file)
                        items.add(mainData)
                        currentItems = items
                        RecyclerPagerActivity.diffResultObservable.onNext(currentItems)
                    }
                    .subscribe(
                            {},
                            { error -> Log.e("tan", error.message) }
                    )
        }

    }


    fun start() {
        api?.let {
            it.login(0, wechatModel.uuid)
                    .subscribeOn(Schedulers.io())
                    .filter { res ->
                        val code = Matchers.match("window.code=(\\d+);", res)
                        code == "200"
                    }
                    .flatMap {
                        res ->
                        val pm = Matchers.match("window.redirect_uri=\"(\\S+?)\";", res)
                        val redirect_uri = pm + "&fun=new"
                        wechatModel.redirect_uri = redirect_uri;
                        wechatModel.baseUrl = redirect_uri.substring(0, redirect_uri.lastIndexOf("/"))
                        cookieApi
                                ?.request(redirect_uri)
                                ?.subscribeOn(Schedulers.io())
                                ?.observeOn(AndroidSchedulers.mainThread())
                    }
                    .flatMap { res ->
                        val cookie = CookieUtil.getCookie(App.cookieJar)
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
                        val openNotifyurl = wechatModel.baseUrl + "/webwxstatusnotify"

                        val gson = Gson()
                        val stringParams = gson.toJson(wechatModel.param)

                        val body = JSONObject()
                        body.put("BaseRequest", stringParams)
                        body.put("Code", 3)
                        body.put("FromUserName", response.User.UserName)
                        body.put("ToUserName", response.User.UserName)
                        body.put("ClientMsgId", DateKit.getCurrentUnixTime())

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
                    .flatMap {
                        res ->
                        val contactsUrl = wechatModel.baseUrl + "/webwxgetcontact"
                        val gson = Gson()
                        val stringParams = gson.toJson(wechatModel.param)
                        api.getContacts(
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

                    }
                    .subscribe(
                            { contacts ->
                                val contactsList = Items()
                                contacts.MemberList.forEach {
                                    contactsList.add(Contact("https://wx.qq.com" + it.HeadImgUrl, it.NickName))
                                }
                                val contactData = ContactData(contactsList)

                                currentItems?.add(contactData)
                                RecyclerPagerActivity.diffResultObservable.onNext(currentItems)

                            },
                            {
                                error ->
                                Log.e("tan", error.message)

                            }

                    )
        }
    }


    private fun writeResponseBodyToDisk(inputStream1: InputStream): File? {
        val file = File(context.cacheDir, "temp.jpg")
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
