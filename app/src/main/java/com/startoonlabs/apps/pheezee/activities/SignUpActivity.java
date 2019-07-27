package com.startoonlabs.apps.pheezee.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.services.MqttHelper;
import com.startoonlabs.apps.pheezee.utils.OtpGeneration;
import com.startoonlabs.apps.pheezee.utils.RegexOperations;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SignUpActivity extends AppCompatActivity {




    EditText et_signup_name, et_signup_password, et_signup_email, et_signup_phone;

    //String to save edittexts
    String str_signup_name, str_signup_password,str_signup_email,str_signup_phone;

    Button btn_signup_create;
    TextView tv_signup_cancel;

    MqttHelper mqttHelper;

    MqttConnectOptions mqttConnectOptions;
    String clientId;
    MqttAndroidClient client;
    boolean dialogStatus = false;
    AlertDialog mdialog = null;
    String otp;


    //topics to subscribe

    String mqtt_subs_signup_response = "signup/phizio/response";



    //topics which are published
    String mqtt_publish_signup_doctor = "signup/phizio";

    String mqtt_publish_confirm_email = "confirm/email";
    String mqtt_publish_confirm_email_response = "confirm/email/response";





    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //defining all the view elements
        dialogStatus = false;
        //EDIT TEXTS
        et_signup_name = (EditText)findViewById(R.id.et_signup_name);
        et_signup_password = (EditText)findViewById(R.id.et_signup_password);
        et_signup_email = (EditText)findViewById(R.id.et_signup_email);
        et_signup_phone = (EditText)findViewById(R.id.et_signup_phone);

        progressDialog = new ProgressDialog(this,R.style.greenprogress);
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);


        //Buttons

        btn_signup_create  = (Button)findViewById(R.id.btn_signup_create);

        //TextViews

        tv_signup_cancel = (TextView)findViewById(R.id.tv_cancel_signup);


        mqttHelper = new MqttHelper(this);





        btn_signup_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                str_signup_name = et_signup_name.getText().toString();
                str_signup_password = et_signup_password.getText().toString();
                str_signup_email = et_signup_email.getText().toString();
                str_signup_phone = et_signup_phone.getText().toString();


                if(str_signup_name.equals("")||str_signup_password.equals("")||str_signup_email.equals("")||str_signup_phone.equals("")){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
                }
                else if(!RegexOperations.isValidEmail(str_signup_email)|| !RegexOperations.isValidMobileNumber(str_signup_phone.replaceAll("\\s",""))){
                    progressDialog.dismiss();
                    if(!RegexOperations.isValidEmail(str_signup_email)&& !RegexOperations.isValidMobileNumber(str_signup_phone))
                        Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    else if(!RegexOperations.isValidEmail(str_signup_email))
                        Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
//                        mqttHelper.mqttAndroidClient.subscribe(mqtt_subs_signup_response+str_signup_email+str_signup_password, 1, null, new IMqttActionListener() {
//                            @Override
//                            public void onSuccess(IMqttToken asyncActionToken) {
//
//                                Log.w("Mqtt", "Subscribed!");
//                                JSONObject jsonObject = new JSONObject();
//                                JSONArray jsonArray = new JSONArray();
//                                MqttMessage mqttMessage = new MqttMessage();
//                                try {
//                                    jsonObject.put("phizioname",str_signup_name);
//                                    jsonObject.put("phiziopassword",str_signup_password);
//                                    jsonObject.put("phizioemail",str_signup_email);
//                                    jsonObject.put("phiziophone",str_signup_phone);
//                                    jsonObject.put("phizioprofilepicurl","url defauld now");
//                                    jsonObject.put("phiziopatients",jsonArray);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                mqttMessage.setPayload(jsonObject.toString().getBytes());
//
//                                mqttHelper.publishMqttTopic(mqtt_publish_signup_doctor, mqttMessage);
//                            }
//
//                            @Override
//                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                                Log.w("Mqtt", "Subscribed fail!");
//                            }
//                        });
                        mqttHelper.mqttAndroidClient.subscribe(mqtt_publish_confirm_email_response+str_signup_email+str_signup_password, 1, null, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {

                                Log.w("Mqtt", "Subscribed!");
                                JSONObject jsonObject = new JSONObject();
                                JSONArray jsonArray = new JSONArray();
                                otp = OtpGeneration.OTP(4);
                                MqttMessage mqttMessage = new MqttMessage();
                                try {
                                    jsonObject.put("phizioname",str_signup_name);
                                    jsonObject.put("phiziopassword",str_signup_password);
                                    jsonObject.put("phizioemail",str_signup_email);
                                    jsonObject.put("phiziophone",str_signup_phone);
                                    jsonObject.put("otp",otp);
                                    jsonObject.put("phizioprofilepicurl","url defauld now");
                                    jsonObject.put("phiziopatients",jsonArray);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mqttMessage.setPayload(jsonObject.toString().getBytes());

                                mqttHelper.publishMqttTopic(mqtt_publish_confirm_email, mqttMessage);
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
            }
        });



        tv_signup_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_redirectlogin = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent_redirectlogin);
            }
        });


        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.equals(mqtt_subs_signup_response+str_signup_email+str_signup_password)){
                    progressDialog.dismiss();
                    Log.i("message",message.toString());
                    if(message.toString().equals("inserted")){
                        Log.i("MQTT MESSAGE", ""+message);
                        editor = sharedPref.edit();
                        editor.putBoolean("isLoggedIn",true);
                        editor.commit();
                        JSONObject jsonObject = new JSONObject();
                        JSONArray jsonArray = new JSONArray();
                        try {
                            jsonObject.put("phizioname",str_signup_name);
                            jsonObject.put("phizioemail",str_signup_email);
                            jsonObject.put("phiziophone",str_signup_phone);
                            jsonObject.put("phizioprofilepicurl","empty");
                            jsonObject.put("phiziopatients",jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.putString("phiziodetails",jsonObject.toString());
                        editor.commit();

                        Intent i = new Intent(SignUpActivity.this, PatientsView.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        Log.i("MQTT MESSAGE", ""+"cacaaca");
                    }
                }

                else if(topic.equals(mqtt_publish_confirm_email_response+str_signup_email+str_signup_password)){
                    progressDialog.dismiss();
                    Log.i("message",message.toString());
                    if(message.toString().equalsIgnoreCase("sent")){
                        if (!dialogStatus) {
                            dialogStatus = true;
                            final AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            builder.setTitle("Please enter otp");
                            builder.setMessage("Otp has been sent to the specified email, Please enter the otp for email verification.");
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
                                            btn_signup_create.performClick();
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
                                        progressDialog.show();
                                        try {
                                            mqttHelper.mqttAndroidClient.subscribe(mqtt_subs_signup_response+str_signup_email+str_signup_password, 1, null, new IMqttActionListener() {
                                            @Override
                                            public void onSuccess(IMqttToken asyncActionToken) {

                                                Log.w("Mqtt", "Subscribed!");
                                                JSONObject jsonObject = new JSONObject();
                                                JSONArray jsonArray = new JSONArray();
                                                MqttMessage mqttMessage = new MqttMessage();
                                                try {
                                                    jsonObject.put("phizioname",str_signup_name);
                                                    jsonObject.put("phiziopassword",str_signup_password);
                                                    jsonObject.put("phizioemail",str_signup_email);
                                                    jsonObject.put("phiziophone",str_signup_phone);
                                                    jsonObject.put("phizioprofilepicurl","url defauld now");
                                                    jsonObject.put("phiziopatients",jsonArray);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                mqttMessage.setPayload(jsonObject.toString().getBytes());

                                                mqttHelper.publishMqttTopic(mqtt_publish_signup_doctor, mqttMessage);
                                            }

                                            @Override
                                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                                Log.w("Mqtt", "Subscribed fail!");
                                            }
                                        });
                                        } catch (MqttException e) {
                                            e.printStackTrace();
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
                    else if (message.toString().equalsIgnoreCase("nsent")){
                        Toast.makeText(getApplicationContext(), "Invalid Email!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttHelper.mqttAndroidClient.unregisterResources();
        mqttHelper.mqttAndroidClient.close();
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
}
