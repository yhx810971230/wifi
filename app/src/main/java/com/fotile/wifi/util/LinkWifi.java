package com.fotile.wifi.util;

import android.app.Service;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;


import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 文件名称：LinkWifi
 * 创建时间：2018/12/10
 * 文件作者：chenyjg
 * 功能描述：wifi配置信息
 */
public class LinkWifi {
    /**
     * wifi manager
     */
    private WifiManager wifiManager;

    /**
     * context
     */
    private Context context;

    /**
     * 定义几种加密方式，一种是WEP，一种是WPA/WPA2，还有没有密码的情况
     */
    public enum WifiCipherType {
        /**
         * wep
         */
        WIFI_CIPHER_WEP, /**
         * wpa
         */
        WIFI_CIPHER_WPA_EAP, /**
         * wpa2 psk加密
         */
        WIFI_CIPHER_WPA_PSK, /**
         * wpa2 psk加密
         */
        WIFI_CIPHER_WPA2_PSK, /**
         * 没密码
         */
        WIFI_CIPHER_NOPASS
    }

    /**
     * 构造函数
     *
     * @param context
     */
    public LinkWifi(Context context) {
        this.context = context;
        wifiManager = (WifiManager) this.context.getSystemService(Service.WIFI_SERVICE);
    }

    public boolean ConnectToNetID(int netID) {
        //System.out.println("ConnectToNetID netID=" + netID);
        return wifiManager.enableNetwork(netID, true);
    }

    /**
     * 转换为带引号的字符串
     *
     * @param string
     * @return
     */
    public static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }

    public int CreateWifiInfo2(ScanResult wifiinfo, String pwd) {
        WifiCipherType type;

        if (wifiinfo.capabilities.contains("WPA2-PSK")) {
            // WPA-PSK加密
            type = WifiCipherType.WIFI_CIPHER_WPA2_PSK;
        } else if (wifiinfo.capabilities.contains("WPA-PSK")) {
            // WPA-PSK加密
            type = WifiCipherType.WIFI_CIPHER_WPA_PSK;
        } else if (wifiinfo.capabilities.contains("WPA-EAP")) {
            // WPA-EAP加密
            type = WifiCipherType.WIFI_CIPHER_WPA_EAP;
        } else if (wifiinfo.capabilities.contains("WEP")) {
            // WEP加密
            type = WifiCipherType.WIFI_CIPHER_WEP;
        } else {
            // 无密码
            type = WifiCipherType.WIFI_CIPHER_NOPASS;
        }

        WifiConfiguration config = CreateWifiInfo(wifiinfo.SSID, wifiinfo.BSSID, pwd, type);
        if (config != null) {
            return wifiManager.addNetwork(config);
        } else {
            return -1;
        }
    }

    /**
     * 配置一个连接
     */
    public WifiConfiguration CreateWifiInfo(String SSID, String BSSID, String password, WifiCipherType type) {

        int priority;

        WifiConfiguration config = this.IsExsits(SSID);
        if (config != null) {
            // wifiManager.removeNetwork(config.networkId); // 如果之前配置过这个网络，删掉它
            // 本机之前配置过此wifi热点，调整优先级后，直接返回
            return setMaxPriority(config);
        } else {
            config = new WifiConfiguration();
        }

        //        config = new WifiConfiguration();
        //清除之前的连接信息
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        config.status = WifiConfiguration.Status.ENABLED;

        priority = getMaxPriority() + 1;
        if (priority > 99999) {
            priority = shiftPriorityAndSave();
        }

        config.priority = priority;
        //各种加密方式判断
        if (type == WifiCipherType.WIFI_CIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WifiCipherType.WIFI_CIPHER_WEP) {
            config.preSharedKey = "\"" + password + "\"";

            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WifiCipherType.WIFI_CIPHER_WPA_EAP) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.status = WifiConfiguration.Status.ENABLED;
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);

            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            //            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN
            //                    | WifiConfiguration.Protocol.WPA);
            //解决卖场配网问题
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);


        } else if (type == WifiCipherType.WIFI_CIPHER_WPA_PSK) {
            config.preSharedKey = "\"" + password + "\"";
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

            //解决卖场配网问题
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

        } else if (type == WifiCipherType.WIFI_CIPHER_WPA2_PSK) {
            config.preSharedKey = "\"" + password + "\"";
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

            //解决卖场配网问题
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

        } else {
            return null;
        }

        return config;
    }

    //    /**
    //     * 查看以前是否也配置过这个网络
    //     * 已弃用
    //     */
    //    public WifiConfiguration IsExsits(String SSID) {
    //
    //        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
    //
    //        if (existingConfigs == null) {
    //            return null;
    //        }
    //
    //        for (WifiConfiguration existingConfig : existingConfigs) {
    //
    //
    //            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
    //                return existingConfig;
    //            }
    //        }
    //        return null;
    //    }

    /**
     * 查看以前是否也配置过这个网络
     */
    public WifiConfiguration IsExsits(String SSID) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> existingConfigs = wifimanager.getConfiguredNetworks();
        if (existingConfigs == null)
            return null;
        for (WifiConfiguration existingConfig : existingConfigs) {

            if (existingConfig.SSID.toString().equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    public boolean isPaired(String SSID){
        WifiConfiguration config = IsExsits(SSID);
        if(null != config){
            return true;
        }
        return false;
    }

    /**
     * 设置优先级
     *
     * @param config
     * @return
     */
    public WifiConfiguration setMaxPriority(WifiConfiguration config) {
        int priority = getMaxPriority() + 1;
        if (priority > 99999) {
            priority = shiftPriorityAndSave();
        }

        config.priority = priority;
        System.out.println("priority=" + priority);

        wifiManager.updateNetwork(config);
        //本机之前配置过此wifi热点，直接返回
        return config;
    }

    private int getMaxPriority() {
        List<WifiConfiguration> localList = this.wifiManager.getConfiguredNetworks();
        int i = 0;
        Iterator<WifiConfiguration> localIterator = localList.iterator();
        while (true) {
            if (!localIterator.hasNext())
                return i;
            WifiConfiguration localWifiConfiguration = localIterator.next();
            if (localWifiConfiguration.priority <= i)
                continue;
            i = localWifiConfiguration.priority;
        }
    }

    private int shiftPriorityAndSave() {
        List<WifiConfiguration> localList = this.wifiManager.getConfiguredNetworks();
        sortByPriority(localList);
        int i = localList.size();
        for (int j = 0; ; ++j) {
            if (j >= i) {
                this.wifiManager.saveConfiguration();
                return i;
            }
            WifiConfiguration localWifiConfiguration = localList.get(j);
            localWifiConfiguration.priority = j;
            this.wifiManager.updateNetwork(localWifiConfiguration);
        }
    }

    private void sortByPriority(List<WifiConfiguration> paramList) {
        Collections.sort(paramList, new WifiManagerCompare());
    }

    /**
     * 比较器
     */
    class WifiManagerCompare implements Comparator<WifiConfiguration> {
        @Override
        public int compare(WifiConfiguration param1, WifiConfiguration param2) {
            return param1.priority - param2.priority;
        }
    }
}
