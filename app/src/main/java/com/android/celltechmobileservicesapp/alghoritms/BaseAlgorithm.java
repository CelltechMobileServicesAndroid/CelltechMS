package com.android.celltechmobileservicesapp.alghoritms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.android.celltechmobileservicesapp.TestWipeFragment;
import com.android.celltechmobileservicesapp.Utils;
import com.android.celltechmobileservicesapp.json.Algorithm;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BaseAlgorithm {
    String TAG = BaseAlgorithm.class.getSimpleName();
    TestWipeFragment fragment;
    Context mContext;

    int numberOfPhases;
    int curentphaseRunning;

    static Handler handler = new Handler();
    static Runnable runnable;

    long initialFreeSpace;
    Date startTime, endTime;
    double timeInSeconds = 0;

    String curentAlgName;
    String messageResult = "no info";

    public static void pause() {
        handler.removeCallbacks(runnable);
    }

    public static void resume() {
        handler.postDelayed(runnable, 1000);
    }

    public void displayMessages(boolean success, String errorMessage) {
        if (success) {
            Log.d(TAG, String.format("Faza %1d/%2d %3s", curentphaseRunning, numberOfPhases, "s-a terminat cu succes"));
            fragment.algRunning.append(String.format(Locale.getDefault(), "\nFaza %1d/%2d %3s", curentphaseRunning, numberOfPhases, " s-a terminat cu succes."));
        } else {
            Log.d(TAG, String.format("Faza %1d/%2d %3s", curentphaseRunning, numberOfPhases, "s-a terminat cu eroare.Va rugam reluati procesul."));
            fragment.algRunning.append(String.format(Locale.getDefault(), "\nFaza %1d/%2d %3s", curentphaseRunning, numberOfPhases, " s-a terminat cu eroare.Va rugam reluati procesul."));
            if (!"".equals(errorMessage) && errorMessage != null) {
                fragment.algRunning.append("\n" + errorMessage);
            }
        }
    }

    public void displayMessage(String errorMessage) {
        fragment.algRunning.append("\n" + errorMessage);
    }

    public void stopAlgoritm(String message) {
        messageResult = messageResult + message + "\n";
        displayMessage("Algoritmul nu a continuat. Reluati procesul.");
        fragment.algFreeSpace.setVisibility(View.GONE);
        fragment.circularProgressBar.setVisibility(View.GONE);
    }

//    @SuppressLint("DefaultLocale")
//    public String secondsToTime(double input) {
//        double sec = input % 60;
//        double min = (input / 60)%60;
//        double hours = (input /60)/60;
//
//        return String.format("Timp total : %02f:%02f:%02f",hours, min, sec);
//    }

    public void showPercentage() {
        fragment.circularProgressBar.enableIndeterminateMode(false);
        long dif = (initialFreeSpace - Utils.getCurrentAvailableSpace()) * 100;
        //Log.d("dif", dif + "");
        float percentage = (float) dif / initialFreeSpace;
        //Log.d("percentage", percentage + "");
        long progress = Math.round(percentage);
        fragment.circularProgressBar.setProgress(progress);
        //Log.d("progress", progress + "");
    }

    public void showIndefiniteLoading() {
        fragment.circularProgressBar.enableIndeterminateMode(true);
    }

    @SuppressLint("DefaultLocale")
    public void finalizeAlgorithm() {
        pause();

        //algoritm duration execution
        endTime = Calendar.getInstance().getTime();

        long difference = endTime.getTime() - startTime.getTime();
        timeInSeconds = difference * 0.001;
        displayMessage(String.format("\nTimp total : %.2f secunde", +timeInSeconds));
        //displayMessage(secondsToTime(timeInSeconds));
        fragment.algFreeSpace.setVisibility(View.GONE);

        //fragment
        fragment.currentAlgResult = new Algorithm(curentAlgName, messageResult, timeInSeconds + "sec");
        fragment.startApplingAlghoritms();
    }
}
