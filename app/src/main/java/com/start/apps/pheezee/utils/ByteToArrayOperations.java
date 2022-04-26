package com.start.apps.pheezee.utils;

import android.util.Log;

import com.start.apps.pheezee.pojos.HealthData;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Main class with the packet structure emg
 */
public class ByteToArrayOperations {
    private static int emg_data_size_session = 20;
    private static int emg_num_packets_session = 40;
    private static int emg_data_size_raw=20;

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static float[] constructEmgData(byte[] sub_byte){
        int k=0;
        float[] emg_data = new float[emg_data_size_session];
        for (int i = 0; i<emg_num_packets_session; i++){
            int a = sub_byte[i]&0xFF;
            int b = sub_byte[i+1]&0xFF;

            emg_data[k] = b<<8 | a;
            //emg formula

            emg_data[k] = (float) (emg_data[k]/284.44);
//            Log.i("Emg before 1000",String.valueOf(emg_data[k]));
            emg_data[k]*=1000;
//            Log.i("Emg after 1000",String.valueOf(emg_data[k]));
            emg_data[k]= Float.parseFloat(roundOffTo2DecPlaces(emg_data[k]));
//            Log.i("EMG VALUE", String.valueOf(emg_data[k]));
            i++;
            k++;
        }
        return emg_data;
    }

    public static int[] constructEmgDataWithGain(byte[] sub_byte, int gain){
        int k=0;
        int[] emg_data = new int[emg_data_size_session];
        for (int i = 0; i<emg_num_packets_session; i++){
            emg_data[k] = sub_byte[i+1]<<8 | sub_byte[i]&0xFF;
            i++;
            k++;
        }
        return emg_data;
    }

    public static String roundOffTo2DecPlaces(float val)
    {
        return String.format("%.2f", val);
    }

    public static int[] constructEmgDataRaw(byte[] sub_byte){
        int k=0;
        int[] emg_data = new int[emg_data_size_raw];
        for (int i=0;i<sub_byte.length;i++){
            int a = sub_byte[i]&0xFF;
            int b = sub_byte[i+1]&0xFF;

            emg_data[k] = b<<8 | a;
            i++;
            k++;
        }
        return emg_data;
    }

    public static int getAngleFromData(byte a, byte b){
        return b<<8 | a&0xFF;
    }

    public static int getNumberOfReps(byte a, byte b){
        return (a & 0xff) |
                ((b & 0xff) << 8);
    }

    public static String byteToStringHexadecimal(byte b){
        return String.format("%02X",b);
    }


    public static String getDeviceUid(byte[] infor_packet){
        StringBuilder uid = new StringBuilder();
        int j=0;
        for(int i=23;i<=38;i++,j++){
            try {
                int a = infor_packet[i] & 0xFF;
                String s = Integer.toHexString(a);
                uid.append(s);
            }catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
        return uid.toString();
    }

    public static HealthData getHealthData(byte[] info_packet){
        HealthData data = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(new Date());
        String uid = ByteToArrayOperations.getDeviceUid(info_packet);
        try {
            data = new HealthData(dateString, uid, info_packet[2]&0xFF,info_packet[3]&0xFF,info_packet[4]&0xFF,info_packet[5]&0xFF,
                    info_packet[6]&0xFF,info_packet[7]&0xFF,info_packet[8]&0xFF,info_packet[12]&0xFF,info_packet[13]&0xFF,
                    info_packet[14]&0xFF,info_packet[15]&0xFF,info_packet[16]&0xFF,info_packet[17]&0xFF,info_packet[18]&0xFF,
                    info_packet[19]&0xFF,info_packet[20]&0xFF,info_packet[21]&0xFF,info_packet[22]&0xFF,info_packet[39]&0xFF
                    ,info_packet[40]&0xFF,info_packet[41]&0xFF,info_packet[42]&0xFF);
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            data = new HealthData(dateString, uid, 0,0,0,0,0,0,0,
                    0,0,0,0,0,0,0,0,
                    0, 0,0,0,0,0,0);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            data = new HealthData(dateString, uid, 0,0,0,0,0,0,0,
                    0,0,0,0,0,0,0,0,
                    0, 0,0,0,0,0,0);
        }
        return data;
    }

    public static boolean getMagnetometerPresent(byte[] info_packet){
        boolean magnetometer_status = false;
        try{
            int a = info_packet[39] & 0xFF;
            int b = info_packet[40] & 0xFF;
            Log.i("a,b",a+", "+b);
            if(a==0 && b==0){
                magnetometer_status = true;
            }
        }catch (ArrayIndexOutOfBoundsException e){
            magnetometer_status = false;
        }catch (IndexOutOfBoundsException e){
            magnetometer_status = false;
        }
        return magnetometer_status;
    }
}
