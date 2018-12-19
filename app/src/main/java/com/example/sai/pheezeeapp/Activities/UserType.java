package com.example.sai.pheezeeapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.example.sai.pheezeeapp.R;


public class UserType extends AppCompatActivity {



    CardView iv_doctor, iv_patient;
    Intent i_navigation_usertype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);


        iv_doctor = (CardView) findViewById(R.id.iv_doctor);
        iv_patient = (CardView) findViewById(R.id.iv_patient);


        //If the user selects the user type as a doctor
        iv_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i_navigation_usertype = new Intent(UserType.this, SignUpActivity.class);
                startActivity(i_navigation_usertype);
            }
        });

    }
}
