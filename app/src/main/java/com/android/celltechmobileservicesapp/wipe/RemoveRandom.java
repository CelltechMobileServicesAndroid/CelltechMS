package com.android.celltechmobileservicesapp.wipe;

import android.os.Environment;
import android.util.Log;

import com.android.celltechmobileservicesapp.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RemoveRandom {
    public static String TAG = "RemoveRandom";

    public static void remove() {
        try {
            // Delete the filled file
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            //File file = new File(path + "/random");
            //Log.d(TAG, "before remove random" + " " + file.length() + " " + Utils.getCurrentAvailableSpace());

            Process process = Runtime.getRuntime().exec("rm " + path + "/random");
            process.waitFor();

            //Log.d(TAG, "after remove one" + " " + file.length() + " " + Utils.getCurrentAvailableSpace());

            try {
                InputStream is = process.getErrorStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                Log.d("Remove random", total.toString());
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
