package com.startoonlabs.apps.pheezee.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;

import com.startoonlabs.apps.pheezee.R;

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

    public static final int PERCENTAGE_TEXT_TO_SPEACH_EMG_PEAK = 70;

    public static final int NUMBER_OF_PATIENTS_THAT_CAN_BE_ADDED = 40;

    public static void showPatientAddingReachedDialog(Context context, String phizioemail, int package_type, String phizioname, String phone){
        if(context!=null) {
            String curent_package = getPackage(package_type);
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Limit Reached");
            builder.setMessage("Patient limit has been reached. Please delete patients to add new ones. \nTo increase the limit, please upgrade" +
                    " the limit.");
            builder.setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@pheezee.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "upgrade for " + phizioemail);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Patient adding limit has been reached. \n Name: " + phizioname + '\n' + "Mobile Number: " + phone + '\n' + "My Current Package: " + curent_package);
                    context.startActivity(Intent.createChooser(emailIntent, "Send Email"));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
    }

    private static String getPackage(int package_type) {
        if(package_type==STANDARD_PACKAGE){
            return "Standard";
        }else if(package_type==GOLD_PACKAGE){
            return "Gold";
        }else if(package_type==TEACH_PACKAGE){
            return "Teach Package";
        }else if(package_type==GOLD_PLUS_PACKAGE){
            return "Gold Plus";
        }else if(package_type==ACHEDAMIC_TEACH_PLUS){
            return "Achedamic Teach Plus";
        }else {
            return "No Package";
        }
    }
}
