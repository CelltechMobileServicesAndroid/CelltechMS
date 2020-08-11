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

import java.util.Calendar;
import java.util.Locale;

public class BSIAlghorithm extends BaseAlgorithm {
    String TAG = BSIAlghorithm.class.getSimpleName();

    //    The VSITR method performs data erasure in the following manner:
//    Pass 1: Overwriting the data with a zero;
//    Pass 2: Overwriting the data with a one;
//    Pass 3: Overwriting the data with a zero;
//    Pass 4: Overwriting the data with a one;
//    Pass 5: Overwriting the data with a zero;
//    Pass 6: Overwriting the data with a one;
//    Pass 7: Overwriting the data with a random character.

    FillZero fillZero1, fillZero3, fillZero5;
    FillOne fillOne2, fillOne4, fillOne6;
    FillRandom fillRandom7;

    public BSIAlghorithm(final Context mContext, TestWipeFragment fragment, String curentAlgName) {

        numberOfPhases = 7;

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
            case 3:
            case 5:
                message = "Suprascriere cu zero ";
                break;
            case 2:
            case 4:
            case 6:
                message = "Suprascriere cu unu ";
                break;
            case 7:
                message = "Suprascriere cu un caracter random";
                break;
        }
        displayMessage(String.format(Locale.getDefault(), "\nFaza %1d/%2d %3s", curentphaseRunning, numberOfPhases, message));
    }

    @SuppressLint("StaticFieldLeak")
    public void initTasks() {
        runnable = new Runnable() {
            @Override
            public void run() {
                showPercentage();
                fragment.algFreeSpace.setText(String.format("%s%s%s", mContext.getString(R.string.free_space), String.valueOf(Utils.getCurrentAvailableSpace()), mContext.getString(R.string.mb)));
                handler.postDelayed(runnable, 1000);
            }
        };

        resume();

        fillZero1 = new FillZero(1) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tasksPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                finalizeZero1();
            }
        };

        //fill with 0
        fillZero3 = new FillZero(3) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tasksPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                finalizeZero3();
            }
        }

        ;

        //fill with 0
        fillZero5 = new FillZero(5) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tasksPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                finalizeZero5();
            }
        }

        ;

        //fill with one
        fillOne2 = new FillOne(2) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tasksPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                finalizeOne2();
            }
        };

        //fill with one
        fillOne4 = new FillOne(4) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tasksPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                finalizeOne4();
            }
        };


        //fill with one
        fillOne6 = new FillOne(6) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tasksPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                finalizeOne6();
            }
        };

        //fill with Random
        fillRandom7 = new FillRandom(7) {
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
    }

    public void wipeData() {
        initTasks();
        fillZero1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
    }

    public void finalizeZero1() {
        RemoveZero.remove();
        displayMessages(fillZero1.success, fillZero1.message);
        if (fillZero1.success) {
            fillOne2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
        } else {
            stopAlgoritm(fillZero1.message);
        }
    }

    private void finalizeOne2() {
        RemoveOne.remove();
        displayMessages(fillOne2.success, fillOne2.message);
        if (fillOne2.success) {
            fillZero3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
        } else {
            stopAlgoritm(fillOne2.message);
        }
    }

    public void finalizeZero3() {
        RemoveZero.remove();
        displayMessages(fillZero3.success, fillZero3.message);
        if (fillZero3.success) {
            fillOne4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
        } else {
            stopAlgoritm(fillOne2.message);
        }
    }

    private void finalizeOne4() {
        RemoveOne.remove();
        displayMessages(fillOne4.success, fillOne4.message);
        if (fillOne4.success) {
            fillZero5.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
        } else {
            stopAlgoritm(fillOne2.message);
        }
    }

    public void finalizeZero5() {
        RemoveZero.remove();
        displayMessages(fillZero5.success, fillZero5.message);
        if (fillZero5.success) {
            fillOne6.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
        } else {
            stopAlgoritm(fillOne2.message);
        }
    }


    private void finalizeOne6() {
        RemoveOne.remove();
        displayMessages(fillOne6.success, fillOne6.message);
        if (fillOne6.success) {
            fillRandom7.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object) null);
        } else {
            stopAlgoritm(fillOne6.message);
        }
    }

    @SuppressLint("DefaultLocale")
    private void finalizeRandom() {
        //clean random
        RemoveRandom.remove();
        displayMessages(fillRandom7.success, null);
        finalizeAlgorithm();
    }

}
