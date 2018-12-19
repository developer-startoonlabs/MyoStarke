package com.example.sai.pheezeeapp.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.Classes.BluetoothSingelton;
import com.example.sai.pheezeeapp.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

public class DeviceInfoActivity extends AppCompatActivity {

    //Bluetooth related declarations
    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothDevice remoteDevice;
    BluetoothAdapter bluetoothAdapter;
    //BluetoothManager mBluetoothManager;
    BluetoothGattDescriptor mBluetoothGattDescriptor;
    BluetoothGattCharacteristic mCharacteristic,firmware_characteristic,serial_characteristic;
    BluetoothGatt bluetoothGatt;



    //Declaring all the view items
    TextView tv_device_name,tv_device_mamc, tv_firmware_version, tv_serial_id, tv_battery_level,tv_connection_status;


    ArrayList<BluetoothGattCharacteristic> arrayList = new ArrayList<>();


    //Service uuid declearation00002A25-0000-1000-8000-00805f9b34fb
    public static final UUID battery_service1_uuid = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID device_info_service1_uuid = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public static final UUID descriptor_characteristic1_service1_uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    //Characteristic uuid declaration
    public static final UUID battery_level_battery_service_characteristic_uuid = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    public static final UUID firmware_version_characteristic_uuid = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    public static final UUID serial_number_characteristic = UUID.fromString("00002a25-0000-1000-8000-00805f9b34fb");
   // public static final UUID manufacture_number_characteristic = UUID.fromString("0x180A");
    // public static final UUID hardware_version_characteristic = UUID.fromString("0x180A");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);


        tv_device_name = (TextView)findViewById(R.id.tv_deviceinfo_device_name);
        tv_device_mamc = (TextView)findViewById(R.id.tv_deviceinfo_device_mac);
        tv_battery_level = (TextView)findViewById(R.id.tv_deviceinfo_device_battery);
        tv_connection_status = (TextView)findViewById(R.id.tv_deviceinfo_device_connection_status);
        tv_serial_id = (TextView)findViewById(R.id.tv_deviceinfo_device_serial);
        tv_firmware_version = (TextView)findViewById(R.id.tv_deviceinfo_device_firmware);





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
            Log.i("hello","hello");
            tv_device_mamc.setText(remoteDevice.getAddress());

            tv_device_name.setText(remoteDevice.getName());
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(remoteDevice!=null) {
                    bluetoothGatt = remoteDevice.connectGatt(DeviceInfoActivity.this, true, callback);
                    if (bluetoothGatt != null) {
                        Log.i("BLGATT", "not connected");
                        Log.i("Remote Device", remoteDevice.getName());
                    }
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
                gatt.setCharacteristicNotification(mCharacteristic,true);
                mBluetoothGattDescriptor = mCharacteristic.getDescriptor(descriptor_characteristic1_service1_uuid);
                mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                bluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);


                arrayList.add(firmware_characteristic);
                arrayList.add(serial_characteristic);

                Message message = new Message();
                message.obj = "setvalues";
                bleStatusHandler.sendMessage(message);



                /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothGatt.readCharacteristic(serial_characteristic);
                    }
                });*/



            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            byte b[] = characteristic.getValue();
            String str = null;
            try {
                str = new String(b, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            Log.i("characteristic",str);

            if(characteristic.getUuid().equals(firmware_version_characteristic_uuid)){
                final String string = str;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        tv_firmware_version.setText(string);
                        // Stuff that updates the UI

                    }
                });
            }
            else if(characteristic.getUuid().equals(serial_number_characteristic)){
                final String finalStr = str;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_serial_id.setText(finalStr);
                        // Stuff that updates the UI

                    }
                });
            }

            arrayList.remove(0);
            if(arrayList.size()>0){
                bluetoothGatt.readCharacteristic(arrayList.get(0));
            }


        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            if(battery_level_battery_service_characteristic_uuid.equals(characteristic.getUuid())){
                Log.i("Battery",characteristic.getValue().toString());
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

    public final Handler firmware_hadler = new Handler() {
        public void handleMessage(Message msg) {
            tv_firmware_version.setText(msg.obj.toString());
        }
    };

    public final Handler serial_hadler = new Handler() {
        public void handleMessage(Message msg) {
            tv_serial_id.setText(msg.obj.toString());
        }
    };



}
