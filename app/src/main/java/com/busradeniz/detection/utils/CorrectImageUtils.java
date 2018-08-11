package com.busradeniz.detection.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.busradeniz.detection.BaseApplication;
import com.busradeniz.detection.R;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;


public class CorrectImageUtils {


    private static List<double[]> founded_rect_with_points = new ArrayList<>();

    private static final String TAG = "Zang Chaofei";

    private static int kHoughLinesPThreshold = 50;
    private static double kHoughLinesPMinLinLength = 30.0;
    private static double kHoughLinesPMaxLineGap = 3.0;

    private static int kMergeLinesMaxDistance = 5;
    private static boolean is_cross_in_image = false;
    private static int kPointOnLineMaxOffset = 8;

    private static double kIntersectionMinAngle = 45;
    private static double kIntersectionMaxAngle = 135;
    private static double kSameSegmentsMaxAngle = 10;

    private static double kRectOpposingSidesMinRatio = 0.5;
    private static boolean founded_rect = false;
    private static int zoom_ratio = 1;
    private static boolean sStatus;
    private static File sFile;
    private static Bitmap sBitmap2;

    public static Bitmap correctImage(Mat dstbitmap, Bitmap srcBitmap, int resize) {
        founded_rect_with_points = findContours(dstbitmap);

        for (int i = 0; i < founded_rect_with_points.size(); i++) {
            for (int j = 0; j < founded_rect_with_points.get(i).length; j++) {
                founded_rect_with_points.get(i)[j] = founded_rect_with_points.get(i)[j] * zoom_ratio * resize;
            }
        }
        double x, y, height, width;
        //get y value
        y = Math.min(founded_rect_with_points.get(0)[1], founded_rect_with_points.get(1)[1]);
        //get x value
        x = Math.min(founded_rect_with_points.get(0)[0], founded_rect_with_points.get(3)[0]);

        //get height
        height = (Math.max(founded_rect_with_points.get(2)[1], founded_rect_with_points.get(3)[1])) - y;
        width = (Math.max(founded_rect_with_points.get(1)[0], founded_rect_with_points.get(2)[0])) - x;

//        srcBitmap = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.mipmap.src10);

        Bitmap cropBitmap = Bitmap.createBitmap(srcBitmap, (int) x, (int) y, (int) width, (int) height);

        Bitmap fixed_roi_image = rectify_img_roi(cropBitmap, founded_rect_with_points, x, y, 480, 960);

        return fixed_roi_image;
    }


