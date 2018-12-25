package com.busradeniz.detection.setting.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.base.BaseActivity;
import com.busradeniz.detection.bean.ParmsListBean;
import com.busradeniz.detection.check.view.adapter.RcyParmsListAdapter;
import com.busradeniz.detection.message.IMessage;
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

public class ParmsSettingActivity extends BaseActivity {

    @BindView(R.id.rl_back)
    RelativeLayout mRlBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.rcy_parms_list)
    RecyclerView mRcyParmsList;
    private List<ParmsListBean> mList = new ArrayList<>();
    private boolean mBoolean;
    private String mString;

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

        //命令数组
        String[] updataTime = UiUtils.getStringArray(R.array.updata_time_name);
        String[] updataTimeCommand = UiUtils.getStringArray(R.array.updata_time_command);

        for (int i = 0; i < updataTime.length; i++) {
            ParmsListBean parmsListBean = new ParmsListBean();
            parmsListBean.setCommand(updataTimeCommand[i]);
            parmsListBean.setName(updataTime[i]);
            mList.add(parmsListBean);
        }

        //获取设置参数的信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < mList.size(); i++) {
                        String command = PlcCommandUtils.plcCommand2String(mList.get(i).getCommand());
                        SerialPortManager.instance().sendCommand(command);
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {

                }
            }
        }).start();

        LinearLayoutManager manager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        mRcyParmsList.setLayoutManager(manager);

        RcyParmsListAdapter adapter = new RcyParmsListAdapter(this, mList);
        mRcyParmsList.setAdapter(adapter);

    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_parms_setting;
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

        if (!mBoolean) {
            if (message.getMessage().length() < 12) {
                mString += message.getMessage();

                


            } else if (message.getMessage().length() > 12) {
                if (!TextUtils.isEmpty(mString)) {
                    String substring = message.getMessage().substring(0, message.getMessage().length() - 12);
                    mString += substring;

                } else {

                    mString += message.getMessage().substring(12, message.getMessage().length());
                }

            } else {

            }

        }

    }

}
