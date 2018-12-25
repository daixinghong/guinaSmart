package com.busradeniz.detection.check.view.activity;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.busradeniz.detection.check.model.CheckResultBean;
import com.busradeniz.detection.check.model.ModelBean;
import com.busradeniz.detection.check.model.RecordListBean;
import com.busradeniz.detection.check.model.RectBean;
import com.busradeniz.detection.check.view.adapter.RcyCheckResultAdapter;
import com.busradeniz.detection.message.IMessage;
import com.busradeniz.detection.setting.presenter.SettingInterface;
import com.busradeniz.detection.setting.presenter.SettingPresenter;
import com.busradeniz.detection.setting.view.activity.ChooseVersionActivity;
import com.busradeniz.detection.setting.view.activity.CreateVersionActivity;
import com.busradeniz.detection.setting.view.activity.DrawRectActivity;
import com.busradeniz.detection.setting.view.activity.SupportFuncationActivity;
import com.busradeniz.detection.tensorflow.Classifier;
import com.busradeniz.detection.tensorflow.TensorFlowObjectDetectionAPIModel;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.CorrectImageUtils;
import com.busradeniz.detection.utils.IntentUtils;
import com.busradeniz.detection.utils.LocationUtils;
import com.busradeniz.detection.utils.PlcCommandUtils;
import com.busradeniz.detection.utils.SerialPortManager;
import com.busradeniz.detection.utils.UiUtils;
import com.google.android.cameraview.CameraView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


/**
 */

public class WorkingActivity extends BaseActivity implements View.OnClickListener, MenuItem.OnMenuItemClickListener, SettingInterface {

