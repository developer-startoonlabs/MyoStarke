package com.startoonlabs.apps.pheezee.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.RangeColumn;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
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
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.activities.PatientsView;
import com.startoonlabs.apps.pheezee.activities.SessionReportActivity;
import com.startoonlabs.apps.pheezee.utils.PatientOperations;
import com.startoonlabs.apps.pheezee.utils.TimeOperations;
import com.startoonlabs.apps.pheezee.views.custom_graph.ApiData;
import com.startoonlabs.apps.pheezee.views.custom_graph.EmonjiBarGraph;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class ReportWeek extends Fragment {

    //rom graph
    AnyChartView anyChartView_rom;
    Set set;
    Cartesian cartesian;

    //custom chart
    private String[] days = {"sun","Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    EmonjiBarGraph rom,emg;
    JSONObject object = new JSONObject();

    //Bar chart
    BarChart barChart;
    BarDataSet barChartDataSet;
    BarData barChartData;
    ArrayList<BarEntry> dataPoints;
    BarGraphSeries<DataPoint> series;
    DataPoint[] datapoints;

    ImageView iv_left, iv_right, iv_left_joint, iv_right_joint;
    TextView tv_report_week, tv_overall, tv_individual, tv_total_hours, tv_total_reps_report, tv_total_holdtime,tv_body_part,tv_back;
    int currentWeek=0,current_bodypart=0;
    String start_date, end_date;

    boolean mTypeSelected = false;   //true for over all and false for individual

    JSONArray session_array;
    ArrayList<String> str_part;
    Iterator iterator;


    public ReportWeek() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_report_week, container, false);


        //custom bar gaph android
        rom = view.findViewById(R.id.graphView);

        rom.setBarNames(days);
        rom.setUpperLineColor(ContextCompat.getColor(getActivity(),R.color.home_orange));
        rom.setLowerLineColor(ContextCompat.getColor(getActivity(),R.color.home_orange));

        emg = view.findViewById(R.id.graphView_emg);
        emg.setBarNames(days);
        emg.setUpperLineColor(ContextCompat.getColor(getActivity(),R.color.home_orange));
        emg.setLowerLineColor(ContextCompat.getColor(getActivity(),R.color.home_orange));

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

        tv_report_week = view.findViewById(R.id.tv_report_date);
//        tv_overall = view.findViewById(R.id.tv_overall);
//        tv_individual = view.findViewById(R.id.tv_individual);
//        tv_total_hours = view.findViewById(R.id.tv_total_hours_session);
//        tv_total_reps_report = view.findViewById(R.id.total_reps_report);
//        tv_total_holdtime = view.findViewById(R.id.report_week_tv_hold_time);
        tv_body_part = view.findViewById(R.id.tv_individual_joint_name);
        tv_back = view.findViewById(R.id.tv_back_report);

        setInitialweek();


        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentWeek--;
                current_bodypart=0;
                getWeek(currentWeek);
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
                updateLines();
            }
        });

        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentWeek++;
                current_bodypart=0;
                getWeek(currentWeek);
                updateScreen(mTypeSelected);
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
                updateLines();
            }
        });



//        tv_overall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mTypeSelected = true;
//                changeViewOverallAndIndividual();
//                tv_overall.setTypeface(null, Typeface.BOLD);
//                tv_overall.setAlpha(1);
//                makeIndividualInvisible();
//                updateScreen(mTypeSelected);
//            }
//        });

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
                updateScreen(mTypeSelected);
                updateLines();
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
                updateLines();
            }
        });

