package com.example.bilibili.ui.live;

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
import com.example.bilibili.model.bean.LiveRoom;
import com.example.bilibili.model.bean.SectionHeader;
import com.example.bilibili.ui.live.adapter.BannerAdapter;
import com.example.bilibili.ui.live.adapter.LiveAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 直播列表页
 * 两列网格展示直播房间卡片，支持下拉刷新
 */
public class LiveFragment extends Fragment {

    private SwipeRefreshLayout mRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv);
        mRefreshLayout = view.findViewById(R.id.layout_refresh);

        //1. 生成混合数据：Banner + 分区标题 + 房间
        List<Banner> banners = Banner.createMockData();
        final List<Object> items = buildItems();

        //2. 两列网格
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // Banner 和分区标题占满两列；房间卡片各占一列
                if (position == 0) {
                    return 2;
                }
                return items.get(position - 1) instanceof SectionHeader ? 2 : 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);

        //3. 间距（把 items 传给装饰器，让它知道哪些是整行）
        int spacing = getResources().getDimensionPixelSize(R.dimen.margin_small);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spacing, items));

        //4. 设置adapter
        LiveAdapter adapter = new LiveAdapter(banners, items);
        recyclerView.setAdapter(adapter);

        //下拉刷新
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    // 生成“分区标题 + 房间”混合列表
    private List<Object> buildItems() {
        List<LiveRoom> rooms = LiveRoom.createMockData();
        List<Object> items = new ArrayList<>();

        items.add(new SectionHeader("推荐"));
        addRooms(items, rooms, 0, 4);

        items.add(new SectionHeader("游戏"));
        addRooms(items, rooms, 4, 8);

        items.add(new SectionHeader("娱乐"));
        addRooms(items, rooms, 8, 12);

        return items;
    }

    private void addRooms(List<Object> items, List<LiveRoom> rooms, int from, int to) {
        for (int i = from; i < to && i < rooms.size(); i++) {
            items.add(rooms.get(i));
        }
    }

    //网格间距处理
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
                // 整行条目：左右统一间距，底部留间距，Banner 顶部也留一点
                outRect.left = spacing;
                outRect.right = spacing;
                outRect.top = position == 0 ? spacing : 0;
                outRect.bottom = spacing;
                return;
            }

            // 房间卡片：列号 = 前面出现过多少个房间，再对 2 取模
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
