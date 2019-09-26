package com.startoonlabs.apps.pheezee.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

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
        if(!sharedPreferences.getBoolean("mqtttohttp",false)){
            editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            repository.clearDatabase();
            editor.putBoolean("mqtttohttp",true);
            Log.i("here i am","here");
            editor.apply();
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
                        startActivity(new Intent(OnStartActivity.this,PatientsView.class));
                    else
                        startActivity(new Intent(OnStartActivity.this,LoginActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        onstartThread.start();
    }
}
