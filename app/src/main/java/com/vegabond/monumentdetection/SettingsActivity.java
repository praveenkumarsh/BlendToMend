package com.vegabond.monumentdetection;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Camera2BasicFragment.setting = SettingUtility.getControlSettings(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        PACKAGE_NAME = getApplicationContext().getApplicationInfo();
        mContext = this.getApplicationContext();
    }

    private static Context mContext;
    public static ApplicationInfo PACKAGE_NAME;

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            //======================================================================================
            // For Share App
            PreferenceScreen preferenceShare = findPreference("shareapp");
            preferenceShare.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    share();
                    return true;
                }
            });


            PreferenceScreen preferenceSharelink = findPreference("shareappLink");
            preferenceSharelink.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    shareViaLink();
                    return true;
                }
            });

            //=====================================Help Page========================================

            PreferenceScreen preferenceHelp = findPreference("pref_help");
            preferenceHelp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    helpPage();
                    return true;
                }
            });


        }



        static  final void shareViaLink(){
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Monument Detection App: "+"https://praveensharma.cf/MonumentDetection");
            sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            mContext.getApplicationContext().startActivity(Intent.createChooser(sharingIntent, "Share app via").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }




        static void helpPage(){
            String url = "https://praveensharma.cf/MonumentDetection_Help";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);

        }

        //Getting apk File For Sharing
        static final void share(){
            ApplicationInfo app = mContext.getApplicationContext().getApplicationInfo();
            String filePath = app.sourceDir;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            // Append file and send Intent
            File originalApk = new File(filePath);
            try {
                //Make new directory in new location
                File tempFile = new File(mContext.getExternalCacheDir() + "/ExtractedApk");
                //If directory doesn't exists create new
                if (!tempFile.isDirectory())
                    if (!tempFile.mkdirs())
                        return;

                PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                tempFile = new File(tempFile.getPath() + "/" + mContext.getResources().getString(R.string.app_name)+" "+pInfo.versionName + ".apk");
                //If file doesn't exists create new
                if (!tempFile.exists()) {
                    if (!tempFile.createNewFile()) {
                        return;
                    }
                }
                //Copy file to new location
                InputStream in = new FileInputStream(originalApk);
                OutputStream out = new FileOutputStream(tempFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

                Uri apkURI = FileProvider.getUriForFile(
                        mContext,
                        mContext.getApplicationContext()
                                .getPackageName() + ".provider", tempFile);

                //Open share dialog
                intent.putExtra(Intent.EXTRA_STREAM, apkURI);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(Intent.createChooser(intent, "Share app via").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }


    }
}