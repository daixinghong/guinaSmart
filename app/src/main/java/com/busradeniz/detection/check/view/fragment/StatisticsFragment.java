package com.busradeniz.detection.check.view.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.busradeniz.detection.R;
import com.busradeniz.detection.base.BaseBean;
import com.busradeniz.detection.bean.ConfigureInfoBean;
import com.busradeniz.detection.bean.ConfigureListBean;
import com.busradeniz.detection.check.model.ModelBean;
import com.busradeniz.detection.check.model.RecordListBean;
import com.busradeniz.detection.setting.presenter.SettingInterface;
import com.busradeniz.detection.setting.presenter.SettingPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class StatisticsFragment extends Fragment implements SettingInterface, View.OnClickListener {


    private RadioButton mRbOn;
    private RadioGroup mRgGroup;
    private RightRecordFragment mRightRecordFragment;
    private ErrorRecordFragment mErrorRecordFragment;
    private SettingPresenter mPresenter;
    private List<ConfigureListBean.DatasBean> mList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View inflate = inflater.inflate(R.layout.fragment_statiscs_view, null);

        initView(inflate);

        initData();

        initEvent();

        return inflate;
    }

    private void initEvent() {

        mRgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                hideAllFragment(transaction);
                switch (checkedId) {
                    case R.id.rb_on:
                        if (mRightRecordFragment == null) {
                            mRightRecordFragment = new RightRecordFragment();
                            transaction.add(R.id.framelayout_record, mRightRecordFragment);
                        } else {
                            transaction.show(mRightRecordFragment);
                        }
                        break;
                    case R.id.rb_off:
                        if (mErrorRecordFragment == null) {
                            mErrorRecordFragment = new ErrorRecordFragment();
                            transaction.add(R.id.framelayout_record, mErrorRecordFragment);
                        } else {
                            transaction.show(mErrorRecordFragment);
                        }
                        break;
                }

                transaction.commit();

            }
        });

        mRbOn.setChecked(true);
    }


    public void hideAllFragment(FragmentTransaction transaction) {
        if (mRightRecordFragment != null) {
            transaction.hide(mRightRecordFragment);
        }
        if (mErrorRecordFragment != null) {
            transaction.hide(mErrorRecordFragment);
        }

    }


    private void initData() {

        mPresenter = new SettingPresenter(this);
        mPresenter.getConfigureList();

    }

    private void initView(View inflate) {

        mRbOn = inflate.findViewById(R.id.rb_on);
        mRgGroup = inflate.findViewById(R.id.rg_group);


    }


    /**
     * 字符串转ascii码
     *
     * @param val
     * @return
     */
    public static String stringToAscii(String val) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < val.length(); i++) {
            char c = val.charAt(i);
            builder.append((byte) c).append(",");
        }

        return builder.toString();
    }


    @Override
    public void getClassifyDataSuccess(ResponseBody responseBody) {

    }

    @Override
    public void checkObjectSuccess(ResponseBody responseBody) {

    }

    @Override
    public void getModelSuccess(ModelBean modelBean) {

    }

    @Override
    public void createConfigureSuccess(BaseBean baseBean) {

    }

    @Override
    public RequestBody getParms() {
        return null;
    }

    @Override
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("page", 1);
        map.put("pagesize", 20);
        map.put("desc", "");
        map.put("config_type_id", 1);
        return map;
    }

    @Override
    public void getConfigureListSuccess(ConfigureListBean configureListBean) {


    }

    @Override
    public void getConfigureInfoSuccess(ConfigureInfoBean bean) {

    }

    @Override
    public void updataConfigureSuccess(BaseBean baseBean) {

    }

    @Override
    public void commitCheckResultSuccess(BaseBean baseBean) {

    }

    @Override
    public void testCutPhotoSuccess(ResponseBody responseBody) {

    }

    @Override
    public void getRecordListSuccess(RecordListBean recordListBean) {

    }

    @Override
    public void getDataError(Throwable throwable) {

    }

    @Override
    public void onClick(View v) {

    }
}
