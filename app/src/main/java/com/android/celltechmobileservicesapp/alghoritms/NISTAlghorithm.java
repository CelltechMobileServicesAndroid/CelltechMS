package com.android.celltechmobileservicesapp.alghoritms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.android.celltechmobileservicesapp.R;
import com.android.celltechmobileservicesapp.TestWipeFragment;
import com.android.celltechmobileservicesapp.Utils;
import com.android.celltechmobileservicesapp.wipe.FillZero;
import com.android.celltechmobileservicesapp.wipe.RemoveZero;
import com.android.celltechmobileservicesapp.wipe.VerifyFillZero;

import java.util.Calendar;
import java.util.Locale;

public class NISTAlghorithm extends BaseAlgorithm {
    String TAG = NISTAlghorithm.class.getSimpleName();

    FillZero fillZero;
    VerifyFillZero verifyFillZero;

    public NISTAlghorithm(final Context mContext, TestWipeFragment fragment, String curentAlgName) {

        numberOfPhases = 2;

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
                message = "Verificare scriere cu zero ";
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
                fragment.algFreeSpace.setText(String.format("%s%s%s", mContext.getString(R.string.free_space), String.valueOf(Utils.getCurrentAvailableSpace()), mContext.getString(R.string.mb)));
                handler.postDelayed(this, 1000);
            }
        };

        resume();

        //fill with Random
        fillZero = new FillZero(1) {
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

        //fill with Random
        verifyFillZero = new VerifyFillZero(2) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tasksPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                finalizeVerifyZero();
            }
        };

    }

    public void wipeData() {
        initTasks();
        fillZero.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
    }

    private void finalizeZero() {
        displayMessages(fillZero.success, fillZero.message);
        if (fillZero.success) {
            verifyFillZero.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
        } else {
            RemoveZero.remove();
            stopAlgoritm(fillZero.message);
        }
    }

    private void finalizeVerifyZero() {
        displayMessages(verifyFillZero.succes, null);
        RemoveZero.remove();
        finalizeAlgorithm();
    }
}