package com.startoonlabs.apps.pheezee.services;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction())){
            System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiibr");
            Toast.makeText(context,
                    "BTStateChangedBroadcastReceiver: STATE_CONNECTED",
                    Toast.LENGTH_SHORT).show();


        }
        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())){
            System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
            Toast.makeText(context,
                    "BTStateChangedBroadcastReceiver: STATE_DISCONNECTED",
                    Toast.LENGTH_SHORT).show();

        }
    }
}
