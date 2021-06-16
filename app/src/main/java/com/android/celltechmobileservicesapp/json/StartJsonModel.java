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
import android.util.Log;

import com.android.celltechmobileservicesapp.Constants;

import org.json.JSONObject;

import java.io.Serializable;

public class StartJsonModel extends InJsonModel implements Serializable {
    String TAG = StartJsonModel.class.getSimpleName();
    Context mContext;

    public StartJsonModel(Context mContext) {
        this.mContext = mContext;
        Log.d(TAG, "StartJsonModel serial " + Constants.SERIAL);
        Log.d(TAG, "StartJsonModel imei " + Constants.IMEI);

        this.model = getDeviceName();
        Log.d(TAG, "StartJsonModel model " + this.model);
 

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
            dataToSend.put("imei", Constants.IMEI);
            dataToSend.put("imei2", Constants.IMEI);
            dataToSend.put("serial", Constants.SERIAL);
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
