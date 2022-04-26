package com.start.apps.pheezee.activities;

import static com.start.apps.pheezee.activities.PatientsView.json_phizioemail;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import start.apps.pheezee.R;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.trncic.library.DottedProgressBar;

import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.start.apps.pheezee.popup.ForgotPasswordDialog;
import com.start.apps.pheezee.popup.OtpBuilder;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;
import com.start.apps.pheezee.utils.NetworkOperations;

import android.widget.Button;

public class ChangePassword extends AppCompatActivity implements MqttSyncRepository.OnLoginResponse {
    SharedPreferences sharedPref;
    DottedProgressBar dottedProgressBar;
    String str_login_email, str_login_password;
    ProgressDialog progressDialog;

    // TextView tv_login,tv_welcome_message,tv_login_welcome_user,tv_signup_screen;
    //LinearLayout ll_login,ll_signin_section,tv_forgot_password,ll_signup_section,ll_welcome;
    Button btn_forgot_password,btn_cancel, btn_login;
    View img_cancel;
    /*RelativeLayout rl_login_section;*/
    TextView et_mail,et_password;
    GetDataService getDataService;
    MqttSyncRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initializeView();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        repository = new MqttSyncRepository(getApplication());
        repository.setOnLoginResponse(this);
    }



    /**
     * disables the welcome view
     */
        /*private void disableWelcomeView() {
                ll_welcome.setVisibility(View.INVISIBLE);
                dottedProgressBar.setVisibility(View.INVISIBLE);
        }*/

    /**
     * Enables the previous view with edit texts etc
     */
      /*  private void enablePreviousView() {
                ll_signup_section.setVisibility(View.VISIBLE);
                ll_signin_section.setVisibility(View.VISIBLE);
                rl_login_section.setVisibility(View.VISIBLE);
        }*/

    private void initializeView() {
        //    final Animation animation_up = AnimationUtils.loadAnimation(ChangePassword.this, R.anim.slide_up_dialog);
//        tv_login = findViewById(R.id.btn_login_login);
        // rl_login_section = findViewById(R.id.rl_login_section);
        //  ll_signin_section = findViewById(R.id.layout_signin);
        btn_login = findViewById(R.id.btn_login);
        btn_forgot_password = findViewById(R.id.btn_forgot_password);
        dottedProgressBar = findViewById(R.id.dot_progress_bar);
        et_mail = findViewById(R.id.login_et_email);
        Intent intent = new Intent();
        //intent.putExtra("et_phizio_email",json_phizioemail);
        intent.putExtra("et_phizio_email", json_phizioemail);
        String str1 = intent.getStringExtra("et_phizio_email");
        et_mail.setText(str1);

        img_cancel = findViewById(R.id.iv_back_phizio_profile);
        //  et_password = findViewById(R.id.login_et_password);
        //    ll_signup_section = findViewById(R.id.ll_login_btn);
        // ll_welcome = findViewById(R.id.ll_welcome_section);
        //  tv_welcome_message = findViewById(R.id.tv_welcome_message);
        //    tv_login_welcome_user = findViewById(R.id.login_tv_welcome_user);
        //   tv_signup_screen = findViewById(R.id.login_tv_signup);
        progressDialog = new ProgressDialog(this,R.style.greenprogress);
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);

        // Skipping the login flow - Haaris 10/7/2020
        setTheme(R.style.AppTheme_NoActionBarLogin);
        //rl_login_section.startAnimation(animation_up);
        //    ll_signin_section.setVisibility(View.VISIBLE);
        //      ll_signin_section.startAnimation(animation_up);

        et_mail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==66)
                    et_password.requestFocus();
                return false;
            }
        });

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

             /*   tv_signup_screen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { startActivity(new Intent(ChangePassword.this,SignUpActivity.class)); }
                });*/

