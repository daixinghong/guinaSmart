package com.busradeniz.detection.tensorflow;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Trace;
import android.support.v4.os.TraceCompat;
import android.util.Log;

import com.busradeniz.detection.env.Logger;
import com.busradeniz.detection.http.NewsService;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.Graph;
import org.tensorflow.Operation;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Wrapper for frozen detection models trained using the Tensorflow Object Detection API:
 * github.com/tensorflow/models/tree/master/research/object_detection
 */
public class TensorFlowObjectDetectionAPIModel implements Classifier {
    private static final Logger LOGGER = new Logger();

    // Only return this many results.
    private static final int MAX_RESULTS = 100;

    // Config values.
    private String inputName;
    //    private int inputSize;
    private int inputWdithSize;

    // Pre-allocated buffers.
    private Vector<String> labels = new Vector<String>();
    private int[] intValues;
    private float[] byteValues;
    public float[] outputLocations;
    public float[] outputScores;
    public float[] outputClasses;
    public float[] outputNumDetections;
    private String[] outputNames;


    private boolean logStats = false;

    private TensorFlowInferenceInterface inferenceInterface;

    /**
     * Initializes a native TensorFlow session for classifying images.
     *
     * @param assetManager  The asset manager to be used to load assets.
     * @param modelFilename The filepath of the model GraphDef protocol buffer.
     * @param labelFilename The filepath of label file for classes.
     */
    public static Classifier create(
            final AssetManager assetManager,
            final String modelFilename,
            final String labelFilename,
            final int inputSize, final int inputWdithSize) throws IOException {
        final TensorFlowObjectDetectionAPIModel d = new TensorFlowObjectDetectionAPIModel();

        InputStream labelsInput = null;
        String actualFilename = labelFilename.split("file:///android_asset/")[1];
        labelsInput = assetManager.open(actualFilename);
        BufferedReader br = null;
        br = new BufferedReader(new InputStreamReader(labelsInput));
        String line;
        while ((line = br.readLine()) != null) {
            LOGGER.w(line);
            d.labels.add(line);
        }
        br.close();


        d.inferenceInterface = new TensorFlowInferenceInterface(assetManager, modelFilename);

        final Graph g = d.inferenceInterface.graph();

        d.inputName = "hed_input";
        // The inputName node has a shape of [N, H, W, C], where
        // N is the batch size
        // H = W are the height and width
        // C is the number of channels (3 for our purposes - RGB)
        final Operation inputOp = g.operation(d.inputName);
        if (inputOp == null) {
            throw new RuntimeException("Failed to find input Node '" + d.inputName + "'");
        }
//        d.inputSize = inputSize;
        d.inputWdithSize = inputWdithSize;
        // The outputScoresName node has a shape of [N, NumLocations], where N
        // is the batch size.
        final Operation outputOp1 = g.operation("out_mask");
        if (outputOp1 == null) {
            throw new RuntimeException("Failed to find output Node 'detection_scores'");
        }
//        final Operation outputOp2 = g.operation("detection_boxes");
//        if (outputOp2 == null) {
//            throw new RuntimeException("Failed to find output Node 'detection_boxes'");
//        }
//        final Operation outputOp3 = g.operation("detection_classes");
//        if (outputOp3 == null) {
//            throw new RuntimeException("Failed to find output Node 'detection_classes'");
//        }

        // Pre-allocate buffers.
        d.outputNames = new String[]{"out_mask"};
        d.outputScores = new float[MAX_RESULTS];
        d.outputLocations = new float[MAX_RESULTS * 4];
        d.outputClasses = new float[MAX_RESULTS];
        d.outputNumDetections = new float[1];
        return d;
    }

    private TensorFlowObjectDetectionAPIModel() {
    }

    @Override
    public List<Recognition> recognizeImage(final Bitmap bitmap, Context context) {
        // Log this method so that it can be analyzed with systrace.
        Trace.beginSection("recognizeImage");
        TraceCompat.beginSection("recognizeImage");

        Trace.beginSection("preprocessBitmap");
        TraceCompat.beginSection("preprocessBitmap");
        // Preprocess the image data from 0-255 int to normalized float based
        // on the provided parameters.
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, 250, 150); //width must be <= bitmap.width()

        for (int i = 0; i < intValues.length; ++i) {
            byteValues[i * 3 + 2] = (byte) (intValues[i] & 0xFF);
            byteValues[i * 3 + 1] = (byte) ((intValues[i] >> 8) & 0xFF);
            byteValues[i * 3 + 0] = (byte) ((intValues[i] >> 16) & 0xFF);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.107:8000/")
                .build();

        NewsService api = retrofit.create(NewsService.class);

        final Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();

        File filesDir = context.getApplicationContext().getFilesDir();

        File file = saveBitmapFile(bitmap, filesDir.getAbsolutePath() + "text.jpg");

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("multipart/form-data"), file);
        builder.addFormDataPart("images", file.getName(), requestBody);


