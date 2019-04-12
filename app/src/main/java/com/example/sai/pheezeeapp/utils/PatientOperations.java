package com.example.sai.pheezeeapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.sai.pheezeeapp.activities.BodyPartSelection;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PatientOperations {

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
                        if (jsonData.getJSONObject(i).has("numofsession")) {
                            sessionNo = Integer.parseInt(jsonData.getJSONObject(i).get("numofsession").toString());
                            sessionNo += 1;
                        } else {
                            sessionNo = 1;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return sessionNo;
    }
}
