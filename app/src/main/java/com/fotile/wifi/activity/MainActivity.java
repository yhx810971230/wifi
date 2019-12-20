package com.fotile.wifi.activity;

import android.app.Service;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cvte.adapter.android.net.WifiManagerAdapter;
import com.fotile.wifi.util.LinkWifi;
import com.fotile.wifi.R;
import com.fotile.wifi.adapter.WifiListAdapter;
import com.fotile.wifi.bean.WifiScanResult;
import com.fotile.wifi.observer.WifiSearchObserverable;

import java.util.List;

import rx.functions.Action1;
@Deprecated
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    WifiListAdapter listAdapter;
    List<WifiScanResult> list;
    private LinkWifi linkWifi;
    private WifiManager wifiManager;
    /**
     * 系統提供的wifi内部接口的适配器
     */
    public WifiManagerAdapter wifiManagerAdapter;
    private WifiConfiguration wifiConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linkWifi = new LinkWifi(this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Service.WIFI_SERVICE);
        wifiManagerAdapter = new WifiManagerAdapter(this, wifiManager);

        listView = (ListView) findViewById(R.id.listview);
        listAdapter = new WifiListAdapter(this);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);

        WifiSearchObserverable.getInstance(this).startWifiSearch();
        WifiSearchObserverable.getInstance(this).addSearchObserver(action1);


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    Action1<List<WifiScanResult>> action1 = new Action1<List<WifiScanResult>>() {
        @Override
        public void call(List<WifiScanResult> wifiScanResultList) {
            list = wifiScanResultList;

            listAdapter.setList(list);
            listAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WifiScanResult wifiScanResult = list.get(position);
        String ssid = wifiScanResult.scanResult.SSID;
        boolean paired = linkWifi.isPaired(ssid);
        //是否配置过这个ssid网络
        WifiConfiguration wifiConfig = linkWifi.IsExsits(ssid);
        WifiScanResult.LINK_STATE link_state = wifiScanResult.link_state;

        //如果当前状态是已连接，执行断开
        if (paired && link_state == WifiScanResult.LINK_STATE.LINK_CONNECTED) {
            wifiManagerAdapter.forget(wifiConfig.networkId, forgetListener);
            return;
        }
        //点击的wifi连接过--执行连接（不用输入密码）
        if (wifiConfig != null) {
            linkWifi.setMaxPriority(wifiConfig);
            linkWifi.ConnectToNetID(wifiConfig.networkId);
            //更新列表
            WifiSearchObserverable.getInstance(MainActivity.this).reStartWifiSearch();
//            listAdapter.notifyItemView(position, WifiScanResult.LINK_STATE.LINK_CONNECTING);
            return;
        }
        //如果当前状态是未连接
        if (link_state == WifiScanResult.LINK_STATE.LINK_NORMAL) {
            //加密
            if(wifiScanResult.encryp){
                WifiPWDDialog wifiPWDDialog = new WifiPWDDialog(MainActivity.this, wifiScanResult.scanResult);
                wifiPWDDialog.show();
            }else {
                int netID = linkWifi.CreateWifiInfo2(wifiScanResult.scanResult, "");
                linkWifi.ConnectToNetID(netID);
            }
        }

    }

    private WifiManagerAdapter.ActionListener forgetListener = new WifiManagerAdapter.ActionListener() {
        @Override
        public void onSuccess() {
            Toast.makeText(MainActivity.this, "wifi忽略成功", Toast.LENGTH_SHORT).show();
            //更新列表
            WifiSearchObserverable.getInstance(MainActivity.this).reStartWifiSearch();
        }

        @Override
        public void onFailure(int reason) {

        }
    };

}
