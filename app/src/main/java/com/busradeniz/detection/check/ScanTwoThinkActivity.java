package com.busradeniz.detection.check;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.busradeniz.detection.bean.NewVersionBean;
import com.busradeniz.detection.bean.SupportBean;
import com.busradeniz.detection.check.adapter.RcyCheckResultAdapter;
import com.busradeniz.detection.check.bean.CheckResultBean;
import com.busradeniz.detection.check.bean.RectBean;
import com.busradeniz.detection.env.Logger;
import com.busradeniz.detection.greendaodemo.db.SupportBeanDao;
import com.busradeniz.detection.http.NewsService;
import com.busradeniz.detection.http.RetrofitNetwork;
import com.busradeniz.detection.setting.ChooseVersionActivity;
import com.busradeniz.detection.setting.CreateVersionActivity;
import com.busradeniz.detection.setting.DrawRectActivity;
import com.busradeniz.detection.setting.SupportFuncationActivity;
import com.busradeniz.detection.tensorflow.Classifier;
import com.busradeniz.detection.tensorflow.TensorFlowObjectDetectionAPIModel;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.CorrectImageUtils;
import com.busradeniz.detection.utils.IntentUtils;
import com.busradeniz.detection.utils.LocationUtils;
import com.busradeniz.detection.utils.SpUtils;
import com.busradeniz.detection.utils.UiUtils;
import com.google.android.cameraview.CameraView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.BufferedOutputStream;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 */

public class ScanTwoThinkActivity extends Activity implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

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
    private SupportBeanDao mSupportBeanDao;
    private List<RectBean> mLocationList;
    private TextView mMtvProjectName;
    private ImageView mIvBack;
    private SupportBean mSupportBean;
    private List<NewVersionBean> mArryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_think_scan);

        intView();

        intData();

        initEvent();

//        if (!UsbConnect.isConnect(this)) {
//            UsbConnect.Connect(this);
//        }
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
                            long startTime = System.currentTimeMillis();
                            try {
                                File fileDir = new File("/sdcard/srcImage/" + startTime + ".png");
                                fileDir.createNewFile();

                                FileOutputStream out = new FileOutputStream(fileDir);
                                mBitmaps.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                out.flush();
                                out.close();
                            } catch (Exception e) {

                            }
                            mImageView.setVisibility(View.VISIBLE);
                            cameraView.setVisibility(View.GONE);
                            mImageView.setImageBitmap(bitmap);
                            Mat recognize = detector.recognize(mBitmaps, TF_OD_API_INPUT_SIZE, TF_Wdith_API_INPUT_SIZE);

                            Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                            Utils.matToBitmap(recognize, bitmap2);

                            Bitmap correctBitmap = CorrectImageUtils.correctImage(recognize, bitmap, 1);
                            mImageView.setImageBitmap(correctBitmap);

                            List<Bitmap> shortBitmapList = new ArrayList<>();
                            for (int i = 0; i < mLocationList.size(); i++) {
                                RectBean rectBean = mLocationList.get(i);
                                int width = Math.abs(rectBean.getLeft() - rectBean.getRight());
                                int height = Math.abs(rectBean.getTop() - rectBean.getBottom());
                                Bitmap cropBitmap = Bitmap.createBitmap(correctBitmap, rectBean.getLeft(), rectBean.getTop(), width, height);
                                shortBitmapList.add(cropBitmap);
                                Log.e(TAG, "run: ");
                            }


                            Log.e(TAG, "run: ");
