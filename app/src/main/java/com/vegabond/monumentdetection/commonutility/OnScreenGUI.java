package com.vegabond.monumentdetection.commonutility;

import android.content.Context;
import android.view.View;

import com.vegabond.monumentdetection.Camera2BasicFragment;
import com.vegabond.monumentdetection.SettingUtility;

public class OnScreenGUI extends Camera2BasicFragment {

    Context mContext;

    public OnScreenGUI(Context context) {
        super();
        mContext = context;
    }

    public void SetSettingsAsPerSettings(){
        if (SettingUtility.getOnScreenGUISettings(mContext).getOnScreenBatteryDetais()){
            tvCurrentBatteryCapacity.setVisibility(View.VISIBLE);
            ivCurrentBatteryCapacity.setVisibility(View.VISIBLE);
        }else{
            tvCurrentBatteryCapacity.setVisibility(View.INVISIBLE);
            ivCurrentBatteryCapacity.setVisibility(View.INVISIBLE);
        }

        if (SettingUtility.getOnScreenGUISettings(mContext).getOnScreenTime()){
            tvCurrentTime.setVisibility(View.VISIBLE);
        }else{
            tvCurrentTime.setVisibility(View.INVISIBLE);
        }

        if (SettingUtility.getOnScreenGUISettings(mContext).getOnScreenStorageDetail()){
            tvCurrentStorageCapacity.setVisibility(View.VISIBLE);
        }else{
            tvCurrentStorageCapacity.setVisibility(View.INVISIBLE);
        }

        if (SettingUtility.getOnScreenGUISettings(mContext).getOnScreenGPSInfo()){
            tvgpsLatitude.setVisibility(View.VISIBLE);
            tvgpsLongitude.setVisibility(View.VISIBLE);
        }else{
            tvgpsLatitude.setVisibility(View.INVISIBLE);
            tvgpsLongitude.setVisibility(View.INVISIBLE);

        }

        if (SettingUtility.getOnScreenGUISettings(mContext).getOnScreenRotationInfo()){
            tvCurrentRotation.setVisibility(View.VISIBLE);
        }else{
            tvCurrentRotation.setVisibility(View.INVISIBLE);
        }


    }
}
