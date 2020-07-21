package com.startoonlabs.apps.pheezee.popup;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.startoonlabs.apps.pheezee.R;
import android.widget.Button;

import android.app.Dialog;
import android.widget.TextView;


public class ForgotPasswordDialog {
    OnForgotPasswordListner listner;
    Context context;
    Dialog dialog;
    public ForgotPasswordDialog(Context context){
        this.context = context;
    }

    public void showDialog(){

        // Custom notification added by Haaris
        // custom dialog
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.notification_dialog_forgot_password);

        TextView notification_title = dialog.findViewById(R.id.notification_box_title);

        Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);
        Button Notification_Button_cancel = (Button) dialog.findViewById(R.id.notification_ButtonCancel);

        final EditText et_new_password = dialog.findViewById(R.id.et_new_password);
        final EditText et_new_password_confirm = dialog.findViewById(R.id.et_confirm_new_password);

        Notification_Button_ok.setText("Update");
        Notification_Button_cancel.setText("Cancel");

        // Setting up the notification dialog
        notification_title.setText("Change Password");
        et_new_password.setHint("Please enter new password");
        et_new_password_confirm.setHint("Please re-enter new password");

        // On click on Continue
        Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_new_password.getText().toString().equalsIgnoreCase("") && et_new_password.getText().toString().equals(et_new_password_confirm.getText().toString())) {
                    if(listner!=null){
                        listner.onUpdateClicked(true,et_new_password.getText().toString());
                    }
                }
                else if (et_new_password.getText().toString().equalsIgnoreCase("")){
                    if(listner!=null){
                        listner.onUpdateClicked(false,"Please enter new Password");
                    }
                }
                else {
                    if(listner!=null){
                        listner.onUpdateClicked(false,"Passwords do not match");
                    }
                }

            }
        });
        // On click Cancel
        Notification_Button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });



        // End


        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }



    public interface OnForgotPasswordListner{
        void onUpdateClicked(boolean flag, String message);
    }

    public void setOnForgotPasswordListner(OnForgotPasswordListner listner){
        this.listner = listner;
    }
}
