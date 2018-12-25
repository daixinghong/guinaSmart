package com.busradeniz.detection.check.model;

import java.util.List;

public class ModelBean {


    /**
     * category_index : ["faster_atrous","faster_atrous_low","faster_atrous_loss","faster_50","faster_101_atrous","faster_101_atrous_loss","faster_50_atrous_low","faster_50_atrous","faster_101","mask_50","ssd_resnet_fpn"]
     * result : 0
     */

    private int result;
    private List<String> category_index;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<String> getCategory_index() {
        return category_index;
    }

    public void setCategory_index(List<String> category_index) {
        this.category_index = category_index;
    }
}
