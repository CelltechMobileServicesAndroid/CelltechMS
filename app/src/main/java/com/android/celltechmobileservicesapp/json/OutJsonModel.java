package com.android.celltechmobileservicesapp.json;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.celltechmobileservicesapp.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OutJsonModel extends InJsonModel implements Serializable {
    String TAG = OutJsonModel.class.getSimpleName();
    Context mContext;
    String localAction;
    List<Algorithm> listAlgsResults = new ArrayList<>();

    public OutJsonModel(Context mCont, String action) {
        this.mContext = mCont;
        this.localAction = action;
        loadContent();
    }

    public OutJsonModel(Context mContext, String localAction, List<Algorithm> listAlgsResults) {
        this.mContext = mContext;
        this.localAction = localAction;
        this.listAlgsResults = listAlgsResults;
        loadContent();
    }

    public void loadContent() {
        Log.d(TAG, "OutJsonModel serial " + Constants.SERIAL);

        model = getDeviceName();
        Log.d(TAG, "OutJsonModel model " + model);

        Log.d(TAG, "OutJsonModel imei " + Constants.IMEI);

        version = Build.VERSION.RELEASE;
        Log.d(TAG, "OutJsonModel version " + version);

        memo = getTotalInternalMemorySize();
        Log.d(TAG, "OutJsonModel memo " + memo);

        baseband = Build.getRadioVersion();
        Log.d(TAG, "OutJsonModel baseband " + baseband);

        status = 3;
    }

    public String createJson() {
        String str = "";
        JSONObject dataToSend = new JSONObject();
        try {
            dataToSend.put("imei", Constants.IMEI);
            dataToSend.put("imei2", Constants.IMEI);
            dataToSend.put("serial", Constants.SERIAL);
            dataToSend.put("model", model);
            dataToSend.put("android_version", version);
            dataToSend.put("memo", memo);
            dataToSend.put("baseband", baseband);
            dataToSend.put("error", "");
            dataToSend.put("success", "true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ("factory_reset".equals(localAction)) {
            try {
                dataToSend.put("action", "factory_reset");
                dataToSend.put("params", "");
                dataToSend.put("info", "started factory reset");
                str = dataToSend.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                dataToSend.put("action", "data_wipe");
                dataToSend.put("status", status);
                JSONArray arrayAlgs = new JSONArray();
                for (Algorithm alg : listAlgsResults) {
                    JSONObject algObj = new JSONObject();
                    algObj.put("name", alg.getName());
                    algObj.put("info", alg.getRun());
                    algObj.put("duration", alg.getDuration());
                    arrayAlgs.put(algObj);
                }
                JSONObject params = new JSONObject();
                params.put("algorithms", arrayAlgs);
                dataToSend.put("params", params);
                dataToSend.put("info", "");
                str = dataToSend.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

}

//{
//        "action": "factory_reset",
//        "params": "",
//        "error": "",
//        "success": "true",
//        "info": "started factory reset"
//        }
//

//{
//        "action": "data_wipe",
//        "status": 3,
//        "params": {
//        "algorithms": [
//        {"name": "HMG5",
//        “info" : "",
//        "duration" : "15 sec",},
//        {"name": "NCSC",
//        “info” : "Not implemented",
//        "duration" : "20 sec",},
//        {"name": "BSI",
//        "info" : “Not implemented",
//        "duration” : "7 sec",},
//        ],
//        "Total_duration" : "42 sec"
//        }
//        "error": ""
//        "success": "true",
//        "info": ""
//        }

