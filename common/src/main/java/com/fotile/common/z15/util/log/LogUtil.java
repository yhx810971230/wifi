package com.fotile.common.z15.util.log;


import android.text.TextUtils;
import android.util.Log;

import com.fotile.common.z15.bean.EnginBean;

/**
 * 文件名称：LogUtil
 * 创建时间：2017/8/7 15:14
 * 文件作者：yaohx
 * 功能描述：项目全局打印日志
 */
public class LogUtil {
    /**
     * 获取isDebug存储值
     */
    public static boolean isDebug = true;

    /**
     * 每一次重启进程都会重新创建一个log文件
     */
    static LogSave logSave = new LogSave("/sdcard/fotile/z15/Log");

    public static void setLogSaveFolder(String folder){
        if(!TextUtils.isEmpty(folder)){
            logSave = new LogSave(folder);
        }
    }

    public static void LOG_SCREEN(String obj) {
        if (isDebug) {
            if (null != obj) {
                logSave.e("屏保相关ScreenSaverService", obj,"屏保相关");
            } else {
                logSave.e("屏保相关ScreenSaverService", "null","屏保相关");
            }
        }
    }

    public static void LOG_RECIPE(String TAG, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----菜谱数据----" + TAG, obj.toString());
            } else {
                Log.e("----菜谱数据----" + TAG, "null--");
            }
        }
    }
    public static void LOG_WIFI(String TAG, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----WIFI----" + TAG, obj.toString());
            } else {
                Log.e("----WIFI----" + TAG, "null--");
            }
        }
    }

    public static void LOG_Memor(String TAG, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----备忘录----" + TAG, obj.toString());
            } else {
                Log.e("----备忘录----" + TAG, "null--");
            }
        }
    }

    public static void LOG_Notify(String TAG, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----消息提醒----" + TAG, obj.toString());
            } else {
                Log.e("----消息提醒----" + TAG, "null--");
            }
        }
    }

    public static void LOG_RECIPE_PLAY(String TAG, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----菜谱播放----" + TAG, obj.toString());
            } else {
                Log.e("----菜谱播放----" + TAG, "null--");
            }
        }
    }

    public static void LOG_TOOTH(String TAG, Object obj) {
        if (isDebug) {
//            if (null != obj) {
//                Log.e("----蓝牙相关----" + TAG, obj.toString());
//            } else {
//                Log.e("----蓝牙相关----" + TAG, "null--");
//            }
            if (null != obj) {
                logSave.e("----蓝牙相关----" + TAG, obj.toString(), "蓝牙相关");
            } else {
                logSave.e("----蓝牙相关----" + TAG, "null--", "蓝牙相关");
            }
        }
    }

    public static void LOG_POWER(String TAG, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----电源键----" + TAG, obj.toString());
            } else {
                Log.e("----电源键----" + TAG, "null--");
            }
        }
    }

    public static void LOG_UART(String TAG, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----串口----" + TAG, obj.toString());
            } else {
                Log.e("----串口----" + TAG, "null--");
            }
        }
    }

    public static void LOGE(String tag, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e(tag, obj.toString());
            } else {
                Log.e(tag, "-null-");
            }
        }
    }

    public static void LOGD(String tag, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.d(tag, obj.toString());
            } else {
                Log.d(tag, "-null-");
            }
        }
    }

    public static void LOG_COMMAND(String TAG, Object obj) {
        if (null != obj) {
            Log.e("----设备指令----" + TAG, obj.toString());
        } else {
            Log.e("----设备指令----" + TAG, "null--");
        }
    }

    public static void LOGOta(String tag, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----Ota升级----" + tag, obj.toString());
            } else {
                Log.e("----Ota升级----" + tag, "null");
            }
        }
    }

    public static void LOG_TICK(String tag, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----定时----" + tag, obj.toString());
            } else {
                Log.e("----定时----" + tag, "null");
            }
        }
    }

    public static void LOG_REQUEST(String tag, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----菜谱request----" + tag, obj.toString());
            } else {
                Log.e("----菜谱request----" + tag, "null");
            }
        }
    }

    public static void LOG_STOVE(String TAG, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----灶具----" + TAG, obj.toString());
            } else {
                Log.e("----灶具----" + TAG, "null--");
            }
        }
    }

    public static void LOG_DB(String tag, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----菜谱db----" + tag, obj.toString());
            } else {
                Log.e("----菜谱db----" + tag, "null");
            }
        }
    }

    public static void LOG_JAZ1(String tag, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----设备指令----" + tag, obj.toString());
            } else {
                Log.e("----设备指令----" + tag, "null");
            }
        }
    }

    public static void LOG_LT(String tag, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----乐投----" + tag, obj.toString());
            } else {
                Log.e("----乐投----" + tag, "null");
            }
        }
    }

    public static void LOG_ENGIN(String tag, EnginBean enginBean, int number) {
        if (isDebug) {
            if (null != enginBean) {
                //0表示正式地址 1表示测试地址 2表示开发环境
                String temp = "";
                if (number == 0) {
                    temp = "online";
                }
                if (number == 1) {
                    temp = "test";
                }
                if (number == 2) {
                    temp = "develop";
                }
                Log.e("----enginBean----" + tag, enginBean.toString() + " [中间件:" + temp + "]");
            } else {
                Log.e("----enginBean----" + tag, "null");
            }
        }
    }

}
