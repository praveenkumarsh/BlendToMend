package com.vegabond.monumentdetection;

import android.content.Context;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class PostProcessing {

    static Mat removeBlackBorder(Context mContext,Mat imageMat){
        if (SettingUtility.getControlSettings(mContext).getRemoveBlackBorder()) {
            imageMat.convertTo(imageMat, CvType.CV_16SC3);

            int size = (int) (imageMat.total() * imageMat.channels());
            short[] temp = new short[size];
            imageMat.get(0, 0, temp);
            for (int i = 0; i < temp.length; i++) {
                if (temp[i]==0){
                    temp[i] = 255;
                }
            }
            imageMat.put(0,0,temp);
            return imageMat;
        }else{
            return imageMat;
        }
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
