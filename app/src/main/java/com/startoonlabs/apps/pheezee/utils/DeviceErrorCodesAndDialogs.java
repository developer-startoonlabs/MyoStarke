package com.startoonlabs.apps.pheezee.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.startoonlabs.apps.pheezee.activities.DeviceInfoActivity;
import com.startoonlabs.apps.pheezee.activities.PatientsView;

public class DeviceErrorCodesAndDialogs {
    public static long UPPER_LSM_INIT = 10;
    public static long UPPER_LSM_REGISTER_READ = 11;
    public static long LOWER_LSM_INIT = 20;
    public static long LOWER_LSM_REGISTER_READ = 21;
    public static long ATTINY_ERROR = 30;
    public static long ADC_INIT = 40;
    public static long GAIN_AMPLIFIER_INIT = 50;
    public static long LDO_STATUS = 60;
    public static long OVER_CURRENT_PROTECTION_STATUS = 70;


    public static void showDeviceErrorDialog(String error, Context context){
        AlertDialog mDeactivatedDialog = new AlertDialog.Builder(context)
                .setTitle("Device Error")
                .setMessage(error)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    public static boolean doalogToShow(byte[] information_packet){
        boolean error_present = false;
        try {
            if((information_packet[2]&0xFF)==1){
                error_present = true;
            }else if((information_packet[7]&0xFF)==1){
                error_present = true;
            }else if((information_packet[3]&0xFF)==1){
                error_present = true;
            }else if((information_packet[8]&0xFF)==1){
                error_present = true;
            }else if((information_packet[5]&0xFF)==1){
                error_present = true;
            }else if((information_packet[6]&0xFF)==1){
                error_present = true;
            }else if((information_packet[4]&0xFF)==1){
                error_present = true;
            }else if((information_packet[18]&0xFF)==1){
                error_present = true;
            }else if((information_packet[19]&0xFF)==1){
                error_present = true;
            }
        }catch (ArrayIndexOutOfBoundsException e){
            error_present = false;
        }catch (IndexOutOfBoundsException e){
            error_present = false;
        }
        return error_present;
    }

    public static boolean isSessionRedirectionEnabled(byte[] information_packet){
        boolean error_present = false;
        try {
            if((information_packet[2]&0xFF)==1){
                error_present = true;
            }else if((information_packet[7]&0xFF)==1){
                error_present = true;
            }else if((information_packet[3]&0xFF)==1){
                error_present = true;
            }else if((information_packet[8]&0xFF)==1){
                error_present = true;
            }else if((information_packet[6]&0xFF)==1){
                error_present = true;
            }else if((information_packet[18]&0xFF)==1){
                error_present = true;
            }else if((information_packet[19]&0xFF)==1){
                error_present = true;
            }
        }catch (ArrayIndexOutOfBoundsException e){
            error_present = false;
        }catch (IndexOutOfBoundsException e){
            error_present = false;
        }
        return error_present;
    }

    public static String getErrorCodeString(byte[] information_packet){
        String error = "Error Code ";
        try {
            if((information_packet[2]&0xFF)==1){
                error = error.concat(String.valueOf(UPPER_LSM_INIT))+", ";
            }if((information_packet[7]&0xFF)==1){
                error = error.concat(String.valueOf(UPPER_LSM_REGISTER_READ))+", ";
            }if((information_packet[3]&0xFF)==1){
                error = error.concat(String.valueOf(LOWER_LSM_INIT))+", ";
                Log.i("Error",error);
            }if((information_packet[8]&0xFF)==1){
                error = error.concat(String.valueOf(LOWER_LSM_REGISTER_READ))+", ";
                Log.i("Error",error);
            }if((information_packet[5]&0xFF)==1){
                error = error.concat(String.valueOf(ATTINY_ERROR))+", ";
                Log.i("Error",error);
            }if((information_packet[6]&0xFF)==1){
                error = error.concat(String.valueOf(ADC_INIT))+", ";
                Log.i("Error",error);
            }if((information_packet[4]&0xFF)==1){
                error = error.concat(String.valueOf(GAIN_AMPLIFIER_INIT))+", ";
                Log.i("Error",error);
            }if((information_packet[18]&0xFF)==1){
                error = error.concat(String.valueOf(LDO_STATUS))+", ";
                Log.i("Error",error);
            }if((information_packet[19]&0xFF)==1){
                error = error.concat(String.valueOf(OVER_CURRENT_PROTECTION_STATUS))+", ";
                Log.i("Error",error);
            }
            error = error.concat("please restart the device.");
        }catch (ArrayIndexOutOfBoundsException e){
            error = "No Error";
            e.printStackTrace();
        }catch (IndexOutOfBoundsException e){
            error = "No Error";
            e.printStackTrace();
        }
        return error;
    }
}
