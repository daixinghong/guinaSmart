package com.busradeniz.detection.check;

import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.busradeniz.detection.R;
import com.busradeniz.detection.base.BaseActivity;
import com.busradeniz.detection.message.CallBackInterface;
import com.busradeniz.detection.message.IMessage;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.SerialPortManager;
import com.busradeniz.detection.utils.SpUtils;
import com.busradeniz.detection.utils.UiUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SystemSettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private Switch mSwitch1;
    private Switch mSwitch2;
    private String[] mComandArray;
    private String TAG = "daixinhong";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initData();

        initEvent();


    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        mSwitch1 = findViewById(R.id.tv_switch1);
        mSwitch2 = findViewById(R.id.tv_switch2);
    }

    private void initData() {
        mComandArray = UiUtils.getStringArray(R.array.comand_array);
        mSwitch1.setChecked((Boolean) SpUtils.getParam(this,Constant.LEFT_NIGHT,false));
        mSwitch2.setChecked((Boolean) SpUtils.getParam(this,Constant.RIGHT_NIGHT,false));

        SerialPortManager.instance().setCallBack(new CallBackInterface() {

            @Override
            public void leftNightCallBack(String string) {

                Log.e(TAG, "leftNightCallBack: 左灯"+string );

            }

            @Override
            public void rightNightCallBack(String string) {
                Log.e(TAG, "leftNightCallBack: 右灯"+string );
            }

            @Override
            public void tricolorLampCallBack(String string) {

            }

            @Override
            public void runCallBack(String string) {

            }

            @Override
            public void cylinderStatusCallBack(String string) {

            }
        });

        SerialPortManager.instance().sendCommand(UiUtils.getString(R.string.get_left_night_status));
        SerialPortManager.instance().sendCommand(UiUtils.getString(R.string.get_right_night_status));


    }

    private void initEvent() {
        mSwitch1.setOnCheckedChangeListener(this);
        mSwitch2.setOnCheckedChangeListener(this);


    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_system_setting;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        switch (compoundButton.getId()){
            case R.id.tv_switch1:
                if(b){
                    SerialPortManager.instance().sendCommand(mComandArray[6]);
                }else{
                    SerialPortManager.instance().sendCommand(mComandArray[9]);
                }
                SpUtils.putParms(SystemSettingActivity.this,Constant.LEFT_NIGHT,b);
                break;
            case R.id.tv_switch2:
                SpUtils.putParms(SystemSettingActivity.this,Constant.RIGHT_NIGHT,b);
                if(b){
                    SerialPortManager.instance().sendCommand(mComandArray[7]);
                }else{
                    SerialPortManager.instance().sendCommand(mComandArray[8]);
                }

                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMessage message) {

        Log.e("vivi", "onEventsss: "+message.getMessage() );
    }
}
