package com.busradeniz.detection.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.busradeniz.detection.BaseApplication;

public class NetUtils {


    /**
     * 判断是否有网络
     *
     * @return
     */
    public static boolean isNetworkAvailable( ) {

        ConnectivityManager manager = (ConnectivityManager) BaseApplication.getContext()
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }

    /**
     * 判断是否是wifi连接
     *
     * @return
     */
    public static boolean isWifi( ) {
        ConnectivityManager cm = (ConnectivityManager) BaseApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;

    }


}
