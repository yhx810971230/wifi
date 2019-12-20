package com.fotile.wifi.util;

import android.util.Log;

public class WifiLogUtil {

    public static boolean isDebug = true;

    public static void LOG_WIFI(String TAG, Object obj) {
        if (isDebug) {
            if (null != obj) {
                Log.e("----wifi----" + TAG, obj.toString());
            } else {
                Log.e("----wifi----" + TAG, "null--");
            }
        }
    }
}
