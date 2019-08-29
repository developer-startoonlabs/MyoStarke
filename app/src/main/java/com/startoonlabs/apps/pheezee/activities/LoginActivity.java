package com.startoonlabs.apps.pheezee.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.PrimaryKey;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.pojos.LoginData;
import com.startoonlabs.apps.pheezee.pojos.LoginResult;
import com.startoonlabs.apps.pheezee.pojos.Phiziopatient;
import com.startoonlabs.apps.pheezee.repository.MqttSyncRepository;
import com.startoonlabs.apps.pheezee.retrofit.GetDataService;
import com.startoonlabs.apps.pheezee.retrofit.RetrofitClientInstance;
import com.startoonlabs.apps.pheezee.room.Entity.PhizioPatients;
import com.startoonlabs.apps.pheezee.room.PheezeeDatabase;
import com.startoonlabs.apps.pheezee.services.MqttHelper;
import com.startoonlabs.apps.pheezee.utils.NetworkOperations;
import com.startoonlabs.apps.pheezee.utils.OtpGeneration;
import com.startoonlabs.apps.pheezee.utils.RegexOperations;
import com.trncic.library.DottedProgressBar;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity implements MqttSyncRepository.OnLoginResponse {
    MqttHelper mqttHelper;
    int backpressCount = 0;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;
    DottedProgressBar dottedProgressBar;
    boolean dialogStatus = false;
    AlertDialog mdialog = null;
    String otp;
    //Srings for edittexts
    String str_login_email, str_login_password;
    ProgressDialog progressDialog;

    //Mqtt Topics
    String mqtt_subs_login_response = "login/phizio/response";    //phizio login response from server
    String mqtt_pubs_login_phizio = "login/phizio";               //phizio login response from server
    String mqtt_pubs_forgot_password = "forgot/password";
    String mqtt_pubs_update_password = "phizioprofile/update/password";
    String mqtt_pubs_update_password_response = "phizioprofile/update/password/response";


    TextView tv_signup,tv_login,tv_welcome_message,tv_login_welcome_user,tv_signup_screen;
    LinearLayout ll_login,ll_signin_section,btn_login,tv_forgot_password,ll_signup_section,ll_welcome;
    RelativeLayout rl_login_section;
    EditText et_mail,et_password;
    GetDataService getDataService;
    MqttSyncRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_continue);

        initializeView();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        mqttHelper = new MqttHelper(this);
        repository = new MqttSyncRepository(getApplication());
        repository.setOnLoginResponse(this);
        /**
         * Handles the data received from the server like, for forgot password and login response
         */
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.equals(mqtt_subs_login_response+str_login_email+str_login_password)){
                    String str = message.toString();
                    Log.i("message",message.toString());
                    if(str.equals("\"invalid\"")){
                        Log.i("MQTT MESSAGE RESPONSE", "User with this mail already exists");
                        setWelcomeText("Invalid Credentials");
//                        showToast("Invalid Credentials");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                disableWelcomeView();
                                enablePreviousView();
                            }
                        },1000);

                    }
                    else {
                        Log.i("inside","inside");
                        JSONArray array = new JSONArray(message.toString());
                        JSONObject object = array.getJSONObject(0);
                        object.remove("phiziopassword");
                        String name = object.getString("phizioname");
                        Log.i("hello","hello");
                        editor = sharedPref.edit();
                        editor.putBoolean("isLoggedIn",true);
                        editor.putString("phiziodetails",object.toString());
                        editor.commit();
                        Log.i("MQTT MESSAGE RESPONSE", object.toString());

                        setWelcomeText("Welcome");
                        tv_login_welcome_user.setText(name);
                        dottedProgressBar.startProgress();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(LoginActivity.this, PatientsView.class);
                                startActivity(i);
                                finish();
                                dottedProgressBar.stopProgress();
                            }
                        },1000);
                    }


                }
                else if (topic.equals(mqtt_pubs_forgot_password+str_login_email+otp)){
                    progressDialog.dismiss();
                    Log.i("message",message.toString());
                    if (message.toString().equals("sent")){
                        if (!dialogStatus) {
                            dialogStatus = true;
                            final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            builder.setTitle("Please enter otp");
                            builder.setMessage("Otp has been sent to the specified email, Please enter the otp.");
                            final View dialogLayout = inflater.inflate(R.layout.pop_otp, null);
                            final PinEntryEditText editText = dialogLayout.findViewById(R.id.txt_pin_entry);
                            builder.setPositiveButton("Resend",null);
                            builder.setNegativeButton("Cancel",null);
                            builder.setView(dialogLayout);
                            mdialog = builder.create();
                            mdialog.setOnShowListener(new DialogInterface.OnShowListener() {

                                @Override
                                public void onShow(final DialogInterface dialog) {

                                    Button p = mdialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    p.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {
                                            tv_forgot_password.performClick();
                                            dialogStatus = true;
                                        }
                                    });
                                    Button n = mdialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                                    n.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mdialog.dismiss();
                                            dialogStatus=false;
                                        }
                                    });
                                }
                            });
                            editText.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                                @Override
                                public void onPinEntered(CharSequence str) {
                                    if (str.toString().equals(otp)) {
                                        Toast.makeText(LoginActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                                        mdialog.dismiss();
                                        dialogStatus = false;
                                        if (!dialogStatus) {
                                            dialogStatus = true;
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                            LayoutInflater inflater = getLayoutInflater();
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
                                                            Toast.makeText(LoginActivity.this, ""+(!et_new_password.getText().toString().equalsIgnoreCase("") && et_new_password.getText().equals(et_new_password_confirm.getText())), Toast.LENGTH_SHORT).show();
                                                            if(!et_new_password.getText().toString().equalsIgnoreCase("") && et_new_password.getText().toString().equals(et_new_password_confirm.getText().toString())){
                                                                progressDialog.show();
                                                                mdialog.dismiss();

                                                                str_login_password = et_new_password.getText().toString();
                                                                try {
                                                                    mqttHelper.mqttAndroidClient.subscribe(mqtt_pubs_update_password_response+str_login_email+str_login_password, 1, null, new IMqttActionListener() {
                                                                        @Override
                                                                        public void onSuccess(IMqttToken asyncActionToken) { Log.w("Mqtt","Subscribed!");
                                                                            Log.i("credentials",str_login_email+" "+str_login_password);
                                                                            MqttMessage msg_update_password = new MqttMessage();
                                                                            JSONObject jsonObject = new JSONObject();
                                                                            try {
                                                                                jsonObject.put("phiziopassword",et_new_password.getText().toString());
                                                                                jsonObject.put("phizioemail",str_login_email);
                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            msg_update_password.setPayload(jsonObject.toString().getBytes());
                                                                            mqttHelper.publishMqttTopic(mqtt_pubs_update_password,msg_update_password);}

                                                                        @Override
                                                                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) { Log.w("Mqtt", "Subscribed fail!"); }
                                                                    });
                                                                } catch (MqttException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    });
                                                    Button n = mdialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                                                    n.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            mdialog.dismiss();
                                                            dialogStatus = false;
                                                        }
                                                    });
                                                }
                                            });
                                            mdialog.setCanceledOnTouchOutside(false);
                                            mdialog.show();
                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Invalid Otp Entered!", Toast.LENGTH_SHORT).show();
                                        editText.setText(null);
                                    }
                                }
                            });

                            mdialog.setCanceledOnTouchOutside(false);
                            mdialog.show();
                        }
                    }
                    else if(message.toString().equalsIgnoreCase("invalid")){
                        Toast.makeText(LoginActivity.this, "Please enter valid email!", Toast.LENGTH_SHORT).show();
                    }
                }

                else if(topic.equals(mqtt_pubs_update_password_response+str_login_email+str_login_password)){
                    progressDialog.dismiss();
                    if(message.toString().equalsIgnoreCase("updated"))
                        Toast.makeText(LoginActivity.this, "Password Updated, please login!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(LoginActivity.this, "Error please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    /**
     * disables the welcome view
     */
    private void disableWelcomeView() {
        ll_welcome.setVisibility(View.INVISIBLE);
        dottedProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Enables the previous view with edit texts etc
     */
    private void enablePreviousView() {
        ll_signup_section.setVisibility(View.VISIBLE);
        ll_signin_section.setVisibility(View.VISIBLE);
        rl_login_section.setVisibility(View.VISIBLE);
    }


    private void initializeView() {
        final Animation animation_up = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.slide_up_dialog);
        tv_login = findViewById(R.id.btn_login_login);
        ll_login = findViewById(R.id.ll_signup);
        rl_login_section = findViewById(R.id.rl_login_section);
        ll_signin_section = findViewById(R.id.layout_signin);
        tv_signup = findViewById(R.id.login_tv_signup);
        btn_login = findViewById(R.id.btn_login);
        tv_forgot_password = findViewById(R.id.btn_forgot_password);
        dottedProgressBar = findViewById(R.id.dot_progress_bar);
        et_mail = findViewById(R.id.login_et_email);
        et_password = findViewById(R.id.login_et_password);
        ll_signup_section = findViewById(R.id.ll_login_btn);
        ll_welcome = findViewById(R.id.ll_welcome_section);
        tv_welcome_message = findViewById(R.id.tv_welcome_message);
        tv_login_welcome_user = findViewById(R.id.login_tv_welcome_user);
        tv_signup_screen = findViewById(R.id.login_tv_signup);
        progressDialog = new ProgressDialog(this,R.style.greenprogress);
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);


        et_mail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==66)
                    et_password.requestFocus();
                return false;
            }
        });
