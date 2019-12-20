package com.fotile.uart;


import android.content.Context;
import android.content.Intent;

import com.fotile.common.z15.util.Tool;
import com.fotile.common.z15.util.log.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by WangChaowei on 2017/12/7.
 */

public class SerialPortUtils {

    private Context context;
    private String path = "/dev/ttyMT1";
    private int baudrate = 9600;
    public boolean serialPortStatus = false; //是否打开串口标志
    public String data_;
    public boolean threadStatus; //线程状态，为了安全终止线程

    public SerialPort serialPort = null;
    public InputStream inputStream = null;
    public OutputStream outputStream = null;

    public SerialPortUtils(Context context){
        this.context = context;
    }

    /**
     * 打开串口
     *
     * @return serialPort串口对象
     */
    public SerialPort openSerialPort() {
        try {
            serialPort = new SerialPort(new File(path), baudrate, 0);
            this.serialPortStatus = true;
            threadStatus = false; //线程状态

            //获取打开的串口中的输入输出流，以便于串口数据的收发
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();

            new ReadThread().start(); //开始线程监控是否有数据要接收
        } catch (IOException e) {
            LogUtil.LOG_UART("openSerialPort: 打开串口异常", e.toString());
            return serialPort;
        }
        LogUtil.LOG_UART("openSerialPort: 打开串口成功", path);
        return serialPort;
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        try {
            inputStream.close();
            outputStream.close();

            this.serialPortStatus = false;
            this.threadStatus = true; //线程状态
            serialPort.close();
        } catch (IOException e) {
            LogUtil.LOG_UART("closeSerialPort: 关闭串口异常", e.toString());
            return;
        }
        LogUtil.LOG_UART("关闭串口成功", "closeSerialPort");
    }

    /**
     * 发送串口指令（字符串）
     *
     * @param data String数据指令
     */
    public void sendSerialPort(final byte[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (serialPortStatus) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        byte[] sendData = data; //string转byte[]
//                        this.data_ = new String(sendData); //byte[]转string
                        if (sendData.length > 0) {
                            outputStream.write(data);
                            outputStream.write('\n');
                            //outputStream.write('\r'+'\n');
                            outputStream.flush();
                            LogUtil.LOG_UART("串口数据发送成功", Tool.getHexBinString(data));
                        }
                    } catch (IOException e) {
                        LogUtil.LOG_UART("串口数据发送失败", e.toString());
                    }
                }
            }
        }).start();

    }

    /**
     * 单开一线程，来读数据
     */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            while (!threadStatus) {
                byte[] buffer = new byte[64];
                int size; //读取数据的大小
                try {
                    size = inputStream.read(buffer);
                    if (size > 0) {
                        LogUtil.LOG_UART("run: 接收到了数据", Tool.getHexBinString(buffer));
                        int temp = buffer[0] & 0xff;
                        if(temp == 0xff){
                            LogUtil.LOG_UART("run: 校验成功", "关闭串口");
                            closeSerialPort();

                            //发送广播
                            Intent testIntent = new Intent("cvte.mid.factory.serial.test.response");
                            testIntent.putExtra("isSuccess",  true);
                            context.sendBroadcast(testIntent);
                        }
                    }
                } catch (IOException e) {
                    LogUtil.LOG_UART("run: 数据读取异常", e.toString());
                }
            }
        }
    }

//    public OnDataReceiveListener onDataReceiveListener = null;
//
//    public interface OnDataReceiveListener {
//        void onDataReceive(byte[] buffer, int size);
//    }
//
//    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
//        onDataReceiveListener = dataReceiveListener;
//    }

}
