package com.example.sai.pheezeeapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.sai.pheezeeapp.R;

public class ValueBasedColorOperations {

    /**
     *
     * @param bodypart
     * @param max
     * @param min
     * @param context
     * @return
     */
    public static int getCOlorBasedOnTheBodyPart(String bodypart, int max, int min, Context context){
        int bodyPart;
        int maxStaticRange = getBodyPartMaximumRange(bodypart);
        int range = max-min;
        if((range<(maxStaticRange/3))){
            Log.i("inside", "less than 3");
            bodyPart = ContextCompat.getColor(context, R.color.red);
        }
        else if((range<((2*maxStaticRange)/3))){
            Log.i("inside", "less than 2/3");
            bodyPart = ContextCompat.getColor(context, R.color.average_blue);
        }
        else {
            Log.i("inside", "else");
            bodyPart = ContextCompat.getColor(context, R.color.good_green);
        }

        return bodyPart;
    }


    /**
     *
     * @param bodypart
     * @return
     */
    public static int getBodyPartMaximumRange(String bodypart){
        int range = 0;
       switch (bodypart.toLowerCase()){
           case "elbow":{
               range = 160;
               break;
           }

           case "knee":{
               range = 135;
               break;
           }

           case "ankle":{
               range = 100;
               break;
           }
           case "hip":{
               range = 50;
               break;
           }

           case "wrist":{
               range = 100;
               break;
           }

           case "shoulder":{
               range = 250;
               break;
           }

           case "others":{
               range = 0;
               break;
           }
       }
       return range;
    }
}
