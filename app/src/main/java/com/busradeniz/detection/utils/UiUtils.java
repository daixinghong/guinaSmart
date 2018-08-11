package com.busradeniz.detection.utils;

import com.busradeniz.detection.BaseApplication;

public class UiUtils {


    public static String getString(int id) {

        return BaseApplication.getContext().getResources().getString(id);
    }

    public static String[] getStringArray(int id) {
        return BaseApplication.getContext().getResources().getStringArray(id);
    }

    public static int getColor(int id) {
        return BaseApplication.getContext().getResources().getColor(id);
    }


}
