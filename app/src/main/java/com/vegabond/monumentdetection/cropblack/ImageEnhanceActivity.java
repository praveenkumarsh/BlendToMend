package com.vegabond.monumentdetection.cropblack;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vegabond.monumentdetection.R;
import com.vegabond.monumentdetection.cropblack.helpers.ImageUtils;
import com.vegabond.monumentdetection.cropblack.helpers.MyConstants;
import com.vegabond.monumentdetection.cropblack.libraries.NativeClass;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.vegabond.monumentdetection.Camera2BasicFragment.storageDirMain;
import static com.vegabond.monumentdetection.cropblack.helpers.ImageUtils.bitmapToMat;


public class ImageEnhanceActivity extends AppCompatActivity {

    ImageView imageView;
    Bitmap selectedImageBitmap;

    Button btnImageToBW;
    Button btnImageToSmoothen;
    Button btnImageToGray;
    Button btnImageOriginal;
    Button btnSave;

    ProgressBar progressBarImageEnhance;

    Bitmap tempOriginal;

    NativeClass nativeClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_enhance);

        initializeElement();
        initializeImage();
    }

    private void initializeElement() {

        nativeClass = new NativeClass();

        imageView = findViewById(R.id.imageView);
        btnImageToGray = findViewById(R.id.btnImageToGray);
        btnImageToBW = findViewById(R.id.btnImageToBW);
        btnImageToSmoothen = findViewById(R.id.btnImageSmoothen);
        btnSave = findViewById(R.id.btnSave);
        progressBarImageEnhance = findViewById(R.id.progressBarImageEnhance);
        btnImageOriginal = findViewById(R.id.btnImageOriginal);

        btnImageToBW.setOnClickListener(btnImageToBWClick);
        btnImageToSmoothen.setOnClickListener(btnImageToSmoothenClick);
        btnImageToGray.setOnClickListener(btnImageToGrayClick);
        btnSave.setOnClickListener(btnSaveClick);
        btnImageOriginal.setOnClickListener(btnOriginal);

        progressBarImageEnhance.setVisibility(View.INVISIBLE);





    }

    private void initializeImage() {

        selectedImageBitmap = MyConstants.selectedImageBitmap;
        MyConstants.selectedImageBitmap = null;

        imageView.setImageBitmap(selectedImageBitmap);
        tempOriginal = selectedImageBitmap;

    }

    private View.OnClickListener btnImageToBWClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: create BW
            progressBarImageEnhance.setVisibility(View.VISIBLE);
            //=========================================================
            Mat imageMat = bitmapToMat(selectedImageBitmap);

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

//            Mat imageMat = bitmapToMat(selectedImageBitmap);
            Mat gray = new Mat(selectedImageBitmap.getHeight(),selectedImageBitmap.getWidth(), CvType.CV_16UC1);
            Imgproc.cvtColor(imageMat, gray, Imgproc.COLOR_RGB2GRAY);

            Mat mat2 = new Mat(selectedImageBitmap.getHeight(),selectedImageBitmap.getWidth(), CvType.CV_16UC1);
            Imgproc.threshold(gray,mat2, 128, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

            Bitmap bitmap = ImageUtils.matToBitmapGrayscale(mat2);
            selectedImageBitmap =bitmap;
            progressBarImageEnhance.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap(selectedImageBitmap);
//            imageView.setImageBitmap(nativeClass.getBWBitmap(selectedImageBitmap));
        }
    };

    private View.OnClickListener btnOriginal = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: create Original
            selectedImageBitmap = tempOriginal;

            imageView.setImageBitmap(selectedImageBitmap);
        }
    };


    private View.OnClickListener btnSaveClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: create save
            Mat saveMat = bitmapToMat(selectedImageBitmap);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
            final String currentTimeStamp = dateFormat.format(new Date());
            Imgcodecs.imwrite(storageDirMain + "/" + "MONUMENT_" + currentTimeStamp + ".jpg", saveMat);
            Toast.makeText(getApplicationContext(),"Saved :"+storageDirMain + "/" + "MONUMENT_" + currentTimeStamp + ".jpg",Toast.LENGTH_SHORT).show();

        }
    };

    private View.OnClickListener btnImageToGrayClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: create Gray
            progressBarImageEnhance.setVisibility(View.VISIBLE);
            Mat imageMat = bitmapToMat(selectedImageBitmap);
            Mat gray = new Mat(selectedImageBitmap.getHeight(),selectedImageBitmap.getWidth(), CvType.CV_16UC1);
            Imgproc.cvtColor(imageMat, gray, Imgproc.COLOR_RGB2GRAY);
            Bitmap bitmap = ImageUtils.matToBitmapGrayscale(gray);
            selectedImageBitmap =bitmap;
            progressBarImageEnhance.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap(selectedImageBitmap);

            //imageView.setImageBitmap(nativeClass.getGrayBitmap(selectedImageBitmap));
        }
    };

    private View.OnClickListener btnImageToSmoothenClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: create Smoothen
            Log.d("Smoothen","Started");
            progressBarImageEnhance.setVisibility(View.VISIBLE);
            Mat imageMat = bitmapToMat(selectedImageBitmap);
            Mat destination = new Mat(imageMat.rows(),imageMat.cols(),imageMat.type());
            Imgproc.GaussianBlur(imageMat, destination, new Size(0,0), 3);
            Core.addWeighted(imageMat, 2.5, destination, -1.5, 0, destination);
            Bitmap bitmap = ImageUtils.matToBitmap(destination);
            selectedImageBitmap =bitmap;

            progressBarImageEnhance.setVisibility(View.INVISIBLE);
            Log.d("Smoothen","Commpleted");

            imageView.setImageBitmap(selectedImageBitmap);
            Log.d("Smoothen","Set");
        }
    };


}
