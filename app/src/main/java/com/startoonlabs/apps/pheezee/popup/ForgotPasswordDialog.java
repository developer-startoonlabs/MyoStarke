package com.startoonlabs.apps.pheezee.popup;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.startoonlabs.apps.pheezee.R;

public class ForgotPasswordDialog {
    OnForgotPasswordListner listner;
    AlertDialog mdialog;
    Context context;
    public ForgotPasswordDialog(Context context){
        this.context = context;
    }

    public void showDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        builder.setTitle("Please enter new password");
        final View dialogLayout = inflater.inflate(R.layout.popup_new_password, null);
        final EditText et_new_password = dialogLayout.findViewById(R.id.et_new_password);
        final EditText et_new_password_confirm = dialogLayout.findViewById(R.id.et_confirm_new_password);
        et_new_password.setHint("Please enter new password");
        et_new_password_confirm.setHint("Please re-enter new password");
        builder.setPositiveButton("Update", null);
        builder.setNegativeButton("Cancel", null);
        builder.setView(dialogLayout);
        mdialog = builder.create();
        mdialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button p = mdialog.getButton(AlertDialog.BUTTON_POSITIVE);
                p.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
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
                Button n = mdialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                n.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mdialog.dismiss();
                    }
                });
            }
        });
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();
    }

    public void dismiss(){
        mdialog.dismiss();
    }



    public interface OnForgotPasswordListner{
        void onUpdateClicked(boolean flag, String message);
    }

    public void setOnForgotPasswordListner(OnForgotPasswordListner listner){
        this.listner = listner;
    }
}
