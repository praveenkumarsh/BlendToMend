/**
 * Created by Praveen on 14/7/2020.
 */

package com.praveen.blendtomend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.util.Log;

import com.praveen.blendtomend.cropblack.ImageCropActivity;
import com.praveen.blendtomend.cropblack.helpers.ImageUtils;
import com.praveen.blendtomend.cropblack.helpers.MyConstants;
import com.praveen.blendtomend.cropblack.libraries.NativeClass;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.praveen.blendtomend.Camera2BasicFragment.storageDirMain;

public class PostProcessing {

    static void manualProcessingMode(Activity activity, String res){
        File file = new File(res);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        Log.d("checkx","2. check");
        gotoCropActivity(activity,bitmap);

    }

    public static void gotoCropActivity(Activity activity,Bitmap selectedBitmap){
        //save selected bitmap to our constants
        //this method will save the image to our device memory
        //so set this variable to null after the image is no longer used
        MyConstants.selectedImageBitmap = selectedBitmap;

        //create new intent to start process image
        Intent intent = new Intent(activity.getApplicationContext(), ImageCropActivity.class);
        activity.startActivity(intent);
    }

    static Mat smoothenImage(Context mContext, Mat imageMat){
                Mat destination = new Mat(imageMat.rows(),imageMat.cols(),imageMat.type());
                Imgproc.GaussianBlur(imageMat, destination, new Size(0,0), 3);
                Core.addWeighted(imageMat, 2.5, destination, -1.5, 0, destination);
                Log.d("Processed","Auto");
                return destination;
    }

    static Mat grayScale(Context mContext,Mat imageMat){
        Imgcodecs.imwrite(storageDirMain+"/temp/forG.jpg",imageMat);
        imageMat = Imgcodecs.imread(storageDirMain+"/temp/forG.jpg");
        Log.d("colorcheck",""+imageMat.type());
        Mat mat1 = new Mat(imageMat.rows(),imageMat.cols(),CvType.CV_16SC1);
        Imgproc.cvtColor(imageMat, mat1, Imgproc.COLOR_RGB2GRAY);
        return mat1;

    }

