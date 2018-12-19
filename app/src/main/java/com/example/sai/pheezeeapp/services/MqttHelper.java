package com.example.sai.pheezeeapp.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.sai.pheezeeapp.Activities.PhizioProfile;
import com.example.sai.pheezeeapp.dashboard.DashboardActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class MqttHelper {
    public MqttAndroidClient mqttAndroidClient;
    //topics to subscribe

    String mqtt_subs_signup_response = "signup/phizio/response";   //phizio signup response from server
    String mqtt_subs_login_response = "login/phizio/response";    //phizio login response from server
    String mqtt_subs_phizio_addpatient_response = "phizio/addpatient/response";
    String mqtt_subs_phizio_deletepatient_response = "phizio/deletepatient/response";
    String mqtt_publish_generate_report_response = "patient/generate/report/response";
    String mqtt_phizio_profile_update_response = "phizioprofile/update/response";
    String mqtt_get_profile_pic_response = "phizio/getprofilepic/response";


    String mqtt_get_profile_pic = "phizio/getprofilepicture";


    //topics which are published
    String mqtt_publish_signup_doctor = "signup/phizio";

    final String serverUri = "tcp://52.66.113.37:1883";

    final String clientId = "ExampleAndroidClient";
    final String subscriptionTopic = "report/sai";

    final String username = "xxxxxxx";
    final String password = "yyyyyyyyyy";
    JSONObject object;

    String whichOne="";

    Context ctx;
    SharedPreferences preferences;
    public MqttHelper(Context context){
        ctx = context;
        DashboardActivity mainActivity = new DashboardActivity();
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
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
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
        DashboardActivity mainActivity = new DashboardActivity();
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
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
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
        mqttConnectOptions.setAutomaticReconnect(true);
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
                    PhizioProfile profile = new PhizioProfile();

                    if(whichOne.equals("phizioprofile")){
                        Log.i("hello","inside profilepic");
                        try {
                            object = new JSONObject(preferences.getString("phiziodetails",""));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MqttMessage message = new MqttMessage();
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("phizioemail",object.getString("phizioemail"));
                            message.setPayload(obj.toString().getBytes());
                            publishMqttTopic(mqtt_get_profile_pic,message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(whichOne.equals("patientsview")){
                        Log.i("hello","inside profilepic");
                        try {
                            object = new JSONObject(preferences.getString("phiziodetails",""));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MqttMessage message = new MqttMessage();
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("phizioemail",object.getString("phizioemail"));
                            message.setPayload(obj.toString().getBytes());
                            publishMqttTopic(mqtt_get_profile_pic,message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    subscribeToTopic();
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


    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(mqtt_subs_signup_response, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt","Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Subscribed fail!");
                }
            });

            mqttAndroidClient.subscribe(mqtt_subs_login_response, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) { Log.w("Mqtt","Subscribed!");  }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) { Log.w("Mqtt", "Subscribed fail!"); }
            });

            mqttAndroidClient.subscribe(mqtt_subs_phizio_addpatient_response, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) { Log.w("Mqtt","Subscribed!");  }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) { Log.w("Mqtt", "Subscribed fail!"); }
            });
            mqttAndroidClient.subscribe(mqtt_subs_phizio_deletepatient_response, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) { Log.w("Mqtt","Subscribed!");  }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) { Log.w("Mqtt", "Subscribed fail!"); }
            });

            mqttAndroidClient.subscribe(mqtt_publish_generate_report_response, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) { Log.w("Mqtt","Subscribed!");  }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) { Log.w("Mqtt", "Subscribed fail!"); }
            });

            mqttAndroidClient.subscribe(mqtt_phizio_profile_update_response, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) { Log.w("Mqtt","Subscribed!");  }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) { Log.w("Mqtt", "Subscribed fail!"); }
            });
            mqttAndroidClient.subscribe(mqtt_get_profile_pic_response, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) { Log.w("Mqtt","Subscribed!");  }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) { Log.w("Mqtt", "Subscribed fail!"); }
            });

        } catch (MqttException ex) {
            System.err.println("Exceptionst subscribing");
            ex.printStackTrace();
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
}