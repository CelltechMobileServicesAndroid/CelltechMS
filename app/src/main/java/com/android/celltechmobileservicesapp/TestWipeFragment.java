package com.android.celltechmobileservicesapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.celltechmobileservicesapp.alghoritms.BSIAlghorithm;
import com.android.celltechmobileservicesapp.alghoritms.DODAlghorithm;
import com.android.celltechmobileservicesapp.alghoritms.HMG5Alghorithm;
import com.android.celltechmobileservicesapp.alghoritms.HMG5AlghorithmRefactored;
import com.android.celltechmobileservicesapp.alghoritms.NCSCAlghorithm;
import com.android.celltechmobileservicesapp.alghoritms.NISTAlghorithm;
import com.android.celltechmobileservicesapp.alghoritms.RandAlghorithm;
import com.android.celltechmobileservicesapp.json.Algorithm;
import com.android.celltechmobileservicesapp.json.InJsonModel;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TestWipeFragment extends Fragment {
    String TAG = TestWipeFragment.class.getSimpleName();
    View mFragmentContainer;
    public TextView algRunning;
    public TextView algFreeSpace;
    InJsonModel model;
    List<String> listAlgsToApply;
    List<Algorithm> listAlgsResults = new ArrayList<>();
    public Algorithm currentAlgResult = new Algorithm();
    int indexCurrentAlg;
    public CircularProgressBar circularProgressBar;
    String fromFragment;

    public static TestWipeFragment newInstance(InJsonModel model, String fromFragment) {
        TestWipeFragment myFragment = new TestWipeFragment();

        Bundle args = new Bundle();
        args.putSerializable("inModel", model);
        args.putString("fromFragment", fromFragment);
        myFragment.setArguments(args);

        return myFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mFragmentContainer = inflater.inflate(R.layout.wipe_fragment, null);

        algRunning = mFragmentContainer.findViewById(R.id.alg_running);
        algRunning.setMovementMethod(new ScrollingMovementMethod());
        algRunning.setSelected(true);
        algFreeSpace = mFragmentContainer.findViewById(R.id.alg_free_space);
        circularProgressBar = mFragmentContainer.findViewById(R.id.alg_loading);

        model = (InJsonModel) getArguments().get("inModel");
        fromFragment = getArguments().getString("fromFragment");

        //total algs initialize
        listAlgsToApply = new ArrayList<>();

        for (int i = 0; i < model.getParams().getAlgorithms().size(); i++) {
            Algorithm alg = model.getParams().getAlgorithms().get(i);
            if ("on".equals(alg.getRun())) {
                listAlgsToApply.add(alg.getName());
            }
        }

        //todo for tests
//        listAlgsToApply.add("Rand");
//        listAlgsToApply.add("HMG5");
//        listAlgsToApply.add("HMG5Refactored");
//        listAlgsToApply.add("NIST");
//        listAlgsToApply.add("BSI");
//        listAlgsToApply.add("DOD");
//        listAlgsToApply.add("NCSC");

        indexCurrentAlg = -1;
        startApplingAlghoritms();
        return mFragmentContainer;
    }

    public void startApplingAlghoritms() {

        if (indexCurrentAlg > -1) {
            listAlgsResults.add(currentAlgResult);
            currentAlgResult = new Algorithm();
        }

        algFreeSpace.setVisibility(View.GONE);
        indexCurrentAlg++;

        if (indexCurrentAlg >= listAlgsToApply.size()) {
            circularProgressBar.setVisibility(View.GONE);

            algRunning.append(Html.fromHtml("<b><br>Stergerea datelor s-a finalizat.</b>"));

            if ("TestFragment".equals(fromFragment)) {
                return;
            }

            //go back to home to send results
            try {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                String title = "HOME";
                MainFragment fragm = (MainFragment) fragmentManager.findFragmentByTag(title);
                if (fragm != null) {
                    Log.d(TAG, "goback to send results");
                    fragm.listAlgsResults = listAlgsResults;
                    Constants.processStatus = 3;
                    fragmentManager.popBackStack();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        String curentAlgName = listAlgsToApply.get(indexCurrentAlg);
        //Algoritm in curs: 1/x - XXX
        algRunning.append(Html.fromHtml(String.format(Locale.getDefault(), "<br><br><b>Algoritm in curs: %d/%d - %s</b>", indexCurrentAlg + 1, listAlgsToApply.size(), curentAlgName)));

        circularProgressBar.setProgress(0);
        circularProgressBar.setProgressMax(100);

        switch (curentAlgName) {
            case "HMG5":
                new HMG5Alghorithm(getActivity(), this, curentAlgName).wipeData();
                break;
            case "NIST":
                new NISTAlghorithm(getActivity(), this, curentAlgName).wipeData();
                break;
            case "DOD":
                new DODAlghorithm(getActivity(), this, curentAlgName).wipeData();
                break;
            case "NCSC":
                new NCSCAlghorithm(getActivity(), this, curentAlgName).wipeData();
                break;
            case "BSI":
                new BSIAlghorithm(getActivity(), this, curentAlgName).wipeData();
                break;
            case "Rand":
                new RandAlghorithm(getActivity(), this, curentAlgName).wipeData();
                break;
            case "HMG5Refactored":
                new HMG5AlghorithmRefactored(getActivity(), this, curentAlgName).wipeData();
                break;
        }
    }

//    public void fakeLoading() {
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                startApplingAlghoritms();
//            }
//        }, 10000);   //10 seconds
//    }

    @Override
    public void onPause() {
        try {
            String curentAlgName = listAlgsToApply.get(indexCurrentAlg);
            switch (curentAlgName) {
                case "HMG5":
                    HMG5Alghorithm.pause();
                    break;
                case "NIST":
                    NISTAlghorithm.pause();
                    break;
                case "DOD":
                    DODAlghorithm.pause();
                    break;
                case "NCSC":
                    NCSCAlghorithm.pause();
                    break;
                case "BSI":
                    BSIAlghorithm.pause();
                    break;
                case "Rand":
                    RandAlghorithm.pause();
                    break;
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        try {
            String curentAlgName = listAlgsToApply.get(indexCurrentAlg);
            switch (curentAlgName) {
                case "HMG5":
                    HMG5Alghorithm.resume();
                    break;
                case "NIST":
                    NISTAlghorithm.resume();
                    break;
                case "DOD":
                    DODAlghorithm.resume();
                    break;
                case "NCSC":
                    NCSCAlghorithm.resume();
                    break;
                case "BSI":
                    BSIAlghorithm.resume();
                    break;
                case "Rand":
                    RandAlghorithm.resume();
                    break;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        super.onResume();
    }

}
