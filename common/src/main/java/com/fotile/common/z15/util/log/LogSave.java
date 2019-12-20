package com.fotile.common.z15.util.log;

import android.util.Log;

import com.fotile.common.z15.util.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 项目名称：Z1.5
 * 创建时间：2018/11/14 9:43
 * 文件作者：yaohx
 * 功能描述：LogSave
 */
class LogSave {
    //日志开关
    private boolean isOpen = true;
    //日志是否保存到本地
    private boolean isSave = true;
    private String logPath;
    private String logName;

    /**
     * /sdcard/fotile/z15/Log
     * @param folder
     */
    public LogSave(String folder) {
        //  /sdcard/fotile/z15/Log/程序启动时间**
        logPath = folder + "/程序启动时间" + getNowTime();
        FileUtil.createFolder(logPath);
    }

    public void d(String tag, String msg, String logName) {
        this.logName = logName + ".txt";
        if (isOpen) {
            Log.d(tag, msg);
        }
        if (isSave) {
            writeToFile(tag, msg);
        }
    }

    public void e(String tag, String msg, String logName) {
        this.logName = logName + ".txt";
        if (isOpen) {
            Log.e(tag, msg);
        }
        if (isSave) {
            writeToFile(tag, msg);
        }
    }

    private void writeToFile(String tag, String msg) {
        String info = tag + " " + msg;
//        writeToFile(info);

        if (null != logPath) {
            String log = getNowTime() + ":" + info + "\n";
            File file = new File(logPath);
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (!mkdirs) {
                    return;
                }
            }
            FileWriter fw = null;
            try {
                fw = new FileWriter(logPath + File.separator + logName, true);
                fw.write(log);
            } catch (IOException var13) {
                var13.printStackTrace();
            } finally {
                try {
                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException var12) {
                    var12.printStackTrace();
                }
            }
        }
    }

    private static String getNowTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

}
