package com.vegabond.monumentdetection.cropblack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vegabond.monumentdetection.Camera2BasicFragment;
import com.vegabond.monumentdetection.MainActivity;
import com.vegabond.monumentdetection.R;
import com.vegabond.monumentdetection.SettingUtility;
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

import static com.vegabond.monumentdetection.Camera2BasicFragment.gpsAddress;
import static com.vegabond.monumentdetection.Camera2BasicFragment.gpsCity;
import static com.vegabond.monumentdetection.Camera2BasicFragment.gpsCountry;
import static com.vegabond.monumentdetection.Camera2BasicFragment.gpsLatitude;
import static com.vegabond.monumentdetection.Camera2BasicFragment.gpsLongitude;
import static com.vegabond.monumentdetection.Camera2BasicFragment.gpsPostalCode;
import static com.vegabond.monumentdetection.Camera2BasicFragment.gpsState;
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
    Button btnRetake;

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

        findViewById(R.id.btnRetake).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ImageEnhanceActivity.this, MainActivity.class));
                finish();
            }
        });





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
            saveMat = getStampImage(saveMat);


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


    private Mat getStampImage(Mat finalMatImage){
        Bitmap bitmap;
        if (Integer.parseInt(SettingUtility.getControlSettings(getApplicationContext()).getEnhanceMode())>=2){
            bitmap = ImageUtils.matToBitmapGrayscale(finalMatImage);
        }else {
            bitmap = ImageUtils.matToBitmap(finalMatImage);
        }

        Bitmap dest = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas cs = new Canvas(dest);

        Paint tPaint = new Paint();
        tPaint.setTextSize(bitmap.getHeight()/45);
        tPaint.setColor(getApplicationContext().getResources().getColor(R.color.colorRed));

        Paint.Style style = Paint.Style.FILL;
        style = Paint.Style.FILL;

        tPaint.setStyle(style);
        tPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        cs.drawBitmap(bitmap, 0f, 0f, null);
        float heights = tPaint.measureText("yY");
        //==============================================================
        String GPSinfo1 = "";
        String GPSinfo2 = "";
        if (!SettingUtility.getControlSettings(getApplicationContext()).getGpsStamp().equals("-1")) {
            if (SettingUtility.getControlSettings(getApplicationContext()).getGpsStamp().equals("LL")) {
                GPSinfo1 = "Latitude: " + gpsLatitude;
                GPSinfo2 = "Longitude: " + gpsLongitude;
            } else if (SettingUtility.getControlSettings(getApplicationContext()).getGpsStamp().equals("Address")) {
                String addr1 = gpsAddress.substring(0,50);
                String addr2 = gpsAddress.substring(50);
                GPSinfo1 = "Address: " +addr1;
                GPSinfo2 = addr2;
            } else if (SettingUtility.getControlSettings(getApplicationContext()).getGpsStamp().equals("CSPI")) {
                GPSinfo1 = "City: " + gpsCity + " " + "State: " + gpsState;
                GPSinfo2 = "Postal Code: " + gpsPostalCode + " " + "Country: " + gpsCountry;
            }

        }

        cs.drawText(GPSinfo1, 30f, heights + 30f, tPaint);
        cs.drawText(GPSinfo2, 30f, 2*heights + 70f, tPaint);

        finalMatImage = ImageUtils.bitmapToMat(dest);
        return finalMatImage;
    }


}
