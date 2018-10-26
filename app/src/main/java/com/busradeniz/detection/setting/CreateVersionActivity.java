package com.busradeniz.detection.setting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.busradeniz.detection.BaseApplication;
import com.busradeniz.detection.R;
import com.busradeniz.detection.base.BaseActivity;
import com.busradeniz.detection.base.BaseBean;
import com.busradeniz.detection.bean.ConfigureInfoBean;
import com.busradeniz.detection.bean.ConfigureListBean;
import com.busradeniz.detection.bean.LocationBean;
import com.busradeniz.detection.bean.NewVersionBean;
import com.busradeniz.detection.check.bean.ModelBean;
import com.busradeniz.detection.check.bean.RecordListBean;
import com.busradeniz.detection.greendaodemo.db.SupportBeanDao;
import com.busradeniz.detection.setting.adapter.RcyCreateModleListAdapter;
import com.busradeniz.detection.setting.presenter.SettingInterface;
import com.busradeniz.detection.setting.presenter.SettingPresenter;
import com.busradeniz.detection.tensorflow.Classifier;
import com.busradeniz.detection.tensorflow.TensorFlowObjectDetectionAPIModel;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.CorrectImageUtils;
import com.busradeniz.detection.utils.DialogUtils;
import com.busradeniz.detection.utils.LocationUtils;
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
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import tensorflow.serving.Predict;
import tensorflow.serving.PredictionServiceGrpc;

public class CreateVersionActivity extends BaseActivity implements View.OnClickListener, SettingInterface {

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
    private SettingPresenter mPresenter;
    private List<String> mClassifyList = new ArrayList<>();
    private int mResizeShortCount = 1;
    private PredictionServiceGrpc.PredictionServiceBlockingStub mStub;
    private Predict.PredictRequest.Builder mPredictRequestBuilder;
    private FloatingActionMenu mFloatingActionMenu;
    private FloatingActionButton mFabMain;
    private RecyclerView mRcyList;
    private RcyCreateModleListAdapter mAdapter;
    private List<NewVersionBean> mList = new ArrayList<>();
    private RelativeLayout mRlSave;
    private EditText mEtProjectName;
    private SupportBeanDao mSupportBeanDao;
    private List<Bitmap> mShortBitmapList;
    private List<Map<String, Object>> mRects;
    private boolean mCheckIsOk;
    private boolean mIsUpdata;
    private int mId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initData();

        initEvent();
    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_create_version;
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

//        DialogUtils.showTopDialog(getSupportFragmentManager(), R.layout.ldialog_top_tips, 10000);

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

        Bundle bundleExtra = getIntent().getBundleExtra(Constant.BUNDLE_PARMS);

        if (bundleExtra != null) {
            mIsUpdata = bundleExtra.getBoolean(Constant.STATUS);
            mId = bundleExtra.getInt(Constant.ID);
        }

        mPresenter = new SettingPresenter(this);
        mPresenter.getTag();

        if (mIsUpdata) {
            mPresenter.getConfigureInfo(mId);
        }

        initTensorFlowAndLoadModel();

        mSupportBeanDao = BaseApplication.getApplicatio().getDaoSession().getSupportBeanDao();
        mAdapter = new RcyCreateModleListAdapter(this, mList);
        mRcyList.setAdapter(mAdapter);

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

