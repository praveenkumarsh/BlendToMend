/**
 * Created by Praveen on 16/7/2020.
 */

package com.praveen.blendtomend.recentcaptureutility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.praveen.blendtomend.BuildConfig;
import com.praveen.blendtomend.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomGalleryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    List<GridViewItem> gridItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_gallery);
        setGridAdapter("");
    }

    private void setGridAdapter(String path) {
        // Create a new grid adapter
        gridItems = createGridItems();
        MyGridAdapter adapter = new MyGridAdapter(this, gridItems);

        // Set the grid adapter
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(adapter);

        // Set the onClickListener
        gridView.setOnItemClickListener(this);
    }


    /**
     * Go through the specified directory, and create items to display in our
     * GridView
     */
    private List<GridViewItem> createGridItems() {
        List<GridViewItem> items = new ArrayList<GridViewItem>();

        // List all the items within the folder.

        File[] files = searchRecentCapturingPaths();
        for (File file : files) {
            Bitmap image = BitmapHelper.decodeBitmapFromFile(file.getAbsolutePath(),
                    50,
                    50);
            items.add(new GridViewItem(file.getAbsolutePath(), false, image));
//            }
        }

        return items;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (gridItems.get(position).isDirectory()) {
            setGridAdapter(gridItems.get(position).getPath());
        }
        else {
            File storageFile = new File(gridItems.get(position).getPath());
            Uri apkURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID+".provider",storageFile);

            view.getContext().grantUriPermission(view.getContext().getPackageName(), apkURI, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (apkURI.toString().endsWith(".mp4")){
                // Display the video
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setDataAndType(apkURI, "video/*");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(i);
            }else{
                // Display the image
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setDataAndType(apkURI, "image/*");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(i);
            }
        }

    }


    public File[] searchRecentCapturingPaths() {
        Intent i = getIntent();
        String savedLocation = i.getStringExtra("location");
        File folder = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/"+savedLocation+"/");

        File[] all = new File[0];


        if(folder.exists()) {
            File[] allFiles = folder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("MONU");
//                    return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")||name.endsWith(".mp4"));
                }
            });
            Arrays.sort(allFiles, Collections.<File>reverseOrder());
            return allFiles;
        }

        return all;

    }

}