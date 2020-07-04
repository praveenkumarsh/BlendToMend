package com.vegabond.monumentdetection;

import android.content.SharedPreferences;

public class Settings extends MainActivity{

    void SaveSettings(){
        SharedPreferences sharedPreferences
                = getSharedPreferences("MonumentDetectionSettings",
                MODE_PRIVATE);
        SharedPreferences.Editor myEditor
                = sharedPreferences.edit();
        myEditor.putBoolean(
                "detectionMode",
                true);
        myEditor.putInt(
                "gaping",
                5);
        myEditor.putInt(
                "maxSnap",
                10);
        myEditor.putBoolean(
                "storeTemp",
                true);
        myEditor.putString(
                "testDirectory",
                "Default");
        myEditor.commit();
    }
}
