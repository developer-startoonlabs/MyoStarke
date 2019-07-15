package com.startoonlabs.apps.pheezee.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.startoonlabs.apps.pheezee.R;

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
               range = 305;
               break;
           }

           case "knee":{
               range = 150;
               break;
           }

           case "ankle":{
               range = 80;
               break;
           }
           case "hip":{
               range = 240;
               break;
           }

           case "wrist":{
               range = 160;
               break;
           }

           case "shoulder":{
               range = 360;
               break;
           }

           case "others":{
               range = 0;
               break;
           }
       }
       return range;
    }
    public static int getBodyPartMaxValue(String bodypart){
        int max = 0;
        switch (bodypart.toLowerCase()){
            case "elbow":{
                max = 150;
                break;
            }

            case "knee":{
                max = 135;
                break;
            }

            case "ankle":{
                max = 50;
                break;
            }
            case "hip":{
                max = 125;
                break;
            }

            case "wrist":{
                max = 90;
                break;
            }

            case "shoulder":{
                max = 180;
                break;
            }

            case "others":{
                max = 0;
                break;
            }
        }
        return max;
    }
    public static int getBodyPartMinValue(String bodypart){
        int range = 0;
        switch (bodypart.toLowerCase()){
            case "elbow":{
                range = -145;
                break;
            }

            case "knee":{
                range = 0;
                break;
            }

            case "ankle":{
                range = -30;
                break;
            }
            case "hip":{
                range = -115;
                break;
            }

            case "wrist":{
                range = -70;
                break;
            }

            case "shoulder":{
                range = -180;
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
