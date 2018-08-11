package com.busradeniz.detection.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class IntentUtils {

    /**
     * 不带参数的跳转，并且不关掉当前的activity
     * @param context 上下文
     * @param clazz 需要调到哪个actiity
     */
    public static void startActivity(Context context , Class<? extends Activity> clazz){

        Intent intent = new Intent(context,clazz);
        context.startActivity(intent);
    }
    /**
     * 不带参数的跳转，关掉当前的activity
     * @param context 上下文
     * @param clazz 需要调到哪个actiity
     */
    public static void startActivityAndFinish(Activity context , Class<? extends Activity> clazz){

        Intent intent = new Intent(context,clazz);
        context.startActivity(intent);
        context.finish();
    }

    /**
     * 带参数的跳转,不关闭当前的activity
     * @param context
     * @param clazz
     * @param bundle
     */
    public static void startActivityForParms(Context context, Class<? extends Activity> clazz, Bundle bundle){

        Intent intent = new Intent(context,clazz);
        intent.putExtra("bundleParms",bundle);
        context.startActivity(intent);
    }

    /**
     * 带参数的跳转,关闭当前的activity
     * @param context
     * @param clazz
     * @param bundle
     */
    public static void startActivityForParmsAndFinish(Activity context, Class<? extends Activity> clazz, Bundle bundle){

        Intent intent = new Intent(context,clazz);
        intent.putExtra("bundleParms",bundle);
        context.startActivity(intent);
        context.finish();
    }


    /**
     * 不带参数的跳转，需要返回结果
     * @param context 上下文
     * @param clazz 需要调到哪个actiity
     */
    public static void startActivityforResult(Activity context , Class<? extends Activity> clazz, int requestCode){

        Intent intent = new Intent(context,clazz);
        context.startActivityForResult(intent,requestCode);
    }

    /**
     * 带参数的跳转，需要返回结果
     * @param context 上下文
     * @param clazz 需要调到哪个actiity
     */
    public static void startActivityforResultAndParms(Activity context , Class<? extends Activity> clazz, int requestCode, Bundle bundle){

        Intent intent = new Intent(context,clazz);
        intent.putExtra("bundleParms",bundle);
        context.startActivityForResult(intent,requestCode);
    }


    /**
     * 带参数的跳转，需要返回结果
     * @param context 上下文
     * @param clazz 需要调到哪个actiity
     */
    public static void starFragemntTotActivityforResultAndParms(Activity context , Class<? extends Activity> clazz, int requestCode, Bundle bundle){

        Intent intent = new Intent();
        intent.setClass(context,clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("bundleParms",bundle);
        context.startActivityForResult(intent,requestCode);
    }

    /**
     * 带参数的跳转，需要返回结果
     * @param context 上下文
     * @param clazz 需要调到哪个actiity
     */
    public static void startActivityforResultAndParms(Activity context , Class<? extends Activity> clazz, int requestCode){

        Intent intent = new Intent(context,clazz);
        context.startActivityForResult(intent,requestCode);
    }





}
