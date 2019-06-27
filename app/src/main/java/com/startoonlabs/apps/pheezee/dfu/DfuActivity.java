package com.startoonlabs.apps.pheezee.dfu;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.startoonlabs.apps.pheezee.classes.BluetoothSingelton;
import com.startoonlabs.apps.pheezee.classes.FileHelper;
import com.startoonlabs.apps.pheezee.dfu.fragment.UploadCancelFragment;
import com.startoonlabs.apps.pheezee.dfu.fragment.ZipInfoFragment;
import com.startoonlabs.apps.pheezee.dfu.settings.SettingsFragment;
import com.startoonlabs.apps.pheezee.R;

import java.io.File;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class DfuActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,UploadCancelFragment.CancelFragmentListener {

    public String TAG  = "DfuActivity";



    private static final String PREFS_DEVICE_NAME = "no.nordicsemi.android.nrftoolbox.dfu.PREFS_DEVICE_NAME";
    private static final String PREFS_FILE_NAME = "no.nordicsemi.android.nrftoolbox.dfu.PREFS_FILE_NAME";
    private static final String PREFS_FILE_TYPE = "no.nordicsemi.android.nrftoolbox.dfu.PREFS_FILE_TYPE";
    private static final String PREFS_FILE_SIZE = "no.nordicsemi.android.nrftoolbox.dfu.PREFS_FILE_SIZE";

    private static final String DATA_DEVICE = "device";
    private static final String DATA_FILE_TYPE = "file_type";
    private static final String DATA_FILE_TYPE_TMP = "file_type_tmp";
    private static final String DATA_FILE_PATH = "file_path";
    private static final String DATA_FILE_STREAM = "file_stream";
    private static final String DATA_INIT_FILE_PATH = "init_file_path";
    private static final String DATA_INIT_FILE_STREAM = "init_file_stream";
    private static final String DATA_STATUS = "status";

    private static final String EXTRA_URI = "uri";

    private static final int PERMISSION_REQ = 25;
    private static final int ENABLE_BT_REQ = 0;
    private static final int SELECT_FILE_REQ = 1;
    private static final int SELECT_INIT_FILE_REQ = 2;



    //DECLARING ALL THE VIEWS



    Button btn_dfu_select_file, btn_dfu_upload_file;

    private TextView mFileScopeView;
    private TextView mFileNameView;
    private TextView mFileTypeView;
    private TextView mFileSizeView;
    private TextView mFileStatusView;
    private TextView mDeviceNameView;
    private TextView mTextPercentage;
    private TextView mTextUploading;


    private ProgressBar mProgressBar;



    //PATH TO FILES AND URI

    private String mFilePath;
    private Uri mFileStreamUri;
    private String mInitFilePath;
    private Uri mInitFileStreamUri;
    private int mFileType;
    private int mFileTypeTmp; // This value is being used when user is selecting a file not to overwrite the old value (in case he/she will cancel selecting file)
    private boolean mStatusOk;




    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothDevice mDfuBluetoothDevice;
    BluetoothGatt mDfuBluetoothGatt;
    BluetoothManager mdfuBluetoothManager;
    BluetoothAdapter mdfuBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfu);


        if (!getIntent().getStringExtra("deviceMacAddress").equals("")){
            mdfuBluetoothAdapter = BluetoothSingelton.getmInstance().getAdapter();
            if (!mdfuBluetoothAdapter.isEnabled()) {
                Toast.makeText(this, "Bluetooth Disabled", Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            if(mDfuBluetoothGatt!=null){
                mDfuBluetoothGatt.disconnect();
                mDfuBluetoothGatt.close();
                Toast.makeText(this, "GATT CLOSED", Toast.LENGTH_SHORT).show();
            }

            mDfuBluetoothDevice = mdfuBluetoothAdapter.getRemoteDevice(getIntent().getStringExtra("deviceMacAddress"));
            if (mDfuBluetoothDevice!=null){

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String string = mDfuBluetoothDevice.getName();
                        mDeviceNameView.setText(string);
                    }
                },100);

            }

            /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //mDfuBluetoothGatt = mDfuBluetoothDevice.connectGatt(DfuActivity.this,true,callback);
                }
            });*/

        }




        setGui();




        btn_dfu_select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFileTypeTmp = mFileType;
                int index = 0;
                switch (mFileType) {
                    case DfuService.TYPE_AUTO:
                        index = 0;
                        break;
                    case DfuService.TYPE_SOFT_DEVICE:
                        index = 1;
                        break;
                    case DfuService.TYPE_BOOTLOADER:
                        index = 2;
                        break;
                    case DfuService.TYPE_APPLICATION:
                        index = 3;
                        break;
                }
                // Show a dialog with file types
                new AlertDialog.Builder(DfuActivity.this).setTitle(R.string.dfu_file_type_title)
                        .setSingleChoiceItems(R.array.dfu_file_type, index, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                switch (which) {
                                    case 0:
                                        mFileTypeTmp = DfuService.TYPE_AUTO;
                                        break;
                                    case 1:
                                        mFileTypeTmp = DfuService.TYPE_SOFT_DEVICE;
                                        break;
                                    case 2:
                                        mFileTypeTmp = DfuService.TYPE_BOOTLOADER;
                                        break;
                                    case 3:
                                        mFileTypeTmp = DfuService.TYPE_APPLICATION;
                                        break;
                                }
                            }
                        }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        openFileChooser();
                    }
                }).setNeutralButton(R.string.dfu_file_info, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        final ZipInfoFragment fragment = new ZipInfoFragment();
                        fragment.show(getSupportFragmentManager(), "help_fragment");
                    }
                }).setNegativeButton(R.string.cancel, null).show();

            }
        });




        btn_dfu_upload_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=26)
                    DfuServiceInitiator.createDfuNotificationChannel(DfuActivity.this);
                if (isDfuServiceRunning()) {
                    showUploadCancelDialog();
                    return;
                }

                // Check whether the selected file is a HEX file (we are just checking the extension)
                if (!mStatusOk) {
                    Toast.makeText(DfuActivity.this, R.string.dfu_file_status_invalid_message, Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(DfuActivity.this, "TEST", Toast.LENGTH_SHORT).show();
                // Save current state in order to restore it if user quit the Activity
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DfuActivity.this);
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putString(PREFS_DEVICE_NAME, mDfuBluetoothDevice.getName());
                Toast.makeText(DfuActivity.this,mDfuBluetoothDevice.getName(), Toast.LENGTH_LONG).show();
                editor.putString(PREFS_FILE_NAME, mFileNameView.getText().toString());
                editor.putString(PREFS_FILE_TYPE, mFileTypeView.getText().toString());
                editor.putString(PREFS_FILE_SIZE, mFileSizeView.getText().toString());
                editor.apply();

                showProgressBar();

                final boolean keepBond = preferences.getBoolean(SettingsFragment.SETTINGS_KEEP_BOND, false);

                final DfuServiceInitiator starter = new DfuServiceInitiator(mDfuBluetoothDevice.getAddress())
                        .setDeviceName(mDfuBluetoothDevice.getName())
                        .setKeepBond(keepBond);
                if (mFileType == DfuService.TYPE_AUTO) {
                    starter.setZip(mFileStreamUri, mFilePath);
                } else if (mFileNameView.getText().toString().endsWith(".img")) {
                    //starter.setNewtImage(mFileStreamUri, mFilePath);
                } else {
                    starter.setBinOrHex(mFileType, mFileStreamUri, mFilePath).setInitFile(mInitFileStreamUri, mInitFilePath);
                }
                starter.start(DfuActivity.this, DfuService.class);
            }
        });



        mFileType = DfuService.TYPE_AUTO;
        if (savedInstanceState != null) {
            mFileType = savedInstanceState.getInt(DATA_FILE_TYPE);
            mFileTypeTmp = savedInstanceState.getInt(DATA_FILE_TYPE_TMP);
            mFilePath = savedInstanceState.getString(DATA_FILE_PATH);
            mFileStreamUri = savedInstanceState.getParcelable(DATA_FILE_STREAM);
            mInitFilePath = savedInstanceState.getString(DATA_INIT_FILE_PATH);
            mInitFileStreamUri = savedInstanceState.getParcelable(DATA_INIT_FILE_STREAM);
            mDfuBluetoothDevice = savedInstanceState.getParcelable(DATA_DEVICE);
            mStatusOk = mStatusOk || savedInstanceState.getBoolean(DATA_STATUS);
            btn_dfu_upload_file.setEnabled(mDfuBluetoothDevice != null && mStatusOk);
        }
    }
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(DATA_FILE_TYPE, mFileType);
        outState.putInt(DATA_FILE_TYPE_TMP, mFileTypeTmp);
        outState.putString(DATA_FILE_PATH, mFilePath);
        outState.putParcelable(DATA_FILE_STREAM, mFileStreamUri);
        outState.putString(DATA_INIT_FILE_PATH, mInitFilePath);
        outState.putParcelable(DATA_INIT_FILE_STREAM, mInitFileStreamUri);
        outState.putParcelable(DATA_DEVICE, mDfuBluetoothDevice);
        outState.putBoolean(DATA_STATUS, mStatusOk);
    }

    private void setGui() {

        mFileNameView = (TextView)findViewById(R.id.tv_dfu_file_name);
        mFileTypeView = (TextView)findViewById(R.id.tv_dfu_file_type);
        mFileScopeView  = (TextView)findViewById(R.id.tv_dfu_file_scope);
        mFileSizeView = (TextView)findViewById(R.id.tv_dfu_file_size);
        mFileStatusView = (TextView)findViewById(R.id.tv_dfu_file_status);
        mDeviceNameView = (TextView) findViewById(R.id.device_name);

        //BUTTONS

        btn_dfu_select_file = (Button)findViewById(R.id.btn_dfu_select_file);
        btn_dfu_upload_file = (Button)findViewById(R.id.btn_dfu_file_upload);


        mTextPercentage = (TextView) findViewById(R.id.textviewProgress);
        mTextUploading = (TextView) findViewById(R.id.textviewUploading);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_file);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (isDfuServiceRunning()) {
            // Restore image file information
            mDeviceNameView.setText(preferences.getString(PREFS_DEVICE_NAME, ""));
            mFileNameView.setText(preferences.getString(PREFS_FILE_NAME, ""));
            mFileTypeView.setText(preferences.getString(PREFS_FILE_TYPE, ""));
            mFileSizeView.setText(preferences.getString(PREFS_FILE_SIZE, ""));
            mFileStatusView.setText(R.string.dfu_file_status_ok);
            mStatusOk = true;
            showProgressBar();
        }
    }


    //BLUETOOTH GATT CALLBACK
   /* public BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTING) {
                    Log.i(TAG, "GATT CONNECTING");
                } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i(TAG, "GATT CONNECTED");
                } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                    Log.i(TAG, "GATT DISCONNECTED");
                }
                else {
                }
            }
            if(status == BluetoothGatt.GATT_FAILURE){
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) throws NullPointerException {

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        }

    };
*/






    //DFU SERVICE LISTNER



    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            mProgressBar.setIndeterminate(true);
            Toast.makeText(DfuActivity.this,"ADLAS",Toast.LENGTH_LONG).show();
            Log.i(TAG,"ExAMPLENKLASSHAFKASLFasfas");
            mTextPercentage.setText(R.string.dfu_status_connecting);
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
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
            mTextPercentage.setText(R.string.dfu_status_completed);
            // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onTransferCompleted();

                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }

        @Override
        public void onDfuAborted(final String deviceAddress) {
            mTextPercentage.setText(R.string.dfu_status_aborted);
            // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onUploadCanceled();

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
            showErrorMessage(message);

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


    protected void onResume() {
        super.onResume();

        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
    }

   @Override
   protected void onPause() {
       super.onPause();

       DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);  //remve comment to clear error
   }


    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQ: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // We have been granted the Manifest.permission.WRITE_EXTERNAL_STORAGE permission. Now we may proceed with exporting.
                    FileHelper.createSamples(this);
                } else {
                    Toast.makeText(this, "NO REQUIRED PERMISSION", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }




    //ON ACTIVITY RESULT WHEN THE FILE IS SELECTED FROM THE MANAGER


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case SELECT_FILE_REQ: {
                // clear previous data
                mFileType = mFileTypeTmp;
                mFilePath = null;
                mFileStreamUri = null;

                // and read new one
                final Uri uri = data.getData();
                /*
                 * The URI returned from application may be in 'file' or 'content' schema. 'File' schema allows us to create a File object and read details from if
                 * directly. Data from 'Content' schema must be read by Content Provider. To do that we are using a Loader.
                 */
                if (uri.getScheme().equals("file")) {
                    // the direct path to the file has been returned
                    final String path = uri.getPath();
                    final File file = new File(path);
                    mFilePath = path;
                    Log.i(TAG,"INSIDE SELECT FILE");

                    updateFileInfo(file.getName(), file.length(), mFileType);
                } else if (uri.getScheme().equals("content")) {
                    // an Uri has been returned
                    mFileStreamUri = uri;
                    Log.i(TAG,"INSIDE SELECT FILE ELSE");
                    // if application returned Uri for streaming, let's us it. Does it works?
                    // FIXME both Uris works with Google Drive app. Why both? What's the difference? How about other apps like DropBox?
                    final Bundle extras = data.getExtras();
                    if (extras != null && extras.containsKey(Intent.EXTRA_STREAM))
                        mFileStreamUri = extras.getParcelable(Intent.EXTRA_STREAM);

                    // file name and size must be obtained from Content Provider
                    final Bundle bundle = new Bundle();
                    bundle.putParcelable(EXTRA_URI, uri);
                    getLoaderManager().restartLoader(SELECT_FILE_REQ, bundle, this);
                }
                break;
            }
            case SELECT_INIT_FILE_REQ: {
                mInitFilePath = null;
                mInitFileStreamUri = null;

                // and read new one
                final Uri uri = data.getData();
                /*
                 * The URI returned from application may be in 'file' or 'content' schema. 'File' schema allows us to create a File object and read details from if
                 * directly. Data from 'Content' schema must be read by Content Provider. To do that we are using a Loader.
                 */
                if (uri.getScheme().equals("file")) {
                    // the direct path to the file has been returned
                    mInitFilePath = uri.getPath();
                    mFileStatusView.setText(R.string.dfu_file_status_ok_with_init);
                } else if (uri.getScheme().equals("content")) {
                    // an Uri has been returned
                    mInitFileStreamUri = uri;
                    // if application returned Uri for streaming, let's us it. Does it works?
                    // FIXME both Uris works with Google Drive app. Why both? What's the difference? How about other apps like DropBox?
                    final Bundle extras = data.getExtras();
                    if (extras != null && extras.containsKey(Intent.EXTRA_STREAM))
                        mInitFileStreamUri = extras.getParcelable(Intent.EXTRA_STREAM);
                    mFileStatusView.setText(R.string.dfu_file_status_ok_with_init);
                }
                break;
            }
            default:
                break;
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Uri uri = args.getParcelable(EXTRA_URI);
        /*
         * Some apps, f.e. Google Drive allow to select file that is not on the device. There is no "_data" column handled by that provider. Let's try to obtain
         * all columns and than check which columns are present.
         */
        // final String[] projection = new String[] { MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATA };
        return new CursorLoader(this, uri, null /* all columns, instead of projection */, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToNext()) {
            /*
             * Here we have to check the column indexes by name as we have requested for all. The order may be different.
             */
            final String fileName = data.getString(data.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)/* 0 DISPLAY_NAME */);
            final int fileSize = data.getInt(data.getColumnIndex(MediaStore.MediaColumns.SIZE) /* 1 SIZE */);
            String filePath = null;
            final int dataIndex = data.getColumnIndex(MediaStore.MediaColumns.DATA);
            if (dataIndex != -1)
                filePath = data.getString(dataIndex /* 2 DATA */);
            if (!TextUtils.isEmpty(filePath))
                mFilePath = filePath;

            updateFileInfo(fileName, fileSize, mFileType);
        } else {
            mFileNameView.setText(null);
            mFileTypeView.setText(null);
            mFileSizeView.setText(null);
            mFilePath = null;
            mFileStreamUri = null;
            mFileStatusView.setText(R.string.dfu_file_status_error);
            mStatusOk = false;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFileNameView.setText(null);
        mFileTypeView.setText(null);
        mFileSizeView.setText(null);
        mFilePath = null;
        mFileStreamUri = null;
        mStatusOk = false;
    }


    //UPDATE FILE
    private void updateFileInfo(final String fileName, final long fileSize, final int fileType) {
        mFileNameView.setText(fileName);
        switch (fileType) {
            case DfuService.TYPE_AUTO:
                Log.i(TAG,"UPDATE FILE INFO");
                Toast.makeText(this, "LOLOLOLOLOL", Toast.LENGTH_SHORT).show();
                mFileTypeView.setText(getResources().getStringArray(R.array.dfu_file_type)[0]);
                break;
            case DfuService.TYPE_SOFT_DEVICE:
                mFileTypeView.setText(getResources().getStringArray(R.array.dfu_file_type)[1]);
                break;
            case DfuService.TYPE_BOOTLOADER:
                mFileTypeView.setText(getResources().getStringArray(R.array.dfu_file_type)[2]);
                break;
            case DfuService.TYPE_APPLICATION:
                mFileTypeView.setText(getResources().getStringArray(R.array.dfu_file_type)[3]);
                break;
        }
        mFileSizeView.setText(getString(R.string.dfu_file_size_text, fileSize));
        final String extension = mFileType == DfuService.TYPE_AUTO ? "(?i)ZIP" : "(?i)HEX|BIN|IMG"; // (?i) =  case insensitive
        final boolean statusOk = mStatusOk = MimeTypeMap.getFileExtensionFromUrl(fileName).matches(extension);
        mFileStatusView.setText(statusOk ? R.string.dfu_file_status_ok : R.string.dfu_file_status_invalid);
        btn_dfu_upload_file.setEnabled(mDfuBluetoothDevice != null && statusOk);

        // Ask the user for the Init packet file if HEX or BIN files are selected. In case of a ZIP file the Init packets should be included in the ZIP.
        if (statusOk && fileType != DfuService.TYPE_AUTO && !MimeTypeMap.getFileExtensionFromUrl(fileName).matches("(?i)IMG")) {
            new AlertDialog.Builder(this).setTitle(R.string.dfu_file_init_title).setMessage(R.string.dfu_file_init_message)
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            mInitFilePath = null;
                            mInitFileStreamUri = null;
                        }
                    }).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType(DfuService.MIME_TYPE_OCTET_STREAM);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, SELECT_INIT_FILE_REQ);
                }
            }).show();
        }
    }


    //OPEN FILE CHOOSER

    private void openFileChooser() {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mFileTypeTmp == DfuService.TYPE_AUTO ? DfuService.MIME_TYPE_ZIP : DfuService.MIME_TYPE_OCTET_STREAM);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // file browser has been found on the device
            startActivityForResult(intent, SELECT_FILE_REQ);
        } else {
            // there is no any file browser app, let's try to download one
            /*final View customView = getLayoutInflater().inflate(R.layout.app_file_browser, null);
            final ListView appsList = (ListView) customView.findViewById(android.R.id.list);
            appsList.setAdapter(new FileBrowserAppsAdapter(this));
            appsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            appsList.setItemChecked(0, true);
            new AlertDialog.Builder(this).setTitle(R.string.dfu_alert_no_filebrowser_title).setView(customView)
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    final int pos = appsList.getCheckedItemPosition();
                    if (pos >= 0) {
                        final String query = getResources().getStringArray(R.array.dfu_app_file_browser_action)[pos];
                        final Intent storeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(query));
                        startActivity(storeIntent);
                    }
                }
            }).show();*/
        }
    }

    //SHOW PROGRESS BAR

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTextPercentage.setVisibility(View.VISIBLE);
        mTextPercentage.setText(null);
        mTextUploading.setText(R.string.dfu_status_uploading);
        mTextUploading.setVisibility(View.VISIBLE);
        btn_dfu_select_file.setEnabled(false);
        btn_dfu_upload_file.setEnabled(true);
        btn_dfu_upload_file.setText(R.string.dfu_action_upload_cancel);
    }

    private void showUploadCancelDialog() {
        final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        final Intent pauseAction = new Intent(DfuService.BROADCAST_ACTION);
        pauseAction.putExtra(DfuService.EXTRA_ACTION, DfuService.ACTION_PAUSE);
        manager.sendBroadcast(pauseAction);

        final UploadCancelFragment fragment = UploadCancelFragment.getInstance();
        fragment.show(getSupportFragmentManager(), TAG);
    }

    private void onTransferCompleted() {
        clearUI(true);
        showToast(R.string.dfu_success);
    }

    public void onUploadCanceled() {
        clearUI(false);
        showToast(R.string.dfu_aborted);
    }

    @Override
    public void onCancelUpload() {
        mProgressBar.setIndeterminate(true);
        mTextUploading.setText(R.string.dfu_status_aborting);
        mTextPercentage.setText(null);
    }

    private void showErrorMessage(final String message) {
        clearUI(false);
        showToast("Upload failed: " + message);
    }

    private void clearUI(final boolean clearDevice) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextPercentage.setVisibility(View.INVISIBLE);
        mTextUploading.setVisibility(View.INVISIBLE);
        btn_dfu_select_file.setEnabled(true);
        btn_dfu_upload_file.setEnabled(false);
        btn_dfu_upload_file.setText(R.string.dfu_action_upload);
        if (clearDevice) {
            mDfuBluetoothDevice = null;
            mDeviceNameView.setText(R.string.dfu_default_name);
        }
        // Application may have lost the right to these files if Activity was closed during upload (grant uri permission). Clear file related values.
        mFileNameView.setText(null);
        mFileTypeView.setText(null);
        mFileSizeView.setText(null);
        mFileStatusView.setText(R.string.dfu_file_status_no_file);
        mFilePath = null;
        mFileStreamUri = null;
        mInitFilePath = null;
        mInitFileStreamUri = null;
        mStatusOk = false;
    }





    private void showToast(final int messageResId) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void showToast(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
