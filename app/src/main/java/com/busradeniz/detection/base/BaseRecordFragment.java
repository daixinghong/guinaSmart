package com.busradeniz.detection.base;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.busradeniz.detection.R;
import com.busradeniz.detection.bean.ConfigureInfoBean;
import com.busradeniz.detection.bean.ConfigureListBean;
import com.busradeniz.detection.check.adapter.RcyRecordListAdapter;
import com.busradeniz.detection.check.bean.ModelBean;
import com.busradeniz.detection.check.bean.RecordListBean;
import com.busradeniz.detection.setting.presenter.SettingInterface;
import com.busradeniz.detection.setting.presenter.SettingPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class BaseRecordFragment extends Fragment implements SettingInterface {


    private RecyclerView mRcyResultList;
    private RcyRecordListAdapter mAdapter;
    private List<RecordListBean.DatasBean> mList = new ArrayList<>();
    private SettingPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View inflate = inflater.inflate(R.layout.fragment_record_view, null);

        initView(inflate);

        initData();

        initEvent();

        return inflate;
    }

    private void initView(View inflate) {
        mRcyResultList = inflate.findViewById(R.id.rcy_result_list);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRcyResultList.setLayoutManager(staggeredGridLayoutManager);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

    }

    private void initData() {

        mAdapter = new RcyRecordListAdapter(getActivity(), mList);
        mRcyResultList.setAdapter(mAdapter);

        mPresenter = new SettingPresenter(this);
        mPresenter.seeRecord();

    }

    private void initEvent() {

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
        map.put("start_time", "");
        map.put("end_time", "");
//        map.put("config_id", 1);

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
        if (recordListBean.getResult() == 0) {
            List<RecordListBean.DatasBean> datas = recordListBean.getDatas();
            mList.clear();
            mList.addAll(datas);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getDataError(Throwable throwable) {

    }
}
