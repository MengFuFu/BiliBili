package com.example.bilibili.ui.recommend.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.RecommendItem;

import java.util.List;

/**
 * 推荐页视频卡片适配器
 */
public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.RecommendViewHolder> {

    private final List<RecommendItem> mItems;

    public RecommendAdapter(List<RecommendItem> items) {
        this.mItems = items;
    }

    @NonNull
    @Override
    public RecommendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommend, parent, false);
        return new RecommendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendViewHolder holder, int position) {
        RecommendItem item = mItems.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvUpName.setText(item.getUpName());
        holder.tvPlay.setText(item.getPlay());
        holder.tvDuration.setText(item.getDuration());

        // 封面暂时都用同一张占位图，后面再换成真实网络图
        holder.ivCover.setImageResource(R.drawable.bili_default_image_tv);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class RecommendViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCover;
        TextView tvDuration;
        TextView tvTitle;
        TextView tvUpName;
        TextView tvPlay;

        RecommendViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvUpName = itemView.findViewById(R.id.tv_up_name);
            tvPlay = itemView.findViewById(R.id.tv_play);
        }
    }
}