//
                            recognizeImage(shortBitmapList);


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
                mList.clear();
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

        Menu menu = mNavigationView.getMenu();
        menu.findItem(R.id.support).setOnMenuItemClickListener(this);
        menu.findItem(R.id.choose_version).setOnMenuItemClickListener(this);
        menu.findItem(R.id.create_version).setOnMenuItemClickListener(this);
        menu.findItem(R.id.about_version).setOnMenuItemClickListener(this);
        menu.findItem(R.id.draw_rect).setOnMenuItemClickListener(this);
        initTensorFlowAndLoadModel();

        mAdapter = new RcyCheckResultAdapter(this, mList);
        mRcyDislikeInfoList.setAdapter(mAdapter);

        try {
            mSupportBeanDao = BaseApplication.getApplicatio().getDaoSession().getSupportBeanDao();
            mSupportBean = queryData(true);


            String location = mSupportBean.getLocation();
            mTvCheckName.setText(mSupportBean.getProjectName());
            mMtvProjectName.setText(mSupportBean.getProjectName());
            Gson gson = new Gson();


            String data = mSupportBean.getData();   //每个零件正确的位置
            mArryList = gson.fromJson(data, new TypeToken<List<NewVersionBean>>() { //解析每个零件正确的位置
            }.getType());

            mLocationList = gson.fromJson(location, new TypeToken<List<RectBean>>() { //解析每个零件允许偏移的位置
            }.getType());

        } catch (Exception e) {

        }

    }


    public SupportBean queryData(boolean status) {
        return mSupportBeanDao.queryBuilder().where(SupportBeanDao.Properties.SelectedStatus.eq(status)).build().unique();

    }


    private void intView() {
        cameraView = findViewById(R.id.cameraView);
        textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());
        btnDetectObject = findViewById(R.id.btnDetectObject);
        mImageView = findViewById(R.id.iv_image);
        mNavigationView = findViewById(R.id.nav);
        mMtvProjectName = findViewById(R.id.tv_model);
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


    public void recognizeImage(List<Bitmap> bitmaps) {


        File filesDir = getApplicationContext().getFilesDir();
        MultipartBody.Builder builder = new MultipartBody.Builder();

        for (int i = 0; i < bitmaps.size(); i++) {

            File file = saveBitmapFile(bitmaps.get(i), filesDir.getAbsolutePath() + "text.jpg");
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("images", file.getName(), requestBody);
        }
        builder.setType(MultipartBody.FORM);

        NewsService anInterface = RetrofitNetwork.getObserableIntence();
        Call<ResponseBody> news = null;

        String modelUrl = (String) SpUtils.getParam(this, Constant.MODEL_URL, "");
        if (!TextUtils.isEmpty(modelUrl)) {
            news = anInterface.originalInterface(modelUrl, builder.build());
        }

        news.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String string = response.body().string();
                    JSONObject json = new JSONObject(string);
                    List<Classifier.Recognition> location = LocationUtils.getLocation(json, 240, 480);

                    Log.e(TAG, "onResponse: 数据大小 " + location.size());
                    for (int i = 0; i < location.size(); i++) {
                        RectF rectF = location.get(i).getLocation();      //检测出来零件的位置
                        RectBean rectBean = mLocationList.get(i);   //正确的零件位置

                         if (Math.abs(rectF.left - rectBean.getLeft()) > 50) {
                            Log.e(TAG, "onResponse: 左边偏移" + Math.abs(rectF.left - rectBean.getLeft()));
                        }
                        if (Math.abs(rectF.right - rectBean.getRight()) > 50) {
                            Log.e(TAG, "onResponse: 右边偏移 " + Math.abs(rectF.right - rectBean.getRight()));
                        }
                        if (Math.abs(rectF.top - rectBean.getTop()) > 50) {
                            Log.e(TAG, "onResponse: 上边偏移" + Math.abs(rectF.top - rectBean.getTop()));
                        }
                        if (Math.abs(rectF.bottom - rectBean.getBottom()) > 50) {
                            Log.e(TAG, "onResponse: 下边偏移" + Math.abs(rectF.bottom - rectBean.getBottom()));

                        }

                        if (Math.abs(rectF.left - rectBean.getLeft()) > 50) {
                            Log.e(TAG, "onResponse: 左边偏移" + Math.abs(rectF.left - rectBean.getLeft()));
                        }
                        if (Math.abs(rectF.right - rectBean.getRight()) > 50) {
                            Log.e(TAG, "onResponse: 右边偏移 " + Math.abs(rectF.right - rectBean.getRight()));
                        }
                        if (Math.abs(rectF.top - rectBean.getTop()) > 50) {
                            Log.e(TAG, "onResponse: 上边偏移" + Math.abs(rectF.top - rectBean.getTop()));
                        }
                        if (Math.abs(rectF.bottom - rectBean.getBottom()) > 50) {
                            Log.e(TAG, "onResponse: 下边偏移" + Math.abs(rectF.bottom - rectBean.getBottom()));
                        }

//                        NewVersionBean.Deviation deviation = mArryList.get(i).getDeviation(); //每个零件允许偏移的位置
                    }


                    cameraView.setVisibility(View.GONE);
                    mImageView.setVisibility(View.VISIBLE);
                    mImageView.setImageBitmap(bitmap);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("vivi", "onFailure: " + t.getMessage());
            }
        });


    }


    public static File saveBitmapFile(Bitmap bitmap, String filepath) {
        File file = new File(filepath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;

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
}