package com.busradeniz.detection.tensorflow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.os.Trace;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.busradeniz.detection.OverlayView;
import com.busradeniz.detection.R;
import com.busradeniz.detection.env.BorderedText;
import com.busradeniz.detection.env.ImageUtils;
import com.busradeniz.detection.env.Logger;
import com.busradeniz.detection.tracking.MultiBoxTracker;
import com.ftdi.j2xx.D2xxManager;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class DetectorActivity extends Activity implements OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();

    private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final String TF_OD_API_MODEL_FILE =
            "file:///android_asset/frozen_inference_graph.pb";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt";

    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.6f;

    private static final float TEXT_SIZE_DIP = 10;
    private Classifier detector;
    private long timestamp = 0;
    //Matri为矩阵
    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private MultiBoxTracker tracker;//对应方形颜色类
    private BorderedText borderedText;//borderedText画笔画的文字
    private ImageView mImageView;
    private TextView mTvDate;
    private TextView mIsOk;
    private TextView mIsOkDate;
    protected int previewWidth = 0;
    protected int previewHeight = 0;
    private TextToSpeech textToSpeech;
    private Executor executor = Executors.newSingleThreadExecutor();
    public static D2xxManager ftD2xx = null;
    private static final int TF_Wdith_API_INPUT_SIZE = 400;
    @Override
    protected void onCreate(Bundle savedInstanceState) {  try {
        ftD2xx = D2xxManager.getInstance(this);
    } catch (D2xxManager.D2xxException e) {
        Log.e("FTDI_HT", "getInstance fail!!");
    }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_image);
        intView();
    }

    OverlayView trackingOverlay;
    Bitmap bitmap;

    private void intView() {
        mImageView = (ImageView) findViewById(R.id.iv_image);
        mTvDate = (TextView) findViewById(R.id.tv_text);
        mIsOk = (TextView) findViewById(R.id.is_ok);
        mIsOkDate = (TextView) findViewById(R.id.is_ok_date);
        Intent intent = this.getIntent();

        bitmap = intent.getParcelableExtra("BITMAP");
        mImageView.setImageBitmap(bitmap);
        //TypedValue.applyDimension()方法的功能就是把非标准尺寸转换成标准尺寸,
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());//Context.getResources().getDisplayMetrics()依赖于手机系统，获取到的是系统的屏幕信息；
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);//typeface设置字体
        //borderedText具体的setText在该类当中MultiBoxTracker
        tracker = new MultiBoxTracker(this);

        int cropSize = TF_OD_API_INPUT_SIZE;

        try {
            //初始化封装类
            detector = TensorFlowObjectDetectionAPIModel.create(
                    getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE,TF_Wdith_API_INPUT_SIZE);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            LOGGER.e("Exception initializing classifier!", e);
            finish();
        }
        DisplayMetrics dm = getResources().getDisplayMetrics();
        previewWidth = 70;
        previewHeight = 70;
//    sensorOrientation = rotation - getScreenOrientation();

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
//        rgbFrameBitmap = bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);//CreateBitmap创建一个位图
//        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);
//        madd = BitmapFactory.decodeResource(getResources(), R.drawable.timg);
        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        180, false);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        trackingOverlay = findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        tracker.draw(canvas);
                    }
                });
        initTensorFlowAndLoadModel();
        processImage();
        Trace.beginSection("imageAvailable");
        this.textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    LOGGER.i("onCreate", "TextToSpeech is initialised");
                } else {
                    LOGGER.e("onCreate", "Cannot initialise text to speech!");
                }
            }
        });
        mIsOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetectorActivity.this, SensorCamaActivity.class);
                startActivity(intent);//
                finish();
            }
        });
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //初始化封装类
                    detector = TensorFlowObjectDetectionAPIModel.create(
                            getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE,TF_Wdith_API_INPUT_SIZE);
                } catch (final IOException e) {
                    LOGGER.e("Exception initializing classifier!", e);
                    Toast toast =
                            Toast.makeText(
                                    getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                }
            }
        });
    }

    protected void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;
//    byte[] originalLuminance = getLuminance();
        tracker.onFrame(
                720,
                720,
                90);
        trackingOverlay.postInvalidate();//  invalidate（）：一般用于更新UI线程里的View界面 postInvalidate（）：用于更新非UI线程的View界面        Log.e("tensorflow+==========", "执行1");
        Log.e("tensorflow+==========", "执行1");

//  获取原Bitmap的像素值存储到pixels数组中
//        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
//        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

