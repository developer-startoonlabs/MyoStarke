package com.startoonlabs.apps.pheezee.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.startoonlabs.apps.pheezee.services.MqttHelper;

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

    public static JSONObject checkReferenceDone(String orientation, Context context, String patientID, String bodypartSelected){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        JSONObject json_phizio = null;
        JSONArray json_patients = null;
        JSONArray json_patient_reference = new JSONArray();
        JSONObject result = null;
        try {
            json_phizio = new JSONObject(preferences.getString("phiziodetails",""));
            json_patients = new JSONArray(json_phizio.getString("phiziopatients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i=0;i<json_patients.length();i++){
            try {
                JSONObject object = json_patients.getJSONObject(i);
                if(object.getString("patientid").equals(patientID)){
                    if(object.has("calibrationSession"))
                        json_patient_reference = new JSONArray(object.getString("calibrationSession"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < json_patient_reference.length(); i++) {
            try {
                JSONObject object = json_patient_reference.getJSONObject(i);

                String exerciseType = object.getString("bodypart");
                String obj_orientation = object.getString("orientation");
                    if (exerciseType.equals(bodypartSelected) && obj_orientation.equals(orientation)) {
                        return object;
                    }
                    else if (exerciseType.equals(bodypartSelected) && obj_orientation.equals(orientation))
                        return object;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  result;
    }

    public static JSONObject checkReferenceWithoutOrientationDone( Context context, String patientID, String bodypartSelected){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        JSONObject json_phizio = null;
        JSONArray json_patients = null;
        JSONArray json_patient_reference = new JSONArray();
        JSONObject result = null;
        try {
            json_phizio = new JSONObject(preferences.getString("phiziodetails",""));
            json_patients = new JSONArray(json_phizio.getString("phiziopatients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i=0;i<json_patients.length();i++){
            try {
                JSONObject object = json_patients.getJSONObject(i);
                if(object.getString("patientid").equals(patientID)){
                    if(object.has("calibrationSession"))
                        json_patient_reference = new JSONArray(object.getString("calibrationSession"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < json_patient_reference.length(); i++) {
            try {
                JSONObject object = json_patient_reference.getJSONObject(i);

                String exerciseType = object.getString("bodypart");
                String obj_orientation = object.getString("orientation");
                if (exerciseType.equalsIgnoreCase(bodypartSelected) )
                    return object;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  result;
    }


    public static void saveReferenceSessionLocally(JSONObject param, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        JSONObject json_phizio = null;
        JSONArray json_patients = null;
        JSONArray json_patient_reference = new JSONArray();
        try {
            json_phizio = new JSONObject(preferences.getString("phiziodetails",""));
            json_patients = new JSONArray(json_phizio.getString("phiziopatients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject selected = new JSONObject();
        try {
            selected.put("bodypart",param.getString("bodypartselected"));
            selected.put("orientation",param.getString("orientationselected"));
            selected.put("maxangle",param.getString("maxangle"));
            selected.put("maxemg",param.getString("maxemg"));
            selected.put("minangle",param.getString("minangle"));
            selected.put("minemg",param.getString("minemg"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("savelocally",selected.toString());
        boolean flag_has = false;
        for (int i=0;i<json_patients.length();i++){

            try {
                JSONObject object = json_patients.getJSONObject(i);
                if(object.getString("patientid").equals(param.getString("patientid"))){
                    if(object.has("calibrationSession")){
                        json_patient_reference = new JSONArray(object.getString("calibrationSession"));
                        for (int j=0;j<json_patient_reference.length();j++){
                            JSONObject object1 = json_patient_reference.getJSONObject(j);
                            if(object1.getString("bodypart").equalsIgnoreCase(param.getString("bodypartselected")) && object1.getString("orientation").equalsIgnoreCase(param.getString("orientationselected"))){
                                flag_has = true;
                                json_patient_reference.getJSONObject(j).put("maxangle",param.getString("maxangle"));
                                json_patient_reference.getJSONObject(j).put("maxemg",param.getString("maxemg"));
                                json_patient_reference.getJSONObject(j).put("minangle",param.getString("minangle"));
                                json_patient_reference.getJSONObject(j).put("minemg",param.getString("minemg"));
                                Log.i("json mmt",json_patient_reference.getJSONObject(j).toString());
                                break;
                            }
                        }
                    }
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(!flag_has){
            json_patient_reference.put(selected);
        }
        for (int i=0;i<json_patients.length();i++){
            try {
                JSONObject object = json_patients.getJSONObject(i);
                if(object.getString("patientid").equals(param.getString("patientid"))){
                    json_patients.getJSONObject(i).put("calibrationSession",json_patient_reference.toString());
                    json_phizio.put("phiziopatients",json_patients);
                    editor.putString("phiziodetails",json_phizio.toString());
                    editor.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
