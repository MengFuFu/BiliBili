package com.example.bilibili.ui.column.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bilibili.R;
import com.example.bilibili.model.bean.ArticleItem;

import java.util.List;

/**
 * 专栏文章适配器
 */
public class ColumnAdapter extends RecyclerView.Adapter<ColumnAdapter.ArticleViewHolder> {

    private final List<ArticleItem> mItems;

    public ColumnAdapter(List<ArticleItem> mItems) {
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_column, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ArticleItem item = mItems.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvSummary.setText(item.getSummary());
        holder.tvAuthor.setText(item.getAuthor());
        holder.tvView.setText(item.getView());

        // 有封面 URL 就用 Glide 加载，没有就保持占位图
        Glide.with(holder.ivCover.getContext())
                .load(item.getCover())
                .placeholder(R.drawable.bili_default_image_tv)
                .into(holder.ivCover);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    //刷新文章列表
    public void resetItems(List<ArticleItem> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCover;
        TextView tvTitle;
        TextView tvSummary;
        TextView tvAuthor;
        TextView tvView;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSummary = itemView.findViewById(R.id.tv_summary);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvView = itemView.findViewById(R.id.tv_view);
        }
    }
}
