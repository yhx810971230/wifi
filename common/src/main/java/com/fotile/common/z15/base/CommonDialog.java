package com.fotile.common.z15.base;


import android.app.Dialog;
import android.content.Context;

import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.fotile.common.z15.R;


/**
 * 文件名称：CommonDialog
 * 创建时间：2018/11/29 10:48
 * 文件作者：chenyqi
 * 功能描述：加载数据dialog
 */
public class CommonDialog extends Dialog {
    TextView tv_loading;
    ImageView imgLoading;
    public static int width;
    public static int height;
    public static int screen_margin;
    public static int bottom_height;
    public static int state_bar_height;
    private RotateAnimation mRotateAnimation;


    public CommonDialog(Context context, int theme) {
        super(context, theme);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common);
        tv_loading = (TextView) findViewById(R.id.tv_loading);
        imgLoading = (ImageView) findViewById(R.id.img_loading);
        imgLoading.setImageResource(R.mipmap.ic_white_loading);
        //先对imageView进行测量，以便拿到它的宽高（否则getMeasuredWidth为0）
        imgLoading.measure(0, 0);
        mRotateAnimation = new RotateAnimation(0, 360, imgLoading.getMeasuredWidth() / 2, imgLoading.getMeasuredHeight() / 2);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(1000);
        mRotateAnimation.setRepeatCount(-1);
        imgLoading.startAnimation(mRotateAnimation);
    }

    @Override
    public void show() {
//        //屏幕长宽
//        int width = 600;
//        int height = 1024;
//        //屏幕缩进距离
//        screen_margin = (int) getContext().getApplicationContext().getResources().getDimension(com.fotile.common.z15.R.dimen.screen_margin);
//        //bottom栏高度，没有计算向上小箭头
//        bottom_height = (int) getContext().getApplicationContext().getResources().getDimension(com.fotile.common.z15.R.dimen.state_bar_height);
//        //顶部状态栏高度
//        state_bar_height = (int) getContext().getApplicationContext().getResources().getDimension(com.fotile.common.z15.R.dimen.bottom_part_height);
//        //获取对话框窗口
//        Window dialogWindow = getWindow();
//        //设置显示窗口的宽高
//        dialogWindow.setLayout(width - screen_margin * 2, height - screen_margin * 2 - bottom_height - state_bar_height);
//        //设置显示窗口的位置
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.x = 0 + screen_margin;
//        layoutParams.y = state_bar_height + screen_margin;
//        dialogWindow.setAttributes(layoutParams);
        super.show();

    }

    @Override
    public void dismiss() {
        if (mRotateAnimation != null) {
            mRotateAnimation.cancel();
        }
        super.dismiss();
    }
}
