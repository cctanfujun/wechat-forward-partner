package com.xiaochen

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutCompat.HORIZONTAL
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import com.xiaochen.wechat_forward_partner.R
import com.xiaochen.wechat_forward_partner.itemdata.MainData
import com.xiaochen.wechat_forward_partner.itemview.MainHolder
import kotlinx.android.synthetic.main.activity_pager.*
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter


/**
 * Created by tanfujun on 6/28/17.
 */

class RePagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager)
        val adapter = MultiTypeAdapter()
        adapter.register(MainData::class.java,MainHolder())
        val linearLayoutManager = LinearLayoutManager(this, HORIZONTAL, false)
        recyclerview.adapter = adapter
        recyclerview.layoutManager = linearLayoutManager
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerview)


        val items = Items()
        items.add(MainData())
        items.add(MainData())
        items.add(MainData())
        items.add(MainData())

        adapter.items = items
        adapter.notifyDataSetChanged()



    }
}