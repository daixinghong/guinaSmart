package com.busradeniz.detection.check.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.busradeniz.detection.R;

public class DebugFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout mRlNextStep;
    private DecelerationFlagDebugFragment mDecelerationFlagFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View inflate = inflater.inflate(R.layout.fragment_debug_view, null);

        initView(inflate);

        initData();

        initEvent();


        return inflate;

    }

    private void initEvent() {
        mRlNextStep.setOnClickListener(this);
    }

    private void initData() {

    }

    private void initView(View inflate) {
        mRlNextStep = inflate.findViewById(R.id.rl_next_step);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_next_step:
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.hide(this);
                if (mDecelerationFlagFragment == null) {
                    mDecelerationFlagFragment = new DecelerationFlagDebugFragment();
                    transaction.add(R.id.framelayout, mDecelerationFlagFragment);
                } else {
                    transaction.show(mDecelerationFlagFragment);
                }
                transaction.commit();
                break;
        }
    }
}