        map.put("images", byteValues);
        Call<ResponseBody> news = api.getNews(builder.build());

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
                        for (int i = 0; i < classes.length(); i++) {
                            outputClasses[i] = (float) classes.getDouble(i);
                            outputScores[i] = (float) scores.getDouble(i);
                        }
                        for (int i = 0; i < box.length(); i++) {
                            outputLocations[i] = (int) box.getDouble(i);
                        }


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


//        Trace.endSection(); // preprocessBitmap
//        TraceCompat.endSection();
//        // Copy the input data into TensorFlow.
//        Trace.beginSection("feed");
//        TraceCompat.beginSection("feed");
//        inferenceInterface.feed(inputName, byteValues, 1, inputSize, inputSize, 3);
//        Trace.endSection();
//        TraceCompat.endSection();
//
//        // Run the inference call.
//        Trace.beginSection("run");
//        TraceCompat.beginSection("run");
//        inferenceInterface.run(outputNames, logStats);
//        Trace.endSection();
//        TraceCompat.endSection();
//
//        // Copy the output Tensor back into the output array.
//        Trace.beginSection("fetch");
//        outputLocations = new float[MAX_RESULTS * 4];
//        outputScores = new float[MAX_RESULTS];
//        outputClasses = new float[MAX_RESULTS];
//        outputNumDetections = new float[1];
//        Log.e("xx", "xxx" + outputNames[0]);
//        inferenceInterface.fetch(outputNames[0], outputLocations);
//        inferenceInterface.fetch(outputNames[1], outputScores);
//        inferenceInterface.fetch(outputNames[2], outputClasses);
//        inferenceInterface.fetch(outputNames[3], outputNumDetections);
//        Trace.endSection();
//        TraceCompat.endSection();

//         Find the best detections.


        final PriorityQueue<Recognition> pq =
                new PriorityQueue<Recognition>(
                        1,
                        new Comparator<Recognition>() {
                            @Override
                            public int compare(final Recognition lhs, final Recognition rhs) {
                                // Intentionally reversed to put high confidence at the head of the queue.
                                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                            }
                        });

        // Scale them back to the input size.
        for (int i = 0; i < outputScores.length; ++i) {
//      if(outputScores[i]>0)
            Log.e("xx", "xxx" + outputScores[i]);
            final RectF detection =
                    new RectF(
                            outputLocations[4 * i + 1] * 480,
                            outputLocations[4 * i] * 800,
                            outputLocations[4 * i + 3] * 480,
                            outputLocations[4 * i + 2] * 800);
            pq.add(
                    new Recognition("" + i, labels.get((int) outputClasses[i]), outputScores[i], detection));
            Log.e("tensprflow", "lable" + ",,,,,," + labels.get((int) outputClasses[i]) + "=======" + outputScores[i] + "++++++++++++" + detection);


        }

        final ArrayList<Recognition> recognitions = new ArrayList<Recognition>();
        for (int i = 0; i < Math.min(pq.size(), MAX_RESULTS); ++i) {
            recognitions.add(pq.poll());
        }
        Trace.endSection(); // "recognizeImage"
        TraceCompat.endSection(); // "recognizeImage"

        return recognitions;
    }

    @Override
    public Mat recognize(Bitmap bitmap, int width, int height) {


        intValues = new int[width * height];
        byteValues = new float[width * height * 3];

        Trace.beginSection("recognizeImage");
        TraceCompat.beginSection("recognizeImage");

        Trace.beginSection("preprocessBitmap");
        TraceCompat.beginSection("preprocessBitmap");

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, width, height); //width must be <= bitmap.width()

        MatOfFloat mats = new MatOfFloat(width, height, CvType.CV_32FC3);
        Utils.bitmapToMat(bitmap, mats);
        mats.convertTo(mats, CvType.CV_32FC3);
        Imgproc.cvtColor(mats, mats, Imgproc.COLOR_RGBA2RGB);

        float[] floats = new float[(int) mats.total() * mats.channels()];
        mats.get(0, 0, floats);

//        for (int i = 0; i < intValues.length; ++i) {
//            byteValues[i * 3 + 2] = (intValues[i] & 0xFF);
//            byteValues[i * 3 + 1] = ((intValues[i] >> 8) & 0xFF);
//            byteValues[i * 3 + 0] = ((intValues[i] >> 16) & 0xFF);
//        }


        Trace.endSection(); // preprocessBitmap
        TraceCompat.endSection();
        // Copy the input data into TensorFlow.
        Trace.beginSection("feed");
        TraceCompat.beginSection("feed");
        inferenceInterface.feed(inputName, floats, height, width, 3);
        Trace.endSection();
        TraceCompat.endSection();
        long time1 = System.currentTimeMillis();

        // Run the inference call.
        Trace.beginSection("run");
        TraceCompat.beginSection("run");
        inferenceInterface.run(outputNames, logStats);
        Trace.endSection();
        TraceCompat.endSection();

        long time2 = System.currentTimeMillis();
        Log.e("gege", "recognize: " + (time2 - time1));

        // Copy the output Tensor back into the output array.
        Trace.beginSection("fetch");
        outputLocations = new float[width * height];
        outputScores = new float[MAX_RESULTS];
        outputClasses = new float[MAX_RESULTS];
        outputNumDetections = new float[1];
        inferenceInterface.fetch(outputNames[0], outputLocations);

        MatOfFloat matOfFloat = new MatOfFloat();
        matOfFloat.fromArray(outputLocations);
        Mat reshape = matOfFloat.reshape(1, bitmap.getHeight());

        Mat srcMat = new Mat();
        reshape.convertTo(reshape, CvType.CV_8UC1);

        Imgproc.resize(reshape, srcMat, new Size(bitmap.getWidth() * 4, bitmap.getHeight() * 4), 0, 0, Imgproc.INTER_LINEAR);
