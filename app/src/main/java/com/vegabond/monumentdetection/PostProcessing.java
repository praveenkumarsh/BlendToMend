package com.vegabond.monumentdetection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.vegabond.monumentdetection.cropblack.ImageCropActivity;
import com.vegabond.monumentdetection.cropblack.helpers.MyConstants;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class PostProcessing {

    static boolean removeBlackBorder(Activity activity, String res){
        if (!SettingUtility.getControlSettings(activity.getApplicationContext()).getRemoveBlackBorder()) {
            File file = new File(res);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            gotoCropActivity(activity,bitmap);
            return true;
//            imageMat.convertTo(imageMat, CvType.CV_16SC3);
//
//            int size = (int) (imageMat.total() * imageMat.channels());
//            short[] temp = new short[size];
//            imageMat.get(0, 0, temp);
//            for (int i = 0; i < temp.length; i++) {
//                if (temp[i]==0){
//                    temp[i] = 255;
//                }
//            }
//            imageMat.put(0,0,temp);
//            return imageMat;
        }else {
            return false;
        }
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

    static Mat postProcessImage(Context mContext, Mat imageMat){

        if (SettingUtility.getControlSettings(mContext).getPostCaptureProcess()){
            if (SettingUtility.getControlSettings(mContext).getPostCaptureProcessMode()){
                Mat destination = new Mat(imageMat.rows(),imageMat.cols(),imageMat.type());
                Imgproc.GaussianBlur(imageMat, destination, new Size(0,0), 2);
                Core.addWeighted(imageMat, 2.5, destination, -1.5, 0, destination);
                Log.d("Processed","Auto");
                return destination;
            }else{
                Log.d("Processed","Manual");
                return imageMat;
            }
        }else{
            Log.d("Processed","Off");
            return imageMat;
        }
    }
}
