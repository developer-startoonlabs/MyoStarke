package com.startoonlabs.apps.pheezee.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.repository.MqttSyncRepository;

public class OnStartActivity extends AppCompatActivity {

    boolean isLoggedIn=false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MqttSyncRepository repository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_start);
        repository = new MqttSyncRepository(getApplication());
        //isLoggedIn = accessToken != null && !accessToken.isExpired();
        sharedPreferences =PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.getBoolean("version_2.14.5",false)){
//            editor = sharedPreferences.edit();
//            editor.clear();
//            editor.commit();
//            repository.clearDatabase();
//            editor.putBoolean("version_2.14.5",true);
//            editor.apply();
        }
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false);
        /**
         * checks if already logged in or not and calls the particular value bassed on that
         */
        Thread onstartThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    if(isLoggedIn)
                        startActivity(new Intent(OnStartActivity.this, PatientsView.class));
                    else
                        startActivity(new Intent(OnStartActivity.this,OnBoardingActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        onstartThread.start();
    }
}
