package com.start.apps.pheezee.utils;

import java.util.Random;

public class OtpGeneration {

    public static String OTP(int len)
    {
        // Using numeric values
        String numbers = "0123456789";

        // Using random method
        Random rndm_method = new Random();

        char[] otp = new char[len];

        for (int i = 0; i < len; i++)
        {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            otp[i] =
                    numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
        String otp_str = new String(otp);
        return otp_str;
    }

}
