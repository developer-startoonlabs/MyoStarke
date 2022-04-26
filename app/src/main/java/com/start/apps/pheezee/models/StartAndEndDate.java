package com.start.apps.pheezee.models;

import java.util.Calendar;

public class StartAndEndDate {

    Calendar start_date, end_date;


    public StartAndEndDate(Calendar start_date, Calendar end_date) {
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public Calendar getStart_date() {
        return start_date;
    }

    public void setStart_date(Calendar start_date) {
        this.start_date = start_date;
    }

    public Calendar getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Calendar end_date) {
        this.end_date = end_date;
    }

}
