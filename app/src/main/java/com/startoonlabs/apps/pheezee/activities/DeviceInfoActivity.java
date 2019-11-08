package com.startoonlabs.apps.pheezee.activities;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.classes.BluetoothSingelton;
import com.startoonlabs.apps.pheezee.dfu.DfuService;
import com.startoonlabs.apps.pheezee.dfu.fragment.UploadCancelFragment;
import com.startoonlabs.apps.pheezee.services.BluetoothReceiver;
import com.startoonlabs.apps.pheezee.services.PheezeeBleService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

import static com.google.android.gms.common.internal.Constants.EXTRA_URI;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.battery_percent;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.bluetooth_state;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.device_state;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.df_progress_current_parts;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.dfu_aborted;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.dfu_completed;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.dfu_device_connecting;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.dfu_enabling_mode;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.dfu_error_message;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.dfu_firmware_validating;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.dfu_process_starting;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.dfu_progress_changed;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.dfu_progress_parts_total;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.dfu_progress_percent;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.dfu_start_initiated;
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
    boolean isBound = false;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    //Declaring all the view items
    TextView tv_device_name,tv_device_mamc, tv_firmware_version, tv_serial_id,
            tv_battery_level,tv_connection_status, tv_disconnect_forget, mTextUploading, mTextPercentage, tv_update_firmware;
    ImageView iv_back_device_info;
    private ProgressBar mProgressBar;
    RelativeLayout rl_dfu;



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
        rl_dfu = findViewById(R.id.rl_dfu);

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
                if(mService!=null){
                    mService.writeToDfuCharacteristic();
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
        intentFilter.addAction(dfu_start_initiated);
        intentFilter.addAction(dfu_error_message);
        intentFilter.addAction(dfu_progress_changed);
        intentFilter.addAction(dfu_aborted);
        intentFilter.addAction(dfu_completed);
        intentFilter.addAction(dfu_device_connecting);
        intentFilter.addAction(dfu_firmware_validating);
        intentFilter.addAction(dfu_enabling_mode);
        intentFilter.addAction(dfu_process_starting);
        registerReceiver(device_info_receiver,intentFilter);
    }


    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBound = true;
            PheezeeBleService.LocalBinder mLocalBinder = (PheezeeBleService.LocalBinder)service;
            mService = mLocalBinder.getServiceInstance();
            mService.gerDeviceInfo();
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
                }else {
                    tv_connection_status.setText("Not Connected");
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
                tv_firmware_version.setText(firmwareVersion);
            }else if(action.equalsIgnoreCase(serial_id)){
                String serial = intent.getStringExtra(serial_id);
                tv_serial_id.setText(serial);
            }else if(action.equalsIgnoreCase(manufacturer_name)){
                String manufacturer = intent.getStringExtra(manufacturer_name);
                tv_device_name.setText(manufacturer);
            }else if(action.equalsIgnoreCase(dfu_start_initiated)){
                boolean start_initiated = intent.getBooleanExtra(dfu_start_initiated,false);
                if(start_initiated){
                    rl_dfu.setVisibility(View.VISIBLE);
                    showProgressBar();
                }else {
                    dfuCanceledView();
                }

            }else if(action.equalsIgnoreCase(dfu_error_message)){
                String message = intent.getStringExtra(dfu_error_message);
                dfuCanceledView();
                showToast("Upload failed "+message);
            }else if(action.equalsIgnoreCase(dfu_progress_changed)){
                int percent = intent.getIntExtra(dfu_progress_percent,0);
                int totalPart = intent.getIntExtra(dfu_progress_parts_total,0);
                int currentParts = intent.getIntExtra(df_progress_current_parts,0);
                sendProgressUpdate(percent,totalPart,currentParts);
            }else if(action.equalsIgnoreCase(dfu_aborted)){
                mTextPercentage.setText(R.string.dfu_status_aborted);
            }else if(action.equalsIgnoreCase(dfu_completed)){
                mTextPercentage.setText(R.string.dfu_status_completed);
            }else if(action.equalsIgnoreCase(dfu_device_connecting)){
                mProgressBar.setIndeterminate(true);
                mTextPercentage.setText(R.string.dfu_status_disconnecting);
            }else if(action.equalsIgnoreCase(dfu_firmware_validating)){
                mProgressBar.setIndeterminate(true);
                mTextPercentage.setText(R.string.dfu_status_validating);
            }else if(action.equalsIgnoreCase(dfu_enabling_mode)){
                mProgressBar.setIndeterminate(true);
                mTextPercentage.setText(R.string.dfu_status_switching_to_dfu);
            }else if(action.equalsIgnoreCase(dfu_process_starting)){
                mProgressBar.setIndeterminate(true);
                mTextPercentage.setText(R.string.dfu_status_starting);
            }
        }
    };

    private void sendProgressUpdate(int percent, int totalPart, int currentParts) {
        mProgressBar.setIndeterminate(false);
        mProgressBar.setProgress(percent);
        mTextPercentage.setText(getString(R.string.dfu_uploading_percentage, percent));
        if (totalPart > 1)
            mTextUploading.setText(getString(R.string.dfu_status_uploading_part, currentParts, totalPart));
        else
            mTextUploading.setText(R.string.dfu_status_uploading);
    }

    private void dfuCanceledView() {
        rl_dfu.setVisibility(View.GONE);
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


//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        final Uri uri = args.getParcelable(EXTRA_URI);
//        /*
//         * Some apps, f.e. Google Drive allow to select file that is not on the device. There is no "_data" column handled by that provider. Let's try to obtain
//         * all columns and than check which columns are present.
//         */
//        // final String[] projection = new String[] { MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATA };
//        return new CursorLoader(this, uri, null /* all columns, instead of projection */, null, null, null);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        if (data != null && data.moveToNext()) {
//            /*
//             * Here we have to check the column indexes by name as we have requested for all. The order may be different.
//             */
//            final String fileName = data.getString(data.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)/* 0 DISPLAY_NAME */);
//            final int fileSize = data.getInt(data.getColumnIndex(MediaStore.MediaColumns.SIZE) /* 1 SIZE */);
//            String filePath = null;
//            final int dataIndex = data.getColumnIndex(MediaStore.MediaColumns.DATA);
//            if (dataIndex != -1)
//                filePath = data.getString(dataIndex /* 2 DATA */);
//            if (!TextUtils.isEmpty(filePath))
//                mFilePath = filePath;
//
//            updateFileInfo(fileName, fileSize, mFileType);
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//    }
}
