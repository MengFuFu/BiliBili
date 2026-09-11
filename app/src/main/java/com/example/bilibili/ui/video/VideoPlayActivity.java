package com.example.bilibili.ui.video;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.bilibili.R;
import com.example.bilibili.model.bean.DanmuMsg;

import java.util.HashMap;
import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.Duration;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * 视频播放页骨架：
 * 播放器 + 返回栏 + 标题 + 自定义控制栏
 */
public class VideoPlayActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_UP_NAME = "extra_up_name";
    public static final String EXTRA_PLAY = "extra_play";

    //测试视频
    private static final String TEST_URL = "https://media.w3.org/2010/05/sintel/trailer.mp4";

    private ExoPlayer mPlayer;

    //控制栏相关
    private View mControlBar;
    private Runnable mHideControlRunnable;

    //进度条相关
    private SeekBar mSeekBar;
    private Runnable mProgressRunnable;
    private boolean mIsUserSeeking = false;

    //全屏相关
    private boolean mIsFullscreen = false;
    private int mPlayerNormalHeight;

    // 音量和倍速相关
    private SeekBar mVolumeSeekBar;
    private TextView mBtnSpeed;
    private final float[] mSpeeds = {1.0f, 1.25f, 1.5f, 2.0f};
    private int mSpeedIndex = 0;

    // 弹幕相关
    private DanmakuView mDanmakuView;
    private DanmakuContext mDanmakuContext;
    private Runnable mDanmuRunnable;
    private int mDanmuCounter = 0;
    private Random mRandom = new Random();

    // 点赞 / 投币 / 收藏计数
    private int mLikes = 0;
    private int mCoins = 0;
    private int mFavorites = 0;

    //外部用这个方法跳转进来
    public static void startActivity(Context context, String title, String upName, String play) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_UP_NAME, upName);
        intent.putExtra(EXTRA_PLAY, play);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_play);

        // 让页面避开系统状态栏
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //记录播放器的正常高度，退出全屏时恢复
        mPlayerNormalHeight = dp2px(200);

        //显示上个页面传过来的标题
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String upName = getIntent().getStringExtra(EXTRA_UP_NAME);
        String play = getIntent().getStringExtra(EXTRA_PLAY);

        //顶部标题栏
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(title);

        //填充下方信息区域
        initInfo(title, upName, play);

        //返回按钮
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initPlayer();

        initControls(); //播放器建好之后再设置控制栏

        initDanmaku(); // 初始化弹幕
    }

    //初始化ExoPlayer并播放测试视频
    private void initPlayer() {
        PlayerView playerView = findViewById(R.id.player_view);

        mPlayer = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(mPlayer);

        mPlayer.setMediaItem(MediaItem.fromUri(TEST_URL));
        mPlayer.prepare();
        mPlayer.setPlayWhenReady(true);
    }

    //初始化控制栏：显示/隐藏、播放/暂停、全屏、进度条
    private void initControls() {
        mControlBar = findViewById(R.id.control_bar);

        //点播放器区域，切换控制栏显示/隐藏
        findViewById(R.id.player_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleControlBar();
            }
        });

        //播放 / 暂停
        findViewById(R.id.iv_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlay();
            }
        });

        //全屏
        findViewById(R.id.iv_fullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullscreen();
            }
        });

        //监听播放状态：同步播放按钮图标
        mPlayer.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                ImageView ivPlay = findViewById(R.id.iv_play);
                ivPlay.setImageResource(isPlaying ? R.drawable.ic_tv_stop : R.drawable.ic_tv_play);
            }
        });

        //一开始显示控制栏，并开始倒计时隐藏
        showControlBar();

        initSeekBar();

        initVolumeAndSpeed(); // 初始化音量和倍速
    }

    private void toggleControlBar() {
        if(mControlBar.getVisibility() == View.VISIBLE) {
            hideControlBarNow();
        }
        else {
            showControlBar();
        }
    }

    private void showControlBar() {
        mControlBar.setVisibility(View.VISIBLE);
        removeHideCallback();
        mHideControlRunnable = new Runnable() {
            @Override
            public void run() {
                hideControlBarNow();
            }
        };
        mControlBar.postDelayed(mHideControlRunnable, 3000);
    }

    private void hideControlBarNow() {
        mControlBar.setVisibility(View.GONE);
        removeHideCallback();
    }

    private void removeHideCallback() {
        if(mHideControlRunnable != null) {
            mControlBar.removeCallbacks(mHideControlRunnable);
            mHideControlRunnable = null;
        }
    }

    private void togglePlay() {
        if(mPlayer == null) {
            return;
        }
        if(mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        else {
            mPlayer.play();
        }
    }

    private void toggleFullscreen() {
        mIsFullscreen = !mIsFullscreen;
        View playerContainer = findViewById(R.id.player_container);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) playerContainer.getLayoutParams();

        if(mIsFullscreen) {
            //全屏：隐藏下方内容区，播放器占满，切横屏
            findViewById(R.id.content_container).setVisibility(View.GONE);
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            //退出全屏：恢复下方内容区、播放器高度、竖屏
            findViewById(R.id.content_container).setVisibility(View.VISIBLE);
            lp.height = mPlayerNormalHeight;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        playerContainer.setLayoutParams(lp);
    }

    //初始化进度条
    private void initSeekBar() {
        mSeekBar = findViewById(R.id.seek_bar);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserSeeking = true; //用户开始拖动，暂停自动更新
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserSeeking = false;
                if(mPlayer != null) {
                    mPlayer.seekTo(seekBar.getProgress());
                }
            }
        });

        startProgressUpdates();
    }

    // 初始化音量和倍速
    private void initVolumeAndSpeed() {
        mVolumeSeekBar = findViewById(R.id.volume_seek_bar);
        mVolumeSeekBar.setMax(100);
        mVolumeSeekBar.setProgress(100);
        mVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // ExoPlayer 的音量范围是 0~1，所以这里要除以 100
                if (fromUser && mPlayer != null) {
                    mPlayer.setVolume(progress / 100f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mBtnSpeed = findViewById(R.id.btn_speed);
        mBtnSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 循环切换：1.0x -> 1.25x -> 1.5x -> 2.0x -> 1.0x
                mSpeedIndex = (mSpeedIndex + 1) % mSpeeds.length;
                float speed = mSpeeds[mSpeedIndex];
                if (mPlayer != null) {
                    mPlayer.setPlaybackSpeed(speed);
                }
                mBtnSpeed.setText(speed + "x");
            }
        });
    }

    //初始化弹幕：让DanmakuView准备好，并监听准备完成回调
    private void initDanmaku() {
        mDanmakuView = findViewById(R.id.danmaku_view);

        mDanmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                runOnUiThread(() -> {
                    mDanmakuView.setVisibility(View.VISIBLE);
                    mDanmakuView.start();
                    startDanmakuLoop();
                });
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {}

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {}

            @Override
            public void drawingFinished() {}
        });

        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.setDuplicateMergingEnabled(false);
        mDanmakuContext.setDanmakuMargin(dp2px(20));

        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 4);
        mDanmakuContext.setMaximumLines(maxLinesPair);

        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        mDanmakuContext.preventOverlapping(overlappingEnablePair);

        // 重点：等View布局测量完成后再执行prepare
        mDanmakuView.post(() -> {
            mDanmakuView.prepare(new BaseDanmakuParser() {
                @Override
                protected IDanmakus parse() {
                    return new Danmakus();
                }
            }, mDanmakuContext);
        });
    }


    //每隔800毫秒自动添加一条假弹幕
    private void startDanmakuLoop() {
        stopDanmakuLoop();
        mDanmuRunnable = new Runnable() {
            @Override
            public void run() {
                addDanmaku(DanmuMsg.mock(mDanmuCounter++).getContent());
                // 随机间隔：600~1400ms 发下一条
                int delay = 600 + mRandom.nextInt(800);
                mDanmakuView.postDelayed(this, delay);
            }
        };
        //第一次等300毫秒再发，让弹幕层有时间准备好
        mDanmakuView.postDelayed(mDanmuRunnable, 300);
    }

    private void stopDanmakuLoop() {
        if(mDanmuRunnable != null) {
            mDanmakuView.removeCallbacks(mDanmuRunnable);
            mDanmuRunnable = null;
        }
    }


    //手动添加一条从右往左滚动的弹幕
    private void addDanmaku(String content) {
        if(mDanmakuView == null || mDanmakuContext == null || !mDanmakuView.isPrepared()) {
            return;
        }

        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.textColor = Color.WHITE;
        danmaku.padding = dp2px(8);

        // 1. 随机文字大小：12~16dp
        danmaku.textSize = dp2px(14);

        // 2. 随机存活时长：8000~15000 毫秒（8~15秒滚完一屏）
        // 时长越短速度越快，时长越长速度越慢，以此实现弹幕速度错落
        long randomMs = 8000L + mRandom.nextInt(7000);
        danmaku.duration = new Duration(randomMs);

        danmaku.setTime(mDanmakuView.getCurrentTime() + 500);
        mDanmakuView.addDanmaku(danmaku);

    }

    //填充视频信息区，并处理关注、点赞、投币、收藏
    private void initInfo(String title, String upName, String play) {
        TextView tvVideoTitle = findViewById(R.id.tv_video_title);
        tvVideoTitle.setText(title);

        TextView tvUpName = findViewById(R.id.tv_up_name);
        tvUpName.setText(upName);

        TextView tvPlayCount = findViewById(R.id.tv_play_count);
        tvPlayCount.setText(play + "次播放");

        TextView tvDanmuCount = findViewById(R.id.tv_danmu_count);
        tvDanmuCount.setText("弹幕 1261");

        //关注按钮
        findViewById(R.id.btn_follow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView btn = (TextView) v;
                if("+关注".contentEquals(btn.getText())) {
                    btn.setText("已关注");
                }
                else {
                    btn.setText("+关注");
                }
            }
        });

        //点赞：每点一次 +1
        findViewById(R.id.btn_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView btn = (TextView) v;
                btn.setText("点赞" + (++mLikes));
            }
        });

        // 投币
        findViewById(R.id.btn_coin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView btn = (TextView) v;
                btn.setText("投币 " + (++mCoins));
            }
        });

        // 收藏
        findViewById(R.id.btn_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView btn = (TextView) v;
                btn.setText("收藏 " + (++mFavorites));
            }
        });
    }

    //自动更新进度条
    private void startProgressUpdates() {
        stopProgressUpdates();
        mProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if(mPlayer != null && !mIsUserSeeking && mPlayer.getDuration() > 0) {
                    mSeekBar.setMax((int) mPlayer.getDuration());
                    mSeekBar.setProgress((int) mPlayer.getCurrentPosition());
                }
                mSeekBar.postDelayed(this, 50);
            }
        };
        mSeekBar.postDelayed(mProgressRunnable, 50);
    }

    private void stopProgressUpdates() {
        if(mProgressRunnable != null) {
            mSeekBar.removeCallbacks(mProgressRunnable);
            mProgressRunnable = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPlayer != null) {
            mPlayer.setPlayWhenReady(false); //离开页面暂停播放
        }
        // 页面不可见时停止发弹幕并暂停弹幕绘制
        stopDanmakuLoop();
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mPlayer != null) {
            mPlayer.setPlayWhenReady(true); //回到页面继续播放
        }
        // 页面可见时恢复弹幕
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            if (mDanmakuView.isPaused()) {
                mDanmakuView.resume();
            }
            startDanmakuLoop();
        }
    }

    @Override
    protected void onDestroy() {
        if(mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        // 释放弹幕资源
        stopDanmakuLoop();
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
        removeHideCallback();
        stopProgressUpdates();
        super.onDestroy();
    }

    private int dp2px(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
