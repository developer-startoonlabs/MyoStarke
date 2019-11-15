package com.startoonlabs.apps.pheezee.activities;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.dfu.DfuService;
import com.startoonlabs.apps.pheezee.dfu.fragment.UploadCancelFragment;
import com.startoonlabs.apps.pheezee.services.PheezeeBleService;
import com.startoonlabs.apps.pheezee.utils.NetworkOperations;
import com.startoonlabs.apps.pheezee.utils.ZipOperations;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import no.nordicsemi.android.dfu.DfuController;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.battery_percent;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.bluetooth_state;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.device_state;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.df_characteristic_written;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.manufacturer_name;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.serial_id;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.usb_state;

public class DeviceInfoActivity extends AppCompatActivity implements UploadCancelFragment.CancelFragmentListener {

    //Bluetooth related declarations
    public String TAG  = "DeviceInfoActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    BluetoothManager mBluetoothManager;
    PheezeeBleService mService;
    private boolean isBound = false, start_update = false, mDeviceState = false;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    //Declaring all the view items
    TextView tv_device_name,tv_device_mamc, tv_firmware_version, tv_serial_id,
            tv_battery_level,tv_connection_status, tv_disconnect_forget, mTextUploading, mTextPercentage, tv_update_firmware;
    ImageView iv_back_device_info;
    private ProgressBar mProgressBar;
    LinearLayout ll_dfu;
    DfuController controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        tv_disconnect_forget = findViewById(R.id.tv_disconnect_forget);
        tv_device_name = findViewById(R.id.tv_deviceinfo_device_name);
        tv_device_mamc = findViewById(R.id.tv_deviceinfo_device_mac);
        tv_battery_level = findViewById(R.id.tv_deviceinfo_device_battery);
        tv_connection_status = findViewById(R.id.tv_deviceinfo_device_connection_status);
        tv_serial_id = findViewById(R.id.tv_deviceinfo_device_serial);
        tv_firmware_version = findViewById(R.id.tv_deviceinfo_device_firmware);
        iv_back_device_info = findViewById(R.id.iv_back_device_info);
        mProgressBar = findViewById(R.id.progressbar_file);
        mTextUploading = findViewById(R.id.textviewUploading);
        mTextPercentage = findViewById(R.id.textviewProgress);
        tv_update_firmware = findViewById(R.id.update_firmware);
        ll_dfu = findViewById(R.id.ll_dfu);

        iv_back_device_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tv_connection_status.setText("N/C");

