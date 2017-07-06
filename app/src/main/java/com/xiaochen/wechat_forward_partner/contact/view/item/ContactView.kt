package com.xiaochen.wechat_forward_partner.main.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.xiaochen.App
import com.xiaochen.robot.wechat.util.CookieUtil
import com.xiaochen.wechat_forward_partner.R
import com.xiaochen.wechat_forward_partner.contact.view.item.Contact
import kotlinx.android.synthetic.main.view_contact.view.*
import me.drakeet.multitype.ItemViewBinder


/**
 * Created by tanfujun on 6/28/17.
 */

class ContactView : ItemViewBinder<Contact, ContactView.ViewHolder>() {


    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.view_contact, parent, false)
        return ContactView.ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, contact: Contact) {
        val context = holder.itemView.context
        holder.itemView.name.text = contact.name
        val glideUrl = GlideUrl(contact.picUrl, LazyHeaders.Builder().addHeader("Cookie", CookieUtil.getCookie(App.cookieJar)).build())
        Glide.with(context).load(glideUrl).into(holder.itemView.pic)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}
