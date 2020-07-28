/**
 * Created by Praveen on 15/7/2020.
 */

package com.praveen.blendtomend.getstoragepath;

import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;

public class storagePath {

    public static File[] searchRecentCapturingPaths() {
        File folder = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Monument/");

        File[] all = new File[0];
        if (folder.exists()) {
            File[] allFiles = folder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("MONU");
                }
            });
            Arrays.sort(allFiles, Collections.<File>reverseOrder());
            return allFiles;
        }
        return all;
    }
}
