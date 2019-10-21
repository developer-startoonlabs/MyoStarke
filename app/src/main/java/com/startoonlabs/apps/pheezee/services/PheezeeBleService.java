package com.startoonlabs.apps.pheezee.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.activities.PatientsView;
import com.startoonlabs.apps.pheezee.classes.DeviceListClass;
import com.startoonlabs.apps.pheezee.utils.ByteToArrayOperations;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.startoonlabs.apps.pheezee.App.CHANNEL_ID;

public class PheezeeBleService extends Service {
    //Intent Actions
    public static String device_state = "device.state";
    public static String bluetooth_state = "ble.state";
    public static String battery_percent = "battery.percent";
    public static String usb_state = "usb.state";
    public static String firmware_version = "firmware.version";








    //Service UUIDS
    public static final UUID generic_service_uuid = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    public static final UUID custom_service_uuid = UUID.fromString("909a1400-9693-4920-96e6-893c0157fedd");
    public static final UUID battery_service_uuid = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID device_info_service_uuid = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");




    //Characteristic
    public static final UUID device_name_characteristic_uuid = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");
    public static final UUID custom_characteristic_uuid = UUID.fromString("909a1401-9693-4920-96e6-893c0157fedd");
    public static final UUID battery_level_characteristic_uuid = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    public static final UUID firmware_version_characteristic_uuid = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    public static final UUID serial_number_characteristic_uuid = UUID.fromString("00002a25-0000-1000-8000-00805f9b34fb");
    public static final UUID manufacturer_name_characteristic_uuid = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");
    public static final UUID hardware_version_characteristic_uuid = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");

    //descriptor
    public static final UUID universal_descriptor = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private Boolean mDeviceState = false, mBluetoothState = false, mUsbState = false;
    private int mBatteryPercent = 0;
    private String mFirmwareVersion = "";
    private static final int REQUEST_ENABLE_BT = 1;
    private boolean mScanning = false;
    public String deviceMacc = "D5:25:E7:4C:05:FC";
    ArrayList<DeviceListClass> mScanResults;
    private BtleScanCallback mScanCallback;
    BluetoothLeScanner mBluetoothLeScanner;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice remoteDevice;
    BluetoothGatt bluetoothGatt;
    boolean isDeviceConnected = false;
    BluetoothGattCharacteristic mCustomCharacteristic, mBatteryCharacteristic, mFirmwareVersionCharacteristic,
            mSerialIdCharacteristic, mManufacturerNameCharacteristic, mHardwareVersionCharacteristic, mDeviceNameCharacteristic;

