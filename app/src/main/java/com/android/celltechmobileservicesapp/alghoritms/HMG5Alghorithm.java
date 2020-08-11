package com.android.celltechmobileservicesapp.alghoritms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.android.celltechmobileservicesapp.R;
import com.android.celltechmobileservicesapp.TestWipeFragment;
import com.android.celltechmobileservicesapp.Utils;
import com.android.celltechmobileservicesapp.wipe.FillOne;
import com.android.celltechmobileservicesapp.wipe.FillRandom;
import com.android.celltechmobileservicesapp.wipe.FillZero;
import com.android.celltechmobileservicesapp.wipe.RemoveOne;
import com.android.celltechmobileservicesapp.wipe.RemoveRandom;
import com.android.celltechmobileservicesapp.wipe.RemoveZero;
import com.android.celltechmobileservicesapp.wipe.VerifyFillRandom;

import java.util.Calendar;
import java.util.Locale;

public class HMG5Alghorithm extends BaseAlgorithm {
    String TAG = HMG5Alghorithm.class.getSimpleName();

//    Pass 1: Overwriting the data with a zero;
//    Pass 2: Overwriting the data with a one;
//    Pass 3: Overwriting the data with a random character as well as verifying the writing of this character.

    FillZero fillZero;
    FillOne fillOne;
    FillRandom fillRandom;
    VerifyFillRandom verifyFillRandom;

    public HMG5Alghorithm(final Context mContext, TestWipeFragment fragment, String curentAlgName) {

        numberOfPhases = 4;

        this.mContext = mContext;
        this.fragment = fragment;
        this.curentAlgName = curentAlgName;

        startTime = Calendar.getInstance().getTime();
        fragment.algFreeSpace.setVisibility(View.VISIBLE);

        curentphaseRunning = 0;
    }

    public void tasksPreExecute() {
        curentphaseRunning++;

        handler.postDelayed(runnable, 1000);
        initialFreeSpace = Utils.getCurrentAvailableSpace();

        String message = "";
        switch (curentphaseRunning) {
            case 1:
                message = "Suprascriere cu zero";
                break;
            case 2:
                message = "Suprascriere cu unu";
                break;
            case 3:
                message = "Suprascriere cu un caracter random";
                break;
            case 4:
                message = "Verificare scriere cu random ";
                break;
        }
        displayMessage(String.format(Locale.getDefault(), "\nFaza %1d/%2d %3s", curentphaseRunning, numberOfPhases, message));
    }

    @SuppressLint("StaticFieldLeak")
    public void initTasks() {
        runnable = new Runnable() {
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

        resume();

        fillZero = new

                FillZero(1) {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        tasksPreExecute();
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        finalizeZero();
                    }
                };

        fillOne = new FillOne(2) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tasksPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                finalizeOne();
            }
        };

        fillRandom = new FillRandom(3) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tasksPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                finalizeRandom();
            }
        };

        verifyFillRandom = new VerifyFillRandom(4) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tasksPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                finalizeVerifyRandom();
            }
        };
    }

    public void wipeData() {
        initTasks();
        fillZero.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
    }

    public void finalizeZero() {
        RemoveZero.remove();
        displayMessages(fillZero.success, fillZero.message);
        if (fillZero.success) {
            fillOne.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
        } else {
            stopAlgoritm(fillZero.message);
        }
    }

    private void finalizeOne() {
        RemoveOne.remove();
        displayMessages(fillOne.success, fillOne.message);
        if (fillOne.success) {
            fillRandom.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
        } else {
            stopAlgoritm(fillOne.message);
        }

    }

    private void finalizeRandom() {
        displayMessages(fillRandom.success, fillRandom.message);
        if (fillRandom.success) {
            verifyFillRandom.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
        } else {
            stopAlgoritm(fillRandom.message);
        }
    }

    private void finalizeVerifyRandom() {
        displayMessages(verifyFillRandom.succes, null);
        RemoveRandom.remove();
        finalizeAlgorithm();
    }
}
