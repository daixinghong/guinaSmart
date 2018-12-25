package com.busradeniz.detection.setting.view.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.base.BaseActivity;
import com.busradeniz.detection.bean.HandOpreateBean;
import com.busradeniz.detection.check.view.adapter.RcyHandOpreateAdapter;
import com.busradeniz.detection.message.IMessage;
import com.busradeniz.detection.utils.PlcCommandUtils;
import com.busradeniz.detection.utils.SerialPortManager;
import com.busradeniz.detection.utils.UiUtils;
import com.example.reallin.buyapp.jssc.SerialPort;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;

public class SystemSettingActivity extends BaseActivity implements RcyHandOpreateAdapter.OnItemClickListener {

    @BindView(R.id.rcy_hand_opread)
    RecyclerView mRcyHandOpread;
    @BindView(R.id.rl_back)
    RelativeLayout mRlBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    private RcyHandOpreateAdapter mAdapter;
    private String[] mHandActionCommand;
    private SerialPort serialPortFW;
    private int mIndex = -1;
    private List<HandOpreateBean> mList = new ArrayList<>();
    private String[] mStatusCommand;
    private boolean mBoolean;
    private String mString;
    private ThreadPoolExecutor mThreadPoolExecutor;
    private String mCheckTrue = "010101011048";
    private String mCheckFalse = "010101005108";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }


    private void init() {

        //线程池
        mThreadPoolExecutor = new ThreadPoolExecutor(3, 5, 1, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(100));

        mTvTitle.setText("SINANO-手动控制");
        String[] handAction = UiUtils.getStringArray(R.array.hand_action);
        mHandActionCommand = UiUtils.getStringArray(R.array.hand_action_command);
        mStatusCommand = UiUtils.getStringArray(R.array.action_status_command);

        for (int i = 0; i < handAction.length; i++) {
            HandOpreateBean handOpreateBean = new HandOpreateBean();
            handOpreateBean.setName(handAction[i]);
            handOpreateBean.setCommand(mHandActionCommand[i]);
            mList.add(handOpreateBean);
        }

        mRcyHandOpread.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new RcyHandOpreateAdapter(this, mList);
        mRcyHandOpread.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mStatusCommand.length; i++) {
                    String command0 = PlcCommandUtils.plcCommand2String(mStatusCommand[i]);
                    SerialPortManager.instance().sendCommand(command0);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mThreadPoolExecutor.execute(runnable);


    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_system_setting;
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMessage message) {
        if (!mBoolean) {
            if (message.getMessage().length() < 12) {
                mString += message.getMessage();
                if (mString.equals(mCheckTrue)) {
                    updataAdapter(true);
                } else if (mString.equals(mCheckFalse)) {
                    updataAdapter(false);
                }
            } else if (message.getMessage().length() > 12) {
                if (!TextUtils.isEmpty(mString)) {
                    String substring = message.getMessage().substring(0, message.getMessage().length() - 12);
                    mString += substring;
                    if (mString.equals(mCheckTrue)) {
                        updataAdapter(true);
                    } else if (mString.equals(mCheckFalse)) {
                        updataAdapter(false);
                    }
                    if(message.getMessage().substring(substring.length(),message.getMessage().length()).equals(mCheckTrue)){
                        updataAdapter(true);

                    }else if (message.getMessage().substring(substring.length(),message.getMessage().length()).equals(mCheckFalse)) {
                        updataAdapter(false);
                    }

                } else {
                    if(message.getMessage().substring(0,12).equals(mCheckTrue)){
                        updataAdapter(true);
                    }else if(message.getMessage().substring(0,12).equals(mCheckFalse)){
                        updataAdapter(false);
                    }
                    mString += message.getMessage().substring(12, message.getMessage().length());
                }

            } else {
                if (message.getMessage().equals(mCheckTrue)) {
                    updataAdapter(true);
                } else if (message.getMessage().equals(mCheckFalse)) {
                    updataAdapter(false);

                }
            }

        }

    }

    public void updataAdapter(boolean status){
        mIndex++;
        mString = "";
        mList.get(mIndex).setStatus(status);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setOnItemClickListener(View view, int position) {

        mBoolean = true;
        String command1 = PlcCommandUtils.plcCommand2String(mHandActionCommand[position], true, 1);
        String command0 = PlcCommandUtils.plcCommand2String(mHandActionCommand[position], true, 0);

        Log.e(TAG, "setOnItemClickListener:   command1 " + command1);
        Log.e(TAG, "setOnItemClickListener:   command0 " + command0);

        if (position < 7) {
            SerialPortManager.instance().sendCommand(command1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(200);
                        SerialPortManager.instance().sendCommand(command0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            if (mList.get(position).isStatus()) {
                SerialPortManager.instance().sendCommand(command0);
            } else {
                SerialPortManager.instance().sendCommand(command1);
            }
        }

        mList.get(position).setStatus(!mList.get(position).isStatus());
        mAdapter.notifyDataSetChanged();

    }

    @OnClick(R.id.rl_back)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
        }
    }
}