    private static Bitmap rectify_img_roi(Bitmap roi_bitmap, List<double[]> rect_with_points, double x, double y, double markW, double markH) {
//        double[] origin_selected_lu = {rect_with_points.get(0)[0] - x, rect_with_points.get(0)[1] - y};
//        double[] origin_selected_ru = {rect_with_points.get(1)[0] - x, rect_with_points.get(1)[1] - y};
//        double[] origin_selected_rd = {rect_with_points.get(2)[0] - x, rect_with_points.get(2)[1] - y};
//        double[] origin_selected_ld = {rect_with_points.get(3)[0] - x, rect_with_points.get(3)[1] - y};


        double top_edge_length = Math.sqrt(Math.pow(rect_with_points.get(1)[0] - rect_with_points.get(0)[0], 2) + Math.pow(rect_with_points.get(1)[1] - rect_with_points.get(0)[1], 2));
        double right_edge_length = Math.sqrt(Math.pow(rect_with_points.get(2)[0] - rect_with_points.get(1)[0], 2) + Math.pow(rect_with_points.get(2)[1] - rect_with_points.get(1)[1], 2));
        double down_edge_length = Math.sqrt(Math.pow(rect_with_points.get(3)[0] - rect_with_points.get(2)[0], 2) + Math.pow(rect_with_points.get(3)[1] - rect_with_points.get(2)[1], 2));
        double left_edge_length = Math.sqrt(Math.pow(rect_with_points.get(0)[0] - rect_with_points.get(3)[0], 2) + Math.pow(rect_with_points.get(0)[1] - rect_with_points.get(3)[1], 2));

        double[] origin_selected_lu = null;
        double[] origin_selected_ru = null;
        double[] origin_selected_rd = null;
        double[] origin_selected_ld = null;

        if (markH > markW) {
            if ((left_edge_length + right_edge_length) > (top_edge_length + down_edge_length)) {
                origin_selected_lu = new double[]{rect_with_points.get(0)[0] - x, rect_with_points.get(0)[1] - y};
                origin_selected_ru = new double[]{rect_with_points.get(1)[0] - x, rect_with_points.get(1)[1] - y};
                origin_selected_ld = new double[]{rect_with_points.get(3)[0] - x, rect_with_points.get(3)[1] - y};
                origin_selected_rd = new double[]{rect_with_points.get(2)[0] - x, rect_with_points.get(2)[1] - y};
            } else {
                origin_selected_lu = new double[]{rect_with_points.get(3)[0] - x, rect_with_points.get(3)[1] - y};
                origin_selected_ru = new double[]{rect_with_points.get(0)[0] - x, rect_with_points.get(0)[1] - y};
                origin_selected_ld = new double[]{rect_with_points.get(2)[0] - x, rect_with_points.get(2)[1] - y};
                origin_selected_rd = new double[]{rect_with_points.get(1)[0] - x, rect_with_points.get(1)[1] - y};
            }
        } else {
            if ((left_edge_length + right_edge_length) < (top_edge_length + down_edge_length)) {
                origin_selected_lu = new double[]{rect_with_points.get(0)[0] - x, rect_with_points.get(0)[1] - y};
                origin_selected_ru = new double[]{rect_with_points.get(1)[0] - x, rect_with_points.get(1)[1] - y};
                origin_selected_rd = new double[]{rect_with_points.get(2)[0] - x, rect_with_points.get(2)[1] - y};
                origin_selected_ld = new double[]{rect_with_points.get(3)[0] - x, rect_with_points.get(3)[1] - y};
            } else {
                origin_selected_lu = new double[]{rect_with_points.get(3)[0] - x, rect_with_points.get(3)[1] - y};
                origin_selected_ru = new double[]{rect_with_points.get(0)[0] - x, rect_with_points.get(0)[1] - y};
                origin_selected_ld = new double[]{rect_with_points.get(2)[0] - x, rect_with_points.get(2)[1] - y};
                origin_selected_rd = new double[]{rect_with_points.get(1)[0] - x, rect_with_points.get(1)[1] - y};
            }
        }

        List<double[]> origin_selected_conors = new ArrayList<>();

        origin_selected_conors.add(origin_selected_lu);
        origin_selected_conors.add(origin_selected_ru);
        origin_selected_conors.add(origin_selected_rd);
        origin_selected_conors.add(origin_selected_ld);

        double show_width = markW;
        double show_height = markH;

        double[] show_window_lu = {0d, 0};
        double[] show_window_ru = {show_width - 1, 0};
        double[] show_window_ld = {0, show_height - 1};
        double[] show_window_rd = {show_width - 1, show_height - 1};

        List<double[]> show_window_conors = new ArrayList<>();
        show_window_conors.add(show_window_lu);
        show_window_conors.add(show_window_ru);
        show_window_conors.add(show_window_rd);
        show_window_conors.add(show_window_ld);

        Mat roi_mat = new Mat();
        Mat fixed_roi_mat = new Mat();
        Utils.bitmapToMat(roi_bitmap, roi_mat);


        List<Point> listSrcs = java.util.Arrays.asList(new Point(origin_selected_lu[0], origin_selected_lu[1]),
                new Point(origin_selected_ru[0], origin_selected_ru[1]),
                new Point(origin_selected_rd[0], origin_selected_rd[1]),
                new Point(origin_selected_ld[0], origin_selected_ld[1]));

        Mat srcPoints = Converters.vector_Point_to_Mat(listSrcs, CvType.CV_32F);

        List<Point> listDsts = java.util.Arrays.asList(new Point(show_window_lu[0], show_window_lu[1]),
                new Point(show_window_ru[0], show_window_ru[1]),
                new Point(show_window_rd[0], show_window_rd[1]),
                new Point(show_window_ld[0], show_window_ld[1]));
        Mat dstPoints = Converters.vector_Point_to_Mat(listDsts, CvType.CV_32F);


        Mat transform = Imgproc.getPerspectiveTransform(srcPoints, dstPoints);

        Imgproc.warpPerspective(roi_mat, fixed_roi_mat, transform, new Size(markW, markH));


        Bitmap fixed_roi_bitmap = Bitmap.createBitmap((int) markW, (int) markH, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(fixed_roi_mat, fixed_roi_bitmap);
        return fixed_roi_bitmap;

    }

    private static int get_zoom_ration(int height, int width) {
        int ratio;
        if (Math.max(height, width) <= 1000) {
            ratio = 1;
        } else if (Math.max(height, width) > 1000 && Math.max(height, width) <= 1000 * 2) {
            ratio = 2;
        } else if (Math.max(height, width) > 1000 * 2 && Math.max(height, width) <= 1000 * 4) {
            ratio = 4;
        } else if (Math.max(height, width) > 1000 * 4 && Math.max(height, width) <= 1000 * 8) {
            ratio = 8;
        } else if (Math.max(height, width) > 1000 * 8 && Math.max(height, width) <= 1000 * 16) {
            ratio = 16;
        } else {
            ratio = 16;
        }
        return ratio;

    }

    private static Mat fix_image_to_detect_edge(Mat bitmap) {
        zoom_ratio = get_zoom_ration(bitmap.rows(), bitmap.cols());
        Mat det = new Mat();
        bitmap.convertTo(bitmap, CvType.CV_8UC3);
        Imgproc.resize(bitmap, det, new Size(bitmap.cols() / zoom_ratio, bitmap.rows() / zoom_ratio));
        return det;
    }


    public static List<double[]> findContours(Mat bitmap) {

        boolean isDebug = false;
        Mat edgesMat = new Mat();
        Mat origin_lines = new Mat();
        Mat hedMat = new Mat();
        if (isDebug) {
            Bitmap hedBitmap = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.mipmap.test10);
            Utils.bitmapToMat(hedBitmap, hedMat);
            hedMat = fix_image_to_detect_edge(hedMat);
        } else {
            hedMat = fix_image_to_detect_edge(bitmap);
        }

        sBitmap2 = Bitmap.createBitmap(hedMat.cols(), hedMat.rows(), Bitmap.Config.ARGB_4444);
        Imgproc.Canny(hedMat, edgesMat, 50, 3 * 50, 3, true);
        Imgproc.HoughLinesP(edgesMat, origin_lines, 1, Math.PI / 180,
                kHoughLinesPThreshold, kHoughLinesPMinLinLength, kHoughLinesPMaxLineGap);

        List<List<double[]>> segment_line_and_reference_line_pairs = new ArrayList<>();
        List<double[]> arraged_original_lines = new ArrayList<>();


        Utils.matToBitmap(hedMat, sBitmap2);
        long l = System.currentTimeMillis();

//        try {
//            File fileDir = new File("/sdcard/image/" + l + ".png");
//            fileDir.createNewFile();
//
//            FileOutputStream out = new FileOutputStream(fileDir);
//            sBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//
//        }

        for (int i = 0; i < origin_lines.rows(); i++) {

            double[] original_segment_line = new double[5];
            if (origin_lines.get(i, 0)[0] <= origin_lines.get(i, 0)[2]) {
                original_segment_line[0] = origin_lines.get(i, 0)[0];
                original_segment_line[1] = origin_lines.get(i, 0)[1];
                original_segment_line[2] = origin_lines.get(i, 0)[2];
                original_segment_line[3] = origin_lines.get(i, 0)[3];
                original_segment_line[4] = 0;
            } else {
                original_segment_line[0] = origin_lines.get(i, 0)[2];
                original_segment_line[1] = origin_lines.get(i, 0)[3];
                original_segment_line[2] = origin_lines.get(i, 0)[0];
                original_segment_line[3] = origin_lines.get(i, 0)[1];
                original_segment_line[4] = 0;
            }

            arraged_original_lines.add(original_segment_line);
//            Imgproc.line(hedMat, new Point(origin_lines.get(i, 0)[0], origin_lines.get(i, 0)[1]),
//                    new Point(origin_lines.get(i, 0)[2], origin_lines.get(i, 0)[3]),
//                    new Scalar(255, 0, 0), 2);


            Random random = new Random();
            int i1 = random.nextInt(70);
            Imgproc.putText(hedMat, i + "", new Point(origin_lines.get(i, 0)[0] + i1, origin_lines.get(i, 0)[1]), 1, 1, new Scalar(255, 255, 0));

        }
        Utils.matToBitmap(hedMat, sBitmap2);

        Collections.sort(arraged_original_lines, new Comparator<double[]>() {
            @Override
            public int compare(double[] px1, double[] px2) {
                if (px1[0] >= px2[0]) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        for (int i = 0; i < arraged_original_lines.size(); i++) {
            double[] original_segment_line = new double[6];
            List<double[]> segment_line_and_reference_line_pair = new ArrayList<>();

            Log.d("original_lines", Arrays.toString(arraged_original_lines.get(i)));
            original_segment_line[0] = arraged_original_lines.get(i)[0];
            original_segment_line[1] = arraged_original_lines.get(i)[1];
            original_segment_line[2] = arraged_original_lines.get(i)[2];
            original_segment_line[3] = arraged_original_lines.get(i)[3];
            original_segment_line[4] = 0;

            double[] reference_line = get_ref_line(original_segment_line, edgesMat.cols(), edgesMat.rows());
            original_segment_line[4] = reference_line[4];


            segment_line_and_reference_line_pair.add(reference_line);
            segment_line_and_reference_line_pair.add(original_segment_line);

            segment_line_and_reference_line_pairs.add(segment_line_and_reference_line_pair);


//            Imgproc.line(hedMat, new Point(reference_line[0], reference_line[1]),
//                    new Point(reference_line[2], reference_line[3]),
//                    new Scalar(255, 0, 0), 1);


//
//            Imgproc.line(hedMat, new Point(original_segment_line[0], original_segment_line[1]),
//                    new Point(original_segment_line[2], original_segment_line[3]),
//                    new Scalar(0, 0, 255), 2);
        }

//        Utils.matToBitmap(hedMat, sBitmap2);

        //<4>下面合并临近的线段
        List<List<double[]>> merged_reference_line_and_segment_pairs = new ArrayList<>();
        merged_reference_line_and_segment_pairs = merge_ref_line_segment_pairs(segment_line_and_reference_line_pairs,
                hedMat, hedMat.cols(), hedMat.rows());
        Collections.sort(merged_reference_line_and_segment_pairs, new Comparator<List<double[]>>() {
            @Override
            public int compare(List<double[]> px1, List<double[]> px2) {
                if (px1.get(0)[5] <= px2.get(0)[5]) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        int total_num = merged_reference_line_and_segment_pairs.size();

        for (int i = 0; i < total_num - 4; i++) {
            merged_reference_line_and_segment_pairs.remove(merged_reference_line_and_segment_pairs.size() - 1);
        }

        //for debug
        Point point1 = new Point();
        Point point2 = new Point();

        for (int i = 0; i < merged_reference_line_and_segment_pairs.size(); i++) {

            point1.x = merged_reference_line_and_segment_pairs.get(i).get(0)[0];
            point1.y = merged_reference_line_and_segment_pairs.get(i).get(0)[1];

            point2.x = merged_reference_line_and_segment_pairs.get(i).get(0)[2];
            point2.y = merged_reference_line_and_segment_pairs.get(i).get(0)[3];

            Imgproc.line(hedMat, point1,
                    point2,
                    new Scalar(255, 0, 0), 4);

        }

        Utils.matToBitmap(hedMat, sBitmap2);

        double[] intersection;

        List<double[]> corners = new ArrayList<>();
        if (merged_reference_line_and_segment_pairs.size() >= 4) {
            for (int i = 0; i < merged_reference_line_and_segment_pairs.size(); i++) {
                for (int j = i + 1; j < merged_reference_line_and_segment_pairs.size(); j++) {
                    intersection = get_segment_intersection(merged_reference_line_and_segment_pairs.get(i).get(0),
                            merged_reference_line_and_segment_pairs.get(j).get(0));
//                    if(is_cross_in_image){
//                        all_corners.add(intersection);
//                    }
                    if (is_cross_in_image && intersection[0] >= 0 && intersection[0] <= hedMat.cols() &&
                            intersection[1] >= 0 && intersection[1] <= hedMat.rows()) {
                        double theta_a = get_angle_of_line(merged_reference_line_and_segment_pairs.get(i).get(0));
                        double theta_b = get_angle_of_line(merged_reference_line_and_segment_pairs.get(j).get(0));
                        double angle = Math.abs(theta_a - theta_b);
                        angle = angle % 180;
                        if (angle >= kIntersectionMinAngle && angle <= kIntersectionMaxAngle) {
                            //前两位表示交叉点的x,y值
                            double[] corner = new double[22];
                            corner[0] = intersection[0];
                            corner[1] = intersection[1];
                            //2-6表示第一条交叉线的referce_line
                            corner[2] = merged_reference_line_and_segment_pairs.get(i).get(0)[0];
                            corner[3] = merged_reference_line_and_segment_pairs.get(i).get(0)[1];
                            corner[4] = merged_reference_line_and_segment_pairs.get(i).get(0)[2];
                            corner[5] = merged_reference_line_and_segment_pairs.get(i).get(0)[3];
                            corner[6] = merged_reference_line_and_segment_pairs.get(i).get(0)[4];
                            //7-11表示第一条加查县的segment_line
                            corner[7] = merged_reference_line_and_segment_pairs.get(i).get(1)[0];
                            corner[8] = merged_reference_line_and_segment_pairs.get(i).get(1)[1];
                            corner[9] = merged_reference_line_and_segment_pairs.get(i).get(1)[2];
                            corner[10] = merged_reference_line_and_segment_pairs.get(i).get(1)[3];
                            corner[11] = merged_reference_line_and_segment_pairs.get(i).get(1)[4];


                            //2-6表示第一条交叉线的referce_line
                            corner[12] = merged_reference_line_and_segment_pairs.get(j).get(0)[0];
                            corner[13] = merged_reference_line_and_segment_pairs.get(j).get(0)[1];
                            corner[14] = merged_reference_line_and_segment_pairs.get(j).get(0)[2];
                            corner[15] = merged_reference_line_and_segment_pairs.get(j).get(0)[3];
                            corner[16] = merged_reference_line_and_segment_pairs.get(j).get(0)[4];

                            //7-11表示第一条加查县的segment_line
                            corner[17] = merged_reference_line_and_segment_pairs.get(j).get(1)[0];
                            corner[18] = merged_reference_line_and_segment_pairs.get(j).get(1)[1];
                            corner[19] = merged_reference_line_and_segment_pairs.get(j).get(1)[2];
                            corner[10] = merged_reference_line_and_segment_pairs.get(j).get(1)[3];
                            corner[21] = merged_reference_line_and_segment_pairs.get(j).get(1)[4];

                            corners.add(corner);
                        }
                    }

                }
            }
        }

        Log.e(TAG, "findContours: 交点数量" + corners.size());
        //下面debug交叉点的代码
        for (int i = 0; i < corners.size(); i++) {
            double x = corners.get(i)[0];
            double y = corners.get(i)[1];
            Imgproc.circle(hedMat, new Point(x, y), 5, new Scalar(0, 255, 0), 2);
        }


        Utils.matToBitmap(hedMat, sBitmap2);


        //<6>寻找四边形
        //List<double[]> rect_corner = new ArrayList<>();
        List<double[]> arranged_rect_corner = new ArrayList<>();
        List<double[]> rect_corners = new ArrayList<>();
        List<double[]> arranged_rect_corners = new ArrayList<>();
        List<double[]> rect_points = new ArrayList<>();
        List<double[]> rect_points_with_max_perimeter = new ArrayList<>();
        double perimeter = 0;
        double max_perimeter = 0;


        int corners_size = corners.size();
        if (corners_size >= 4) {
            for (int i = 0; i < corners_size; i++) {

                for (int j = i + 1; j < corners_size; j++) {
                    for (int m = j + 1; m < corners_size; m++) {
                        for (int n = m + 1; n < corners_size; n++) {
                            rect_corners.clear();
                            rect_corners.add(corners.get(i));
                            rect_corners.add(corners.get(j));
                            rect_corners.add(corners.get(m));
                            rect_corners.add(corners.get(n));
                            arranged_rect_corners = arrange_rect_corners(rect_corners);
                            if (is_rect_corners_reasonable(arranged_rect_corners) == false) {
                                continue;
                            }
                            rect_points.clear();
                            rect_points.add(Arrays.copyOfRange(arranged_rect_corners.get(0), 0, 2));
                            rect_points.add(Arrays.copyOfRange(arranged_rect_corners.get(1), 0, 2));
                            rect_points.add(Arrays.copyOfRange(arranged_rect_corners.get(2), 0, 2));
                            rect_points.add(Arrays.copyOfRange(arranged_rect_corners.get(3), 0, 2));


                            List<Point> pointList = new ArrayList<>();

                            for (int a = 0; a < rect_points.size(); a++) {
                                Point point = new Point();
                                point.x = rect_points.get(a)[0];
                                point.y = rect_points.get(a)[1];
                                pointList.add(point);
                            }
                            MatOfPoint mp1 = new MatOfPoint();
                            mp1.fromList(pointList);
                            perimeter = Imgproc.contourArea(mp1);

                            if (perimeter > max_perimeter) {
                                max_perimeter = perimeter;
                                rect_points_with_max_perimeter = rect_points;
                            }

                        }
                    }
                }
            }

            if (rect_points_with_max_perimeter.size() == 0) {
                founded_rect = false;
            }


        }


        //


        //Utils.matToBitmap(hedMat, debugBitmap);
        //img.setImageBitmap(debugBitmap);

        return rect_points_with_max_perimeter;

    }


    /**
     * 一组策略，判断4个 corner 是否可以形成一个可信度高的矩形(有透视变换效果，所以肯定不是标准的长方形，而是一个梯形或平行四边形)
     * 4个 point 是已经经过ArrangeRectPoints排过序的
     * 4 points top-left, top-right, bottom-right, bottom-left, index are 0, 1, 2, 3
     *
     * @param
     * @return
     */
    private static boolean is_rect_corners_reasonable(List<double[]> arranged_rect_points) {
        assert (arranged_rect_points.size() == 4);
        List<double[]> rect_points = new ArrayList<>();

        List<List<double[]>> rect_segments = new ArrayList<>();
        List<double[]> point0_segments_reflines = new ArrayList<>();
        List<double[]> point1_segments_reflines = new ArrayList<>();
        List<double[]> point2_segments_reflines = new ArrayList<>();
        List<double[]> point3_segments_reflines = new ArrayList<>();
        double[] point = new double[2];
        rect_points.add(Arrays.copyOfRange(arranged_rect_points.get(0), 0, 2));
        rect_points.add(Arrays.copyOfRange(arranged_rect_points.get(1), 0, 2));
        rect_points.add(Arrays.copyOfRange(arranged_rect_points.get(2), 0, 2));
        rect_points.add(Arrays.copyOfRange(arranged_rect_points.get(3), 0, 2));


        //segment_line_0: point 0--> 1
        List<double[]> rect_segment0 = new ArrayList<>();
        rect_segment0.add(rect_points.get(0));
        rect_segment0.add(rect_points.get(1));
        rect_segments.add(rect_segment0);

        //segment_line_0: point 1--> 2
        List<double[]> rect_segment1 = new ArrayList<>();
        rect_segment1.add(rect_points.get(1));
        rect_segment1.add(rect_points.get(2));
        rect_segments.add(rect_segment1);

        //segment_line_0: point 2--> 3
        List<double[]> rect_segment2 = new ArrayList<>();
        rect_segment2.add(rect_points.get(1));
        rect_segment2.add(rect_points.get(2));
        rect_segments.add(rect_segment2);

        //segment_line_0: point 3--> 0
        List<double[]> rect_segment3 = new ArrayList<>();
        rect_segment3.add(rect_points.get(1));
        rect_segment3.add(rect_points.get(2));
        rect_segments.add(rect_segment3);

        point0_segments_reflines.add(Arrays.copyOfRange(arranged_rect_points.get(0), 2, 12));
        point0_segments_reflines.add(Arrays.copyOfRange(arranged_rect_points.get(0), 12, 22));

        point1_segments_reflines.add(Arrays.copyOfRange(arranged_rect_points.get(1), 2, 12));
        point1_segments_reflines.add(Arrays.copyOfRange(arranged_rect_points.get(1), 12, 22));

        point2_segments_reflines.add(Arrays.copyOfRange(arranged_rect_points.get(2), 2, 12));
        point2_segments_reflines.add(Arrays.copyOfRange(arranged_rect_points.get(2), 12, 22));

        point3_segments_reflines.add(Arrays.copyOfRange(arranged_rect_points.get(0), 2, 12));
        point3_segments_reflines.add(Arrays.copyOfRange(arranged_rect_points.get(0), 12, 22));


        double distance_of_0_to_1 = points_distance(rect_points.get(0), rect_points.get(1));
        double distance_of_1_to_2 = points_distance(rect_points.get(1), rect_points.get(2));
        double distance_of_2_to_3 = points_distance(rect_points.get(2), rect_points.get(3));
        double distance_of_3_to_0 = points_distance(rect_points.get(3), rect_points.get(0));

        double ratio1 = Math.min(distance_of_0_to_1, distance_of_2_to_3) / Math.max(distance_of_0_to_1, distance_of_2_to_3);
        double ratio2 = Math.min(distance_of_1_to_2, distance_of_3_to_0) / Math.max(distance_of_1_to_2, distance_of_3_to_0);

        if ((ratio1 >= kRectOpposingSidesMinRatio) && (ratio2 >= kRectOpposingSidesMinRatio)) {
            double angle_top = get_angle_of_two_points(rect_points.get(1), rect_points.get(0));
            double angle_bottom = get_angle_of_two_points(rect_points.get(2), rect_points.get(3));
            double angle_right = get_angle_of_two_points(rect_points.get(2), rect_points.get(1));
            double angle_left = get_angle_of_two_points(rect_points.get(3), rect_points.get(0));

            double diff1 = Math.abs(angle_top - angle_bottom);
            double diff2 = Math.abs(angle_right - angle_left);
            diff1 = diff1 % 90;
            diff2 = diff2 % 90; //修正到0~90度

            //# 这里的几个值，都是经验值
            if (diff1 <= 8 && diff2 <= 8) {
                //#俯视拍摄，平行四边形
                return true;
            }

            if (diff1 <= 8 && diff2 <= 45) {
                //#梯形，有透视效果
                return true;
            }

            if (diff1 <= 45 && diff2 <= 8) {
                //# 梯形，有透视效果
                return true;
            }

        }

        return false;
    }


    private static double get_angle_of_two_points(double[] point_a, double[] point_b) {
        double angle = Math.atan2(point_b[1] - point_a[1], point_b[0] - point_a[0]) * 180.0 / Math.PI;
        double fix_angle = (angle + 360) % 360;
        assert (fix_angle >= 0);
        assert (fix_angle <= 360);
        return fix_angle;

    }

    private static double points_distance(double[] pointa, double[] pointb) {
        double x_distance = pointa[0] - pointb[0];
        double y_distance = pointa[1] - pointb[1];

        return Math.sqrt(x_distance * x_distance + y_distance * y_distance);
    }

    private static boolean is_segments_has_same_segment(List<double[]> segment_reflines, List<double[]> segment) {
        double[] sega = new double[4];
        double[] segb = new double[4];
        sega[0] = segment_reflines.get(0)[0];
        sega[1] = segment_reflines.get(0)[1];
        sega[2] = segment_reflines.get(1)[0];
        sega[3] = segment_reflines.get(1)[1];

        segb[0] = segment.get(0)[0];
        segb[1] = segment.get(0)[1];
        segb[2] = segment.get(1)[0];
        segb[3] = segment.get(1)[1];
        for (int i = 0; i < segment_reflines.size(); i++) {
            sega = Arrays.copyOfRange(segment_reflines.get(i), 0, 4);
            double angle_a = get_angle_of_line(sega);
            double angle_b = get_angle_of_line(segb);
            double diff = Math.abs(angle_a - angle_b);
            diff = diff % 90;
            if (diff < kSameSegmentsMaxAngle) {
                return true;
            }
        }

        return false;

    }


    private static List<double[]> arrange_rect_corners(List<double[]> rect_corners) {
        assert (rect_corners.size() == 4);
        List<double[]> four_points = new ArrayList<>();
        List<double[]> left_points = new ArrayList<>();
        List<double[]> right_points = new ArrayList<>();
        List<double[]> sorted_points = new ArrayList<>();
        four_points.clear();
        four_points.addAll(rect_corners);

        Collections.sort(four_points, new Comparator<double[]>() {
            @Override
            public int compare(double[] px1, double[] px2) {
                if (px1[0] >= px2[0]) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        left_points.add(four_points.get(0));
        left_points.add(four_points.get(1));

        right_points.add(four_points.get(2));
        right_points.add(four_points.get(3));

        Collections.sort(left_points, new Comparator<double[]>() {
            @Override
            public int compare(double[] px1, double[] px2) {
                if (px1[1] >= px2[1]) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        Collections.sort(right_points, new Comparator<double[]>() {
            @Override
            public int compare(double[] px1, double[] px2) {
                if (px1[1] >= px2[1]) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        sorted_points.add(left_points.get(0));
        sorted_points.add(right_points.get(0));
        sorted_points.add(right_points.get(1));
        sorted_points.add(left_points.get(1));

        return sorted_points;

    }

    private static double get_angle_of_line(double[] line) {
        double x1 = line[0];
        double y1 = line[1];

        double x2 = line[2];
        double y2 = line[3];

        double angle = Math.atan2(y2 - y1, x2 - x1) * 180.0 / Math.PI;
        double fix_angle = (angle + 360) % 360;
        assert (fix_angle >= 0);
        assert (fix_angle <= 360);
        return fix_angle;
    }


    private static double[] cross(double[] a, double[] b) {
        double[] result = new double[3];
        result[0] = a[1] * b[2] - a[2] * b[1];
        result[1] = a[2] * b[0] - a[0] * b[2];
        result[2] = a[0] * b[1] - a[1] * b[0];
        return result;
    }


    private static double[] get_segment_intersection(double[] ref_line_a, double[] ref_line_b) {
        double[] pa = new double[3];
        pa[0] = ref_line_a[0];
        pa[1] = ref_line_a[1];
        pa[2] = 1;

        double[] pb = new double[3];
        pb[0] = ref_line_a[2];
        pb[1] = ref_line_a[3];
        pb[2] = 1;

        double[] la = new double[3];

        la = cross(pa, pb);

        double[] pc = new double[3];
        pc[0] = ref_line_b[0];
        pc[1] = ref_line_b[1];
        pc[2] = 1;

        double[] pd = new double[3];
        pd[0] = ref_line_b[2];
        pd[1] = ref_line_b[3];
        pd[2] = 1;

        double[] lb = new double[3];

        lb = cross(pc, pd);

        double[] inter = new double[3];
        inter = cross(la, lb);

        double[] intersection = {-1d, -1d};

        if (inter[2] == 0) {
            is_cross_in_image = false;
            return intersection;
        } else {
            intersection[0] = inter[0] / inter[2];
            intersection[1] = inter[1] / inter[2];
            if (is_point_on_line(intersection, ref_line_a) &&
                    is_point_on_line(intersection, ref_line_b)) {
                is_cross_in_image = true;
                return intersection;
            } else {
                is_cross_in_image = false;
                return intersection;
            }


        }


    }

    private static boolean is_point_on_line(double[] point, double[] line) {
        double[] p0 = {0, 0};
        double[] p1 = {0, 0};
        p0[0] = line[0];
        p0[1] = line[1];
        p1[0] = line[2];
        p1[1] = line[3];

        double min_x = Math.min(p0[0], p1[0]) - kPointOnLineMaxOffset;
        double max_x = Math.max(p0[0], p1[0]) + kPointOnLineMaxOffset;
        double min_y = Math.min(p0[1], p1[1]) - kPointOnLineMaxOffset;
        double max_y = Math.max(p0[1], p1[1]) + kPointOnLineMaxOffset;

        if (point[0] >= min_x &&
                point[0] <= max_x &&
                point[1] >= min_y &&
                point[1] <= max_y) {
            return true;
        } else {
            return false;
        }
    }

    private static List<List<double[]>> merge_ref_line_segment_pairs(List<List<double[]>> before_segment_line_and_reference_line_pairs,
                                                                     Mat hedMat, int imageWidth, int imageHeight) {
        Bitmap debugBitmap1 = Bitmap.createBitmap(hedMat.cols(), hedMat.rows(), Bitmap.Config.RGB_565);
        List<List<double[]>> merged_ref_line_and_segment_pairs = new ArrayList<>();
        for (int i = 0; i < before_segment_line_and_reference_line_pairs.size(); i++) {
            Boolean is_close = false;
            for (int j = 0; j < merged_ref_line_and_segment_pairs.size(); j++) {
                //List<double[]> current_segment_line_and_reference_line_pair = before_segment_line_and_reference_line_pairs.get(i);
                //List<double[]> current_merged_ref_line_and_segment_pair = merged_ref_line_and_segment_pairs.get(j);
                if (is_two_ref_line_close_to_eachother(before_segment_line_and_reference_line_pairs.get(i),
                        merged_ref_line_and_segment_pairs.get(j),
                        imageWidth, imageHeight) == true) {

//                    //使用绿色打印原来的两个线段
//                    Point point1 = new Point();
//                    Point point2 = new Point();
//
//                    point1.x = before_segment_line_and_reference_line_pairs.get(i).get(1)[0];
//                    point1.y = before_segment_line_and_reference_line_pairs.get(i).get(1)[1];
//
//                    point2.x = before_segment_line_and_reference_line_pairs.get(i).get(1)[2];
//                    point2.y = before_segment_line_and_reference_line_pairs.get(i).get(1)[3];
//
//                    Imgproc.line(hedMat, point1,
//                            point2,
//                            new Scalar(0, 255, 0), 1);

//                    //使用红色打印原来的合并线段
//
//                    Point point3 = new Point();
//                    Point point4 = new Point();
//
//                    point3.x = merged_ref_line_and_segment_pairs.get(j).get(1)[0];
//                    point3.y = merged_ref_line_and_segment_pairs.get(j).get(1)[1];
//
//                    point4.x = merged_ref_line_and_segment_pairs.get(j).get(1)[2];
//                    point4.y = merged_ref_line_and_segment_pairs.get(j).get(1)[3];
//
//                    Imgproc.line(hedMat, point3,
//                            point4,
//                            new Scalar(255, 0, 0), 1);
//

                    List<double[]> new_merged_ref_line_and_segment_pair = start_merging(before_segment_line_and_reference_line_pairs.get(i),
                            merged_ref_line_and_segment_pairs.get(j),
                            imageWidth, imageHeight);

//                    //使用蓝色打印原来的两个线段
//
//                    Point point5 = new Point();
//                    Point point6 = new Point();
//
//                    point5.x = new_merged_ref_line_and_segment_pair.get(1)[0];
//                    point5.y = new_merged_ref_line_and_segment_pair.get(1)[1];
//
//                    point6.x = new_merged_ref_line_and_segment_pair.get(1)[2];
//                    point6.y = new_merged_ref_line_and_segment_pair.get(1)[3];
//
//                    Imgproc.line(hedMat, point5,
//                            point6,
//                            new Scalar(0, 0, 255), 2);
//
//                    Utils.matToBitmap(hedMat, debugBitmap1);
//                    img.setImageBitmap(debugBitmap1);

                    merged_ref_line_and_segment_pairs.set(j, new_merged_ref_line_and_segment_pair);

                    is_close = true;
                    break;
                }

            }


            if (!is_close) {
                merged_ref_line_and_segment_pairs.add(before_segment_line_and_reference_line_pairs.get(i));
            }
        }


        return merged_ref_line_and_segment_pairs;

    }

    //合并的前提是，第一，合并的线段一定在左边，第二，合并的线段，一点是前两个点在左边。
    //结果：更新index位置的merged线段
    private static List<double[]> start_merging(List<double[]> new_add__ref_segment,
                                                List<double[]> merged__ref_segment,
                                                int imageWidth, int imageHeight) {
        int old_merged_times = (int) merged__ref_segment.get(1)[5];
        double[] adding_segment_line = new_add__ref_segment.get(1);
        double[] main_merged_segment_line = merged__ref_segment.get(1);

        double[] new_segment_line = new double[6];


        List<Double> point_0 = new ArrayList<>();
        List<Double> point_1 = new ArrayList<>();
        List<Double> point_2 = new ArrayList<>();
        List<Double> point_3 = new ArrayList<>();
        List<List<Double>> four_points = new ArrayList<>();

        List<List<Double>> left_points = new ArrayList<>();
        List<List<Double>> right_points = new ArrayList<>();

        point_0.add(adding_segment_line[0]);
        point_0.add(adding_segment_line[1]);
        four_points.add(point_0);

        point_1.add(adding_segment_line[2]);
        point_1.add(adding_segment_line[3]);
        four_points.add(point_1);

        point_2.add(main_merged_segment_line[0]);
        point_2.add(main_merged_segment_line[1]);
        four_points.add(point_2);

        point_3.add(main_merged_segment_line[2]);
        point_3.add(main_merged_segment_line[3]);
        four_points.add(point_3);


        List<double[]> new_segment_line_and_reference_line_pair = new ArrayList<>();


        //对于1和1\3\5\6\7\9\10的组合，我们认为这是类垂直线的组合，取y轴的最大值和最小值组合就可以了。
        if ((adding_segment_line[4] == 1 && main_merged_segment_line[4] == 1) ||
                (adding_segment_line[4] == 1 && main_merged_segment_line[4] == 3) ||
                (adding_segment_line[4] == 3 && main_merged_segment_line[4] == 1) ||
                (adding_segment_line[4] == 1 && main_merged_segment_line[4] == 5) ||
                (adding_segment_line[4] == 5 && main_merged_segment_line[4] == 1) ||
                (adding_segment_line[4] == 1 && main_merged_segment_line[4] == 6) ||
                (adding_segment_line[4] == 6 && main_merged_segment_line[4] == 1) ||
                (adding_segment_line[4] == 1 && main_merged_segment_line[4] == 7) ||
                (adding_segment_line[4] == 7 && main_merged_segment_line[4] == 1) ||
                (adding_segment_line[4] == 1 && main_merged_segment_line[4] == 9) ||
                (adding_segment_line[4] == 9 && main_merged_segment_line[4] == 1) ||
                (adding_segment_line[4] == 1 && main_merged_segment_line[4] == 10) ||
                (adding_segment_line[4] == 10 && main_merged_segment_line[4] == 1)) {

            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(1) >= px2.get(1)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = four_points.get(0).get(0);
            new_segment_line[1] = four_points.get(0).get(1);

            new_segment_line[2] = four_points.get(3).get(0);
            new_segment_line[3] = four_points.get(3).get(1);

            new_segment_line[4] = 0;

        }

        //对于2和2\3\4\6\7\8\10的组合，我们认为这是类垂直线的组合，取x轴的最大值和最小值组合就可以了。
        if ((adding_segment_line[4] == 2 && main_merged_segment_line[4] == 2) ||
                (adding_segment_line[4] == 2 && main_merged_segment_line[4] == 3) ||
                (adding_segment_line[4] == 3 && main_merged_segment_line[4] == 2) ||
                (adding_segment_line[4] == 2 && main_merged_segment_line[4] == 4) ||
                (adding_segment_line[4] == 4 && main_merged_segment_line[4] == 2) ||
                (adding_segment_line[4] == 2 && main_merged_segment_line[4] == 6) ||
                (adding_segment_line[4] == 6 && main_merged_segment_line[4] == 2) ||
                (adding_segment_line[4] == 2 && main_merged_segment_line[4] == 7) ||
                (adding_segment_line[4] == 7 && main_merged_segment_line[4] == 2) ||
                (adding_segment_line[4] == 2 && main_merged_segment_line[4] == 8) ||
                (adding_segment_line[4] == 8 && main_merged_segment_line[4] == 2) ||
                (adding_segment_line[4] == 2 && main_merged_segment_line[4] == 10) ||
                (adding_segment_line[4] == 10 && main_merged_segment_line[4] == 2)) {

            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(0) >= px2.get(0)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = four_points.get(0).get(0);
            new_segment_line[1] = four_points.get(0).get(1);

            new_segment_line[2] = four_points.get(3).get(0);
            new_segment_line[3] = four_points.get(3).get(1);

            new_segment_line[4] = 0;

        }

        //对于3,4,5,6的任意组合，我们只需要取左边最下点，和右边最上点。
        List<Double> aPosType = new ArrayList<>();
        aPosType.add(3d);
        aPosType.add(4d);
        aPosType.add(5d);
        aPosType.add(6d);
        if (aPosType.contains(adding_segment_line[4]) && aPosType.contains(main_merged_segment_line[4])) {
            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(0) >= px2.get(0)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            left_points.add(four_points.get(0));
            left_points.add(four_points.get(1));

            right_points.add(four_points.get(2));
            right_points.add(four_points.get(3));

            Collections.sort(left_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(1) >= px2.get(1)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            Collections.sort(right_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(1) >= px2.get(1)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });


            new_segment_line[0] = left_points.get(0).get(0);
            new_segment_line[1] = left_points.get(0).get(1);

            new_segment_line[2] = right_points.get(1).get(0);
            new_segment_line[3] = right_points.get(1).get(1);

            new_segment_line[4] = 0;
        }

        //对于7,8,9,10的任意组合，我们只需要取左边最上点，和右边最下点。
        List<Double> aNegType = new ArrayList<>();
        aNegType.add(7d);
        aNegType.add(8d);
        aNegType.add(9d);
        aNegType.add(10d);

        if (aNegType.contains(adding_segment_line[4]) && aNegType.contains(main_merged_segment_line[4])) {

            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(0) >= px2.get(0)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            left_points.add(four_points.get(0));
            left_points.add(four_points.get(1));

            right_points.add(four_points.get(2));
            right_points.add(four_points.get(3));

            Collections.sort(left_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(1) >= px2.get(1)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            Collections.sort(right_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(1) >= px2.get(1)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = left_points.get(1).get(0);
            new_segment_line[1] = left_points.get(1).get(1);

            new_segment_line[2] = right_points.get(0).get(0);
            new_segment_line[3] = right_points.get(0).get(1);

            new_segment_line[4] = 0;
        }

        //下面只需要讨论几种特殊的组合
        //3 and 7\8\10
        //4 and 7\8\10
        //5 and 7\9\10
        //6 and 8\9\10

        //3 and 7，这两条线如果能组合，表明是近垂直线，取y轴最大和最小值的点
        if ((adding_segment_line[4] == 3 && main_merged_segment_line[4] == 7) ||
                (adding_segment_line[4] == 7 && main_merged_segment_line[4] == 3)) {
            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(1) >= px2.get(1)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = four_points.get(0).get(0);
            new_segment_line[1] = four_points.get(0).get(1);

            new_segment_line[2] = four_points.get(3).get(0);
            new_segment_line[3] = four_points.get(3).get(1);

            new_segment_line[4] = 0;
        }

        //3 and 8，这两条线如果能组合，表明是近水平线，取x轴最大和最小值的点
        if ((adding_segment_line[4] == 3 && main_merged_segment_line[4] == 8) ||
                (adding_segment_line[4] == 8 && main_merged_segment_line[4] == 3)) {
            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(0) >= px2.get(0)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = four_points.get(0).get(0);
            new_segment_line[1] = four_points.get(0).get(1);

            new_segment_line[2] = four_points.get(3).get(0);
            new_segment_line[3] = four_points.get(3).get(1);

            new_segment_line[4] = 0;
        }

        //3 and 10，这两条线如果能组合，表明是近水平线，取x轴最大和最小值的点
        if ((adding_segment_line[4] == 3 && main_merged_segment_line[4] == 10) ||
                (adding_segment_line[4] == 10 && main_merged_segment_line[4] == 3)) {
            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(0) >= px2.get(0)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = four_points.get(0).get(0);
            new_segment_line[1] = four_points.get(0).get(1);

            new_segment_line[2] = four_points.get(3).get(0);
            new_segment_line[3] = four_points.get(3).get(1);

            new_segment_line[4] = 0;
        }

        //4 and 7，这两条线如果能组合，表明是近水平线，取x轴最大和最小值的点
        if ((adding_segment_line[4] == 4 && main_merged_segment_line[4] == 7) ||
                (adding_segment_line[4] == 7 && main_merged_segment_line[4] == 4)) {
            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(0) >= px2.get(0)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = four_points.get(0).get(0);
            new_segment_line[1] = four_points.get(0).get(1);

            new_segment_line[2] = four_points.get(3).get(0);
            new_segment_line[3] = four_points.get(3).get(1);

            new_segment_line[4] = 0;
        }

        //4 and 8，这两条线如果能组合，表明是近水平线，取x轴最大和最小值的点
        if ((adding_segment_line[4] == 4 && main_merged_segment_line[4] == 8) ||
                (adding_segment_line[4] == 8 && main_merged_segment_line[4] == 4)) {
            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(0) >= px2.get(0)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = four_points.get(0).get(0);
            new_segment_line[1] = four_points.get(0).get(1);

            new_segment_line[2] = four_points.get(3).get(0);
            new_segment_line[3] = four_points.get(3).get(1);

            new_segment_line[4] = 0;
        }

        //4 and 10，这两条线如果能组合，表明是近水平线，取x轴最大和最小值的点
        if ((adding_segment_line[4] == 4 && main_merged_segment_line[4] == 10) ||
                (adding_segment_line[4] == 10 && main_merged_segment_line[4] == 4)) {
            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(0) >= px2.get(0)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = four_points.get(0).get(0);
            new_segment_line[1] = four_points.get(0).get(1);

            new_segment_line[2] = four_points.get(3).get(0);
            new_segment_line[3] = four_points.get(3).get(1);

            new_segment_line[4] = 0;
        }

        //5 and 7，这两条线如果能组合，表明是近垂直线，取y轴最大和最小值的点
        if ((adding_segment_line[4] == 5 && main_merged_segment_line[4] == 7) ||
                (adding_segment_line[4] == 7 && main_merged_segment_line[4] == 5)) {
            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(1) >= px2.get(1)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = four_points.get(0).get(0);
            new_segment_line[1] = four_points.get(0).get(1);

            new_segment_line[2] = four_points.get(3).get(0);
            new_segment_line[3] = four_points.get(3).get(1);

            new_segment_line[4] = 0;
        }

        //5 and 9，这两条线如果能组合，表明是近垂直线，取y轴最大和最小值的点
        if ((adding_segment_line[4] == 5 && main_merged_segment_line[4] == 9) ||
                (adding_segment_line[4] == 9 && main_merged_segment_line[4] == 5)) {
            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(1) >= px2.get(1)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = four_points.get(0).get(0);
            new_segment_line[1] = four_points.get(0).get(1);

            new_segment_line[2] = four_points.get(3).get(0);
            new_segment_line[3] = four_points.get(3).get(1);

            new_segment_line[4] = 0;
        }

        //5 and 10，这两条线如果能组合，表明是近垂直线，取y轴最大和最小值的点
        if ((adding_segment_line[4] == 5 && main_merged_segment_line[4] == 10) ||
                (adding_segment_line[4] == 10 && main_merged_segment_line[4] == 5)) {
            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(1) >= px2.get(1)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = four_points.get(0).get(0);
            new_segment_line[1] = four_points.get(0).get(1);

            new_segment_line[2] = four_points.get(3).get(0);
            new_segment_line[3] = four_points.get(3).get(1);

            new_segment_line[4] = 0;
        }

        //6 and 7，这两条线如果能组合，表明是近水平线，取x轴最大和最小值的点
        if ((adding_segment_line[4] == 6 && main_merged_segment_line[4] == 7) ||
                (adding_segment_line[4] == 7 && main_merged_segment_line[4] == 6)) {
            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(0) >= px2.get(0)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = four_points.get(0).get(0);
            new_segment_line[1] = four_points.get(0).get(1);

            new_segment_line[2] = four_points.get(3).get(0);
            new_segment_line[3] = four_points.get(3).get(1);

            new_segment_line[4] = 0;
        }

        //6 and 9，这两条线如果能组合，表明是近垂直线，取y轴最大和最小值的点
        if ((adding_segment_line[4] == 6 && main_merged_segment_line[4] == 9) ||
                (adding_segment_line[4] == 9 && main_merged_segment_line[4] == 6)) {
            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(1) >= px2.get(1)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = four_points.get(0).get(0);
            new_segment_line[1] = four_points.get(0).get(1);

            new_segment_line[2] = four_points.get(3).get(0);
            new_segment_line[3] = four_points.get(3).get(1);

            new_segment_line[4] = 0;
        }

        //6 and 10，这两条线如果能组合，表明是近垂直线，取y轴最大和最小值的点
        if ((adding_segment_line[4] == 6 && main_merged_segment_line[4] == 10) ||
                (adding_segment_line[4] == 10 && main_merged_segment_line[4] == 6)) {
            Collections.sort(four_points, new Comparator<List<Double>>() {
                @Override
                public int compare(List<Double> px1, List<Double> px2) {
                    if (px1.get(1) >= px2.get(1)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            new_segment_line[0] = four_points.get(0).get(0);
            new_segment_line[1] = four_points.get(0).get(1);

            new_segment_line[2] = four_points.get(3).get(0);
            new_segment_line[3] = four_points.get(3).get(1);

            new_segment_line[4] = 0;
        }


        new_segment_line[5] = old_merged_times + 1;
        double[] new_reference_line = get_ref_line(new_segment_line, imageWidth, imageHeight);
        new_segment_line[4] = new_reference_line[4];
        new_segment_line[5] = old_merged_times + 1;
        new_reference_line[5] = old_merged_times + 1;
        new_segment_line_and_reference_line_pair.add(new_reference_line);
        new_segment_line_and_reference_line_pair.add(new_segment_line);

        return new_segment_line_and_reference_line_pair;

    }

    private static Boolean is_two_ref_line_close_to_eachother(List<double[]> segment_line_and_reference_line_pair_a,
                                                              List<double[]> segment_line_and_reference_line_pair_b,
                                                              int imageWidth,
                                                              int imageHeight) {


        double segment_line_a_length = Math.sqrt(Math.pow((segment_line_and_reference_line_pair_a.get(1)[1] - segment_line_and_reference_line_pair_a.get(1)[3]), 2)
                + Math.pow((segment_line_and_reference_line_pair_a.get(1)[0] - segment_line_and_reference_line_pair_a.get(1)[2]), 2));
        double segment_line_b_length = Math.sqrt(Math.pow((segment_line_and_reference_line_pair_b.get(1)[1] - segment_line_and_reference_line_pair_b.get(1)[3]), 2)
                + Math.pow((segment_line_and_reference_line_pair_b.get(1)[0] - segment_line_and_reference_line_pair_b.get(1)[2]), 2));

        //暂时不考虑这个参数double center_distance =
        double theta_a = get_angle_of_line(segment_line_and_reference_line_pair_a.get(0));
        double theta_b = get_angle_of_line(segment_line_and_reference_line_pair_b.get(0));
        double angle = Math.abs(theta_a - theta_b);
        angle = angle % 180;
        if (angle > 30 && angle < 150) return false;

//        Point pa= new Point();
//        pa.x = (segment_line_and_reference_line_pair_a.get(0)[0]+segment_line_and_reference_line_pair_a.get(0)[2])/2;
//        pa.y = (segment_line_and_reference_line_pair_a.get(0)[1]+segment_line_and_reference_line_pair_a.get(0)[3])/2;
//
//
//        Point pb= new Point();
//        pb.x = (segment_line_and_reference_line_pair_b.get(0)[0]+segment_line_and_reference_line_pair_b.get(0)[2])/2;
//        pb.y = (segment_line_and_reference_line_pair_b.get(0)[1]+segment_line_and_reference_line_pair_b.get(0)[3])/2;
//
//        double line_center_distance = get_two_point_length(pa,pb);
        double measure_distance = get_measure_distance(segment_line_and_reference_line_pair_a.get(0),
                segment_line_and_reference_line_pair_b.get(0),
                imageWidth, imageHeight);
        //下面这个值是经验参数
        double k_merge_lines_max_distance = ((imageWidth + imageHeight) / (segment_line_a_length + segment_line_b_length)) * 0.40 * kMergeLinesMaxDistance;
        if (k_merge_lines_max_distance < 30) {
            k_merge_lines_max_distance = 30;
        }

        if (measure_distance <= k_merge_lines_max_distance) {
            return true;
        } else {
            return false;
        }

    }


    private static double get_measure_distance(double[] cross_line_a, double[] cross_line_b, double w, double h) {
        //注意：所有延长线都是从左到右排列点的
        //先排查两条线相同的情况
        if (cross_line_a[4] == 1 && cross_line_b[4] == 1) {
            return Math.max(Math.abs(cross_line_a[0] - cross_line_b[0]), Math.abs(cross_line_a[2] - cross_line_b[2]));
        }

        if (cross_line_a[4] == 2 && cross_line_b[4] == 2) {
            return Math.max(Math.abs(cross_line_a[1] - cross_line_b[1]), Math.abs(cross_line_a[3] - cross_line_b[3]));
        }

        if (cross_line_a[4] == 3 && cross_line_b[4] == 3) {
            return Math.max(Math.abs(cross_line_a[1] - cross_line_b[1]), Math.abs(cross_line_a[2] - cross_line_b[2]));
        }

        if (cross_line_a[4] == 4 && cross_line_b[4] == 4) {
            return Math.max(Math.abs(cross_line_a[1] - cross_line_b[1]), Math.abs(cross_line_a[3] - cross_line_b[3]));
        }

        if (cross_line_a[4] == 5 && cross_line_b[4] == 5) {
            return Math.max(Math.abs(cross_line_a[0] - cross_line_b[0]), Math.abs(cross_line_a[2] - cross_line_b[2]));
        }

        if (cross_line_a[4] == 6 && cross_line_b[4] == 6) {
            return Math.max(Math.abs(cross_line_a[0] - cross_line_b[0]), Math.abs(cross_line_a[3] - cross_line_b[3]));
        }

        if (cross_line_a[4] == 7 && cross_line_b[4] == 7) {
            return Math.max(Math.abs(cross_line_a[1] - cross_line_b[1]), Math.abs(cross_line_a[2] - cross_line_b[2]));
        }

        if (cross_line_a[4] == 8 && cross_line_b[4] == 8) {
            return Math.max(Math.abs(cross_line_a[1] - cross_line_b[1]), Math.abs(cross_line_a[3] - cross_line_b[3]));
        }

        if (cross_line_a[4] == 9 && cross_line_b[4] == 9) {
            return Math.max(Math.abs(cross_line_a[0] - cross_line_b[0]), Math.abs(cross_line_a[2] - cross_line_b[2]));
        }

        if (cross_line_a[4] == 10 && cross_line_b[4] == 10) {
            return Math.max(Math.abs(cross_line_a[0] - cross_line_b[0]), Math.abs(cross_line_a[3] - cross_line_b[3]));
        }

        //后面排查两条线不同的情况
        //从1开始
        //1和2\4\8不可能
        //1和3\6,7,9,10

        if (cross_line_a[4] == 1 && cross_line_b[4] == 3) {
            return Math.max(Math.max(cross_line_a[0], cross_line_b[1]), Math.abs(cross_line_a[2] - cross_line_b[2]));
        }

        if (cross_line_a[4] == 3 && cross_line_b[4] == 1) {
            return Math.max(Math.max(cross_line_b[0], cross_line_a[1]), Math.abs(cross_line_b[2] - cross_line_a[2]));
        }


        if (cross_line_a[4] == 1 && cross_line_b[4] == 5) {
            return Math.max(Math.abs(cross_line_a[0] - cross_line_b[0]), Math.abs(cross_line_a[2] - cross_line_b[2]));
        } //ok

        if (cross_line_a[4] == 5 && cross_line_b[4] == 1) {
            return Math.max(Math.abs(cross_line_b[0] - cross_line_a[0]), Math.abs(cross_line_b[2] - cross_line_a[2]));
        } //ok

        if (cross_line_a[4] == 1 && cross_line_b[4] == 6) {
            return Math.max(Math.max(w - cross_line_a[0], w - cross_line_b[0]), Math.max(w - cross_line_a[2], h - cross_line_b[3]));
        } //ok

        if (cross_line_a[4] == 6 && cross_line_b[4] == 1) {
            return Math.max(Math.max(w - cross_line_b[0], w - cross_line_a[0]), Math.max(w - cross_line_b[2], h - cross_line_a[3]));
        } //ok


        if (cross_line_a[4] == 1 && cross_line_b[4] == 7) {
            return Math.max(Math.abs(cross_line_a[0] - cross_line_b[0]), Math.abs(cross_line_a[2] - cross_line_b[2]));
        }


        if (cross_line_a[4] == 7 && cross_line_b[4] == 1) {
            return Math.max(Math.abs(cross_line_b[0] - cross_line_a[0]), Math.abs(cross_line_b[2] - cross_line_a[2]));
        }


        if (cross_line_a[4] == 1 && cross_line_b[4] == 9) {
            return Math.max(Math.abs(cross_line_a[0] - cross_line_b[0]), Math.abs(cross_line_a[2] - cross_line_b[2]));
        } //ok

        if (cross_line_a[4] == 9 && cross_line_b[4] == 1) {
            return Math.max(Math.abs(cross_line_b[0] - cross_line_a[0]), Math.abs(cross_line_b[2] - cross_line_a[2]));
        } //ok


        if (cross_line_a[4] == 1 && cross_line_b[4] == 10) {
            return Math.max(Math.abs(cross_line_a[0] - cross_line_b[0]), Math.max(w - cross_line_a[2], cross_line_b[3]));
        }

        if (cross_line_a[4] == 10 && cross_line_b[4] == 1) {
            return Math.max(Math.abs(cross_line_b[0] - cross_line_a[0]), Math.max(w - cross_line_b[2], cross_line_a[3]));
        }

        //考虑2的情况
        //2和5\9不可能
        //2和3,4,6,7,8,10有可能
        if (cross_line_a[4] == 2 && cross_line_b[4] == 3) {
            return Math.max(Math.abs(cross_line_a[1] - cross_line_b[1]), Math.max(h - cross_line_a[3], w - cross_line_b[2]));
        }//ok

        if (cross_line_a[4] == 3 && cross_line_b[4] == 2) {
            return Math.max(Math.abs(cross_line_b[1] - cross_line_a[1]), Math.max(h - cross_line_b[3], w - cross_line_a[2]));
        }//ok


        if (cross_line_a[4] == 2 && cross_line_b[4] == 4) {
            return Math.max(Math.abs(cross_line_a[1] - cross_line_b[1]), Math.abs(cross_line_a[3] - cross_line_b[3]));
        }//ok


        if (cross_line_a[4] == 4 && cross_line_b[4] == 2) {
            return Math.max(Math.abs(cross_line_b[1] - cross_line_a[1]), Math.abs(cross_line_b[3] - cross_line_a[3]));
        }//ok


        if (cross_line_a[4] == 2 && cross_line_b[4] == 6) {
            return Math.max(Math.max(cross_line_a[1], cross_line_b[0]), Math.abs(cross_line_a[3] - cross_line_b[3]));
        }//ok

        if (cross_line_a[4] == 6 && cross_line_b[4] == 2) {
            return Math.max(Math.max(cross_line_b[1], cross_line_a[0]), Math.abs(cross_line_b[3] - cross_line_a[3]));
        }//ok


        if (cross_line_a[4] == 2 && cross_line_b[4] == 7) {
            return Math.max(Math.abs(cross_line_a[1] - cross_line_b[1]), Math.max(cross_line_a[3], w - cross_line_b[2]));
        }//ok

        if (cross_line_a[4] == 7 && cross_line_b[4] == 2) {
            return Math.max(Math.abs(cross_line_b[1] - cross_line_a[1]), Math.max(cross_line_b[3], w - cross_line_a[2]));
        }//ok

        if (cross_line_a[4] == 2 && cross_line_b[4] == 8) {
            return Math.max(Math.abs(cross_line_a[1] - cross_line_b[1]), Math.abs(cross_line_a[3] - cross_line_b[3]));
        }//ok

        if (cross_line_a[4] == 8 && cross_line_b[4] == 2) {
            return Math.max(Math.abs(cross_line_b[1] - cross_line_a[1]), Math.abs(cross_line_b[3] - cross_line_a[3]));
        }//ok

        if (cross_line_a[4] == 2 && cross_line_b[4] == 10) {
            return Math.max(Math.max(h - cross_line_a[1], cross_line_b[0]), Math.abs(cross_line_a[3] - cross_line_b[3]));
        }

        if (cross_line_a[4] == 10 && cross_line_b[4] == 2) {
            return Math.max(Math.max(h - cross_line_b[1], cross_line_a[0]), Math.abs(cross_line_b[3] - cross_line_a[3]));
        }


        //考虑3的情况
        //3和7\9不可能
        //3和4\5\6\8\10可能
        if (cross_line_a[4] == 3 && cross_line_b[4] == 4) {
            return Math.max(Math.abs(cross_line_a[1] - cross_line_b[1]), Math.max(Math.abs(w - cross_line_a[2]), Math.abs(h - cross_line_b[3])));
        }//ok

        if (cross_line_a[4] == 4 && cross_line_b[4] == 3) {
            return Math.max(Math.abs(cross_line_b[1] - cross_line_a[1]), Math.max(Math.abs(w - cross_line_b[2]), Math.abs(h - cross_line_a[3])));
        }//ok

        if (cross_line_a[4] == 3 && cross_line_b[4] == 5) {
            return Math.max(Math.max(cross_line_a[1], cross_line_b[0]), Math.abs(cross_line_a[2] - cross_line_b[2]));
        }//ok

        if (cross_line_a[4] == 5 && cross_line_b[4] == 3) {
            return Math.max(Math.max(cross_line_b[1], cross_line_a[0]), Math.abs(cross_line_b[2] - cross_line_a[2]));
        }//ok

        if (cross_line_a[4] == 3 && cross_line_b[4] == 6) {
            return Math.max(Math.max(cross_line_a[1], cross_line_b[0]), Math.max(w - cross_line_a[2], h - cross_line_b[3]));
        }//ok

        if (cross_line_a[4] == 6 && cross_line_b[4] == 3) {
            return Math.max(Math.max(cross_line_b[1], cross_line_a[0]), Math.max(w - cross_line_b[2], h - cross_line_a[3]));
        }//ok

        if (cross_line_a[4] == 3 && cross_line_b[4] == 8) {
            return Math.max(Math.abs(cross_line_a[1] - cross_line_b[1]), Math.abs(w - cross_line_a[2] - h - cross_line_b[3]));
        }//ok

        if (cross_line_a[4] == 8 && cross_line_b[4] == 3) {
            return Math.max(Math.abs(cross_line_b[1] - cross_line_a[1]), Math.abs(w - cross_line_b[2] - h - cross_line_a[3]));
        }//ok

        if (cross_line_a[4] == 3 && cross_line_b[4] == 10) {
            return Math.max(Math.max(h - cross_line_a[1], cross_line_b[0]), Math.max(w - cross_line_a[2], h - cross_line_b[3]));
        }

        if (cross_line_a[4] == 10 && cross_line_b[4] == 3) {
            return Math.max(Math.max(h - cross_line_b[1], cross_line_a[0]), Math.max(w - cross_line_b[2], h - cross_line_a[3]));
        }


        //考虑4的情况
        //4和9\不可能
        //4和5\6\7\8\10可能

        if (cross_line_a[4] == 4 && cross_line_b[4] == 5) {
            return Math.max(Math.max(cross_line_a[1], cross_line_b[0]), Math.max(Math.abs(w - cross_line_b[2]), Math.abs(h - cross_line_a[3])));
        }

        if (cross_line_a[4] == 5 && cross_line_b[4] == 5) {
            return Math.max(Math.max(cross_line_b[1], cross_line_a[0]), Math.max(Math.abs(w - cross_line_a[2]), Math.abs(h - cross_line_b[3])));
        }


        if (cross_line_a[4] == 4 && cross_line_b[4] == 6) {
            return Math.max(Math.max(cross_line_a[1], cross_line_b[0]), Math.abs(cross_line_a[3] - cross_line_b[3]));
        }//ok

        if (cross_line_a[4] == 6 && cross_line_b[4] == 4) {
            return Math.max(Math.max(cross_line_b[1], cross_line_a[0]), Math.abs(cross_line_b[3] - cross_line_a[3]));
        }//ok

        if (cross_line_a[4] == 4 && cross_line_b[4] == 7) {
            return Math.max(Math.abs(cross_line_a[1] - cross_line_b[1]), Math.max(cross_line_a[3], w - cross_line_b[2]));
        } //ok

        if (cross_line_a[4] == 7 && cross_line_b[4] == 4) {
            return Math.max(Math.abs(cross_line_b[1] - cross_line_a[1]), Math.max(cross_line_b[3], w - cross_line_a[2]));
        } //ok

        if (cross_line_a[4] == 4 && cross_line_b[4] == 8) {
            return Math.max(Math.abs(cross_line_a[1] - cross_line_b[1]), Math.abs(cross_line_a[3] - cross_line_b[3]));
        }

        if (cross_line_a[4] == 8 && cross_line_b[4] == 4) {
            return Math.max(Math.abs(cross_line_b[1] - cross_line_a[1]), Math.abs(cross_line_b[3] - cross_line_a[3]));
        }

        if (cross_line_a[4] == 4 && cross_line_b[4] == 10) {
            return Math.max(Math.max(h - cross_line_a[1], cross_line_b[0]), Math.abs(cross_line_a[3] - cross_line_b[3]));
        }

        if (cross_line_a[4] == 10 && cross_line_b[4] == 4) {
            return Math.max(Math.max(h - cross_line_b[1], cross_line_a[0]), Math.abs(cross_line_b[3] - cross_line_a[3]));
        }

        //考虑5的情况
        //5和8不可能
        //5和6,7,9,10可能
        if (cross_line_a[4] == 5 && cross_line_b[4] == 6) {
            return Math.max(Math.abs(cross_line_a[0] - cross_line_b[0]), Math.max(w - cross_line_a[2], h - cross_line_b[3]));
        }

        if (cross_line_a[4] == 6 && cross_line_b[4] == 5) {
            return Math.max(Math.abs(cross_line_b[0] - cross_line_a[0]), Math.max(w - cross_line_b[2], h - cross_line_a[3]));
        }

        if (cross_line_a[4] == 5 && cross_line_b[4] == 7) {
            return Math.max(Math.max(cross_line_a[2], h - cross_line_b[1]), Math.abs(w - cross_line_a[0] - cross_line_b[2]));
        }

        if (cross_line_a[4] == 7 && cross_line_b[4] == 5) {
            return Math.max(Math.max(cross_line_b[2], h - cross_line_a[1]), Math.abs(w - cross_line_b[0] - cross_line_a[2]));
        }

        if (cross_line_a[4] == 5 && cross_line_b[4] == 9) {
            return Math.max(Math.abs(cross_line_a[0] - cross_line_b[0]), Math.abs(cross_line_a[2] - cross_line_b[2]));
        }

        if (cross_line_a[4] == 9 && cross_line_b[4] == 5) {
            return Math.max(Math.abs(cross_line_b[0] - cross_line_a[0]), Math.abs(cross_line_b[2] - cross_line_a[2]));
        }

        if (cross_line_a[4] == 5 && cross_line_b[4] == 10) {
            return Math.max(Math.max(cross_line_a[0], cross_line_b[3]), Math.abs(cross_line_a[2] - cross_line_b[0]));
        }

        if (cross_line_a[4] == 10 && cross_line_b[4] == 5) {
            return Math.max(Math.max(cross_line_b[0], cross_line_a[3]), Math.abs(cross_line_b[2] - cross_line_a[0]));
        }

        //考虑6的情况
        //6和\9\10不可能
        //6和7,8有可能
        //第六根线和后面没有可能
        if (cross_line_a[4] == 6 && cross_line_b[4] == 7) {
            return Math.max(Math.max(cross_line_a[1], cross_line_b[0]), Math.max(cross_line_a[3], w - cross_line_b[2]));
        }

        if (cross_line_a[4] == 7 && cross_line_b[4] == 6) {
            return Math.max(Math.max(cross_line_b[1], cross_line_a[0]), Math.max(cross_line_b[3], w - cross_line_a[2]));
        }

        if (cross_line_a[4] == 6 && cross_line_b[4] == 8) {
            return Math.max(Math.max(cross_line_a[0], cross_line_b[1]), Math.abs(cross_line_a[3] - cross_line_b[3]));
        }

        if (cross_line_a[4] == 8 && cross_line_b[4] == 6) {
            return Math.max(Math.max(cross_line_b[0], cross_line_a[1]), Math.abs(cross_line_b[3] - cross_line_a[3]));
        }

        //考虑7的情况
        //7和后面都有可能
        //7和8\9\10都有可能

        if (cross_line_a[4] == 7 && cross_line_b[4] == 8) {
            return Math.max(Math.abs(cross_line_a[1] - cross_line_b[1]), Math.max(w - cross_line_a[2], cross_line_b[3]));
        }

        if (cross_line_a[4] == 8 && cross_line_b[4] == 7) {
            return Math.max(Math.abs(cross_line_b[1] - cross_line_a[1]), Math.max(w - cross_line_b[2], cross_line_a[3]));
        }

        if (cross_line_a[4] == 7 && cross_line_b[4] == 9) {
            return Math.max(Math.max(h - cross_line_a[1], cross_line_b[0]), Math.abs(cross_line_a[2] - h - cross_line_b[2]));
        }

        if (cross_line_a[4] == 9 && cross_line_b[4] == 7) {
            return Math.max(Math.max(h - cross_line_b[1], cross_line_a[0]), Math.abs(cross_line_b[2] - h - cross_line_a[2]));
        }

        if (cross_line_a[4] == 7 && cross_line_b[4] == 10) {
            return Math.max(Math.max(h - cross_line_a[1], cross_line_b[0]), Math.max(w - cross_line_a[2], cross_line_b[3]));
        }

        if (cross_line_a[4] == 10 && cross_line_b[4] == 7) {
            return Math.max(Math.max(h - cross_line_b[1], cross_line_a[0]), Math.max(w - cross_line_b[2], cross_line_a[3]));
        }

        //考虑8的情况
        //8 and 9,10都有可能

        if (cross_line_a[4] == 8 && cross_line_b[4] == 9) {
            return Math.max(Math.max(h - cross_line_a[1], cross_line_b[0]), Math.max(cross_line_a[3], w - cross_line_b[2]));
        }

        if (cross_line_a[4] == 9 && cross_line_b[4] == 8) {
            return Math.max(Math.max(h - cross_line_a[1], cross_line_b[0]), Math.max(cross_line_a[3], w - cross_line_b[2]));
        }

        if (cross_line_a[4] == 8 && cross_line_b[4] == 10) {
            return Math.max(Math.max(h - cross_line_b[1], cross_line_a[0]), Math.abs(cross_line_b[3] - cross_line_a[3]));
        }

        if (cross_line_a[4] == 10 && cross_line_b[4] == 8) {
            return Math.max(Math.max(h - cross_line_a[1], cross_line_b[0]), Math.abs(cross_line_a[3] - cross_line_b[3]));
        }

        //最后一种情况：
        //9 and 10
        if (cross_line_a[4] == 9 && cross_line_b[4] == 10) {
            return Math.max(Math.abs(cross_line_a[0] - cross_line_b[0]), Math.max(w - cross_line_a[2], cross_line_b[3]));
        }

        if (cross_line_a[4] == 10 && cross_line_b[4] == 9) {
            return Math.max(Math.abs(cross_line_b[0] - cross_line_a[0]), Math.max(w - cross_line_b[2], cross_line_a[3]));
        }

        return 35565;

    }


    private static double[] get_ref_line(double[] segmentLine, int imageWidth, int imageHeight) {
        double[] reference_line = new double[6];
        if (segmentLine[0] == segmentLine[2]) {
            //说明当前线段为垂直线
            reference_line[0] = segmentLine[0];
            reference_line[1] = 0;
            reference_line[2] = segmentLine[2];
            reference_line[3] = imageHeight;
            reference_line[4] = 1;

        } else if (segmentLine[1] == segmentLine[3]) {
            //说明当前线段为水平直线
            reference_line[0] = 0;
            reference_line[1] = segmentLine[1];
            reference_line[2] = imageHeight;
            reference_line[3] = segmentLine[3];
            reference_line[4] = 2;

        } else {
            double a = 0;
            double b = 0;

            //a = (np.float32)(line[0,1] - line[0,3]) / (np.float32)(line[0,0] - line[0,2])
            //b = (np.float32)(line[0,0]*line[0,3] - line[0,2]*line[0,1]) / (np.float32)(line[0,0] - line[0,2])
            a = (segmentLine[1] - segmentLine[3]) / (segmentLine[0] - segmentLine[2]);
            b = (segmentLine[0] * segmentLine[3] - segmentLine[2] * segmentLine[1]) / (segmentLine[0] - segmentLine[2]);

            if (a > 0) {
                if (b >= 0) {
                    //截距为正
                    if (a * imageWidth + b >= imageHeight) {
                        reference_line[0] = 0;  //# 从左往右
                        reference_line[1] = b;
                        reference_line[2] = (imageHeight - b) / a;
                        reference_line[3] = imageHeight;
                        reference_line[4] = 3;

                    } else {
                        //a*imageWidth+b <  imageHeight
                        reference_line[0] = 0;  //# 从左往右
                        reference_line[1] = b;
                        reference_line[2] = imageWidth;
                        reference_line[3] = imageWidth * a + b;
                        reference_line[4] = 4;
                    }
                } else {
                    //这里b<0
                    //截距为正
                    if (a * imageWidth + b >= imageHeight) {
                        reference_line[0] = 0 - b / a;  //# 从左往右
                        reference_line[1] = 0;
                        reference_line[2] = (imageHeight - b) / a;
                        reference_line[3] = imageHeight;
                        reference_line[4] = 5;

                    } else {
                        //a*imageWidth+b <  imageHeight
                        reference_line[0] = 0 - b / a;  //# 从左往右
                        reference_line[1] = 0;
                        reference_line[2] = imageWidth;
                        reference_line[3] = imageWidth * a + b;
                        reference_line[4] = 6;
                    }
                }

            } else {
                //a不会等于0，这里是a《0
                if (b < imageHeight) {
                    //截距为正
                    if (-b / a <= imageWidth) {
                        reference_line[0] = 0;  //# 从左往右
                        reference_line[1] = b;
                        reference_line[2] = 0 - b / a;
                        reference_line[3] = 0;
                        reference_line[4] = 7;
                    } else {
                        //-b/a > imageWidt
                        reference_line[0] = 0;  //# 从左往右
                        reference_line[1] = b;
                        reference_line[2] = imageWidth;
                        reference_line[3] = a * imageWidth + b;
                        reference_line[4] = 8;
                    }

                } else {
                    //这里b>=imageHeight
                    //截距为正
                    if (-b / a <= imageWidth) {

                        reference_line[0] = 0 - b / a; // # 从左往右
                        reference_line[1] = 0;
                        reference_line[2] = (imageHeight - b) / a;
                        reference_line[3] = imageHeight;
                        reference_line[4] = 9;
                    } else {
                        //-b/a > imageWidt
                        reference_line[0] = (imageHeight - b) / a;  //# 从左往右
                        reference_line[1] = imageHeight;
                        reference_line[2] = imageWidth;
                        reference_line[3] = a * imageWidth + b;
                        reference_line[4] = 10;
                    }
                }
            }
        }
        return reference_line;
    }


}
