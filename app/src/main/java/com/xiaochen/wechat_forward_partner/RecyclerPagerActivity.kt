package com.xiaochen.wechat_forward_partner

import android.app.Activity
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.util.Log
import android.widget.LinearLayout.HORIZONTAL
import com.xiaochen.tools.DiffCallBack
import com.xiaochen.wechat_forward_partner.contact.view.ContactData
import com.xiaochen.wechat_forward_partner.main.business.MainBusiness
import com.xiaochen.wechat_forward_partner.main.view.ContactHolder
import com.xiaochen.wechat_forward_partner.main.view.MainData
import com.xiaochen.wechat_forward_partner.main.view.MainHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_pager.*
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter


/**
 * Created by tanfujun on 6/28/17.
 */

class RecyclerPagerActivity : Activity() {

    companion object {
        val Tag = "RecyclerPagerActivity"
        val diffResultObservable: PublishSubject<Items> = PublishSubject.create()
    }

    var disposable: Disposable? = null
    var items: Items? = null
    var mainBusiness: MainBusiness? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager)

        val adapter = MultiTypeAdapter()
        adapter.register(MainData::class.java, MainHolder())
        adapter.register(ContactData::class.java,ContactHolder())
        val linearLayoutManager = LinearLayoutManager(this, HORIZONTAL, false)
        recyclerview.adapter = adapter
        recyclerview.layoutManager = linearLayoutManager
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerview)

        disposable = diffResultObservable
                .subscribeOn(Schedulers.io())
                .map {
                    newItems ->
                    adapter.items = newItems
                    val diffresult = DiffUtil.calculateDiff(DiffCallBack(newItems, items))
                    diffresult
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { diffResult -> diffResult.dispatchUpdatesTo(adapter) },
                        { error -> Log.e(Tag, error.message) }
                )

        mainBusiness = MainBusiness(this)
        mainBusiness?.getQrImageData()

    }




    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}