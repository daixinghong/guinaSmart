package com.busradeniz.detection.http;


import com.busradeniz.detection.base.BaseBean;
import com.busradeniz.detection.bean.ConfigureInfoBean;
import com.busradeniz.detection.bean.ConfigureListBean;
import com.busradeniz.detection.check.bean.ModelBean;
import com.busradeniz.detection.check.bean.RecordListBean;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface NewsService {

    /**
     * 获取模型
     */
    @GET("api/get_models_info")
    Observable<ModelBean> getModel();


    /**
     * 获取标签
     *
     * @return
     */
    @GET("api/get_labels")
    Call<ResponseBody> getTag();

    /**
     * 通用接口
     */
    @POST("/api/det/{url}")
    Call<ResponseBody> originalInterface(@Path("url") String url, @Body RequestBody requestBody);

    /**
     * 获取模型状态
     */
    @GET("api/get_model_status/{url}")
    Call<ResponseBody> getModelStatus(@Path("url") String url);


    /**
     * 重启tensorflow_serving服务器
     */
    @POST("api/tensorflow_serving/restart/")
    Call<ResponseBody> restartTensorflowServing(@Path("url") String url, @Body RequestBody requestBody);

    /**
     * 获取tensorflow_serving状态
     */
    @GET("api/tensorflow_serving/status/")
    Call<ResponseBody> getTensorflowServingStatus();

    /**
     * 获取配置列表
     */
    @GET("api/config/index/")
    Observable<ConfigureListBean> getConfigureList(@QueryMap Map<String, Object> map);

    /**
     * 获取单个配置信息
     */
    @GET("api/config/show/{id}")
    Observable<ConfigureInfoBean> getConfigureInfo(@Path("id") int id);

    /**
     * 新建配置
     */
    @POST("api/config/save")
    Observable<BaseBean> createConfigure(@Body RequestBody requestBody);

    /**
     * 更新配置
     */
    @POST("api/config/update/{id}")
    Observable<BaseBean> updataConfigure(@Path("id") int id, @Body RequestBody requestBody);

    /**
     * 删除配置
     */
    @POST("api/config/delete/{id}")
    Call<ResponseBody> deleteConfigure(@Path("id") int id);

    /**
     * 提交检测记录
     */
    @POST("api/record/upload")
    Observable<BaseBean> commitCheckResult(@Body RequestBody requestBody);

    /**
     * 查看记录
     */
    @GET("api/record/index")
    Observable<RecordListBean> seeRecord(@QueryMap Map<String, Object> map);


}
