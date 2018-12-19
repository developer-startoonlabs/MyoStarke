package com.example.sai.pheezeeapp;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class DemoActivity extends AppCompatActivity {

    private ImageView image1;
    private int[] imageArray;
    private int currentIndex;
    private int startIndex;
    private int endIndex;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        image1 = findViewById(R.id.imageView);
        imageArray = new int[8];
        imageArray[0] = R.drawable.patient_view_demo;
        imageArray[1] = R.drawable.monitoring_view_demo;
        imageArray[2] = R.drawable.monitoring_landscape_demo;
        imageArray[3] = R.drawable.report_view_demo;
        imageArray[4] = R.drawable.report_view_landscape_demo;
        imageArray[5] = R.drawable.session_analysis_demo;
//        imageArray[6] = R.drawable.seven;
//        imageArray[7] = R.drawable.eight;
        image1.setImageResource(imageArray[currentIndex++]);
        startIndex = 0;
        endIndex = 5;
        image1.setOnTouchListener(new OnSwipeTouchListener(DemoActivity.this) {

            public void onSwipeLeft() {
                image1.setImageResource(imageArray[currentIndex++]);
            }

            public void onSwipeBottom() {
                image1.setImageResource(imageArray[currentIndex--]);
            }
        });
    }


}
