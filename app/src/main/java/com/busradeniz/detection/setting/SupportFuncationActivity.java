package com.busradeniz.detection.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busradeniz.detection.R;

public class SupportFuncationActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout mRlBack;
    private RecyclerView mRcySupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_funcation);

        initView();

        initData();

        initEvent();

    }

    private void initView() {
        mRlBack = findViewById(R.id.rl_back);
        TextView tvTitle = findViewById(R.id.tv_base_title);
        mRcySupport = findViewById(R.id.rcy_support);
        mRcySupport.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initData() {

    }

    private void initEvent() {
        mRlBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
        }
    }
}
