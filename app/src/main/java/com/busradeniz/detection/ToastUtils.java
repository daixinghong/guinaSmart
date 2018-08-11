package com.busradeniz.detection;

import android.content.Context;
import android.widget.Toast;

/**
 *
 */


public class ToastUtils {
    public static Toast toast;
    public static void showTextToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
    public static void showTextToast( String msg) {
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.getContext(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
}