        //checking bluetooth switched on and connecting gatt functions
        mBluetoothManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = mBluetoothManager.getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(DeviceInfoActivity.this, "Bluetooth Disabled", Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if(!getIntent().getStringExtra("deviceMacAddress").equals("")){
            tv_device_mamc.setText(getIntent().getStringExtra("deviceMacAddress"));
            tv_disconnect_forget.setText("Forget Device");
        }


        tv_disconnect_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    editor = preferences.edit();
                    editor.putString("deviceMacaddress","");
                    editor.apply();
                    if(mService!=null){
                        mService.forgetPheezee();
                        mService.disconnectDevice();
                    }
                    refreshView();
                    tv_disconnect_forget.setText("");
                Intent intent = new Intent();
                setResult(13, intent);
                finish();
            }
        });

        tv_update_firmware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkOperations.isNetworkAvailable(DeviceInfoActivity.this)){
                    Log.i("here","updating");
                    startFirmwareUpdate();
                }else {
                    NetworkOperations.networkError(DeviceInfoActivity.this);
                }
            }
        });

        Intent mIntent = new Intent(this,PheezeeBleService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(device_state);
        intentFilter.addAction(bluetooth_state);
        intentFilter.addAction(usb_state);
        intentFilter.addAction(battery_percent);
        intentFilter.addAction(PheezeeBleService.firmware_version);
        intentFilter.addAction(serial_id);
        intentFilter.addAction(manufacturer_name);
        intentFilter.addAction(df_characteristic_written);
        registerReceiver(device_info_receiver,intentFilter);


        if(getIntent().getBooleanExtra("start_update", false)){
            tv_update_firmware.setVisibility(View.VISIBLE);
            Log.i("here","hereinupdate");
            start_update = true;
        }
    }

    private void startFirmwareUpdate(){
        if(mDeviceState) {
            String str = tv_update_firmware.getText().toString();
            if (str.equalsIgnoreCase("update")) {
                if (mService != null) {
                    mService.writeToDfuCharacteristic();
                }
            } else {
                if (controller != null) {
                    if (isDfuServiceRunning()) {
                        showUploadCancelDialog();
                    }
                }
            }
        }else {
            showToast("Please connect device");
        }
    }


    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBound = true;
            PheezeeBleService.LocalBinder mLocalBinder = (PheezeeBleService.LocalBinder)service;
            mService = mLocalBinder.getServiceInstance();
            mService.gerDeviceInfo();
            mDeviceState = mService.getDeviceState();
            if(start_update){
                tv_update_firmware.performClick();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            mService = null;
        }
    };




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isBound){
            unbindService(mConnection);
        }
        unregisterReceiver(device_info_receiver);
        if(controller!=null){
            Log.i("here1","controller not null");
            if(isDfuServiceRunning()){
                Log.i("here2","DFU is running");
                controller.abort();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(!isDfuServiceRunning())
            super.onBackPressed();
        else {
            showUploadCancelDialog();
        }
    }

    /**
     * Refresh the view with null string once disconnected
     */
    public void refreshView(){
        tv_device_name.setText(R.string.device_null);
        tv_device_mamc.setText(R.string.device_null);
        tv_firmware_version.setText(R.string.device_null);
        tv_serial_id.setText(R.string.device_null);
        tv_battery_level.setText(R.string.device_null);
        tv_connection_status.setText(R.string.device_not_connected);
        tv_disconnect_forget.setText("");
    }



    BroadcastReceiver device_info_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equalsIgnoreCase(device_state)){
                boolean device_status = intent.getBooleanExtra(device_state,false);
                if(device_status){
                    tv_connection_status.setText("Connected");
                    mDeviceState = true;
                }else {
                    tv_update_firmware.setVisibility(View.GONE);
                    tv_connection_status.setText("Not Connected");
                    mDeviceState = false;
                }
            }else if(action.equalsIgnoreCase(bluetooth_state)){
                boolean ble_state = intent.getBooleanExtra(bluetooth_state,false);
                if(ble_state){
                }else {
                    tv_connection_status.setText("Not Connected");
                }
            }else if(action.equalsIgnoreCase(usb_state)){
                boolean usb_status = intent.getBooleanExtra(usb_state,false);
                if(usb_status){
                }else {
                }
            }else if(action.equalsIgnoreCase(battery_percent)){
                String percent = intent.getStringExtra(battery_percent);
                tv_battery_level.setText(percent.concat("%"));
            }else if(action.equalsIgnoreCase(PheezeeBleService.firmware_version)){
                String firmwareVersion = intent.getStringExtra(PheezeeBleService.firmware_version);
                if(!Objects.requireNonNull(preferences.getString("firmware_update", "")).equalsIgnoreCase("")
                        && !preferences.getString("firmware_version","").equalsIgnoreCase(firmwareVersion)){
                    tv_update_firmware.setVisibility(View.VISIBLE);
                }
                tv_firmware_version.setText(firmwareVersion);
            }else if(action.equalsIgnoreCase(serial_id)){
                String serial = intent.getStringExtra(serial_id);
                tv_serial_id.setText(serial);
            }else if(action.equalsIgnoreCase(manufacturer_name)){
                String manufacturer = intent.getStringExtra(manufacturer_name);
                tv_device_name.setText(manufacturer);
            }else if(action.equalsIgnoreCase(df_characteristic_written)){
                Log.i("here","here");
                startDfuService();
            }
        }
    };

    private void dfuCanceledView() {
        ll_dfu.setVisibility(View.GONE);
//        mProgressBar.setVisibility(View.INVISIBLE);
//        mTextPercentage.setVisibility(View.INVISIBLE);
//        mTextUploading.setVisibility(View.INVISIBLE);
        mTextPercentage.setText("");
        mTextUploading.setText("");
        mProgressBar.setProgress(0);
    }

    @Override
    public void onCancelUpload() {
        mProgressBar.setIndeterminate(true);
        mTextUploading.setText(R.string.dfu_status_aborting);
        mTextPercentage.setText(null);
    }


    private void showUploadCancelDialog() {
        final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        final Intent pauseAction = new Intent(DfuService.BROADCAST_ACTION);
        pauseAction.putExtra(DfuService.EXTRA_ACTION, DfuService.ACTION_PAUSE);
        manager.sendBroadcast(pauseAction);

        final UploadCancelFragment fragment = UploadCancelFragment.getInstance();
        fragment.show(getSupportFragmentManager(), TAG);
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTextPercentage.setVisibility(View.VISIBLE);
        mTextPercentage.setText(null);
        mTextUploading.setText(R.string.dfu_status_uploading);
        mTextUploading.setVisibility(View.VISIBLE);
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    //DFU SERVICE LISTNER
    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            mProgressBar.setIndeterminate(true);
            mTextPercentage.setText(R.string.dfu_status_connecting);
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
            tv_update_firmware.setVisibility(View.INVISIBLE);
            mProgressBar.setIndeterminate(true);
            mTextPercentage.setText(R.string.dfu_status_starting);
        }

        @Override
        public void onEnablingDfuMode(final String deviceAddress) {
            mProgressBar.setIndeterminate(true);
            mTextPercentage.setText(R.string.dfu_status_switching_to_dfu);
        }

        @Override
        public void onFirmwareValidating(final String deviceAddress) {
            mProgressBar.setIndeterminate(true);
            mTextPercentage.setText(R.string.dfu_status_validating);
        }

        @Override
        public void onDeviceDisconnecting(final String deviceAddress) {
            mProgressBar.setIndeterminate(true);
            mTextPercentage.setText(R.string.dfu_status_disconnecting);
        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {
            tv_update_firmware.setVisibility(View.GONE);
            mTextPercentage.setText(R.string.dfu_status_completed);
            editor = preferences.edit();
            editor.putString("firmware_update","");
            editor.putString("firmware_version", "");
            editor.apply();
            // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dfuCanceledView();
                    showToast(getResources().getString(R.string.firmware_updated));

                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }

        @Override
        public void onDfuAborted(final String deviceAddress) {
            tv_update_firmware.setText("Update");
            tv_update_firmware.setVisibility(View.VISIBLE);
            mTextPercentage.setText(R.string.dfu_status_aborted);
            // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dfuCanceledView();
                    showToast(getResources().getString(R.string.dfu_aborted));

                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
            mProgressBar.setIndeterminate(false);
            mProgressBar.setProgress(percent);
            mTextPercentage.setText(getString(R.string.dfu_uploading_percentage, percent));
            if (partsTotal > 1)
                mTextUploading.setText(getString(R.string.dfu_status_uploading_part, currentPart, partsTotal));
            else
                mTextUploading.setText(R.string.dfu_status_uploading);
        }

        @Override
        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
            dfuCanceledView();
            tv_update_firmware.setText("Update");
            tv_update_firmware.setVisibility(View.VISIBLE);
            showToast("Error :"+message);
            // We have to wait a bit before canceling notification. This is called before DfuService creates the last notification.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this,mDfuProgressListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
    }

    private void startDfuService(){
        Log.i("here1","here");
        if(Build.VERSION.SDK_INT>=26)
            DfuServiceInitiator.createDfuNotificationChannel(this);
        if (isDfuServiceRunning()) {
            showUploadCancelDialog();
            return;
        }
        ll_dfu.setVisibility(View.VISIBLE);
        showProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String mFilePath = "";
                Uri mFileStreamUri;

                // Moves the current Thread into the background
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                HttpURLConnection httpURLConnection = null;
                byte[] buffer = new byte[2048];
                try {
                    //Your http connection
                    httpURLConnection = (HttpURLConnection) new URL(preferences.getString("firmware_update","")).openConnection();

                    //Change below path to Environment.getExternalStorageDirectory() or something of your
                    // own by creating storage utils
                    File zip = new File(Environment.getExternalStorageDirectory()+"/Pheezee/firmware/");
                    if(!zip.exists())
                        zip.mkdir();
//                            File file = new File(zip, "latest.zip");
//                            file.createNewFile();

                    ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(httpURLConnection.getInputStream()));
                    ZipEntry zipEntry = zipInputStream.getNextEntry();

                    int readLength;

                    while(zipEntry != null){
                        File newFile = new File(zip, zipEntry.getName());

                        if (!zipEntry.isDirectory()) {
                            FileOutputStream fos = new FileOutputStream(newFile);
                            while ((readLength = zipInputStream.read(buffer)) > 0) {
                                fos.write(buffer, 0, readLength);
                            }
                            fos.close();
                        } else {
                            newFile.mkdirs();
                        }

                        Log.i("zip file path = ", newFile.getPath());
                        zipInputStream.closeEntry();
                        zipEntry = zipInputStream.getNextEntry();
                    }
                    // Close Stream and disconnect HTTP connection. Move to finally
                    zipInputStream.closeEntry();
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                        ZipOperations.zipFolder(new File(Environment.getExternalStorageDirectory()+"/Pheezee/firmware/"));
                        File file = new File(Environment.getExternalStorageDirectory()+"/Pheezee/firmware.zip");
                        if(file!=null) {
                            mFilePath = Environment.getExternalStorageDirectory() + "/Pheezee/firmware.zip";
                            mFileStreamUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Pheezee/firmware.zip"));


                            if(mService!=null) {
                                final DfuServiceInitiator starter = new DfuServiceInitiator(mService.getMacAddress())
                                        .setDeviceName(mService.getDeviceName())
                                        .setKeepBond(false);
                                starter.setZip(mFileStreamUri, mFilePath);
                                controller = starter.start(getApplicationContext(), DfuService.class);
                            }
                        }
                    }
                }
            }
        }).start();
    }


    private boolean isDfuServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (DfuService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
