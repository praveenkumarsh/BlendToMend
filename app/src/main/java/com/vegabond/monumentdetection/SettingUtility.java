package com.vegabond.monumentdetection;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SettingUtility {


    public static SettingsControl getControlSettings(Context mContext){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        Boolean detectionMode = preferences.getBoolean("key_monumentDetectionModeState",true);
        String snapDuration = preferences.getString("key_SnapGap","10");
        Boolean storeOriginal = preferences.getBoolean("key_StorePic",true);
        String maxPhoto = preferences.getString("key_MaximumPhoto","10");
        String mode = preferences.getString("key_Mode","1");

        return new SettingsControl(detectionMode,snapDuration,maxPhoto,storeOriginal,mode);
    }

    public static class SettingsControl {
        Boolean DetectionMode;
        String SnapDuration;
        String MaxPhoto;
        Boolean StoreOriginal;
        String Mode;

        public SettingsControl(Boolean detectionMode, String snapDuration, String maxPhoto, Boolean storeOriginal,String mode) {
            DetectionMode = detectionMode;
            SnapDuration = snapDuration;
            MaxPhoto = maxPhoto;
            StoreOriginal = storeOriginal;
            Mode = mode;
        }

        public Boolean getDetectionMode() {
            return DetectionMode;
        }

        public void setDetectionMode(Boolean detectionMode) {
            DetectionMode = detectionMode;
        }

        public String getSnapDuration() {
            return SnapDuration;
        }

        public void setSnapDuration(String snapDuration) {
            SnapDuration = snapDuration;
        }

        public String getMaxPhoto() {
            return MaxPhoto;
        }

        public void setMaxPhoto(String maxPhoto) {
            MaxPhoto = maxPhoto;
        }

        public Boolean getStoreOriginal() {
            return StoreOriginal;
        }

        public void setStoreOriginal(Boolean storeOriginal) {
            StoreOriginal = storeOriginal;
        }

        public String getMode() {
            return Mode;
        }

        public void setMode(String mode) {
            Mode = mode;
        }
    }

}