//        tv_individual.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mTypeSelected = false;
//                changeViewOverallAndIndividual();
//                tv_individual.setTypeface(null, Typeface.BOLD);
//                tv_individual.setAlpha(1);
//                makeIndividualVisible();
//                str_part = new ArrayList<>();
//                HashSet<String> set_part = fetchAllParts();
//                str_part = new ArrayList<>();
//                iterator = set_part.iterator();
//                while (iterator.hasNext()){
//                    str_part.add(iterator.next()+"");
//                }
//                Log.i("array",str_part.toString());
//                if(str_part.size()>0) {
//                    makeIndividualVisible();
//                    tv_body_part.setText(str_part.get(current_bodypart));
//                }
//                else {
//                    makeIndividualInvisible();
//                    Toast.makeText(getActivity(), "No Exercises done", Toast.LENGTH_SHORT).show();
//                }
//                updateScreen(mTypeSelected);
//
//            }
//        });

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PatientsView.class));
                getActivity().finish();
            }
        });

        return view;
    }

    private void setInitialweek() {
        getWeek(currentWeek);
        JSONArray array = getCurrentWeekJson(false);
        mTypeSelected = false;
                //changeViewOverallAndIndividual();
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
                updateScreen(mTypeSelected);
                updateLines();
//        updateTotalTime();
//        updateHoldTime(array);
//        updateTotalReps(array);
        updateGraphs();

    }


    public void updateLines(){
        if(str_part.size()>0) {
            object = PatientOperations.checkReferenceWithoutOrientationDone(getActivity(), SessionReportActivity.patientId, str_part.get(current_bodypart));

            if (object != null) Log.i("object123", object.toString());
            if (object != null) {
                try {
                    int maxangle=180,minangle=1,maxemg=180;
                    if(object.has("maxangle") && !object.getString("maxangle").equalsIgnoreCase(""))
                        maxangle = Integer.parseInt(object.getString("maxangle"));
                    if(object.has("minangle") && !object.getString("minangle").equalsIgnoreCase(""))
                        minangle = Integer.parseInt(object.getString("minangle"));
                    if(object.has("maxemg") && !object.getString("maxemg").equalsIgnoreCase(""))
                        maxemg = Integer.parseInt(object.getString("maxemg"));
                    rom.setUpperLine((short) maxangle);
                    rom.setLowerLine((short) minangle);
                    emg.setUpperLine((short) maxemg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i("inside123", "null");
                rom.setUpperLine((short) 179);
                rom.setLowerLine((short) 1);
                emg.setUpperLine((short) 179);
            }
        }
        else {
            Log.i("inside123", "null");
            rom.setUpperLine((short) 179);
            rom.setLowerLine((short) 1);
            emg.setUpperLine((short) 179);
        }
    }

    public void getWeek(int weekFromToday) {
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
        System.out.println(show_date);

        //gestureEvent.setText(reportDate);
        mCalendar.add(Calendar.DAY_OF_MONTH, 6);
        String date = format_date.format(mCalendar.getTime());
        String month =  month_name.format(mCalendar.getTime());
        String year = format_year.format(mCalendar.getTime());
        String show_date2 = date+" "+month.substring(0,3)+" "+year;
        end_date = ymd.format(mCalendar.getTime());
        System.out.println(start_date + "-" + end_date);
        tv_report_week.setText(show_date + "-" + show_date2);
    }


    public void changeViewOverallAndIndividual(){
        tv_overall.setTypeface(null, Typeface.NORMAL);
        tv_individual.setTypeface(null, Typeface.NORMAL);
        tv_overall.setAlpha(0.5f);
        tv_individual.setAlpha(0.5f);
    }

    public void makeIndividualInvisible(){
        tv_body_part.setVisibility(View.GONE);
        iv_left_joint.setVisibility(View.GONE);
        iv_right_joint.setVisibility(View.GONE);
    }


    public void makeIndividualVisible(){
        tv_body_part.setVisibility(View.VISIBLE);
        iv_left_joint.setVisibility(View.VISIBLE);
        iv_right_joint.setVisibility(View.VISIBLE);
    }


    public JSONArray getCurrentWeekAverageJson(boolean mTypeSelected){
        JSONArray array = new JSONArray();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dt = start_date;
            for (int i=0;i<7;i++){
                  // Start date
                JSONObject object = getCurrentDayAverageValues(mTypeSelected,dt);
                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(sdf.parse(dt));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c.add(Calendar.DATE, 1);  // number of days to add
                dt = sdf.format(c.getTime());

                Log.i("date",dt.toString());
                if(object!=null)
                    array.put(object);
            }
        return array;
    }

    private JSONObject getCurrentDayAverageValues(boolean mTypeSelected, String dt) {
        JSONObject object = new JSONObject();
        int maxEmg=0,maxAngle=0,minAngle=0,x=0;
        if(mTypeSelected) {
            for (int i = 0; i < session_array.length(); i++) {
                try {
                    JSONObject object1 = session_array.getJSONObject(i);
                    if (dt.equals(object1.getString("heldon").substring(0, 10).trim())) {
                        x++;
                        maxAngle += Integer.parseInt(object1.getString("maxangle"));
                        minAngle += Integer.parseInt(object1.getString("minangle"));
                        maxEmg += Integer.parseInt(object1.getString("maxemg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            for (int i = 0; i < session_array.length(); i++) {
                try {
                    JSONObject object1 = session_array.getJSONObject(i);
                    if (dt.equals(object1.getString("heldon").substring(0, 10).trim())&&object1.getString("bodypart").equals(tv_body_part.getText().toString())) {
                        x++;
                        maxAngle += Integer.parseInt(object1.getString("maxangle"));
                        minAngle += Integer.parseInt(object1.getString("minangle"));
                        maxEmg += Integer.parseInt(object1.getString("maxemg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if(x>0) {
            maxAngle = maxAngle / x;
            minAngle = minAngle / x;
            maxEmg = maxEmg / x;
        }

        try {
            object.put("maxemg",maxEmg);
            object.put("maxangle",maxAngle);
            object.put("minangle",minAngle);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
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
        JSONArray array = getCurrentWeekJson(true);
        TimeOperations timeOperations = new TimeOperations();
        String str_time = timeOperations.addTotalTime(array);
        tv_total_hours.setText(str_time);
    }

    private void updateGraphs() {
        JSONArray array = getCurrentWeekAverageJson(mTypeSelected);
        Log.i("Average",array.toString());
        updateEmgGraph(array);
        updateRomGraph(array);
    }



    private void updateRomGraph(JSONArray array) {
        List<DataEntry> data = new ArrayList<>();
        Log.i("Length",array.length()+"");
//        if(array.length()==0){
//            data.add(new CustomDataEntry(0,0,0));
//        }
//        else {
//            for (int i = 0; i < array.length(); i++) {
//                try {
//                    int x = i + 1;
//                    JSONObject object = array.getJSONObject(i);
//                    int maxAngle = Integer.parseInt(object.getString("maxangle"));
//                    int minAngle = Integer.parseInt(object.getString("minangle"));
//                    data.add(new CustomDataEntry(x, maxAngle, minAngle));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        set.data(data);
        ApiData[] data1 = new ApiData[array.length()];
        for (int i = 0; i < array.length(); i++) {
                try {
                    int x = i + 1;
                    JSONObject object = array.getJSONObject(i);
                    int maxAngle = Integer.parseInt(object.getString("maxangle"));
                    int minAngle = Integer.parseInt(object.getString("minangle"));
                    data1[i] = new ApiData(i,minAngle,maxAngle);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
        rom.setBarData(data1);  
    }

    private void updateEmgGraph(JSONArray array) {
//        dataPoints.clear();
//        barChart.invalidate();
//        barChart.notifyDataSetChanged();
//        barChart.clear();
//        series = new BarGraphSeries<>(datapoints);
//
//        barChartDataSet=new BarDataSet(dataPoints, "Sessions Vs EMG Graph");
//        Log.i("emg data set",array.length()+"");
//        int j=0;
//        for (int i=0;i<array.length();i++){
//            String maxemg = null;
//            try {
//                maxemg = array.getJSONObject(i).get("maxemg").toString();
//                barChartData.addEntry(new BarEntry(++j,Integer.parseInt(maxemg)),0);
//                barChart.moveViewToX(j);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            //maxemg = maxemg.substring(0,maxemg.length());
//
//
//        }
//        barChart.setData(barChartData);
//        barChart.notifyDataSetChanged();
//        barChart.invalidate();
//
//        setEmgChartCharacteristics();
//        XAxis xAxis = barChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setAxisMinimum(0f);
//        xAxis.setAxisMaximum(array.length()+1);
//        xAxis.setGranularity(1f);


        Log.i("array",array.toString());
        ApiData data2[] = null;
        try {
            data2 = new ApiData[array.length()];
            Log.i("Response",array.length()+"");
            for (int i = 0; i < array.length(); i++) {

                int x = i + 1;
                JSONObject object = array.getJSONObject(i);
                Log.i("Response",object.toString());
//                    String weekday = arr.getString("week");
                int maxEmg = Integer.parseInt(object.getString("maxemg"));
                data2[i] = new ApiData(i,-1,maxEmg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        emg.setBarData(data2);

    }
    public void updateScreen(boolean mTypeSelected){
        JSONArray array = getCurrentWeekJson(mTypeSelected);
//        updateTotalTime();
//        updateHoldTime(array);
//        updateTotalReps(array);
        updateGraphs();
    }

    private HashSet<String> fetchAllParts() {
        HashSet<String> hashSet = new HashSet<>();
        JSONArray array = getCurrentWeekJson(true);
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

    private JSONArray getCurrentWeekJson(boolean b) {
        JSONArray array= new JSONArray();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        Log.i("current_date",start_date);
        Date initial_date = null,final_date = null;
        try {
            initial_date = simpleDateFormat.parse(start_date);
            final_date = simpleDateFormat.parse(end_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(b){
            for (int i=0;i<session_array.length();i++){
                JSONObject object = null;
                try {
                    object = session_array.getJSONObject(i);
                    String heldon = object.getString("heldon").substring(0,10).trim();
                    Date date1 = simpleDateFormat.parse(heldon);
                    if(date1.compareTo(initial_date)>=0 && date1.compareTo(final_date)<=0){
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
            for (int i=0;i<session_array.length();i++){
                JSONObject object = null;
                try {
                    object = session_array.getJSONObject(i);
                    String heldon = object.getString("heldon").substring(0,10).trim();
                    Date date1 = simpleDateFormat.parse(heldon);
                    if(date1.compareTo(initial_date)>=0 && date1.compareTo(final_date)<=0 && object.getString("bodypart").equals(tv_body_part.getText().toString())){
                        array.put(object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return array;
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
