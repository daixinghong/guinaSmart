package com.busradeniz.detection;


import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import com.busradeniz.detection.greendaodemo.db.DaoMaster;
import com.busradeniz.detection.greendaodemo.db.DaoSession;

import java.util.ArrayList;

/**
 * Created by will.li on 2017/6/6.
 */

public class BaseApplication extends Application {
    public static Application mApplication;
    public static BaseApplication mBaseApplication;
    //不管是蓝牙连接方还是服务器方，得到socket对象后都传入
    public static Context mContext;
    public static Handler mHandler;
    public static ArrayList<Integer> mList;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        mBaseApplication = this;

        mContext = getApplicationContext();
        mList = new ArrayList<>();
        mHandler = new Handler(Looper.getMainLooper());
        setDatabase();
    }


    public static ArrayList<Integer> getList() {

        return mList;
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static Context getContext() {
        return mContext;
    }

    public static Application getApplication() {
        return mApplication;
    }

    public static BaseApplication getApplicatio() {
        return mBaseApplication;
    }

    private String username;


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper mHelpter = new DaoMaster.DevOpenHelper(this,"notes-db");
        db = mHelpter.getWritableDatabase();
//        mHelper.onUpgrade(db,2,3);
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();

    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }





    //不管是蓝牙连接方还是服务器方，得到socket对象后都传入
    public static BluetoothSocket bluetoothSocket;
    public static boolean isScanConnect;


}