package com.example.sai.pheezeeapp.utils;

import android.util.Patterns;

import java.util.regex.Pattern;

public class RegexOperations {
    public static boolean isValidEmail(String email){
        if(email.equalsIgnoreCase("")){
           return false;
        }
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public static boolean isValidMobileNumber(String mobileNumber){
        if(mobileNumber.equalsIgnoreCase(""))
            return false;

        Pattern pattern = Patterns.PHONE;
       if(mobileNumber.substring(0,1).equalsIgnoreCase("+")){
           if(mobileNumber.length()<13 || mobileNumber.length()>13)
               return false;
           return  pattern.matcher(mobileNumber).matches();
       }
       else if(mobileNumber.substring(0,1).equalsIgnoreCase("0")){
           if(mobileNumber.length()!=11)
               return false;
           return  pattern.matcher(mobileNumber).matches();
       }
       else {
           if (mobileNumber.length()!=10)
               return false;
           return pattern.matcher(mobileNumber).matches();
       }
    }
}
