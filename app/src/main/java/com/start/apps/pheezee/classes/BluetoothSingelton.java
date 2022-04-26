package com.start.apps.pheezee.classes;

import android.bluetooth.BluetoothAdapter;

public class BluetoothSingelton {
    BluetoothAdapter adapter;
    private static BluetoothSingelton mInstance = new BluetoothSingelton();

    private BluetoothSingelton(){}

    public static BluetoothSingelton getmInstance(){
        return mInstance;
    }

    public void setAdapter(BluetoothAdapter adapter){
        this.adapter = adapter;
    }

    public BluetoothAdapter getAdapter(){
        return this.adapter;
    }
}
