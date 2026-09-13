package com.example.bilibili.ui.video.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.Comment;

import java.util.List;

/**
 * 评论列表适配器
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final List<Comment> mItems;

    public CommentAdapter(List<Comment> mItems) {
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
        Comment comment = mItems.get(position);
        holder.tvUserName.setText(comment.getUserName());
        holder.tvContent.setText(comment.getContent());
        holder.tvLike.setText("赞" + comment.getLikeCount());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName;
        TextView tvContent;
        TextView tvLike;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvLike = itemView.findViewById(R.id.tv_like);
        }
    }
}
