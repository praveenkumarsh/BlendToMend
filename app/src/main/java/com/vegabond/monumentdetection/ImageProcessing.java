package com.vegabond.monumentdetection;

import android.os.Build;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static com.vegabond.monumentdetection.Camera2BasicFragment.count;
import static com.vegabond.monumentdetection.Camera2BasicFragment.setting;
import static com.vegabond.monumentdetection.Camera2BasicFragment.storageDir;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class ImageProcessing {

//    static void transform(Mat img_ref){
//        Imgcodecs.imwrite("new"+count+".jpg",img_ref);
//    }

//    static void postImageCapturing(){
//        for (int i=0;i<Integer.parseInt(setting.getMaxPhoto());i++){
//            Mat finalImg = imageRegistration(Imgcodecs.imread(storageDir+"/0.jpg"),Imgcodecs.imread(storageDir+"/"+i+".jpg"));
//            Imgcodecs.imwrite(storageDir+"/final"+i+".jpg",finalImg);
//
//        }
//    }

    static Mat imageRegistration(Mat img1, Mat img2){
        // Creating the empty destination matrix
        Imgcodecs.imwrite(storageDir+"/ref_new.jpg",img1);
        Mat img_ref = Imgcodecs.imread(storageDir + "/ref_new.jpg");
        Imgcodecs.imwrite(storageDir+"/comp_new.jpg",img2);
        Mat new_img = Imgcodecs.imread(storageDir + "/comp_new.jpg");



        Mat img_ref_gray=new Mat();
        cvtColor(img_ref,img_ref_gray,Imgproc.COLOR_BGR2GRAY);
        Imgcodecs.imwrite(storageDir + "/orGrayOrg" + count + ".jpg", img_ref_gray);

        Size s=new_img.size();
        Mat new_img_gray=new Mat();
        cvtColor(new_img,new_img_gray,Imgproc.COLOR_BGR2GRAY);
        Imgcodecs.imwrite(storageDir + "/nwGrayOrg" + count + ".jpg", new_img_gray);

        ORB orb_detector=ORB.create();

        orb_detector.setMaxFeatures(5000);
        MatOfKeyPoint keypoints_1=new MatOfKeyPoint();
        Mat descriptors_1=new Mat();
        MatOfKeyPoint keypoints_2=new MatOfKeyPoint();
        Mat descriptors_2=new Mat();
        orb_detector.detectAndCompute(img_ref_gray, img_ref_gray, keypoints_1, descriptors_1);
        orb_detector.detectAndCompute(new_img_gray, new_img_gray ,keypoints_2, descriptors_2);
        List<KeyPoint> kp1=keypoints_1.toList();
        List<KeyPoint> kp2=keypoints_2.toList();
        BFMatcher matcher=BFMatcher.create(BFMatcher.BRUTEFORCE_HAMMING, true);
        MatOfDMatch matches=new MatOfDMatch();
        matcher.match(descriptors_1, descriptors_2, matches);

        List<DMatch> match_new=matches.toList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            match_new.sort(new Compare_Dmatch());
        }
        List<DMatch> final_match=match_new.subList(0, (int)(match_new.size()*0.9));

        List<Point> p1=new ArrayList<Point>(final_match.size());
        List<Point> p2=new ArrayList<Point>(final_match.size());

        for(int j=0;j<final_match.size();j++) {

            int index_1=final_match.get(j).queryIdx;
            p1.add(kp1.get(index_1).pt);

            int index_2=final_match.get(j).trainIdx;
            p2.add(kp2.get(index_2).pt);

        }

        MatOfPoint2f matObject = new MatOfPoint2f();
        matObject.fromList(p1);

        MatOfPoint2f matScene = new MatOfPoint2f();
        matScene.fromList(p2);

        Mat homography = Calib3d.findHomography(matObject, matScene, Calib3d.RANSAC,5.0f);

        Mat transformed_img=new Mat();
        Imgproc.warpPerspective(new_img,transformed_img,homography,new_img.size());

        return transformed_img;
    }
}

class Compare_Dmatch implements Comparator<DMatch> {

    @Override
    public int compare(DMatch o1, DMatch o2) {
        // TODO Auto-generated method stub
        return (o1.distance<o2.distance)?1:0;
    }

}
