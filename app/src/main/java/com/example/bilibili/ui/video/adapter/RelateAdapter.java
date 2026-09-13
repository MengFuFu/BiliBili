package com.example.bilibili.ui.video.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.RecommendItem;

import java.util.List;

/**
 * 相关推荐列表适配器
 */
public class RelateAdapter extends RecyclerView.Adapter<RelateAdapter.RelatedViewHolder> {

    private final List<RecommendItem> mItems;

    public RelateAdapter(List<RecommendItem> mItems) {
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public RelateAdapter.RelatedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_related, parent, false);
        return new RelatedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RelateAdapter.RelatedViewHolder holder, int position) {
        RecommendItem item = mItems.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvUpName.setText(item.getUpName());
        holder.tvPlay.setText(item.getPlay() + "次播放");
        holder.tvDuration.setText(item.getDuration());
        holder.ivCover.setImageResource(R.drawable.bili_default_image_tv);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class RelatedViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCover;
        TextView tvDuration;
        TextView tvTitle;
        TextView tvUpName;
        TextView tvPlay;

        public RelatedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvUpName = itemView.findViewById(R.id.tv_up_name);
            tvPlay = itemView.findViewById(R.id.tv_play);
        }
    }
}
