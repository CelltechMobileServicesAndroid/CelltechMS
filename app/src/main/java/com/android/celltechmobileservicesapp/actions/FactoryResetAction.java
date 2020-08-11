package com.android.celltechmobileservicesapp.actions;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.android.celltechmobileservicesapp.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
