package com.example.sai.pheezeeapp.classes;

import android.bluetooth.BluetoothGatt;

public class BluetoothGattSingleton {
    BluetoothGatt bluetoothGatt;
    private static BluetoothGattSingleton mInstance = new BluetoothGattSingleton();

    private BluetoothGattSingleton(){}

    public static BluetoothGattSingleton getmInstance(){
        return mInstance;
    }

    public void setAdapter(BluetoothGatt adapter){
        this.bluetoothGatt = adapter;
    }

    public BluetoothGatt getAdapter(){
        return this.bluetoothGatt;
    }
}
