package com.busradeniz.detection.setting.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.busradeniz.detection.base.BaseActivity;
import com.busradeniz.detection.http.RetrofitNetwork;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.FileUtils;
import com.busradeniz.detection.utils.SpUtils;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    /**
     * 获取零件名字
     */
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

    /**
     * 获取检测模型
     */
    public void getModel() {
        RetrofitNetwork
                .getObserableIntence()
                .getModel()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSettingInterface::getModelSuccess, mSettingInterface::getDataError);
    }

    /**
     * 新建配置
     */
    public void createConfigure() {
        RetrofitNetwork
                .getObserableIntence()
                .createConfigure(mSettingInterface.getParms())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSettingInterface::createConfigureSuccess, mSettingInterface::getDataError);
    }

    /**
     * 获取配置列表
     */
    public void getConfigureList() {
        RetrofitNetwork
                .getObserableIntence()
                .getConfigureList(mSettingInterface.getMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSettingInterface::getConfigureListSuccess, mSettingInterface::getDataError);
    }

    /**
     * 获取配置信息
     *
     * @param id
     */
    public void getConfigureInfo(int id) {
        RetrofitNetwork
                .getObserableIntence()
                .getConfigureInfo(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSettingInterface::getConfigureInfoSuccess, mSettingInterface::getDataError);
    }

    /**
     * 更新配置信息
     *
     * @param id
     */
    public void updataConfigure(int id) {
        RetrofitNetwork
                .getObserableIntence()
                .updataConfigure(id, mSettingInterface.getParms())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSettingInterface::updataConfigureSuccess, mSettingInterface::getDataError);
    }

    /**
     * 提交检测结果
     */
    public void commitCheckResult() {
        RetrofitNetwork
                .getObserableIntence()
                .commitCheckResult(mSettingInterface.getParms())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSettingInterface::commitCheckResultSuccess, mSettingInterface::getDataError);
    }

    /**
     * 查看检测记录
     */
    public void seeRecord() {
        RetrofitNetwork
                .getObserableIntence()
                .seeRecord(mSettingInterface.getMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSettingInterface::getRecordListSuccess, mSettingInterface::getDataError);
    }


    public void requestModel(Context context, Bitmap bitmaps) {

        File filesDir = context.getFilesDir();
        File file = FileUtils.saveBitmapFile(bitmaps, filesDir.getAbsolutePath() + "text.jpg");

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("multipart/form-data"), file);

        builder.addFormDataPart("images", file.getName(), requestBody);

        String modelUrl = (String) SpUtils.getParam(context, Constant.MODEL_URL, "");

        if (TextUtils.isEmpty(modelUrl))
            return;
        RetrofitNetwork
                .getObserableIntence()
                .originalInterface(modelUrl, builder.build()).enqueue(new Callback<ResponseBody>() {
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

    public void sendPhotoGetResult(Context context, List<Bitmap> list) {

        File filesDir = context.getFilesDir();
        MultipartBody.Builder builder = new MultipartBody.Builder();

        for (int i = 0; i < list.size(); i++) {
            File file = ((BaseActivity) context).saveBitmapFile(list.get(i), filesDir.getAbsolutePath() + "text" + i + ".jpg");
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("images", file.getName(), requestBody);
        }

        builder.setType(MultipartBody.FORM);

        String modelUrl = (String) SpUtils.getParam(context, Constant.MODEL_URL, "");

        if (TextUtils.isEmpty(modelUrl))
            return;

        RetrofitNetwork
                .getObserableIntence()
                .originalInterface(modelUrl, builder.build()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mSettingInterface.testCutPhotoSuccess(response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mSettingInterface.getDataError(t);
            }
        });

    }


}
