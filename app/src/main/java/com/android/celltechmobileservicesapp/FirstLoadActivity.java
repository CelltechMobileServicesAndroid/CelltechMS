package com.android.celltechmobileservicesapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import java.util.Calendar;

import androidx.annotation.Nullable;

public class FirstLoadActivity extends Activity {
    Context mContext;
    int progressStatus = 0;
    ProgressBar progress;
    Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());

        mContext = this;

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("serial")) {
                Constants.SERIAL = extras.getString("serial");
                Utils.appendLogFile(this, "Constants.SERIAL " + Constants.SERIAL);
            } else {
                Utils.appendLogFile(this,"no serial received");
            }
            if (extras.containsKey("imei")) {
                Constants.IMEI = extras.getString("imei");
                Utils.appendLogFile(this,"Constants.IMEI " + Constants.IMEI);
            } else {
                Utils.appendLogFile(this,"no imei received");
            }
        } else {
            Utils.appendLogFile(this,"no extras received");
        }

        setContentView(R.layout.firstload);

        progress = findViewById(R.id.firstload_progressBar);
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 5;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progress.setProgress(progressStatus);
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).start();

        Utils.clearFileContent(this);
        Utils.appendLogFile(this,Calendar.getInstance().getTime().toString());
    }
}
