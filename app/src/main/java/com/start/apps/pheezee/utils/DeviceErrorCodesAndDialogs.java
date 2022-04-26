package com.start.apps.pheezee.utils;
import android.app.Dialog;
import android.widget.Button;
import android.widget.TextView;
import start.apps.pheezee.R;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

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

    static String error_heading="Device Error";
    static String error_message="Device is facing issue. Please contact us at care@startoonlabs.com to resolve.";

    public static void showDeviceErrorDialog(String error, Context context){

        //
        // Custom notification added by Haaris
        // custom dialog


        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.notification_dialog_box_single_button);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setAttributes(lp);

        TextView notification_title = dialog.findViewById(R.id.notification_box_title);
        TextView notification_message = dialog.findViewById(R.id.notification_box_message);

        Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);

        Notification_Button_ok.setText("Okay");

        // Setting up the notification dialog
        notification_title.setText(error_heading);
        notification_message.setText(error_message);

        // On click on Continue
        Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();


            }
        });


        dialog.show();



        // End

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
        // Reseting the string
        error_heading="Device Error";
        error_message = "Device is facing issue. Please contact us at care@startoonlabs.com to resolve.";
        try {



            if((information_packet[2]&0xFF)==1){
                error = error.concat(String.valueOf(UPPER_LSM_INIT))+", ";
                error_heading = error_heading.concat(String.valueOf(UPPER_LSM_INIT))+", ";
            }if((information_packet[7]&0xFF)==1){
                error = error.concat(String.valueOf(UPPER_LSM_REGISTER_READ))+", ";
                error_heading = error_heading.concat(String.valueOf(UPPER_LSM_REGISTER_READ))+", ";
            }if((information_packet[3]&0xFF)==1){
                error = error.concat(String.valueOf(LOWER_LSM_INIT))+", ";
                error_heading = error_heading.concat(String.valueOf(LOWER_LSM_INIT))+", ";
                Log.i("Error",error);
            }if((information_packet[8]&0xFF)==1){
                error = error.concat(String.valueOf(LOWER_LSM_REGISTER_READ))+", ";
                error_heading = error_heading.concat(String.valueOf(LOWER_LSM_REGISTER_READ))+", ";
                Log.i("Error",error);
            }if((information_packet[5]&0xFF)==1){
                error = error.concat(String.valueOf(ATTINY_ERROR))+", ";
                error_heading = error_heading.concat(String.valueOf(ATTINY_ERROR))+", ";
                Log.i("Error",error);
            }if((information_packet[6]&0xFF)==1){
                error = error.concat(String.valueOf(ADC_INIT))+", ";
                error_heading = error_heading.concat(String.valueOf(ADC_INIT))+", ";
                Log.i("Error",error);
            }if((information_packet[4]&0xFF)==1){
                error = error.concat(String.valueOf(GAIN_AMPLIFIER_INIT))+", ";
                error_heading = error_heading.concat(String.valueOf(GAIN_AMPLIFIER_INIT))+", ";
                Log.i("Error",error);
            }if((information_packet[18]&0xFF)==1){
                error = error.concat(String.valueOf(LDO_STATUS))+", ";
                error_heading = error_heading.concat(String.valueOf(LDO_STATUS))+", ";
                Log.i("Error",error);
            }if((information_packet[19]&0xFF)==1){
                error = error.concat(String.valueOf(OVER_CURRENT_PROTECTION_STATUS))+", ";
                error_heading = error_heading.concat(String.valueOf(OVER_CURRENT_PROTECTION_STATUS))+", ";
                Log.i("Error",error);
            }
            // Removing the comma
            error_heading = error_heading.substring(0,error_heading.length()-2);
            error_heading = error_heading + ")";

            // Single Errors
            // Gain amplifier
            if((information_packet[2]&0xFF)==0 && (information_packet[7]&0xFF)==0 && (information_packet[3]&0xFF)==0 && (information_packet[8]&0xFF)==0 && (information_packet[5]&0xFF)==0 && (information_packet[6]&0xFF)==0 && (information_packet[4]&0xFF)==1)
            {
                error_heading = "Device Alert ";
                error_message = "EMG view is restricted. Please contact us at care@startoonlabs.com to resolve.";

            }

            // Battery error
            if((information_packet[2]&0xFF)==0 && (information_packet[7]&0xFF)==0 && (information_packet[3]&0xFF)==0 && (information_packet[8]&0xFF)==0 && (information_packet[5]&0xFF)==1 && (information_packet[6]&0xFF)==0 && (information_packet[4]&0xFF)==0)
            {
                error_heading = "Device Alert ";
                 error_message ="Battery status is not communicated. Please contact us at care@startoonlabs.com to resolve.";
            }


            error = error.concat("Please restart the device.");
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
