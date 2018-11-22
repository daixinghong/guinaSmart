package com.busradeniz.detection.check;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.serialport.SerialPortFinder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.busradeniz.detection.R;
import com.busradeniz.detection.base.BaseActivity;
import com.busradeniz.detection.check.fragment.ConfigureFragment;
import com.busradeniz.detection.check.fragment.SettingFragment;
import com.busradeniz.detection.check.fragment.StatisticsFragment;
import com.busradeniz.detection.check.fragment.WorkFragment;
import com.busradeniz.detection.setting.CreateVersionActivity;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.CorrectImageUtils;
import com.busradeniz.detection.utils.Device;
import com.busradeniz.detection.utils.IntentUtils;
import com.busradeniz.detection.utils.SerialPortManager;
import com.busradeniz.detection.utils.ToastUtils;
import com.busradeniz.detection.utils.UiUtils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.concurrent.ThreadPoolExecutor;

public class HomeActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

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
    private String[] mCommandArray;
    private Bitmap mCorrectBitmap;
    private String[] mReadList;
    private ThreadPoolExecutor mThreadPoolExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initData();

    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_home;
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:

                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private void initData() {

        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        Mat mat = new Mat();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.yy);
        Utils.bitmapToMat(bitmap,mat);
        mCorrectBitmap = CorrectImageUtils.correctImage(mat, bitmap, 1);


//        Bundle bundle = new Bundle();
//        bundle.putInt(Constant.ID,10);
//        IntentUtils.startActivityForParms(this,ScanTwoThinkActivity.class,bundle);
//        mCorrectBitmap = CorrectImageUtils.correctImage(null, null, 1);

        mCommandArray = UiUtils.getStringArray(R.array.comand_array);

        mReadList = UiUtils.getStringArray(R.array.read_list);


        SerialPortFinder serialPortFinder = new SerialPortFinder();

        String[] allDevicesPath = serialPortFinder.getAllDevicesPath();

        if (allDevicesPath == null || allDevicesPath.length == 0) {
            ToastUtils.showTextToast("找不到串口设备");
            return;
        }

        Device device = new Device(allDevicesPath[0], Constant.BAUDRATES);

        boolean open = SerialPortManager.instance().open(device) != null;

        if (open) {
            ToastUtils.showTextToast("成功");
            sendData();
        } else {
            ToastUtils.showTextToast("失败");
        }

    }

    /**
     * 发送命令
     */
    private void sendData() {
        new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
//                            SerialPortManager.instance().sendCommand(mReadList[21]);
                            Thread.sleep(300);
                            SerialPortManager.instance().sendCommand(mCommandArray[0]);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

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
//                IntentUtils.startActivity(this, CreateVersionActivity.class);

                if (mConfigureFragment == null) {
                    mConfigureFragment = new ConfigureFragment();
                    transaction.add(R.id.framelayout, mConfigureFragment);
                } else {
                    transaction.show(mConfigureFragment);
                }
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
