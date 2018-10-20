package com.busradeniz.detection.check.bean;

public class CheckResultBean {

    String name;

    String IsSuccess;

    String error;

    public String getIsSuccess() {
        return IsSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        IsSuccess = isSuccess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
