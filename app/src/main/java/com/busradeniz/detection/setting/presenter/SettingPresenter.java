package com.busradeniz.detection.setting.presenter;

import com.busradeniz.detection.http.RetrofitNetwork;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SettingPresenter {


    private SettingInterface mSettingInterface;

    public SettingPresenter(SettingInterface settingInterface) {
        mSettingInterface = settingInterface;
    }

    public void getTag() {
        RetrofitNetwork
                .getObserableIntence()
                .getTag().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mSettingInterface.getClassifyDataSuccess(response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mSettingInterface.getDataError(t);
            }
        });

    }


    public void getModel() {
        RetrofitNetwork
                .getObserableIntence()
                .getModel()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSettingInterface::getModelSuccess, mSettingInterface::getDataError);
    }


    public void requestModel(String url, MultipartBody build) {
        RetrofitNetwork
                .getObserableIntence()
                .originalInterface(url, build).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mSettingInterface.checkObjectSuccess(response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mSettingInterface.getDataError(t);
            }
        });

    }


}
