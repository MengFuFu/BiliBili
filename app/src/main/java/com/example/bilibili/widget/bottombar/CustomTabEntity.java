package com.example.bilibili.widget.bottombar;

import androidx.annotation.DrawableRes;

/**
 * - `widget`：专门存放自定义 View、自定义控件的包。Android 开发惯例：自己封装的控件、组件全部放`widget`包。
 * - `bottombar`：widget 下面再细分，放底部导航栏这一组相关的全部类。
 */

/**
 * 定义一个底部Tab需要的数据
 * 任何实现这个接口的类，都可以作为BottomBar里的一个Tab
 */

public interface CustomTabEntity {
    //这个Tab显示的文字，例如“首页”
    String getTabTitle();

    //选中状态下的图标资源
    @DrawableRes
    int getTabSelectedIcon();

    //未选中状态下的图标资源
    @DrawableRes
    int getTabUnSelectedIcon();
}
