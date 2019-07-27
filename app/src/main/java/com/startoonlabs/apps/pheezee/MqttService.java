package com.startoonlabs.apps.pheezee;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MqttService extends Service {

    File sessionDataTrackedFile, sessionDataUnTrackedFile;
    static Context context;
    ConnectivityManager connectivityManager;
    MqttAndroidClient client;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        context = this;
        sessionDataTrackedFile      = generateFile("sessionDataTracked.txt");
        sessionDataUnTrackedFile    = generateFile( "sessionDataUnTracked.txt");
        connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        //registerReceiver(networkReceiver,filter);
        return super.onStartCommand(intent, flags, startId);
    }

    public void StartMqtt(){
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, "tcp://13.127.78.38:1883", clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                static final String TAG = "Mqtt Message";

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    syncDataToServer();
                    client.setCallback(new MqttCallback() {
                        public void messageArrived(String topic, MqttMessage message) {}
                        public void connectionLost(Throwable cause) {}
                        public void deliveryComplete(IMqttDeliveryToken token) {}
                    });
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "MqttService onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void syncDataToServer(){
        try {
            JSONArray temp = readFromFile(sessionDataUnTrackedFile);
            if (temp != null) {
                client.publish("phoneStorage/app/sessionData", temp.toString().getBytes(), 0, false);
                JSONArray trackedData;
                trackedData = readFromFile(sessionDataTrackedFile);
                if (trackedData == null) {
                    trackedData = new JSONArray();
                }
                for (int i = 0; i < temp.length(); i++) {
                    trackedData.put(temp.getJSONObject(i));
                }
                FileOutputStream fo = new FileOutputStream(sessionDataTrackedFile);
                fo.write(trackedData.toString().getBytes());
                if(sessionDataUnTrackedFile.delete())
                    Log.d("Storage","your untracked data is moved to cloud");
                fo.close();
            }
        } catch (IOException | MqttException | JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray readFromFile(File file){
        try {
            FileInputStream fi = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fi);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }
            fi.close();
            if (!(stringBuilder.length()<=0))
                return new JSONArray(stringBuilder.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File generateFile(String fileName){
        File file = new File(context.getFilesDir(),  fileName);
        if(!file.exists()){
            try {
                if(file.createNewFile())
                    System.out.println("File created");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    public BroadcastReceiver networkReceiver = new BroadcastReceiver (){
        @Override
        public void onReceive(final Context context, Intent intent) {


            if(intent.getExtras()!=null) {
                NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
                if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
                    if(client!=null){
                        client.unregisterResources();
                        client.close();
                    }
                    StartMqtt();
                }else {
                }
            }else{
            }
            //super.onReceive(context, intent);
        }
    };
}
