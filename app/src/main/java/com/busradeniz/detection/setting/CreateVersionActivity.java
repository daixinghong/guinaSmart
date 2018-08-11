package com.busradeniz.detection.setting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busradeniz.detection.BaseApplication;
import com.busradeniz.detection.R;
import com.busradeniz.detection.ToastUtils;
import com.busradeniz.detection.bean.NewVersionBean;
import com.busradeniz.detection.bean.SupportBean;
import com.busradeniz.detection.greendaodemo.db.SupportBeanDao;
import com.busradeniz.detection.http.NewsService;
import com.busradeniz.detection.setting.adapter.RcyCreateModleListAdapter;
import com.busradeniz.detection.setting.presenter.SettingInterface;
import com.busradeniz.detection.setting.presenter.SettingPresenter;
import com.busradeniz.detection.tensorflow.Classifier;
import com.busradeniz.detection.tensorflow.TensorFlowObjectDetectionAPIModel;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.CorrectImageUtils;
import com.busradeniz.detection.utils.DialogUtils;
import com.busradeniz.detection.utils.FileUtils;
import com.busradeniz.detection.utils.LocationUtils;
import com.busradeniz.detection.utils.UiUtils;
import com.google.android.cameraview.CameraView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class CreateVersionActivity extends AppCompatActivity implements View.OnClickListener, SettingInterface {

    private ImageView mIvMakePhoto;
    private CameraView mCameraView;
    private RelativeLayout mRlBack;
    private int mWidth;
    private int mHeight;
    private Classifier mDetector;
    private ImageView mIvImage;
    private List<NewVersionBean> mList = new ArrayList<>();
    private RecyclerView mRcyList;
    private RcyCreateModleListAdapter mAdapter;
    private RelativeLayout mRlSave;
    private SupportBeanDao mSupportBeanDao;
    private EditText mEtProjectName;
    private LinearLayout mLlProject;
    private Bitmap mBitmap;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Classifier detector;
    private Bitmap mCorrectBitmap;
    private final String TAG = "daixinhong";
    private NewsService mAnInterface;
    private SettingPresenter mPresenter;
    private List<String> mClassifyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_version);

        initView();

        initData();

        initEvent();
    }

    private void initView() {
        TextView tvTitle = findViewById(R.id.tv_base_title);
        tvTitle.setText(UiUtils.getString(R.string.create_new_mondel));
        mRlBack = findViewById(R.id.rl_back);
        mIvMakePhoto = findViewById(R.id.iv_make_photo);
        mCameraView = findViewById(R.id.cameraView);
        mIvImage = findViewById(R.id.iv_image);
        mRcyList = findViewById(R.id.rcy_list);
        mRlSave = findViewById(R.id.rl_save);

        mEtProjectName = findViewById(R.id.et_project_name);
        mLlProject = findViewById(R.id.ll_project);
        LinearLayoutManager manager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRcyList.setLayoutManager(manager);

        DialogUtils.showTopDialog(getSupportFragmentManager(), R.layout.ldialog_top_tips, 10000);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.stop();
    }

    private void initData() {

        mPresenter = new SettingPresenter(this);
        mPresenter.getClassify();
        mSupportBeanDao = BaseApplication.getApplicatio().getDaoSession().getSupportBeanDao();

        mAdapter = new RcyCreateModleListAdapter(this, mList);
        mRcyList.setAdapter(mAdapter);

        initTensorFlowAndLoadModel();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CameraActivity.REQUEST_CODE && resultCode == CameraActivity.RESULT_CODE) {
//            //获取图片路径，显示图片
//            final String path = CameraActivity.getImagePath(data);
//            if (!TextUtils.isEmpty(path)) {
//                mIvImage.setVisibility(View.VISIBLE);
//                mIvImage.setImageBitmap(BitmapFactory.decodeFile(path));
//            }
//        }
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //初始化封装类
                    detector = TensorFlowObjectDetectionAPIModel.create(
                            getAssets(), Constant.MODEL_FILE, Constant.MODEL_TEXT, 0, 0);
                } catch (IOException e) {

                }
            }
        });
    }


    private void initEvent() {
        mIvMakePhoto.setOnClickListener(this);
        mRlBack.setOnClickListener(this);
        mRlSave.setOnClickListener(this);

        mAdapter.setOnItemClickListener(new RcyCreateModleListAdapter.OnItemClickListener() {
            @Override
            public void setOnItemClickListener(View view, int position) {

//                if (mList.get(position).isStatus()) {
//                    return;
//                } else {
//                    for (int i = 0; i < mList.size(); i++) {
//                        mList.get(i).setStatus(false);
//                    }
//                    mList.get(position).setStatus(true);
//                }
//                mAdapter.notifyDataSetChanged();
//                mIvImage.setImageBitmap(mBitmap);
//                NewVersionBean.Location location = mList.get(position).getLocation();
//                RectF rectF = new RectF(location.getLeft(), location.getTop(), location.getRight(), location.getBottom());
//                Bitmap mutableBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
//                if (mutableBitmap != null) {
//                    Canvas canvas = new Canvas(mutableBitmap);
//                    canvas.drawRect(rectF, mPaint);
//                    mCameraView.setVisibility(View.GONE);
//                    mIvImage.setVisibility(View.VISIBLE);
//                    mIvImage.setImageBitmap(mutableBitmap);
//                }
            }
        });

        mCameraView.addCallback(new CameraView.Callback() {

            @Override
            public void onPictureTaken(CameraView cameraView, final byte[] data) {
                super.onPictureTaken(cameraView, data);

                if (mCorrectBitmap != null)
                    mCorrectBitmap.recycle();

                BaseApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                "picture.jpg");
                        OutputStream os = null;
                        try {
                            os = new FileOutputStream(file);
                            os.write(data);
                            os.close();

//                            if (bitmaps != null)
//                                recognizeImage(bitmaps);

                        } catch (Exception e) {
                            Log.e(TAG, "run: " + e.getMessage());
                        }

                        mBitmap = BitmapFactory.decodeFile(file.getPath());
                        mWidth = mBitmap.getWidth() / 4;
                        mHeight = mBitmap.getHeight() / 4;
                        mCameraView.setVisibility(View.GONE);
                        mIvImage.setVisibility(View.VISIBLE);
                        mRlSave.setVisibility(View.VISIBLE);
                        Bitmap bitmaps = Bitmap.createScaledBitmap(mBitmap, mWidth, mHeight, false);//400,300实际传过去的图片大小

                        Mat recognize = detector.recognize(bitmaps, mWidth, mHeight);

                        Bitmap bitmap2 = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.RGB_565);
                        Utils.matToBitmap(recognize, bitmap2);


                        mCorrectBitmap = CorrectImageUtils.correctImage(recognize, mBitmap, 1);
                        mIvImage.setImageBitmap(mCorrectBitmap);

                        recognizeImage(mCorrectBitmap);

//                        mIvImage.setImageBitmap(mCorrectBitmap);

                    }
                });


            }
        });


    }

    private void recognizeImage(Bitmap bitmaps) {
        File filesDir = getApplicationContext().getFilesDir();
        File file = FileUtils.saveBitmapFile(bitmaps, filesDir.getAbsolutePath() + "text.jpg");

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("multipart/form-data"), file);
        builder.addFormDataPart("images", file.getName(), requestBody);

        mPresenter.getClassify(builder.build());

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_make_photo:
                if (mBitmap != null && !mBitmap.isRecycled()) {
                    mBitmap.recycle();
                    mBitmap = null;
                }
                if (mCameraView.getVisibility() == View.GONE) {
                    mCameraView.setVisibility(View.VISIBLE);
                    mIvImage.setVisibility(View.GONE);
                    mLlProject.setVisibility(View.GONE);
                    mList.clear();
                    mAdapter.notifyDataSetChanged();
                    return;
                }
                System.gc();
                mCameraView.takePicture();
                break;
            case R.id.rl_back:
                finish();
                break;
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


        }
    }


    @Override
    public void getClassifyDataSuccess(ResponseBody responseBody) {
        try {
            String string = responseBody.string();
            JSONObject jsonObject = new JSONObject(string);
            JSONObject category_index = jsonObject.getJSONObject("category_index");
            mClassifyList.clear();
            for (int i = 0; i < category_index.length(); i++) {
                mClassifyList.add(category_index.getJSONObject(i + 1 + "").getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkObjectSuccess(ResponseBody responseBody) {
        try {
            String string = responseBody.string();
            try {
                JSONObject json = new JSONObject(string);
                List<Classifier.Recognition> location = LocationUtils.getLocation(json, mWidth, mHeight);
                mList.clear();
                for (int i = 0; i < location.size(); i++) {
                    if (location.get(i).getConfidence() > 0.5) {
                        NewVersionBean newVersionBean = new NewVersionBean();
                        if (Integer.parseInt(location.get(i).getTitle()) != 0)
                            newVersionBean.setName(mClassifyList.get(Integer.parseInt(location.get(i).getTitle()) - 1));
                        NewVersionBean.Location address = new NewVersionBean.Location();
                        address.setBottom(location.get(i).getLocation().bottom);
                        address.setLeft(location.get(i).getLocation().left);
                        address.setRight(location.get(i).getLocation().right);
                        address.setTop(location.get(i).getLocation().top);
                        newVersionBean.setLocation(address);

                        NewVersionBean.Deviation deviation = new NewVersionBean.Deviation();
                        deviation.setBottom(new NewVersionBean.Deviation.Left());
                        deviation.setTop(new NewVersionBean.Deviation.Left());
                        deviation.setRight(new NewVersionBean.Deviation.Left());
                        deviation.setLeft(new NewVersionBean.Deviation.Left());

                        newVersionBean.setDeviation(deviation);
                        mList.add(newVersionBean);
                    }
                }
                mLlProject.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getDataError(Throwable throwable) {

    }
}
