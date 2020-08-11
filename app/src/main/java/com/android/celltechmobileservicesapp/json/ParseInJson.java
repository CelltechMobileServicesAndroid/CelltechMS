package com.android.celltechmobileservicesapp.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParseInJson {

    public static final String TAG = "ParseInJson";

    public static InJsonModel parseJson(String content) {
        InJsonModel model = new InJsonModel();

        try {
            JSONObject reader = new JSONObject(content);
            String action  = reader.optString("action");
            model.setAction(action);
            String error  = reader.optString("error");
            model.setError(error);
            String success  = reader.optString("success");
            model.setSuccess(success);
            String info  = reader.optString("info");
            model.setInfo(info);

            JSONObject params = reader.getJSONObject("params");
            JSONArray algsNod = params.getJSONArray("algorithms");
            List<Algorithm> algs = new ArrayList<Algorithm>();
            for (int i = 0; i < algsNod.length(); i++) {
                JSONObject algNod = algsNod.getJSONObject(i);
                Algorithm alg = new Algorithm();
                alg.setName(algNod.getString("name"));
                alg.setRun(algNod.getString("run"));
                algs.add(alg);
            }
            Params paramss = new Params();
            paramss.setAlgorithms(algs);
            model.setParams(paramss);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return model;
    }


}
