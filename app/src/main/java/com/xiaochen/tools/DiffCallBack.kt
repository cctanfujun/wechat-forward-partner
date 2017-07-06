package com.xiaochen.tools

import android.support.v7.util.DiffUtil

import me.drakeet.multitype.Items

/**
 * Created by tanfujun on 7/5/17.
 */

class DiffCallBack(private val mOldDatas: Items?, private val mNewDatas: Items?) : DiffUtil.Callback() {


    override fun getOldListSize(): Int {
        return mOldDatas?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return mNewDatas?.size ?: 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (mOldDatas?.get(oldItemPosition) != null && mNewDatas?.get(newItemPosition) != null) {
            return mOldDatas[oldItemPosition] == mNewDatas[newItemPosition]
        } else {
            return false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return false
    }
}
