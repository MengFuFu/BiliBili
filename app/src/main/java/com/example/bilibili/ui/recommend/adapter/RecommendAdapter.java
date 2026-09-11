package com.example.bilibili.ui.recommend.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.Banner;
import com.example.bilibili.model.bean.RecommendItem;
import com.example.bilibili.ui.live.adapter.BannerAdapter;
import com.example.bilibili.ui.video.VideoPlayActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐页视频卡片适配器:支持两种条目
 * 顶部轮播Banner
 * 视频卡片
 */
public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BANNER = 0; //轮播图
    private static final int TYPE_ITEM = 1; //视频卡片

    private final List<Banner> mBanners;
    private final List<RecommendItem> mItems;

    public RecommendAdapter(List<Banner> mBanners, List<RecommendItem> mItems) {
        this.mBanners = mBanners;
        this.mItems = mItems;
    }

    @Override
    public int getItemViewType(int position) {
        //第一个是Banner 其余都是视频
        return position == 0 ? TYPE_BANNER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == TYPE_BANNER) {
            //Banner直接复用直播页的轮播局面
            return new BannerViewHolder(inflater.inflate(R.layout.item_live_banner, parent, false));
        }
        return new RecommendViewHolder(inflater.inflate(R.layout.item_recommend, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof BannerViewHolder) {
            ((BannerViewHolder) holder).bind(mBanners);
        }
        else {
            //视频卡片从position 1开始
            bindItem((RecommendViewHolder)holder, mItems.get(position - 1));
        }
    }

    //ViewHolder被回收时,停止Banner的自动连播
    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if(holder instanceof BannerViewHolder) {
            ((BannerViewHolder) holder).stopAutoScroll();
        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return 1 + mItems.size();
    }

    //追加数据(加载更多用)
    public void addItems(List<RecommendItem> newItems) {
        if(newItems == null || newItems.isEmpty()) {
            return;
        }
        int start = mItems.size();
        mItems.addAll(newItems);
        //注意：position 0 被banner占了，视频卡片的列表位置 = 1 + 下标
        notifyItemRangeInserted(1 + start, newItems.size());
    }

    //清空并重新填充（下拉刷新用）
    public void resetItems(List<RecommendItem> newItems) {
        mItems.clear();
        mItems.addAll(newItems);
        notifyDataSetChanged();
    }

    //填充视频卡片
    private void bindItem(RecommendViewHolder holder, RecommendItem item) {
        holder.tvTitle.setText(item.getTitle());
        holder.tvUpName.setText(item.getUpName());
        holder.tvPlay.setText(item.getPlay());
        holder.tvDuration.setText(item.getDuration());
        holder.ivCover.setImageResource(R.drawable.bili_default_image_tv);

        //点击整张卡片，跳转到视频播放页
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPlayActivity.startActivity(v.getContext(),
                        item.getTitle(),
                        item.getUpName(),
                        item.getPlay());
            }
        });
    }

    // ==================== Banner ViewHolder（含自动轮播） ====================
    static class BannerViewHolder extends RecyclerView.ViewHolder {

        private static final int AUTO_SCROLL_DELAY = 3000; // 3 秒滚一次

        private final ViewPager2 viewPager;
        private final LinearLayout indicator;
        private final List<View> dots = new ArrayList<>();

        private Runnable autoScrollRunnable;
        private int bannerCount;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPager = itemView.findViewById(R.id.banner_view_pager);
            indicator = itemView.findViewById(R.id.banner_indicator);
        }

        void bind(List<Banner> banners) {
            bannerCount = banners.size();

            viewPager.setAdapter(new BannerAdapter(banners));
            addDots(banners.size());

            // 页面切换时更新圆点
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    updateDots(position % bannerCount);
                }
            });

            // 从中间位置开始，配合无限数量，前后都能滑
            if (bannerCount > 0) {
                viewPager.setCurrentItem(bannerCount * 1000, false);
            }

            // 解决 Banner 和列表上下滑动抢手势的问题
            View innerRecyclerView = viewPager.getChildAt(0);
            if (innerRecyclerView != null) {
                setupNestedScroll(innerRecyclerView);
            }

            startAutoScroll();
        }

        private void startAutoScroll() {
            stopAutoScroll();
            if (bannerCount <= 1) {
                return;
            }
            autoScrollRunnable = new Runnable() {
                @Override
                public void run() {
                    // 平滑滚到下一张，不会跳回第一张
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    viewPager.postDelayed(this, AUTO_SCROLL_DELAY);
                }
            };
            viewPager.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
        }

        void stopAutoScroll() {
            if (autoScrollRunnable != null) {
                viewPager.removeCallbacks(autoScrollRunnable);
                autoScrollRunnable = null;
            }
        }

        // 处理横滑 / 竖滑手势冲突
        private void setupNestedScroll(final View inner) {
            inner.setOnTouchListener(new View.OnTouchListener() {

                private float downX;
                private float downY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            downX = event.getX();
                            downY = event.getY();
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_MOVE: {
                            float dx = event.getX() - downX;
                            float dy = event.getY() - downY;
                            if (Math.abs(dy) > Math.abs(dx)) {
                                // 竖向滑动交给列表
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                            } else {
                                // 横向滑动交给 Banner
                                boolean canScroll = ((RecyclerView) v)
                                        .canScrollHorizontally(dx < 0 ? 1 : -1);
                                v.getParent().requestDisallowInterceptTouchEvent(canScroll);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    return false;
                }
            });
        }

        // 根据 Banner 数量创建圆点
        private void addDots(int count) {
            indicator.removeAllViews();
            dots.clear();
            for (int i = 0; i < count; i++) {
                View dot = new View(indicator.getContext());
                int size = dp(6);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
                lp.setMarginStart(dp(3));
                lp.setMarginEnd(dp(3));
                dot.setLayoutParams(lp);
                dot.setBackground(createDotDrawable(false));
                indicator.addView(dot);
                dots.add(dot);
            }
            if (!dots.isEmpty()) {
                dots.get(0).setBackground(createDotDrawable(true));
            }
        }

        // 更新圆点：当前页高亮
        private void updateDots(int position) {
            for (int i = 0; i < dots.size(); i++) {
                dots.get(i).setBackground(createDotDrawable(i == position));
            }
        }

        // 生成一个小圆点：选中是实心白，未选中是半透明白
        private GradientDrawable createDotDrawable(boolean selected) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(selected ? 0xFFFFFFFF : 0x66FFFFFF);
            return drawable;
        }

        private int dp(float value) {
            return (int) (value * itemView.getResources().getDisplayMetrics().density + 0.5f);
        }
    }

    // ==================== 视频卡片 ViewHolder ====================
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