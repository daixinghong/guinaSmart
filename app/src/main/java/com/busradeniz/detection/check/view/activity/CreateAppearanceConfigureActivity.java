package com.busradeniz.detection.check.view.activity;

import android.os.Bundle;

import com.busradeniz.detection.R;
import com.busradeniz.detection.base.BaseActivity;

public class CreateAppearanceConfigureActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initView();

        initData();

        initEvent();
    }

    private void initView() {

    }

    private void initData() {

    }

    private void initEvent() {

    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_create_appearance_configure;
    }
}
