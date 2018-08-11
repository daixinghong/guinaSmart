package com.busradeniz.detection.utils;

import android.graphics.RectF;
import android.os.Trace;
import android.support.v4.os.TraceCompat;

import com.busradeniz.detection.tensorflow.Classifier;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class LocationUtils {


    private static List<Float> mLocationList = new ArrayList<>();
    private static List<Integer> mClassesList = new ArrayList<>();
    private static List<Float> mScoresList = new ArrayList<>();

    public static List<Classifier.Recognition> getLocation(JSONObject baseJson, int width, int heigh) {

        try {
            JSONArray datas = baseJson.getJSONArray("datas");
            JSONObject jsonObject = datas.getJSONObject(0);
            JSONArray box = jsonObject.getJSONObject("data").getJSONArray("boxes");
            JSONArray classes = jsonObject.getJSONObject("data").getJSONArray("classes");
            JSONArray scores = jsonObject.getJSONObject("data").getJSONArray("scores");


            mClassesList.clear();
            mScoresList.clear();
            for (int i = 0; i < classes.length(); i++) {
                int aDouble = (int) classes.getDouble(i);
                mClassesList.add(aDouble);
                mScoresList.add((float) scores.getDouble(i));
            }
            mLocationList.clear();
            for (int i = 0; i < box.length(); i++) {
                mLocationList.add((float) box.getDouble(i));
            }

            final PriorityQueue<Classifier.Recognition> pq =
                    new PriorityQueue<Classifier.Recognition>(
                            1,
                            new Comparator<Classifier.Recognition>() {
                                @Override
                                public int compare(final Classifier.Recognition lhs, final Classifier.Recognition rhs) {
                                    // Intentionally reversed to put high confidence at the head of the queue.
                                    return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                                }
                            });

            for (int i = 0; i < mScoresList.size(); ++i) {
                final RectF detection =
                        new RectF(
                                mLocationList.get(4 * i + 1) * width,
                                mLocationList.get(4 * i) * heigh,
                                mLocationList.get(4 * i + 3) * width,
                                mLocationList.get(4 * i + 2) * heigh);
                int id = mClassesList.get(i);
                pq.add(new Classifier.Recognition("" + i, id + "", mScoresList.get(i), detection));
            }

            final ArrayList<Classifier.Recognition> recognitions = new ArrayList<Classifier.Recognition>();
            for (int i = 0; i < Math.min(pq.size(), mLocationList.size()); ++i) {
                recognitions.add(pq.poll());
            }
            Trace.endSection(); // "recognizeImage"
            TraceCompat.endSection(); // "recognizeImage"

            return recognitions;

        } catch (Exception e) {
            return null;
        }
    }


}
