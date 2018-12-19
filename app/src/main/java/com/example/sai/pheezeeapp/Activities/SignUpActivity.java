package com.example.sai.pheezeeapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.services.MqttHelper;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
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


    //topics to subscribe

    String mqtt_subs_signup_response = "signup/phizio/response";



    //topics which are published
    String mqtt_publish_signup_doctor = "signup/phizio";





    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //defining all the view elements

        //EDIT TEXTS
        et_signup_name = (EditText)findViewById(R.id.et_signup_name);
        et_signup_password = (EditText)findViewById(R.id.et_signup_password);
        et_signup_email = (EditText)findViewById(R.id.et_signup_email);
        et_signup_phone = (EditText)findViewById(R.id.et_signup_phone);


        //Buttons

        btn_signup_create  = (Button)findViewById(R.id.btn_signup_create);

        //TextViews

        tv_signup_cancel = (TextView)findViewById(R.id.tv_cancel_signup);


        mqttHelper = new MqttHelper(this);





        btn_signup_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_signup_name = et_signup_name.getText().toString();
                str_signup_password = et_signup_password.getText().toString();
                str_signup_email = et_signup_email.getText().toString();
                str_signup_phone = et_signup_phone.getText().toString();
                MqttMessage mqttMessage = new MqttMessage();
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                if(str_signup_name.equals("")||str_signup_password.equals("")||str_signup_email.equals("")||str_signup_phone.equals("")){
                    Toast.makeText(SignUpActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                }
                else {



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
                if(topic.equals(mqtt_subs_signup_response)){
                    if(message.toString().equals("inserted")){
                        Log.i("MQTT MESSAGE", ""+message);
                        editor = sharedPref.edit();
                        editor.putBoolean("isLoggedIn",true);
                        JSONObject jsonObject = new JSONObject();
                        JSONArray jsonArray = new JSONArray();
                        try {
                            jsonObject.put("phizioname",str_signup_name);
                            jsonObject.put("phizioemail",str_signup_email);
                            jsonObject.put("phiziophone",str_signup_phone);
                            jsonObject.put("phizioprofilepicurl","url defauld now");
                            jsonObject.put("phiziopatients",jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.putString("phiziodetails",jsonObject.toString());
                        editor.commit();

                        Intent i = new Intent(SignUpActivity.this, PatientsView.class);
                        startActivity(i);
                    }
                    else{
                        Log.i("MQTT MESSAGE", ""+"cacaaca");
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });





    }
    private MqttConnectOptions getMqttConnectionOption() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        //mqttConnectOptions.setWill(, "I am going offline".getBytes(), 1, true);
        //mqttConnectOptions.setUserName("username");
        //mqttConnectOptions.setPassword("password".toCharArray());
        return mqttConnectOptions;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttHelper.mqttAndroidClient.unregisterResources();
        mqttHelper.mqttAndroidClient.close();
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
        /*View view = toast.getView();
        view.setBackgroundResource(Color.BLACK);
        toast.setView(view);
        toast.show();*/
    }
}
