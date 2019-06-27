package com.startoonlabs.apps.pheezee;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("APP","INSIDE THIS");
        startService(new Intent(this, MqttService.class));
    }
}
