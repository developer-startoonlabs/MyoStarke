package com.startoonlabs.apps.pheezee.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class PackageTypes {
    public static final int STANDARD_PACKAGE = 1;
    public static final int GOLD_PACKAGE = 2;
    public static final int TEACH_PACKAGE = 3;
    public static final int GOLD_PLUS_PACKAGE = 4;
    public static final int ACHEDAMIC_TEACH_PLUS = 5;


    public static final int NUMBER_OF_VALUES_FOR_BASE_LINE = 10;
    public static final int ONE_STAR_VALUE = 100;
    public static final int SECOND_TIME_FOR_STAR = 1;
    public static final int NUMBER_OF_STARTS = 5;

    public static final int NUMBER_OF_PATIENTS_THAT_CAN_BE_ADDED = 40;

    public static void showPatientAddingReachedDialog(Context context){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Limit Reached");
        builder.setMessage("Patient adding limit has been reached, please delete or archive patients to add new.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}
