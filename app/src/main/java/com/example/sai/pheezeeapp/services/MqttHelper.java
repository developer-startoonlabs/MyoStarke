package com.example.sai.pheezeeapp.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.sai.pheezeeapp.activities.LoginActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MqttHelper {
    public MqttAndroidClient mqttAndroidClient;
    private SharedPreferences preferences;
    private int sync_count = 0;
    private SharedPreferences.Editor editor;
    //topics to subscribe


    private String mqtt_subs_signup_response = "signup/phizio/response";   //phizio signup response from server
    private String mqtt_subs_login_response = "login/phizio/response";    //phizio login response from server
    private String mqtt_subs_phizio_addpatient_response = "phizio/addpatient/response";
    private String mqtt_subs_phizio_deletepatient_response = "phizio/deletepatient/response";
    private String mqtt_publish_generate_report_response = "patient/generate/report/response";
    private String mqtt_phizio_profile_update_response = "phizioprofile/update/response";
    private String mqtt_get_profile_pic_response = "phizio/getprofilepic/response";
    private String mqtt_publish_add_patient_session_response = "phizio/addpatientsession/response";
    private String mqtt_publish_add_patient_session_emg_data_response = "patient/entireEmgData/response";
    String mqtt_phizio_profilepic_change_response = "phizio/profilepic/upload/response";


    String mqtt_get_profile_pic = "phizio/getprofilepicture";


    //topics which are published
    private String mqtt_publish_signup_doctor = "signup/phizio";
    String mqtt_publish_getpatientReport = "patient/generate/report";

    private final String serverUri = "tcp://52.66.113.37:1883";

    private final String clientId = MqttClient.generateClientId();

    private final String username = "xxxxxxx";
    private final String password = "yyyyyyyyyy";
    private JSONObject object;

    private String whichOne="",patientid, phizioemail;

    private Context ctx;
    public MqttHelper(Context context){
        ctx = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                Log.w("Mqtt", mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        connect();
    }

    public MqttHelper(Context context,String whichOne){
        ctx = context;
        this.whichOne = whichOne;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage)  {
                Log.w("Mqtt", mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        connect();
    }

    public MqttHelper(Context context,String whichOne, String patientid, String phizioemail){
        ctx = context;
        this.whichOne = whichOne;
        this.patientid = patientid;
        this.phizioemail = phizioemail;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage)  {
                Log.w("Mqtt", mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
//        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    String phizio_email = null;
                    if(!preferences.getString("phiziodetails","").equals("")) {
                        try {
                            object = new JSONObject(preferences.getString("phiziodetails", ""));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if(whichOne.equals("phizioprofile")){
                        Log.i("hello","inside profilepic");

                        MqttMessage message = new MqttMessage();
                        JSONObject obj = new JSONObject();
                        try {
                            if(obj!=null && object!=null) {
                                obj.put("phizioemail", object.getString("phizioemail"));
                                message.setPayload(obj.toString().getBytes());
                                publishMqttTopic(mqtt_get_profile_pic, message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(whichOne.equals("patientsview")){
                        Log.i("hello","inside profilepic");
                        try {
                            if(!preferences.getString("phiziodetails","").equals(""))
                                object = new JSONObject(preferences.getString("phiziodetails",""));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MqttMessage message = new MqttMessage();
                        JSONObject obj = new JSONObject();
                        try {
                            if(obj!=null && object!=null) {
                                obj.put("phizioemail", object.getString("phizioemail"));
                                message.setPayload(obj.toString().getBytes());
                                publishMqttTopic(mqtt_get_profile_pic, message);
                            }
//                            if (isNetworkAvailable())
//                                syncData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    else if(whichOne.endsWith("report")){
                        MqttMessage message = new MqttMessage();
                        JSONObject object = new JSONObject();
                        try {
                            object.put("patientid",patientid);
                            object.put("phizioemail",phizioemail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        message.setPayload(object.toString().getBytes());
                        publishMqttTopic(mqtt_publish_getpatientReport,message);
                    }

                    if(object!=null){
                        if(object.has("phizioemail")){
                            try {
                                phizio_email = object.getString("phizioemail");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    subscribeToTopic(phizio_email);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect to: " + serverUri + exception.toString());
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }


    private void subscribeToTopic(final String phizio_email) {
        if(phizio_email!=null) {
            try {

                mqttAndroidClient.subscribe(mqtt_subs_phizio_addpatient_response+phizio_email, 1, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.w("Mqtt", "Subscribed!"+mqtt_subs_phizio_addpatient_response+phizio_email);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.w("Mqtt", "Subscribed fail!");
                    }
                });
                mqttAndroidClient.subscribe(mqtt_subs_phizio_deletepatient_response+phizio_email, 1, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.w("Mqtt", "Subscribed!"+mqtt_subs_phizio_deletepatient_response+phizio_email);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.w("Mqtt", "Subscribed fail!");
                    }
                });

                mqttAndroidClient.subscribe(mqtt_publish_generate_report_response+phizio_email, 1, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.w("Mqtt", "Subscribed!"+mqtt_publish_generate_report_response+phizio_email);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.w("Mqtt", "Subscribed fail!");
                    }
                });

                mqttAndroidClient.subscribe(mqtt_phizio_profile_update_response+phizio_email, 1, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.w("Mqtt", "Subscribed!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.w("Mqtt", "Subscribed fail!");
                    }
                });
                mqttAndroidClient.subscribe(mqtt_get_profile_pic_response+phizio_email, 1, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.w("Mqtt", "Subscribed!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.w("Mqtt", "Subscribed fail!");
                    }
                });
                mqttAndroidClient.subscribe(mqtt_publish_add_patient_session_response+phizio_email, 1, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.w("Mqtt", "Subscribed!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.w("Mqtt", "Subscribed fail!");
                    }
                });
                mqttAndroidClient.subscribe(mqtt_publish_add_patient_session_emg_data_response+phizio_email, 1, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.w("Mqtt", "Subscribed!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.w("Mqtt", "Subscribed fail!");
                    }
                });

                mqttAndroidClient.subscribe(mqtt_phizio_profilepic_change_response+phizio_email, 1, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.w("Mqtt", "Subscribed!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.w("Mqtt", "Subscribed fail!");
                    }
                });


            } catch (MqttException ex) {
                System.err.println("Exceptionst subscribing");
                ex.printStackTrace();
            }
        }

    }

    public void publishMqttTopic(String topic, MqttMessage message){
        try {
            if(mqttAndroidClient.isConnected())
                mqttAndroidClient.publish(topic,message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void syncData(){
        String str_sync_session = preferences.getString("sync_session","");
        Log.i("sync",str_sync_session);
        JSONArray array = null;
        if(!str_sync_session.equals("")) {
            try {
                array = new JSONArray(str_sync_session);
                Log.i("array", array.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    MqttMessage message = new MqttMessage();
                    message.setPayload(object.get("message").toString().getBytes());
                    publishMqttTopic(object.getString("topic"), message);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final JSONArray finalArray = array;
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                if(topic.equals(mqtt_publish_add_patient_session_response)){
                    if(message.toString().equals("inserted")){
                        sync_count++;
                        if(sync_count== finalArray.length()) {
                            Log.i("message", message.toString());
                            editor = preferences.edit();
                            editor.putString("sync_session", "");
                            editor.apply();
                        }
                    }
                }
                if(topic.equals(mqtt_publish_add_patient_session_emg_data_response)){
                    if(message.toString().equals("inserted")){
                        sync_count++;
                        if(sync_count== finalArray.length()) {
                            Log.i("message", message.toString());
                            editor = preferences.edit();
                            editor.putString("sync_session", "");
                            editor.apply();
                        }
                    }
                }
                if(topic.equals(mqtt_subs_phizio_addpatient_response)){
                    sync_count++;
                    if(sync_count== finalArray.length()) {
                        Log.i("message", message.toString());
                        editor = preferences.edit();
                        editor.putString("sync_session", "");
                        editor.apply();
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }



}