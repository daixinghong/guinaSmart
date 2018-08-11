package com.busradeniz.detection.http;


import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NewsService {


    @POST("api/")
    Call<ResponseBody> getNews(@Body RequestBody requestBody);

    @POST("api/faster_50/")
    Call<ResponseBody> test1(@Body RequestBody requestBody);

    @POST("api/ssd_v1/")
    Call<ResponseBody> test2(@Body RequestBody requestBody);

    @POST("api/faster_v2/")
    Call<ResponseBody> test3(@Body RequestBody requestBody);

    @POST("api/mask_50/")
    Call<ResponseBody> test4(@Body RequestBody requestBody);

    @POST("api/ssd_resnet_fpn/")
    Call<ResponseBody> test5(@Body RequestBody requestBody);

    @POST("api/get_labels/")
    Call<ResponseBody> getTag();


}
