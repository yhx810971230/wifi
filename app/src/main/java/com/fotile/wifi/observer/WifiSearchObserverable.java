package com.fotile.wifi.observer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.fotile.wifi.bean.WifiScanResult;
import com.fotile.wifi.util.LinkWifi;
import com.fotile.wifi.util.WifiLogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fotile.wifi.util.WifiConstant.IKCC;


/**
 * 文件名称：WifiSearchObserverable
 * 创建时间：2019/12/3 16:15
 * 文件作者：yaohx
 * 功能描述：wifi搜索类
 */
public class WifiSearchObserverable {

    private Context context;
    /**
     * 同步锁
     */
    private static Object lock_instance = new Object();

    private static Object lock_research = new Object();

    private static WifiSearchObserverable instance;
    /**
     * wifi搜索的间隔时长
     */
    private static final int LOOP_TIME = 40 * 1000;

    final int WHAT_SEARCH_START = 1001;
    final int WHAT_SEARCH_LOOP = 1002;

    private LinkWifi linkWifi;
    /**
     * 网络连接管理
     */
    private ConnectivityManager connectivityManager;
    /**
     * WifiManager
     */
    private WifiManager wifiManager;

    private IntentFilter filter;
    /**
     * 订阅者
     */
    CopyOnWriteArrayList<Action1<List<WifiScanResult>>> list_search = new CopyOnWriteArrayList<>();
    /**
     * 搜索状态
     */
    public boolean searching = false;

    private WifiSearchObserverable(Context con) {
        context = con.getApplicationContext();
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        linkWifi = new LinkWifi(context);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //注册广播
        registerReciver();
    }

    public static WifiSearchObserverable getInstance(Context context) {
        synchronized (lock_instance) {
            if (null == instance) {
                instance = new WifiSearchObserverable(context);
            }
            return instance;
        }
    }

    private void registerReciver() {
        filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        context.registerReceiver(wifiReceiver, filter);
    }

    public void startWifiSearch() {
        if (!wifiManager.isWifiEnabled()) {
            throw new IllegalStateException("请打开wifi开关");
        }

        wifiManager.startScan();
        handler.sendEmptyMessage(WHAT_SEARCH_LOOP);
        searching = true;
    }

    public void stopWifiSearch() {
        handler.removeMessages(WHAT_SEARCH_START);
        handler.removeMessages(WHAT_SEARCH_LOOP);
        searching = false;
    }

    /**
     * 重新搜索，用于刷新页面
     */
    public void reStartWifiSearch() {
        synchronized (lock_research) {
            stopWifiSearch();
            startWifiSearch();
        }
    }

    /**
     * 广播接收，监听网络
     */
    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!searching){
                return;
            }
            final String action = intent.getAction();
            //网络状态改变
            if ((WifiManager.NETWORK_STATE_CHANGED_ACTION).equals(action)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != networkInfo) {
                    //wifi当前连接状态
                    NetworkInfo.State state = networkInfo.getState();
                    //获取连接成功的wifiInfo，连接中获取断开时该对象为空
                    WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);

                    String ssid = wifiInfo == null ? "null" : wifiInfo.getSSID().replace("\"", "");
                    WifiLogUtil.LOG_WIFI("wifi广播连接状态", ssid + "：" + state);
                    //连接中
                    if (state == NetworkInfo.State.CONNECTING) {
                        reStartWifiSearch();
                    }
                    //连接成功
                    if (state == NetworkInfo.State.CONNECTED) {
                        reStartWifiSearch();
                    }

//                    //如果是连接上了大厨管家，保存ssid和密码
//                    if (state == NetworkInfo.State.CONNECTED && wifiInfo.getSSID().replace("\"", "").startsWith
//                            ("FotileAP_")) {
//                        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.LAST_CONNECTED_WIFI_NAME, wifiInfo
//                                .getSSID().replace("\"", ""));
//                        PreferenceUtil.setPreferenceValue(context, PreferenceUtil.LAST_CONNECTED_WIFI_PWD, hexPsw);
//                    }
                }
            }
        }
    };

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //搜索
                case WHAT_SEARCH_LOOP:
                    //更新UI
                    List<ScanResult> list = wifiManager.getScanResults();
                    notifySearchData(list);

                    //开始搜索附近wifi
                    wifiManager.startScan();
                    sendEmptyMessageDelayed(WHAT_SEARCH_LOOP, LOOP_TIME);
                    break;
            }
        }
    };

    /**
     * 添加观察者
     *
     * @param iAction
     */
    public void addSearchObserver(Action1<List<WifiScanResult>> iAction) {
        if (!list_search.contains(iAction) && null != iAction) {
            list_search.add(iAction);
        }
    }

    public void removeSearchObserver(Action1<List<WifiScanResult>> iAction) {
        if (null != list_search && null != iAction) {
            list_search.remove(iAction);
        }
    }

    private void notifySearchData(List<ScanResult> list) {
        if (null != list_search && null != list) {
            //名称相同时使用信号更强的ssid
            List<ScanResult> filterList = filterLevel(list);
            //排序
            Collections.sort(filterList, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    return -(lhs.level - rhs.level);
                }
            });
            //处理应用层的一些业务
            List<WifiScanResult> result = initScanWifilist(filterList);

