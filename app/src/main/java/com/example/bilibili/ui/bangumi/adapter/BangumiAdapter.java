package com.example.bilibili.ui.bangumi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.BangumiItem;

import java.util.List;

/**
 * 番剧列表适配器
 */
public class BangumiAdapter extends RecyclerView.Adapter<BangumiAdapter.BangumiViewHolder> {

    private final List<BangumiItem> mItems;

    public BangumiAdapter(List<BangumiItem> mItems) {
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public BangumiAdapter.BangumiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bangumi, parent, false);
        return new BangumiViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BangumiAdapter.BangumiViewHolder holder, int position) {
        BangumiItem item = mItems.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDesc());
        holder.ivCover.setImageResource(R.drawable.bili_default_image_tv);

        //追番按钮
        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView btn = (TextView) v;
                if("追番".contentEquals(btn.getText())) {
                    btn.setText("已追番");
                }
                else {
                    btn.setText("追番");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class BangumiViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCover;
        TextView tvTitle;
        TextView tvDesc;
        TextView btnFollow;

        public BangumiViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            btnFollow = itemView.findViewById(R.id.btn_follow);
        }
    }
}
