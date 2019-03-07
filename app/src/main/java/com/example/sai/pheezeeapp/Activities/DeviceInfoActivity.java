package com.example.sai.pheezeeapp.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.Classes.BluetoothSingelton;
import com.example.sai.pheezeeapp.R;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

public class DeviceInfoActivity extends AppCompatActivity {

    //Bluetooth related declarations
    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothDevice remoteDevice;
    BluetoothAdapter bluetoothAdapter;
    BluetoothGattDescriptor mBluetoothGattDescriptor;
    BluetoothGattCharacteristic mCharacteristic,firmware_characteristic,serial_characteristic,devicename_characteristic;
    BluetoothGatt bluetoothGatt;



    //Declaring all the view items
    TextView tv_device_name,tv_device_mamc, tv_firmware_version, tv_serial_id, tv_battery_level,tv_connection_status;
    ImageView iv_back_device_info;

    ArrayList<BluetoothGattCharacteristic> arrayList;

    //Service uuid declearation00002A25-0000-1000-8000-00805f9b34fb
    public static final UUID battery_service1_uuid = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID device_info_service1_uuid = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public static final UUID descriptor_characteristic1_service1_uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    //Characteristic uuid declaration
    public static final UUID battery_level_battery_service_characteristic_uuid = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    public static final UUID firmware_version_characteristic_uuid = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    public static final UUID serial_number_characteristic = UUID.fromString("00002a25-0000-1000-8000-00805f9b34fb");
    public static final UUID device_name_characteristic = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);


        tv_device_name = findViewById(R.id.tv_deviceinfo_device_name);
        tv_device_mamc = findViewById(R.id.tv_deviceinfo_device_mac);
        tv_battery_level = findViewById(R.id.tv_deviceinfo_device_battery);
        tv_connection_status = findViewById(R.id.tv_deviceinfo_device_connection_status);
        tv_serial_id = findViewById(R.id.tv_deviceinfo_device_serial);
        tv_firmware_version = findViewById(R.id.tv_deviceinfo_device_firmware);
        iv_back_device_info = findViewById(R.id.iv_back_device_info);

        iv_back_device_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tv_connection_status.setText("N/C");
        arrayList = new ArrayList<>();




        //checking bluetooth switched on and connecting gatt functions
        bluetoothAdapter = BluetoothSingelton.getmInstance().getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(DeviceInfoActivity.this, "Bluetooth Disabled", Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        //bluetoothGatt = BluetoothGattSingleton.getmInstance().getAdapter();
        if(bluetoothGatt!=null){
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            Toast.makeText(DeviceInfoActivity.this, "GATT CLOSED", Toast.LENGTH_SHORT).show();
        }

        if(!getIntent().getStringExtra("deviceMacAddress").equals(""))
            remoteDevice = bluetoothAdapter.getRemoteDevice(getIntent().getStringExtra("deviceMacAddress"));
        //Log.i("Remote Device",remoteDevice.getName());
        if(remoteDevice!=null){
            tv_device_mamc.setText(remoteDevice.getAddress());
            tv_device_name.setText(remoteDevice.getName());
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(remoteDevice!=null) {
                    bluetoothGatt = remoteDevice.connectGatt(DeviceInfoActivity.this, true, callback);
                }
            }
        });

    }


    public BluetoothGattCallback callback = new BluetoothGattCallback() {



        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(newState == BluetoothProfile.STATE_CONNECTED){
                arrayList = new ArrayList<>();
                Log.i("GATT CONNECTED", "Attempting to start the service discovery");
                Message message = new Message();
                message.obj = "Connected";
                bleStatusHandler.sendMessage(message);
                gatt.discoverServices();
            }
            else if (newState == BluetoothProfile.STATE_CONNECTING){
                Log.i("GATT DISCONNECTED", "GATT server is being connected");
            }

            else if(newState == BluetoothProfile.STATE_DISCONNECTING){
                Log.i("GATT DISCONNECTING","Gatt server disconnecting");
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                Log.i("GATT DISCONNECTED", "Gatt server disconnected");
            }

            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status==BluetoothGatt.GATT_SUCCESS){
                mCharacteristic = gatt.getService(battery_service1_uuid).getCharacteristic(battery_level_battery_service_characteristic_uuid);
                Log.i("Check",mCharacteristic.getUuid().toString());
                firmware_characteristic = gatt.getService(device_info_service1_uuid).getCharacteristic(firmware_version_characteristic_uuid);
                Log.i("Check",firmware_characteristic.getUuid().toString());
                serial_characteristic = gatt.getService(device_info_service1_uuid).getCharacteristic(serial_number_characteristic);
                Log.i("Check",serial_characteristic.getUuid().toString());
                devicename_characteristic = gatt.getService(device_info_service1_uuid).getCharacteristic(device_name_characteristic);
                gatt.setCharacteristicNotification(mCharacteristic,true);



                arrayList.add(firmware_characteristic);
                arrayList.add(serial_characteristic);
                arrayList.add(devicename_characteristic);
                arrayList.add(mCharacteristic);


                Message message = new Message();
                message.obj = "setvalues";
                bleStatusHandler.sendMessage(message);



            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, final int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            byte b[] = characteristic.getValue();
            final String str;
            str = new String(b, StandardCharsets.UTF_8);

            if(characteristic.getUuid().equals(firmware_version_characteristic_uuid)){
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        tv_firmware_version.setText(str);
                        // Stuff that updates the UI

                    }
                });
            }
            else if(characteristic.getUuid().equals(serial_number_characteristic)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_serial_id.setText(str);
                        // Stuff that updates the UI

                    }
                });
            }
            else if(characteristic.getUuid().equals(device_name_characteristic)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_device_name.setText(str);
                        // Stuff that updates the UI
                    }
                });
            }
            else if(characteristic.getUuid().equals(battery_level_battery_service_characteristic_uuid)){
                final int battery  = b[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tv_battery_level.setText(String.valueOf(battery).concat("%"));
                        mBluetoothGattDescriptor = mCharacteristic.getDescriptor(descriptor_characteristic1_service1_uuid);
                        mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        bluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);
                    }
                });
            }

            arrayList.remove(0);
            if(arrayList.size()>0){
                bluetoothGatt.readCharacteristic(arrayList.get(0));
            }


        }
        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            if(characteristic.getUuid().equals(battery_level_battery_service_characteristic_uuid)){
                byte b[] = characteristic.getValue();
                final int battery  = b[0];
                Log.i("battery",battery+"");
                Message message = new Message();
                message.obj = battery+"";
                batteryStatus.sendMessage(message);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.i("hello","READ");
        }



        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.i("descriptor","written");
        }
    };

    @SuppressLint("HandlerLeak")
    public final Handler bleStatusHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.obj.equals("setvalues")){
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if(bluetoothGatt!=null)
                            bluetoothGatt.readCharacteristic(arrayList.get(0));
                    }
                });
            }
            else {
                tv_connection_status.setText((String) msg.obj);
            }
        }
    };


    @SuppressLint("HandlerLeak")
    public final Handler batteryStatus = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_battery_level.setText(msg.obj.toString().concat("%"));
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        String conn_status = tv_connection_status.getText().toString();
        if(bluetoothGatt!=null && conn_status.equals("Connected")) {
            bluetoothGatt.setCharacteristicNotification(mCharacteristic, false);
            mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);
        }
    }
}
