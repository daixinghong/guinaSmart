package com.busradeniz.detection.check.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.base.BaseActivity;

public class DeviceAutoCheckActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIvHelp;
    private ImageView mIvChooseMachine;
    private ImageView mIvError;
    private TextView mTvVisionLight;
    private TextView mTvDataGet;
    private TextView mTvPlcContorl;
    private TextView mTvClassif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initData();

    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_device_auto_check;
    }

    private void initView() {



        mTvVisionLight = findViewById(R.id.tv_vision_light);
        mTvDataGet = findViewById(R.id.tv_data_get);
        mTvPlcContorl = findViewById(R.id.tv_plc_contorl);
        mTvClassif = findViewById(R.id.tv_classif);
    }

    private void initData() {

    }


    @Override
    public void onClick(View v) {

    }
}
