package com.example.bilibili.ui.live.adapter;

import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.bilibili.R;
import com.example.bilibili.model.bean.Banner;
import com.example.bilibili.model.bean.LiveRoom;
import com.example.bilibili.model.bean.SectionHeader;
import com.example.bilibili.ui.live.liveplay.LivePlayActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 直播列表适配器：支持两种条目
 * - 顶部轮播图Banner
 * - 直播房间卡片
 */
public class LiveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BANNER = 0; //条目类型是轮播
    private static final int TYPE_ROOM = 1; //条目类型是直播间卡片
    private static final int TYPE_SECTION = 2; // 分区标题

    private final List<Banner> mBanners;
    // 里面是SectionHeader 和 LIveRoom 混合的列表
    private final List<Object> mItems;

    public LiveAdapter(List<Banner> mBanners, List<Object> mItems) {
        this.mBanners = mBanners;
        this.mItems = mItems;
    }

    //RecyclerView 问适配器：现在要画第 position 个条目，这个条目是什么样式？
    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return TYPE_BANNER;
        }
        Object item = mItems.get(position - 1);
        if(item instanceof SectionHeader) {
            return TYPE_SECTION;
        }
        return TYPE_ROOM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if(viewType == TYPE_BANNER) {
                return new BannerViewHolder(inflater.inflate(R.layout.item_live_banner, parent, false));
            }
            else if (viewType == TYPE_SECTION){
                return new SectionViewHolder(inflater.inflate(R.layout.item_live_partition, parent, false));
            }
            else {
                return new LiveViewHolder(inflater.inflate(R.layout.item_live_common, parent, false));
            }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof BannerViewHolder) {
            ((BannerViewHolder) holder).bind(mBanners);
        }
        else if (holder instanceof SectionViewHolder) {
            SectionHeader header = (SectionHeader) mItems.get(position - 1);
            ((SectionViewHolder) holder).bind(header);
        } else {
            LiveRoom room = (LiveRoom) mItems.get(position - 1);
            bindRoom((LiveViewHolder) holder, room);
        }
    }

    //避免 ViewHolder 被回收后自动轮播还在跑
    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if(holder instanceof BannerViewHolder) {
            ((BannerViewHolder) holder).stopAutoScroll();
        }
        super.onViewRecycled(holder);
    }

    //填充房间卡片
    private void bindRoom(LiveViewHolder holder, LiveRoom room) {
        holder.tvUserName.setText(room.getUserName());
        holder.tvTitle.setText(room.getTitle());
        holder.tvAreaName.setText(room.getAreaName());
        holder.tvOnline.setText(formatOnline(room.getOnline()));

        // 用 Glide 加载真实封面
        Glide.with(holder.ivCover.getContext())
                .load(room.getCoverUrl())
                .placeholder(R.drawable.bili_default_image_tv)
                .centerCrop()
                .into(holder.ivCover);

        holder.cvContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LivePlayActivity.startActivity(
                        holder.cvContainer.getContext(),
                        room.getTitle(),
                        room.getUserName(),
                        room.getOnline()
                );
            }
        });
    }

    // 在线人数格式化：超过 1 万显示成「x.x万」
    private String formatOnline(int online) {
        if (online >= 10000) {
            return String.format("%.1f万", online / 10000.0);
        }
        return String.valueOf(online);
    }

    @Override
    public int getItemCount() {
        //1个banner + 房间数量
        return 1 + (mItems == null ? 0 : mItems.size());
    }

    // ==================== 轮播图 ViewHolder ====================
    static class BannerViewHolder extends RecyclerView.ViewHolder {

        private static final int AUTO_SCROLL_DELAY = 3000; //3秒滚一次

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
                    // position 可能很大，取模得到真实页码
                    updateDots(position % bannerCount);
                }
            });

            // 从中间位置开始，这样前后都能滑，配合无限数量就永远不会“跳回第一张”
            if (bannerCount > 0) {
                viewPager.setCurrentItem(bannerCount * 1000, false);
            }

            View innerRecyclerView = viewPager.getChildAt(0);
            if (innerRecyclerView != null) {
                setupNestedScroll(innerRecyclerView);
            }

            startAutoScroll();
        }

        // ===== 自动轮播 =====
        private void startAutoScroll() {
            stopAutoScroll();
            if (bannerCount <= 1) {
                return;
            }
            autoScrollRunnable = new Runnable() {
                @Override
                public void run() {
                    // 永远平滑地滚到下一张，不会跳回第一张
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    viewPager.postDelayed(this, AUTO_SCROLL_DELAY);
                }
            };
            viewPager.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
        }
        private void stopAutoScroll() {
            if(autoScrollRunnable != null) {
                viewPager.removeCallbacks(autoScrollRunnable);
                autoScrollRunnable = null;
            }
        }

        // 解决 Banner 与“首页子 Tab / 纵向列表”抢手势的问题
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
                            //按下先禁止父级拦截，保证横向手势能到banner
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_MOVE: {
                            float dx = event.getX() - downX;
                            float dy = event.getY() - downY;
                            if (Math.abs(dy) > Math.abs(dx)) {
                                // 纵向：交给列表滚动
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                            } else {
                                // 横向：banner 自己滑；滑到边缘就交给外层（首页子 Tab）
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
                    // 返回 false，让 RecyclerView 自己继续处理滚动
                    return false;
                }
            });
        }

        //按banner数量创建圆点
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

        //更新圆点：当前位置宽高
        private void updateDots(int position) {
            for (int i = 0; i < dots.size(); i++) {
                dots.get(i).setBackground(createDotDrawable(i == position));
            }
        }

        //生成一个圆形小点：选中白色实心，未选中半透明白
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

    // ==================== 房间卡片 ViewHolder ====================
    static class LiveViewHolder extends RecyclerView.ViewHolder {

        CardView cvContainer;
        ImageView ivCover;
        TextView tvUserName;
        TextView tvTitle;
        TextView tvAreaName;
        TextView tvOnline;

        public LiveViewHolder(@NonNull View itemView) {
            super(itemView);
            cvContainer = itemView.findViewById(R.id.cv_container);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAreaName = itemView.findViewById(R.id.tv_area_name);
            tvOnline = itemView.findViewById(R.id.tv_online);
        }
    }

    // ================= 分区标题 ViewHolder ================
    static class SectionViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }

        void bind(SectionHeader header) {
            tvName.setText(header.getTitle());
        }
    }
}
