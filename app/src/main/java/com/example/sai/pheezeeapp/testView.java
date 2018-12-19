package com.example.sai.pheezeeapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.goodiebag.protractorview.ProtractorView;

import java.util.ArrayList;

public class testView extends AppCompatActivity {

    ProtractorView protractorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view);
        protractorView = findViewById(R.id.emgProtractor);
        protractorView.setRotationY(180);
        protractorView.setTextDirection(View.TEXT_DIRECTION_LTR);
        //protractorView.setTouchInside(false);
        protractorView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        //ArrayList<Integer> colors = new ArrayList<>();
        //colors.add(Color.parseColor(""));
        PieChart pieChart = findViewById(R.id.pieChart);
        pieChart.setTouchEnabled(false);pieChart.needsHighlight(0);
        pieChart.setRotationEnabled(false);
        pieChart.setMaxAngle(180f);
        pieChart.setRotationAngle(180f);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(false);
//        pieChart.getLayoutParams().height = chartSize / 2;
//        pieChart.getLayoutParams().width = chartSize;
        ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();
        pieEntries.add(new PieEntry(10));
        pieEntries.add(new PieEntry(20));
        pieChart.setCenterText("10");
        pieChart.setCenterTextSize(48);
        PieDataSet dataSet =new PieDataSet(pieEntries, "Range of motion");
        dataSet.setValueTextColor(0);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setData(new PieData(dataSet));
        pieChart.invalidate();
    }
}
