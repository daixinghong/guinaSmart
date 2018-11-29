package com.busradeniz.detection.check;

import android.app.AlertDialog;
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
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.busradeniz.detection.check.adapter.RcyCheckResultAdapter;
import com.busradeniz.detection.check.bean.CheckResultBean;
import com.busradeniz.detection.check.bean.ModelBean;
import com.busradeniz.detection.check.bean.RecordListBean;
import com.busradeniz.detection.check.bean.RectBean;
import com.busradeniz.detection.env.Logger;
import com.busradeniz.detection.message.IMessage;
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
import com.busradeniz.detection.utils.DialogUtils;
import com.busradeniz.detection.utils.IntentUtils;
import com.busradeniz.detection.utils.LocationUtils;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private List<CheckResultBean> mList = new ArrayList<>();
    private NavigationView mNavigationView;
    private Bitmap mBitmaps;
    private int  mStatus;
    private Bitmap mResizeBitmap;
    private RecyclerView mRcyDislikeInfoList;
    private RcyCheckResultAdapter mAdapter;
    private TextView mTvCheckName;
    private ImageView mIvSetting;
    private DrawerLayout mDrawerLayout;
    private List<RectBean> mLocationList = new ArrayList<>();
    private ImageView mIvBack;
    private List<NewVersionBean> mArryList = new ArrayList<>();
    private List<Bitmap> mShortBitmapList;
    private SettingPresenter mPresenter;
    private List<String> mClassifyList = new ArrayList<>();
    private Bitmap mCorrectBitmap;
    private int mId;
    private int flag;
    private TextView mTvCheckResult;
    private String[] mComandArray;
    private boolean mCheckStatus;
    private String mResult;
    private TextView mTvModel;
    private boolean mIsStart;
    private String mIsStartCamera = null;
    private int mIntoMaterialStatus;          // 1 进料口有产品  0  进料口无产品
    private int mFirstWaitStatus;             // 1 第一阶段过渡区域有产品，0，无产品
    private int mSleepStatus;                 // 1 进料口产品睡眠中
    private ThreadPoolExecutor mThreadPoolExecutor;  //线程池
    private int mArriveCutPhotoPlace;         // 1  拍照位置有产品  0 拍照位置无产品
    private int mArriveWaitResultPlace;       // 1  分拣位置有产品  0 分拣位置无产品
    private int mTwoWaitStatus;               // 1 第二阶段过渡区域有产品 0 无产品
    private int mIsArriveCutPhoto;
    private int mCutPhotoSleepStatus;         //拍照区域休眠中
    private int mResultSleepStatus;           //分拣区域休眠中


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

        mIvBack.setOnClickListener(this);
        cameraView.addCallback(new CameraView.Callback() {
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

                            TF_OD_API_INPUT_SIZE = bitmap.getWidth() / 4;
                            TF_Wdith_API_INPUT_SIZE = bitmap.getHeight() / 4;

                            mBitmaps = Bitmap.createScaledBitmap(bitmap, TF_OD_API_INPUT_SIZE, TF_Wdith_API_INPUT_SIZE, false);

                            Mat recognize = detector.recognize(mBitmaps, TF_OD_API_INPUT_SIZE, TF_Wdith_API_INPUT_SIZE);

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
                            mImageView.setVisibility(View.VISIBLE);
                            mImageView.setImageBitmap(mCorrectBitmap);

                            mShortBitmapList = new ArrayList<>();
                            for (int i = 0; i < mLocationList.size(); i++) {
                                RectBean rectBean = mLocationList.get(i);
                                int width = Math.abs(rectBean.getLeft() - rectBean.getRight());
                                int height = Math.abs(rectBean.getTop() - rectBean.getBottom());
                                Bitmap cropBitmap = Bitmap.createBitmap(mCorrectBitmap, rectBean.getLeft(), rectBean.getTop(), width, height);
                                mShortBitmapList.add(cropBitmap);
                            }

                            Log.e(TAG, "run: 嘎嘎，来到这里");
                            //发送请求
                            mPresenter.sendPhotoGetResult(ScanTwoThinkActivity.this, mShortBitmapList);

                        } catch (Exception e) {
//                            System.gc();
//                            cameraView.takePicture();
                            mResult = "";
                            flag = 1;
                            ToastUtils.showTextToast(UiUtils.getString(R.string.photo_check_fild));

                        } catch (Error error) {

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

                mCorrectBitmap = null;

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

        mComandArray = UiUtils.getStringArray(R.array.comand_array);


        mThreadPoolExecutor = new ThreadPoolExecutor(4,10,1,TimeUnit.SECONDS,
               new LinkedBlockingQueue<Runnable>(100));


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
        mTvCheckResult = findViewById(R.id.tv_check_result);
        mTvModel = findViewById(R.id.tv_model);
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

            Log.e(TAG, "testCutPhotoSuccess: 来到这里");
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
                String name = mLocationList.get(i).getName();

                for (int j = 0; j < location.size(); j++) {
                    RectF rectF = location.get(j).getLocation();  //检测零件返回的位置

                    String id = location.get(j).getTitle();

                    String classifyName = mClassifyList.get(Integer.parseInt(id) - 1);

                    if (name.equals(classifyName)) {  //判断分类
                        if (location.get(j).getConfidence() > 0.5) {    //相识度大于一半才进行判断，其余的pass

//                            Imgproc.rectangle(mat, new Point(rectF.left, rectF.top), new Point(rectF.right, rectF.bottom), new Scalar(255, 0, 0), 2);
//
//                            Utils.matToBitmap(mat, bitmap);
//
//                            if (Math.abs(rect.left - rectF.left) > 40) {
//                                continue;
//                            }
//                            if (Math.abs(rect.right - rectF.right) > 40) {
//                                continue;
//                            }
//                            if (Math.abs(rect.top - rectF.top) > 40) {
//                                continue;
//                            }
//                            if (Math.abs(rect.bottom - rectF.bottom) > 40) {
//                                continue;
//                            }

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
            mResult = "";

            mPresenter.commitCheckResult();


        } catch (Exception e) {
            e.printStackTrace();
            mResult = "";
        }
    }

    @Override
    public void getRecordListSuccess(RecordListBean recordListBean) {

    }

    /**
     * 连接plc收到消息的回调
     *
     * @param message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMessage message) {

        Log.e(TAG, "onEvent: "+message.getMessage());

        if (message.getMessage().equals(mComandArray[11])) {  //进料口感应器

            if(mSleepStatus == 1)  //因为这里我读取进料口感应器我只需要读取一次，读取到了一次之后，就休眠1秒钟，之后再继续读
                 return;

            mSleepStatus = 1;        //设置为读取休眠

            Log.e(TAG, "onEvent: 到达进料口" );

            mIntoMaterialStatus = 1;

            Log.e(TAG, "run:  ss"+mIntoMaterialStatus+"  "+mFirstWaitStatus+"  "+mArriveCutPhotoPlace );

            if (mIntoMaterialStatus==1&&mFirstWaitStatus == 0 &&mArriveCutPhotoPlace==0) {  // 进料口放行条件为:进料口有产品，过渡区域无产品时可以放行

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            Log.e(TAG, "run: 开始放行" );
                            SerialPortManager.instance().sendCommand(mComandArray[10]);

                            mIntoMaterialStatus = 0; //设置进料口无产品
                            mFirstWaitStatus = 1;    //设置第一阶段等待区域有产品

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };

                mThreadPoolExecutor.execute(runnable);

//                exceptionPrecoss();      //计时10秒做相应的操作
            }
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        mSleepStatus=0;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            mThreadPoolExecutor.execute(runnable);    //休眠800毫秒

        }else if (message.getMessage().equals(mComandArray[1])) {  //拍照区域感应器

            if(mCutPhotoSleepStatus==1)
                return;

            mCutPhotoSleepStatus =1; //拍照休眠中

            Log.e(TAG, "onEvent: 到达拍照位" );

            mIsArriveCutPhoto = 1;
            mArriveCutPhotoPlace = 1;  //设置到达拍照位
            mFirstWaitStatus = 0;      //第一过渡期设置为0

            Log.e(TAG, "onEvent: "+mArriveCutPhotoPlace +"  " +mTwoWaitStatus +"  "+mArriveWaitResultPlace);
            if(mArriveCutPhotoPlace==1&&mTwoWaitStatus==0&&mArriveWaitResultPlace==0){  // 拍照区域放行条件: 拍照区域为1 第二阶段过渡期为0 分拣区域为0

                System.gc();
                cameraView.takePicture();   //调用摄像

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);

                            Log.e(TAG, "onEvent: 摄像完成");
                            SerialPortManager.instance().sendCommand(mComandArray[2]);  //发送摄像完成

                            mTwoWaitStatus =1;   //第二阶段过渡期设置为1
                            mArriveCutPhotoPlace = 0; //设置拍照区域无产品

                            Log.e(TAG, "run:  ss  "+mIntoMaterialStatus+"  "+mFirstWaitStatus+"  "+mArriveCutPhotoPlace );

                            if (mIntoMaterialStatus==1&&mFirstWaitStatus == 0&&mArriveCutPhotoPlace==0) {  // 进料口放行条件为:进料口有产品，过渡区域无产品,拍照区域无产品时可以放行

                                Thread.sleep(500);
                                Log.e(TAG, "run: 开始放行" );
                                SerialPortManager.instance().sendCommand(mComandArray[10]);

                                mIntoMaterialStatus = 0; //设置进料口无产品
                                mFirstWaitStatus = 1;    //设置第一阶段等待区域有产品

//                            exceptionPrecoss();      //计时10秒做相应的操作
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };

                mThreadPoolExecutor.execute(runnable);


            }

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        mCutPhotoSleepStatus=0;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            mThreadPoolExecutor.execute(runnable);    //休眠800毫秒

        } else if (message.getMessage().equals(mComandArray[3])) { //分拣区域感应器   分拣放行条件:分拣区域有产品,等到识别结果出来就可以放行


            if(mResultSleepStatus==1)
                return;

            mResultSleepStatus = 1;
            Log.e(TAG, "run: 到达分拣区域" );


            mArriveWaitResultPlace = 1;  //已到达分拣位置
            mTwoWaitStatus = 0;         //第二过渡期设置为0

            Runnable runnables = new Runnable() {
                @Override
                public void run() {
                    try {

                    Log.e(TAG, "run: "+mArriveWaitResultPlace +"  "+mResult+"   hahah" );

                    while (true){
                        if(mArriveWaitResultPlace==1&&mResult!=null){ //如果分拣区域有产品，识别结果不等于null

                            Log.e(TAG, "onEvent: 发送结果" );

                            if (flag == 1) {
                                SerialPortManager.instance().sendCommand(mComandArray[4]);
                            } else {
                                SerialPortManager.instance().sendCommand(mComandArray[5]);
                            }

                            mArriveWaitResultPlace = 0;   //设置分拣区域为空
                            mResult = null;               //识别结果为空

                            if(mArriveCutPhotoPlace==1&&mTwoWaitStatus==0&&mArriveWaitResultPlace==0){  // 拍照区域放行条件: 拍照区域为1 第二阶段过渡期为0 分拣区域为0

                                System.gc();
                                cameraView.takePicture();   //调用摄像

                                Thread.sleep(500);

                                Log.e(TAG, "run: 摄像完成" );
                                SerialPortManager.instance().sendCommand(mComandArray[2]);  //发送摄像完成

                                mTwoWaitStatus =1;   //第二阶段过渡期设置为1
                                mArriveCutPhotoPlace = 0; //设置拍照区域无产品

                                Log.e(TAG, "run:  ss"+mIntoMaterialStatus+"  "+mFirstWaitStatus+"  "+mArriveCutPhotoPlace );

                                if (mIntoMaterialStatus==1&&mFirstWaitStatus == 0&&mArriveCutPhotoPlace==0) {  // 进料口放行条件为:进料口有产品，过渡区域无产品时可以放行

                                    Thread.sleep(500);

                                    Log.e(TAG, "run: 开始放行" );
                                    SerialPortManager.instance().sendCommand(mComandArray[10]);

                                    mIntoMaterialStatus = 0; //设置进料口无产品
                                    mFirstWaitStatus = 1;    //设置第一阶段等待区域有产品


//                        exceptionPrecoss();      //计时10秒做相应的操作
                                }
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mImageView.setVisibility(View.GONE);
                                    cameraView.setVisibility(View.VISIBLE);
                                }
                            });
                            break;
                        }
                    }

                    }catch (Exception e){

                    }

                    mResult = null;
                }
            };

            mThreadPoolExecutor.execute(runnables);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        mResultSleepStatus=0;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            mThreadPoolExecutor.execute(runnable);    //休眠800毫秒

        }
    }

    public void exceptionPrecoss(){
            new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //放行后10秒钟执行该程序,如果没有达到拍照位则报警
                if(mArriveCutPhotoPlace==1){
                    //到达了拍照位,正常,将状态复位
                    mArriveCutPhotoPlace = 0;
                }else{
                    //没有到达拍照位,不正常,设备报警，这时候,放行气缸不再放行，知道处理完异常之后再继续放行
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View view = DialogUtils.inflateView(ScanTwoThinkActivity.this, R.layout.dialog_device_exception_view);
                            TextView tvExceptionInfo =   view.findViewById(R.id.tv_exception_info);
                            TextView tvNextStep =  view.findViewById(R.id.tv_next_step);
                            AlertDialog dialog = DialogUtils.createDialog(view);
                            //如果这里读取到移位气缸1限位

                            tvNextStep.setOnClickListener(new View.OnClickListener() {  //点击了已处理
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    mFirstWaitStatus = 0;

                                    if(mIntoMaterialStatus==1&&mFirstWaitStatus==0&&mArriveCutPhotoPlace==0){
                                        SerialPortManager.instance().sendCommand(mComandArray[10]);
                                        mIntoMaterialStatus = 0;
                                        mFirstWaitStatus = 1;
                                        exceptionPrecoss();
                                    }else{
                                        mFirstWaitStatus = 0;
                                    }
                                    mSleepStatus = 0;
                                }
                            });
                            tvExceptionInfo.setText("第一次阶段产品跑位");

                            Window dialogWindow = dialog.getWindow();
                            WindowManager m = getWindowManager();
                            Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
                            p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.65
                            dialogWindow.setAttributes(p);
                            dialog.show();
                        }
                    });

                }

            }
        },10000);
    }

}