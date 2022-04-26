package com.start.apps.pheezee.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import start.apps.pheezee.R;

public class PackageTypes {
    public static final int STANDARD_PACKAGE = 1;
    public static final int GOLD_PACKAGE = 2;
    public static final int TEACH_PACKAGE = 3;
    public static final int GOLD_PLUS_PACKAGE = 4;
    public static final int ACHEDAMIC_TEACH_PLUS = 5;


    public static final int NUMBER_OF_VALUES_FOR_BASE_LINE = 10;
    public static final int ONE_STAR_VALUE = 20;
    public static final int SECOND_TIME_FOR_STAR = 1;
    public static final int NUMBER_OF_STARTS = 5;

    public static final int PERCENTAGE_TEXT_TO_SPEACH_EMG_PEAK = 70;
    public static final int MIN_PEAK_DECIDED = 30;

    public static final int NUMBER_OF_PATIENTS_THAT_CAN_BE_ADDED = 200;
    private static View layoutInflater;

    public static void showPatientAddingReachedDialog(Context context, String phizioemail, int package_type, String phizioname, String phone) {
        if (context != null) {
            String curent_package = getPackage(package_type);
        // Chnage for New Dialog box

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.notification_dialog_box);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setAttributes(lp);

            TextView notification_title = dialog.findViewById(R.id.notification_box_title);
            TextView notification_message = dialog.findViewById(R.id.notification_box_message);

            Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);
            Button Notification_Button_cancel = (Button) dialog.findViewById(R.id.notification_ButtonCancel);

            Notification_Button_ok.setText("Update");
            Notification_Button_cancel.setText("Cancel");

            // Setting up the notification dialog
            notification_title.setText("Limit Reached");
            notification_message.setText("Patient limit has been reached. \nPlease delete patients to add new ones. \nTo increase the limit, please upgrade the limit");

            Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"care@startoonlabs.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "upgrade for " + phizioemail);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Patient adding limit has been reached. \n Name: " + phizioname + '\n' + "Mobile Number: " + phone + '\n' + "My Current Package: " + curent_package);
                    context.startActivity(Intent.createChooser(emailIntent, "Send Email"));

                    dialog.dismiss();
                }
            });
            // On click Cancel
            Notification_Button_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });

            dialog.show();




         // final AlertDialog.Builder alert = new AlertDialog.Builder(context);
           // View mView = getLayoutInflater().inflate(R.layout.custom_dialog,null);

            //TextView textView = (TextView) mView.findViewById(R.id.txt1);
            //TextView textView1 = (TextView) mView.findViewById(R.id.txt2);
           // Button btn_update = (Button) mView.findViewById(R.id.btn_cancel);
           // Button btn_cancel = (Button) mView.findViewById(R.id.btn_update);

            //alert.setView(mView);
          //  final AlertDialog alertDialog = alert.create();
          //  alertDialog.setCanceledOnTouchOutside(false);




        }
    }

       /*   builder.setTitle("Limit Reached");
            builder.setMessage("Patient limit has been reached. Please delete patients to add new ones. \nTo increase the limit, please upgrade" +
                    " the limit.");
            builder.setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"customercare@start.com"});
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
           builder.show();/*
        }
    }*/

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
