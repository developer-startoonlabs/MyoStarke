package com.example.sai.pheezeeapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class FragmentReportDay extends Fragment {

    ImageView iv_left, iv_right, iv_left_joint, iv_right_joint;
    TextView tv_report_day, tv_overall, tv_individual, tv_joint_name, tv_total_hours, tv_total_reps_report, tv_total_holdtime,tv_body_part,tv_back;
    int date_no=0, current_bodypart=0;
    String current_date;
    String mPartSelected = null;
    ArrayList<String> str_part;
    AnyChartView anyChartView_rom;
    boolean mTypeSelected = true;   //true for over all and false for individual
    Iterator iterator;
    JSONArray session_array;
    Set set;
    Cartesian cartesian;


    //Bar chart
    BarChart barChart;
    BarDataSet barChartDataSet;
    BarData barChartData;
    ArrayList<BarEntry> dataPoints;
    BarGraphSeries<DataPoint> series;
    DataPoint[] datapoints;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fragment_report_day, container, false);




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

        tv_report_day = view.findViewById(R.id.tv_report_date);
        tv_overall = view.findViewById(R.id.tv_overall);
        tv_individual = view.findViewById(R.id.tv_individual);
        tv_joint_name = view.findViewById(R.id.tv_individual_joint_name);
        tv_total_hours = view.findViewById(R.id.tv_total_hours_session);
        tv_total_reps_report = view.findViewById(R.id.total_reps_report);
        tv_total_holdtime = view.findViewById(R.id.tv_hold_time_report);
        tv_body_part = view.findViewById(R.id.tv_individual_joint_name);
        tv_back = view.findViewById(R.id.tv_back_report);




        setInitialDay();

        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDay(--date_no);
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
                updateScreen(mTypeSelected);
            }
        });

        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDay(++date_no);
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
                updateScreen(mTypeSelected);
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
                updateScreen(mTypeSelected);
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
                updateScreen(mTypeSelected);
            }
        });

        tv_overall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTypeSelected = true;
                changeViewOverallAndIndividual();
                tv_overall.setTypeface(null, Typeface.BOLD);
                tv_overall.setAlpha(1);
                makeIndividualInvisible();
                updateScreen(mTypeSelected);
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
                updateScreen(mTypeSelected);
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

    private HashSet<String> fetchAllParts() {
        HashSet<String> hashSet = new HashSet<>();
        JSONArray array = getCurrentDayJson(true);
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

    private void setInitialDay() {
        getDay(date_no);
        JSONArray array = getCurrentDayJson(true);
        updateTotalTime();
        updateHoldTime(array);
        updateTotalReps(array);
        updateGraphs(array);
    }

    private void updateGraphs(JSONArray array) {
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

    private void updateEmgGraph(JSONArray array) {
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

    private void updateTotalReps(JSONArray array) {
            TimeOperations timeOperations = new TimeOperations();
            int total_reps = timeOperations.addTotalRes(array);
            tv_total_reps_report.setText(total_reps+"");
    }

    private void updateHoldTime(JSONArray array) {
            TimeOperations timeOperations = new TimeOperations();
            String str_time = timeOperations.addTotalHoldTime(array);
            tv_total_holdtime.setText(str_time);
    }

    private void updateTotalTime() {
        JSONArray array = getCurrentDayJson(true);
        TimeOperations timeOperations = new TimeOperations();
        String str_time = timeOperations.addTotalTime(array);
        tv_total_hours.setText(str_time);
    }


    public void getDay(int day){
        Calendar c = Calendar.getInstance();

        c.add(Calendar.DATE, day);

        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat df_standard = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = df.format(c.getTime());
        current_date = df_standard.format(c.getTime());

        Log.v("PREVIOUS DATE : ", current_date);
        tv_report_day.setText(formattedDate);
    }


    public void changeViewOverallAndIndividual(){
        tv_overall.setTypeface(null, Typeface.NORMAL);
        tv_individual.setTypeface(null, Typeface.NORMAL);
        tv_overall.setAlpha(0.5f);
        tv_individual.setAlpha(0.5f);
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

    public JSONArray getCurrentDayJson(boolean mTypeSelected){
        JSONArray array = new JSONArray();
        if(mTypeSelected){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
            Log.i("current_date",current_date);
            Date date = null;
            try {
                date = simpleDateFormat.parse(current_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (int i=0;i<session_array.length();i++){
                try {
                    JSONObject object = session_array.getJSONObject(i);
                    String heldon = object.getString("heldon").substring(0,10).trim();
                    Date date1 = simpleDateFormat.parse(heldon);
                    if(date.compareTo(date1) == 0){
                        array.put(object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
            Log.i("current_date",current_date);
            Date date = null;
            try {
                date = simpleDateFormat.parse(current_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (int i=0;i<session_array.length();i++){
                try {
                    JSONObject object = session_array.getJSONObject(i);
                    String heldon = object.getString("heldon").substring(0,10).trim();
                    Date date1 = simpleDateFormat.parse(heldon);
                    if(date.compareTo(date1) == 0 && object.getString("bodypart").equals(tv_body_part.getText().toString())){
                        array.put(object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            Log.i("part array",array.toString());
        }

        return array;
    }




    public void updateScreen(boolean mTypeSelected){
        JSONArray array = getCurrentDayJson(mTypeSelected);
        updateTotalTime();
        updateHoldTime(array);
        updateTotalReps(array);
        updateGraphs(array);
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
        barChartData.setBarWidth(0.7f);

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
        rightAxis.setAxisMinimum(0f);
        rightAxis.setEnabled(false);// this replaces setStartAtZero(true)


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
