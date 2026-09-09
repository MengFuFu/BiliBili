package com.example.bilibili.widget.bottombar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

/**
 * 自定义底部导航栏
 * 内部是一个横向LinearLayout，每个Tab宽度均分，点击时处理选中状态并回调
 */
public class BottomBar extends LinearLayout {

    private LinearLayout mTabLayout; //放所有Tab的横向容器
    private LayoutParams mTabParams; //每个Tab的布局参数
    private int mCurrentPosition; //当前选中的位置
    private OnTabSelectedListener mListener; //选中回调

    public BottomBar(Context context) {
        this(context, null);
    }
    //attrs 里面存放的是 XML 布局中写在这个控件标签上的所有属性。
    public BottomBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);

        mTabLayout = new LinearLayout(context);
        mTabLayout.setBackgroundColor(Color.WHITE);
        mTabLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(mTabLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //每个Tab宽度均分：weight = 1
        mTabParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        mTabParams.weight = 1;
    }

    //添加一个Tab
    public BottomBar addItem(CustomTabEntity entity) {
        final  BottomBarTab tab = new BottomBarTab(getContext());
        tab.setData(entity);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener == null) {
                    return;
                }
                int pos = tab.getTabPosition();
                if(mCurrentPosition == pos) {
                    //点击的是当前已选中的Tab
                    mListener.onTabReselected(pos);
                }
                else {
                    //切换到新Tab
                    int prePosition = mCurrentPosition;

                    mListener.onTabSelected(pos, prePosition);

                    tab.setSelected(true);
                    mListener.onTabUnselected(prePosition);
                    mTabLayout.getChildAt(prePosition).setSelected(false);
                    mCurrentPosition = pos;
                }
            }
        });
        tab.setTabPosition(mTabLayout.getChildCount());
        tab.setLayoutParams(mTabParams);
        mTabLayout.addView(tab);
        return this;
    }

    //批量添加Tab
    public void setTabEntities(ArrayList<CustomTabEntity> tabEntities) {
        for(CustomTabEntity entity : tabEntities) {
            addItem(entity);
        }
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        mListener = listener;
    }

    //代码里切换Tab（本质是模拟点击）
    public void setCurrentItem(final int position) {
        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                mTabLayout.getChildAt(position).performClick();
            }
        });
    }


    public interface OnTabSelectedListener {
        void onTabSelected(int position, int prePosition);

        void onTabUnselected(int position);

        void onTabReselected(int position);
    }
}
