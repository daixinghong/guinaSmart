package com.busradeniz.detection.check.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.busradeniz.detection.R;
import com.busradeniz.detection.base.BaseBean;
import com.busradeniz.detection.bean.ConfigureInfoBean;
import com.busradeniz.detection.bean.ConfigureListBean;
import com.busradeniz.detection.check.model.ModelBean;
import com.busradeniz.detection.check.model.RecordListBean;
import com.busradeniz.detection.check.view.adapter.RcyProductListAdapter;
import com.busradeniz.detection.setting.presenter.SettingInterface;
import com.busradeniz.detection.setting.presenter.SettingPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class WorkFragment extends Fragment implements SettingInterface {

    private EditText mEtSearch;
    private RecyclerView mRcyList;
    private RcyProductListAdapter mAdapter;
    private List<ConfigureListBean.DatasBean> mList = new ArrayList<>();
    private String mSerachName = "";
    private SettingPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View inflate = inflater.inflate(R.layout.fragment_work_view, null);

        initView(inflate);

        initData();

        initEvent();

        return inflate;

    }

    private void initEvent() {

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String string = s.toString();
                mSerachName = string;
                mPresenter.getConfigureList();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null)
            mPresenter.getConfigureList();
    }

    private void initData() {


        mAdapter = new RcyProductListAdapter(getActivity(), mList);
        mRcyList.setAdapter(mAdapter);

        mPresenter = new SettingPresenter(this);
        mPresenter.getConfigureList();


    }

    private void initView(View inflate) {

        mEtSearch = inflate.findViewById(R.id.et_search);
        mRcyList = inflate.findViewById(R.id.rcy_list);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRcyList.setLayoutManager(manager);
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
        map.put("name", mSerachName);
        map.put("config_type_id", 1);

        return map;
    }

    @Override
    public void getConfigureListSuccess(ConfigureListBean configureListBean) {

        if (configureListBean.getResult() == 0) {
            List<ConfigureListBean.DatasBean> datas = configureListBean.getDatas();
            mList.clear();
            mList.addAll(datas);
            mAdapter.notifyDataSetChanged();
        }

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


}
