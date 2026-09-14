package com.example.bilibili.ui.region.adapter;

import android.app.job.PendingJobReasonsInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.RecommendItem;
import com.example.bilibili.model.bean.RegionItem;
import com.example.bilibili.ui.video.VideoPlayActivity;

import java.util.List;

/**
 * 分区页适配器：支持两种条目
 * - 分区入口
 * - 视频卡片
 */
public class RegionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PARTITION = 0; //分区入口
    private static final int TYPE_BODY = 1; //视频卡片

    private final List<RegionItem> mPartitions;
    private final List<RecommendItem> mVideos;

    public RegionAdapter(List<RegionItem> mPartitions, List<RecommendItem> mVideos) {
        this.mPartitions = mPartitions;
        this.mVideos = mVideos;
    }

    @Override
    public int getItemViewType(int position) {
        //前面几个是分区入口，后面是视频卡片
        return position < mPartitions.size() ? TYPE_PARTITION : TYPE_BODY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == TYPE_PARTITION) {
            return new PartitionViewHolder(inflater.inflate(R.layout.item_region_partition, parent, false));
        }
        //视频卡片直接复用推荐页的卡片布局
        return new BodyViewHolder(inflater.inflate(R.layout.item_recommend, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PartitionViewHolder) {
            bindPartition((PartitionViewHolder) holder, mPartitions.get(position));
        }
        else {
            bindBody((BodyViewHolder) holder, mVideos.get(position - mPartitions.size()));
        }
    }

    @Override
    public int getItemCount() {
        return mPartitions.size() + mVideos.size();
    }

    private void bindPartition(PartitionViewHolder holder, RegionItem item) {
        holder.ivIcon.setImageResource(item.getIcon());
        holder.tvName.setText(item.getName());
    }

    private void bindBody(BodyViewHolder holder, RecommendItem item) {
        holder.tvTitle.setText(item.getTitle());
        holder.tvUpName.setText(item.getUpName());
        holder.tvPlay.setText(item.getPlay());
        holder.tvDuration.setText(item.getDuration());
        holder.ivCover.setImageResource(R.drawable.bili_default_image_tv);

        //点击视频卡片跳转播放页
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPlayActivity.startActivity(v.getContext(), item.getTitle(), item.getUpName(), item.getPlay());
            }
        });
    }

    static class PartitionViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIcon;
        TextView tvName;

        public PartitionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

    static class BodyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCover;
        TextView tvDuration;
        TextView tvTitle;
        TextView tvUpName;
        TextView tvPlay;

        public BodyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvUpName = itemView.findViewById(R.id.tv_up_name);
            tvPlay = itemView.findViewById(R.id.tv_play);
        }
    }
}
