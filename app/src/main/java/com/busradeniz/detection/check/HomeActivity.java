package com.busradeniz.detection.check;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.busradeniz.detection.R;
import com.busradeniz.detection.check.fragment.ConfigureFragment;
import com.busradeniz.detection.check.fragment.SettingFragment;
import com.busradeniz.detection.check.fragment.StatisticsFragment;
import com.busradeniz.detection.check.fragment.WorkFragment;
import com.busradeniz.detection.setting.CreateVersionActivity;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.IntentUtils;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private ImageView mIvHelp;
    private ImageView mIvChooseMachine;
    private ImageView mIvError;
    private RadioButton mRlWord;
    private RadioButton mRlConfigure;
    private RadioButton mRlStatistics;
    private RadioButton mRlSeting;
    private FrameLayout mFramelayout;
    private RadioGroup mRgGroup;
    private WorkFragment mWorkFragment;
    private ConfigureFragment mConfigureFragment;
    private StatisticsFragment mStatisticsFragment;
    private SettingFragment mSettingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        mRlWord = findViewById(R.id.rb_work);
        mRlConfigure = findViewById(R.id.rb_configure);
        mRlStatistics = findViewById(R.id.rb_statistics);
        mRlSeting = findViewById(R.id.rb_setting);
        mRgGroup = findViewById(R.id.rg_group);
        mRgGroup.setOnCheckedChangeListener(this);
        mRlWord.setChecked(true);
    }

    private void initData() {

    }

    public void hideAllFragment(FragmentTransaction transaction) {
        if (mWorkFragment != null) {
            transaction.hide(mWorkFragment);
        }
        if (mConfigureFragment != null) {
            transaction.hide(mConfigureFragment);
        }
        if (mStatisticsFragment != null) {
            transaction.hide(mStatisticsFragment);
        }
        if (mSettingFragment != null) {
            transaction.hide(mSettingFragment);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Bundle bundleExtra = getIntent().getBundleExtra(Constant.BUNDLE_PARMS);
        int index = 0;
        if (bundleExtra != null) {
            index = bundleExtra.getInt(Constant.KEY, 1);
        }
        switch (index) {
            case 0:
                mRlWord.setChecked(true);
                break;
            case 1:
                mRlConfigure.setChecked(true);
                break;
            case 2:
                mRlStatistics.setChecked(true);
                break;
            case 3:
                mRlSeting.setChecked(true);
                break;
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_help:
                IntentUtils.startActivity(this, ScanTwoThinkActivity.class);
                break;
            case R.id.iv_error:
                IntentUtils.startActivity(this, CreateVersionActivity.class);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch (checkedId) {
            case R.id.rb_work:
                if (mWorkFragment == null) {
                    mWorkFragment = new WorkFragment();
                    transaction.add(R.id.framelayout, mWorkFragment);
                } else {
                    transaction.show(mWorkFragment);
                }
                break;
            case R.id.rb_configure:

                IntentUtils.startActivity(this, CreateVersionActivity.class);
//                if (mConfigureFragment == null) {
//                    mConfigureFragment = new ConfigureFragment();
//                    transaction.add(R.id.framelayout, mConfigureFragment);
//                } else {
//                    transaction.show(mConfigureFragment);
//                }
                break;
            case R.id.rb_statistics:
                if (mStatisticsFragment == null) {
                    mStatisticsFragment = new StatisticsFragment();
                    transaction.add(R.id.framelayout, mStatisticsFragment);
                } else {
                    transaction.show(mStatisticsFragment);
                }
                break;
            case R.id.rb_setting:
                if (mSettingFragment == null) {
                    mSettingFragment = new SettingFragment();
                    transaction.add(R.id.framelayout, mSettingFragment);
                } else {
                    transaction.show(mSettingFragment);
                }
                break;
        }

        transaction.commit();
    }
}