    public void notifyAdapter(List<Rect> rects, List<String> strings) {

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
            NewVersionBean.Location location = new NewVersionBean.Location();
            location.setLeft(rects.get(i).left);
            location.setTop(rects.get(i).top);
            location.setRight(rects.get(i).right);
            location.setBottom(rects.get(i).bottom);
            newVersionBean.setLocation(location);
            mList.add(newVersionBean);
        }

    }

    private void initEvent() {
        mIvMakePhoto.setOnClickListener(this);
        mRlSave.setOnClickListener(this);

        mEtProjectName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEtProjectName.setFocusableInTouchMode(true);
                return false;
            }
        });
        mIvImage.setUpdataAdapterInterface(new ScaleImageView.UpdataAdapterInterface() {

            @Override
            public void setUpdataAdapterListener(int index, String strings) {

                mList.get(index).setName(strings);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void setAddListener(Rect rects, String strings) {

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
                newVersionBean.setName(strings);
                NewVersionBean.Location location = new NewVersionBean.Location();
                location.setLeft(rects.left);
                location.setTop(rects.top);
                location.setRight(rects.right);
                location.setBottom(rects.bottom);
                newVersionBean.setLocation(location);
                mList.add(newVersionBean);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void setDeleteListListener(int index) {
                mList.remove(index);
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

                            //获取hed图
                            Mat recognize = detector.recognize(bitmaps, mWidth, mHeight);


                            //矫正图片
                            mCorrectBitmap = CorrectImageUtils.correctImage(recognize, mBitmap, 1);


                            Bitmap bitmap = Bitmap.createBitmap(mCorrectBitmap.getWidth(), mCorrectBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(recognize, bitmap);

                            File fileDirs = new File("/sdcard/image/" + startTime + ".png");
                            fileDirs.createNewFile();

                            FileOutputStream outs = new FileOutputStream(fileDir);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outs);
                            outs.flush();
                            outs.close();



                            //发送请求
                            mPresenter.requestModel(CreateVersionActivity.this, mCorrectBitmap);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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

                break;
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_save:
                if (TextUtils.isEmpty(mEtProjectName.getText().toString().trim())) {
                    ToastUtils.showTextToast(UiUtils.getString(R.string.not_input_project_name));
                    return;
                }

                //保存之前先把零件切割发送到服务器检测是否ok

                mShortBitmapList = new ArrayList<>();
                mRects = new ArrayList<>();

                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).isStatus()) {
                        Map<String, Object> map = new HashMap<>();
                        Rect rect = new Rect();
                        int left = mList.get(i).getLocation().getLeft();
                        int right = mList.get(i).getLocation().getRight();
                        int top = mList.get(i).getLocation().getTop();
                        int bottom = mList.get(i).getLocation().getBottom();
                        int leftDistance = 0;
                        int topDistance = 0;

                        if (left - 40 >= 0) {
                            leftDistance = 40;
                            rect.left = left - 40;
                        } else {
                            leftDistance = Math.abs(0 - left);
                            rect.left = 0;
                        }

                        if (top - 40 >= 0) {
                            rect.top = top - 40;
                            topDistance = 40;
                        } else {
                            rect.top = 0;
                            topDistance = Math.abs(0 - top);
                        }

                        if (right + 40 <= 400) {
                            rect.right = right + 40;
                        } else {
                            rect.right = 400;
                        }

                        if (bottom + 40 <= 800) {
                            rect.bottom = bottom + 40;
                        } else {
                            rect.bottom = 800;
                        }

                        map.put("width", Math.abs(left - right));//存储检测对象的宽
                        map.put("height", Math.abs(top - bottom));//存储检测对象的高度
                        map.put("left_distance", leftDistance);
                        map.put("top_distance", topDistance);
                        map.put("checkLocation", rect);
                        map.put("name", mList.get(i).getName());

                        mRects.add(map);

                        int width = Math.abs(rect.left - rect.right);
                        int height = Math.abs(rect.top - rect.bottom);
                        Bitmap cropBitmap = Bitmap.createBitmap(mCorrectBitmap, rect.left, rect.top, width, height);
                        mShortBitmapList.add(cropBitmap);
                    }

                }

                //测试零件是否ok
                mPresenter.sendPhotoGetResult(this, mShortBitmapList);

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

                JSONArray jsonArray = json.getJSONArray("datas");

                for (int j = 0; j < jsonArray.length(); j++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    List<Classifier.Recognition> location = LocationUtils.getLocation(jsonObject, mCorrectBitmap.getWidth(), mCorrectBitmap.getHeight());

                    Mat mat = new Mat();
                    Utils.bitmapToMat(mCorrectBitmap, mat);

                    List<Rect> list = new ArrayList<>();
                    List<Rect> originalList = new ArrayList<>();
                    List<String> partName = new ArrayList<>();
                    for (int i = 0; i < location.size(); i++) {
                        if (location.get(i).getConfidence() >= 0.5) {
                            String index = location.get(i).getTitle(); // 零件名称的角标

                            RectF rectF = location.get(i).getLocation();
                            Rect rect = new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                            Rect originalRect = new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                            list.add(rect);
                            originalList.add(originalRect);
                            partName.add(mClassifyList.get(Integer.parseInt(index) - 1));
//                        Imgproc.rectangle(mat, new Point(rectF.left, rectF.top), new Point(rectF.right, rectF.bottom), new Scalar(0, 255, 0), 2);
//                                canvas.drawRect(data.get(i).getLocation(), mPaint);
//                                canvas.drawText(data.get(i).getTitle(), data.get(i).getLocation().left + 40, data.get(i).getLocation().top + 70, mTextPaint);
                        }
                    }
                    mIvImage.setRectList(list);
                    mIvImage.setOriginalRectList(originalList);
                    mIvImage.setPartNameList(partName);
                    Bitmap bitmap = Bitmap.createBitmap(mCorrectBitmap.getWidth(), mCorrectBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(mat, bitmap);
                    mIvImage.setImageBitmap(bitmap);
                    mCameraView.setVisibility(View.GONE);
                    mIvImage.setVisibility(View.VISIBLE);


                    mList.clear();
                    notifyAdapter(originalList, partName);

                    mRlSave.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();

                }


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
    public void createConfigureSuccess(BaseBean baseBean) {

        if (baseBean.getResult() == 0) {
            ToastUtils.showTextToast(UiUtils.getString(R.string.save_success));
            finish();
        }

    }

    @Override
    public RequestBody getParms() {

        Gson gson = new Gson();

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("deviation", mList);
        dataMap.put("location", mRects);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        if (!mIsUpdata) {
            File filesDir = getFilesDir();
            File file = saveBitmapFile(mCorrectBitmap, filesDir.getAbsolutePath() + "text.jpg");
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("image", file.getName(), requestBody);
        }

        builder.addFormDataPart("name", mEtProjectName.getText().toString().trim());
        builder.addFormDataPart("config_type_id", "1");
        builder.addFormDataPart("data", gson.toJson(dataMap));

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

            String url = Constant.URL + "api/record/download?url=" + data.getPath();

            Glide.with(this).load(url).into(mIvImage);

            Bitmap bitmap = returnBitMap(url);


            mCameraView.setVisibility(View.GONE);
            mIvImage.setVisibility(View.VISIBLE);
            mIvMakePhoto.setVisibility(View.GONE);
            mEtProjectName.setText(data.getName());
            mEtProjectName.setSelection(data.getName().length());

            String location = data.getData();
            Gson gson = new Gson();
            LocationBean locationBean = gson.fromJson(location, LocationBean.class);

            List<LocationBean.DeviationBeanX> deviationList = locationBean.getDeviation();


            List<Rect> list = new ArrayList<>();
            List<Rect> originalList = new ArrayList<>();
            List<String> partNameList = new ArrayList<>();
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


                LocationBean.DeviationBeanX.LocationBeanB rectF = deviationList.get(i).getLocation();

                Rect rect = new Rect(rectF.getLeft(), rectF.getTop(), rectF.getRight(), rectF.getBottom());
                Rect originalRect = new Rect(rectF.getLeft(), rectF.getTop(), rectF.getRight(), rectF.getBottom());
                list.add(rect);
                originalList.add(originalRect);

                partNameList.add(deviationList.get(i).getName());

                newVersionBean.setLocation(beanLocation);

                mList.add(newVersionBean);
            }

            mIvImage.setRectList(list);
            mRlSave.setVisibility(View.VISIBLE);
            mIvImage.setOriginalRectList(originalList);
            mIvImage.setPartNameList(partNameList);
            mIvImage.postInvalidate();
            mAdapter.notifyDataSetChanged();

        }

    }


    public Bitmap returnBitMap(final String url) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imageurl = null;

                try {
                    imageurl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection) imageurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    mCorrectBitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return mCorrectBitmap;

    }


    @Override
    public void updataConfigureSuccess(BaseBean baseBean) {

        if (baseBean.getResult() == 0) {
            ToastUtils.showTextToast(UiUtils.getString(R.string.updata_success));
            finish();
        }
    }

    @Override
    public void commitCheckResultSuccess(BaseBean baseBean) {

    }

    @Override
    public void testCutPhotoSuccess(ResponseBody responseBody) {

        try {
            String string = responseBody.string();
            JSONObject json = new JSONObject(string);

            JSONArray jsonArray = json.getJSONArray("datas");

            int count = 0;

            for (int j = 0; j < jsonArray.length(); j++) {

                JSONObject jsonObject = jsonArray.getJSONObject(j);

                int left = (int) mRects.get(j).get("left_distance");
                int top = (int) mRects.get(j).get("top_distance");
                int width = (int) mRects.get(j).get("width");
                int height = (int) mRects.get(j).get("height");
                String name = (String) mRects.get(j).get("name");

                List<Classifier.Recognition> location = LocationUtils.getLocation(jsonObject, mShortBitmapList.get(j).getWidth(), mShortBitmapList.get(j).getHeight());


                Rect rect = new Rect();  //目标零件的正确位置
                rect.left = left;
                rect.top = top;
                rect.right = rect.left + width;
                rect.bottom = rect.top + height;

                Bitmap bitmap = mShortBitmapList.get(j);

                Mat mat = new Mat();
                Utils.bitmapToMat(bitmap, mat);


                for (int i = 0; i < location.size(); i++) {

                    String id = location.get(i).getTitle();

                    String classifyName = mClassifyList.get(Integer.parseInt(id) - 1);

                    if (name.equals(classifyName)) {
                        if (location.get(i).getConfidence() >= 0.5) {
                            RectF rectF = location.get(i).getLocation();    //检测获取到的结果

                            Imgproc.rectangle(mat, new Point(rectF.left, rectF.top), new Point(rectF.right, rectF.bottom), new Scalar(255, 180, 0), 5);

                            Utils.matToBitmap(mat, bitmap);

                            if (Math.abs(rect.left - rectF.left) > 10) {
                                continue;
                            }
                            if (Math.abs(rect.right - rectF.right) > 10) {
                                continue;
                            }
                            if (Math.abs(rect.top - rectF.top) > 10) {
                                continue;
                            }
                            if (Math.abs(rect.bottom - rectF.bottom) > 10) {
                                continue;
                            }
                            count++;
                            break;
                        }
                    }

                }
            }

            DialogUtils.showIosDialog(getSupportFragmentManager(), UiUtils.getString(R.string.confirm_save) + "\"" + (mEtProjectName.getText().toString().trim()) + "\"" + UiUtils.getString(R.string.the_mondel));
            DialogUtils.setOnConfirmClickListener(new DialogUtils.IosDialogListener() {
                @Override
                public void onConfirmClickListener(View view) {

                    if (mIsUpdata) {  //更新
                        mPresenter.updataConfigure(mId);

                    } else {        //新建
                        mPresenter.createConfigure();

                    }

                }
            });

            if (count == jsonArray.length()) { //如果检测ok 就直接保存

            } else {  //不ok


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void getRecordListSuccess(RecordListBean recordListBean) {

    }


}
