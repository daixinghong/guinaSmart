package com.busradeniz.detection.http;


import com.busradeniz.detection.check.bean.ModelBean;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
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

}
