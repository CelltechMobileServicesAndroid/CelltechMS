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
import com.android.celltechmobileservicesapp.wipe.VerifyFillOne;
import com.android.celltechmobileservicesapp.wipe.VerifyFillRandom;
import com.android.celltechmobileservicesapp.wipe.VerifyFillZero;

import java.util.Calendar;
import java.util.Locale;

public class DODAlghorithm extends BaseAlgorithm {
    String TAG = DODAlghorithm.class.getSimpleName();

    FillZero fillZero;
    VerifyFillZero verifyFillZero;
    FillOne fillOne;
    VerifyFillOne verifyFillOne;
    FillRandom fillRandom;
    VerifyFillRandom verifyFillRandom;

    public DODAlghorithm(final Context mContext, TestWipeFragment fragment, String curentAlgName) {

        numberOfPhases = 6;

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
            case 3:
                message = "Suprascriere cu unu";
                break;
            case 4:
                message = "Verificare scriere cu unu ";
                break;
            case 5:
                message = "Suprascriere cu un caracter random";
                break;
            case 6:
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
                    case 3:
                    case 5:
                        showPercentage();
                        break;
                    case 2:
                    case 4:
                    case 6:
                        showIndefiniteLoading();
                        break;
                }
                fragment.algFreeSpace.setText(String.format("%s%s%s", mContext.getString(R.string.free_space), String.valueOf(Utils.getCurrentAvailableSpace()), mContext.getString(R.string.mb)));
                handler.postDelayed(this, 1000);
            }
        };

        resume();

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


        fillOne = new FillOne(3) {
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

        verifyFillOne = new VerifyFillOne(4) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tasksPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                finalizeVerifyOne();
            }
        };

        fillRandom = new FillRandom(5) {
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

        verifyFillRandom = new VerifyFillRandom(6) {
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
        RemoveZero.remove();
        displayMessages(verifyFillZero.succes, null);
        if (verifyFillZero.succes) {
            fillOne.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
        } else {
            stopAlgoritm("");
        }
    }

    private void finalizeOne() {
        displayMessages(fillOne.success, fillOne.message);
        if (fillOne.success) {
            verifyFillOne.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
        } else {
            RemoveOne.remove();
            stopAlgoritm(fillOne.message);
        }
    }

    private void finalizeVerifyOne() {
        RemoveOne.remove();
        displayMessages(verifyFillOne.succes, null);
        if (verifyFillOne.succes) {
            fillRandom.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
        } else {
            stopAlgoritm("");
        }
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