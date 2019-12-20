package com.fotile.wifi.bean;

import android.net.wifi.ScanResult;

import com.google.gson.Gson;

public class WifiScanResult {

    public ScanResult scanResult;
    /**
     * 是否加密
     */
    public boolean encryp;
    /**
     * 是否配对过该wifi
     */
    public boolean paired;
    /**
     * 信号强度分为四个等级
     */
    public int signalLevel;

    public LINK_STATE link_state = LINK_STATE.LINK_NORMAL;

    public enum LINK_STATE {
        LINK_CONNECTED(1),     //已连接
        LINK_CONNECTING(2),    //连接中
        LINK_NORMAL(3);         //正常状态
        int value;

        LINK_STATE(int value) {
            this.value = value;
        }
    }

    public WifiScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }


    public String toString() {
        return "[wifi名称:" + scanResult.SSID + "] " + "[是否加密:" + encryp + "] " + "[是否配对过:" + paired + "] " + "[连接状态:"
                + link_state + "] " + "[信号强度:" + scanResult.level + "] ";
    }
}
