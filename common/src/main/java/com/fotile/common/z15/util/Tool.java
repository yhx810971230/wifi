package com.fotile.common.z15.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.fotile.common.z15.R;
import com.fotile.common.z15.util.log.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

/**
 * 文件名称：MusicTool
 * 创建时间：2018/7/4 11:50
 * 文件作者：yaohx
 * 功能描述：工具类
 */
public class Tool {

    private static long last_time = 0;

    /**
     * 没有连接网络
     */
    public static final int NETWORK_NONE = -1;
    /**
     * 移动网络
     */
    public static final int NETWORK_MOBILE = 0;
    /**
     * 无线网络
     */
    public static final int NETWORK_WIFI = 1;
    /**
     * 黑屏广播
     */
    public static final String ACTION_SYSTEM_SLEEP = "com.cvte.androidsystemtoolbox.action.SYSTEM_SLEEP";

    /**
     * 限制按钮的快速点击情况
     *
     * @return true 执行click事件
     */
    public static boolean fastclick() {
        long time = System.currentTimeMillis();
        long interval = time - last_time;
        last_time = time;
        if (interval > 400) {
            return true;
        }
        return false;
    }

    public static boolean fastclick_900() {
        long time = System.currentTimeMillis();
        long interval = time - last_time;
        last_time = time;
        if (interval > 900) {
            return true;
        }
        return false;
    }

    /**
     * 将屏幕亮度调暗
     */
    public static void setCurrentBrightDark(Context context) {
        try {
            //当前系统亮度
            int currentBright = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            //将亮度保存
            PreferenceUtil.setPreferenceValue(context, PreferenceUtil.BRIGHT_LAST, currentBright);

            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, currentBright * 5
                    / 100);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将屏幕亮度恢复到上一次保存的值
     */
    public static void setCurrentBrightLight(Context context) {
        int last_bright = (int) PreferenceUtil.getPreferenceValue(context, PreferenceUtil.BRIGHT_LAST, 255);
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, last_bright);
    }

