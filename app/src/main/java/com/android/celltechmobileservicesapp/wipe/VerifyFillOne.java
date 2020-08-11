package com.android.celltechmobileservicesapp.wipe;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class VerifyFillOne extends AsyncTask {
    String TAG = VerifyFillOne.class.getSimpleName();
    public int phaseNumber;
    public Process process;
    File file;
    public boolean succes;

    public VerifyFillOne(int phaseNumber) {
        this.phaseNumber = phaseNumber;
    }

    @Override
    public Object doInBackground(Object[] objects) {
        try {
            byte[] bs = new byte[4];
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            file = new File(path + "/one");
            boolean isOned = true;
            FileInputStream in = new FileInputStream(file);
            int i = in.read(bs);
            for (byte b : bs) {
                if (b != (byte) 0xFF) {
                    isOned = false;
                    break;
                }
            }
            in.close();

            succes = isOned;
            Log.d(TAG, "succes " + succes);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}