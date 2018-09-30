package com.busradeniz.detection.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busradeniz.detection.BaseApplication;
import com.busradeniz.detection.R;
import com.busradeniz.detection.ToastUtils;
import com.busradeniz.detection.bean.NewVersionBean;
import com.busradeniz.detection.bean.SupportBean;
import com.busradeniz.detection.greendaodemo.db.SupportBeanDao;
import com.busradeniz.detection.setting.adapter.RcyProductDetailsAdapter;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.DialogUtils;
import com.busradeniz.detection.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRcyProductDetailsList;
    private List<NewVersionBean> mList = new ArrayList();
    private RelativeLayout mRlBack;
    private RelativeLayout mRlSave;
    private SupportBeanDao mSupportBeanDao;
    private String mTitle;
    private TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        initView();

        initData();

        initEvent();


    }

    private void initEvent() {
        mRlBack.setOnClickListener(this);
        mRlSave.setOnClickListener(this);
    }

    private void initData() {

        mSupportBeanDao = BaseApplication.getApplicatio().getDaoSession().getSupportBeanDao();
        Bundle bundleExtra = getIntent().getBundleExtra(Constant.BUNDLE_PARMS);
        if (bundleExtra != null) {
            mTitle = bundleExtra.getString(Constant.TITLE);
            mTvTitle.setText(mTitle);
            ArrayList<NewVersionBean> list = (ArrayList<NewVersionBean>) bundleExtra.getSerializable(Constant.KEY);
            mList.clear();
            mList.addAll(list);
        }
        RcyProductDetailsAdapter adapter = new RcyProductDetailsAdapter(this, mList);
        mRcyProductDetailsList.setAdapter(adapter);
    }

    private void initView() {
        mRcyProductDetailsList = findViewById(R.id.rcy_product_details_list);
        mRlBack = findViewById(R.id.rl_back);
        mTvTitle = findViewById(R.id.tv_base_title);
        TextView tvSave = findViewById(R.id.tv_save);
        mRlSave = findViewById(R.id.rl_save);
        mRlSave.setVisibility(View.VISIBLE);
        tvSave.setText(UiUtils.getString(R.string.switchs));
        mRcyProductDetailsList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_save:

                DialogUtils.showIosDialog(getSupportFragmentManager(), UiUtils.getString(R.string.is_switch_project));
                DialogUtils.setOnConfirmClickListener(new DialogUtils.IosDialogListener() {
                    @Override
                    public void onConfirmClickListener(View view) {
                        //把之前识别中的产品的状态改为false
                        queryData(true, false);

                        //把这个产品状态设置为true
                        if (queryData(mTitle, true)) {
                            ToastUtils.showTextToast(UiUtils.getString(R.string.switch_success));
                        } else {
                            ToastUtils.showTextToast(UiUtils.getString(R.string.switch_fild));
                        }
                        finish();
                    }
                });

                break;
        }
    }

    /**
     * 更新数据库产品型号数据
     *
     * @param key
     * @return
     */
    public boolean queryData(String key, boolean status) {
        try {
            SupportBean unique = mSupportBeanDao.queryBuilder().where(SupportBeanDao.Properties.ProjectName.eq(key)).build().unique();
            if (unique != null) {
                unique.setSelectedStatus(status);
                mSupportBeanDao.update(unique);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }

    public boolean queryData(boolean key, boolean status) {
        try {
            SupportBean unique = mSupportBeanDao.queryBuilder().where(SupportBeanDao.Properties.SelectedStatus.eq(key)).build().unique();
            if (unique != null) {
                unique.setSelectedStatus(status);
                mSupportBeanDao.update(unique);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }


}
