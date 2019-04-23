package com.example.sai.pheezeeapp.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.sai.pheezeeapp.services.MqttHelper;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PatientOperations {
    static String mqtt_publish_phizio_deletepatient = "phizio/deletepatient";
    static JSONObject json_phizio = null;
    static JSONArray jsonData = new JSONArray();

    public static int getPatientNewSessionNumber(String phizioemail, String patientid, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        JSONObject json_phizio;
        JSONArray jsonData = new JSONArray();
        try {
            json_phizio = new JSONObject(preferences.getString("phiziodetails", ""));
            jsonData = new JSONArray(json_phizio.getString("phiziopatients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int sessionNo = 0;
        if (jsonData.length() > 0) {
            for (int i = 0; i < jsonData.length(); i++) {
                try {
                    if (jsonData.getJSONObject(i).get("patientid").equals(patientid)) {
                        if (jsonData.getJSONObject(i).has("numofsessions")) {
                            sessionNo = Integer.parseInt(jsonData.getJSONObject(i).get("numofsessions").toString());
                            sessionNo += 1;
                        } else {
                            sessionNo = 1;
                        }
                        Log.i("session",String.valueOf(Integer.parseInt(jsonData.getJSONObject(i).get("numofsessions").toString())));
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return sessionNo;
    }

    public static String getJoinDateOfPatiet(Context context, String patientId){
        Log.i("pateintID",patientId);
        String dateofjoin="";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            json_phizio = new JSONObject(preferences.getString("phiziodetails", ""));
            jsonData = new JSONArray(json_phizio.getString("phiziopatients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int k=0;k<jsonData.length();k++){
            try {
                if(jsonData.getJSONObject(k).getString("patientid").equals(patientId)){
                    if(jsonData.getJSONObject(k).has("dateofjoin")){
                        dateofjoin = jsonData.getJSONObject(k).getString("dateofjoin");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i("dateofjoin",dateofjoin);
        return dateofjoin;
    }

    public static void deletePatient(final Context context, final String patientId, final MqttHelper mqttHelper){
        final MqttMessage mqttMessage = new MqttMessage();
        final JSONObject object = new JSONObject();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        try {
            json_phizio = new JSONObject(preferences.getString("phiziodetails", ""));
            jsonData = new JSONArray(json_phizio.getString("phiziopatients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Patient");
        builder.setMessage("Are you sure you want to delete the patient?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(NetworkOperations.isNetworkAvailable(context)){
                    for(int k=0;k<jsonData.length();k++){
                        try {
                            if(jsonData.getJSONObject(k).getString("patientid").equals(patientId)){


                                object.put("phizioemail", json_phizio.get("phizioemail"));
                                object.put("patientid",jsonData.getJSONObject(k).get("patientid"));
                                jsonData.remove(k);
                                json_phizio.put("phiziopatients",jsonData);
                                editor.putString("phiziodetails",json_phizio.toString());
                                editor.commit();

                                Log.i("jsonData", json_phizio.getString("phiziopatients"));
                                mqttMessage.setPayload(object.toString().getBytes());

                                mqttHelper.publishMqttTopic(mqtt_publish_phizio_deletepatient,mqttMessage);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    NetworkOperations.networkError(context);
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    public static void putPatientProfilePicUrl(Context context, String patientId, String s) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        try {
            json_phizio = new JSONObject(preferences.getString("phiziodetails", ""));
            jsonData = new JSONArray(json_phizio.getString("phiziopatients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonData.length(); i++) {
            try {
                int numofsessions;
                if (jsonData.getJSONObject(i).get("patientid").equals(patientId)) {
                    jsonData.getJSONObject(i).put("patientprofilepicurl",s);
                    json_phizio.put("phiziopatients",jsonData);
                    editor.putString("phiziodetails", json_phizio.toString());
                    editor.apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static JSONObject findPatient(Context context,String patientId){
        JSONObject object= new JSONObject();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        try {
            json_phizio = new JSONObject(preferences.getString("phiziodetails", ""));
            jsonData = new JSONArray(json_phizio.getString("phiziopatients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonData.length(); i++) {
            try {
                int numofsessions;
                if (jsonData.getJSONObject(i).get("patientid").equals(patientId)) {
                    object = jsonData.getJSONObject(i);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

}
