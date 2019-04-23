package com.example.sai.pheezeeapp.utils;

public class ByteToArrayOperations {
    private static int emg_data_size_session = 10;
    private static int emg_num_packets_session = 20;
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

    public static int[] constructEmgData(byte[] sub_byte){
        int k=0;
        int[] emg_data = new int[emg_data_size_session];
        for (int i = 0; i<emg_num_packets_session; i++){
            int a = sub_byte[i]&0xFF;
            int b = sub_byte[i+1]&0xFF;

            emg_data[k] = b<<8 | a;
            i++;
            k++;
        }
        return emg_data;
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

    public static int[] constructEmgDataRawWithoutCombine(byte[] sub_byte){
        int k=0;
        int[] emg_data = new int[emg_data_size_raw];
        for (int i=0;i<sub_byte.length;i++){
            emg_data[k] = sub_byte[i]&0xFF;
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
}
