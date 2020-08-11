package com.android.celltechmobileservicesapp.alghoritms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.android.celltechmobileservicesapp.R;
import com.android.celltechmobileservicesapp.TestWipeFragment;
import com.android.celltechmobileservicesapp.Utils;
import com.android.celltechmobileservicesapp.wipe.FillRandom;
import com.android.celltechmobileservicesapp.wipe.RemoveRandom;
import com.android.celltechmobileservicesapp.wipe.VerifyFillRandom;

import java.util.Calendar;
import java.util.Locale;

public class RandAlghorithm extends BaseAlgorithm {
    String TAG = RandAlghorithm.class.getSimpleName();

    FillRandom fillRandom;
    VerifyFillRandom verifyFillRandom;

    public RandAlghorithm(final Context mContext, TestWipeFragment fragment, String curentAlgName) {
        numberOfPhases = 2;
        this.curentAlgName = curentAlgName;

        this.mContext = mContext;
        this.fragment = fragment;

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
                message = "Suprascriere cu un caracter random";
                fragment.circularProgressBar.setProgress(0);
                break;
            case 2:
                message = "Verificare scriere cu caracter random ";
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
                        showPercentage();
                        break;
                    case 2:
                        showIndefiniteLoading();
                        break;
                }
                fragment.algFreeSpace.setText(String.format("%s%s%s", mContext.getString(R.string.free_space),
                        String.valueOf(Utils.getCurrentAvailableSpace()), mContext.getString(R.string.mb)));
                handler.postDelayed(this, 1000);
            }
        };

        resume();

        //fill with Random
        fillRandom = new FillRandom(1) {
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

        //fill with Random
        verifyFillRandom = new VerifyFillRandom(2) {
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
        fillRandom.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
    }

    private void finalizeRandom() {
        displayMessages(fillRandom.success, fillRandom.message);
        if (fillRandom.success) {
            verifyFillRandom.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
        } else {
            RemoveRandom.remove();
            stopAlgoritm(fillRandom.message);
        }
    }

    private void finalizeVerifyRandom() {
        displayMessages(verifyFillRandom.succes, null);
        RemoveRandom.remove();
        finalizeAlgorithm();
    }

}