//        tv_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setTheme(R.style.AppTheme_NoActionBarLogin);
//                rl_login_section.startAnimation(animation_up);
//                ll_signin_section.setVisibility(View.VISIBLE);
//                ll_signin_section.startAnimation(animation_up);
//                ll_login.setVisibility(View.GONE);
//            }
//        });

             /*   btn_login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if (NetworkOperations.isNetworkAvailable(ChangePassword.this)) {
                                        str_login_email = et_mail.getText().toString();
                                        str_login_password = et_password.getText().toString();
                                        if(RegexOperations.isLoginValid(str_login_email,str_login_password)) {
                                                repository.loginUser(str_login_email, str_login_password);
                                                disablePreviousView();
                                                enableWelcomeView();
                                                setWelcomeText("Logging in..");
                                                dottedProgressBar.startProgress();
                                        }
                                        else {
                                                showToast(RegexOperations.getNonValidMessageLogin(str_login_email,str_login_password));
                                        }
                                }
                                else { NetworkOperations.networkError(ChangePassword.this); }
                        }
                });*/




        btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_login_email = et_mail.getText().toString();
                if (NetworkOperations.isNetworkAvailable(ChangePassword.this)) {
                    if (!str_login_email.equalsIgnoreCase("")) {
                        setTheme(R.style.AppTheme_NoActionBar);
                        progressDialog.show();
                        repository.forgotPassword(str_login_email);
                    } else {
                        Toast.makeText(ChangePassword.this, "Please enter the email address!", Toast.LENGTH_SHORT).show();
                    }
                }
                else { NetworkOperations.networkError(ChangePassword.this); }
            }
        });

    }

       /* private void setWelcomeText(String str){
                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                tv_welcome_message.setText(str);
                        }
                });
        }*/

        /*private void enableWelcomeView() {
                ll_welcome.setVisibility(View.VISIBLE);
                dottedProgressBar.setVisibility(View.VISIBLE);
        }*/

        /*private void disablePreviousView() {
                ll_signup_section.setVisibility(View.INVISIBLE);
                ll_signin_section.setVisibility(View.INVISIBLE);
                rl_login_section.setVisibility(View.INVISIBLE);
        }*/

    @SuppressLint("ResourceType")
    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }






    @Override
    public void onDetachedFromWindow() { super.onDetachedFromWindow(); }

    @Override
    public void onLoginResponse(boolean response, String message) {
              /*  if(response){
                        /*setWelcomeText("Welcome");
                        tv_login_welcome_user.setText("Dr. "+ message);
                        dottedProgressBar.startProgress();
                        new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                        Intent i = new Intent(ChangePassword.this, PatientsView.class);
                                        startActivity(i);
                                        finish();
                                        dottedProgressBar.stopProgress();
                                }
                        },500);*/
    }
                /*else {
                        setWelcomeText(message);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                        disableWelcomeView();
                                        enablePreviousView();
                                }
                        },1000);
                }*/
    /*}*/

    @Override
    public void onForgotPasswordResponse(boolean response, String message) {
        progressDialog.dismiss();
        if(response){
            OtpBuilder builder = new OtpBuilder(this,message);
            builder.showDialog();
            builder.setOnOtpResponseListner(new OtpBuilder.OtpResponseListner() {

                @Override
                public void onResendClick() {
                    builder.dismiss();
                    progressDialog.show();
                    repository.forgotPassword(str_login_email);
                }

                @Override
                public void onPinEntery(boolean pin) {
                    if(pin){
                        builder.dismiss();
                        ForgotPasswordDialog dialog = new ForgotPasswordDialog(ChangePassword.this);
                        dialog.showDialog();
                        dialog.setOnForgotPasswordListner(new ForgotPasswordDialog.OnForgotPasswordListner() {
                            @Override
                            public void onUpdateClicked(boolean flag, String message) {
                                if(flag){
                                    dialog.dismiss();
                                    progressDialog.show();
                                    repository.updatePassword(str_login_email,message);
                                }
                                else { showToast(message); }
                            }
                        });
                    }
                    else { showToast("Invalid OTP"); }
                }
            });
        }
        else { showToast(message); }
    }

    @Override
    public void onPasswordUpdated(String message) {
        progressDialog.dismiss();
        showToast(message);
        finish();
    }



}

