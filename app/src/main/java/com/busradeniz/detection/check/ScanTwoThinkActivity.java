package com.busradeniz.detection.check;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.busradeniz.detection.BaseApplication;
import com.busradeniz.detection.tensorflow.Classifier;
import com.busradeniz.detection.http.NewsService;
import com.busradeniz.detection.R;
import com.busradeniz.detection.tensorflow.TensorFlowObjectDetectionAPIModel;
import com.busradeniz.detection.ToastUtils;
import com.busradeniz.detection.check.adapter.RcyCheckResultAdapter;
import com.busradeniz.detection.check.bean.CheckResultBean;
import com.busradeniz.detection.env.Logger;
import com.busradeniz.detection.http.RetrofitNetwork;
import com.busradeniz.detection.setting.ChooseVersionActivity;
import com.busradeniz.detection.setting.CreateVersionActivity;
import com.busradeniz.detection.setting.SupportFuncationActivity;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.CorrectImageUtils;
import com.busradeniz.detection.utils.IntentUtils;
import com.google.android.cameraview.CameraView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

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

                            Mat recognize = detector.recognize(mBitmaps, TF_OD_API_INPUT_SIZE, TF_Wdith_API_INPUT_SIZE);

                            Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                            Utils.matToBitmap(recognize, bitmap2);
