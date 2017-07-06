package com.xiaochen.wechat_forward_partner.main.view

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xiaochen.wechat_forward_partner.R
import com.xiaochen.wechat_forward_partner.contact.view.ContactData
import com.xiaochen.wechat_forward_partner.contact.view.item.Contact
import kotlinx.android.synthetic.main.layout_contact.view.*
import me.drakeet.multitype.ItemViewBinder
import me.drakeet.multitype.MultiTypeAdapter


/**
 * Created by tanfujun on 6/28/17.
 */

class ContactHolder : ItemViewBinder<ContactData, ContactHolder.ViewHolder>() {


    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.layout_contact, parent, false)
        return ContactHolder.ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, itemData: ContactData) {
        val context = holder.itemView.context
        val adapter = MultiTypeAdapter()
        adapter.register(Contact::class.java,ContactView())
        holder.itemView.contact_rv.layoutManager = LinearLayoutManager(context)
        holder.itemView.contact_rv.adapter = adapter
        adapter.items = itemData.contacts
        adapter.notifyDataSetChanged()


    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}
