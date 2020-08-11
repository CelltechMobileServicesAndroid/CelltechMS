package com.android.celltechmobileservicesapp.actions;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import com.android.celltechmobileservicesapp.MainActivity;

public class FactoryResetAction {

    public static void finalizeFactoryResetWithAlert(final MainActivity activity) {
        AlertDialog.Builder builderW = new AlertDialog.Builder(activity);
        builderW.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                activity.mDPM.wipeData(0);
                // apare mesaj : Revenire la setari fabrica... si apoi se restarteaza
            }
        });
        builderW.setNegativeButton("Anulare", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builderW.setMessage("\n Ati lansat actiunea de Factory reset, Continuati?").create().show();
    }

    public static void finalizeFactoryResetWithoutAlert(final MainActivity activity) {
        activity.mDPM.wipeData(0);
        // apare mesaj : Revenire la setari fabrica... si apoi se restarteaza
    }
}
