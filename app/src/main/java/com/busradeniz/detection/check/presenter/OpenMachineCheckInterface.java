package com.busradeniz.detection.check.presenter;

import com.busradeniz.detection.bean.BaseErrorBean;

import okhttp3.ResponseBody;

public interface OpenMachineCheckInterface extends BaseErrorBean {

    void getTensorflowServingStatusSuccess(ResponseBody responseBody);

    void getServingStatusSuccess(ResponseBody responseBody);
}
