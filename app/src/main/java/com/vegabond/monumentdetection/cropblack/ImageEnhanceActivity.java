package com.vegabond.monumentdetection.cropblack;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vegabond.monumentdetection.R;
import com.vegabond.monumentdetection.cropblack.helpers.ImageUtils;
import com.vegabond.monumentdetection.cropblack.helpers.MyConstants;
import com.vegabond.monumentdetection.cropblack.libraries.NativeClass;

import org.opencv.core.Core;
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
    Button btnSave;

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

        btnImageToBW.setOnClickListener(btnImageToBWClick);
        btnImageToSmoothen.setOnClickListener(btnImageToSmoothenClick);
        btnImageToGray.setOnClickListener(btnImageToGrayClick);
        btnSave.setOnClickListener(btnSaveClick);

    }

    private void initializeImage() {

        selectedImageBitmap = MyConstants.selectedImageBitmap;
        MyConstants.selectedImageBitmap = null;

        imageView.setImageBitmap(selectedImageBitmap);

    }

    private View.OnClickListener btnImageToBWClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: create BW
//            imageView.setImageBitmap(nativeClass.getBWBitmap(selectedImageBitmap));
        }
    };

    private View.OnClickListener btnSaveClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: create magic color
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
            //imageView.setImageBitmap(nativeClass.getGrayBitmap(selectedImageBitmap));
        }
    };

    private View.OnClickListener btnImageToSmoothenClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: create Smoothen
            findViewById(R.id.progressBarImageEnhance).setVisibility(View.VISIBLE);
            Mat imageMat = bitmapToMat(selectedImageBitmap);
            Mat destination = new Mat(imageMat.rows(),imageMat.cols(),imageMat.type());
            Imgproc.GaussianBlur(imageMat, destination, new Size(0,0), 2);
            Core.addWeighted(imageMat, 2.5, destination, -1.5, 0, destination);
            Bitmap bitmap = ImageUtils.matToBitmap(imageMat);
            selectedImageBitmap =bitmap;

            findViewById(R.id.progressBarImageEnhance).setVisibility(View.INVISIBLE);

            imageView.setImageBitmap(selectedImageBitmap);
        }
    };


}