    public static void setMaxBrightLight(Context context) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.BRIGHT_LAST, 255);
    }

    /**
     * 发送息屏广播
     *
     * @param context
     * @return
     */
    public static void sendSleepBroadcast(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_SYSTEM_SLEEP);
        context.sendBroadcast(intent);
        //息屏时关闭乐投
        //closeLt(context);
    }

    /**
     * 获取网络状态
     *
     * @return
     */
    public static int getNetWorkState(Context context) {
        if (context == null) {
            throw new UnsupportedOperationException("please use NetUtils before init it");
        }
        // 得到连接管理器对象
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);

            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                    return NETWORK_WIFI;
                } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                    return NETWORK_MOBILE;
                }
            } else {
                return NETWORK_NONE;
            }

        } else {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //通过循环将网络信息逐个取出来
            for (int i = 0; i < networks.length; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                if (networkInfo.isConnected()) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        return NETWORK_MOBILE;
                    } else {
                        return NETWORK_WIFI;
                    }
                }
            }
        }
        return NETWORK_NONE;
    }

    /**
     * 是否在屏保时钟界面
     *
     * @return
     */
    public static boolean isAwaitView() {
        Activity activity = AppManagerUtil.getInstance().currentActivity();
        if (null != activity) {
            String name = activity.getClass().getSimpleName();
            if (!TextUtils.isEmpty(name)) {
                if (name.contains("AwaitClockActivity") || name.contains("DigitalClockActivity")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isAvailable()) {
            return true;
        }
        return false;
    }


    /**
     * 获取Mac地址
     *
     * @return
     */
    public static String getLocalMacAddress() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
            if (!TextUtils.isEmpty(macSerial)) {
                macSerial = macSerial.replace(":", "");
                macSerial = macSerial.toUpperCase();
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        } finally {
            return macSerial;
        }
    }

    /**
     * 获取进程名
     *
     * @param context
     * @return
     */
    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context
                .ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }


    //ACSII转码
    private static String toHexUtil(int n) {
        String rt = "";
        switch (n) {
            case 10:
                rt += "A";
                break;
            case 11:
                rt += "B";
                break;
            case 12:
                rt += "C";
                break;
            case 13:
                rt += "D";
                break;
            case 14:
                rt += "E";
                break;
            case 15:
                rt += "F";
                break;
            default:
                rt += n;
        }
        return rt;
    }

    public static String toHex(int n) {
        StringBuilder sb = new StringBuilder();
        if (n / 16 == 0) {
            return toHexUtil(n);
        } else {
            String t = toHex(n / 16);
            int nn = n % 16;
            sb.append(t).append(toHexUtil(nn));
        }
        return sb.toString();
    }

    public static String parseAscii(String str) {
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();
        for (int i = 0; i < bs.length; i++)
            sb.append(toHex(bs[i]));
        return sb.toString().toLowerCase();
    }

    /**
     * 1--长动画
     * 2--短动画
     *
     * @param type
     */
    public static void setBootAnim(Context context, int type) {
        String path = "persist.cvte.bootanim.path";
        String short_anim = "/system/media/bootanimation_short.zip";
        String long_anim = "/system/media/bootanimation_long.zip";

        //长动画
        if (type == 1) {
            PropertiesUtil.getInstance(context).setSystemProp(path, long_anim);
        } else {
            PropertiesUtil.getInstance(context).setSystemProp(path, short_anim);
        }
    }

    /**
     * 十六进制转成ASCII
     *
     * @param hex
     * @return
     */
    public static String convertHexToASCII(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        // 49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {
            // grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            // convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            // convert the decimal to character
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

    /**
     * 当前屏幕是否点亮
     *
     * @param context
     * @return
     */
    public static boolean isScreenLight(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn();
    }


    /**
     * 重启设备
     */
    public static void restartDevice(Context context) {
        Intent rebootIntent = new Intent();
        rebootIntent.setAction("com.cvte.androidsystemtoolbox.action.SYSTEM_REBOOT");
        context.sendBroadcast(rebootIntent);
    }


    /**
     * 关机
     *
     * @param cmd
     * @return
     */
    public static String runCmd(String cmd) {
        Process runsum;
        Runtime runtime;
        OutputStream suConsole;
        BufferedReader suConsoleRet;
        try {
            runtime = Runtime.getRuntime();
            runsum = runtime.exec("sh");
            suConsole = runsum.getOutputStream();
            suConsoleRet = new BufferedReader(new InputStreamReader(runsum.getInputStream()));
            suConsole.write((cmd + "\n").getBytes());
            suConsole.flush();
            suConsole.write("exit\n".getBytes());
            suConsole.flush();
            runsum.waitFor();
        } catch (Exception e) {
            Log.e("runCmd", "Fails to sh:" + e);
            e.printStackTrace();
            return "";
        }
        try {
            String ret = "";
            while (true) {
                String line = suConsoleRet.readLine();
                //Log.d(TAG, "line = " + line);
                if (line == null) {
                    break;
                }
                ret += line + "\n";
            }
            suConsole.close();
            suConsoleRet.close();
            return ret;
        } catch (Exception e) {
            Log.e("runCmd", "Fails to get ret:" + e);
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 让屏幕亮起
     *
     * @param context
     */
    public static void lightScreen(Context context,String tag) {
        LogUtil.LOG_SCREEN("点亮屏幕lightScreen-" + tag);
        PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager
                .FULL_WAKE_LOCK, "bright");
        wl.acquire();
        wl.release();
    }

    /**
     * 获取本地软件版本号 versionCode
     */
    public static int getVersionCode(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext().getPackageManager().getPackageInfo(ctx
                    .getPackageName(), 0);
            localVersion = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }


    /**
     * 开关wifi
     *
     * @param context
     * @param state
     */
    public static void setWifiEnable(Context context, boolean state) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(state);
    }

    /**
     * 获取手机分配的堆内存
     * 烟机为128 M
     *
     * @param context
     * @return
     */
    public static int getHeapSize(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return manager.getMemoryClass();
    }


    /**
     * 从asset目录下获取json字符串文件
     *
     * @param fileName
     * @param context
     * @return
     */
    public static String getFileJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 清除Glide内存缓存
     */
    public static boolean clearGlideCacheMemory(Context context) {
        try {
            Glide.get(context).clearMemory();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 判断某个进程是否运行
     *
     * @param context    上下文环境
     * @param proessName 进程名
     * @return
     */
    public static boolean isProessRunning(Context context, String proessName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                isRunning = true;
            }
        }
        return isRunning;
    }

    /**
     * 设置系统闹钟和通知音量静音
     *
     * @param context
     */
    public static void setVolume(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    /**
     * 计算两数相除
     *
     * @return
     */
    public static float divide(float x, float y) {
        if (y <= 0) {
            return 0;
        }
        BigDecimal bigDecimal1 = new BigDecimal(x);
        BigDecimal bigDecimal2 = new BigDecimal(y);
        return bigDecimal1.divide(bigDecimal2, 4, BigDecimal.ROUND_DOWN).floatValue();
    }

    public static int plus(byte b1, byte b2) {
        return (b1 & 0xff) << 8 | (b2 & 0xff);
    }

    /**
     * 获取本地固件包版本
     */
    public static String getLocalSysVersion() {
        String defaultValue = "100";
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, "ro.cvte.customer.version", defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return value;
        }
    }

    /**
     * 获取meta-data中配置的metaKey
     */
    public static String getPkgMetaValue(Context context, String metaKey) {
        String metaValue = "";
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            if (null != ai) {
                Bundle metaData = ai.metaData;
                if (null != metaData) {
                    metaValue = metaData.getString(metaKey);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return metaValue;
    }

    /**
     * 复制的本地菜谱数据库名称
     *
     * @param context
     * @return
     */
    public static String getRecipeLocalDbName(Context context, String dbName) {
        return dbName + "-" + getVersionCode(context);
    }


    //删除文件
    public static void deleteFile(final String path) {
        File file = new File(path);
        if (file == null || !file.exists() || file.isDirectory()) {
            return;
        } else {
            file.delete();
        }
    }

    /**
     * 将字节数组转成 String
     *
     * @param bs
     * @param islog 是否打印设备控制字节前的标记tag
     * @return
     */
    public static String getHexBinString(byte[] bs, boolean islog) {
        return getHexBinString(bs);
    }

    /**
     * 将字节数组转成 String
     *
     * @param bs
     * @return
     */
    public static String getHexBinString(byte[] bs) {
        StringBuffer log = new StringBuffer();
        for (int i = 0; i < bs.length; i++) {
            log.append(String.format("%02x", (byte) bs[i]) + " ");
        }
        return log.toString();
    }


    /**
     * 杀死指定的程序
     *
     * @param context
     */
    public static void killProjectionProcess(Context context, String pkgName) {
        try {
            //强制杀死进程，防止自启动
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(am, pkgName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断应用是否处于后台
     *
     * @param context
     * @return
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            android.content.ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}
