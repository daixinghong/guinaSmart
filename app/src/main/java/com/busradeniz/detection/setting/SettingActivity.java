package com.busradeniz.detection.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busradeniz.detection.R;

public class SettingActivity extends AppCompatActivity {

    private TextView mTvVersion;
    private RelativeLayout mRlCheckVersion;
    private RelativeLayout mRlChooseVersion;
    private RelativeLayout mRlCreateType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();

        initData();

        initEvent();

    }

    private void initView() {
        mTvVersion = findViewById(R.id.tv_version);
        mRlCheckVersion = findViewById(R.id.rl_check_version);
        mRlChooseVersion = findViewById(R.id.rl_choose_version);
        mRlCreateType = findViewById(R.id.rl_create_type);
    }

    private void initData() {

    }

    private void initEvent() {

    }
}
