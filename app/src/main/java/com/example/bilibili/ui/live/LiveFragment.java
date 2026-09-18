package com.example.bilibili.ui.live;

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
import com.example.bilibili.model.bean.LiveRoom;
import com.example.bilibili.model.bean.SectionHeader;
import com.example.bilibili.ui.live.adapter.LiveAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播列表页
 */
public class LiveFragment extends Fragment {

    // Banner + 分区标题 + 房间 的混合列表；装饰器和 Adapter 共用同一个对象
    private final List<Object> mItems = new ArrayList<>();
    private LiveAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.layout_refresh);

        LiveViewModel viewModel = new ViewModelProvider(this).get(LiveViewModel.class);

        // Banner 暂时保留假数据
        List<Banner> banners = Banner.createMockData();

        // 两列网格：Banner 和分区标题占满两列，房间卡片各占一列
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2; // Banner
                }
                return mItems.get(position - 1) instanceof SectionHeader ? 2 : 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);

        int spacing = getResources().getDimensionPixelSize(R.dimen.margin_small);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spacing, mItems));

        mAdapter = new LiveAdapter(banners, mItems);
        recyclerView.setAdapter(mAdapter);

        // 观察房间列表：重建混合列表
        viewModel.getRooms().observe(getViewLifecycleOwner(), new Observer<List<LiveRoom>>() {
            @Override
            public void onChanged(List<LiveRoom> rooms) {
                mItems.clear();
                mItems.add(new SectionHeader("推荐"));
                mItems.addAll(rooms);
                mAdapter.notifyDataSetChanged();
            }
        });

        // 观察刷新状态
        viewModel.getRefreshing().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                refreshLayout.setRefreshing(aBoolean);
            }
        });

        // 观察错误状态
        viewModel.getError().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    Toast.makeText(getContext(), "加载失败，请检查网络后重试", Toast.LENGTH_SHORT).show();
                    viewModel.consumeError();
                }
            }
        });

        // 下拉刷新
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh();
            }
        });

        // 初次加载
        viewModel.refresh();
    }

    // 网格间距处理
    static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int spacing;
        private final List<Object> items;

        GridSpacingItemDecoration(int spacing, List<Object> items) {
            this.spacing = spacing;
            this.items = items;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);

            boolean fullSpan;
            if (position == 0) {
                fullSpan = true; // Banner
            } else {
                fullSpan = items.get(position - 1) instanceof SectionHeader;
            }

            if (fullSpan) {
                outRect.left = spacing;
                outRect.right = spacing;
                outRect.top = position == 0 ? spacing : 0;
                outRect.bottom = spacing;
                return;
            }

            int column = countRoomsBefore(position) % 2;
            outRect.left = spacing - column * spacing / 2;
            outRect.right = (column + 1) * spacing / 2;
            outRect.top = 0;
            outRect.bottom = spacing;
        }

        private int countRoomsBefore(int position) {
            int count = 0;
            for (int i = 0; i < position - 1; i++) {
                if (items.get(i) instanceof LiveRoom) {
                    count++;
                }
            }
            return count;
        }
    }
}