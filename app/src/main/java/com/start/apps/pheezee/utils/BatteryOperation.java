package com.start.apps.pheezee.utils;

import android.content.Context;

import start.apps.pheezee.R;

public class BatteryOperation {
    /**
     * battery formulae 0-25,50,75,100
     * @param percent
     * @return
     */
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

    /**
     * Sends dialog message based on the battery percentage
     * @param percent
     * @param context
     * @return
     */
    public static String getDialogMessageForLowBattery(int percent, Context context){
        String message;

        if(percent>10 && percent<=15)
            message = context.getResources().getString(R.string.battery_percent_lower_than_15);
        else if(percent>5 && percent<=10)
            message = context.getResources().getString(R.string.battery_percent_lower_than_10);
        else if(percent<=5)
            message = context.getResources().getString(R.string.battery_percent_lower_than_5);
        else
            message = "c";
        return message;
    }
}
