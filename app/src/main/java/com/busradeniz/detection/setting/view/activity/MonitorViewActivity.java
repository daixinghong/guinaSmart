package com.busradeniz.detection.setting.view.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.base.BaseActivity;
import com.busradeniz.detection.check.view.adapter.RcyMonitorViewAdapter;
import com.busradeniz.detection.message.IMessage;
import com.busradeniz.detection.setting.model.MonitorListBean;
import com.busradeniz.detection.utils.PlcCommandUtils;
import com.busradeniz.detection.utils.SerialPortManager;
import com.busradeniz.detection.utils.UiUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MonitorViewActivity extends BaseActivity {

    @BindView(R.id.rcy_monitor_view)
    RecyclerView mRcyMonitorView;
    @BindView(R.id.rl_back)
    RelativeLayout mRlBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    private int mIndex;
    private List<MonitorListBean> mList = new ArrayList<>();
    private RcyMonitorViewAdapter mAdapter;
    private String mString;
    private String mCheckTrue = "010101011048";
    private String mCheckFalse = "010101005108";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
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


    private void init() {
        mTvTitle.setText("SINANO-监控画面");
        String[] actionName = UiUtils.getStringArray(R.array.action_name);
        String[] mActionCommand = UiUtils.getStringArray(R.array.action_command);

        for (int i = 0; i < actionName.length; i++) {
            MonitorListBean bean = new MonitorListBean();
            bean.setCommand(mActionCommand[i]);
            bean.setName(actionName[i]);
            mList.add(bean);
        }

        mRcyMonitorView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new RcyMonitorViewAdapter(this, mList);
        mRcyMonitorView.setAdapter(mAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        for (int i = 0; i < mList.size(); i++) {
                            String command0 = PlcCommandUtils.plcCommand2String(mList.get(i).getCommand());
                            SerialPortManager.instance().sendCommand(command0);
                            Thread.sleep(10);
                        }
                    } catch (Exception e) {

                    }
                }

            }
        }).start();

    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_monitor_view;
    }


    @OnClick(R.id.rl_back)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMessage message) {

        if (mIndex == 11) {
            mIndex = 0;
        }

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
                if (message.getMessage().substring(substring.length(), message.getMessage().length()).equals(mCheckTrue)) {
                    updataAdapter(true);

                } else if (message.getMessage().substring(substring.length(), message.getMessage().length()).equals(mCheckFalse)) {
                    updataAdapter(false);
                }

            } else {
                if (message.getMessage().substring(0, 12).equals(mCheckTrue)) {
                    updataAdapter(true);
                } else if (message.getMessage().substring(0, 12).equals(mCheckFalse)) {
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

    public void updataAdapter(boolean status) {
        mString = "";
        mList.get(mIndex).setStatus(status);
        mAdapter.notifyDataSetChanged();
        mIndex++;

    }

}
