package com.busradeniz.detection.setting.presenter;

import com.busradeniz.detection.bean.BaseErrorBean;

import okhttp3.ResponseBody;

public interface SettingInterface extends BaseErrorBean {


    void getClassifyDataSuccess(ResponseBody responseBody);

    void checkObjectSuccess(ResponseBody responseBody);




}
