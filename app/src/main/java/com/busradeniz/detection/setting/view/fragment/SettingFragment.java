package com.busradeniz.detection.setting.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.busradeniz.detection.R;
import com.busradeniz.detection.ToastUtils;
import com.busradeniz.detection.base.BaseBean;
import com.busradeniz.detection.bean.ConfigureInfoBean;
import com.busradeniz.detection.bean.ConfigureListBean;
import com.busradeniz.detection.check.model.ModelBean;
import com.busradeniz.detection.check.model.RecordListBean;
import com.busradeniz.detection.setting.view.activity.AlarmActivity;
import com.busradeniz.detection.setting.view.activity.MonitorViewActivity;
import com.busradeniz.detection.setting.view.activity.ParmsSettingActivity;
import com.busradeniz.detection.setting.view.activity.SystemSettingActivity;
import com.busradeniz.detection.setting.view.adapter.RcyModelAdapter;
import com.busradeniz.detection.setting.presenter.SettingInterface;
import com.busradeniz.detection.setting.presenter.SettingPresenter;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.DialogUtils;
import com.busradeniz.detection.utils.IntentUtils;
import com.busradeniz.detection.utils.SpUtils;
import com.busradeniz.detection.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class SettingFragment extends Fragment implements SettingInterface, View.OnClickListener {


    @BindView(R.id.rl_check_version)
    RelativeLayout mRlCheckVersion;
    @BindView(R.id.rl_replace_model)
    RelativeLayout mRlReplaceModel;
    @BindView(R.id.rl_system_setting)
    RelativeLayout mRlSystemSetting;
    @BindView(R.id.rl_monitor)
    RelativeLayout mRlMonitor;
    @BindView(R.id.rl_parms_setting)
    RelativeLayout mRlParmsSetting;
    @BindView(R.id.rl_choose_version)
    RelativeLayout mRlChooseVersion;
    @BindView(R.id.rl_alarm)
    RelativeLayout mRlAlarm;
    private List<String> mModelList = new ArrayList<>();
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View inflate = inflater.inflate(R.layout.fragment_setting_view, null);
        mBind = ButterKnife.bind(this, inflate);
        init();
        return inflate;
    }

    private void init() {
        SettingPresenter presenter = new SettingPresenter(this);
        presenter.getModel();

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
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.rl_check_version, R.id.rl_replace_model,
            R.id.rl_system_setting, R.id.rl_monitor,
            R.id.rl_parms_setting, R.id.rl_choose_version,
            R.id.rl_alarm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_check_version:
                break;
            case R.id.rl_replace_model:
                View viewDialog = DialogUtils.inflateView(getActivity(), R.layout.dialog_model_view);
                RecyclerView rcyModelList = viewDialog.findViewById(R.id.rcy_model_list);
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
            case R.id.rl_system_setting:
                IntentUtils.startActivity(getActivity(), SystemSettingActivity.class);
                break;
            case R.id.rl_monitor:
                IntentUtils.startActivity(getActivity(), MonitorViewActivity.class);
                break;
            case R.id.rl_parms_setting:
                IntentUtils.startActivity(getActivity(), ParmsSettingActivity.class);
                break;
            case R.id.rl_choose_version:
                break;
            case R.id.rl_alarm:
                IntentUtils.startActivity(getActivity(),AlarmActivity.class);
                break;
        }
    }


}
