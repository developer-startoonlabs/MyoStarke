package com.start.apps.pheezee.fragments;


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

import androidx.annotation.NonNull;
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

import static com.start.apps.pheezee.activities.SessionReportActivity.patientId;
import static com.start.apps.pheezee.activities.SessionReportActivity.patientName;
import static com.start.apps.pheezee.activities.SessionReportActivity.phizioemail;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportMonth extends Fragment implements MqttSyncRepository.OnReportDataResponseListner {

    private TextView tv_report_month, tv_click_to_generate_report;
    private int currentMonth1, currentMonth2;

    private GetReportDataResponse session_array;
    private ArrayList<String> str_part;
    private Iterator iterator;
    private String month_end_date = "";
    private ProgressDialog report_dialog;
    private StartAndEndDate global_date = null;
    MqttSyncRepository repository;


    public ReportMonth() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_report_month, container, false);




        session_array = ((SessionReportActivity)getActivity()).getSessions();

        //defining all the view items
        ImageView iv_left = view.findViewById(R.id.fragment_month_iv_left);
        ImageView iv_right = view.findViewById(R.id.fragment_month_iv_right);

        tv_report_month = view.findViewById(R.id.fragment_month_tv_report_date);
        tv_click_to_generate_report = view.findViewById(R.id.fragment_month_generate_report);
        report_dialog = new ProgressDialog(getActivity());
        report_dialog.setMessage("Generating monthly report please wait....");
        report_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        report_dialog.setIndeterminate(true);
        repository = new MqttSyncRepository(getActivity().getApplication());
        repository.setOnReportDataResponseListener(this);


        //setting the initial month
        try {
            setInitialMonth();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global_date = getPreviousDates();
                currentMonth1 = global_date.getStart_date().get(Calendar.MONTH);
                currentMonth2 = global_date.getEnd_date().get(Calendar.MONTH);
                String month_string = calanderToString(global_date.getStart_date())+" - "+calanderToString(global_date.getEnd_date());
                tv_report_month.setText(month_string);
                month_end_date = calenderToYYYMMDD(global_date.getEnd_date());
                str_part = new ArrayList<>();
                HashSet<String> set_part = fetchAllParts();
                str_part = new ArrayList<>();
                iterator = set_part.iterator();
                while (iterator.hasNext()){
                    str_part.add(iterator.next()+"");
                }
                if(str_part.size()<=0) {
                    tv_click_to_generate_report.setText("No exercise done in this month");
                }else {
                    tv_click_to_generate_report.setText("Click to generate monthly report");
                }
            }
        });

        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global_date = getNextDates();
                currentMonth1 = global_date.getStart_date().get(Calendar.MONTH);
                currentMonth2 = global_date.getEnd_date().get(Calendar.MONTH);
                String month_string = calanderToString(global_date.getStart_date())+" - "+calanderToString(global_date.getEnd_date());
                tv_report_month.setText(month_string);
                month_end_date = calenderToYYYMMDD(global_date.getEnd_date());
                str_part = new ArrayList<>();
                HashSet<String> set_part = fetchAllParts();
                str_part = new ArrayList<>();
                iterator = set_part.iterator();
                while (iterator.hasNext()){
                    str_part.add(iterator.next()+"");
                }
                if(str_part.size()<=0)
                    tv_click_to_generate_report.setText("No exercise done in this month");
                else {
                    tv_click_to_generate_report.setText("Click to generate monthly report");
                }
            }
        });

        tv_click_to_generate_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_click_to_generate_report.getText().toString().substring(0,2).equals("No")){
                    Toast.makeText(getActivity(), "No Exercises done in this month.", Toast.LENGTH_SHORT).show();
                }
                else {
                    getMonthReport(month_end_date);
                }
            }
        });

        return view;
    }


    private void setInitialMonth() throws JSONException {
        global_date = getFirstDates();
        currentMonth1 = global_date.getStart_date().get(Calendar.MONTH);
        currentMonth2 = global_date.getEnd_date().get(Calendar.MONTH);
        String month_string = calanderToString(global_date.getStart_date())+" - "+calanderToString(global_date.getEnd_date());
        tv_report_month.setText(month_string);
        month_end_date = calenderToYYYMMDD(global_date.getEnd_date());
        str_part = new ArrayList<>();
        HashSet<String> set_part = fetchAllParts();
        str_part = new ArrayList<>();
        iterator = set_part.iterator();
        while (iterator.hasNext()){
            str_part.add(iterator.next()+"");
        }
        if(str_part.size()<=0)
            tv_click_to_generate_report.setText("No exercise done in this month");
        else {
            tv_click_to_generate_report.setText("Click to generate monthly report");
        }
    }



    private JSONArray getCurrentMonthJson(boolean mTypeSelected) throws JSONException {
        JSONArray array = new JSONArray();
//        for (int i = 0; i < session_array.length(); i++) {
//            JSONObject object = session_array.getJSONObject(i);
//            String month = object.getString("heldon");
//            month = month.substring(5, 7);
//            int m = Integer.parseInt(month);
//            if (m == (currentMonth1 + 1) || m == (currentMonth2 + 1) ) {
//                array.put(object);
//            }
//        }
        return array;
    }

    private HashSet<String> fetchAllParts() {
        HashSet<String> hashSet = new HashSet<>();
        JSONArray array = null;
        try {
            array = getCurrentMonthJson(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    private StartAndEndDate getFirstDates(){
        StartAndEndDate date = null;
        Calendar cal_first_date = Calendar.getInstance(), cal_end_date = Calendar.getInstance(), cal_end_month = Calendar.getInstance();
        String dateOfJoin = SessionReportActivity.dateofjoin;
        Date first_date= null;
        try {
            first_date = new SimpleDateFormat("dd/MM/yyyy").parse(dateOfJoin);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal_first_date.add(Calendar.DATE,-29);
//        cal_end_month.setTime(first_date);
        date = new StartAndEndDate(cal_first_date, cal_end_month);
        return date;
    }

    private StartAndEndDate getPreviousDates(){
        Calendar cal_first_date = Calendar.getInstance(), cal_end_date = Calendar.getInstance(), cal_end_month = Calendar.getInstance();
        String dateOfJoin = SessionReportActivity.dateofjoin;
        Date first_date_join= null;
        try {
            first_date_join = new SimpleDateFormat("dd/MM/yyyy").parse(dateOfJoin);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal_first_date.setTime(first_date_join);
        Calendar first_date = global_date.getStart_date();
        Calendar end_date = global_date.getEnd_date();
        first_date.add(Calendar.DATE,-30);
        end_date.add(Calendar.DATE,-30);
        if(end_date.compareTo(cal_first_date)>=0){
            global_date.setStart_date(first_date);
            global_date.setEnd_date(end_date);
            return global_date;
        }
        else {
            first_date.add(Calendar.DATE,30);
            end_date.add(Calendar.DATE,30);
            global_date.setStart_date(first_date);
            global_date.setEnd_date(end_date);
            return global_date;
        }
    }

    private StartAndEndDate getNextDates(){
        Calendar cal_end_date = Calendar.getInstance();
        Calendar first_date = global_date.getStart_date();
        Calendar end_date = global_date.getEnd_date();
        first_date.add(Calendar.DATE,30);
        end_date.add(Calendar.DATE,30);
        if(end_date.compareTo(cal_end_date)<=0){
            global_date.setStart_date(first_date);
            global_date.setEnd_date(end_date);
            return global_date;
        }
        else {
            first_date.add(Calendar.DATE,-30);
            end_date.add(Calendar.DATE,-30);
            global_date.setStart_date(first_date);
            global_date.setEnd_date(end_date);
            return global_date;
        }
    }


    /**
     *
     *date of join to next month datea
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
//        cal_end_month.add(Calendar.DATE,29);
//
//        while (cal_end_month.compareTo(cal_end_date)<0){
//            cal_first_date.add(Calendar.DATE,29);
//            cal_end_month.add(Calendar.DATE,29);
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
//        first_date.add(Calendar.DATE,-29);
//        end_date.add(Calendar.DATE,-29);
//        Log.i("comparedates", String.valueOf(first_date.compareTo(cal_first_date)));
//        if(first_date.compareTo(cal_first_date)>=0){
//            global_date.setStart_date(first_date);
//            global_date.setEnd_date(end_date);
//            return global_date;
//        }
//        else {
//            first_date.add(Calendar.DATE,29);
//            end_date.add(Calendar.DATE,29);
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
//        first_date.add(Calendar.DATE,29);
//        end_date.add(Calendar.DATE,29);
//        Log.i("comparedates", String.valueOf(first_date.compareTo(cal_end_date)));
//        if(first_date.compareTo(cal_end_date)<=0){
//            global_date.setStart_date(first_date);
//            global_date.setEnd_date(end_date);
//            return global_date;
//        }
//        else {
//            first_date.add(Calendar.DATE,-29);
//            end_date.add(Calendar.DATE,-29);
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


    private void getMonthReport(String date){
        String url = "/getreport/monthly/"+patientId+"/"+phizioemail+"/" + date;
        report_dialog.setMessage("Generating monthly report for sessions held before "+date+", please wait....");
        report_dialog.show();
        repository.getDayReport(url,patientName+"-monthly");
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