package com.example.sai.pheezeeapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.services.MqttHelper;
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


public class LoginActivity extends AppCompatActivity {
    MqttHelper mqttHelper;
    int backpressCount = 0;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;
    DottedProgressBar dottedProgressBar;

    //Srings for edittexts
    String str_login_email, str_login_password;

    //Mqtt Topics
    String mqtt_subs_login_response = "login/phizio/response";    //phizio login response from server
    String mqtt_pubs_login_phizio = "login/phizio";    //phizio login response from server

    TextView tv_signup,tv_login,tv_welcome_message,tv_login_welcome_user,tv_signup_screen;
    LinearLayout ll_login,ll_signin_section,btn_login,tv_forgot_password,ll_signup_section,ll_welcome;
    RelativeLayout rl_login_section;
    EditText et_mail,et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_continue);

        initializeView();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        mqttHelper = new MqttHelper(this);
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
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void disableWelcomeView() {
        ll_welcome.setVisibility(View.INVISIBLE);
        dottedProgressBar.setVisibility(View.INVISIBLE);
    }

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
                rl_login_section.startAnimation(animation_up);
                ll_signin_section.setVisibility(View.VISIBLE);
                ll_signin_section.startAnimation(animation_up);
                ll_login.setVisibility(View.GONE);
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_login_email = et_mail.getText().toString();
                str_login_password = et_password.getText().toString();
                final MqttMessage mqttMessage = new MqttMessage();

                if(str_login_email.equals("")||str_login_password.equals("")){
                        showToast("Invalid Credentials");
                }
                else {
                    try {
                        mqttHelper.mqttAndroidClient.subscribe(mqtt_subs_login_response+str_login_email+str_login_password, 1, null, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) { Log.w("Mqtt","Subscribed!");
                                Log.i("credentials",str_login_email+" "+str_login_password);
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("phiziopassword",str_login_password);
                                    jsonObject.put("phizioemail",str_login_email);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mqttMessage.setPayload(jsonObject.toString().getBytes());
                                mqttHelper.publishMqttTopic(mqtt_pubs_login_phizio,mqttMessage);}

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) { Log.w("Mqtt", "Subscribed fail!"); }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }


                    disablePreviousView();
                    enableWelcomeView();
                    setWelcomeText("Loging in..");
                    dottedProgressBar.startProgress();
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
}