    BluetoothGattDescriptor mBatteryDescriptor, mDfuDescriptor, mCustomCharacteristicDescriptor;
    public PheezeeBleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String str = intent.getStringExtra("inputExtra");
        showNotification("Device not connected");
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if(bluetoothAdapter!=null && bluetoothAdapter.isEnabled()){
            bluetoothStateBroadcast(true);
            startScanInBackground(true);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothProfile.EXTRA_STATE);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(bluetoothReceiver, filter);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScan();
//        disconnectDevice();
        unregisterReceiver(bluetoothReceiver);
    }

    public void startScanInBackground(final boolean devicePresent){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                startScan(devicePresent);
            }
        });
    }

    public void stopScaninBackground(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        });
    }

    public void showNotification(String deviceState){
        Intent notificationintent = new Intent(this, PatientsView.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, notificationintent,0);
        Notification builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Pheezee")
                .setContentText(deviceState)
                .setSmallIcon(R.mipmap.pheezee_logos_final_square)
//                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,builder);
    }


    private void startScan(boolean localDevicePresent) {
        List<ScanFilter> filters = new ArrayList<>();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();


        mScanResults = new ArrayList<>();
        mScanCallback = new BtleScanCallback(mScanResults,localDevicePresent);
        mBluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
        mScanning = true;
    }

    public void deviceStateBroadcast(boolean isDeviceConnected){
        Intent i = new Intent(device_state);
        i.putExtra(device_state,isDeviceConnected);
        sendBroadcast(i);
    }

    public void bluetoothStateBroadcast(boolean isBluetoothEnabled){
        Intent i = new Intent(bluetooth_state);
        i.putExtra(bluetooth_state,isBluetoothEnabled);
        sendBroadcast(i);
    }

    public void sendBatteryLevelBroadCast(String percentage){
        Intent i = new Intent(battery_percent);
        i.putExtra(battery_percent,percentage);
        sendBroadcast(i);
    }

    public void sendUsbStateBroadcast(boolean usbstate){
        Intent i = new Intent(usb_state);
        i.putExtra(usb_state,usbstate);
        sendBroadcast(i);
    }

    public void sendFirmwareVersion(String firmwareVersion){
        Intent i = new Intent(firmware_version);
        i.putExtra(firmware_version,firmwareVersion);
        sendBroadcast(i);
    }


    public void stopScan(){
        if (mScanning && bluetoothAdapter != null && bluetoothAdapter.isEnabled() && mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mScanCallback);
        }
        mScanCallback = null;
        mScanning = false;
    }

    public void connectDevice(String deviceMacc){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(deviceMacc);
        this.remoteDevice = remoteDevice;
        bluetoothGatt = remoteDevice.connectGatt(this, false, callback);
    }

    public void disconnectDevice() {
        if(bluetoothGatt==null){
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    class BtleScanCallback extends ScanCallback {
        private ArrayList<DeviceListClass> mScanResults;
        boolean localDevicePresent;

        BtleScanCallback(ArrayList<DeviceListClass> mScanResults, boolean localDeviePresent) {
            this.mScanResults = mScanResults;
            this.localDevicePresent = localDeviePresent;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if(localDevicePresent && !isDeviceConnected){
                BluetoothDevice device = result.getDevice();
                String deviceAddress = device.getAddress();
                String deviceName = device.getName();
                if(deviceAddress.equalsIgnoreCase(deviceMacc)){
                    connectDevice(deviceMacc);
                }
            }else {
                addScanResult(result);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                if(localDevicePresent && !isDeviceConnected){
                    BluetoothDevice device = result.getDevice();
                    String deviceAddress = device.getAddress();
                    String deviceName = device.getName();
                    if(deviceAddress.equalsIgnoreCase(deviceMacc)){
                        connectDevice(deviceMacc);
                    }
                }else {
                    addScanResult(result);
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            //Log.e(TAG, "BLE Scan Failed with code " + errorCode);
        }

        private void addScanResult(ScanResult result) {

            String setDeviceBondState;

            BluetoothDevice device = result.getDevice();
            String deviceAddress = device.getAddress();
            String deviceName = device.getName();

            if(deviceName==null)
                deviceName = "UNKNOWN DEVICE";
            int deviceRssi = result.getRssi();
            int deviceBondState = device.getBondState();

            //Just to update the bondstate if needed to
            if(deviceBondState == 0)
                setDeviceBondState = "BONDED";
            else
                setDeviceBondState = "NOT BONDED";

            //

            boolean flag = false;
            for (int i = 0; i < mScanResults.size(); i++) {
                if (mScanResults.get(i).getDeviceMacAddress().equals(deviceAddress)) {
                    if(!Objects.equals(mScanResults.get(i).getDeviceBondState(), setDeviceBondState)){
                        mScanResults.get(i).setDeviceBondState(setDeviceBondState);
                    }

                    if (Integer.parseInt(mScanResults.get(i).getDeviceRssi())!= deviceRssi){
                        mScanResults.get(i).setDeviceRssi(""+deviceRssi);
                    }
                    flag = true;
                }
            }



            if (!flag) {
                Log.i("device name",deviceName);
                String str = "pheezee";
                if(deviceName.toLowerCase().contains(str)) {
                    DeviceListClass deviceListClass = new DeviceListClass();
                    deviceListClass.setDeviceName(deviceName);
                    deviceListClass.setDeviceMacAddress(deviceAddress);
                    deviceListClass.setDeviceRssi("" + deviceRssi);
                    if (deviceBondState == 0)
                        deviceListClass.setDeviceBondState("BONDED");
                    else
                        deviceListClass.setDeviceBondState("NOT BONDED");

                    if(deviceAddress.equalsIgnoreCase(deviceMacc)){
                        connectDevice(deviceAddress);
                    }
                }
            }
        }
    }




    public BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    isDeviceConnected = true;
                    refreshDeviceCache(gatt);
                    gatt.discoverServices();
                    deviceStateBroadcast(isDeviceConnected);
                    showNotification("Device Connected");
                    stopScaninBackground();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Message msg = Message.obtain();
                    isDeviceConnected = false;
                }
            }
            if(status == BluetoothGatt.GATT_FAILURE){
                Message msg = Message.obtain();
                msg.obj = "N/C";
                showNotification("Device Not Connected");
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) throws NullPointerException {
//            mDeviceNameCharacteristic = gatt.getService(generic_service_uuid).getCharacteristic(device_name_characteristic_uuid);
            mCustomCharacteristic = gatt.getService(custom_service_uuid).getCharacteristic(custom_characteristic_uuid);
            mBatteryCharacteristic = gatt.getService(battery_service_uuid).getCharacteristic(battery_level_characteristic_uuid);
            mFirmwareVersionCharacteristic = gatt.getService(device_info_service_uuid).getCharacteristic(firmware_version_characteristic_uuid);
            mManufacturerNameCharacteristic = gatt.getService(device_info_service_uuid).getCharacteristic(manufacturer_name_characteristic_uuid);
            mHardwareVersionCharacteristic = gatt.getService(device_info_service_uuid).getCharacteristic(hardware_version_characteristic_uuid);
            mSerialIdCharacteristic = gatt.getService(device_info_service_uuid).getCharacteristic(serial_number_characteristic_uuid);

            //Descriptors
            mCustomCharacteristicDescriptor = mCustomCharacteristic.getDescriptor(universal_descriptor);
            mBatteryDescriptor = mBatteryCharacteristic.getDescriptor(universal_descriptor);
            writeCharacteristic(mCustomCharacteristic,"AA02");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            bluetoothGatt.readCharacteristic(mFirmwareVersionCharacteristic);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if(characteristic.getUuid().equals(battery_level_characteristic_uuid)){
                byte b[] = characteristic.getValue();
                int battery  = b[0];
                int usb_state = b[1];
                if(usb_state==1) {
                    sendUsbStateBroadcast(true);
                    Log.i("usb",String.valueOf(usb_state));
                }
                else if(usb_state==0) {
                    sendUsbStateBroadcast(false);
                    Log.i("usb",String.valueOf(usb_state));
                }
                sendBatteryLevelBroadCast(String.valueOf(battery));
            }else if(characteristic.getUuid().equals(custom_characteristic_uuid)){

            }
        }
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if(characteristic.getUuid().equals(battery_level_characteristic_uuid)){
                        byte info_packet[] = characteristic.getValue();
                        int battery = info_packet[11] & 0xFF;
                        int device_status = info_packet[12] & 0xFF;
                        int device_usb_state = info_packet[13] & 0xFF;
                        if(device_usb_state==1) {
                            mUsbState = true;
                            sendUsbStateBroadcast(true);
                            Log.i("usb",String.valueOf(usb_state));

                        }
                        else if(device_status==0) {
                            mUsbState = false;
                            sendUsbStateBroadcast(false);
                            Log.i("usb",String.valueOf(usb_state));
                        }
                        sendBatteryLevelBroadCast(String.valueOf(battery));
                bluetoothGatt.readCharacteristic(mCustomCharacteristic);

            }else if (characteristic.getUuid().equals(custom_characteristic_uuid)){
                        byte b[] = characteristic.getValue();
                        int battery  = b[0];
                        int usb_state = b[1];
                        if(usb_state==1) {
                            mUsbState = true;
                            sendUsbStateBroadcast(true);
                        }
                        else if(usb_state==0) {
                            mUsbState = false;
                            sendUsbStateBroadcast(false);
                            Log.i("usb",String.valueOf(usb_state));
                        }
                        mBatteryPercent = battery;
                        sendBatteryLevelBroadCast(String.valueOf(battery));
                gatt.setCharacteristicNotification(mBatteryCharacteristic, true);
                mBatteryDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(mBatteryDescriptor);

            }else if(characteristic.getUuid().equals(firmware_version_characteristic_uuid)){
                byte b[] = characteristic.getValue();
                String str = new String(b, StandardCharsets.UTF_8);
                mFirmwareVersion = str;
                sendFirmwareVersion(str);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i("characteristic","written");
            bluetoothGatt.readCharacteristic(mCustomCharacteristic);
        }
    };

    private boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, String value) {
        byte[] b = ByteToArrayOperations.hexStringToByteArray("AA02");
        if (bluetoothGatt == null ) {
            return false;
        }
        if (characteristic == null) {
            return false;
        }
        characteristic.setValue(value);
        return bluetoothGatt.writeCharacteristic(characteristic);
    }


    private boolean refreshDeviceCache(BluetoothGatt gatt){
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                return (Boolean) localMethod.invoke(localBluetoothGatt, new Object[0]);
            }
        }
        catch (Exception localException) {
        }
        return false;
    }



    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
                isDeviceConnected = false;
                showNotification("Device Not Connected");
                deviceStateBroadcast(false);
                startScanInBackground(true);
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            startScanInBackground(true);
                            bluetoothStateBroadcast(true);
                        }
                    });
                }
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            bluetoothStateBroadcast(false);
                            deviceStateBroadcast(false);
                        }
                    });
                }
            }

        }
    };
}
