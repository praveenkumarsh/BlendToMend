/**
 * Created by Praveen on 20/7/2020.
 */

package com.praveen.blendtomend.commonutility;

import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.Display;


import com.praveen.blendtomend.Camera2BasicFragment;
import com.praveen.blendtomend.R;

import java.io.File;
import java.text.SimpleDateFormat;

public class OnDisplayOverlay extends Camera2BasicFragment {

    private static String TAG = "OnDisplayOverlay";
    BatteryManager bm;
    static Display display;

    public OnDisplayOverlay(BatteryManager bm, Display display) {
        super();
        this.bm = bm;
        this.display = display;
    }

    public void setRotationDetails(){
        int rotation = display.getOrientation();
        tvCurrentRotation.setText("Rotation : "+rotation);
    }


    public static void setCurrentTime(){
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
        String dateString = sdf.format(date);
        tvCurrentTime.setText(dateString);
    }
    public  void setBatteryDetails(){
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        boolean batIsCharging = bm.isCharging();
        tvCurrentBatteryCapacity.setText(batLevel+"%");
        Log.d(TAG,"ChargingState: "+batIsCharging+" | "+"Battery Level: "+batLevel);
        if (batIsCharging){
            ivCurrentBatteryCapacity.setImageResource(R.drawable.ic_battery_charging_white);
        }else {
            if (batLevel <= 10) {
                ivCurrentBatteryCapacity.setImageResource(R.drawable.ic_battery_alert_white);
            } else if (batLevel > 10 && batLevel <= 20) {
                ivCurrentBatteryCapacity.setImageResource(R.drawable.ic_battery_20_white);
            } else if (batLevel > 20 && batLevel <= 30) {
                ivCurrentBatteryCapacity.setImageResource(R.drawable.ic_battery_30_white);
            } else if (batLevel > 10 && batLevel <= 40) {
                ivCurrentBatteryCapacity.setImageResource(R.drawable.ic_battery_50_white);
            } else if (batLevel > 10 && batLevel <= 50) {
                ivCurrentBatteryCapacity.setImageResource(R.drawable.ic_battery_50_white);
            } else if (batLevel > 10 && batLevel <= 60) {
                ivCurrentBatteryCapacity.setImageResource(R.drawable.ic_battery_60_white);
            } else if (batLevel > 10 && batLevel <= 70) {
                ivCurrentBatteryCapacity.setImageResource(R.drawable.ic_battery_80_white);
            } else if (batLevel > 10 && batLevel <= 80) {
                ivCurrentBatteryCapacity.setImageResource(R.drawable.ic_battery_80_white);
            } else if (batLevel > 10 && batLevel <= 90) {
                ivCurrentBatteryCapacity.setImageResource(R.drawable.ic_battery_90_white);
            } else if (batLevel > 10 && batLevel < 100) {
                ivCurrentBatteryCapacity.setImageResource(R.drawable.ic_battery_90_white);
            }else{
                ivCurrentBatteryCapacity.setImageResource(R.drawable.ic_battery_full_white);
            }
        }

    }


    public static void setAvailableStorage() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        String strg = formatSize(availableBlocks * blockSize);
        tvCurrentStorageCapacity.setText(strg);
    }

    public static String formatSize(long size) {
        String suffix = null;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }
        float sizes = size;
        if (size>=1024){
            suffix = "GB";
            sizes/=1024;
            String output = String.format("%.02f", sizes);
            return output+suffix;
        }
        StringBuilder resultBuffer = new StringBuilder(sizes+"");
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }
        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }


}
