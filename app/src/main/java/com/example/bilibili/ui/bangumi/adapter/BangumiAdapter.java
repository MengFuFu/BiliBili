package com.example.bilibili.ui.bangumi.adapter;

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
import com.example.bilibili.model.bean.BangumiItem;
import com.example.bilibili.model.bean.Banner;
import com.example.bilibili.ui.bangumi.BangumiDetailActivity;
import com.example.bilibili.ui.live.adapter.BannerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 番剧列表适配器
 * - 顶部轮播 Banner
 * - 分区入口
 * - 番剧卡片
 */
public class BangumiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BANNER = 0; //轮播图
    private static final int TYPE_ENTRY = 1; // 分区入口
    private static final int TYPE_ITEM = 2; //番剧卡片

    private final List<Banner> mBanners;
    private final List<BangumiItem> mItems;

    public BangumiAdapter(List<Banner> mBanners, List<BangumiItem> mItems) {
        this.mBanners = mBanners;
        this.mItems = mItems;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return TYPE_BANNER;
        }
        if(position == 1) {
            return TYPE_ENTRY;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == TYPE_BANNER) {
            return new BannerViewHolder(inflater.inflate(R.layout.item_live_banner, parent, false));
        } else if (viewType == TYPE_ENTRY) {
            return new EntryViewHolder(inflater.inflate(R.layout.item_bangumi_entry, parent, false));
        }
        return new BangumiViewHolder(inflater.inflate(R.layout.item_bangumi, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof BannerViewHolder) {
            ((BannerViewHolder) holder).bind(mBanners);
        } else if (holder instanceof BangumiViewHolder) {
            //番剧卡片从position2 开始
            bindItem((BangumiViewHolder) holder, mItems.get(position - 2));
        }

    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if(holder instanceof BannerViewHolder) {
            ((BannerViewHolder) holder).stopAutoScroll();
        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return 2 + mItems.size();
    }

    private void bindItem(BangumiViewHolder holder, BangumiItem item) {
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

        //点整张卡片跳转到番剧详细页
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BangumiDetailActivity.startActivity(v.getContext(), item.getTitle());
            }
        });
    }

    // ==================== Banner ViewHolder ====================
    static class BannerViewHolder extends RecyclerView.ViewHolder {

        private static final int AUTO_SCROLL_DELAY = 3000;

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

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    updateDots(position % bannerCount);
                }
            });

            if(bannerCount > 0) {
                viewPager.setCurrentItem(bannerCount * 1000, false);
            }

            View innerRecyclerView = viewPager.getChildAt(0);
            if(innerRecyclerView != null) {
                setupNestedScroll(innerRecyclerView);
            }

            startAutoScroll();
        }

        private void startAutoScroll() {
            stopAutoScroll();
            if(bannerCount <= 1) {
                return;
            }
            autoScrollRunnable = new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    viewPager.postDelayed(this, AUTO_SCROLL_DELAY);
                }
            };
            viewPager.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
        }

        void stopAutoScroll() {
            if(autoScrollRunnable != null) {
                viewPager.removeCallbacks(autoScrollRunnable);
                autoScrollRunnable = null;
            }
        }

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
                            if(Math.abs(dy) > Math.abs(dx)) {
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                            }
                            else {
                                boolean canScroll = ((RecyclerView) v).canScrollHorizontally(dx < 0 ? 1 : -1);
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
            if(!dots.isEmpty()) {
                dots.get(0).setBackground(createDotDrawable(true));
            }
        }

        private void updateDots(int position) {
            for (int i = 0; i < dots.size(); i++) {
                dots.get(i).setBackground(createDotDrawable(i == position));
            }
        }

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

    // ==================== 分区入口 ViewHolder ====================
    static class EntryViewHolder extends RecyclerView.ViewHolder {

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    // ==================== 番剧卡片 ViewHolder ====================
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
