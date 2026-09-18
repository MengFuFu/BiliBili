package com.example.bilibili.ui.message.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.MessageItem;

import java.util.List;

/**
 * 消息列表适配器
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<MessageItem> mItems;

    public MessageAdapter(List<MessageItem> mItems) {
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        MessageItem item = mItems.get(position);
        holder.ivIcon.setImageResource(item.getIcon());
        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    // 刷新消息列表（配合 LiveData 使用）
    public void resetItems(List<MessageItem> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIcon;
        TextView tvTitle;
        TextView tvSubtitle;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
        }
    }
}
