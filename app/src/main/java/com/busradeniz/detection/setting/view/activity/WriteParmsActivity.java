package com.busradeniz.detection.setting.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.busradeniz.detection.BaseApplication;
import com.busradeniz.detection.R;
import com.busradeniz.detection.ToastUtils;
import com.busradeniz.detection.bean.NewVersionBean;
import com.busradeniz.detection.bean.SupportBean;
import com.busradeniz.detection.greendaodemo.db.SupportBeanDao;
import com.busradeniz.detection.setting.view.adapter.RcyCreateModleListAdapter;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.DialogUtils;
import com.busradeniz.detection.utils.UiUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class WriteParmsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtProjectName;
    private RelativeLayout mRlSave;
    private SupportBeanDao mSupportBeanDao;
    private RcyCreateModleListAdapter mAdapter;
    private List<NewVersionBean> mList = new ArrayList<>();
    private RecyclerView mRcyList;
    private RelativeLayout mRlBack;
    private ArrayList<ArrayList<Integer>> mListArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_parms);

        initView();

        initData();

        initEvent();

    }

    private void initView() {
        mEtProjectName = findViewById(R.id.et_project_name);
        mRlSave = findViewById(R.id.rl_save);
        mRlBack = findViewById(R.id.rl_back);
        mRlSave.setVisibility(View.VISIBLE);
        mRcyList = findViewById(R.id.rcy_list);
        mRcyList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {

        Bundle bundleExtra = getIntent().getBundleExtra(Constant.BUNDLE_PARMS);

        if (bundleExtra != null) {
            ArrayList<String> serializable = (ArrayList<String>) bundleExtra.getSerializable(Constant.KEY);
            mListArrayList = (ArrayList<ArrayList<Integer>>) bundleExtra.getSerializable(Constant.LIST);
            for (int i = 0; i < serializable.size(); i++) {
                NewVersionBean newVersionBean = new NewVersionBean();
                NewVersionBean.Deviation deviation = new NewVersionBean.Deviation();
                NewVersionBean.Deviation.Left left = new NewVersionBean.Deviation.Left();
                deviation.setLeft(left);
                NewVersionBean.Deviation.Left right = new NewVersionBean.Deviation.Left();
                deviation.setRight(right);
                NewVersionBean.Deviation.Left top = new NewVersionBean.Deviation.Left();
                deviation.setTop(top);
                NewVersionBean.Deviation.Left bottom = new NewVersionBean.Deviation.Left();
                deviation.setBottom(bottom);
                newVersionBean.setDeviation(deviation);
                newVersionBean.setName(serializable.get(i));
                mList.add(newVersionBean);
            }
        }

        mSupportBeanDao = BaseApplication.getApplicatio().getDaoSession().getSupportBeanDao();

        mAdapter = new RcyCreateModleListAdapter(this, mList);
        mRcyList.setAdapter(mAdapter);
    }

    private void initEvent() {
        mRlSave.setOnClickListener(this);
        mRlBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_save:
                if (TextUtils.isEmpty(mEtProjectName.getText().toString().trim())) {
                    ToastUtils.showTextToast(UiUtils.getString(R.string.not_input_project_name));
                    return;
                }
                final List<SupportBean> supportBeans = mSupportBeanDao.loadAll();
                DialogUtils.showIosDialog(getSupportFragmentManager(), UiUtils.getString(R.string.confirm_save) + "\"" + (mEtProjectName.getText().toString().trim()) + "\"" + UiUtils.getString(R.string.the_mondel));
                DialogUtils.setOnConfirmClickListener(new DialogUtils.IosDialogListener() {
                    @Override
                    public void onConfirmClickListener(View view) {
                        SupportBean supportBean = new SupportBean();
                        supportBean.setProjectName(mEtProjectName.getText().toString().trim());
                        Gson gson = new Gson();
                        String s = gson.toJson(mList);
                        supportBean.setData(s);
                        supportBean.setLocation(gson.toJson(mListArrayList));
                        if (supportBeans.size() != 0) {
                            supportBean.set_id(supportBeans.get(supportBeans.size() - 1).get_id() + 1);
                        } else {
                            supportBean.set_id(supportBean.get_id() + 1);
                        }
                        mSupportBeanDao.insert(supportBean);
                        ToastUtils.showTextToast(UiUtils.getString(R.string.save_success));
                        finish();
                    }
                });
                break;
            case R.id.rl_back:
                finish();
                break;
        }
    }
}
