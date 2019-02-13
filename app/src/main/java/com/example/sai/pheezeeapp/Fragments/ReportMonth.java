package com.example.sai.pheezeeapp.Fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.RangeColumn;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.example.sai.pheezeeapp.Activities.PatientsView;
import com.example.sai.pheezeeapp.Activities.SessionReportActivity;
import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.utils.TimeOperations;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportMonth extends Fragment {
    //rom graph
    AnyChartView anyChartView_rom;
    Set set;
    Cartesian cartesian;


    //Bar chart
    BarChart barChart;
    BarDataSet barChartDataSet;
    BarData barChartData;
    ArrayList<BarEntry> dataPoints;
    BarGraphSeries<DataPoint> series;
    DataPoint[] datapoints;

    ImageView iv_left, iv_right, iv_left_joint, iv_right_joint;
    TextView tv_report_month, tv_overall, tv_individual, tv_joint_name, tv_total_hours, tv_total_reps_report, tv_total_holdtime,tv_body_part,tv_back;
    int currentMonth,current_bodypart=0;

    boolean mTypeSelected = true;   //true for over all and false for individual

    JSONArray session_array;
    ArrayList<String> str_part;
    Iterator iterator;

    public ReportMonth() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_report_month, container, false);

        anyChartView_rom = view.findViewById(R.id.rom_chartView);
        barChart = view.findViewById(R.id.emg_barchart_report);

        //bar chart related stuff
        dataPoints = new ArrayList<>();
        barChartDataSet=new BarDataSet(dataPoints, "Sessions Vs Emg Graph");
        barChartDataSet.setColor(Color.parseColor("#7BC0F7"));

        barChartData = new BarData(barChartDataSet);
        barChartData.setBarWidth(0.1f);
        barChart.setData(barChartData);
        barChart.invalidate();

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener()
        {
            @Override
            public void onValueSelected(Entry e, Highlight h)
            {
                float x=e.getX();

            }

            @Override
            public void onNothingSelected()
            {

            }
        });
        datapoints=new DataPoint[]{};
        series = new BarGraphSeries<>(datapoints);


        //range chart related stuff
        cartesian = AnyChart.cartesian();
        cartesian.animation(true,500);
        cartesian.title("Range Bar chart");
        anyChartView_rom.setChart(cartesian);
        anyChartView_rom.setZoomEnabled(true);
        anyChartView_rom.setHorizontalScrollBarEnabled(true);
        List<DataEntry> seriesData = new ArrayList<>();
        seriesData.add(new CustomDataEntry(1,0,0));

        set = Set.instantiate();
        set.data(seriesData);
        Mapping edinburgData = set.mapAs("{ x: 'x', high: 'MaxAngle', low: 'MinAngle' }");
        RangeColumn columnEdinburg = cartesian.rangeColumn(edinburgData);
        columnEdinburg.name("Range Of Motion");


        cartesian.xAxis(true);
        cartesian.yAxis(true);


        cartesian.yScale()
                .minimum(0d)
                .maximum(180d);


        cartesian.legend(true);

        cartesian.yGrid(false)
                .yMinorGrid(true);
        cartesian.tooltip().titleFormat("{%SeriesName} ({%x})");




        session_array = ((SessionReportActivity)getActivity()).getSessions();
        Log.i("month",session_array.toString());

        //defining all the view items
        iv_left = view.findViewById(R.id.iv_left);
        iv_right = view.findViewById(R.id.iv_right);
        iv_left_joint = view.findViewById(R.id.iv_left_joint);
        iv_right_joint = view.findViewById(R.id.iv_right_joint);

        tv_report_month = view.findViewById(R.id.tv_report_date);
        tv_overall = view.findViewById(R.id.tv_overall);
        tv_individual = view.findViewById(R.id.tv_individual);
        tv_joint_name = view.findViewById(R.id.tv_individual_joint_name);
        tv_total_hours = view.findViewById(R.id.tv_total_hours_session);
        tv_total_reps_report = view.findViewById(R.id.total_reps_report);
        tv_total_holdtime = view.findViewById(R.id.report_month_tv_hold_time);
        tv_body_part = view.findViewById(R.id.tv_individual_joint_name);
        tv_back = view.findViewById(R.id.tv_back_report);




        //setting the initial month
        try {
            setInitialMonth();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonth=currentMonth-1;
                String month = getMonthForInt(currentMonth);
                tv_report_month.setText(month);
                str_part = new ArrayList<>();
                HashSet<String> set_part = fetchAllParts();
                str_part = new ArrayList<>();
                iterator = set_part.iterator();
                while (iterator.hasNext()){
                    str_part.add(iterator.next()+"");
                }
                Log.i("array",str_part.toString());
                if(str_part.size()>0) {
                    makeIndividualVisible();
                    tv_body_part.setText(str_part.get(current_bodypart));
                }
                else {
                    makeIndividualInvisible();
                    Toast.makeText(getActivity(), "No Exercises done", Toast.LENGTH_SHORT).show();
                }
                try {
                    updateScreen(mTypeSelected);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonth+=1;
                String month = getMonthForInt(currentMonth);
                tv_report_month.setText(month);
                str_part = new ArrayList<>();
                HashSet<String> set_part = fetchAllParts();
                str_part = new ArrayList<>();
                iterator = set_part.iterator();
                while (iterator.hasNext()){
                    str_part.add(iterator.next()+"");
                }
                Log.i("array",str_part.toString());
                if(str_part.size()>0) {
                    makeIndividualVisible();
                    tv_body_part.setText(str_part.get(current_bodypart));
                }
                else {
                    makeIndividualInvisible();
                    Toast.makeText(getActivity(), "No Exercises done", Toast.LENGTH_SHORT).show();
                }
                try {
                    updateScreen(mTypeSelected);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });





        iv_left_joint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                current_bodypart--;
                Log.i("current body part",current_bodypart+"");
                if(current_bodypart>=0 && current_bodypart<str_part.size())
                    tv_body_part.setText(str_part.get(current_bodypart));
                else if(current_bodypart<0){
                    current_bodypart = str_part.size()-1;
                    tv_body_part.setText(str_part.get(current_bodypart));
                }
                try {
                    updateScreen(mTypeSelected);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        iv_right_joint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_bodypart++;
                Log.i("current body part",current_bodypart+"");
                if(current_bodypart>=0 && current_bodypart<str_part.size())
                    tv_body_part.setText(str_part.get(current_bodypart));
                else if(current_bodypart>=str_part.size()){
                    current_bodypart = 0;
                    tv_body_part.setText(str_part.get(current_bodypart));
                }
                try {
                    updateScreen(mTypeSelected);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        tv_overall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTypeSelected=true;
                changeViewOverallAndIndividual();
                tv_overall.setTypeface(null, Typeface.BOLD);
                tv_overall.setAlpha(1);
                makeIndividualInvisible();
                try {
                    updateScreen(mTypeSelected);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        tv_individual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTypeSelected = false;
                changeViewOverallAndIndividual();
                tv_individual.setTypeface(null, Typeface.BOLD);
                tv_individual.setAlpha(1);
                makeIndividualVisible();
                str_part = new ArrayList<>();
                HashSet<String> set_part = fetchAllParts();
                str_part = new ArrayList<>();
                iterator = set_part.iterator();
                while (iterator.hasNext()){
                    str_part.add(iterator.next()+"");
                }
                Log.i("array",str_part.toString());
                if(str_part.size()>0) {
                    makeIndividualVisible();
                    tv_body_part.setText(str_part.get(current_bodypart));
                }
                else {
                    makeIndividualInvisible();
                    Toast.makeText(getActivity(), "No Exercises done", Toast.LENGTH_SHORT).show();
                }
                try {
                    updateScreen(mTypeSelected);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PatientsView.class));
                getActivity().finish();
            }
        });

        return view;
    }


    private void setInitialMonth() throws JSONException {
        currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        String month = getMonthForInt(currentMonth);
        tv_report_month.setText(month);
        JSONArray array = getCurrentMonthJson(true);
        updateTotalTime();
        updateHoldTime(array);
        updateTotalReps(array);
        updateGraphs();
    }

    private void updateHoldTime(JSONArray array) throws JSONException {
            TimeOperations timeOperations = new TimeOperations();
            String temp_hours = timeOperations.addTotalHoldTime(array);
            tv_total_holdtime.setText(temp_hours);
    }


    public void makeIndividualInvisible(){
        tv_joint_name.setVisibility(View.GONE);
        iv_left_joint.setVisibility(View.GONE);
        iv_right_joint.setVisibility(View.GONE);
    }


    public void makeIndividualVisible(){
        tv_joint_name.setVisibility(View.VISIBLE);
        iv_left_joint.setVisibility(View.VISIBLE);
        iv_right_joint.setVisibility(View.VISIBLE);
    }


    public JSONArray getCurrentMonthJson(boolean mTypeSelected) throws JSONException {
        Toast.makeText(getActivity(), ""+currentMonth, Toast.LENGTH_SHORT).show();
        JSONArray array = new JSONArray();

        if(mTypeSelected) {
            for (int i = 0; i < session_array.length(); i++) {
                JSONObject object = session_array.getJSONObject(i);
                String month = object.getString("heldon");
                month = month.substring(5, 7);
                int m = Integer.parseInt(month);
                if (m == (currentMonth + 1)) {
                    Log.i("current", currentMonth + "");
                    array.put(object);
                }
            }
        }
        else {
            for (int i = 0; i < session_array.length(); i++) {
                JSONObject object = session_array.getJSONObject(i);
                String month = object.getString("heldon");
                month = month.substring(5, 7);
                int m = Integer.parseInt(month);
                if (m == (currentMonth + 1) && object.getString("bodypart").equals(tv_body_part.getText().toString())) {
                    Log.i("current", currentMonth + "");
                    array.put(object);
                }
            }
        }
        return array;
    }


    private void updateTotalTime() {
        JSONArray array = null;
        try {
            array = getCurrentMonthJson(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TimeOperations timeOperations = new TimeOperations();
        String str_time = timeOperations.addTotalTime(array);
        tv_total_hours.setText(str_time);
    }

    String getMonthForInt(int m) {
        String month;
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if(m<0){
            m=11;
            currentMonth=m;
        }
        else if(m>11){
            m=0;
            currentMonth=m;
        }
        month = months[m];

        return month;
    }

    public void updateTotalReps( JSONArray array) throws JSONException {
            int total_reps = 0;
            TimeOperations timeOperations = new TimeOperations();
            total_reps = timeOperations.addTotalRes(array);
            tv_total_reps_report.setText(total_reps+"");
    }



    public void changeViewOverallAndIndividual(){
        tv_overall.setTypeface(null, Typeface.NORMAL);
        tv_individual.setTypeface(null, Typeface.NORMAL);
        tv_overall.setAlpha(0.5f);
        tv_individual.setAlpha(0.5f);
    }

    public void updateScreen(boolean mTypeSelected) throws JSONException {
        JSONArray array = getCurrentMonthJson(mTypeSelected);
        Log.i("array",array.toString());
        updateTotalTime();
        updateHoldTime(array);
        updateTotalReps(array);
        updateGraphs();
    }

    private void updateGraphs() {
        JSONArray array = fetchAllWeeksArray(mTypeSelected);
        Log.i("Graphs",array.toString());
        updateEmgGraph(array);
        updateRomGraph(array);
    }

    private void updateRomGraph(JSONArray array) {
        List<DataEntry> data = new ArrayList<>();
        Log.i("Length",array.length()+"");
        if(array.length()==0){
            data.add(new CustomDataEntry(0,0,0));
        }
        else {
            for (int i = 0; i < array.length(); i++) {
                try {
                    int x = i + 1;
                    JSONObject object = array.getJSONObject(i);
                    int maxAngle = Integer.parseInt(object.getString("maxangle"));
                    int minAngle = Integer.parseInt(object.getString("minangle"));
                    data.add(new CustomDataEntry(x, maxAngle, minAngle));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        set.data(data);
    }

    private void updateEmgGraph(JSONArray array){
        dataPoints.clear();
        barChart.invalidate();
        barChart.notifyDataSetChanged();
        barChart.clear();
        series = new BarGraphSeries<>(datapoints);

        barChartDataSet=new BarDataSet(dataPoints, "Sessions Vs EMG Graph");
        Log.i("emg data set",array.length()+"");
        int j=0;
        for (int i=0;i<array.length();i++){
            String maxemg = null;
            try {
                maxemg = array.getJSONObject(i).get("maxemg").toString();
                barChartData.addEntry(new BarEntry(++j,Integer.parseInt(maxemg)),0);
                barChart.moveViewToX(j);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //maxemg = maxemg.substring(0,maxemg.length());


        }
        barChart.setData(barChartData);
        barChart.notifyDataSetChanged();
        barChart.invalidate();

        setEmgChartCharacteristics();
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(array.length()+1);
        xAxis.setGranularity(1f);

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

    public JSONArray fetchAllWeeksArray(boolean mTypeSelected){
        Date d_first_of_month = getFirstDate(currentMonth);
        Date d_last_of_month = getLastDate(currentMonth);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String first_date = simpleDateFormat.format(d_first_of_month);
        String last_date = simpleDateFormat.format(d_last_of_month);

        int last = Integer.parseInt(last_date.substring(8,10).trim());
        Log.i("First date",first_date+" "+last_date+" "+last);
        int maxAngle =0, minAngle = 0, maxEmg = 0, week=0, x=0;
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        if(mTypeSelected){
            for (int i=0;i<last;i++){
                week++;
                for (int j=0;j<session_array.length();j++){
                    try {
                        JSONObject object1 = session_array.getJSONObject(j);
                        if (first_date.equals(object1.getString("heldon").substring(0, 10).trim())) {
                            x++;
                            maxAngle += Integer.parseInt(object1.getString("maxangle"));
                            minAngle += Integer.parseInt(object1.getString("minangle"));
                            maxEmg += Integer.parseInt(object1.getString("maxemg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(week==7 || i==last-1){
                    if (x>0){
                        maxAngle = maxAngle/x;
                        minAngle = minAngle/x;
                        maxEmg = maxEmg/x;
                    }
                    try {
                        object.put("maxangle",maxAngle);
                        object.put("minangle",minAngle);
                        object.put("maxemg",maxEmg);
                        array.put(object);
                        object = new JSONObject();
                        week=0;x=0;maxAngle=0;minAngle=0;maxEmg=0;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(simpleDateFormat.parse(first_date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c.add(Calendar.DATE, 1);  // number of days to add
                first_date = simpleDateFormat.format(c.getTime());
                Log.i("First Date",first_date);
            }
        }
        else {
            for (int i = 0; i < last; i++) {
                week++;
                for (int j = 0; j < session_array.length(); j++) {
                    try {
                        JSONObject object1 = session_array.getJSONObject(j);
                        if (first_date.equals(object1.getString("heldon").substring(0, 10).trim()) && object1.getString("bodypart").equals(tv_body_part.getText().toString())) {
                            x++;
                            maxAngle += Integer.parseInt(object1.getString("maxangle"));
                            minAngle += Integer.parseInt(object1.getString("minangle"));
                            maxEmg += Integer.parseInt(object1.getString("maxemg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (week == 7 || i == last - 1) {
                    if (x > 0) {
                        maxAngle = maxAngle / x;
                        minAngle = minAngle / x;
                        maxEmg = maxEmg / x;
                    }
                    try {
                        object.put("maxangle", maxAngle);
                        object.put("minangle", minAngle);
                        object.put("maxemg", maxEmg);
                        array.put(object);
                        object = new JSONObject();
                        week = 0;
                        x = 0;
                        maxAngle = 0;
                        minAngle = 0;
                        maxEmg = 0;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(simpleDateFormat.parse(first_date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c.add(Calendar.DATE, 1);  // number of days to add
                first_date = simpleDateFormat.format(c.getTime());
                Log.i("First Date", first_date);
            }
        }


        return array;
    }

    private Date getFirstDate(int num) {
        Calendar c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.MONTH,num);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return  c.getTime();
    }
    private Date getLastDate(int num) {
        Calendar c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.MONTH,num);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return  c.getTime();
    }


    private class CustomDataEntry extends DataEntry {
        public CustomDataEntry(int x, Number maxAngle, Number minAngle) {
            setValue("x", x);
            setValue("MaxAngle", maxAngle);
            setValue("MinAngle", minAngle);
        }
    }


    private void setEmgChartCharacteristics() {
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChartData.setBarWidth(0.9f);

        //mChart.setHighlightFullBarEnabled(false);
        barChart.setPinchZoom(true);

        barChart.animateXY(1500, 2000);
        barChart.setHorizontalScrollBarEnabled(true);

        Legend l = barChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);// this replaces setStartAtZero(true)


        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


        //XAxis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
    }
}
