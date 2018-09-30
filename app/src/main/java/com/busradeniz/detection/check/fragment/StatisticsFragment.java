package com.busradeniz.detection.check.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.busradeniz.detection.R;

public class StatisticsFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View inflate = inflater.inflate(R.layout.fragment_statiscs_view, null);

        initView();

        initData();

        return inflate;
    }

    private void initData() {

    }

    private void initView() {

    }
}
