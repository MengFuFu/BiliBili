package com.example.bilibili.ui.live.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bilibili.R;
import com.example.bilibili.model.bean.Banner;

import java.util.List;

/**
 * 轮播图页面适配器：每一页就是一张铺满的 ImageView
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<Banner> mData;

    public BannerAdapter(List<Banner> data) {
        mData = data;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BannerViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        final Banner banner = mData.get(position % mData.size());

        Glide.with(holder.imageView.getContext())
                .load(banner.getImageUrl())
                .placeholder(R.drawable.bili_default_image_tv)
                .centerCrop()
                .into(holder.imageView);

        // 关键：把手势冲突处理放到 ImageView 上，因为它现在是消费事件的那个 View
        holder.imageView.setOnTouchListener(new View.OnTouchListener() {
            private float downX;
            private float downY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // v 是 ImageView
                ViewParent inner = v.getParent();                              // 内层 RecyclerView
                ViewParent pager = inner != null ? inner.getParent() : null;   // banner ViewPager2

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        if (pager != null) {
                            // 禁止外层 ViewPager（首页顶部 Tab）抢横滑
                            pager.requestDisallowInterceptTouchEvent(true);
                        }
                        break;

                    case MotionEvent.ACTION_MOVE: {
                        float dx = event.getX() - downX;
                        float dy = event.getY() - downY;
                        if (pager == null) {
                            break;
                        }
                        if (Math.abs(dy) > Math.abs(dx)) {
                            // 纵向滑动：交给外层列表滚动
                            pager.requestDisallowInterceptTouchEvent(false);
                        } else {
                            // 横向滑动：banner 自己还能滚就阻止外层抢；滚到头就交出去
                            boolean canScroll = inner instanceof RecyclerView
                                    && ((RecyclerView) inner).canScrollHorizontally(dx < 0 ? 1 : -1);
                            pager.requestDisallowInterceptTouchEvent(canScroll);
                        }
                        break;
                    }

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (pager != null) {
                            pager.requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                }
                // 返回 false：不消费事件，让 ViewPager2 的滚动和点击都继续走
                return false;
            }
        });

        // 点击跳转链接
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (banner.getLink() != null && !banner.getLink().isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(banner.getLink()));
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null || mData.isEmpty() ? 0 : Integer.MAX_VALUE;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        BannerViewHolder(ImageView itemView) {
            super(itemView);
            this.imageView = itemView;
        }
    }
}