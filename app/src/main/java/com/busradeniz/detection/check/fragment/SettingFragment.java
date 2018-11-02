package com.busradeniz.detection.check.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.busradeniz.detection.R;
import com.busradeniz.detection.ToastUtils;
import com.busradeniz.detection.base.BaseBean;
import com.busradeniz.detection.bean.ConfigureInfoBean;
import com.busradeniz.detection.bean.ConfigureListBean;
import com.busradeniz.detection.check.bean.ModelBean;
import com.busradeniz.detection.check.bean.RecordListBean;
import com.busradeniz.detection.setting.adapter.RcyModelAdapter;
import com.busradeniz.detection.setting.presenter.SettingInterface;
import com.busradeniz.detection.setting.presenter.SettingPresenter;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.DialogUtils;
import com.busradeniz.detection.utils.SpUtils;
import com.busradeniz.detection.utils.UiUtils;
import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.JCameraListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class SettingFragment extends Fragment implements SettingInterface, View.OnClickListener {


    private List<String> mModelList = new ArrayList<>();
    private RelativeLayout mRlReplaceModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View inflate = inflater.inflate(R.layout.fragment_setting_view, null);

        initView(inflate);

        initData();

        initEvent();

        return inflate;
    }

    private void initEvent() {
        mRlReplaceModel.setOnClickListener(this);
    }

    private void initData() {


        SettingPresenter presenter = new SettingPresenter(this);
        presenter.getModel();

    }

    private void initView(View view) {
        mRlReplaceModel = view.findViewById(R.id.rl_replace_model);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void getClassifyDataSuccess(ResponseBody responseBody) {

    }

    @Override
    public void checkObjectSuccess(ResponseBody responseBody) {

    }

    @Override
    public void getModelSuccess(ModelBean modelBean) {

        switch (modelBean.getResult()) {
            case 0:
                List<String> category_index = modelBean.getCategory_index();
                mModelList.clear();
                mModelList.addAll(category_index);
                break;
        }
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
        return null;
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

        throwable.printStackTrace();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_replace_model:

                View view = DialogUtils.inflateView(getActivity(), R.layout.dialog_model_view);
                RecyclerView rcyModelList = view.findViewById(R.id.rcy_model_list);
                rcyModelList.setLayoutManager(new LinearLayoutManager(getActivity()));
                String modelUrl = (String) SpUtils.getParam(getActivity(), Constant.MODEL_URL, "");
                RcyModelAdapter adapter = new RcyModelAdapter(getActivity(), mModelList, modelUrl);
                rcyModelList.setAdapter(adapter);
                adapter.setOnItemClickListener(new RcyModelAdapter.OnItemClickListener() {
                    @Override
                    public void setOnItemClickListener(View view, int position) {
                        DialogUtils.dissDialog();
                        SpUtils.putParms(getActivity(), Constant.MODEL_URL, mModelList.get(position));
                        ToastUtils.showTextToast(UiUtils.getString(R.string.switch_success));
                    }
                });

                DialogUtils.createDialog(view);
                break;
        }
    }
}
