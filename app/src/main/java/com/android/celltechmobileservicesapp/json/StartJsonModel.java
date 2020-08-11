package com.android.celltechmobileservicesapp.json;
//          Public status As Integer -> 1
//          Public serial As String -> serialul cum este afisat de catre adb
//          Public imei As String -> IMEI-ul telefonului

//        - modelul telefonului,
//        - versiunea de android,
//        - IMEI 2 (daca exista),
//        - capacitate memorie interna,
//        - serial number,
//        - versiune baseband ?

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

public class StartJsonModel extends InJsonModel implements Serializable {
    String TAG = StartJsonModel.class.getSimpleName();
    Context mContext;

    public StartJsonModel(Context mContext) {
        this.mContext = mContext;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.serial = Build.getSerial();
        } else {
            this.serial = Build.SERIAL;
        }
        Log.d(TAG, "StartJsonModel serial " + this.serial);

        this.model = getDeviceName();
        Log.d(TAG, "StartJsonModel model " + this.model);

//        //get serial for Samsung devices
//        if (model.toLowerCase().contains("samsung")) {
//            this.serial = getManufacturerSerialNumber();
//        }
//        Log.d(TAG, "StartJsonModel serial " + this.serial);

        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        this.imei = telephonyManager.getDeviceId();
        this.imei2 = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (telephonyManager.getPhoneCount() == 2) {
                    Log.i(TAG, "Single 1 " + telephonyManager.getDeviceId(0));
                    Log.i(TAG, "Single 2 " + telephonyManager.getDeviceId(1));
                    this.imei = telephonyManager.getDeviceId(0);
                    this.imei2 = telephonyManager.getDeviceId(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "StartJsonModel imei " + this.imei);
        Log.d(TAG, "StartJsonModel imei2 " + this.imei2);

        this.status = 1;

        this.version = Build.VERSION.RELEASE;
        Log.d(TAG, "StartJsonModel version " + this.version);

        this.memo = getTotalInternalMemorySize();
        Log.d(TAG, "StartJsonModel memo " + this.memo);

        this.baseband = Build.getRadioVersion();
        Log.d(TAG, "StartJsonModel baseband " + this.baseband);
    }

    public String createJson() {
        String str = "";
        try {
            JSONObject dataToSend = new JSONObject();
            dataToSend.put("imei", this.imei);
            dataToSend.put("imei2", this.imei2);
            dataToSend.put("serial", this.serial);
            dataToSend.put("status", this.status);
            dataToSend.put("model", this.model);
            dataToSend.put("android_version", this.version);
            dataToSend.put("memo", this.memo);
            dataToSend.put("baseband", this.baseband);
            str = dataToSend.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

}
