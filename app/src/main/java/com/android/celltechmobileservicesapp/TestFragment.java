package com.android.celltechmobileservicesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.celltechmobileservicesapp.actions.FactoryResetAction;
import com.android.celltechmobileservicesapp.json.Algorithm;
import com.android.celltechmobileservicesapp.json.InJsonModel;
import com.android.celltechmobileservicesapp.json.ParseInJson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.app.Activity.RESULT_OK;

public class TestFragment extends Fragment {
    String TAG = TestFragment.class.getSimpleName();
    View mFragmentContainer;
    TextView uploadJson, wipeData, factoryReset, resultJsonUpload;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mFragmentContainer = inflater.inflate(R.layout.test_fragment, null);

        uploadJson = mFragmentContainer.findViewById(R.id.upload_json);
        factoryReset = mFragmentContainer.findViewById(R.id.factory_reset);
        factoryReset.setVisibility(View.GONE);
        wipeData = mFragmentContainer.findViewById(R.id.wipe_data);
        wipeData.setVisibility(View.GONE);
        resultJsonUpload = mFragmentContainer.findViewById(R.id.upload_json_result);

        uploadJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadJsonMethod();
            }
        });

        wipeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment fragment = null;

                String title = "WIPE DETAIL";
                try {
                    fragment = TestWipeFragment.newInstance(modelIn, "TestFragment");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_right, R.anim.enter_from_right, R.anim.exit_from_right);
                transaction.addToBackStack(title).replace(R.id.content_frame, fragment, title).commit();
            }
        });

        factoryReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FactoryResetAction.finalizeFactoryResetWithAlert((MainActivity) getActivity());
            }
        });

        return mFragmentContainer;
    }

    private void uploadJsonMethod() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        getActivity().startActivityForResult(intent, 7);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 7:
                if (resultCode == RESULT_OK) {
                    if (data.getData() != null) {
                        parseJsonContent(data.getData());
                    }
                }
                break;
        }
    }

    InJsonModel modelIn;

    private void parseJsonContent(Uri uri) {
        StringBuilder total = new StringBuilder();
        try {
            //inputstream from uri
            InputStream is = getContext().getContentResolver().openInputStream(uri);
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }

            //parse received json
            modelIn = ParseInJson.parseJson(total.toString());

            resultJsonUpload.setText(String.format("Actiunea primita: %s\n", Utils.getActionNameByCode(getContext(), modelIn.getAction())));
            resultJsonUpload.setVisibility(View.VISIBLE);

            //display action
            if (modelIn.getAction().contains("wipe") && modelIn.getParams().getAlgorithms().size() != 0) {
                //enable wipe data
                wipeData.setVisibility(View.VISIBLE);
                factoryReset.setVisibility(View.GONE);
                resultJsonUpload.append("Algoritmi de aplicat:\n");
                for (Algorithm alg : modelIn.getParams().getAlgorithms()) {
                    if ("on".equals(alg.getRun())) {
                        resultJsonUpload.append(alg.getName() + "\n");
                    }
                }
            } else {
                //enable factory reset
                factoryReset.setVisibility(View.VISIBLE);
                wipeData.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