//        et_password.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if(keyCode==66)
//
//                return false;
//            }
//        });

        tv_signup_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTheme(R.style.AppTheme_NoActionBarLogin);
                rl_login_section.startAnimation(animation_up);
                ll_signin_section.setVisibility(View.VISIBLE);
                ll_signin_section.startAnimation(animation_up);
                ll_login.setVisibility(View.GONE);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkOperations.isNetworkAvailable(LoginActivity.this)) {
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
                else {
                    NetworkOperations.networkError(LoginActivity.this);
                }
            }
        });


        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkOperations.isNetworkAvailable(LoginActivity.this)) {
                    if (!et_mail.getText().toString().equalsIgnoreCase("")) {
                        if(!mqttHelper.mqttAndroidClient.isConnected()){
                            MqttMessage message = new MqttMessage();
                            message.setPayload("hello".getBytes());
                            mqttHelper.publishMqttTopic("temp",message);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setTheme(R.style.AppTheme_NoActionBar);
                                dialogStatus = false;
                                progressDialog.show();
                                str_login_email = et_mail.getText().toString();
                                final MqttMessage mqttMessage = new MqttMessage();
                                otp = OtpGeneration.OTP(4);
                                try {
                                    mqttHelper.mqttAndroidClient.subscribe(mqtt_pubs_forgot_password + str_login_email + otp, 1, null, new IMqttActionListener() {
                                        @Override
                                        public void onSuccess(IMqttToken asyncActionToken) {
                                            Log.w("Mqtt", "Subscribed!");
                                            Log.i("credentials", str_login_email + " " + str_login_password);
                                            JSONObject jsonObject = new JSONObject();
                                            try {
                                                jsonObject.put("phizioemail", str_login_email);
                                                jsonObject.put("otp", otp);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            mqttMessage.setPayload(jsonObject.toString().getBytes());
                                            mqttHelper.publishMqttTopic(mqtt_pubs_forgot_password, mqttMessage);
                                        }

                                        @Override
                                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                            Log.w("Mqtt", "Subscribed fail!");
                                        }
                                    });
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }
                            }
                        },200);

                    } else {
                        Toast.makeText(LoginActivity.this, "Please enter the email address!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    NetworkOperations.networkError(LoginActivity.this);
                }
            }
        });

    }

    private void setWelcomeText(String str){
        tv_welcome_message.setText(str);
    }

    private void enableWelcomeView() {
        ll_welcome.setVisibility(View.VISIBLE);
        dottedProgressBar.setVisibility(View.VISIBLE);
    }

    private void disablePreviousView() {
        ll_signup_section.setVisibility(View.INVISIBLE);
        ll_signin_section.setVisibility(View.INVISIBLE);
        rl_login_section.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    @SuppressLint("ResourceType")
    public void showToast(String message){
         Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttHelper.mqttAndroidClient.unregisterResources();
        mqttHelper.mqttAndroidClient.close();

    }


    @Override
    public void onBackPressed() {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                    finish();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mqttHelper.mqttAndroidClient.unregisterResources();
        mqttHelper.mqttAndroidClient.close();
    }


    @Override
    public void onLoginResponse(boolean response, String message) {
        if(response){
            setWelcomeText("Welcome");
            tv_login_welcome_user.setText(message);
            dottedProgressBar.startProgress();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(LoginActivity.this, PatientsView.class);
                    startActivity(i);
                    finish();
                    dottedProgressBar.stopProgress();
                }
            },500);
        }
        else {
            setWelcomeText(message);
//                        showToast("Invalid Credentials");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    disableWelcomeView();
                    enablePreviousView();
                }
            },1000);
        }
    }


}
