package com.busradeniz.detection.setting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.busradeniz.detection.BaseApplication;
import com.busradeniz.detection.R;
import com.busradeniz.detection.bean.NewVersionBean;
import com.busradeniz.detection.bean.SupportBean;
import com.busradeniz.detection.check.bean.ModelBean;
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
import com.busradeniz.detection.utils.SpUtils;
import com.busradeniz.detection.utils.ToastUtils;
import com.busradeniz.detection.utils.UiUtils;
import com.busradeniz.detection.view.ScaleImageView;
import com.google.android.cameraview.CameraView;
import com.google.gson.Gson;
import com.novaapps.floatingactionmenu.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import tensorflow.serving.Model;
import tensorflow.serving.Predict;
import tensorflow.serving.PredictionServiceGrpc;

public class CreateVersionActivity extends AppCompatActivity implements View.OnClickListener, SettingInterface {

    private ImageView mIvMakePhoto;
    private CameraView mCameraView;
    private int mWidth;
    private int mHeight;
    private Classifier mDetector;
    private ScaleImageView mIvImage;
    private Bitmap mBitmap;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Classifier detector;
    private Bitmap mCorrectBitmap;
    private final String TAG = "daixinhong";
    private NewsService mAnInterface;
    private SettingPresenter mPresenter;
    private List<String> mClassifyList = new ArrayList<>();
    private int mResizeShortCount = 1;
    private PredictionServiceGrpc.PredictionServiceBlockingStub mStub;
    private Predict.PredictRequest.Builder mPredictRequestBuilder;
    private FloatingActionMenu mFloatingActionMenu;
    private FloatingActionButton mFabMain;
    private boolean mIsSave;
    private RecyclerView mRcyList;
    private RcyCreateModleListAdapter mAdapter;
    private List<NewVersionBean> mList = new ArrayList<>();
    private RelativeLayout mRlSave;
    private EditText mEtProjectName;
    private SupportBeanDao mSupportBeanDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_version);

        initView();

        initData();

        initEvent();
    }

    private void initView() {
        mIvMakePhoto = findViewById(R.id.iv_make_photo);
        mCameraView = findViewById(R.id.cameraView);
        mIvImage = findViewById(R.id.iv_image);
        mFloatingActionMenu = findViewById(R.id.fab_menu_line);
        mFabMain = findViewById(R.id.fab_main);
        mRlSave = findViewById(R.id.rl_save);
        mEtProjectName = findViewById(R.id.et_project_name);
        mRcyList = findViewById(R.id.rcy_list);
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

    private void initData() {
        mPresenter = new SettingPresenter(this);
        mPresenter.getTag();
        initTensorFlowAndLoadModel();

        mSupportBeanDao = BaseApplication.getApplicatio().getDaoSession().getSupportBeanDao();
        mAdapter = new RcyCreateModleListAdapter(this, mList);
        mRcyList.setAdapter(mAdapter);

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
        mRlSave.setOnClickListener(this);
        mIvImage.setUpdataAdapterInterface(new ScaleImageView.UpdataAdapterInterface() {
            @Override
            public void setUpdataAdapterListener(List<Rect> rects, List<String> strings) {

                mList.clear();
                for (int i = 0; i < strings.size(); i++) {
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
                    newVersionBean.setName(strings.get(i));
                    mList.add(newVersionBean);
                }
                mRlSave.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }
        });
        mFloatingActionMenu.setOnMenuItemClickListener(new FloatingActionMenu.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionMenu fam, int index, FloatingActionButton item) {

                Mat det = new Mat();
                det.convertTo(det, CvType.CV_8UC3);
                Mat mat = new Mat();
                boolean delete = false;
                boolean modify = false;

                switch (index) {
                    case 0:     //放大
                        bigImage(mat, det);
                        mFabMain.setImageResource(R.mipmap.big);
                        break;

                    case 1:    //缩小
                        shortImage(mat, det);
                        mFabMain.setImageResource(R.mipmap.shorts);
                        break;
                    case 2:   //绘制
                        mFloatingActionMenu.close();
                        mIvImage.setPoint(true);
                        mFabMain.setImageResource(R.mipmap.draw);
                        break;

                    case 3:  //删除
                        delete = true;
                        mFloatingActionMenu.close();
                        mFabMain.setImageResource(R.mipmap.delete);
                        break;

                    case 4:  //拖动
                        mFloatingActionMenu.close();
                        mIvImage.setPoint(false);
                        mFabMain.setImageResource(R.mipmap.finger);
                        break;
                    case 5: //修改
                        mFloatingActionMenu.close();
                        mFabMain.setImageResource(R.mipmap.edit);
                        modify = true;
                        mIvImage.setModify(modify);
                        break;
                }
                mIvImage.mIsDelete = delete;

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

                        long startTime = System.currentTimeMillis();
                        try {
                            File fileDir = new File("/sdcard/srcImage/" + startTime + ".png");
                            fileDir.createNewFile();

                            FileOutputStream out = new FileOutputStream(fileDir);
                            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();

                            mWidth = mBitmap.getWidth() / 4;
                            mHeight = mBitmap.getHeight() / 4;
                            mCameraView.setVisibility(View.GONE);
                            mIvImage.setVisibility(View.VISIBLE);
                            Bitmap bitmaps = Bitmap.createScaledBitmap(mBitmap, mWidth, mHeight, false);//400,300实际传过去的图片大小


                            Mat recognize = detector.recognize(bitmaps, mWidth, mHeight);


                            Mat mat = new Mat();
                            Utils.bitmapToMat(bitmaps, mat);


                            Bitmap bitmap2 = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.RGB_565);
                            Utils.matToBitmap(recognize, bitmap2);


                            mCorrectBitmap = CorrectImageUtils.correctImage(recognize, mBitmap, 1);

                            File fileDirs = new File("/sdcard/image/" + startTime + ".png"); //将图片保存到手机
                            fileDirs.createNewFile();
                            FileOutputStream outs = new FileOutputStream(fileDirs);
                            mCorrectBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outs);
                            outs.flush();
                            outs.close();

                            recognizeImage(mCorrectBitmap);  //把矫正后的图片发给后台识别

                        } catch (Exception e) {
                            Log.e(TAG, "run: 矫正失败" + e.getMessage());
                            e.printStackTrace();
                        }

//                        mIvImage.setImageBitmap(mCorrectBitmap);

                    }
                });


            }
        });

    }


    /**
     * 放大图片
     *
     * @param mat
     * @param det
     */
    public void bigImage(Mat mat, Mat det) {
        mResizeShortCount += 1;
        Utils.bitmapToMat(mCorrectBitmap, mat);

        int resizeWidthBig = mCorrectBitmap.getWidth() * mResizeShortCount;
        int resizeHeightBig = mCorrectBitmap.getHeight() * mResizeShortCount;

        Imgproc.resize(mat, det, new Size(resizeWidthBig, resizeHeightBig));
        Bitmap bitmapBig = Bitmap.createBitmap(resizeWidthBig, resizeHeightBig, Bitmap.Config.ARGB_8888);

        List<Rect> rectListBig = mIvImage.getRectList();
        List<Rect> originalRectList = mIvImage.getOriginalRectList();   //第一次在屏幕上画的矩形

        for (int i = 0; i < rectListBig.size(); i++) {
            Rect rect = rectListBig.get(i);
            rect.left = (rect.left + originalRectList.get(i).left);
            rect.right = (rect.right + originalRectList.get(i).right);
            rect.top = (rect.top + originalRectList.get(i).top);
            rect.bottom = (rect.bottom + originalRectList.get(i).bottom);
            rectListBig.set(i, rect);
        }

        mIvImage.setRectList(rectListBig);
        Utils.matToBitmap(det, bitmapBig);
        mIvImage.setImageBitmap(bitmapBig);
        mIvImage.setResize(mResizeShortCount);
        mIvImage.postInvalidate();

    }

    /**
     * 缩小图片
     *
     * @param mat
     * @param det
     */
    public void shortImage(Mat mat, Mat det) {
        if (mResizeShortCount == 1)
            return;
        mResizeShortCount -= 1;
        Utils.bitmapToMat(mCorrectBitmap, mat);

        int resizeWidth = mCorrectBitmap.getWidth() * mResizeShortCount;
        int resizeHeight = mCorrectBitmap.getHeight() * mResizeShortCount;

        Imgproc.resize(mat, det, new Size(resizeWidth, resizeHeight));
        Bitmap bitmap = Bitmap.createBitmap(resizeWidth, resizeHeight, Bitmap.Config.ARGB_8888);
        List<Rect> rectList = mIvImage.getRectList();
        List<Rect> originalRectListShort = mIvImage.getOriginalRectList();   //第一次在屏幕上画的矩形

        for (int i = 0; i < rectList.size(); i++) {
            Rect rect = rectList.get(i);
            rect.left = (rect.left - originalRectListShort.get(i).left);
            rect.right = (rect.right - originalRectListShort.get(i).right);
            rect.top = (rect.top - originalRectListShort.get(i).top);
            rect.bottom = (rect.bottom - originalRectListShort.get(i).bottom);
            rectList.set(i, rect);
        }

        mIvImage.setRectList(rectList);
        Utils.matToBitmap(det, bitmap);
        mIvImage.setImageBitmap(bitmap);
        mIvImage.postInvalidate();
        mIvImage.setResize(mResizeShortCount);
    }


    private void recognizeImage(Bitmap bitmaps) {
        File filesDir = getApplicationContext().getFilesDir();
        File file = FileUtils.saveBitmapFile(bitmaps, filesDir.getAbsolutePath() + "text.jpg");

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("multipart/form-data"), file);

        builder.addFormDataPart("images", file.getName(), requestBody);

        String modelUrl = (String) SpUtils.getParam(this, Constant.MODEL_URL, "");
        if (!TextUtils.isEmpty(modelUrl)) {
            mPresenter.requestModel(modelUrl, builder.build());
        }

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
                    return;
                }
                System.gc();
                mCameraView.takePicture();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            while (true) {
//                                Thread.sleep(3000);
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        System.gc();
//                                        mCameraView.takePicture();
//                                    }
//                                });
//                            }
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();

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

                        supportBean.setLocation(gson.toJson(mIvImage.getOriginalRectList()));
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
            String string = responseBody.string();
            try {
                JSONObject json = new JSONObject(string);
                List<Classifier.Recognition> location = LocationUtils.getLocation(json, mCorrectBitmap.getWidth(), mCorrectBitmap.getHeight());

                Mat mat = new Mat();
                Utils.bitmapToMat(mCorrectBitmap, mat);

                List<Rect> list = new ArrayList<>();
                List<Rect> originalList = new ArrayList<>();
                for (int i = 0; i < location.size(); i++) {

                    if (location.get(i).getConfidence() >= 0.5) {
                        RectF rectF = location.get(i).getLocation();
                        Rect rect = new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                        Rect originalRect = new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                        list.add(rect);
                        originalList.add(originalRect);
//                        Imgproc.rectangle(mat, new Point(rectF.left, rectF.top), new Point(rectF.right, rectF.bottom), new Scalar(0, 255, 0), 2);
//                                canvas.drawRect(data.get(i).getLocation(), mPaint);
//                                canvas.drawText(data.get(i).getTitle(), data.get(i).getLocation().left + 40, data.get(i).getLocation().top + 70, mTextPaint);
                    }
                }
                mIvImage.setRectList(list);
                mIvImage.setOriginalRectList(originalList);
                Bitmap bitmap = Bitmap.createBitmap(mCorrectBitmap.getWidth(), mCorrectBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat, bitmap);
                mIvImage.setImageBitmap(bitmap);
                mCameraView.setVisibility(View.GONE);
                mIvImage.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getModelSuccess(ModelBean modelBean) {

    }

    @Override
    public void getDataError(Throwable throwable) {

    }


    public void test(int width, int height, Mat mats) {

        mats.convertTo(mats, CvType.CV_32FC3);
        float[] floats = new float[(int) mats.total() * mats.channels()];

        mats.get(0, 0, floats);

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < floats.length; i++) {
            list.add((int) floats[i]);
        }


        DataType dataType = DataType.DT_UINT8;

//这里还是先用block模式
        if (mStub == null) {
            ManagedChannel channel = ManagedChannelBuilder.forAddress("192.168.1.97", 9000).usePlaintext(true).keepAliveTime(600, TimeUnit.SECONDS).maxInboundMessageSize(100 * 1024 * 1024).build();
            mStub = PredictionServiceGrpc.newBlockingStub(channel);
            Log.e(TAG, "test: sss s ");
        }

//创建请求
        if (mPredictRequestBuilder == null) {
            mPredictRequestBuilder = Predict.PredictRequest.newBuilder();
            Model.ModelSpec.Builder modelSpecBuilder = Model.ModelSpec.newBuilder();
            modelSpecBuilder.setName("faster_50");
            modelSpecBuilder.setSignatureName("detection_signature");
            mPredictRequestBuilder.setModelSpec(modelSpecBuilder);
        }

//设置入参,访问默认是最新版本，如果需要特定版本可以使用tensorProtoBuilder.setVersionNumber方法
        TensorProto.Builder tensorProtoBuilder = TensorProto.newBuilder();
        tensorProtoBuilder.setDtype(dataType);
        TensorShapeProto.Builder tensorShapeBuilder = TensorShapeProto.newBuilder();
        tensorShapeBuilder.addDim(TensorShapeProto.Dim.newBuilder().setSize(1));
        tensorShapeBuilder.addDim(TensorShapeProto.Dim.newBuilder().setSize(width));

        tensorShapeBuilder.addDim(TensorShapeProto.Dim.newBuilder().setSize(height));

        tensorShapeBuilder.addDim(TensorShapeProto.Dim.newBuilder().setSize(3));
        tensorProtoBuilder.setTensorShape(tensorShapeBuilder.build());
        tensorProtoBuilder.addAllIntVal(list);
        mPredictRequestBuilder.putInputs("inputs", tensorProtoBuilder.build());
        Predict.PredictRequest build = mPredictRequestBuilder.build();
        long l = System.currentTimeMillis();

//访问并获取结果
        Predict.PredictResponse predictResponse = mStub.predict(build);
        long end = System.currentTimeMillis();
        Log.e(TAG, "test: " + (end - l));

        TensorProto detection_boxes = predictResponse.getOutputsOrThrow("detection_boxes");
        TensorProto num_detections = predictResponse.getOutputsOrThrow("num_detections");
        TensorProto detection_scores = predictResponse.getOutputsOrThrow("detection_scores");
        TensorProto detection_classes = predictResponse.getOutputsOrThrow("detection_classes");


//        final RectF detection =
//                new RectF(
//                        outputLocations[4 * i + 1] * 480,
//                        outputLocations[4 * i] * 800,
//                        outputLocations[4 * i + 3] * 480,
//                        outputLocations[4 * i + 2] * 800);


    }
}
