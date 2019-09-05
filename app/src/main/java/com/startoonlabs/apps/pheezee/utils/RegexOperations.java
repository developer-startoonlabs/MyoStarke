package com.startoonlabs.apps.pheezee.utils;

import android.util.Patterns;
import android.widget.Toast;

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

    public static boolean checkIfNumeric(String value){
        boolean flag = true;
        int number = 0;
        try {
            number = Integer.parseInt(value);
        }catch (NumberFormatException e){
            flag = false;
        }
//        if(number>1000)
//            flag = false;
        return flag;
    }

    public static boolean isLoginValid(String email, String password){
        if(email.equals("")||password.equals(""))
            return false;
        else if(!isValidEmail(email))
            return false;
        else
            return true;
    }

    public static String getNonValidMessageLogin(String email, String password){
        if(email.equals("")||password.equals(""))
            return "Please fill all details";
        else
            return "Invalid Email Address";
    }

    public static boolean isSignupValid(String name, String email, String password, String phone){
        if(name.equals("")||password.equals("")||email.equals("")||phone.equals(""))
            return false;
        else if(!isValidEmail(email)|| !isValidMobileNumber(phone.replaceAll("\\s","")))
            return false;
        else
            return true;
    }

    public static String getNonValidMessageSignup(String name, String email, String password, String phone ){
        if(name.equals("")||password.equals("")||email.equals("")||phone.equals("")){
            return "Please fill all the details";
        }

        else {
            if(!RegexOperations.isValidEmail(email)&& !RegexOperations.isValidMobileNumber(phone))
                return "Invalid Credentials";
            else if(!RegexOperations.isValidEmail(email))
                return "Invalid Email Address";
            else
                return "Invalid Phone Number";
        }
    }

    public static boolean isValidUpdatePhizioDetails(String name, String phone){
        if (!name.equals("")) {
            if (!phone.equals("") || isValidMobileNumber(phone.replaceAll("\\s", ""))) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public static String getNonValidStringForPhizioDetails(String name, String phone){
        if (!name.equals("")) {
            return "Please enter name";
        }
        else if(phone.equals("")) {
            return "Please enter phone";
        }
        else {
            return "Enter valid phone";
        }
    }


}
