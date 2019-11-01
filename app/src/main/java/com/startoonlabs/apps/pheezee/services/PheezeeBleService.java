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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelUuid;
import android.os.PatternMatcher;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.activities.PatientsView;
import com.startoonlabs.apps.pheezee.classes.DeviceListClass;
import com.startoonlabs.apps.pheezee.utils.ByteToArrayOperations;
import com.startoonlabs.apps.pheezee.utils.RegexOperations;
import com.startoonlabs.apps.pheezee.utils.ValueBasedColorOperations;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.startoonlabs.apps.pheezee.App.CHANNEL_ID;

public class PheezeeBleService extends Service {
    private long first_scan = 0;
    private int num_of_scan = 0;
    private boolean tooFrequentScan  =false;
    SharedPreferences preferences;
    private final String device_connected_notif = "Device Connected";
    private final String device_disconnected_notif = "Device not connected";
    private final String device_charging = "Device Connected, charging";
    private boolean first_packet = true;
    Message mMessage = new Message();

    boolean isConnectCommandGiven = false;
    //Intent Actions
    public static String device_state = "device.state";
    public static String bluetooth_state = "ble.state";
    public static String battery_percent = "battery.percent";
    public static String usb_state = "usb.state";
    public static String firmware_version = "firmware.version";
    public static String manufacturer_name = "manufacturer.name";
    public static String serial_id = "serial.id";
    public static String scanned_list = "scanned.list";
    public static String session_data = "session.data";
    public static String scan_state = "scan.state";
    public static String scan_too_frequent = "scan.too.frequent";








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


    //Binder
    IBinder myServiceBinder = new LocalBinder();

    //Characteristic read list
    ArrayList<BluetoothGattCharacteristic> mCharacteristicReadList;

    private Boolean mDeviceState = false, mBluetoothState = false, mUsbState = false, isPreviousDevicePresent = false;
    private int mBatteryPercent = 0;
    private String mFirmwareVersion = "", mSerialId = "", mManufacturerName = "";
    private boolean mScanning = false;
    public String deviceMacc = "";
    ArrayList<DeviceListClass> mScanResults;
    private BtleScanCallback mScanCallback;
    BluetoothLeScanner mBluetoothLeScanner;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice remoteDevice;
    BluetoothGatt bluetoothGatt;
    BluetoothGattCharacteristic mCustomCharacteristic, mBatteryCharacteristic, mFirmwareVersionCharacteristic,
            mSerialIdCharacteristic, mManufacturerNameCharacteristic, mHardwareVersionCharacteristic, mDeviceNameCharacteristic;

    BluetoothGattDescriptor mBatteryDescriptor, mDfuDescriptor, mCustomCharacteristicDescriptor;

    public static final int MESSENGER = 1;
    Messenger messageActivity;
    private String mCharacteristicWrittenValue = "";




