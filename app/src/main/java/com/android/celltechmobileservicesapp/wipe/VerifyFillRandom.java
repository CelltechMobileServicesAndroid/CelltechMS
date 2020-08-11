package com.android.celltechmobileservicesapp.wipe;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class VerifyFillRandom extends AsyncTask {
    String TAG = VerifyFillRandom.class.getSimpleName();
    public int phaseNumber;
    public Process process;
    public boolean succes;
    File file;

    public VerifyFillRandom(int phaseNumber) {
        this.phaseNumber = phaseNumber;
    }

    @Override
    public Object doInBackground(Object[] objects) {
        try {
            byte[] bs = new byte[4];
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            file = new File(path + "/random");
            boolean isOk = true;
            FileInputStream in = new FileInputStream(file);
            int i = in.read(bs);
            for (byte b : bs) {
                if (b != 0x0F) { //17 octal
                    isOk = false;
                    break;
                }
            }
            in.close();
            succes = isOk;
        } catch (IOException e) {
            e.printStackTrace();
            succes = false;
        }catch (Exception e) {
            e.printStackTrace();
            succes = false;
        }
        return null;
    }
}