    static Mat bandw(Context mContext,Mat imageMat){
        Imgcodecs.imwrite(storageDirMain+"/temp/forG.jpg",imageMat);
        imageMat = Imgcodecs.imread(storageDirMain+"/temp/forG.jpg");

        Mat destination = new Mat(imageMat.rows(),imageMat.cols(),imageMat.type());
        Imgproc.GaussianBlur(imageMat, destination, new Size(0,0), 3);
        Core.addWeighted(imageMat, 2.5, destination, -1.5, 0, destination);
        destination.copyTo(imageMat);

        destination = new Mat(imageMat.rows(),imageMat.cols(),imageMat.type());
        Imgproc.GaussianBlur(imageMat, destination, new Size(0,0), 3);
        Core.addWeighted(imageMat, 2.5, destination, -1.5, 0, destination);
        destination.copyTo(imageMat);

        destination = new Mat(imageMat.rows(),imageMat.cols(),imageMat.type());
        Imgproc.GaussianBlur(imageMat, destination, new Size(0,0), 3);
        Core.addWeighted(imageMat, 2.5, destination, -1.5, 0, destination);
        destination.copyTo(imageMat);

        destination = new Mat(imageMat.rows(),imageMat.cols(),imageMat.type());
        Imgproc.GaussianBlur(imageMat, destination, new Size(0,0), 3);
        Core.addWeighted(imageMat, 2.5, destination, -1.5, 0, destination);
        destination.copyTo(imageMat);

        Mat gray = new Mat(imageMat.rows(),imageMat.cols(), CvType.CV_16UC1);
        Imgproc.cvtColor(imageMat, gray, Imgproc.COLOR_RGB2GRAY);

        Mat mat2 = new Mat(imageMat.rows(),imageMat.cols(), CvType.CV_16UC1);
        Imgproc.threshold(gray,mat2, 128, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        return mat2;

    }

    static Mat autoCrop(Mat imageMat){
        Imgcodecs.imwrite(storageDirMain+"/temp/forC.jpg",imageMat);
        imageMat = Imgcodecs.imread(storageDirMain+"/temp/forC.jpg");
        Bitmap tempImg = ImageUtils.matToBitmap(imageMat);
        List<PointF> result = new ArrayList<>();
        result.add(new PointF(100, 100));
        result.add(new PointF(tempImg.getWidth()-100, 100));
        result.add(new PointF(100, tempImg.getHeight()-100));
        result.add(new PointF(tempImg.getWidth()-100, tempImg.getHeight()-100));

        Map<Integer, PointF> points = getOrderedPoints(result);
        float xRatio = 1 ;
        float yRatio = 1 ;

        float x1 = (points.get(0).x) * xRatio;
        float x2 = (points.get(1).x) * xRatio;
        float x3 = (points.get(2).x) * xRatio;
        float x4 = (points.get(3).x) * xRatio;
        float y1 = (points.get(0).y) * yRatio;
        float y2 = (points.get(1).y) * yRatio;
        float y3 = (points.get(2).y) * yRatio;
        float y4 = (points.get(3).y) * yRatio;

        NativeClass nativeClass = new NativeClass();

        Bitmap bitmap =  nativeClass.getScannedBitmap(tempImg, x1, y1, x2, y2, x3, y3, x4, y4);
        return ImageUtils.bitmapToMat(bitmap);

    }


    static Mat autoCropAutoSelection(Mat img){
        Imgcodecs.imwrite(storageDirMain+"/temp/forC.jpg",img);
        img = Imgcodecs.imread(storageDirMain+"/temp/forC.jpg");
        Bitmap tempImg = ImageUtils.matToBitmap(img);

        //===================================================================
        int border = 100;
        Core.copyMakeBorder(img, img, border, border, border, border, Core.BORDER_CONSTANT);
        Imgcodecs.imwrite(storageDirMain+"/temp/"+"/mats.jpg", img);
        img = Imgcodecs.imread(storageDirMain+"/temp/"+"/mats.jpg");
        List<PostProcessing.Pair> pairList = PostProcessing.findLargestRectangle(img);

        List<PointF> result = new ArrayList<>();
        result.add(new PointF((float)pairList.get(1).x-80, (float)pairList.get(1).y-120)); //Bottom left
        result.add(new PointF((float)pairList.get(2).x-120, (float)pairList.get(2).y-120)); //Bottom right

        result.add(new PointF((float)pairList.get(3).x-120, (float)pairList.get(3).y-80)); //Top right
        result.add(new PointF((float)pairList.get(0).x-80, (float)pairList.get(0).y-80)); //Top left
        //====================================================================

        Map<Integer, PointF> points = getOrderedPoints(result);
        float xRatio = 1 ;
        float yRatio = 1 ;

        float x1 = (points.get(0).x) * xRatio;
        float x2 = (points.get(1).x) * xRatio;
        float x3 = (points.get(2).x) * xRatio;
        float x4 = (points.get(3).x) * xRatio;
        float y1 = (points.get(0).y) * yRatio;
        float y2 = (points.get(1).y) * yRatio;
        float y3 = (points.get(2).y) * yRatio;
        float y4 = (points.get(3).y) * yRatio;

        NativeClass nativeClass = new NativeClass();

        Bitmap bitmap =  nativeClass.getScannedBitmap(tempImg, x1, y1, x2, y2, x3, y3, x4, y4);
        return ImageUtils.bitmapToMat(bitmap);

    }


    public static Map<Integer, PointF> getOrderedPoints(List<PointF> points) {

        PointF centerPoint = new PointF();
        int size = points.size();
        for (PointF pointF : points) {
            centerPoint.x += pointF.x / size;
            centerPoint.y += pointF.y / size;
        }
        Map<Integer, PointF> orderedPoints = new HashMap<>();
        for (PointF pointF : points) {
            int index = -1;
            if (pointF.x < centerPoint.x && pointF.y < centerPoint.y) {
                index = 0;
            } else if (pointF.x > centerPoint.x && pointF.y < centerPoint.y) {
                index = 1;
            } else if (pointF.x < centerPoint.x && pointF.y > centerPoint.y) {
                index = 2;
            } else if (pointF.x > centerPoint.x && pointF.y > centerPoint.y) {
                index = 3;
            }
            orderedPoints.put(index, pointF);
        }
        return orderedPoints;
    }

    //=====================Get Max Area==========================================
    public static List<Pair> findLargestRectangle(Mat imgSource) {
//	    Mat imgSource = original_image;
//	    Mat untouched = original_image.clone();
//
//	    //convert the image to black and white
//	    Imgproc.cvtColor(imgSource, imgSource, Imgproc.COLOR_BGR2GRAY);

        //convert the image to black and white does (8 bit)
        Imgproc.cvtColor(imgSource, imgSource, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(imgSource, imgSource, 0, 255.0, Imgproc.THRESH_BINARY);
        Imgproc.Canny(imgSource, imgSource, 10, 10 * 3, 3, false);
//	    Imgproc.Canny(imgSource, imgSource, 50, 50);

        //apply gaussian blur to smoothen lines of dots
        Imgproc.GaussianBlur(imgSource, imgSource, new Size(5, 5), 5);

        //find the contours
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(imgSource, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        double maxArea = -1;
        int maxAreaIdx = -1;
        MatOfPoint temp_contour = contours.get(0); //the largest is at the index 0 for starting point
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        MatOfPoint2f maxCurve = new MatOfPoint2f();
        List<MatOfPoint> largest_contours = new ArrayList<MatOfPoint>();
        for (int idx = 0; idx < contours.size(); idx++) {
            temp_contour = contours.get(idx);
            double contourarea = Imgproc.contourArea(temp_contour);
            //compare this contour to the previous largest contour found
            if (contourarea > maxArea) {
                //check if this contour is a square
                MatOfPoint2f new_mat = new MatOfPoint2f( temp_contour.toArray() );
                int contourSize = (int)temp_contour.total();
                Imgproc.approxPolyDP(new_mat, approxCurve, contourSize*0.05, true);
                if (approxCurve.total() == 4) {
                    maxCurve = approxCurve;
                    maxArea = contourarea;
                    maxAreaIdx = idx;
                    largest_contours.add(temp_contour);
                }
            }
        }

        List<Pair> list = new ArrayList<>();

//	    //create the new image here using the largest detected square
//	    Mat new_image = new Mat(imgSource.size(), CvType.CV_8U); //we will create a new black blank image with the largest contour
//	    Imgproc.cvtColor(new_image, new_image, Imgproc.COLOR_BayerBG2RGB);
//	    Imgproc.drawContours(new_image, contours, maxAreaIdx, new Scalar(255, 255, 255), 1); //will draw the largest square/rectangle

        double temp_double[] = maxCurve.get(0, 0);
        Point p1 = new Point(temp_double[0], temp_double[1]);
//	    Imgproc.circle(new_image, new Point(p1.x, p1.y), 20, new Scalar(255, 0, 0), 5); //p1 is colored red
        String temp_string = "Point 1: (" + p1.x + ", " + p1.y + ")";
        list.add(new Pair(p1.x, p1.y));

        temp_double = maxCurve.get(1, 0);
        Point p2 = new Point(temp_double[0], temp_double[1]);
//	    Imgproc.circle(new_image, new Point(p2.x, p2.y), 20, new Scalar(0, 255, 0), 5); //p2 is colored green
        temp_string += "\nPoint 2: (" + p2.x + ", " + p2.y + ")";
        list.add(new Pair(p2.x, p2.y));

        temp_double = maxCurve.get(2, 0);
        Point p3 = new Point(temp_double[0], temp_double[1]);
//	    Imgproc.circle(new_image, new Point(p3.x, p3.y), 20, new Scalar(0, 0, 255), 5); //p3 is colored blue
        temp_string += "\nPoint 3: (" + p3.x + ", " + p3.y + ")";
        list.add(new Pair(p3.x, p3.y));

        temp_double = maxCurve.get(3, 0);
        Point p4 = new Point(temp_double[0], temp_double[1]);
//	    Imgproc.circle(new_image, new Point(p4.x, p4.y), 20, new Scalar(0, 255, 255), 5); //p1 is colored violet
        temp_string += "\nPoint 4: (" + p4.x + ", " + p4.y + ")";
        list.add(new Pair(p4.x, p4.y));

        return list;
    }

    public static class Pair{
        public double x;
        public double y;
        public Pair() {
            // TODO Auto-generated constructor stub
        }
        public Pair(double x2,double y2){
            this.x = x2;
            this.y = y2;
        }
    }
}
