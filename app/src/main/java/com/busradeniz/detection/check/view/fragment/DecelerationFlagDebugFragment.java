package com.busradeniz.detection.check.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.busradeniz.detection.R;

public class DecelerationFlagDebugFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View inflate = inflater.inflate(R.layout.fragment_deceleration_debug_view, null);

        initView(inflate);

        initData();


        return inflate;
    }

    private void initData() {

    }

    private void initView(View inflate) {

    }
}
