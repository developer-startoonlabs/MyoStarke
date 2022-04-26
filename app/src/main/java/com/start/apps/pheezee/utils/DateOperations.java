package com.start.apps.pheezee.utils;

import android.annotation.SuppressLint;
import android.net.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateOperations {
    @SuppressLint("SimpleDateFormat")
    public static String getDateInMonthAndDate(String date) throws ParseException {
        if(!date.equals("")) {
            int d= Integer.parseInt(date.substring(0,2));
            int m  = (Integer.parseInt(date.substring(3,5))-1);
            int y = Integer.parseInt(date.substring(6,10));
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DATE,d);
            cal.set(Calendar.MONTH,m);
            cal.set(Calendar.YEAR,y);
            String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
            String dayName = new SimpleDateFormat("EEEE").format(cal.getTime());
            String dat = new SimpleDateFormat("dd").format(cal.getTime());
            dayName = dayName.substring(0,3);
            return (monthName.substring(0,3)+" "+dat+", "+dayName);
        }

        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateInMonthAndDateNew(String date) throws ParseException {
        if(!date.equals("")) {
            int y= Integer.parseInt(date.substring(0,4));
            int m  = (Integer.parseInt(date.substring(5,7))-1);
            int d = Integer.parseInt(date.substring(8,10));
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DATE,d);
            cal.set(Calendar.MONTH,m);
            cal.set(Calendar.YEAR,y);
            String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
            String dayName = new SimpleDateFormat("EEEE").format(cal.getTime());
            String dat = new SimpleDateFormat("dd").format(cal.getTime());
            return (monthName.substring(0,3)+" "+dat+", "+y);
        }
        return "";
    }


    public static String dateInMmDdYyyy(){
        Date today =new Date();
        int date = today.getDate();
        int month = today.getMonth()+1;
        int year = today.getYear()+1900;
        String todaysDate, d,m;
        d=date+""; m=month+"";
        if(date<10)
            d = "0"+date;

        if(month<10)
            m = "0"+month;

        todaysDate = d+"/"+m+"/"+year+"";

        return todaysDate;
    }
}
