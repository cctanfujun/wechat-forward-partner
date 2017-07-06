package com.xiaochen.wechat_forward_partner.main.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.xiaochen.wechat_forward_partner.R
import com.xiaochen.wechat_forward_partner.RecyclerPagerActivity
import kotlinx.android.synthetic.main.layout_main.view.*
import me.drakeet.multitype.ItemViewBinder


/**
 * Created by tanfujun on 6/28/17.
 */

class MainHolder : ItemViewBinder<MainData, MainHolder.ViewHolder>() {


    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.layout_main, parent, false)
        return MainHolder.ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, itemData: MainData) {
        val context = holder.itemView.context
        with(itemData) {
            Glide.with(context).load(itemData.qr_image_file).into(holder.itemView.qr_img)
            holder.itemView.refresh.setOnClickListener {
                (context as RecyclerPagerActivity).mainBusiness?.getQrImageData()
            }
            holder.itemView.start.setOnClickListener {
                (context as RecyclerPagerActivity).mainBusiness?.start()
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}
