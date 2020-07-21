package com.vegabond.monumentdetection.cropblack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.vegabond.monumentdetection.PostProcessing;
import com.vegabond.monumentdetection.R;
import com.vegabond.monumentdetection.cropblack.helpers.ImageUtils;
import com.vegabond.monumentdetection.cropblack.helpers.MyConstants;
import com.vegabond.monumentdetection.cropblack.libraries.NativeClass;
import com.vegabond.monumentdetection.cropblack.libraries.PolygonView;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vegabond.monumentdetection.Camera2BasicFragment.storageDirMain;

public class ImageCropActivity extends Activity {

    FrameLayout holderImageCrop;
    ImageView imageView;
    PolygonView polygonView;
    Bitmap selectedImageBitmap;
    Button btnImageEnhance;

    NativeClass nativeClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);
        initializeElement();
    }

    private void initializeElement() {
        nativeClass = new NativeClass();
        btnImageEnhance = findViewById(R.id.btnImageEnhance);
        holderImageCrop = findViewById(R.id.holderImageCrop);
        imageView = findViewById(R.id.imageView);
        polygonView = findViewById(R.id.polygonView);

        holderImageCrop.post(new Runnable() {
            @Override
            public void run() {
                initializeCropping();
            }
        });
        btnImageEnhance.setOnClickListener(btnImageEnhanceClick);

    }

    private void initializeCropping() {

        selectedImageBitmap = MyConstants.selectedImageBitmap;
        MyConstants.selectedImageBitmap = null;

        Bitmap scaledBitmap = scaledBitmap(selectedImageBitmap, holderImageCrop.getWidth(), holderImageCrop.getHeight());
        imageView.setImageBitmap(scaledBitmap);

        Bitmap tempBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Map<Integer, PointF> pointFs = getEdgePoints(tempBitmap);

        polygonView.setPoints(pointFs);
        polygonView.setVisibility(View.VISIBLE);

        int padding = (int) getResources().getDimension(R.dimen.scanPadding);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;

        polygonView.setLayoutParams(layoutParams);

    }

    private View.OnClickListener btnImageEnhanceClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //save selected bitmap to our constants
            //this method will save the image to our device memory
            //so set this variable to null after the image is no longer used
            MyConstants.selectedImageBitmap = getCroppedImage();

            //create new intent to start process image
            Intent intent = new Intent(getApplicationContext(), ImageEnhanceActivity.class);
            startActivity(intent);

        }
    };

    protected Bitmap getCroppedImage() {

        Map<Integer, PointF> points = polygonView.getPoints();

        float xRatio = (float) selectedImageBitmap.getWidth() / imageView.getWidth();
        float yRatio = (float) selectedImageBitmap.getHeight() / imageView.getHeight();

        float x1 = (points.get(0).x) * xRatio;
        float x2 = (points.get(1).x) * xRatio;
        float x3 = (points.get(2).x) * xRatio;
        float x4 = (points.get(3).x) * xRatio;
        float y1 = (points.get(0).y) * yRatio;
        float y2 = (points.get(1).y) * yRatio;
        float y3 = (points.get(2).y) * yRatio;
        float y4 = (points.get(3).y) * yRatio;

        return nativeClass.getScannedBitmap(selectedImageBitmap, x1, y1, x2, y2, x3, y3, x4, y4);

    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Log.v("Cropping", "scaledBitmap");
        Log.v("Cropping", width + " " + height);
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) {
        Log.v("Cropping", "getEdgePoints");
        List<PointF> pointFs = getContourEdgePoints(tempBitmap);
        Map<Integer, PointF> orderedPoints = orderedValidEdgePoints(tempBitmap, pointFs);
        return orderedPoints;
    }

    private List<PointF> getContourEdgePoints(Bitmap tempBitmap) {
//        Log.v("Cropping", "getContourEdgePoints");
//
//        MatOfPoint2f point2f = nativeClass.getPoint(tempBitmap);
//        List<Point> points = Arrays.asList(point2f.toArray());
//        Log.d("Checking","List Size :"+points.size());
//        for (int i = 0; i < points.size(); i++) {
//            result.add(new PointF(((float) points.get(i).x), ((float) points.get(i).y)));
//            Log.d("Checking","Size:"+points.get(i).x+"--"+points.get(i).y);
//        }

        //=====================================

        Mat img = ImageUtils.bitmapToMat(tempBitmap);

        int border = 100;
        Core.copyMakeBorder(img, img, border, border, border, border, Core.BORDER_CONSTANT);
//        selectedImageBitmap = ImageUtils.matToBitmap(img);
        Imgcodecs.imwrite(storageDirMain+"/temp/"+"/mats.jpg", img);
        img = Imgcodecs.imread(storageDirMain+"/temp/"+"/mats.jpg");
        List<PostProcessing.Pair> pairList = PostProcessing.findLargestRectangle(img);

        List<PointF> result = new ArrayList<>();
        result.add(new PointF((float)pairList.get(1).x-80, (float)pairList.get(1).y-120)); //Bottom left
        result.add(new PointF((float)pairList.get(2).x-120, (float)pairList.get(2).y-120)); //Bottom right

        result.add(new PointF((float)pairList.get(3).x-120, (float)pairList.get(3).y-80)); //Top right
        result.add(new PointF((float)pairList.get(0).x-80, (float)pairList.get(0).y-80)); //Top left

//        List<PointF> result = new ArrayList<>();
//        result.add(new PointF((float)pairList.get(1).x-80, (float)pairList.get(1).y-80)); //Bottom left expected
//        result.add(new PointF((float)pairList.get(2).x-80, (float)pairList.get(2).y-80));
//        result.add(new PointF((float)pairList.get(0).x-80, (float)pairList.get(0).y-80)); //Top left
//        result.add(new PointF((float)pairList.get(3).x-80, (float)pairList.get(3).y-80)); //Top right


        //=====================================

//        List<PointF> result = new ArrayList<>();
//        result.add(new PointF(20, 20));
//        result.add(new PointF(tempBitmap.getWidth()-20, 20));
//        result.add(new PointF(20, tempBitmap.getHeight()-20));
//        result.add(new PointF(tempBitmap.getWidth()-20, tempBitmap.getHeight()-20));


        return result;

    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Log.v("Cropping", "getOutlinePoints");
        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Log.v("Cropping", "orderedValidEdgePoints");
        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        if (!polygonView.isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
    }

}
