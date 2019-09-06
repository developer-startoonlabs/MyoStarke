package com.startoonlabs.apps.pheezee.utils;

import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;

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
    public static int getCOlorBasedOnTheBodyPart(int bodypart, int exercise, int max, int min, Context context){
        int bodyPart;
        int maxStaticRange = getBodyPartMaxValue(bodypart,exercise);
        int range = max-min;
        if((range<(maxStaticRange/3))){
            bodyPart = ContextCompat.getColor(context, R.color.red);
        }
        else if((range<((2*maxStaticRange)/3))){
            bodyPart = ContextCompat.getColor(context, R.color.average_blue);
        }
        else {
            bodyPart = ContextCompat.getColor(context, R.color.summary_green);
        }

        return bodyPart;
    }


    public static int getEmgColor(int emg, int trueemg, Context context){
        int color;
        if(trueemg<(emg/3)){
            color = ContextCompat.getColor(context, R.color.red);
        }
        else if(trueemg<((2*emg)/3)){
            color = ContextCompat.getColor(context, R.color.average_blue);
        }
        else {
            color = ContextCompat.getColor(context, R.color.summary_green);
        }
            return color;
    }


    public static int getCOlorBasedOnTheBodyPartExercise(int bodypart, int exercise, int max, int min, Context context){
        int bodyPart;
        int maxStaticRange = getBodyPartMaxValue(bodypart,exercise);
        int range = max-min;
        if((range<(maxStaticRange/3))){
            Log.i("inside", "less than 3");
            bodyPart = 2;
        }
        else if((range<((2*maxStaticRange)/3))){
            Log.i("inside", "less than 2/3");
            bodyPart = 1;
        }
        else {
            Log.i("inside", "else");
            bodyPart = 0;
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
    private final static int[][] min_values = {
            {0,0,0,0,0,0,0,0},    //elbow
            {0,0,0,0,0,0},          //knee
            {0,0,0,0,0,0},             //ankle
            {0,0,0,0,0,0},  //Hip
            {0,0,0,0,0,0},             //wrist
            {0,0,0,0,0,0,0,0,0,0}, //shoulder
            {0,0}
            //elbow
    };
    public static int getBodyPartMinValue(int bodypart, int exercisename){
        return min_values[bodypart][exercisename];
    }

    private final static int[][] max_values = {
            {0,160,145,90,90,90,90,0},    //elbow
            {0,150,150,45,45,0},          //knee
            {0,30,50,35,25,0},             //ankle
            {0,125,115,45,45,0},  //Hip
            {0,90,75,25,65,0},             //wrist
            {0,180,60,184,140,30,30,30,30,0}, //shoulder
            {0,0}
    };

    public static int getBodyPartMaxValue(int bodypart, int exercisename){
        return max_values[bodypart][exercisename];
    }


    public static byte[] getParticularDataToPheeze(String string){

        switch (string.toLowerCase()){

            case "elbow":{
                return ByteToArrayOperations.hexStringToByteArray("AA03");
            }

            case "knee":{
                return ByteToArrayOperations.hexStringToByteArray("AA04");
            }

            case "ankle":{
                return ByteToArrayOperations.hexStringToByteArray("AA05");
            }
            case "hip":{
                return ByteToArrayOperations.hexStringToByteArray("AA06");
            }

            case "wrist":{
                return ByteToArrayOperations.hexStringToByteArray("AA07");
            }

            case "shoulder":{
                return ByteToArrayOperations.hexStringToByteArray("AA08");
            }

            case "others":{
                return ByteToArrayOperations.hexStringToByteArray("AA04");
            }

            default:
                return ByteToArrayOperations.hexStringToByteArray("AA04");
        }
    }
}
