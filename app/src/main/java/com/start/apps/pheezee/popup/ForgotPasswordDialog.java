package com.start.apps.pheezee.popup;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import start.apps.pheezee.R;

import android.app.Dialog;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;


public class ForgotPasswordDialog extends AppCompatActivity {
    OnForgotPasswordListner listner;
    Context context;
    Dialog dialog;




    public ForgotPasswordDialog(Context context){
        this.context = context;
    }

//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.notification_dialog_forgot_password);
//        TextView notification_title = dialog.findViewById(R.id.notification_box_title);
//        Button Notification_Button_ok = findViewById(R.id.notification_ButtonOK);
//        ImageView imageview_back = findViewById(R.id.imageview_back);
//        final EditText et_new_password = findViewById(R.id.et_new_password);
//        final EditText et_new_password_confirm = findViewById(R.id.et_confirm_new_password);
//        Notification_Button_ok.setText("Reset Password");
//        Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!et_new_password.getText().toString().equalsIgnoreCase("") && et_new_password.getText().toString().equals(et_new_password_confirm.getText().toString())) {
//                    if(listner!=null){
//                        listner.onUpdateClicked(true,et_new_password.getText().toString());
//                    }
//                }
//                else if (et_new_password.getText().toString().equalsIgnoreCase("")){
//                    if(listner!=null){
//                        listner.onUpdateClicked(false,"Please enter new Password");
//                    }
//                }
//                else {
//                    if(listner!=null){
//                        listner.onUpdateClicked(false,"Passwords do not match");
//                    }
//                }
//
//            }
//        });
//
//        imageview_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listner!=null){
//                    dismiss();
//                }
//
//
//            }
//        });
//
//
//    }

    public void showDialog(){

        // Custom notification added by Haaris
        // custom dialog
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.notification_dialog_forgot_password);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.getWindow().setAttributes(lp);

        TextView notification_title = dialog.findViewById(R.id.notification_box_title);

        Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);
        Button Notification_Button_cancel = (Button) dialog.findViewById(R.id.notification_ButtonCancel);
        ImageView imageview_back = dialog.findViewById(R.id.iv_back_app_info);

        final EditText et_new_password = dialog.findViewById(R.id.et_new_password);
        final EditText et_new_password_confirm = dialog.findViewById(R.id.et_confirm_new_password);

        Notification_Button_ok.setText("Reset Password");
       // Notification_Button_cancel.setText("Cancel");

        // Setting up the notification dialog
        //notification_title.setText("Change Password");
       // et_new_password.setHint("Please enter new password");
        //et_new_password_confirm.setHint("Please re-enter new password");

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
     /*   Notification_Button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });*/

        imageview_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listner!=null){
                    dismiss();
                }


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
