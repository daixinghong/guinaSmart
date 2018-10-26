package com.busradeniz.detection.setting;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busradeniz.detection.BaseApplication;
import com.busradeniz.detection.R;
import com.busradeniz.detection.ToastUtils;
import com.busradeniz.detection.base.BaseActivity;
import com.busradeniz.detection.base.BaseBean;
import com.busradeniz.detection.bean.ChooseVersionBean;
import com.busradeniz.detection.bean.ConfigureInfoBean;
import com.busradeniz.detection.bean.ConfigureListBean;
import com.busradeniz.detection.bean.NewVersionBean;
import com.busradeniz.detection.bean.SupportBean;
import com.busradeniz.detection.check.ScanTwoThinkActivity;
import com.busradeniz.detection.check.bean.ModelBean;
import com.busradeniz.detection.check.bean.RecordListBean;
import com.busradeniz.detection.greendaodemo.db.SupportBeanDao;
import com.busradeniz.detection.setting.adapter.RcyChooseVersionAdapter;
import com.busradeniz.detection.setting.adapter.RcyModelAdapter;
import com.busradeniz.detection.setting.presenter.SettingInterface;
import com.busradeniz.detection.setting.presenter.SettingPresenter;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.DialogUtils;
import com.busradeniz.detection.utils.IntentUtils;
import com.busradeniz.detection.utils.SpUtils;
import com.busradeniz.detection.utils.UiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class ChooseVersionActivity extends BaseActivity implements View.OnClickListener, SettingInterface {

    private RelativeLayout mRlBack;
    private RecyclerView mRcyChooseVersion;
    private List<ChooseVersionBean> mList = new ArrayList<>();
    private RcyChooseVersionAdapter mAdapter;
    private RelativeLayout mRlSave;
    private TextView mTvCurrentProduct;
    private View mView;
    private TextView mTvCreateProduct;
    private SupportBeanDao mSupportBeanDao;
    private TextView mTvReplaceModel;
    private List<String> mModelList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initData();

        initEvent();

    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_choose_version;
    }

    private void initEvent() {
        mRlBack.setOnClickListener(this);
        mRlSave.setOnClickListener(this);
        mTvCreateProduct.setOnClickListener(this);
        mTvReplaceModel.setOnClickListener(this);
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

        initEvent();
    }

    private void initData() {

        SettingPresenter presenter = new SettingPresenter(this);
        presenter.getModel();

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
        mTvReplaceModel = findViewById(R.id.tv_replace_model);
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
            case R.id.tv_replace_model:

                View view = DialogUtils.inflateView(this, R.layout.dialog_model_view);
                RecyclerView rcyModelList = view.findViewById(R.id.rcy_model_list);
                rcyModelList.setLayoutManager(new LinearLayoutManager(this));
                String modelUrl = (String) SpUtils.getParam(this, Constant.MODEL_URL, "");
                RcyModelAdapter adapter = new RcyModelAdapter(this, mModelList, modelUrl);
                rcyModelList.setAdapter(adapter);
                adapter.setOnItemClickListener(new RcyModelAdapter.OnItemClickListener() {
                    @Override
                    public void setOnItemClickListener(View view, int position) {
                        DialogUtils.dissDialog();
                        SpUtils.putParms(ChooseVersionActivity.this, Constant.MODEL_URL, mModelList.get(position));
                        ToastUtils.showTextToast(UiUtils.getString(R.string.switch_success));
                    }
                });

                DialogUtils.createDialog(view);

                break;
        }
    }


    @Override
    public void getClassifyDataSuccess(ResponseBody responseBody) {

    }

    @Override
    public void checkObjectSuccess(ResponseBody responseBody) {
        try {
            String string = responseBody.string();

            JSONObject jsonObject = new JSONObject(string);
            JSONArray array = jsonObject.getJSONArray("array");
            for (int i = 0; i < array.length(); i++) {


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

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

}
