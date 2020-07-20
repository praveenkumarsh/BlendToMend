package com.vegabond.monumentdetection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.util.Log;

import com.vegabond.monumentdetection.cropblack.ImageCropActivity;
import com.vegabond.monumentdetection.cropblack.helpers.ImageUtils;
import com.vegabond.monumentdetection.cropblack.helpers.MyConstants;
import com.vegabond.monumentdetection.cropblack.libraries.NativeClass;
import com.vegabond.monumentdetection.cropblack.libraries.PolygonView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vegabond.monumentdetection.Camera2BasicFragment.storageDirMain;

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
        Mat mat1 = new Mat(imageMat.cols(),imageMat.rows(),CvType.CV_16SC1);
        Imgproc.cvtColor(imageMat, mat1, Imgproc.COLOR_RGB2GRAY);
        Mat mat2 = new Mat(imageMat.cols(),imageMat.rows(),CvType.CV_16SC1);
        Imgproc.threshold(mat1,mat2, 128, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
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
}
