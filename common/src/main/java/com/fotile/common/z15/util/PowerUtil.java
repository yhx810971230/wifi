package com.fotile.common.z15.util;

import java.io.FileWriter;

/**
 * 项目名称：Z1.5
 * 创建时间：2019/3/12 10:43
 * 文件作者：yaohx
 * 功能描述：电源键相关的操作
 */
public class PowerUtil {

    /**
     * 将电源键事件开放给app层
     */
    public static void powerProvideApp() {
        //电源键不可点击，事件上报给用户自定义
        cmdFileWriter("/proc/power_key", "disable");
    }

    /**
     * 将电源键事件开放给系统层
     */
    public static void powerProvideSys() {
        //电源键可点，事件上报给系统层
        cmdFileWriter("/proc/power_key", "enable");
    }
    /**
     * 写入172，电源键响应home事件
     */
    public static void power172() {
        cmdFileWriter("/proc/power_key", "172");
    }

    /**
     * 写入158，是电源键作为返回键
     */
    public static void power158() {
        cmdFileWriter("/proc/power_key", "158");
    }


    /**
     * 按键灯亮
     */
    public static void powerLampOn(){
        cmdFileWriter("/sys/power_key_led/ctl", "1");
    }

    /**
     * 按键灯熄灭
     */
    public static void powerLampOff(){
        cmdFileWriter("/sys/power_key_led/ctl", "0");
    }

    /**
     * 写入控制命令的方法
     *
     * @param path /proc/power_key
     * @param str  disable:屏蔽电源键 enable:开启电源键
     * @return
     */
    private static boolean cmdFileWriter(String path, String str) {
        try {
            FileWriter fw = new FileWriter(path);
            fw.write(str, 0, str.length());
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
