package com.fotile.wifi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fotile.wifi.R;
import com.fotile.wifi.bean.WifiScanResult;
import com.fotile.wifi.util.WifiLogUtil;

import java.util.List;

public class WifiListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<WifiScanResult> list;
    private int color_normal = Color.parseColor("#333333");
    private int color_pressed = Color.parseColor("#ff0000");

    public WifiListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<WifiScanResult> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return null == list ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.item_wifi, null);
            holder.txt_wifi_name = (TextView) convertView.findViewById(R.id.txt_wifi_name);
            holder.txt_link_status = (TextView) convertView.findViewById(R.id.txt_link_status);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WifiScanResult wifiScanResult = list.get(position);
        //wifi名称
        String name = wifiScanResult.scanResult.SSID;
        holder.txt_wifi_name.setText(name);

        //连接成功
        if (wifiScanResult.link_state == WifiScanResult.LINK_STATE.LINK_CONNECTED) {
            holder.txt_link_status.setTextColor(color_pressed);
            holder.txt_link_status.setText("已连接");
        }
        //连接中
        else if (wifiScanResult.link_state == WifiScanResult.LINK_STATE.LINK_CONNECTING) {
            holder.txt_link_status.setTextColor(color_pressed);
            holder.txt_link_status.setText("连接中");
        } else {
            holder.txt_link_status.setTextColor(color_normal);
            holder.txt_link_status.setText("未连接");
        }

        return convertView;
    }

    /**
     * 更新某一个item的显示状态
     * @param position
     * @param link_state
     */
    public void notifyItemView(int position, WifiScanResult.LINK_STATE link_state) {
        if (null != list && list.size() > 0) {
            if(position >= 0 && position <= list.size() - 1){
                WifiScanResult wifiScanResult = list.get(position);
                wifiScanResult.link_state = link_state;
                notifyDataSetChanged();
            }
        }
    }

    /**
     * Holder
     */
    public final class ViewHolder {
        /**
         * 可用设备名称
         */
        public TextView txt_wifi_name;
        /**
         * 连接状态文字
         */
        public TextView txt_link_status;

    }
}
