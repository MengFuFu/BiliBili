package com.example.bilibili.ui.region.adapter;

import android.app.job.PendingJobReasonsInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

    //分区入口点击回调
    public interface OnPartitionClickListener {
        void onPartitionClick(RegionItem item);
    }

    private final List<RegionItem> mPartitions;
    private final List<RecommendItem> mVideos;

    private int mSelectedIndex = 0; //当前选中的分区下标
    private OnPartitionClickListener mListener;

    public RegionAdapter(List<RegionItem> mPartitions, List<RecommendItem> mVideos) {
        this.mPartitions = mPartitions;
        this.mVideos = mVideos;
    }

    public void setOnPartitionClickListener(OnPartitionClickListener listener) {
        mListener = listener;
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

    //刷新视频列表
    public void resetVideos(List<RecommendItem> videos) {
        mVideos.clear();
        mVideos.addAll(videos);
        notifyDataSetChanged();
    }

    private void bindPartition(PartitionViewHolder holder, RegionItem item) {
        holder.ivIcon.setImageResource(item.getIcon());
        holder.tvName.setText(item.getName());

        //选中项标题用主题粉色高亮，未选中用正常文字色
        boolean selected = holder.getAdapterPosition() == mSelectedIndex;
        int textColor = ContextCompat.getColor(holder.itemView.getContext(), selected ? R.color.colorPrimary : R.color.text_main);
        holder.tvName.setTextColor(textColor);

        //点击整个入口切换分区
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if(pos == mSelectedIndex) {
                    return; //已经被选中的
                }
                mSelectedIndex = pos;
                notifyDataSetChanged(); //刷新高亮
                if(mListener != null) {
                    mListener.onPartitionClick(item);
                }
            }
        });
    }

    private void bindBody(BodyViewHolder holder, RecommendItem item) {
        holder.tvTitle.setText(item.getTitle());
        holder.tvUpName.setText(item.getUpName());
        holder.tvPlay.setText(item.getPlay());
        holder.tvDuration.setText(item.getDuration());

        // 用 Glide 加载封面，加载失败或为空时显示占位图
        Glide.with(holder.ivCover.getContext())
                .load(item.getCover())
                .placeholder(R.drawable.bili_default_image_tv)
                .into(holder.ivCover);

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
