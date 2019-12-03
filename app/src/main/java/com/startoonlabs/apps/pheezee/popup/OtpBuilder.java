package com.startoonlabs.apps.pheezee.popup;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.activities.LoginActivity;

import java.util.logging.Handler;

import retrofit2.http.POST;

public class OtpBuilder {
    AlertDialog mdialog = null;
    OtpResponseListner listner;
    Context context;
    String otp;

    public OtpBuilder(Context context, String otp){
        this.context = context;
        this.otp = otp;
    }

    public void showDialog(){

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        builder.setTitle("Please enter otp");
        builder.setMessage("Otp has been sent to the specified email, Please enter the otp.");
        builder.setCancelable(false);
        final View dialogLayout = inflater.inflate(R.layout.pop_otp, null);
        final PinEntryEditText editText = dialogLayout.findViewById(R.id.txt_pin_entry);
        builder.setPositiveButton("Continue",null);
        builder.setNegativeButton("Cancel",null);
        builder.setNeutralButton("Resend",null);
        builder.setView(dialogLayout);
        mdialog = builder.create();
        mdialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button p = mdialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
                p.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if(editText.getText().toString().length()==4){
                            if(editText.getText().toString().equals(otp)){
                                mdialog.dismiss();
                            }
                            else {
                                showToast("Invalid Otp");
                            }
                        }else {
                            showToast("Invalid Otp");
                        }
                    }
                });
                Button n = mdialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                n.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mdialog.dismiss();
                    }
                });

                Button nu = mdialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                nu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listner!=null){
                            listner.onResendClick();
                        }
                    }
                });

            }
        });
        editText.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence str) {
                if (str.toString().equals(otp)) {
                    if (listner!=null){
                        listner.onPinEntery(true);
                    }
                }
                else {
                    if (listner!=null){
                        listner.onPinEntery(false);
                    }
                }
            }
        });
        mdialog.show();
    }

    public void dismiss(){
        mdialog.dismiss();
    }


    public interface OtpResponseListner{
        void onResendClick();
        void onPinEntery(boolean pin);
    }

    public void setOnOtpResponseListner(OtpResponseListner listner){
        this.listner = listner;
    }

    public void showToast(String message){
        if(context!=null){
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

}
