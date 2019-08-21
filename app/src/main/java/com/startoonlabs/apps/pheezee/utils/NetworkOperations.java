package com.startoonlabs.apps.pheezee.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AlertDialog;

public class NetworkOperations {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void networkError(Context context){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Network Error");
        builder.setMessage("Please connect to internet and try again");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    public static void openServerBusyDialog(Context context){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Server Not Responding");
        builder.setMessage("The server is not responding. Sorry for the inconvenience, please try again later.... ");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }


}