    @BindView(R.id.cameraView)
    CameraView mCameraView;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_check_title)
    TextView mTvCheckTitle;
    @BindView(R.id.iv_setting)
    ImageView mIvSetting;
    @BindView(R.id.iv_image)
    ImageView mIvImage;
    @BindView(R.id.tv_model)
    TextView mTvModel;
    @BindView(R.id.tv_type)
    TextView mTvType;
    @BindView(R.id.tv_imei1)
    TextView mTvImei1;
    @BindView(R.id.tv_imei2)
    TextView mTvImei2;
    @BindView(R.id.tv_sn)
    TextView mTvSn;
    @BindView(R.id.rcy_dislike_info_list)
    RecyclerView mRcyDislikeInfoList;
    @BindView(R.id.tv_check_result)
    TextView mTvCheckResult;
    @BindView(R.id.imageViewResult)
    ImageView mImageViewResult;
    @BindView(R.id.textViewResult)
    TextView mTextViewResult;
    @BindView(R.id.nav)
    NavigationView mNav;
    @BindView(R.id.activity_na)
    DrawerLayout mActivityNa;
    @BindView(R.id.tv_run)
    TextView mTvRun;
    @BindView(R.id.rl_run_status)
    RelativeLayout mRlRunStatus;
    @BindView(R.id.tv_stop)
    TextView mTvStop;
    @BindView(R.id.rl_stop_status)
    RelativeLayout mRlStopStatus;
    private int mWidth;
    private int mHeight;
    private Classifier detector;
    private List<CheckResultBean> mList = new ArrayList<>();
    private Bitmap mBitmaps;
    private int mStatus;
    private Bitmap mResizeBitmap;
    private RcyCheckResultAdapter mAdapter;
    private TextView mTvCheckName;
    private DrawerLayout mDrawerLayout;
    private List<RectBean> mLocationList = new ArrayList<>();
    private List<NewVersionBean> mArryList = new ArrayList<>();
    private List<Bitmap> mShortBitmapList;
    private SettingPresenter mPresenter;
    private List<String> mClassifyList = new ArrayList<>();
    private Bitmap mCorrectBitmap;
    private int mId;
    private int flag;
    private ThreadPoolExecutor mThreadPoolExecutor;
    private boolean mRunStatus;
    private boolean mStopStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        initEvent();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_two_think_scan;
    }


    private void initEvent() {

        mCameraView.addCallback(new CameraView.Callback() {
            @Override
            public void onPictureTaken(CameraView cameraView, byte[] data) {
                super.onPictureTaken(cameraView, data);
                for (int i = 0; i < mList.size(); i++) {
                    mList.get(i).setError("");
                    mList.get(i).setIsSuccess("");
                }
                mAdapter.notifyDataSetChanged();

                mTvCheckResult.setText(UiUtils.getString(R.string.checking));
                mTvCheckResult.setTextColor(UiUtils.getColor(R.color.mo_line));

                BaseApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream os = null;
                        try {

                            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                    "picture.jpg");
                            os = new FileOutputStream(file);
                            os.write(data);
                            os.close();

                            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

                            mWidth = bitmap.getWidth() / 4;
                            mHeight = bitmap.getHeight() / 4;

                            mBitmaps = Bitmap.createScaledBitmap(bitmap, mWidth, mHeight, false);

                            Mat recognize = detector.recognize(mBitmaps, mWidth, mHeight);

                            Mat det = new Mat();
                            recognize.convertTo(recognize, CvType.CV_8UC3);
                            Imgproc.resize(recognize, det, new Size(recognize.cols(), recognize.rows()));

                            Bitmap sBitmap2 = Bitmap.createBitmap(recognize.cols(), recognize.rows(), Bitmap.Config.ARGB_4444);
                            Utils.matToBitmap(det, sBitmap2);

                            long startTime = System.currentTimeMillis();

                            File fileDirs = new File("/sdcard/srcImage/" + startTime + ".png");

                            fileDirs.createNewFile();

                            FileOutputStream outs = new FileOutputStream(fileDirs);
                            sBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, outs);
                            outs.flush();
                            outs.close();

                            mCorrectBitmap = CorrectImageUtils.correctImage(recognize, bitmap, 1);

//                            File fileDirss = new File("/sdcard/image/" + startTime + ".png");
//
//                            fileDirss.createNewFile();
//
//                            FileOutputStream outss = new FileOutputStream(fileDirss);
//                            mCorrectBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outss);
//                            outss.flush();
//                            outss.close();


                            cameraView.setVisibility(View.GONE);
                            mIvImage.setVisibility(View.VISIBLE);
                            mIvImage.setImageBitmap(mCorrectBitmap);

                            mShortBitmapList = new ArrayList<>();
                            for (int i = 0; i < mLocationList.size(); i++) {
                                RectBean rectBean = mLocationList.get(i);
                                int width = Math.abs(rectBean.getLeft() - rectBean.getRight());
                                int height = Math.abs(rectBean.getTop() - rectBean.getBottom());
                                Bitmap cropBitmap = Bitmap.createBitmap(mCorrectBitmap, rectBean.getLeft(), rectBean.getTop(), width, height);
                                mShortBitmapList.add(cropBitmap);
                            }

                            //发送请求
                            mPresenter.sendPhotoGetResult(WorkingActivity.this, mShortBitmapList);

                        } catch (Exception e) {
                            flag = 1;
                            ToastUtils.showTextToast(UiUtils.getString(R.string.photo_check_fild));

                        } catch (Error error) {

                        } finally {
                            if (os != null) {
                                try {
                                    os.close();
                                } catch (IOException e) {
                                }
                            }
                        }

                    }
                });

            }
        });

    }


    private void init() {
        LinearLayoutManager manager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRcyDislikeInfoList.setLayoutManager(manager);

        Bundle bundleExtra = getIntent().getBundleExtra(Constant.BUNDLE_PARMS);
        mId = bundleExtra.getInt(Constant.ID);

        mThreadPoolExecutor = new ThreadPoolExecutor(4, 10, 1, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(100));

        Menu menu = mNav.getMenu();
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


        SerialPortManager.instance().sendCommand(PlcCommandUtils.plcCommand2String("m0", true, 1));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    SerialPortManager.instance().sendCommand(PlcCommandUtils.plcCommand2String("m0",true,1));
                } catch (Exception e) {

                }
            }
        };
        mThreadPoolExecutor.execute(runnable);


    }

    private void initTensorFlowAndLoadModel() {
        mThreadPoolExecutor.execute(new Runnable() {
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
        mCameraView.start();
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
        mCameraView.stop();
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
            mTvModel.setText(data.getName());
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

                rectBean.setTop(locationBeanXList.get(i).getCheckLocation().getTop());
                rectBean.setRight(locationBeanXList.get(i).getCheckLocation().getRight());
                rectBean.setBottom(locationBeanXList.get(i).getCheckLocation().getBottom());
                rectBean.setLeft(locationBeanXList.get(i).getCheckLocation().getLeft());
                rectBean.setName(locationBeanXList.get(i).getName());
                mLocationList.add(rectBean);

                CheckResultBean checkResultBean = new CheckResultBean();
                checkResultBean.setName(locationBeanXList.get(i).getName());
                mList.add(checkResultBean);

            }

            mAdapter.notifyDataSetChanged();

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
        try {
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
                String name = mLocationList.get(i).getName();

                for (int j = 0; j < location.size(); j++) {
                    RectF rectF = location.get(j).getLocation();  //检测零件返回的位置

                    String id = location.get(j).getTitle();

                    String classifyName = mClassifyList.get(Integer.parseInt(id) - 1);

                    if (name.equals(classifyName)) {  //判断分类
                        if (location.get(j).getConfidence() > 0.5) {    //相识度大于一半才进行判断，其余的pass
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

            if (flag == 0) {
                mTvCheckResult.setTextColor(UiUtils.getColor(R.color.color_50FF00));
                mTvCheckResult.setText("success");
            } else {
                mTvCheckResult.setTextColor(UiUtils.getColor(R.color.red));
                mTvCheckResult.setText("fail");
            }
            mPresenter.commitCheckResult();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getRecordListSuccess(RecordListBean recordListBean) {

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMessage message) {


    }


    @OnClick({R.id.iv_setting, R.id.iv_back, R.id.rl_run_status, R.id.rl_stop_status})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_setting:
                if (!mDrawerLayout.isDrawerOpen(mNav)) {
                    mDrawerLayout.openDrawer(mNav);
                }
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_run_status:
                if (!mRunStatus && mStopStatus) {
                    mTvRun.setText(UiUtils.getString(R.string.runing));
                    mTvStop.setText(UiUtils.getString(R.string.stop));
                    SerialPortManager.instance().sendCommand(PlcCommandUtils.plcCommand2String("m0", true, 1));
                }
                mRunStatus = true;
                mStopStatus = false;
                break;
            case R.id.rl_stop_status:
                if (mRunStatus && !mStopStatus) {
                    mTvRun.setText(UiUtils.getString(R.string.run));
                    mTvStop.setText(UiUtils.getString(R.string.stopping));
                    SerialPortManager.instance().sendCommand(PlcCommandUtils.plcCommand2String("m1", true, 1));
                }
                mRunStatus = false;
                mStopStatus = true;
                break;
        }
    }
}