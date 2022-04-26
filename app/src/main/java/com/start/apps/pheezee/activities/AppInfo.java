package com.start.apps.pheezee.activities;

import static start.apps.pheezee.R.id.iv_back_app_info;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import start.apps.pheezee.R;
import com.start.apps.pheezee.Usermanual;

import start.apps.pheezee.R;

public class AppInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        ImageView button1 = findViewById(iv_back_app_info);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }
}

