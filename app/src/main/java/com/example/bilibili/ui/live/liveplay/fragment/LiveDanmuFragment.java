package com.example.bilibili.ui.live.liveplay.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.DanmuMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * 弹幕列表页（互动）
 */
public class LiveDanmuFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private DanmuAdapter mAdapter;
    private Runnable mAppendRunnable;
    private int mCounter = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_danmu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new DanmuAdapter();
        mRecyclerView.setAdapter(mAdapter);

        startDanmu();
    }

    // 每隔800ms追加一条弹幕，并滚到底部
    private void startDanmu() {
        stopDanmu();
        mAppendRunnable = new Runnable() {
            @Override
            public void run() {
                mAdapter.add(DanmuMsg.mock(mCounter++));
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                mRecyclerView.postDelayed(this, 800);
            }
        };
        mRecyclerView.postDelayed(mAppendRunnable, 800);
    }

    private void stopDanmu() {
        if(mAppendRunnable != null) {
            mRecyclerView.removeCallbacks(mAppendRunnable);
            mAppendRunnable = null;
        }
    }

    @Override
    public void onDestroyView() {
        stopDanmu(); //页面销毁时停止定时器，避免泄露
        super.onDestroyView();
    }

    //弹幕列表适配器
    private static class DanmuAdapter extends RecyclerView.Adapter<DanmuAdapter.DanmuViewHolder> {

        private final List<DanmuMsg> data = new ArrayList<>();

        void add(DanmuMsg msg) {
            data.add(msg);
            notifyItemInserted(data.size() - 1);
        }

        @NonNull
        @Override
        public DanmuAdapter.DanmuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_danmu, parent, false);
            return new DanmuViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DanmuAdapter.DanmuViewHolder holder, int position) {
            DanmuMsg msg = data.get(position);
            holder.tvDanmu.setText(msg.getSenderNick() + "：" + msg.getContent());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class DanmuViewHolder extends RecyclerView.ViewHolder {
            TextView tvDanmu;

            public DanmuViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDanmu = (TextView) itemView;
            }
        }
    }
}
