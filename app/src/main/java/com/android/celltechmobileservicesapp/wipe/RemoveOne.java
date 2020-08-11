package com.android.celltechmobileservicesapp.wipe;

import android.os.Environment;
import android.util.Log;

import com.android.celltechmobileservicesapp.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class RemoveOne {
    public static String TAG = "RemoveOne";
    public static Process process;
    //public static File file;

    public static void remove() {
        try {
            // Delete the filled file
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            //file = new File(path + "/one");
            //Log.d(TAG, "before remove one" + " " + file.length() + " " + Utils.getCurrentAvailableSpace());

            process = Runtime.getRuntime().exec("rm " + path + "/one");
            process.waitFor();

            //Log.d(TAG, "after remove one" + " " + file.length() + " " + Utils.getCurrentAvailableSpace());
            try {
                InputStream is = process.getErrorStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                Log.d("RemoveOne", total.toString());
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