//                            mImageView.setImageBitmap(bitmap2);

                            Bitmap correctBitmap = CorrectImageUtils.correctImage(recognize, bitmap, 1);
                            Mat resizeMat = new Mat();

                            Utils.bitmapToMat(correctBitmap, resizeMat);

                            Imgproc.resize(resizeMat, resizeMat, new Size(correctBitmap.getWidth() * 2, correctBitmap.getHeight() * 2));

                            mResizeBitmap = Bitmap.createBitmap(correctBitmap.getWidth() * 2, correctBitmap.getHeight() * 2, Bitmap.Config.ARGB_8888);

                            Utils.matToBitmap(resizeMat, mResizeBitmap);

                            recognizeImage(mResizeBitmap);


                        } catch (Exception e) {

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

    }

    private void intData() {

        Menu menu = mNavigationView.getMenu();
        menu.findItem(R.id.support).setOnMenuItemClickListener(this);
        menu.findItem(R.id.choose_version).setOnMenuItemClickListener(this);
        menu.findItem(R.id.create_version).setOnMenuItemClickListener(this);
        menu.findItem(R.id.about_version).setOnMenuItemClickListener(this);
        initTensorFlowAndLoadModel();

        mAdapter = new RcyCheckResultAdapter(this, mList);
        mRcyDislikeInfoList.setAdapter(mAdapter);

    }

    private void intView() {
        cameraView = findViewById(R.id.cameraView);
        textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());
        btnDetectObject = findViewById(R.id.btnDetectObject);
        mImageView = findViewById(R.id.iv_image);
        mNavigationView = findViewById(R.id.nav);
        mRcyDislikeInfoList = findViewById(R.id.rcy_dislike_info_list);
        mRcyDislikeInfoList.setLayoutManager(new LinearLayoutManager(this));

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


    public void recognizeImage(final Bitmap bitmaps) {


        File filesDir = getApplicationContext().getFilesDir();
        File file = saveBitmapFile(bitmaps, filesDir.getAbsolutePath() + "text.jpg");

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("multipart/form-data"), file);
        builder.addFormDataPart("images", file.getName(), requestBody);
        NewsService anInterface = RetrofitNetwork.getObserableIntence();
        Call<ResponseBody> news = null;

        if (mStatus) {
            news = anInterface.test1(builder.build());
        } else {
            news = anInterface.test5(builder.build());
        }

        news.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String string = response.body().string();
                    try {
                        JSONObject json = new JSONObject(string);
                        JSONArray datas = json.getJSONArray("datas");
                        JSONObject jsonObject = datas.getJSONObject(0);
                        JSONArray box = jsonObject.getJSONObject("data").getJSONArray("boxes");
                        JSONArray classes = jsonObject.getJSONObject("data").getJSONArray("classes");
                        JSONArray scores = jsonObject.getJSONObject("data").getJSONArray("scores");
//                        JSONArray masks = jsonObject.getJSONObject("data").getJSONArray("masks");
//                        float array[][] = new float[masks.getJSONArray(0).length()][masks.getJSONArray(0).getJSONArray(0).length()];
//
//                        for (int i = 0; i < masks.length(); i++) {
//                            JSONArray jsonArray = masks.getJSONArray(i);
//                            for (int j = 0; j < jsonArray.length(); j++) {
//                                JSONArray list = jsonArray.getJSONArray(j);
//                                for (int a = 0; a < list.length(); a++) {
//                                    int anInt = list.getInt(a);
//                                    array[j][a] = anInt;
//                                }
//
//                            }
//                        }
//

//                        List<float[]> listArray = new ArrayList<>();
//                        for (int i = 0; i < masks.length(); i++) {
//                            float firstArr[] = new float[masks.getJSONArray(i).length() * masks.getJSONArray(i).getJSONArray(i).length()];
//                            int index = 0;
//                            JSONArray jsonArray = masks.getJSONArray(i);
//                            for (int j = 0; j < jsonArray.length(); j++) {
//                                JSONArray list = jsonArray.getJSONArray(j);
//                                for (int a = 0; a < list.length(); a++) {
//                                    firstArr[index] = list.getInt(a) * 255;
//                                    index++;
//                                }
//                            }
//                            listArray.add(firstArr);
//                        }
//                        Bitmap bitmap = BitmapCreateFactroy.createBitmap(listArray.get(0), 33, 33);
//                        mImageView.setImageBitmap(bitmap);
//                        mImageView.setVisibility(View.VISIBLE);

                        for (int i = 0; i < classes.length(); i++) {
                            ((TensorFlowObjectDetectionAPIModel) detector).outputClasses[i] = (float) classes.getDouble(i);
                            ((TensorFlowObjectDetectionAPIModel) detector).outputScores[i] = (float) scores.getDouble(i);
                        }
                        for (int i = 0; i < box.length(); i++) {
                            ((TensorFlowObjectDetectionAPIModel) detector).outputLocations[i] = (float) box.getDouble(i);
                        }

                        List<Classifier.Recognition> data = detector.getData(bitmaps.getWidth(), bitmaps.getHeight());
                        Bitmap bitmap = Bitmap.createBitmap(bitmaps.getWidth(), bitmaps.getHeight(), Bitmap.Config.ARGB_8888);
                        Mat mat = new Mat();
                        Utils.bitmapToMat(bitmaps, mat);
                        for (int i = 0; i < data.size(); i++) {
                            if (data.get(i).getConfidence() >= 0.2) {
                                RectF location = data.get(i).getLocation();
                                Imgproc.rectangle(mat, new Point(location.left, location.top), new Point(location.right, location.bottom), new Scalar(0, 255, 0), 2);
//                                canvas.drawRect(data.get(i).getLocation(), mPaint);
//                                canvas.drawText(data.get(i).getTitle(), data.get(i).getLocation().left + 40, data.get(i).getLocation().top + 70, mTextPaint);
                            }
                        }

                        Utils.matToBitmap(mat, bitmap);

                        cameraView.setVisibility(View.GONE);
                        mImageView.setVisibility(View.VISIBLE);
                        mImageView.setImageBitmap(bitmap);

//                        mTvDate.setText(ct);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } catch (IOException e) {
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
//            case R.id.rl_back:
//                finish();
//                break;
//            case R.id.bt_connnect:
////                if (!UsbConnect.isConnect(this)) {
////                    UsbConnect.Connect(this);
////                }
//                UsbConnect.Connect(this);

//                break;
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
        }


        return false;
    }
}