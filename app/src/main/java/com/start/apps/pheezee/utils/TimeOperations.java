package com.start.apps.pheezee.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeOperations {
    public String addTotalTime(JSONArray array){
        String temp_hours= "00:00";
        int total_seconds = 0;
        if(array.length()==0){
            return temp_hours;
        }
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = null;
                try {
                    object = array.getJSONObject(i);
                    String session_time = object.getString("sessiontime");
                    int min = Integer.parseInt(session_time.substring(0, 2));
                    int sec = Integer.parseInt(session_time.substring(5, 7));
                    total_seconds += (min * 60) + sec;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            int hours = total_seconds / 3600;
            int min = ((total_seconds) / 60) % 60;
            int sec = total_seconds % 60;
            String s_hours = "" + hours, s_min = "" + min, s_sec = "" + sec;
            if (hours < 10)
                s_hours = "0" + hours;
            if (min < 10)
                s_min = "0" + min;
            if (sec < 10)
                s_sec = "0" + sec;

            temp_hours = s_hours + "h:" + s_min + "m:" + s_sec + "s";

        return temp_hours;
    }

    public int addTotalRes(JSONArray array){
        int total_reps = 0;
        if(!(array.length()==0)) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = null;
                try {
                    object = array.getJSONObject(i);
                    String session_time = object.getString("numofreps");
                    int reps = Integer.parseInt(session_time);
                    total_reps += reps;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return total_reps;
    }

    public String addTotalHoldTime(JSONArray array){
        String temp_hours= "00:00";
        int total_seconds = 0;
        if(array.length()==0){
            return temp_hours;
        }
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = null;
            try {
                object = array.getJSONObject(i);
                String session_time = object.getString("holdtime");
                int min = Integer.parseInt(session_time.substring(0, 2));
                int sec = Integer.parseInt(session_time.substring(5, 7));
                total_seconds += (min * 60) + sec;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        int hours = total_seconds / 3600;
        int min = ((total_seconds) / 60) % 60;
        int sec = total_seconds % 60;
        String s_hours = "" + hours, s_min = "" + min, s_sec = "" + sec;
        if (hours < 10)
            s_hours = "0" + hours;
        if (min < 10)
            s_min = "0" + min;
        if (sec < 10)
            s_sec = "0" + sec;

        temp_hours = s_hours + "h:" + s_min + "m:" + s_sec + "s";

        return temp_hours;
    }


    public static String getUTCdatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

}
