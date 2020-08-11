package com.android.celltechmobileservicesapp.wipe;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.android.celltechmobileservicesapp.Constants;
import com.android.celltechmobileservicesapp.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FillRandom extends AsyncTask {
    String TAG = "FillRandomAlg";
    //    public Process process;
    public int phaseNumber;
    public boolean success = true;
    public String message = "";

    public FillRandom(int nr) {
        this.phaseNumber = nr;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    public Object doInBackground(Object[] objects) {
        try {
            byte[] objFileBytes = new byte[Constants.ddBS];
            for (int i = 0; i < Constants.ddBS; i++) {
                objFileBytes[i] = (byte) 0x0F;
            }

            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(path + "/random");
            FileOutputStream fos = new FileOutputStream(file.getPath());

            while (Utils.getCurrentAvailableSpace() > 0) {
                try {
                    fos.write(objFileBytes);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            fos.close();
            success = true;
        } catch (IOException e) {
            success = false;
            e.printStackTrace();
            message = e.getMessage();
        } catch (Exception e) {
            success = false;
            message = e.getMessage();
            e.printStackTrace();
        }

        return null;
    }
}