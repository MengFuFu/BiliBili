package com.example.bilibili.widget.bottombar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.bilibili.R;

/**
 * 底栏的一个Tab：上面图标、下面文字
 * 根据选中、未选中状态切换图标和文字
 */

public class BottomBarTab extends FrameLayout {

    private ImageView mIvIcon; //图标
    private TextView mTvTitle; //文字
    private Context mContext;
    private int mTabPosition = -1; //这个Tab在底栏中的位置
    private CustomTabEntity mEntity; //这个Tab的数据

    public BottomBarTab(Context context) {
        this(context, null);
    }
    public BottomBarTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public BottomBarTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //设置数据，并初始化内部的图标和文字
    public void setData(CustomTabEntity entity) {
        init(getContext(), entity);
    }

    private void init(Context context, CustomTabEntity entity) {
        mContext = context;
        mEntity = entity;

        //点击时的水波纹背景
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{androidx.appcompat.R.attr.selectableItemBackgroundBorderless});
        Drawable drawable = typedArray.getDrawable(0);
        setBackground(drawable);
        typedArray.recycle();

        //垂直容器：图标在上、文字在下、整体居中
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        LayoutParams containerLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        containerLp.gravity = Gravity.CENTER;
        container.setLayoutParams(containerLp);

        //图标默认先显示“未选中”
        mIvIcon = new ImageView(context);
        int iconSize = dp2px(20);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(iconSize, iconSize);
        mIvIcon.setImageResource(mEntity.getTabUnSelectedIcon());
        container.addView(mIvIcon);

        //文字
        mTvTitle = new TextView(context);
        mTvTitle.setText(mEntity.getTabTitle());
        LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvLp.topMargin = dp2px(2);
        mTvTitle.setTextSize(10);
        mTvTitle.setLayoutParams(tvLp);
        container.addView(mTvTitle);
        //把我们代码动态 new 出来的垂直 LinearLayout（里面装了 ImageView+TextView），加到当前这个 BottomBarTab 控件自己内部。
        addView(container);
    }

    //切换选中、未选中状态：换图标和文字
    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if(selected) {
            mIvIcon.setImageResource(mEntity.getTabSelectedIcon());
            mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.theme_color_primary));
        }
        else {
            mIvIcon.setImageResource(mEntity.getTabUnSelectedIcon());
            mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
        }
    }

    //记录这个Tab的位置，第一个Tab默认选中
    public void setTabPosition(int position) {
        mTabPosition = position;
        if(position == 0) {
            setSelected(true);
        }
    }

    public int getTabPosition() {
        return mTabPosition;
    }

    private int dp2px(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
