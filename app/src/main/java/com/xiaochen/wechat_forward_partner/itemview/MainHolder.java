package com.xiaochen.wechat_forward_partner.itemview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaochen.wechat_forward_partner.R;
import com.xiaochen.wechat_forward_partner.itemdata.MainData;

import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by tanfujun on 6/28/17.
 */

public class MainHolder extends ItemViewBinder<MainData, MainHolder.ViewHolder> {


    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.activity_main, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull MainData item) {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


}
