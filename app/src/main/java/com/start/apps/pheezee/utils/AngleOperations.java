package com.start.apps.pheezee.utils;

public class AngleOperations {
    /**
     * getting max angle
     * @param string
     * @return
     */
    public int getMaxAngle(String string){
        int angle;
        switch (string.toLowerCase()){
            case "knee": {
                angle = 145;
                break;
            }
            case "hip":{
                angle = -15;
                break;
            }

            case "wrist":{
                angle = 90;
                break;
            }
            case "elbow":{
                angle = 25;
                break;
            }

            default:{
                angle=180;
                break;
            }
        }

        return angle;
    }

    /**
     * get min angle
     * @param string
     * @return
     */
    public int getMinAngle(String string){
        int angle;
        switch (string.toLowerCase()){
            case "knee": {
                angle = 0;
                break;
            }
            case "hip":{
                angle = -70;
                break;
            }

            case "wrist":{
                angle = 0;
                break;
            }
            case "elbow":{
                angle = -35;
                break;
            }

            default:{
                angle=180;
                break;
            }
        }

        return angle;
    }
}
