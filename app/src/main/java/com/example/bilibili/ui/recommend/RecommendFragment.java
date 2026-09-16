package com.example.bilibili.ui.recommend;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.Banner;
import com.example.bilibili.model.bean.RecommendItem;
import com.example.bilibili.ui.recommend.adapter.RecommendAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页 - 推荐页
 * 顶部轮播 Banner + 两列视频卡片，支持下拉刷新和加载更多
 */
public class RecommendFragment extends Fragment {

    private SwipeRefreshLayout mRefreshLayout;
    private RecommendAdapter mAdapter;
    private RecommendViewModel mViewModel;

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

        //拿到ViewModel
        mViewModel = new ViewModelProvider(this).get(RecommendViewModel.class);

        //Banner是静态数据，这里创建一次即可
        List<Banner> banners = Banner.createMockData();

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

        mAdapter = new RecommendAdapter(banners, new ArrayList<>());
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
                if(lastVisible + 4 >= total) {
                    mViewModel.loadMore();
                }
            }
        });

        //下拉刷新
        //观察列表数据，变化时刷新列表
        mViewModel.getItems().observe(getViewLifecycleOwner(), new Observer<List<RecommendItem>>() {
            @Override
            public void onChanged(List<RecommendItem> recommendItems) {
                mAdapter.resetItems(recommendItems);
            }
        });
        //观察刷新状态，控制下拉转圈
        mViewModel.getRefreshing().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mRefreshLayout.setRefreshing(aBoolean);
            }
        });
        //观察错误状态，加载失败时弹提示
        mViewModel.getError().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null && aBoolean) {
                    Toast.makeText(getContext(), "加载失败，请检查网络后重试", Toast.LENGTH_SHORT).show();
                    mViewModel.consumeError(); //提示完重置
                }
            }
        });

        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.refresh();
            }
        });

        //初次加载
        mViewModel.refresh();
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
