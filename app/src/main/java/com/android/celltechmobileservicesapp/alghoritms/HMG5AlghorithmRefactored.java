package com.android.celltechmobileservicesapp.alghoritms;

import android.content.Context;
import android.os.Environment;
import android.view.View;

import com.android.celltechmobileservicesapp.R;
import com.android.celltechmobileservicesapp.TestWipeFragment;
import com.android.celltechmobileservicesapp.Utils;
import com.android.celltechmobileservicesapp.wipe.FillOne;
import com.android.celltechmobileservicesapp.wipe.FillRandom;
import com.android.celltechmobileservicesapp.wipe.FillZero;
import com.android.celltechmobileservicesapp.wipe.RemoveOne;
import com.android.celltechmobileservicesapp.wipe.RemoveZero;
import com.android.celltechmobileservicesapp.wipe.VerifyFillRandom;

import java.io.IOException;
import java.util.Calendar;

public class HMG5AlghorithmRefactored extends BaseAlgorithm {
    String TAG = HMG5AlghorithmRefactored.class.getSimpleName();

//    Pass 1: Overwriting the data with a zero;
//    Pass 2: Overwriting the data with a one;
//    Pass 3: Overwriting the data with a random character as well as verifying the writing of this character.

    FillZero fillZero;
    FillOne fillOne;
    FillRandom fillRandom;
    VerifyFillRandom verifyFillRandom;

    public HMG5AlghorithmRefactored(final Context mContext, TestWipeFragment fragment, String curentAlgName) {

        numberOfPhases = 4;

        this.mContext = mContext;
        this.fragment = fragment;
        this.curentAlgName = curentAlgName;

        startTime = Calendar.getInstance().getTime();
        fragment.algFreeSpace.setVisibility(View.VISIBLE);

        curentphaseRunning = 0;
    }

    Thread mainThreadOne, mainThreadZero, mainThreadRandom;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
                switch (curentphaseRunning) {
                    case 1:
                    case 2:
                    case 3:
                        showPercentage();
                        break;
                    case 4:
                        showIndefiniteLoading();
                        break;
                }
                fragment.algFreeSpace.setText(String.format("%s%s%s", mContext.getString(R.string.free_space), String.valueOf(Utils.getCurrentAvailableSpace()), mContext.getString(R.string.mb)));
                handler.postDelayed(this, 1000);
        }
    };

    Process processZero = null;
    Process processOne = null;
    Process processRandom = null;

    public void wipeData() {
        handler.postDelayed(runnable, 1000);

        curentphaseRunning = 1;
        mainThreadZero = new Thread() {
            @Override
            public void run() {
                try {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    processZero = Runtime.getRuntime().exec("dd if=/dev/zero of=" + path + "/zero");
                    processZero.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mainThreadZero.start();
    }

    public void wipeData2() {
        RemoveZero.remove();
        curentphaseRunning = 2;
        handler.postDelayed(runnable, 1000);
        mainThreadOne = new Thread(){
            @Override
            public void run() {
                try {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String[] command = {
                            "/system/bin/sh",
                            "-c",
                            "tr '\\0' '\\377' < /dev/zero | dd of=" + path + "/one"
                    };
                    processOne = Runtime.getRuntime().exec(command);
                    processOne.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mainThreadOne.start();
    }

    public void wipeData3() {
        RemoveOne.remove();
        curentphaseRunning = 3;
        handler.postDelayed(runnable, 1000);
        mainThreadRandom = new Thread() {
            @Override
            public void run() {
                try {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String[] command = {
                            "/system/bin/sh",
                            "-c",
                            "tr '\\0' '\\17' < /dev/zero | dd of=" + path + "/random"
                    };
                    processRandom = Runtime.getRuntime().exec(command);
                    processRandom.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mainThreadRandom.start();
    }

    public void wipeData4() {

    }
}
