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
        Boolean previewMode = preferences.getBoolean("key_Preview",true);

        String processingMode = preferences.getString("key_Processing_Mode","2");
        Boolean removeBlackBorder = preferences.getBoolean("key_RemoveBlackBorder",true);
        String enhanceMode = preferences.getString("key_Image_Enhance_Mode","0");

        return new SettingsControl(detectionMode,snapDuration,maxPhoto,storeOriginal,mode,previewMode,processingMode,removeBlackBorder,enhanceMode);
    }

    public static class SettingsControl {
        Boolean DetectionMode;
        String SnapDuration;
        String MaxPhoto;
        Boolean StoreOriginal;
        String Mode;
        Boolean PreviewMode;
        String ProcessingMode;
        Boolean RemoveBlackBorder;
        String EnhanceMode;

        public SettingsControl(Boolean detectionMode, String snapDuration, String maxPhoto, Boolean storeOriginal,String mode,Boolean previewMode,String processingMode,Boolean removeBlackBorder,String enhanceMode) {
            DetectionMode = detectionMode;
            SnapDuration = snapDuration;
            MaxPhoto = maxPhoto;
            StoreOriginal = storeOriginal;
            Mode = mode;
            PreviewMode = previewMode;
            ProcessingMode = processingMode;
            RemoveBlackBorder = removeBlackBorder;
            EnhanceMode = enhanceMode;

        }

        public String getProcessingMode() {
            return ProcessingMode;
        }

        public void setProcessingMode(String processingMode) {
            ProcessingMode = processingMode;
        }

        public Boolean getRemoveBlackBorder() {
            return RemoveBlackBorder;
        }

        public void setRemoveBlackBorder(Boolean removeBlackBorder) {
            RemoveBlackBorder = removeBlackBorder;
        }

        public String getEnhanceMode() {
            return EnhanceMode;
        }

        public void setEnhanceMode(String enhanceMode) {
            EnhanceMode = enhanceMode;
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

        public Boolean getPreviewMode() {
            return PreviewMode;
        }

        public void setPreviewMode(Boolean previewMode) {
            PreviewMode = previewMode;
        }
    }

}
