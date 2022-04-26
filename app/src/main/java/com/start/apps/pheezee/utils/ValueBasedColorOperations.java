package com.start.apps.pheezee.utils;

import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;

import start.apps.pheezee.R;

import java.util.HashMap;

public class ValueBasedColorOperations {
    public static final int MAX_NORMAL_EMG = 900;
    public static final int SMILE_ARC_MAX_ANGLE = 180;


    static HashMap<String, Integer> Bodypart_exercise_lookuptable = new HashMap<String, Integer>(){
        {
            put("Shoulder", 0);
            put("Elbow", 1);
            put("Forearm", 2);
            put("Wrist", 3);
            put("Hip", 4);
            put("Knee", 5);
            put("Ankle", 6);
            put("Spine", 7);
            put("Cervical", 9);
            put("Thoracic", 10);
            put("Lumbar", 11);
            put("Abdomen", 8);
        }
    };
    /**
     *
     * @param bodypart
     * @param max
     * @param min
     * @param context
     * @return
     */
    public static int getCOlorBasedOnTheBodyPart(String bodypart, int exercise, int max, int min, Context context){
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


    public static int getCOlorBasedOnTheBodyPartExercise(String bodypart ,int exercise, int max, int min, Context context){
        int bodyPart;
        int maxStaticRange = getBodyPartMaxValue(bodypart,exercise);
        int range = max-min;
        if((range<(maxStaticRange/3))){
            bodyPart = 2;
        }
        else if((range<((2*maxStaticRange)/3))){
            bodyPart = 1;
        }
        else {
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
    public static int getBodyPartMaxValue(String str_body_part, String bodypart){
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
            {0,0,0,0,0,0,0,0}, //shoulder
            {0,0,0,0},    //elbow
            {0,0,0,0},        // forearm
            {0,0,0,0,0,0},             //wrist
            {0,0,0,0,0,0,0,0},  //Hip
            {0,0,0,0,0,0},          //knee
            {0,0,0,0,0,0},             //ankle
            {0,0,0,0,0,0},    // spine
            {0,0,0,0,0,0},    // abdomen
            {0,0,0,0,0,0},    // cervical
            {0,0,0,0,0,0},    // thoracic
            {0,0,0,0,0,0},    // lumbar
            {0,0}
    };
    public static int getBodyPartMinValue(String bodypart, int exercisename){
        return min_values[Bodypart_exercise_lookuptable.get(bodypart)][exercisename];
    }

    private final static int[][] max_values = {
            {0,180,45,180,45,0}, //shoulder
            {0,145,145,0},    //elbow
            {0,90,90,0},            // forearm
            {0,80,70,0},             //wrist
            {0,125,10,45,10,0},  //Hip
            {0,140,140,0},          //knee
            {0,45,20,40,20,0},             //ankle
            {0,75,30,35,30,0},      // spine
            {0,75,30,35,0},      // abdomen
            {0,75,30,35,30,0},      // cervical
            {0,75,30,35,30,0},      // thoracic
            {0,75,30,35,30,0},      // lumbar
            {0,0}
    };

    public static int getBodyPartMaxValue(String bodypart, int exercisename){
        return max_values[Bodypart_exercise_lookuptable.get(bodypart)][exercisename];
    }

    public static byte[] getParticularDataToPheeze(int body_orientation, int muscle_index, int exercise_index, int bodypart_index,
                                                   int orientation_position){
        Log.i("VALUEORIENTATION", String.valueOf(orientation_position));
        byte[] b = new byte[6];
        if(bodypart_index==8){
            bodypart_index = 1;
        }
        String ae = "AE";
        byte[] b1 = ByteToArrayOperations.hexStringToByteArray("AE");
//        try {
//            b[0] = Byte.parseByte(String.format("%040x", new BigInteger(1, ae.getBytes("UTF-16"))));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        b[0] = b1[0];
        b[1] = (byte) bodypart_index;
        b[2] = (byte) exercise_index;
        b[3] = (byte) muscle_index;
        b[4] = (byte) body_orientation;
        b[5] = (byte) orientation_position;

//        b = ByteToArrayOperations.hexStringToByteArray(ae);
        return b;
//
//
//        Log.i("bodypart", String.valueOf(bodypart_index));
//        byte[] b;
//        if(bodypart_index!=6) {
//            b = ByteToArrayOperations.hexStringToByteArray("AA0" + (bodypart_index + 3));
//            Log.i("value","AA0"+(bodypart_index+3));
//        }
//        else {
//            b = ByteToArrayOperations.hexStringToByteArray("AA04");
//        }
//        return b;
    }


    public static int getDrawableBasedOnBodyPart(String bodypart){
        switch (bodypart.toLowerCase()){
            case "elbow":{
                return R.drawable.elbow_part_new;
            }

            case "knee":{
                return R.drawable.knee_part_new;
            }

            case "ankle":{
                return R.drawable.ankle_part_new;
            }

            case "hip":{
                return R.drawable.hip_part_new;
            }

            case "wrist":{
                return R.drawable.wrist_part_new;
            }

            case "shoulder":{
                return R.drawable.shoulder_part_new;
            }

            case "forearm":{
                return R.drawable.forearm_part_new;
            }

            case "spine":{
                return R.drawable.spine_part_new;
            }
            case "cervical":{
                return R.drawable.cervical_part_new;
            }
            case "thoracic":{
                return R.drawable.thoracic_part_new;
            }
            case "lumbar":{
                return R.drawable.lumbar_part_new;
            }
            case "abdomen":{
                return R.drawable.abdomen_part_new;
            }

            default:{
                return R.drawable.other_part_new;
            }
        }
    }





}
