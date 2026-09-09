package com.example.bilibili.ui.live.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilibili.model.bean.Banner;

import java.util.List;

/**
 * 轮播图页面适配器
 * 每一也就是一个铺满的TextView：背景色 + 标题
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<Banner> mData;

    public BannerAdapter(List<Banner> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public BannerAdapter.BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //代码创建一个居中的TextView作为一页
        TextView textView = new TextView(parent.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        return new BannerViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        // 用取模拿到真实的某一张
        Banner banner = mData.get(position % mData.size());
        holder.textView.setText(banner.getTitle());
        holder.textView.setBackgroundColor(banner.getCoverColor());
    }

    @Override
    public int getItemCount() {
        // 返回一个很大的数，实现“无限轮播”
        return mData == null || mData.isEmpty() ? 0 : Integer.MAX_VALUE;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public BannerViewHolder(TextView itemView) {
            super(itemView);
            this.textView = itemView;
        }
    }
}
