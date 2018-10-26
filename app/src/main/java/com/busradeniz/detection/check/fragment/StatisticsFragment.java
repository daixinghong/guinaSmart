package com.busradeniz.detection.check.fragment;

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

public class StatisticsFragment extends Fragment {


    private RadioButton mRbOn;
    private RadioGroup mRgGroup;
    private RightRecordFragment mRightRecordFragment;
    private ErrorRecordFragment mErrorRecordFragment;

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


}
