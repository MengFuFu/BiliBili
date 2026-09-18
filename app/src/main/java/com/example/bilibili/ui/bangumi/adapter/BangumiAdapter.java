package com.example.bilibili.ui.bangumi.adapter;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.bilibili.R;
import com.example.bilibili.data.db.AppDatabase;
import com.example.bilibili.data.db.FollowEntity;
import com.example.bilibili.model.bean.BangumiItem;
import com.example.bilibili.model.bean.Banner;
import com.example.bilibili.ui.bangumi.BangumiDetailActivity;
import com.example.bilibili.ui.live.adapter.BannerAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private final int mSeasonType; // 1=番剧，2=影视
    // 已追番/已想看的标题集合，避免每条都查数据库
    private final Set<String> mFollowedTitles = new HashSet<>();


    public BangumiAdapter(List<Banner> mBanners, List<BangumiItem> mItems, int mSeasonType) {
        this.mBanners = mBanners;
        this.mItems = mItems;
        this.mSeasonType = mSeasonType;
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
            int entryLayout = mSeasonType == 2
                    ? R.layout.item_bangumi_entry_movie
                    : R.layout.item_bangumi_entry;
            return new EntryViewHolder(inflater.inflate(entryLayout, parent, false));
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

    //清空并重新填充数据（配合LiveData使用）
    public void resetItems(List<BangumiItem> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    // 从数据库加载已追番/已想看的标题，刷新按钮状态
    public void reloadFollowed(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<FollowEntity> list = AppDatabase.getInstance(context)
                        .followDao()
                        .getAll();
                final Set<String> titles = new HashSet<>();
                for (FollowEntity e : list) {
                    titles.add(e.title);
                }
                // 回到主线程更新集合并刷新
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mFollowedTitles.clear();
                        mFollowedTitles.addAll(titles);
                        notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void bindItem(BangumiViewHolder holder, BangumiItem item) {

        // 影视显示"想看"，番剧显示"追番"
        final String followText = mSeasonType == 2 ? "想看" : "追番";
        final String followedText = mSeasonType == 2 ? "已想看" : "已追番";

        // 根据内存里的已追番集合设置按钮初始状态
        boolean followed = mFollowedTitles.contains(item.getTitle());
        holder.btnFollow.setText(followed ? followedText : followText);

        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView btn = (TextView) v;
                final String title = item.getTitle();

                if (followText.contentEquals(btn.getText())) {
                    // 追番/想看：更新 UI + 写入数据库
                    btn.setText(followedText);
                    mFollowedTitles.add(title);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AppDatabase.getInstance(btn.getContext())
                                    .followDao()
                                    .insert(new FollowEntity(title));
                        }
                    }).start();
                } else {
                    // 取消追番/想看
                    btn.setText(followText);
                    mFollowedTitles.remove(title);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AppDatabase.getInstance(btn.getContext())
                                    .followDao()
                                    .delete(new FollowEntity(title));
                        }
                    }).start();
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
