package com.busradeniz.detection.check;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.busradeniz.detection.BaseApplication;
import com.busradeniz.detection.R;
import com.busradeniz.detection.ToastUtils;
import com.busradeniz.detection.base.BaseActivity;
import com.busradeniz.detection.base.BaseBean;
import com.busradeniz.detection.bean.ConfigureInfoBean;
import com.busradeniz.detection.bean.ConfigureListBean;
import com.busradeniz.detection.bean.LocationBean;
import com.busradeniz.detection.bean.NewVersionBean;
import com.busradeniz.detection.bean.SupportBean;
import com.busradeniz.detection.check.adapter.RcyCheckResultAdapter;
import com.busradeniz.detection.check.bean.CheckResultBean;
import com.busradeniz.detection.check.bean.ModelBean;
import com.busradeniz.detection.check.bean.RecordListBean;
import com.busradeniz.detection.check.bean.RectBean;
import com.busradeniz.detection.env.Logger;
import com.busradeniz.detection.setting.ChooseVersionActivity;
import com.busradeniz.detection.setting.CreateVersionActivity;
import com.busradeniz.detection.setting.DrawRectActivity;
import com.busradeniz.detection.setting.SupportFuncationActivity;
import com.busradeniz.detection.setting.presenter.SettingInterface;
import com.busradeniz.detection.setting.presenter.SettingPresenter;
import com.busradeniz.detection.tensorflow.Classifier;
import com.busradeniz.detection.tensorflow.TensorFlowObjectDetectionAPIModel;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.CorrectImageUtils;
import com.busradeniz.detection.utils.IntentUtils;
import com.busradeniz.detection.utils.LocationUtils;
import com.busradeniz.detection.utils.UiUtils;
import com.google.android.cameraview.CameraView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


/**
 */

public class ScanTwoThinkActivity extends BaseActivity implements View.OnClickListener, MenuItem.OnMenuItemClickListener, SettingInterface {

    private TextView textViewResult;
    private ImageView btnDetectObject;
    private CameraView cameraView;
    private final String TAG = "daixinhong";

    private static final Logger LOGGER = new Logger();

    private int TF_OD_API_INPUT_SIZE;
    private int TF_Wdith_API_INPUT_SIZE;
    private Classifier detector;
    private ImageView mImageView;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Bitmap bitmap;
    private List<CheckResultBean> mList = new ArrayList<>();
    private NavigationView mNavigationView;
    private Bitmap mBitmaps;
    private float mWidth;
    private boolean mStatus;
    private float mHeight;
    private Bitmap mResizeBitmap;
    private RecyclerView mRcyDislikeInfoList;
    private RcyCheckResultAdapter mAdapter;
    private TextView mTvCheckName;
    private ImageView mIvSetting;
    private DrawerLayout mDrawerLayout;
    private List<RectBean> mLocationList = new ArrayList<>();
    private ImageView mIvBack;
    private SupportBean mSupportBean;
    private List<NewVersionBean> mArryList = new ArrayList<>();
    private List<Bitmap> mShortBitmapList;
    private SettingPresenter mPresenter;
    private List<String> mClassifyList = new ArrayList<>();
    private Bitmap mCorrectBitmap;
    private int mId;
    private int flag;
    private String mDesc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intView();

        intData();

