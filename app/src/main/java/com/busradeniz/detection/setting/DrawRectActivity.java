package com.busradeniz.detection.setting;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.tensorflow.Classifier;
import com.busradeniz.detection.tensorflow.TensorFlowObjectDetectionAPIModel;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.CorrectImageUtils;
import com.busradeniz.detection.view.ScaleImageView;
import com.google.android.cameraview.CameraView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DrawRectActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout mRlBack;
    private TextView mTvBaseTitle;
    private TextView mTvSave;
    private RelativeLayout mRlSave;
    private CameraView mCameraView;
    private ScaleImageView mIvPhoto;
    private ImageView mIvTakePhoto;
    private Bitmap mCorrectBitmap;
    private String TAG = "daixinhong";
    private Bitmap mBitmap;
    private int mWidth;
    private int mHeight;
    private Classifier detector;
    private Executor executor = Executors.newSingleThreadExecutor();
    private ImageView mIvResizeBig;
    private ImageView mIvResizeShort;
    private RelativeLayout mRlRect;
    private int mResizeShortCount = 1;
    private float mDownX;
    private float mDownY;
    private TextView mTvSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_rect);

        initView();

        initData();

        initEvent();

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
        initTensorFlowAndLoadModel();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {

        mIvTakePhoto.setOnClickListener(this);
        mRlBack.setOnClickListener(this);
        mIvResizeBig.setOnClickListener(this);
        mIvResizeShort.setOnClickListener(this);
        mTvSwitch.setOnClickListener(this);
        mCameraView.addCallback(new CameraView.Callback() {

            @Override
            public void onPictureTaken(CameraView cameraView, final byte[] data) {
                super.onPictureTaken(cameraView, data);
                //根据训练的模型生成hed图
                Mat headPhoto = getHeadPhoto(data);

                mCorrectBitmap = CorrectImageUtils.correctImage(headPhoto, mBitmap, 1);
                mIvPhoto.setImageBitmap(mCorrectBitmap);

            }
        });


//        mIvPhoto.setOnTouchListener(shopCarSettleTouch);

    }


    /**
     * 获取hed图
     *
     * @param data
     */
    private Mat getHeadPhoto(byte[] data) {
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
            mRlRect.setVisibility(View.VISIBLE);
            Bitmap bitmaps = Bitmap.createScaledBitmap(mBitmap, mWidth, mHeight, false);//400,300实际传过去的图片大小

            Mat mRecognize = detector.recognize(bitmaps, mWidth, mHeight);

            Bitmap bitmap2 = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(mRecognize, bitmap2);

            File fileDirs = new File("/sdcard/image/" + startTime + ".png");
            fileDirs.createNewFile();

            FileOutputStream outs = new FileOutputStream(fileDirs);
            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, outs);
            outs.flush();
            outs.close();
            return mRecognize;

        } catch (Exception e) {
            Log.e(TAG, "run: 矫正失败");
        }

        return null;
    }

    private void initView() {
        mRlBack = findViewById(R.id.rl_back);
        mTvBaseTitle = findViewById(R.id.tv_base_title);
        mTvSave = findViewById(R.id.tv_save);
        mRlSave = findViewById(R.id.rl_save);
        mCameraView = findViewById(R.id.cameraView);
        mIvPhoto = findViewById(R.id.iv_photo);
        mIvTakePhoto = findViewById(R.id.iv_take_photo);
        mIvResizeBig = findViewById(R.id.iv_resize_big);
        mIvResizeShort = findViewById(R.id.iv_resize_short);
        mRlRect = findViewById(R.id.rl_rect);
        mTvSwitch = findViewById(R.id.tv_switch);
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

    @Override
    public void onClick(View view) {
        Mat det = new Mat();
        det.convertTo(det, CvType.CV_8UC3);
        Mat mat = new Mat();
        switch (view.getId()) {
            case R.id.iv_take_photo:
                if (mBitmap != null && !mBitmap.isRecycled()) {
                    mBitmap.recycle();
                    mBitmap = null;
                }
                if (mCameraView.getVisibility() == View.GONE) {
                    mCameraView.setVisibility(View.VISIBLE);
                    mRlRect.setVisibility(View.GONE);
                    return;
                }
                System.gc();
                mCameraView.takePicture();
                break;
            case R.id.rl_back:
                finish();

                break;
            case R.id.iv_resize_big:        //放大
                mResizeShortCount += 1;
                Utils.bitmapToMat(mCorrectBitmap, mat);

                int resizeWidthBig = mCorrectBitmap.getWidth() * mResizeShortCount;
                int resizeHeightBig = mCorrectBitmap.getHeight() * mResizeShortCount;

                Imgproc.resize(mat, det, new Size(resizeWidthBig, resizeHeightBig));
                Bitmap bitmapBig = Bitmap.createBitmap(resizeWidthBig, resizeHeightBig, Bitmap.Config.ARGB_8888);

                List<Rect> rectListBig = mIvPhoto.getRectList();
                List<Rect> originalRectList = mIvPhoto.getOriginalRectList();   //第一次在屏幕上画的矩形

                for (int i = 0; i < rectListBig.size(); i++) {
                    Rect rect = rectListBig.get(i);
                    rect.left = (rect.left + originalRectList.get(i).left * 1);
                    rect.right = (rect.right + originalRectList.get(i).right * 1);
                    rect.top = (rect.top + originalRectList.get(i).top * 1);
                    rect.bottom = (rect.bottom + originalRectList.get(i).bottom * 1);
                    rectListBig.set(i, rect);
                }

                mIvPhoto.setRectList(rectListBig);
                Utils.matToBitmap(det, bitmapBig);
                mIvPhoto.setImageBitmap(bitmapBig);
                mIvPhoto.postInvalidate();

                mIvPhoto.setResize(mResizeShortCount);

                break;
            case R.id.iv_resize_short:      //缩小
                if (mResizeShortCount == 1)
                    return;
                mResizeShortCount -= 1;
                Utils.bitmapToMat(mCorrectBitmap, mat);

                int resizeWidth = mCorrectBitmap.getWidth() * mResizeShortCount;
                int resizeHeight = mCorrectBitmap.getHeight() * mResizeShortCount;

                Imgproc.resize(mat, det, new Size(resizeWidth, resizeHeight));
                Bitmap bitmap = Bitmap.createBitmap(resizeWidth, resizeHeight, Bitmap.Config.ARGB_8888);
                List<Rect> rectList = mIvPhoto.getRectList();
                List<Rect> originalRectListShort = mIvPhoto.getOriginalRectList();   //第一次在屏幕上画的矩形

                for (int i = 0; i < rectList.size(); i++) {
                    Rect rect = rectList.get(i);
                    rect.left = (rect.left - originalRectListShort.get(i).left * 1);
                    rect.right = (rect.right - originalRectListShort.get(i).right * 1);
                    rect.top = (rect.top - originalRectListShort.get(i).top * 1);
                    rect.bottom = (rect.bottom - originalRectListShort.get(i).bottom * 1);
                    rectList.set(i, rect);
                }

                mIvPhoto.setRectList(rectList);
                Utils.matToBitmap(det, bitmap);
                mIvPhoto.setImageBitmap(bitmap);
                mIvPhoto.postInvalidate();
                mIvPhoto.setResize(mResizeShortCount);

                break;

            case R.id.tv_switch:

                mIvPhoto.setPoint(!mIvPhoto.isPoint());

                if (mIvPhoto.isPoint()) {
                    mTvSwitch.setText("画矩形");
                } else {
                    mTvSwitch.setText("手势放大");
                }

                break;
        }
    }
}
