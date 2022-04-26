package com.start.apps.pheezee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import start.apps.pheezee.R;

public class DeleteAccountOne extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account_one);

        RadioButton button1 = findViewById(R.id.radioButton);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str2 = button1.getText().toString();
                Intent intent = new Intent(getApplicationContext(), DeleteAccountTwo.class);
                intent.putExtra("feedback", str2);
                startActivity(intent);
            }

        });

        RadioButton button2 = findViewById(R.id.radioButton2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String str2 = button2.getText().toString();
                Intent intent = new Intent(getApplicationContext(), DeleteAccountTwo.class);
                intent.putExtra("feedback", str2);
                startActivity(intent);
            }

        });

        RadioButton button3 = findViewById(R.id.radioButton3);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str2 = button3.getText().toString();
                Intent intent = new Intent(getApplicationContext(), DeleteAccountTwo.class);
                intent.putExtra("feedback", str2);
                startActivity(intent);
            }

        });

        RadioButton button4 = findViewById(R.id.radioButton4);

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str2 = button4.getText().toString();
                Intent intent = new Intent(getApplicationContext(), DeleteAccountTwo.class);
                intent.putExtra("feedback", str2);
                startActivity(intent);
            }

        });
        ImageView button = findViewById(R.id.iv_back_phizio_profile);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }

        });


    }
}