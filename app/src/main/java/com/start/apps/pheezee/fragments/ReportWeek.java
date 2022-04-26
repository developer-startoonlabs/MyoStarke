//week
package com.start.apps.pheezee.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import start.apps.pheezee.R;
import com.start.apps.pheezee.activities.SessionReportActivity;
import com.start.apps.pheezee.models.StartAndEndDate;
import com.start.apps.pheezee.pojos.GetReportDataResponse;
import com.start.apps.pheezee.repository.MqttSyncRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import static com.start.apps.pheezee.activities.SessionReportActivity.patientId;
import static com.start.apps.pheezee.activities.SessionReportActivity.patientName;
import static com.start.apps.pheezee.activities.SessionReportActivity.phizioemail;


public class ReportWeek extends Fragment implements MqttSyncRepository.OnReportDataResponseListner {

    ImageView iv_left, iv_right;
    TextView tv_report_week, tv_click_to_view_report;
    String start_date, end_date;

    GetReportDataResponse session_array;
    ArrayList<String> str_part;
    Iterator iterator;
    ProgressDialog report_dialog;
    private StartAndEndDate global_date = null;
    private String week_end_date = "";
    MqttSyncRepository repository;

    public ReportWeek() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_report_week, container, false);

        session_array = ((SessionReportActivity)getActivity()).getSessions();

        //defining all the view items
        iv_left = view.findViewById(R.id.fragment_week_iv_left);
        iv_right = view.findViewById(R.id.fragment_week_iv_right);

        tv_report_week = view.findViewById(R.id.fragment_week_tv_report_date);
        tv_click_to_view_report = view.findViewById(R.id.fragment_week_generate_report);
        report_dialog = new ProgressDialog(getActivity());
        report_dialog.setMessage("Generating weekly report please wait....");
        report_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        report_dialog.setIndeterminate(true);
        repository = new MqttSyncRepository(getActivity().getApplication());
        repository.setOnReportDataResponseListener(this);


        setInitialweek();
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global_date = getPreviousDates();
                tv_report_week.setText(calanderToString(global_date.getStart_date())+" - "+calanderToString(global_date.getEnd_date()));
                week_end_date = calenderToYYYMMDD(global_date.getEnd_date());
                HashSet<String> set_part = fetchAllParts();
                str_part = new ArrayList<>();
                iterator = set_part.iterator();
                while (iterator.hasNext()){
                    str_part.add(iterator.next()+"");
                }
                if(str_part.size()<=0) {
                    tv_click_to_view_report.setText("No exercise done in this week");

                }else {
                    tv_click_to_view_report.setText("Click to generate weekly report");
                }
            }
        });

        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global_date = getNextDates();
                tv_report_week.setText(calanderToString(global_date.getStart_date())+" - "+calanderToString(global_date.getEnd_date()));
                week_end_date = calenderToYYYMMDD(global_date.getEnd_date());
                HashSet<String> set_part = fetchAllParts();
                str_part = new ArrayList<>();
                iterator = set_part.iterator();
                while (iterator.hasNext()){
                    str_part.add(iterator.next()+"");
                }
                if(str_part.size()<=0) {
                    tv_click_to_view_report.setText("No exercise done in this week");
                }else {
                    tv_click_to_view_report.setText("Click to generate weekly report");
                }
            }
        });


        tv_click_to_view_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_click_to_view_report.getText().toString().substring(0,2).equals("No")){
                    Toast.makeText(getActivity(), "No Exercises done in this week.", Toast.LENGTH_SHORT).show();
                }
                else {
                    getWeekReport(week_end_date);
                }
            }
        });

        return view;
    }

    private void setInitialweek() {
        global_date = getFirstDates();
        tv_report_week.setText(calanderToString(global_date.getStart_date())+" - "+calanderToString(global_date.getEnd_date()));
        week_end_date = calenderToYYYMMDD(global_date.getEnd_date());
        str_part = new ArrayList<>();
        HashSet<String> set_part = fetchAllParts();
        str_part = new ArrayList<>();
        iterator = set_part.iterator();
        while (iterator.hasNext()){
            str_part.add(iterator.next()+"");
        }
        if(str_part.size()<=0) {
            tv_click_to_view_report.setText("No exercise done in this week");
        }
        else {
            tv_click_to_view_report.setText("Click to generate weekly report");
        }

    }

    public String getWeek(int weekFromToday) {
        System.out.println("Pass Wee "+weekFromToday);
        Calendar mCalendar =  Calendar.getInstance();
        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        mCalendar.set(Calendar.WEEK_OF_YEAR,
                mCalendar.get(Calendar.WEEK_OF_YEAR) + weekFromToday);

        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat month_name = new SimpleDateFormat("MMMM");
        SimpleDateFormat format_date = new SimpleDateFormat("dd");
        SimpleDateFormat format_year = new SimpleDateFormat("yyyy");
        start_date= ymd.format(mCalendar.getTime());
        String show_date = format_date.format(mCalendar.getTime());

        //gestureEvent.setText(reportDate);
        mCalendar.add(Calendar.DAY_OF_MONTH, 6);
        Date d = mCalendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String endDate= formatter.format(d);
        String date = format_date.format(mCalendar.getTime());
        String month =  month_name.format(mCalendar.getTime());
        String year = format_year.format(mCalendar.getTime());
        String show_date2 = date+" "+month.substring(0,3)+" "+year;
        end_date = ymd.format(mCalendar.getTime());
        tv_report_week.setText(show_date + "-" + show_date2);

        return endDate;
    }

    private HashSet<String> fetchAllParts() {
        HashSet<String> hashSet = new HashSet<>();
        JSONArray array = getCurrentWeekJson();
        if(array.length()>0) {
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    hashSet.add(object.getString("bodypart"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        return hashSet;
    }

    private JSONArray getCurrentWeekJson() {
        JSONArray array= new JSONArray();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date initial_date = global_date.getStart_date().getTime(),final_date = global_date.getEnd_date().getTime();
        String str_initial_date = "", str_final_date = "";
        str_initial_date = dateFormat.format(initial_date);
        str_final_date = dateFormat.format(final_date);

        try {
            initial_date = simpleDateFormat.parse(str_initial_date);
            final_date = simpleDateFormat.parse(str_final_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        for (int i=0;i<session_array.length();i++){
//            JSONObject object = null;
//            try {
//                object = session_array.getJSONObject(i);
//                String heldon = object.getString("heldon").substring(0,10).trim();
//                Date date1 = simpleDateFormat.parse(heldon);
//                if(date1.compareTo(initial_date)>=0 && date1.compareTo(final_date)<=0){
//                    array.put(object);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
        return array;
    }


    @SuppressLint("SimpleDateFormat")
    private StartAndEndDate getFirstDates(){
        StartAndEndDate date = null;
        Calendar cal_first_date = Calendar.getInstance(), cal_end_date = Calendar.getInstance(), cal_end_month = Calendar.getInstance();
        String dateOfJoin = SessionReportActivity.dateofjoin;
        cal_first_date.add(Calendar.DATE,-6);
//        cal_end_month.setTime(first_date);
        date = new StartAndEndDate(cal_first_date, cal_end_month);
        return date;
    }

    private StartAndEndDate getPreviousDates(){
        Calendar cal_first_date = Calendar.getInstance(), cal_end_date = Calendar.getInstance(), cal_end_month = Calendar.getInstance();
        String dateOfJoin = SessionReportActivity.dateofjoin;
        Date first_date_join = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            first_date_join = format.parse(dateOfJoin);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cal_first_date.setTime(first_date_join);
        Calendar first_date = global_date.getStart_date();
        Calendar end_date = global_date.getEnd_date();
        first_date.add(Calendar.DATE,-7);
        end_date.add(Calendar.DATE,-7);
        if(end_date.compareTo(cal_first_date)>=0){
            global_date.setStart_date(first_date);
            global_date.setEnd_date(end_date);
            return global_date;
        }
        else {
            first_date.add(Calendar.DATE,7);
            end_date.add(Calendar.DATE,7);
            global_date.setStart_date(first_date);
            global_date.setEnd_date(end_date);
            return global_date;
        }
    }

    private StartAndEndDate getNextDates(){
        Calendar cal_end_date = Calendar.getInstance();
        Calendar first_date = global_date.getStart_date();
        Calendar end_date = global_date.getEnd_date();
        first_date.add(Calendar.DATE,7);
        end_date.add(Calendar.DATE,7);
        if(end_date.compareTo(cal_end_date)<=0){
            global_date.setStart_date(first_date);
            global_date.setEnd_date(end_date);
            return global_date;
        }
        else {
            first_date.add(Calendar.DATE,-7);
            end_date.add(Calendar.DATE,-7);
            global_date.setStart_date(first_date);
            global_date.setEnd_date(end_date);
            return global_date;
        }
    }

    /**
     *
     * dateofjoin to the next date
     */
//    private StartAndEndDate getFirstDates(){
//        StartAndEndDate date = null;
//        Calendar cal_first_date = Calendar.getInstance(), cal_end_date = Calendar.getInstance(), cal_end_month = Calendar.getInstance();
//        String dateOfJoin = SessionReportActivity.dateofjoin;
//        Date first_date= null;
//        try {
//            first_date = new SimpleDateFormat("dd/MM/yyyy").parse(dateOfJoin);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Log.i("Date of join", first_date.toString());
//        cal_first_date.setTime(first_date);
//        cal_end_month.setTime(first_date);
//        cal_end_month.add(Calendar.DATE,6);
//
//        while (cal_end_month.compareTo(cal_end_date)<0){
//            cal_first_date.add(Calendar.DATE,6);
//            cal_end_month.add(Calendar.DATE,6);
//        }
//        date = new StartAndEndDate(cal_first_date, cal_end_month);
//        return date;
//    }
//
//    private StartAndEndDate getPreviousDates(){
//        Calendar cal_first_date = Calendar.getInstance(), cal_end_date = Calendar.getInstance(), cal_end_month = Calendar.getInstance();
//        String dateOfJoin = SessionReportActivity.dateofjoin;
//        Date first_date_join= null;
//        try {
//            first_date_join = new SimpleDateFormat("dd/MM/yyyy").parse(dateOfJoin);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Log.i("Date of join", first_date_join.toString());
//        cal_first_date.setTime(first_date_join);
//        Calendar first_date = global_date.getStart_date();
//        Calendar end_date = global_date.getEnd_date();
//        first_date.add(Calendar.DATE,-6);
//        end_date.add(Calendar.DATE,-6);
//        Log.i("comparedates", String.valueOf(first_date.compareTo(cal_first_date)));
//        if(first_date.compareTo(cal_first_date)>=0){
//            global_date.setStart_date(first_date);
//            global_date.setEnd_date(end_date);
//            return global_date;
//        }
//        else {
//            first_date.add(Calendar.DATE,6);
//            end_date.add(Calendar.DATE,6);
//            global_date.setStart_date(first_date);
//            global_date.setEnd_date(end_date);
//            return global_date;
//        }
//    }
//
//    private StartAndEndDate getNextDates(){
//        Calendar cal_end_date = Calendar.getInstance();
//        Calendar first_date = global_date.getStart_date();
//        Calendar end_date = global_date.getEnd_date();
//        first_date.add(Calendar.DATE,6);
//        end_date.add(Calendar.DATE,6);
//        Log.i("comparedates", String.valueOf(first_date.compareTo(cal_end_date)));
//        if(first_date.compareTo(cal_end_date)<=0){
//            global_date.setStart_date(first_date);
//            global_date.setEnd_date(end_date);
//            return global_date;
//        }
//        else {
//            first_date.add(Calendar.DATE,-6);
//            end_date.add(Calendar.DATE,-6);
//            global_date.setStart_date(first_date);
//            global_date.setEnd_date(end_date);
//            return global_date;
//        }
//    }

    private String calanderToString(Calendar date){
        Date date_cal = date.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        String strDate = formatter.format(date_cal);
        return strDate;
    }

    private String calenderToYYYMMDD(Calendar date){
        Date date_cal = date.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(date_cal);
        return strDate;
    }

    private void getWeekReport(String date){
        String url = "/getreport/weekly/"+patientId+"/"+phizioemail+"/" + date;
        report_dialog.setMessage("Generating weekly report for sessions before "+date+", please wait....");
        report_dialog.show();
        repository.getDayReport(url,patientName+"-weekly");
    }

    @Override
    public void onReportDataReceived(GetReportDataResponse array, boolean response) {

    }

    @Override
    public void onDayReportReceived(File file, String message, Boolean response) {
        report_dialog.dismiss();
        if(response){
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".my.package.name.provider", file), "application/pdf");
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                startActivity(target);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
            }
        }
        else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        repository.disableReportDataListner();
        super.onDestroy();
    }
}

