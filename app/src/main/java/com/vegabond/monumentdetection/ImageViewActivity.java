package com.vegabond.monumentdetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.vegabond.monumentdetection.Camera2BasicFragment.count;

public class ImageViewActivity extends AppCompatActivity {

    ImageView resOut;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Intent intent = getIntent();
        String str = intent.getStringExtra("imagePath");
        
        resOut = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBarImageView);
        File f=new File(str);
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        resOut.setImageBitmap(b);

        findViewById(R.id.retake).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ImageViewActivity.this,MainActivity.class));
            }
        });

        findViewById(R.id.process).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ckeckcount","count :"+(--count));
                progressBar.setVisibility(View.VISIBLE);
                findViewById(R.id.process).setClickable(false);
                findViewById(R.id.retake).setClickable(false);
                Runnable r = new Runnable() {
                    public void run() {
                        String res = ImageProcessing.imageProcess(getApplicationContext(),false);
                        progressBar.setVisibility(View.INVISIBLE);
                        findViewById(R.id.process).setClickable(true);
                        findViewById(R.id.retake).setClickable(true);
                        if (!res .equals("")) {
                            File file = new File(res);
                            final Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                                            FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", file)
                                            : Uri.fromFile(file),
                                    "image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);


                        }

                    }
                };

                new Thread(r).start();

            }

            //=======================================================================================
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
    }

    @Override
    public void onPause() {
        stopBackgroundThread();
        super.onPause();
    }


    private HandlerThread mBackgroundThread;

    private Handler mBackgroundHandler;

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
