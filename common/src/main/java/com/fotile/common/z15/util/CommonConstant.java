package com.fotile.common.z15.util;

/**
 * Created by chenyqi on 2019/5/27.
 */

public class CommonConstant {
    /*********************************打包需要全部更改为false↓*********************************/
    /**
     * log和crash弹框都显示，打包改为 false
     */
    public static boolean LOG_AND_CRASH_SHOW = false;
    /**
     * 屏保处于测试状态，打包时改成 false
     */
    public static boolean IS_SCREEN_TEST = false;
    /**
     * 开发调试时，不上传bugly报错日志，crash不捕获报错日志，打包时改成 false
     */
    public static boolean BUGLY_LOG_INVAILD = false;
    /*********************************打包需要全部更改为false↑*********************************/
}
