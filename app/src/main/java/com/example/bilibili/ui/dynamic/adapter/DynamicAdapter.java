package com.example.bilibili.ui.dynamic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.RecommendItem;
import com.example.bilibili.ui.video.VideoPlayActivity;

import java.util.List;

/**
 * 动态 列表适配器
 */
public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.DynamicViewHolder> {

    private final List<RecommendItem> mItems;

    public DynamicAdapter(List<RecommendItem> mItems) {
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public DynamicAdapter.DynamicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic, parent, false);
        return new DynamicViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DynamicAdapter.DynamicViewHolder holder, int position) {
        RecommendItem item = mItems.get(position);

        holder.tvUserName.setText(item.getUpName());
        holder.tvTitle.setText(item.getTitle());
        holder.tvPlay.setText(item.getPlay() + "次播放");
        holder.tvDuration.setText(item.getDuration());
        holder.ivCover.setImageResource(R.drawable.bili_default_image_tv);

        //点击动态跳转到视频播放页
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPlayActivity.startActivity(
                        v.getContext(),
                        item.getTitle(),
                        item.getUpName(),
                        item.getPlay()
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class DynamicViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAvatar;
        ImageView ivCover;
        TextView tvUserName;
        TextView tvTitle;
        TextView tvDuration;
        TextView tvPlay;

        public DynamicViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvPlay = itemView.findViewById(R.id.tv_play);
        }
    }
}
