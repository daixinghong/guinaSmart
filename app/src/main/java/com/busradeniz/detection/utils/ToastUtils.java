package com.busradeniz.detection.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.busradeniz.detection.BaseApplication;


/**
 *
 */


public class ToastUtils {
    public static Toast toast;

    public static void showTextToast(Context context, String msg) {
        if (TextUtils.isEmpty(msg))
            return;
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void showTextToast(String msg) {
        if (TextUtils.isEmpty(msg))
            return;
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.getContext(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void showTextToastLong(String msg) {
        if (TextUtils.isEmpty(msg))
            return;
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.getContext(), msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

}