        initEvent();

//        if (!UsbConnect.isConnect(this)) {
//            UsbConnect.Connect(this);
//        }

    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_two_think_scan;
    }


    private void initEvent() {


        mIvBack.setOnClickListener(this);
        cameraView.addCallback(new CameraView.Callback() {
            @Override
            public void onCameraOpened(CameraView cameraView) {
                super.onCameraOpened(cameraView);
            }

            @Override
            public void onCameraClosed(CameraView cameraView) {
                super.onCameraClosed(cameraView);
            }

            @Override
            public void onPictureTaken(final CameraView cameraView, final byte[] data) {
                super.onPictureTaken(cameraView, data);

                BaseApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "picture.jpg");
                        OutputStream os = null;
                        try {
                            os = new FileOutputStream(file);
                            os.write(data);
                            os.close();
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
//                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.xixix5);
                            TF_OD_API_INPUT_SIZE = bitmap.getWidth() / 4;
                            TF_Wdith_API_INPUT_SIZE = bitmap.getHeight() / 4;

                            mBitmaps = Bitmap.createScaledBitmap(bitmap, TF_OD_API_INPUT_SIZE, TF_Wdith_API_INPUT_SIZE, false);

                            mImageView.setVisibility(View.VISIBLE);
                            cameraView.setVisibility(View.GONE);
                            mImageView.setImageBitmap(bitmap);
                            Mat recognize = detector.recognize(mBitmaps, TF_OD_API_INPUT_SIZE, TF_Wdith_API_INPUT_SIZE);

                            Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                            Utils.matToBitmap(recognize, bitmap2);

                            mCorrectBitmap = CorrectImageUtils.correctImage(recognize, bitmap, 1);
                            mImageView.setImageBitmap(mCorrectBitmap);

                            mShortBitmapList = new ArrayList<>();
                            for (int i = 0; i < mLocationList.size(); i++) {
                                RectBean rectBean = mLocationList.get(i);
                                int width = Math.abs(rectBean.getLeft() - rectBean.getRight());
                                int height = Math.abs(rectBean.getTop() - rectBean.getBottom());
                                Bitmap cropBitmap = Bitmap.createBitmap(mCorrectBitmap, rectBean.getLeft(), rectBean.getTop(), width, height);
                                mShortBitmapList.add(cropBitmap);
                            }


                            try {
                                FileOutputStream out = null;
                                for (int i = 0; i < mShortBitmapList.size(); i++) {
                                    long startTime = System.currentTimeMillis();
                                    File fileDir = new File("/sdcard/srcImage/" + startTime + ".png");
                                    fileDir.createNewFile();
                                    out = new FileOutputStream(fileDir);
                                    mShortBitmapList.get(i).compress(Bitmap.CompressFormat.JPEG, 100, out);

                                }
                                out.flush();
                                out.close();
                            } catch (Exception e) {

                            }

                            //发送请求
                            mPresenter.sendPhotoGetResult(ScanTwoThinkActivity.this, mShortBitmapList);


                        } catch (Exception e) {
                            ToastUtils.showTextToast(UiUtils.getString(R.string.photo_check_fild));

                        } finally {
                            if (os != null) {
                                try {
                                    os.close();
                                } catch (IOException e) {
                                    // Ignore
                                }
                            }
                        }

                    }
                });

            }
        });


        //拍照键
        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < mList.size(); i++) {
                    mList.get(i).setError("");
                    mList.get(i).setIsSuccess("");
                }

                if (mResizeBitmap != null && !mResizeBitmap.isRecycled()) {
                    mResizeBitmap.recycle();
                    mResizeBitmap = null;
                }
                if (cameraView.getVisibility() == View.GONE) {
                    cameraView.setVisibility(View.VISIBLE);
                    mImageView.setVisibility(View.GONE);
                    return;
                }
                System.gc();
                cameraView.takePicture();
            }
        });

        mIvSetting.setOnClickListener(this);


    }

    private void intData() {


        Bundle bundleExtra = getIntent().getBundleExtra(Constant.BUNDLE_PARMS);

        mId = bundleExtra.getInt(Constant.ID);


        Menu menu = mNavigationView.getMenu();
        menu.findItem(R.id.support).setOnMenuItemClickListener(this);
        menu.findItem(R.id.choose_version).setOnMenuItemClickListener(this);
        menu.findItem(R.id.create_version).setOnMenuItemClickListener(this);
        menu.findItem(R.id.about_version).setOnMenuItemClickListener(this);
        menu.findItem(R.id.draw_rect).setOnMenuItemClickListener(this);
        initTensorFlowAndLoadModel();

        mAdapter = new RcyCheckResultAdapter(this, mList);
        mRcyDislikeInfoList.setAdapter(mAdapter);

        mPresenter = new SettingPresenter(this);
        mPresenter.getTag();
        mPresenter.getConfigureInfo(mId);


    }


    private void intView() {
        cameraView = findViewById(R.id.cameraView);
        textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());
        btnDetectObject = findViewById(R.id.btnDetectObject);
        mImageView = findViewById(R.id.iv_image);
        mNavigationView = findViewById(R.id.nav);
        mIvBack = findViewById(R.id.iv_back);
        mRcyDislikeInfoList = findViewById(R.id.rcy_dislike_info_list);
        LinearLayoutManager manager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRcyDislikeInfoList.setLayoutManager(manager);
        mTvCheckName = findViewById(R.id.tv_check_title);
        mIvSetting = findViewById(R.id.iv_setting);
        mDrawerLayout = findViewById(R.id.activity_na);
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //初始化封装类
                    detector = TensorFlowObjectDetectionAPIModel.create(
                            getAssets(), Constant.MODEL_FILE, Constant.MODEL_TEXT, 0, 0);
                } catch (final IOException e) {
                    Log.e(TAG, "run: " + e.getMessage());
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
//        OpenCVLoader.initDebug();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:

                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
//            case R.id.bt_connnect:
////                if (!UsbConnect.isConnect(this)) {
////                    UsbConnect.Connect(this);
////                }
//                UsbConnect.Connect(this);

//                break;
            case R.id.iv_setting:

                if (!mDrawerLayout.isDrawerOpen(mNavigationView)) {
                    mDrawerLayout.openDrawer(mNavigationView);
                }
                break;
        }


    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.support:
                IntentUtils.startActivity(this, SupportFuncationActivity.class);
                break;
            case R.id.choose_version:
                IntentUtils.startActivity(this, ChooseVersionActivity.class);
                break;
            case R.id.create_version:
                IntentUtils.startActivity(this, CreateVersionActivity.class);
                break;
            case R.id.about_version:
                ToastUtils.showTextToast("wuwu");
                break;
            case R.id.draw_rect:
                IntentUtils.startActivity(this, DrawRectActivity.class);
                break;
        }


        return false;
    }

    @Override
    public void getClassifyDataSuccess(ResponseBody responseBody) {
        try {
            String string = responseBody.string();
            JSONObject jsonObject = new JSONObject(string);
            JSONArray category_index = jsonObject.getJSONArray("category_index");
            mClassifyList.clear();
            for (int i = 0; i < category_index.length(); i++) {
                mClassifyList.add(category_index.getJSONObject(i).getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkObjectSuccess(ResponseBody responseBody) {
        try {


            //把标记重置
            flag = 0;
            String string = responseBody.string();
            JSONObject json = new JSONObject(string);

            for (int i = 0; i < mLocationList.size(); i++) {
                int left = mLocationList.get(i).getLeft();
                int right = mLocationList.get(i).getRight();
                int top = mLocationList.get(i).getTop();
                int bottom = mLocationList.get(i).getBottom();

                JSONArray datas = json.getJSONArray("datas");

                List<Classifier.Recognition> location = LocationUtils.getLocation(datas.getJSONObject(i), Math.abs(left - right), Math.abs(top - bottom));

                Rect rect = new Rect();  //目标零件的正确位置
                rect.left = mLocationList.get(i).getLeftDistance();
                rect.top = mLocationList.get(i).getTopDistance();
                rect.right = rect.left + mLocationList.get(i).getWidth();
                rect.bottom = rect.top + mLocationList.get(i).getHeight();


                Bitmap bitmap = mShortBitmapList.get(i);

                Mat mat = new Mat();
                Utils.bitmapToMat(bitmap, mat);
                Imgproc.rectangle(mat, new Point(rect.left, rect.top), new Point(rect.right, rect.bottom), new Scalar(255, 180, 0), 2);

                Utils.matToBitmap(mat, bitmap);

                Log.e(TAG, "onResponse: ");
                String name = mArryList.get(i).getName();

                for (int j = 0; j < location.size(); j++) {
                    RectF rectF = location.get(j).getLocation();  //检测零件返回的位置

                    String id = location.get(j).getTitle();

                    String classifyName = mClassifyList.get(Integer.parseInt(id) - 1);

                    if (name.equals(classifyName)) {  //判断分类
                        if (location.get(j).getConfidence() > 0.5) {    //相识度大于一半才进行判断，其余的pass

                            Imgproc.rectangle(mat, new Point(rectF.left, rectF.top), new Point(rectF.right, rectF.bottom), new Scalar(255, 0, 0), 2);

                            Utils.matToBitmap(mat, bitmap);

                            if (Math.abs(rect.left - rectF.left) > 40) {
                                continue;
                            }
                            if (Math.abs(rect.right - rectF.right) > 40) {
                                continue;
                            }
                            if (Math.abs(rect.top - rectF.top) > 40) {
                                continue;
                            }
                            if (Math.abs(rect.bottom - rectF.bottom) > 40) {
                                continue;
                            }

                            mList.get(i).setIsSuccess("ok");
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    if (j == location.size() - 1) {
                        if (TextUtils.isEmpty(mList.get(i).getIsSuccess())) {
                            flag = 1;
                            mList.get(i).setIsSuccess("ng");
                            mList.get(i).setError(UiUtils.getString(R.string.location_deviation));
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }

            mPresenter.commitCheckResult();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getModelSuccess(ModelBean modelBean) {

    }

    @Override
    public void createConfigureSuccess(BaseBean baseBean) {

    }

    @Override
    public RequestBody getParms() {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        File filesDir = getFilesDir();
        File file = saveBitmapFile(mCorrectBitmap, filesDir.getAbsolutePath() + "text.jpg");
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("multipart/form-data"), file);
        builder.addFormDataPart("image", file.getName(), requestBody);
        if (flag == 0) {
            builder.addFormDataPart("defect", "位置OK");
        } else {
            builder.addFormDataPart("defect", "位置偏移");
        }
        builder.addFormDataPart("config_id", mId + "");
        builder.addFormDataPart("result", flag + "");

        return builder.build();
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

        if (bean.getResult() == 0) {
            ConfigureInfoBean.DataBean data = bean.getData();

            mTvCheckName.setText(data.getName());

            String location = data.getData();
            Gson gson = new Gson();
            LocationBean locationBean = gson.fromJson(location, LocationBean.class);

            List<LocationBean.DeviationBeanX> deviationList = locationBean.getDeviation();
            List<LocationBean.LocationBeanX> locationBeanXList = locationBean.getLocation();

            for (int i = 0; i < locationBeanXList.size(); i++) {
                RectBean rectBean = new RectBean();
                rectBean.setLeftDistance(locationBeanXList.get(i).getLeft_distance());
                rectBean.setTopDistance(locationBeanXList.get(i).getTop_distance());
                rectBean.setWidth(locationBeanXList.get(i).getWidth());
                rectBean.setHeight(locationBeanXList.get(i).getHeight());
                rectBean.setTop(deviationList.get(i).getLocation().getTop());
                rectBean.setRight(deviationList.get(i).getLocation().getRight());
                rectBean.setBottom(deviationList.get(i).getLocation().getBottom());
                rectBean.setLeft(deviationList.get(i).getLocation().getLeft());
                mLocationList.add(rectBean);
            }


            for (int i = 0; i < deviationList.size(); i++) {
                NewVersionBean newVersionBean = new NewVersionBean();
                LocationBean.DeviationBeanX.DeviationBean deviation = deviationList.get(i).getDeviation();

                NewVersionBean.Deviation beanDeviation = new NewVersionBean.Deviation();

                NewVersionBean.Deviation.Left leftBottom = new NewVersionBean.Deviation.Left();
                leftBottom.setNegative(deviation.getLeft().getNegative());
                leftBottom.setPuls(deviation.getLeft().getPuls());

                beanDeviation.setLeft(leftBottom);

                NewVersionBean.Deviation.Left topBottom = new NewVersionBean.Deviation.Left();
                topBottom.setNegative(deviation.getTop().getNegative());
                topBottom.setPuls(deviation.getTop().getPuls());

                beanDeviation.setTop(topBottom);

                NewVersionBean.Deviation.Left rightBottom = new NewVersionBean.Deviation.Left();
                rightBottom.setNegative(deviation.getRight().getNegative());
                rightBottom.setPuls(deviation.getRight().getPuls());

                beanDeviation.setRight(rightBottom);

                NewVersionBean.Deviation.Left bottom = new NewVersionBean.Deviation.Left();
                bottom.setNegative(deviation.getBottom().getNegative());
                bottom.setPuls(deviation.getBottom().getPuls());

                beanDeviation.setBottom(bottom);

                newVersionBean.setDeviation(beanDeviation);

                newVersionBean.setName(deviationList.get(i).getName());
                newVersionBean.setStatus(deviationList.get(i).isStatus());
                NewVersionBean.Location beanLocation = new NewVersionBean.Location();

                beanLocation.setBottom(deviationList.get(i).getLocation().getBottom());
                beanLocation.setTop(deviationList.get(i).getLocation().getTop());
                beanLocation.setLeft(deviationList.get(i).getLocation().getLeft());
                beanLocation.setRight(deviationList.get(i).getLocation().getRight());

                newVersionBean.setLocation(beanLocation);

                mArryList.add(newVersionBean);
            }

            mAdapter.notifyDataSetChanged();

        }

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