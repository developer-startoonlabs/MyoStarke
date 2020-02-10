package com.startoonlabs.apps.pheezee.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.pojos.SignUpData;
import com.startoonlabs.apps.pheezee.popup.OtpBuilder;
import com.startoonlabs.apps.pheezee.repository.MqttSyncRepository;
import com.startoonlabs.apps.pheezee.utils.RegexOperations;

import java.util.ArrayList;


public class SignUpActivity extends AppCompatActivity implements MqttSyncRepository.OnSignUpResponse {




    EditText et_signup_name, et_signup_password, et_signup_email, et_signup_phone, et_package_id;

    //String to save edittexts
    String str_signup_name, str_signup_password,str_signup_email,str_signup_phone, str_package_id;

    Button btn_signup_create;
    TextView tv_signup_cancel;

    boolean dialogStatus = false;
    MqttSyncRepository repository;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        repository = new MqttSyncRepository(getApplication());
        repository.setOnSignUpResponse(this);
        //defining all the view elements
        dialogStatus = false;
        //EDIT TEXTS
        et_signup_name = findViewById(R.id.et_signup_name);
        et_signup_password = findViewById(R.id.et_signup_password);
        et_signup_email = findViewById(R.id.et_signup_email);
        et_signup_phone = findViewById(R.id.et_signup_phone);
        et_package_id = findViewById(R.id.et_package_id);

        progressDialog = new ProgressDialog(this,R.style.greenprogress);
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        //Buttons
        btn_signup_create  = findViewById(R.id.btn_signup_create);
        //TextViews
        tv_signup_cancel = findViewById(R.id.tv_cancel_signup);
        /**
         * create account
         */
        btn_signup_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_signup_name = et_signup_name.getText().toString();
                str_signup_password = et_signup_password.getText().toString();
                str_signup_email = et_signup_email.getText().toString();
                str_signup_phone = et_signup_phone.getText().toString();
                str_package_id = et_package_id.getText().toString();
                if(RegexOperations.isSignupValid(str_signup_name,str_signup_email,str_signup_password,str_signup_phone,str_package_id)){
                    progressDialog.show();
                    repository.confirmEmail(str_signup_email,str_package_id);
                }
                else {
                    showToast(RegexOperations.getNonValidMessageSignup(str_signup_name,str_signup_email,str_signup_password,str_signup_phone, str_package_id));
                }
            }
        });
        tv_signup_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_redirectlogin = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent_redirectlogin);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConfirmEmail(boolean response, String message) {
        progressDialog.dismiss();
        if(response){
            OtpBuilder builder = new OtpBuilder(this,message);
            builder.showDialog();
            builder.setOnOtpResponseListner(new OtpBuilder.OtpResponseListner() {
                @Override
                public void onResendClick() {
                    builder.dismiss();
                    progressDialog.show();
                    repository.confirmEmail(str_signup_email,str_package_id);
                }

                @Override
                public void onPinEntery(boolean pin) {
                    if(pin){
                        progressDialog.show();
                        SignUpData data = new SignUpData(str_signup_name,str_signup_email,str_signup_password,
                                str_signup_phone,"empty", new ArrayList<>(), str_package_id);
                        repository.signUp(data);
                    }
                    else {
                        showToast("Invalid pin!");
                    }
                }
            });
        }
        else {
            showToast(message);
        }
    }

    @Override
    public void onSignUp(boolean response) {
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
        if(!response){
            showToast("Error, try again later");
        }
        else {
            Intent i = new Intent(SignUpActivity.this, PatientsView.class);
            startActivity(i);
            finish();
        }
    }
}
