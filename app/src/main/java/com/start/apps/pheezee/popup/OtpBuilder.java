package com.start.apps.pheezee.popup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import start.apps.pheezee.R;

public class OtpBuilder extends AppCompatActivity {
    Dialog dialog;
    OtpResponseListner listner;
    Context context;
    String otp;

    public OtpBuilder(Context context, String otp){
        this.context = context;
        this.otp = otp;
    }

    public void setContext(Context context) {
        this.context = context;
    }




    public void showDialog(){


        dialog = new Dialog(context);
        // Custom notification added by Haaris
        // custom dialog

        dialog.setContentView(R.layout.notification_dialog_box_otp);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);

        Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);
        TextView notification_otp_resend = dialog.findViewById(R.id.notification_otp_resend);
        TextView notification_skip = dialog.findViewById(R.id.notification_skip);
        ImageView imageview_back = dialog.findViewById(R.id.imageview_back);
        final PinEntryEditText editText = dialog.findViewById(R.id.txt_pin_entry_otp);

       // On click on Continue
        Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().length()==4){
                    if(editText.getText().toString().equals(otp)){
                        dialog.dismiss();
                    }
                    else {
                        showToast("Invalid OTP");
                    }
                }else {
                    showToast("Invalid OTP");
                }


            }
        });

        // Resend on click
        notification_otp_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listner!=null){
                    listner.onResendClick();
                }


            }
        });
        // End

        notification_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listner!=null){
                    dismiss();
                }


            }
        });
        imageview_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listner!=null){
                    dismiss();
                }


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
        dialog.show();
    }



    public void dismiss(){
        dialog.dismiss();
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
