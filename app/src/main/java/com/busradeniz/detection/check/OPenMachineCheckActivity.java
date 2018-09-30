package com.busradeniz.detection.check;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.check.presenter.OpenMachineCheckInterface;
import com.busradeniz.detection.check.presenter.OpenMachineCheckPresenter;
import com.busradeniz.detection.utils.IntentUtils;
import com.busradeniz.detection.utils.UiUtils;

import org.json.JSONObject;

import okhttp3.ResponseBody;

public class OPenMachineCheckActivity extends AppCompatActivity implements View.OnClickListener, OpenMachineCheckInterface {

    private ImageView mIvHelp;
    private ImageView mIvChooseMachine;
    private ImageView mIvError;
    private TextView mTvSeringStatus;
    private TextView mTvTfServingStatus;
    private boolean mTfStatus;
    private boolean mServingStatus;
    private TextView mTvHint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_machine_check);

        initView();

        initData();

        initEvent();

    }

    private void initView() {

        mIvHelp = findViewById(R.id.iv_help);
        mIvChooseMachine = findViewById(R.id.iv_choose_machine);
        mIvError = findViewById(R.id.iv_error);
        mTvSeringStatus = findViewById(R.id.tv_serving_status);
        mTvTfServingStatus = findViewById(R.id.tv_tf_serving_status);
        mTvHint = findViewById(R.id.tv_hint);
    }

    private void initData() {

        OpenMachineCheckPresenter presenter = new OpenMachineCheckPresenter(this);
        presenter.getTensorflowServingStatus();
        presenter.getServingStatus();
    }

    private void initEvent() {
        mIvHelp.setOnClickListener(this);
        mIvChooseMachine.setOnClickListener(this);
        mIvError.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_help:

                break;
            case R.id.iv_choose_machine:

                break;
            case R.id.iv_error:

                break;
        }
    }

    @Override
    public void getTensorflowServingStatusSuccess(ResponseBody responseBody) {
        try {
            String string = responseBody.string();
            JSONObject jsonObject = new JSONObject(string);
            String staus = jsonObject.getString("status");
            int result = jsonObject.getInt("result");
            if (result == 0) {//服务器正常运行
                mTfStatus = true;
                mTvTfServingStatus.setText(UiUtils.getString(R.string.connect));
                mTvTfServingStatus.setTextColor(UiUtils.getColor(R.color.color_50FF00));
            } else {
                mTfStatus = false;
                mTvTfServingStatus.setText(UiUtils.getString(R.string.not_connect));
                mTvTfServingStatus.setTextColor(UiUtils.getColor(R.color.color_E51C23));
            }
            if (mServingStatus && mTfStatus) {
                mTvHint.setText(UiUtils.getString(R.string.serving_and_tfServing_connect));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    IntentUtils.startActivity(OPenMachineCheckActivity.this, HomeActivity.class);
                                    finish();
                                }
                            });

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }

        } catch (Exception e) {
            e.printStackTrace();
            mTfStatus = false;
            mTvTfServingStatus.setText(UiUtils.getString(R.string.not_connect));
            mTvTfServingStatus.setTextColor(UiUtils.getColor(R.color.color_E51C23));
        }
    }

    @Override
    public void getServingStatusSuccess(ResponseBody responseBody) {
        try {
            mServingStatus = true;
            mTvSeringStatus.setText(UiUtils.getString(R.string.connect));
            mTvSeringStatus.setTextColor(UiUtils.getColor(R.color.color_50FF00));
            if (mServingStatus && mTfStatus) {
                mTvHint.setText(UiUtils.getString(R.string.serving_and_tfServing_connect));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    IntentUtils.startActivity(OPenMachineCheckActivity.this, HomeActivity.class);
                                    finish();
                                }
                            });

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }

        } catch (Exception e) {
            e.printStackTrace();
            mServingStatus = false;
            mTvSeringStatus.setText(UiUtils.getString(R.string.not_connect));
            mTvSeringStatus.setTextColor(UiUtils.getColor(R.color.color_E51C23));
        }
    }

    @Override
    public void getDataError(Throwable throwable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            IntentUtils.startActivity(OPenMachineCheckActivity.this, HomeActivity.class);
                            finish();
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}
