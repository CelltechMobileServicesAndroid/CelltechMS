package com.android.celltechmobileservicesapp.wipe;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.android.celltechmobileservicesapp.Constants;
import com.android.celltechmobileservicesapp.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FillZero extends AsyncTask {
    String TAG = "FillZeroAlg";
    public Process process;
    public int phaseNumber;
    public boolean success = true;
    public String message = "";

    public FillZero(int nr) {
        this.phaseNumber = nr;
    }

    @Override
    public Object doInBackground(Object[] objects) {
        try {
            long ddCount = Utils.getAvailableSpaceForZero();
            long ddBs = Constants.ddBS;
            Log.d(TAG, "bs " + ddBs + " count " + ddCount);

            success = true;
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            process = Runtime.getRuntime().exec("dd if=/dev/zero of=" + path + "/zero bs=" + ddBs + " count=" + ddCount);
            process.waitFor();

        } catch (IOException e) {
            success = false;
            e.printStackTrace();
            message = e.getMessage();
        } catch (InterruptedException e) {
            success = false;
            message = e.getMessage();
            e.printStackTrace();
        } catch (Exception e) {
            success = false;
            message = e.getMessage();
            e.printStackTrace();
        }

        try {
            InputStream is = process.getErrorStream();
            if (is != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                //success = false;
                message = total.toString();
                Log.d(TAG, "message " + message);
            }

        } catch (Exception e) {
            //e.printStackTrace();
        }

        return null;
    }
}