//        Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth() * 4, bitmap.getHeight() * 4, Bitmap.Config.RGB_565);

        Imgproc.threshold(srcMat, srcMat, 0, 255, Imgproc.THRESH_BINARY);

//        Utils.matToBitmap(srcMat, bitmap2);

        Trace.endSection();
        TraceCompat.endSection();
        return srcMat;
    }

    public static Bitmap createBitmap(float[] values, int picW, int picH) {
        if (values == null || picW <= 0 || picH <= 0)
            return null;
        //使用8位来保存图片
        Bitmap bitmap = Bitmap
                .createBitmap(picW, picH, Bitmap.Config.ARGB_8888);
        int pixels[] = new int[picW * picH];
        for (int i = 0; i < picW * picH; i++) {
            if (values[i] > 0) {
                pixels[i] = (0xFFFFFFFF);
                Log.e("vivi", "createBitmap: " + pixels[i]);
            } else {
                pixels[i] = (0xFF000000);
            }
//            pixels[i] = (int) (values[i] + values[i] * 256 + values[i] * 256 * 256 + 0xFF000000);
//            pixels[i] = (int) (values[i] * 256 + values[i] * 256 * 256+ 0xFF000000);
        }
        bitmap.setPixels(pixels, 0, picW, 0, 0, picW, picH);
        return bitmap;
    }


    @Override
    public List<Recognition> getData(float width, float heigh) {
        final PriorityQueue<Recognition> pq =
                new PriorityQueue<Recognition>(
                        1,
                        new Comparator<Recognition>() {
                            @Override
                            public int compare(final Recognition lhs, final Recognition rhs) {
                                // Intentionally reversed to put high confidence at the head of the queue.
                                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                            }
                        });

        // Scale them back to the input size.
        for (int i = 0; i < outputScores.length; ++i) {
            final RectF detection =
                    new RectF(
                            outputLocations[4 * i + 1] * width,
                            outputLocations[4 * i] * heigh,
                            outputLocations[4 * i + 3] * width,
                            outputLocations[4 * i + 2] * heigh);
            int id = (int) outputClasses[i];

            try {
                pq.add(new Recognition("" + i, "", outputScores[i], detection));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final ArrayList<Recognition> recognitions = new ArrayList<Recognition>();
        for (int i = 0; i < Math.min(pq.size(), MAX_RESULTS); ++i) {
            recognitions.add(pq.poll());
        }
        Trace.endSection(); // "recognizeImage"
        TraceCompat.endSection(); // "recognizeImage"

        return recognitions;
    }


    @Override
    public List<Recognition> getData(JSONObject jsonObject, float width, float heigh) {
        final PriorityQueue<Recognition> pq =
                new PriorityQueue<Recognition>(
                        1,
                        new Comparator<Recognition>() {
                            @Override
                            public int compare(final Recognition lhs, final Recognition rhs) {
                                // Intentionally reversed to put high confidence at the head of the queue.
                                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                            }
                        });

        // Scale them back to the input size.
        for (int i = 0; i < outputScores.length; ++i) {
            final RectF detection =
                    new RectF(
                            outputLocations[4 * i + 1] * width,
                            outputLocations[4 * i] * heigh,
                            outputLocations[4 * i + 3] * width,
                            outputLocations[4 * i + 2] * heigh);
            int id = (int) outputClasses[i];

            try {
                pq.add(new Recognition("" + i, jsonObject.getJSONObject(id + "").getString("name"), outputScores[i], detection));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final ArrayList<Recognition> recognitions = new ArrayList<Recognition>();
        for (int i = 0; i < Math.min(pq.size(), MAX_RESULTS); ++i) {
            recognitions.add(pq.poll());
        }
        Trace.endSection(); // "recognizeImage"
        TraceCompat.endSection(); // "recognizeImage"

        return recognitions;
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
    public void enableStatLogging(final boolean logStats) {
        this.logStats = logStats;
    }

    @Override
    public String getStatString() {
        return inferenceInterface.getStatString();
    }

    @Override
    public void close() {
        inferenceInterface.close();
    }
}
