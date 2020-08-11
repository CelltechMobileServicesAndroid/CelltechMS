package com.android.celltechmobileservicesapp.wipe;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class VerifyFillZero extends AsyncTask {
    String TAG = VerifyFillZero.class.getSimpleName();
    public int phaseNumber;
    public Process process;
    File file;
    public boolean succes;

    public VerifyFillZero(int phaseNumber) {
        this.phaseNumber = phaseNumber;
    }

    @Override
    public Object doInBackground(Object[] objects) {
        try {
            byte[] bs = new byte[4];
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            file = new File(path + "/zero");
            boolean isZeroed = true;
            FileInputStream in = new FileInputStream(file);
            int i = in.read(bs);
            for (byte b : bs) {
                if (b != 00) {
                    isZeroed = false;
                    break;
                }
            }
            in.close();

            succes = isZeroed;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}