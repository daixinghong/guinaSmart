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
import com.busradeniz.detection.bean.ChooseVersionBean;
import com.busradeniz.detection.bean.NewVersionBean;
import com.busradeniz.detection.bean.SupportBean;
import com.busradeniz.detection.check.ScanTwoThinkActivity;
import com.busradeniz.detection.greendaodemo.db.SupportBeanDao;
import com.busradeniz.detection.setting.adapter.RcyChooseVersionAdapter;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.DialogUtils;
import com.busradeniz.detection.utils.IntentUtils;
import com.busradeniz.detection.utils.UiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ChooseVersionActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout mRlBack;
    private RecyclerView mRcyChooseVersion;
    private List<ChooseVersionBean> mList = new ArrayList<>();
    private RcyChooseVersionAdapter mAdapter;
    private RelativeLayout mRlSave;
    private TextView mTvCurrentProduct;
    private View mView;
    private TextView mTvCreateProduct;
    private SupportBeanDao mSupportBeanDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_version);

        initView();

        initData();

        initEvent();

    }

    private void initEvent() {
        mRlBack.setOnClickListener(this);
        mRlSave.setOnClickListener(this);
        mTvCreateProduct.setOnClickListener(this);
        mAdapter.setOnItemClickListener(new RcyChooseVersionAdapter.OnItemClickListener() {
            @Override
            public void setOnItemClickListener(View view, int position) {
                Bundle bundle = new Bundle();
                ArrayList<NewVersionBean> list = (ArrayList<NewVersionBean>) mList.get(position).getList();
                bundle.putSerializable(Constant.KEY, list);
                bundle.putString(Constant.TITLE, mList.get(position).getProjectName());
                IntentUtils.startActivityForParms(ChooseVersionActivity.this, ProductDetailsActivity.class, bundle);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    private void initData() {

        mSupportBeanDao = BaseApplication.getApplicatio().getDaoSession().getSupportBeanDao();

        //获取当前识别的产品
        SupportBean unique = mSupportBeanDao.queryBuilder().where(SupportBeanDao.Properties.SelectedStatus.eq(true)).build().unique();
        if (unique != null) {
            mTvCurrentProduct.setText(unique.getProjectName());
        }

        //获取所有的产品数据
        List<SupportBean> supportBeans = mSupportBeanDao.loadAll();
        Gson gson = new Gson();
        mList.clear();
        for (int i = 0; i < supportBeans.size(); i++) {
            String projectName = supportBeans.get(i).getProjectName();
            String data = supportBeans.get(i).getData();
            List<NewVersionBean> arryList = gson.fromJson(data, new TypeToken<List<NewVersionBean>>() {
            }.getType());

            ChooseVersionBean chooseVersionBean = new ChooseVersionBean();
            chooseVersionBean.setProjectName(projectName);
            chooseVersionBean.setList(arryList);
            mList.add(chooseVersionBean);
        }


        if (mList.size() == 0) {
            mRcyChooseVersion.setVisibility(View.GONE);
            mView.setVisibility(View.VISIBLE);
        } else {
            mRcyChooseVersion.setVisibility(View.VISIBLE);
            mView.setVisibility(View.GONE);
        }

        mAdapter = new RcyChooseVersionAdapter(this, mList);
        mRcyChooseVersion.setAdapter(mAdapter);
    }

    private void initView() {
        mRlBack = findViewById(R.id.rl_back);
        mRlBack.setVisibility(View.GONE);
        TextView tvTitle = findViewById(R.id.tv_base_title);
        tvTitle.setText(UiUtils.getString(R.string.choose_product_model));
        mRcyChooseVersion = findViewById(R.id.rcy_choose_version);
        mRlSave = findViewById(R.id.rl_save);
        mRlSave.setVisibility(View.VISIBLE);
        TextView tvSave = findViewById(R.id.tv_save);
        tvSave.setText(UiUtils.getString(R.string.work));
        mTvCurrentProduct = findViewById(R.id.tv_current_product);
        mView = findViewById(R.id.empty);
        mTvCreateProduct = findViewById(R.id.tv_create_product);
        LinearLayoutManager manager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mRcyChooseVersion.setLayoutManager(manager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_save:
                DialogUtils.showIosDialog(getSupportFragmentManager(), UiUtils.getString(R.string.is_begin_check) + " \"" + mTvCurrentProduct.getText().toString().trim() + "\"");
                DialogUtils.setOnConfirmClickListener(new DialogUtils.IosDialogListener() {
                    @Override
                    public void onConfirmClickListener(View view) {
                        Bundle bundle = new Bundle();
                        IntentUtils.startActivityForParms(ChooseVersionActivity.this, ScanTwoThinkActivity.class, bundle);
                    }
                });

                break;
            case R.id.tv_create_product:
                IntentUtils.startActivity(this, CreateVersionActivity.class);
                break;
        }
    }


}