    public PheezeeBleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return myServiceBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!Objects.requireNonNull(preferences.getString("deviceMacaddress", "")).equalsIgnoreCase(""))
            deviceMacc = preferences.getString("deviceMacaddress","");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String str = intent.getStringExtra("inputExtra");
        showNotification(device_disconnected_notif);
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        mCharacteristicReadList = new ArrayList<>();
        if(bluetoothAdapter!=null && bluetoothAdapter.isEnabled()){
            mBluetoothState = true;
            bluetoothStateBroadcast();
            if(!deviceMacc.equalsIgnoreCase(""))
                startScanInBackground();
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
        if(bluetoothGatt!=null){
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
        stopScaninBackground();
        disconnectDevice();
        unregisterReceiver(bluetoothReceiver);
        stopSelf();
    }

    public void startScanInBackground(){
        if(!tooFrequentScan) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    startScan();
                }
            });
        }else {
            sendTooFrequentScanBroadCast();
        }
    }

    public void stopScaninBackground(){
        if(!tooFrequentScan) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    long current_scan_time = Calendar.getInstance().getTimeInMillis();
                    int scan_difference = 0;
                    if (first_scan == 0) {
                        first_scan = Calendar.getInstance().getTimeInMillis();
                    } else {
                        scan_difference = (int) ((current_scan_time - first_scan) / 1000);
                        Log.i("scan_diff", String.valueOf(scan_difference));
                    }
                    if (num_of_scan > 3 && scan_difference != 0 && scan_difference <= 30) {

                        Log.i("here","here1");
                        tooFrequentScan = true;
                        sendTooFrequentScanBroadCast();
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                first_scan = 0;
                                num_of_scan = 0;
                                tooFrequentScan = false;
                                sendTooFrequentScanBroadCast();
                            }
                        }, 30000);
                    } else {
                        if(scan_difference>30){
                            scan_difference = 0;
                            first_scan = 0;
                            num_of_scan = 0;
                            tooFrequentScan = false;
                        }
                        stopScan();
                        num_of_scan++;
                    }
                }
            });
        }else {
            sendTooFrequentScanBroadCast();
        }
    }

    public void showNotification(String deviceState){
        Intent notificationintent = new Intent(this, PatientsView.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, notificationintent,0);
        Notification builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Pheezee")
                .setContentText(deviceState)
                .setSmallIcon(R.mipmap.pheezee_logos_final_square_round)
                .setColor(getResources().getColor(R.color.default_blue_light))
//                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,builder);
    }


    private void startScan() {
        List<ScanFilter> filters = new ArrayList<>();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        if(!deviceMacc.equalsIgnoreCase("")){
            ScanFilter mFilter = new ScanFilter.Builder().setDeviceAddress(deviceMacc).build();
            filters.add(mFilter);
        }

        if(!mScanning && mBluetoothState) {
            mScanResults = new ArrayList<>();
            mScanCallback = new BtleScanCallback(mScanResults);
            mBluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
            mScanning = true;
            sendScanStateBroadcast();
        }
    }

    public boolean isScanning(){
        return mScanning;
    }

    //Body part selection
    public boolean getDeviceState() {
        return mDeviceState;
    }

    public boolean getUsbState(){
        return mUsbState;
    }

    public void sendTooFrequentScanBroadCast(){
        Intent i = new Intent(scan_too_frequent);
        i.putExtra(scan_too_frequent,tooFrequentScan);
        sendBroadcast(i);
    }

    public void deviceStateBroadcast(){
        Intent i = new Intent(device_state);
        i.putExtra(device_state,mDeviceState);
        sendBroadcast(i);
    }

    public void updatePheezeeMac(String macaddress){
        this.deviceMacc = macaddress;
        if(mBluetoothState)
            startScanInBackground();
    }

    public void forgetPheezee(){
        this.deviceMacc = "";
        isConnectCommandGiven = false;
    }

    public void bluetoothStateBroadcast(){
        Intent i = new Intent(bluetooth_state);
        i.putExtra(bluetooth_state,mBluetoothState);
        sendBroadcast(i);
    }

    public void sendScanStateBroadcast(){
        Intent i = new Intent(scan_state);
        i.putExtra(scan_state,mScanning);
        sendBroadcast(i);
    }

    public void sendBatteryLevelBroadCast(){
        Intent i = new Intent(battery_percent);
        i.putExtra(battery_percent,String.valueOf(mBatteryPercent));
        sendBroadcast(i);
    }

    public void sendUsbStateBroadcast(){
        Intent i = new Intent(usb_state);
        i.putExtra(usb_state,mUsbState);
        sendBroadcast(i);
    }

    public void sendFirmwareVersion(){
        Intent i = new Intent(firmware_version);
        i.putExtra(firmware_version,mFirmwareVersion);
        sendBroadcast(i);
    }

    public void sendSerialNumberBroadcast(){
        Intent i = new Intent(serial_id);
        i.putExtra(serial_id,mSerialId);
        sendBroadcast(i);
    }

    public void sendManufacturerName(){
        Intent i = new Intent(manufacturer_name);
        i.putExtra(manufacturer_name,mManufacturerName);
        sendBroadcast(i);
    }

    public void sendScannedListBroadcast(){
        Intent i = new Intent(scanned_list);
        i.putExtra(scanned_list,"");
        sendBroadcast(i);
    }

    public void sendSessionDataBroadcast(){
        Intent i = new Intent(session_data);
        i.putExtra(session_data,"");
        sendBroadcast(i);
    }

    public Message getSessionData(){
        return mMessage;
    }

    public ArrayList<DeviceListClass> getScannedList(){
        return mScanResults;
    }



    public void stopScan(){
        if (mScanning && bluetoothAdapter != null && bluetoothAdapter.isEnabled() && mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mScanCallback);
        }
        mScanCallback = null;
        mScanning = false;
        sendScanStateBroadcast();
    }


    public void gerDeviceInfo(){
        bluetoothStateBroadcast();
        deviceStateBroadcast();
        sendBatteryLevelBroadCast();
        sendFirmwareVersion();
        deviceStateBroadcast();
        sendSerialNumberBroadcast();
        sendManufacturerName();
    }

    public void increaseGain(){
        writeCharacteristic(mCustomCharacteristic,"AD01");
    }

    public void decreaseGain(){
        writeCharacteristic(mCustomCharacteristic,"AD02");
    }

    public void sendBodypartDataToDevice(String exerciseType, int body_orientation, String patientName){
        String session_performing_notif = "Device Connected, Session is going on ";
        showNotification(session_performing_notif +patientName);
        writeCharacteristic(mCustomCharacteristic, ValueBasedColorOperations.getParticularDataToPheeze(exerciseType,body_orientation));
    }

    public void disableNotificationOfSession(){
        showNotification(device_connected_notif);
        if(bluetoothGatt!=null && mCustomCharacteristicDescriptor!=null && mCustomCharacteristic!=null){
            bluetoothGatt.setCharacteristicNotification(mCustomCharacteristic,false);
            mCustomCharacteristicDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(mCustomCharacteristicDescriptor);
            bluetoothGatt.writeCharacteristic(mCustomCharacteristic);
        }
    }

    public void connectDevice(String deviceMacc){
        if(!isConnectCommandGiven) {
            isConnectCommandGiven = true;
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(deviceMacc);
            this.remoteDevice = remoteDevice;
            bluetoothGatt = remoteDevice.connectGatt(this, false, callback);
        }
    }

    public void disconnectDevice() {
        if(bluetoothGatt==null){
            return;
        }
        bluetoothGatt.disconnect();
        bluetoothGatt.close();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    class BtleScanCallback extends ScanCallback {
        private ArrayList<DeviceListClass> mScanResults;

        BtleScanCallback(ArrayList<DeviceListClass> mScanResults) {
            this.mScanResults = mScanResults;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if(!deviceMacc.equalsIgnoreCase("") && !mDeviceState){
                BluetoothDevice device = result.getDevice();
                String deviceAddress = device.getAddress();
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
//                Log.i("device0",results.get(0).getDevice().getAddress());
                if(!deviceMacc.equalsIgnoreCase("") && !mDeviceState){
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
                if(mDeviceState){
                    stopScaninBackground();
                }
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

                    mScanResults.add(deviceListClass);

                    sendScannedListBroadcast();
                }
            }else {
//                sendScannedListBroadcast();
            }
        }
    }




    public BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    mCharacteristicReadList = new ArrayList<>();
                    mDeviceState = true;
                    refreshDeviceCache(gatt);
                    gatt.discoverServices();
                    deviceStateBroadcast();
                    showNotification(device_connected_notif);
                    stopScaninBackground();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Message msg = Message.obtain();
                    mDeviceState = false;
                }
            }
            if(status == BluetoothGatt.GATT_FAILURE){
                Message msg = Message.obtain();
                msg.obj = "N/C";
                showNotification(device_disconnected_notif);
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) throws NullPointerException {
            bluetoothGatt = gatt;
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
                    mUsbState = true;
                    sendUsbStateBroadcast();
                    showNotification(device_charging);
                }
                else if(usb_state==0) {
                    mUsbState = false;
                    sendUsbStateBroadcast();
                    showNotification(device_connected_notif);
                }
                Log.i("battery percentN", String.valueOf(battery));
                mBatteryPercent = battery;
                sendBatteryLevelBroadCast();
            }else if(characteristic.getUuid().equals(custom_characteristic_uuid)){
                byte[] temp_byte;
                temp_byte = characteristic.getValue();
                byte header_main = temp_byte[0];
                byte header_sub = temp_byte[1];
                //session related
                int sub_byte_size = 10;
                byte[] sub_byte = new byte[sub_byte_size];
                if (ByteToArrayOperations.byteToStringHexadecimal(header_main).equals("AA")) {
                    if (ByteToArrayOperations.byteToStringHexadecimal(header_sub).equals("01")) {
                        int j = 2;
                        for (int i = 0; i < sub_byte_size; i++, j++) {
                            sub_byte[i] = temp_byte[j];
                        }
                        mMessage = Message.obtain();
                        mMessage.obj = sub_byte;
                        boolean sessionCompleted = false;
                        if (!sessionCompleted && !first_packet) {
                            sendSessionDataBroadcast();
                        } else {
                            first_packet = false;
                        }
                    }
                }
                if (ByteToArrayOperations.byteToStringHexadecimal(header_main).equals("AF")) {
//                    software_gain = header_sub;
                    mMessage = Message.obtain();
                    mMessage.obj = sub_byte;
                    sendSessionDataBroadcast();
                }
            }
        }
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if(characteristic.getUuid().equals(custom_characteristic_uuid)){
                byte info_packet[] = characteristic.getValue();
                int battery = info_packet[11] & 0xFF;
                int device_status = info_packet[12] & 0xFF;
                int device_usb_state = info_packet[13] & 0xFF;
                if(device_usb_state==1) {
                    mUsbState = true;
                    sendUsbStateBroadcast();
                    showNotification(device_charging);
                }
                else if(device_status==0) {
                    mUsbState = false;
                    sendUsbStateBroadcast();
                    showNotification(device_connected_notif);
                }
                Log.i("battery percent2", String.valueOf(battery));
                mBatteryPercent = battery;
                sendBatteryLevelBroadCast();
                bluetoothGatt.readCharacteristic(mBatteryCharacteristic);

            }else if (characteristic.getUuid().equals(battery_level_characteristic_uuid)){
                byte b[] = characteristic.getValue();
                int battery  = b[0];
                int usb_state = b[1];
                if(usb_state==1) {
                    mUsbState = true;
                    sendUsbStateBroadcast();
                    showNotification(device_charging);
                }
                else if(usb_state==0) {
                    mUsbState = false;
                    sendUsbStateBroadcast();
                    showNotification(device_connected_notif);
                }
                Log.i("battery percent1", String.valueOf(battery));
                mBatteryPercent = battery;
                sendBatteryLevelBroadCast();
                gatt.setCharacteristicNotification(mBatteryCharacteristic, true);
                mBatteryDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(mBatteryDescriptor);

            }else if(characteristic.getUuid().equals(firmware_version_characteristic_uuid)){
                byte b[] = characteristic.getValue();
                String str = new String(b, StandardCharsets.UTF_8);
                mFirmwareVersion = str;
                sendFirmwareVersion();
                mCharacteristicReadList.add(mSerialIdCharacteristic);
                mCharacteristicReadList.add(mManufacturerNameCharacteristic);
            }else if(characteristic.getUuid().equals(serial_number_characteristic_uuid)){
                byte b[] = characteristic.getValue();
                String str = new String(b, StandardCharsets.UTF_8);
                mSerialId = str;
                sendSerialNumberBroadcast();
                mCharacteristicReadList.remove(0);
            }else if(characteristic.getUuid().equals(manufacturer_name_characteristic_uuid)){
                byte b[] = characteristic.getValue();
                String str = new String(b, StandardCharsets.UTF_8);
                mManufacturerName = str;
                sendManufacturerName();
                mCharacteristicReadList.remove(0);
            }

            if(mCharacteristicReadList.size()>0){
                bluetoothGatt.readCharacteristic(mCharacteristicReadList.get(0));
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if(mCharacteristicWrittenValue.equalsIgnoreCase("AA02")) {
                bluetoothGatt.readCharacteristic(mCustomCharacteristic);
                Log.i("here","battery read");
            }
            else{
                if(mCharacteristicWrittenValue.contains("AA")) {
                    bluetoothGatt.setCharacteristicNotification(mCustomCharacteristic, true);
                    mCustomCharacteristicDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    bluetoothGatt.writeDescriptor(mCustomCharacteristicDescriptor);
                }
            }
        }
    };

    private boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, String value) {
        byte[] b = ByteToArrayOperations.hexStringToByteArray(value);
        if (bluetoothGatt == null ) {
            return false;
        }
        if (characteristic == null) {
            return false;
        }
        characteristic.setValue(b);
        mCharacteristicWrittenValue = value;
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
                isConnectCommandGiven=false;
                mDeviceState = false;mFirmwareVersion="Null"; mSerialId="NULL";mBatteryPercent = 0;mManufacturerName="Null";
                if(bluetoothGatt!=null) {
                    bluetoothGatt.disconnect();
                    bluetoothGatt.close();
                }
                showNotification(device_disconnected_notif);
                gerDeviceInfo();
                if(!deviceMacc.equalsIgnoreCase(""))
                    startScanInBackground();
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                    isConnectCommandGiven = false;
                    mBluetoothState = true;
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if(!deviceMacc.equalsIgnoreCase(""))
                                startScanInBackground();
                            bluetoothStateBroadcast();
                        }
                    });
                }
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    isConnectCommandGiven = false;
                    mBluetoothState = false;mDeviceState = false;mFirmwareVersion="Null"; mSerialId="NULL";mBatteryPercent = 0;mManufacturerName="Null";
                    gerDeviceInfo();
                }
            }

        }
    };


    public class LocalBinder extends Binder {
        public PheezeeBleService getServiceInstance(){
            return PheezeeBleService.this;
        }
    }
}
