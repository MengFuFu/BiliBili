package com.example.bilibili.ui.recommend;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.Banner;
import com.example.bilibili.model.bean.RecommendItem;
import com.example.bilibili.ui.recommend.adapter.RecommendAdapter;

import java.util.List;

/**
 * 首页 - 推荐页
 * 顶部轮播 Banner + 两列视频卡片，支持下拉刷新和加载更多
 */
public class RecommendFragment extends Fragment {

    private SwipeRefreshLayout mRefreshLayout;
    private RecommendAdapter mAdapter;
    private int mPage = 1; //当前加载到第几页
    private boolean mLoadingMore = false; //是否正在加载更多

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        mRefreshLayout = view.findViewById(R.id.layout_refresh);

        //初始只加载第一页
        List<Banner> banners = Banner.createMockData();
        List<RecommendItem> items = RecommendItem.createMockData(1);

        //两列网格
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);

        //统一的网格间距
        int spacing = getResources().getDimensionPixelSize(R.dimen.margin_small);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spacing));

        mAdapter = new RecommendAdapter(banners, items);
        recyclerView.setAdapter(mAdapter);

        //滚动监听：快滚到底部时触发加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                if(manager == null) {
                    return;
                }
                int lastVisible = manager.findLastVisibleItemPosition();
                int total = mAdapter.getItemCount();
                //还差4个条目就到底时，开始加载下一页
                if(lastVisible + 4 >= total && !mLoadingMore) {
                    loadMore();
                }
            }
        });

        //下拉刷新
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    //下拉刷新：回到第一页，重新生成数据
    private void refresh() {
        mPage = 1;
        //用延迟模拟网络请求
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.resetItems(RecommendItem.createMockData(1));
                mRefreshLayout.setRefreshing(false);
            }
        }, 600);
    }

    //加载更多：追加下一页假数据
    private void loadMore() {
        mLoadingMore = true;
        mPage++;
        //用延迟模拟网络请求
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.addItems(RecommendItem.createMockData(mPage));
                mLoadingMore = false;
            }
        }, 600);
    }

    // 网格间距处理：Banner 整行留边距，视频卡片左右对称
    static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int spacing;

        GridSpacingItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);

            if (position == 0) {
                // Banner 占满整行
                outRect.left = spacing;
                outRect.right = spacing;
                outRect.top = spacing;
                outRect.bottom = spacing;
                return;
            }

            // 视频卡片从 position 1 开始，奇偶交替决定左右列
            int column = (position - 1) % 2;
            outRect.left = spacing - column * spacing / 2;
            outRect.right = (column + 1) * spacing / 2;
            outRect.top = 0;
            outRect.bottom = spacing;
        }
    }
}
