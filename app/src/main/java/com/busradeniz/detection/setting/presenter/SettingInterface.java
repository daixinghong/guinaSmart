package com.busradeniz.detection.setting.presenter;

import com.busradeniz.detection.base.BaseBean;
import com.busradeniz.detection.bean.BaseErrorBean;
import com.busradeniz.detection.bean.ConfigureInfoBean;
import com.busradeniz.detection.bean.ConfigureListBean;
import com.busradeniz.detection.check.bean.ModelBean;
import com.busradeniz.detection.check.bean.RecordListBean;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public interface SettingInterface extends BaseErrorBean {


    void getClassifyDataSuccess(ResponseBody responseBody);

    void checkObjectSuccess(ResponseBody responseBody);

    void getModelSuccess(ModelBean modelBean);

    void createConfigureSuccess(BaseBean baseBean);

    RequestBody getParms();

    Map<String,Object> getMap();

    void getConfigureListSuccess(ConfigureListBean configureListBean);

    void getConfigureInfoSuccess(ConfigureInfoBean bean);

    void updataConfigureSuccess(BaseBean baseBean);

    void commitCheckResultSuccess(BaseBean baseBean);

    void testCutPhotoSuccess(ResponseBody responseBody);

    void getRecordListSuccess(RecordListBean recordListBean);








}
