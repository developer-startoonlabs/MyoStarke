package com.start.apps.pheezee.classes;

import android.bluetooth.BluetoothGatt;

/**
 * Bluetooth singleton class
 */
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
