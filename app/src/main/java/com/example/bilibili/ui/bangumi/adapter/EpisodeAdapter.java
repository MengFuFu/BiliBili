package com.example.bilibili.ui.bangumi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bilibili.R;
import com.example.bilibili.ui.video.VideoPlayActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 集数列表适配器
 */
public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    private final List<Integer> mEpisodes;
    private final String mTitle; //番剧名

    public EpisodeAdapter(int count, String title) {
        mEpisodes = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            mEpisodes.add(i);
        }
        mTitle = title;
    }

    @NonNull
    @Override
    public EpisodeAdapter.EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_episode, parent, false);
        return new EpisodeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeAdapter.EpisodeViewHolder holder, int position) {
        int episode = mEpisodes.get(position);
        holder.tvEpisode.setText("第" + episode + "话");

        //点击某一集，跳转到视频播放页
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPlayActivity.startActivity(
                        v.getContext(),
                        mTitle + "第" + episode + "话",
                        "番剧UP主",
                        episode + "万"
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEpisodes.size();
    }

    static class EpisodeViewHolder extends RecyclerView.ViewHolder {

        TextView tvEpisode;

        public EpisodeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEpisode = itemView.findViewById(R.id.tv_episode);
        }
    }
}
