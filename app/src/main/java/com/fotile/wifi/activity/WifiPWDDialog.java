package com.fotile.wifi.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cvte.adapter.android.net.WifiManagerAdapter;
import com.fotile.wifi.R;
import com.fotile.wifi.observer.WifiSearchObserverable;
import com.fotile.wifi.util.LinkWifi;

import java.util.List;

@Deprecated
public class WifiPWDDialog extends Dialog implements View.OnClickListener {

    /**
     * 连接超时的消息类型
     */
    private static final int MESSAGE_CONNECT_TIMEOUT = 1;

    EditText editWifiPwd;

    TextView tvJoin;

    TextView tvCancel;

    private Context context;

    private LinkWifi linkWifi;
    private ScanResult scanResult;

    /**
     * 网络ID
     */
    private int netWorkId = -1;

    private WifiManager wifiManager;

    private InputMethodManager inputManager;

    /**
     * 系統提供的wifi内部接口的适配器
     */
    private WifiManagerAdapter wifiManagerAdapter;

    /**
     * 输入的密码
     */
    private String pwd;
    /**
     * 默认不显示密码
     */
    private boolean pwdEye = false;

    public WifiPWDDialog(@NonNull Context context, ScanResult scanResult) {
        super(context, R.style.FullScreenDialog);
        this.context = context;
        this.scanResult = scanResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_input_password);
        initData();
        initView();
        registerNetworkReceiver();
    }

    /**
     * 注册监听网络变化
     */
    private void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        context.registerReceiver(mReceiver, filter);
    }

    private void initData() {
        linkWifi = new LinkWifi(context);
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManagerAdapter = new WifiManagerAdapter(context, wifiManager);
    }

    private void initView() {
        tvJoin = (TextView) findViewById(R.id.tv_join);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        editWifiPwd = (EditText) findViewById(R.id.edit_wifi_pwd);
        inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        tvJoin.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        editWifiPwd.setOnEditorActionListener(editorActionListener);
    }

    /**
     * 广播接收，监听网络
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.State state = networkInfo.getState();
                WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                if (wifiInfo != null && state == NetworkInfo.State.CONNECTED && wifiInfo.getSSID().equals(LinkWifi
                        .convertToQuotedString(scanResult.SSID))) {
                    //连接成功
                    hideLoadingIcon();
                    dismiss();

                    WifiSearchObserverable.getInstance(context).reStartWifiSearch();
                }

            } else if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
                if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                    handleConnectFailure(false);
                }
            }
        }
    };

    private Handler connectHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            handleConnectFailure(true);
            return false;
        }
    });

    /**
     * AP连接失败的处理
     *
     * @param isTimeout
     */
    private void handleConnectFailure(boolean isTimeout) {
        connectHandler.removeMessages(MESSAGE_CONNECT_TIMEOUT);
        hideLoadingIcon();
        editWifiPwd.setText("");
        editWifiPwd.setHint("请输入密码");
        editWifiPwd.setHintTextColor(Color.parseColor("#D64247"));

        if (isTimeout) {
            Toast.makeText(context, "连接超时，稍后请重试", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "连接失败，请输入正确密码", Toast.LENGTH_SHORT).show();
        }
        forgetWifi();
    }

    /**
     * 忘记已保存的wifi配置
     */
    private void forgetWifi() {
        if (netWorkId != -1) {
            wifiManagerAdapter.forget(netWorkId, forgetListener);
        }
    }

    /**
     * 忽略网络监听
     */
    WifiManagerAdapter.ActionListener forgetListener = new WifiManagerAdapter.ActionListener() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onFailure(int reason) {
        }
    };

    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                connectAp();
                //隐藏软键盘
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            return false;
        }

    };

    /**
     * 连接已保存的AP
     */
    private void connectExistAp() {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if (null != existingConfigs && existingConfigs.size() > 0) {
            for (WifiConfiguration wifiConfiguration : existingConfigs) {
                if (wifiConfiguration.networkId != netWorkId) {
                    linkWifi.ConnectToNetID(wifiConfiguration.networkId);
                    break;
                }
            }
        }
    }

    /**
     * 连接wifi
     */
    private void connectAp() {
        String wifiPwd = editWifiPwd.getText().toString();
        if (TextUtils.isEmpty(wifiPwd)) {
            Toast.makeText(context,"密码不能为空",Toast.LENGTH_SHORT).show();
        } else if (wifiPwd.length() <= 7) {
            Toast.makeText(context,"密码需要8位数以上",Toast.LENGTH_SHORT).show();
        } else {
            pwd = wifiPwd;
            // 此处加入连接wifi代码
            int netID = linkWifi.CreateWifiInfo2(scanResult, pwd);
            netWorkId = netID;
            linkWifi.ConnectToNetID(netID);
            showLoadingIcon();
            connectHandler.removeMessages(MESSAGE_CONNECT_TIMEOUT);
            connectHandler.sendEmptyMessageDelayed(MESSAGE_CONNECT_TIMEOUT, 60000);
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        connectHandler.removeMessages(MESSAGE_CONNECT_TIMEOUT);
        context.unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View v) {
        // 点击加入
        if (v.getId() == R.id.tv_join) {
            connectAp();
            //隐藏软键盘
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        // 点击取消
        else if (v.getId() == R.id.tv_cancel) {
            hideLoadingIcon();
            forgetWifi();
//            connectExistAp();
            //解决bug477,点击取消后已连接的wifi重连的bug
            //connectExistAp();
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            dismiss();
        }
    }

    /**
     * 显示加载图标
     */
    private void showLoadingIcon() {
//        loadingView.setVisibility(View.VISIBLE);
//        loadingView.startRotationAnimation();
    }

    /**
     * 隐藏加载图标
     */
    private void hideLoadingIcon() {
//        loadingView.setVisibility(View.GONE);
//        loadingView.stopRotationAnimation();
    }

    /**
     * 点击非编辑区域收起键盘
     * 获取点击事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() ==  MotionEvent.ACTION_DOWN ) {
            View view = getCurrentFocus();
            if (isShouldHideKeyBord(view, ev)) {
                hideSoftInput(view.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判定当前是否需要隐藏
     */
    protected boolean isShouldHideKeyBord(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom);
            //return !(ev.getY() > top && ev.getY() < bottom);
        }
        return false;
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