//            for (WifiScanResult w : result) {
//                WifiLogUtil.LOG_WIFI("wifiScanResult", w);
//            }

            Observable<List<WifiScanResult>> observable = Observable.just(result);
            for (Action1<List<WifiScanResult>> action : list_search) {
                observable.subscribeOn(Schedulers.immediate())//指定被观察者的执行线程（立即在当前线程执行指定的工作）
                        .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                        .subscribe(action);
            }
        }
    }

    /**
     * 遍历result列表中保存的wifi，名称相同时使用信号更强的ssid
     */
    private List<ScanResult> filterLevel(List<ScanResult> newList) {
        List<ScanResult> exitList = null;
        if (null != newList) {
            exitList = new ArrayList<ScanResult>();
            for (int k = 0; k <= newList.size() - 1; k++) {
                //未过滤的wifi
                ScanResult newScanResult = newList.get(k);
                if (!TextUtils.isEmpty(newScanResult.SSID)) {
                    //表示是否过了添加到了列表
                    boolean isAdd = false;
                    //遍历已经添加到temp列表中的wifi，找到相同的ssid，替换强度更强的
                    for (int p = 0; p <= exitList.size() - 1; p++) {
                        ScanResult exitScanResult = exitList.get(p);
                        //两个路由器的SSID相同
                        if (exitScanResult.SSID.equals(newScanResult.SSID)) {
                            isAdd = true;
                            if (exitScanResult.level < newScanResult.level) {
                                exitList.remove(exitScanResult);
                                exitList.add(newScanResult);
                                break;
                            }
                        }
                    }
                    if (!isAdd) {
                        exitList.add(newScanResult);
                    }
                }
            }
        }
        return exitList;
    }

    /**
     * 处理一些应用层的业务逻辑
     * 判断是否加密、是否配置
     * 将ikcc排序在列表前面
     *
     * @param list
     * @return
     */
    private List<WifiScanResult> initScanWifilist(List<ScanResult> list) {
        WifiScanResult wifiScanResult;
        List<WifiScanResult> wifiList = new ArrayList<WifiScanResult>();
        List<WifiScanResult> ikccList = new ArrayList<>();
        List<WifiScanResult> normalList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            ScanResult scanResult = list.get(i);
            //总共分为四个等级
            int signalLevel = WifiManager.calculateSignalLevel(scanResult.level, 4);
            String ssid = scanResult.SSID;
            if (!TextUtils.isEmpty(ssid)) {
                //是否加密
                boolean encryp = scanResult.capabilities.contains("WEP") || scanResult.capabilities.contains("PSK")
                        || scanResult.capabilities.contains("EAP");
                //是否以前配置过该网络
                boolean paired = linkWifi.isPaired(ssid);

                wifiScanResult = new WifiScanResult(scanResult);
                wifiScanResult.encryp = encryp;
                wifiScanResult.paired = paired;
                wifiScanResult.signalLevel = signalLevel;

                //设置wifi AP的连接状态
                handleWifiConnectState(wifiScanResult);

                //将已经连接或者正在连接的排在第一位
                WifiScanResult.LINK_STATE state = wifiScanResult.link_state;
                //将正在连接的放在第一位
                if (state == WifiScanResult.LINK_STATE.LINK_CONNECTED || state == WifiScanResult.LINK_STATE
                        .LINK_CONNECTING) {
                    ikccList.add(0, wifiScanResult);
                } else {
                    if (ssid.startsWith(IKCC)) {
                        ikccList.add(wifiScanResult);
                    } else {
                        normalList.add(wifiScanResult);
                    }
                }
            }
        }
        wifiList.addAll(ikccList);
        wifiList.addAll(normalList);
        return wifiList;
    }

    /**
     * 设置wifi AP的连接状态
     *
     * @param wifiScanResult
     */
    private void handleWifiConnectState(WifiScanResult wifiScanResult) {
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // Return dynamic information about the current Wi-Fi connection, if any is active.
        WifiInfo activeWifiInfo = wifiManager.getConnectionInfo();

        //获取正在活动状态的AP的ssid，如果AP还没有开始连接（DISCONNECTED状态），获取的为“<unknown ssid>”
        String activSsid = activeWifiInfo.getSSID();
        String scanSsid = LinkWifi.convertToQuotedString(wifiScanResult.scanResult.SSID);
        NetworkInfo.State state = networkInfo.getState();

        //如果active的ssid和某一个scan的ssid相同
        if (activSsid.startsWith("\"") && activSsid.equals(scanSsid)) {
            if (wifiScanResult.paired) {
                WifiLogUtil.LOG_WIFI("获取wifi状态", "[activSsid：" + activSsid + "][state：" + state + "]");

                //连接成功
                if (state == NetworkInfo.State.CONNECTED) {
                    wifiScanResult.link_state = WifiScanResult.LINK_STATE.LINK_CONNECTED;
                }
                //将active的wifi，状态设置为连接中（因为activeWifiInfo即表示在连接中或者连接成功的AP）
                else {
                    wifiScanResult.link_state = WifiScanResult.LINK_STATE.LINK_CONNECTING;
                }
            }
        } else {
            wifiScanResult.link_state = WifiScanResult.LINK_STATE.LINK_NORMAL;
        }
    }
}