//        final Canvas canvas = new Canvas(bitmap);
//        final Canvas canvas = new Canvas(croppedBitmap);
//        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                //必不可少
//                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
//                final List<Classifier.Recognition> results = detector.recognizeImage(bitmap);
//                Log.e("tensorflow+==========", results.toString());
//
////                cropCopyBitmap = Bitmap.createBitmap(bitmap);
//                final Canvas canvas = new Canvas(bitmap);
//                final Paint paint = new Paint();
//                paint.setColor(Color.RED);
//                paint.setStyle(Style.STROKE);
//                paint.setStrokeWidth(2.0f);
//                for (int m = 0; m < results.size(); m++) {
//                    if (results.get(m).getLocation().bottom > 0)
//                        mTvDate.setText(results.get(m).toString());
//                    canvas.drawRect(results.get(m).getLocation(), paint);
//                    cropToFrameTransform.mapRect(results.get(m).getLocation());
//                }
//                final List<Classifier.Recognition> mappedRecognitions =
//                        new LinkedList<>();
//                for (final Classifier.Recognition result : results) {
//                    final RectF location = result.getLocation();
//                    if (location != null && result.getConfidence() >= MINIMUM_CONFIDENCE_TF_OD_API) {
//                        //将识别道德lable画在屏幕上,location为坐标值
////                Title: quaffle-------RectF(55.358044, 128.11821, 101.772194, 171.94029)
//                        LOGGER.i("Title: " + result.getTitle() + "-------" + location);
//                        canvas.drawRect(location, paint);
//                        cropToFrameTransform.mapRect(location);
//                        result.setLocation(location);
//                        mappedRecognitions.add(result);
//                    }
//                }
//
//                tracker.trackResults(mappedRecognitions);
////                toSpeech(mappedRecognitions);
//                trackingOverlay.postInvalidate();
//                Trace.beginSection("imageAvailable");
//
//            }
//        }
//        );
        Log.e("tensorflow+==========", "执行1");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //必不可少
//                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 300, false);
                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 200, false);
                final List<Classifier.Recognition> results = detector.recognizeImage(bitmap,DetectorActivity.this);
                Log.e("tensorflow+==========", results.toString());
//                cropCopyBitmap = Bitmap.createBitmap(bitmap);
//                final Canvas canvas = new Canvas(bitmap);
//                final Paint paint = new Paint();
//                paint.setColor(Color.RED);
//                paint.setStyle(Paint.Style.STROKE);
//                paint.setStrokeWidth(2.0f);
                Log.e("tensorflow+==========", "执行2");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int m = 0; m < results.size(); m++) {
//                            if (results.get(m).getLocation().bottom > 0)//[0] quaffle (42.7%) RectF(34.575035, 112.486725, 72.52824, 143.74046),
                            String mBaifenbi=results.get(m).getConfidence()+"";//0.49094936

//                            if (!mBaifenbi.contains("0.0")) {
                            if (results.get(m).getConfidence()>0.0000) {
                                mTvDate.append(results.get(m).toString() + "\n");
//                                mTvDate.setText(results.get(m).toString());
                                mTvDate.postInvalidate();//更新非UI线程的View界面
//                            canvas.drawRect(results.get(m).getLocation(), paint);
                                cropToFrameTransform.mapRect(results.get(m).getLocation());
                                Log.e("tensorflow+==========", "执行3");
//                                if (Integer.parseInt((results.get(m).getConfidence() + "").split(".")[0]) > 40) {
//                                if (Double.valueOf(((results.get(m).getConfidence() + "").split(".")[1])).intValue() > 4) {
                                if (results.get(m).getConfidence()>0.4) {
                                    mIsOkDate.append(results.get(m).getTitle()+"-----" + "PASS"+"\n");
                                } else {
                                    mIsOkDate.append(results.get(m).getTitle() +"-----" +"NG"+"\n");
                                }
                            }
                        }
                    }
                });

                    for (int k = 0; k < results.size(); k++) {
                        if (results.get(k).getLocation().bottom > 0) {
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {

                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
//                                    ToastUtils.showTextToast(DetectorActivity.this,"一秒后我执行了");
                                            Intent intent = new Intent(DetectorActivity.this, SensorCamaActivity.class);
                                            startActivity(intent);//
                                            finish();
                                        }
                                    });
                                }
                            }, 5000);
                        }


                    }

                final List<Classifier.Recognition> mappedRecognitions =
                        new LinkedList<>();
                for (final Classifier.Recognition result : results) {
                    final RectF location = result.getLocation();
                    if (location != null && result.getConfidence() >= MINIMUM_CONFIDENCE_TF_OD_API) {
                        //将识别道德lable画在屏幕上,location为坐标值
//                Title: quaffle-------RectF(55.358044, 128.11821, 101.772194, 171.94029)
                        LOGGER.e("Title: " + result.getTitle() + "-------" + location);
//                        canvas.drawRect(location, paint);
                        cropToFrameTransform.mapRect(location);
                        result.setLocation(location);
                        mappedRecognitions.add(result);
                    }
                }

                tracker.trackResults(mappedRecognitions);//画目标矩形







                for (int m = 0; m < mappedRecognitions.size(); m++) {
                    Log.e("UUU=====", mappedRecognitions.get(m).getLocation() + "");
                }
//                toSpeech(mappedRecognitions);
                trackingOverlay.postInvalidate();
                Trace.beginSection("imageAvailable");
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                detector.close();
            }
        });
    }


    /**
     * 当有一张图片可用时会回调此方法，但有一点一定要注意：
     * 一定要调用 reader.acquireNextImage()和close()方法，否则画面就会卡住！！！！！我被这个坑坑了好久！！！
     * 很多人可能写Demo就在这里打一个Log，结果卡住了，或者方法不能一直被回调。
     **/
    @Override
    public void onImageAvailable(ImageReader reader) {

        return;
    }
}