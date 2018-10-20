package com.busradeniz.detection.check.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.busradeniz.detection.R;
import com.busradeniz.detection.check.CreateAppearanceConfigureActivity;
import com.busradeniz.detection.setting.CreateVersionActivity;
import com.busradeniz.detection.utils.IntentUtils;

public class ConfigureFragment extends Fragment implements View.OnClickListener {

    private RadioGroup mRgType;
    private RadioButton mRbCutPhoto;
    private RadioButton mRbPhoto;
    private RelativeLayout mRlCreateType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View inflate = inflater.inflate(R.layout.fragment_configure_view, null);

        initView(inflate);

        initData();

        initEvent();

        return inflate;
    }

    private void initEvent() {

        mRlCreateType.setOnClickListener(this);

    }

    private void initData() {
        mRbCutPhoto.setChecked(true);
    }

    private void initView(View inflate) {

        mRgType = inflate.findViewById(R.id.rg_type);
        mRbCutPhoto = inflate.findViewById(R.id.rb_cut_photo);
        mRbPhoto = inflate.findViewById(R.id.rb_photo);
        mRlCreateType = inflate.findViewById(R.id.rl_create_type);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_create_type:

                if (mRbCutPhoto.isChecked()) {   //需要拍照识别零件的配置
                    IntentUtils.startActivity(getActivity(), CreateVersionActivity.class);

                } else {                        //识别壳料的外观
                    IntentUtils.startActivity(getActivity(), CreateAppearanceConfigureActivity.class);

                }

                break;
        }
    }
}
