package com.example.sai.pheezeeapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sai.pheezeeapp.adapters.DeviceListArrayAdapter;
import com.example.sai.pheezeeapp.Classes.BluetoothSingelton;
import com.example.sai.pheezeeapp.Classes.DeviceListClass;
import com.example.sai.pheezeeapp.R;
import com.google.android.gms.common.internal.IAccountAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ScanDevicesActivity extends AppCompatActivity {

    private static final String TAG = "ScanDevicesActivity";
    ListView lv_scandevices;
    SwipeRefreshLayout swipeRefreshLayout;
    public static ImageView iv_device_connected;
    public static ImageView iv_device_disconnected;
    public static ImageView iv_bluetooth_connected;
    public static ImageView iv_bluetooth_disconnected;

    private boolean mScanning = false;
    Handler handler;


    private static final int REQUEST_FINE_LOCATION = 1;
    TextView tv_stoScan;
    ArrayList<DeviceListClass> mScanResults;
    DeviceListArrayAdapter deviceListArrayAdapter;
    private BtleScanCallback mScanCallback;
    BluetoothLeScanner mBluetoothLeScanner;
    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter madapter_scandevices;
    public static String selectedDeviceMacAddress;
    ImageView iv_back_scan_devices;

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_devices);
        Toolbar toolbar = findViewById(R.id.toolbar_scandevices);
        setSupportActionBar(toolbar);


        //Initialization
        tv_stoScan = findViewById(R.id.tv_stopscan);
        iv_back_scan_devices = findViewById(R.id.back_scan_devices);
        lv_scandevices =findViewById(R.id.lv_deviceList);
        swipeRefreshLayout = findViewById(R.id.scandevices_swiperefresh);

//        iv_bluetooth_connected = findViewById(R.id.iv_bluetooth_connected);
//        iv_bluetooth_disconnected = findViewById(R.id.iv_bluetooth_disconnected);
//        iv_device_connected = findViewById(R.id.iv_device_connected);
//        iv_device_disconnected = findViewById(R.id.iv_device_disconnected);


        handler = new Handler();
        mScanResults = new ArrayList<>();
        deviceListArrayAdapter = new DeviceListArrayAdapter(this, mScanResults);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        madapter_scandevices = BluetoothSingelton.getmInstance().getAdapter();
        madapter_scandevices = bluetoothManager.getAdapter();
        if(madapter_scandevices == null || !madapter_scandevices.isEnabled()){
            Intent enable_bluetooth  = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enable_bluetooth, REQUEST_ENABLE_BT);
        }


        iv_back_scan_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tv_stoScan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                String check_operation;


                check_operation = tv_stoScan.getText().toString();
                if(check_operation.equalsIgnoreCase("SCAN")){
                    tv_stoScan.setText(R.string.scandevices_stop);
                    startScan();
                }
                else {
                    tv_stoScan.setText(R.string.scandevices_scan);
                    stopScan();
                }
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.good_green,R.color.pale_good_green);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startScan();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },2000);
            }
        });

        lv_scandevices.setAdapter(deviceListArrayAdapter);

        startScan();
    }
    public Context getContext(){
        return this;
    }

    @Override
    protected void onResume() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        }
        super.onResume();
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startScan() {
        if(Build.VERSION.SDK_INT>22) {
            if (!hasPermissions() || mScanning) {
                return;
            }
        }

        tv_stoScan.setText(R.string.scandevices_stop);
        List<ScanFilter> filters = new ArrayList<>();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();


        mScanResults = new ArrayList<>();
        mScanCallback = new BtleScanCallback(mScanResults, deviceListArrayAdapter);
        mBluetoothLeScanner = madapter_scandevices.getBluetoothLeScanner();
        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
        mScanning = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mScanning)
                    stopScan();
            }

        }, 100000);
        // TODO start the scan
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopScan() {
        if (mScanning && madapter_scandevices != null && madapter_scandevices.isEnabled() && mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mScanCallback);
            scanComplete();
        }
        tv_stoScan.setText(R.string.scandevices_scan);
        mScanCallback = null;
        mScanning = false;
    }

    private void scanComplete() {
        if (mScanResults.isEmpty()) {
            return;
        }

        for (int i=0;i<mScanResults.size();i++) {

            Log.d(TAG,"found device:"+mScanResults.get(i).getDeviceMacAddress()+ "DEvice Name "+ mScanResults.get(i).getDeviceName());

        }
        deviceListArrayAdapter.updateList(mScanResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermissions() {
        if (madapter_scandevices == null || !madapter_scandevices.isEnabled()) {
            requestBluetoothEnable();
            return false;
        } else if (!hasLocationPermissions()) {
            requestLocationPermission();
            return false;
        }
        return true;
    }

    private void requestBluetoothEnable() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        //Log.d(TAG, "Requested user enables Bluetooth. Try starting the scan again.");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasLocationPermissions() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
    }


//    public static void setBleStatus(String str){
//        if(tv_bleStatus!=null){
//            tv_bleStatus.setText(str);
//        }
//    }

}


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class BtleScanCallback extends ScanCallback {
    private ArrayList<DeviceListClass> mScanResults;
    private DeviceListArrayAdapter deviceListArrayAdapter;

    BtleScanCallback(ArrayList<DeviceListClass> mScanResults, DeviceListArrayAdapter deviceListArrayAdapter) {
        this.mScanResults = mScanResults;
        this.deviceListArrayAdapter = deviceListArrayAdapter;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        addScanResult(result);
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        for (ScanResult result : results) {
            addScanResult(result);
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

                mScanResults.add(deviceListClass);
            }


        }
        deviceListArrayAdapter.updateList(mScanResults);
    }


}
