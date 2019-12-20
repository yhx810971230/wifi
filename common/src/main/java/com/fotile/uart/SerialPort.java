package com.fotile.uart;


import com.fotile.common.z15.util.log.LogUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件名称：SerialPort
 * 创建时间：2018/7/20 10:43
 * 文件作者：yaohx
 * 功能描述：串口操作类，类名、包名不可更改，和Jni匹配
 */
public class SerialPort {

    public FileDescriptor fileDescriptor;
    private FileInputStream fileInputStream;
    private FileOutputStream fileOutputStream;

    public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
        //检查访问权限，如果没有读写权限，进行文件操作，修改文件访问权限
//        if (!device.canRead() || !device.canWrite()) {
//
//            try {
//                //通过挂载到linux的方式，修改文件的操作权限
//                Process su = Runtime.getRuntime().exec("/system/xbin/su");
//                String cmd = "chmod 777 " + device.getAbsolutePath() + "\n" + "exit\n";
//                su.getOutputStream().write(cmd.getBytes());
//
//                if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
//                    LogUtil.LOG_UART("SerialPort", "SerialPort: 没有权限");
//                    throw new SecurityException();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new SecurityException();
//            }
//        }

        fileDescriptor = open(device.getAbsolutePath(), baudrate, flags);

        if (fileDescriptor == null) {
            throw new IOException("native open returns null");
        }

        fileInputStream = new FileInputStream(fileDescriptor);
        fileOutputStream = new FileOutputStream(fileDescriptor);
    }

    // Getters and setters
    public InputStream getInputStream() {
        return fileInputStream;
    }

    public OutputStream getOutputStream() {
        return fileOutputStream;
    }


    private native static FileDescriptor open(String path, int baudrate, int flags);

    public native void close();

    //加载jni下的C文件库
    static {
        System.loadLibrary("serialPort");
    }
}
