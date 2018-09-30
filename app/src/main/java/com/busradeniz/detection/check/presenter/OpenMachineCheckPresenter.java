package com.busradeniz.detection.check.presenter;

import com.busradeniz.detection.http.RetrofitNetwork;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenMachineCheckPresenter {

    private OpenMachineCheckInterface mOpenMachineCheckInterface;

    public OpenMachineCheckPresenter(OpenMachineCheckInterface openMachineCheckInterface) {
        mOpenMachineCheckInterface = openMachineCheckInterface;
    }


    public void getTensorflowServingStatus() {
        RetrofitNetwork
                .getObserableIntence()
                .getTensorflowServingStatus().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mOpenMachineCheckInterface.getTensorflowServingStatusSuccess(response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mOpenMachineCheckInterface.getDataError(t);
            }
        });
    }


    public void getServingStatus() {
        RetrofitNetwork
                .getObserableIntence()
                .getTag().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mOpenMachineCheckInterface.getServingStatusSuccess(response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mOpenMachineCheckInterface.getDataError(t);
            }
        });
    }


}
