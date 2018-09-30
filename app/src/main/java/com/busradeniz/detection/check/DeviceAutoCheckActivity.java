package com.busradeniz.detection.check;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.busradeniz.detection.R;

public class DeviceAutoCheckActivity extends AppCompatActivity implements View.OnClickListener {

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
        setContentView(R.layout.activity_device_auto_check);

        initView();

        initData();


    }

    private void initView() {

        mIvHelp = findViewById(R.id.iv_help);
        mIvHelp.setOnClickListener(this);
        mIvChooseMachine = findViewById(R.id.iv_choose_machine);
        mIvChooseMachine.setOnClickListener(this);
        mIvError = findViewById(R.id.iv_error);
        mIvError.setOnClickListener(this);

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
