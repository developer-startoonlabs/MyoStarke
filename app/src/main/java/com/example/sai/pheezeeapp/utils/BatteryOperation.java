package com.example.sai.pheezeeapp.utils;

import android.content.Context;

import com.example.sai.pheezeeapp.R;

public class BatteryOperation {
    public static int convertBatteryToCell(int percent){
        if(percent<=25)
            percent = 25;
        else if(percent <= 50)
            percent = 50;
        else if(percent <= 75)
            percent = 75;
        else
            percent =100;

        return percent;
    }

    public static String getDialogMessageForLowBattery(int percent, Context context){
        String message;

        if(percent>10 && percent<=15)
            message = context.getResources().getString(R.string.battery_percent_lower_than_15);
        else if(percent>5 && percent<=10)
            message = context.getResources().getString(R.string.battery_percent_lower_than_10);
        else if(percent<5)
            message = context.getResources().getString(R.string.battery_percent_lower_than_5);
        else
            message = "c";
        return message;
    }
}
