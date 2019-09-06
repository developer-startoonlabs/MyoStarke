package com.startoonlabs.apps.pheezee.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.classes.BluetoothGattSingleton;
import com.startoonlabs.apps.pheezee.classes.BluetoothSingelton;
import com.startoonlabs.apps.pheezee.classes.MyBottomSheetDialog;
import com.startoonlabs.apps.pheezee.adapters.PatientsRecyclerViewAdapter;
import com.startoonlabs.apps.pheezee.pojos.AddPatientData;
import com.startoonlabs.apps.pheezee.pojos.DeletePatientData;
import com.startoonlabs.apps.pheezee.pojos.PatientDetailsData;
import com.startoonlabs.apps.pheezee.pojos.PatientStatusData;
import com.startoonlabs.apps.pheezee.pojos.ResponseData;
import com.startoonlabs.apps.pheezee.popup.AddPatientPopUpWindow;
import com.startoonlabs.apps.pheezee.popup.EditPopUpWindow;
import com.startoonlabs.apps.pheezee.popup.UploadImageDialog;
import com.startoonlabs.apps.pheezee.repository.MqttSyncRepository;
import com.startoonlabs.apps.pheezee.retrofit.GetDataService;
import com.startoonlabs.apps.pheezee.retrofit.RetrofitClientInstance;
import com.startoonlabs.apps.pheezee.room.Entity.MqttSync;
import com.startoonlabs.apps.pheezee.room.Entity.PhizioPatients;
import com.startoonlabs.apps.pheezee.room.PheezeeDatabase;
import com.startoonlabs.apps.pheezee.services.PicassoCircleTransformation;
import com.startoonlabs.apps.pheezee.services.Scanner;
import com.startoonlabs.apps.pheezee.utils.BatteryOperation;
import com.startoonlabs.apps.pheezee.utils.BitmapOperations;
import com.startoonlabs.apps.pheezee.utils.ByteToArrayOperations;
import com.startoonlabs.apps.pheezee.utils.NetworkOperations;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 */
public class PatientsView extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener, PatientsRecyclerViewAdapter.onItemClickListner, MqttSyncRepository.onServerResponse {
    String json_phizioemail = "";
    public static boolean deviceState = true, connectPressed = false, deviceBatteryUsbState = false,sessionStarted = false;
    public static int deviceBatteryPercent=0;
    public static boolean insideMonitor = false;
    private boolean insidePatientViewActivity = true;
    RelativeLayout rl_cap_view;
    Toast connected_disconnected_toast;
    ConstraintLayout cl_phizioProfileNavigation;
    //Caracteristic uuids
    //All the constant uuids are written here
    public static final UUID service1_uuid = UUID.fromString("909a1400-9693-4920-96e6-893c0157fedd");
    public static final UUID characteristic1_service1_uuid = UUID.fromString("909a1401-9693-4920-96e6-893c0157fedd");
    public static final UUID descriptor_characteristic1_service1_uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    public static final UUID battery_service1_uuid = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID battery_level_battery_service_characteristic_uuid = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");

    //Boolean for weather the bodypart window is present or not on the activity
    boolean f_bodypart_popup = false;

    static BluetoothGattCharacteristic mCharacteristic;
    BluetoothGattCharacteristic mCustomCharacteristic;
    static BluetoothGattDescriptor mBluetoothGattDescriptor;
    LinearLayout ll_device_and_bluetooth;

    //bluetooth and device connection state
    ImageView iv_bluetooth_connected, iv_bluetooth_disconnected, iv_device_connected, iv_device_disconnected, iv_sync_data,  iv_sync_not_available;

    View patientLayoutView;
    MyBottomSheetDialog myBottomSheetDialog;

    final CharSequence[] peezee_items = { "Scan Nearby Devices",
            "Qrcode Scan", "Cancel" };


    TextView email,fullName;
    public static ImageView ivBasicImage;
    //new
    JSONObject json_phizio = new JSONObject();
    //MQTT HELPER
    String mqtt_publish_phizio_addpatient = "phizio/addpatient";
    int REQUEST_ENABLE_BT = 1;
    final int REQUEST_ENABLE_BT_SCAN = 2;
    boolean isBleConnected;
    public boolean gattconnection_established = false;
    Intent to_scan_devices_activity;
    private PatientsRecyclerViewAdapter mAdapter;
    TextView tv_battery_percentage, tv_patient_view_add_patient;
    ProgressBar battery_bar;
    int backpressCount = 0;
    DrawerLayout drawer;
    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;
    AlertDialog.Builder builder;
    static BluetoothGatt bluetoothGatt;
    BluetoothDevice remoteDevice;
    BluetoothAdapter bluetoothAdapter;
    BluetoothManager mBluetoothManager;
    String MacAddress;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    Context context;
    LinearLayout patientTabLayout;
    ImageView iv_addPatient;
    LinearLayout ll_add_bluetooth, ll_add_device;
    RelativeLayout rl_battery_usb_state;
    RecyclerView mRecyclerView;
    ProgressDialog progress, deletepatient_progress;
    SearchView searchView;
    MqttSyncRepository repository ;
    Handler server_busy_handler;
    GetDataService getDataService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_view);
        context = this;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        repository = new MqttSyncRepository(getApplication());
        repository.setOnServerResponseListner(this);

        editor = sharedPref.edit();
        MacAddress = sharedPref.getString("deviceMacaddress", "");
        iv_addPatient = findViewById(R.id.home_iv_addPatient);
        rl_cap_view = findViewById(R.id.rl_cap_view);
        mRecyclerView = findViewById(R.id.patients_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        server_busy_handler = new Handler();
        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        //connected disconnected toast
        connected_disconnected_toast = Toast.makeText(getApplicationContext(),null,Toast.LENGTH_SHORT);
        connected_disconnected_toast.setGravity(Gravity.BOTTOM,30,40);
        mAdapter = new PatientsRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();

        //bluetooth and device status related
        iv_bluetooth_connected = findViewById(R.id.iv_bluetooth_connected);
        iv_bluetooth_disconnected = findViewById(R.id.iv_bluetooth_disconnected);
        iv_device_connected = findViewById(R.id.iv_device_connected);
        iv_device_disconnected = findViewById(R.id.iv_device_disconnected);
        ll_add_bluetooth = findViewById(R.id.ll_add_bluetooth);
        ll_add_device = findViewById(R.id.ll_add_device);
        tv_battery_percentage = findViewById(R.id.tv_battery_percent);
        battery_bar = findViewById(R.id.progress_battery_bar);
        tv_patient_view_add_patient = findViewById(R.id.tv_patient_view_add_patient);
        ll_device_and_bluetooth = findViewById(R.id.ll_device_and_bluetooth);
        rl_battery_usb_state = findViewById(R.id.rl_battery_usb_state);
        iv_sync_data = findViewById(R.id.iv_sync_data);
        iv_sync_not_available = findViewById(R.id.iv_sync_data_disabled);

        iv_sync_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkOperations.isNetworkAvailable(PatientsView.this)){
                    progress = new ProgressDialog(PatientsView.this);
                    progress.setMessage("Syncing session data to the server");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(true);
                    progress.setCancelable(false);
                    progress.show();
                    repository.syncDataToServer();
//                    new SyncDataAsync().execute();
                }

                else
                    NetworkOperations.networkError(PatientsView.this);
            }
        });



        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.nav_header_patients_view, navigationView);

        //external storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        //Getting previous patient data
        try {
            json_phizio = new JSONObject(sharedPref.getString("phiziodetails", ""));

            json_phizioemail = json_phizio.getString("phizioemail");
            Log.i("phiziodetails",json_phizioemail);
            Log.i("Patient View", json_phizio.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        searchView = findViewById(R.id.search_view);
        ivBasicImage =  view.findViewById(R.id.imageViewdp);
        email = view.findViewById(R.id.emailId);
        Picasso.get().load(Environment.getExternalStoragePublicDirectory("profilePic"))
                .placeholder(R.drawable.user_icon)
                .error(R.drawable.user_icon)
                .transform(new PicassoCircleTransformation())
                .into(ivBasicImage);

        try {
            if(!json_phizio.getString("phizioprofilepicurl").equals("empty")){

                String temp = json_phizio.getString("phizioprofilepicurl");
                Log.i("phiziopic",temp);
                temp = temp.replaceFirst("@", "%40");
                temp = "https://s3.ap-south-1.amazonaws.com/pheezee/"+temp;
                Picasso.get().load(temp)
                        .placeholder(R.drawable.user_icon)
                        .error(R.drawable.user_icon)
                        .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                        .transform(new PicassoCircleTransformation())
                        .into(ivBasicImage);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        fullName = view.findViewById(R.id.fullName);
        cl_phizioProfileNavigation = view.findViewById(R.id.phizioProfileNavigation);
        cl_phizioProfileNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientsView.this, PhizioProfile.class));
            }
        });
        try {
            email.setText(json_phizioemail);
            fullName.setText(json_phizio.getString("phizioname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ivBasicImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientsView.this, PhizioProfile.class));
            }
        });

        String macAddress;


        repository.getAllPatietns().observe(this, new Observer<List<PhizioPatients>>() {
            @Override
            public void onChanged(List<PhizioPatients> patients) {
                if(patients.size()>0) {
                    findViewById(R.id.noPatient).setVisibility(View.GONE);
                    findViewById(R.id.cl_recycler_view).setVisibility(View.VISIBLE);
                }
                else {
                    findViewById(R.id.cl_recycler_view).setVisibility(View.GONE);
                    findViewById(R.id.noPatient).setVisibility(View.VISIBLE);
                }

                Collections.reverse(patients);
                mAdapter.setNotes(patients);
            }
        });
        if (!(getIntent().getStringExtra("macAddress") == null || getIntent().getStringExtra("macAddress").equals(""))) {
                macAddress = getIntent().getStringExtra("macAddress");
                editor.putString("deviceMacaddress", macAddress);
                editor.apply();
        }
        if(ScanDevicesActivity.selectedDeviceMacAddress != null){
            macAddress = ScanDevicesActivity.selectedDeviceMacAddress;
            editor.putString("deviceMacaddress",macAddress);
            editor.apply();
            ScanDevicesActivity.selectedDeviceMacAddress = null;
        }

        mBluetoothManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = mBluetoothManager.getAdapter();
        BluetoothSingelton.getmInstance().setAdapter(bluetoothAdapter);
        //Add device and bluetooth turn on click events
        ll_add_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPheezeeDevice(v);
            }
        });
        ll_add_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBluetoothRequest();
            }
        });
        if (bluetoothAdapter==null || !bluetoothAdapter.isEnabled()) {
            bluetoothDisconnected();
            startBluetoothRequest();
        } else {
            bluetoothConnected();
            if (bluetoothGatt != null) {
//                bluetoothGatt.disconnect();
                Log.i("pressed",""+connectPressed);

                BluetoothGattSingleton.getmInstance().setAdapter(bluetoothGatt);
                gattconnection_established = false;
                Message message = Message.obtain();
                message.obj = "N/C";
                bleStatusHandler.sendMessage(message);
            }
            if(!Objects.requireNonNull(sharedPref.getString("deviceMacaddress", "")).equals("")) {
                Log.i("Enabled","true");
                remoteDevice = bluetoothAdapter.getRemoteDevice(sharedPref.getString("deviceMacaddress", "EC:24:B8:31:BD:67"));

                if(!sharedPref.getString("pressed","").equalsIgnoreCase("c") || bluetoothGatt==null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            bluetoothGatt = remoteDevice.connectGatt(PatientsView.this, true, callback);
                            if (bluetoothGatt != null) {
                                gattconnection_established = true;
                                BluetoothGattSingleton.getmInstance().setAdapter(bluetoothGatt);
                            }
                        }
                    });
                }
            }
        }

        iv_addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow();
            }
        });
        tv_patient_view_add_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow();
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothProfile.EXTRA_STATE);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(bluetoothReceiver, filter);


        //search option android
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });


        repository.getCount().observe(this,
                new Observer<Long>() {
                    @Override
                    public void onChanged(@Nullable Long mqttSyncs) {
                        try {
                            if (mqttSyncs != null && mqttSyncs > 0) {
                                iv_sync_not_available.setVisibility(View.GONE);
                                iv_sync_data.setVisibility(View.VISIBLE);
                            } else {
                                iv_sync_data.setVisibility(View.GONE);
                                iv_sync_not_available.setVisibility(View.VISIBLE);
                            }
                        }catch (NullPointerException e){
                            Log.i("Exception",e.getMessage());
                        }
                    }
                });


        mAdapter.setOnItemClickListner(this);
    }

    /**
     * Promts user to turn on bluetooth
     */
    private void startBluetoothRequest() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    /**
     * Bluetooth disconnected
     */
    private void bluetoothDisconnected() {
        iv_bluetooth_disconnected.setVisibility(View.VISIBLE);
        iv_bluetooth_connected.setVisibility(View.GONE);
        ll_add_bluetooth.setVisibility(View.VISIBLE);
        findViewById(R.id.ll_device_and_bluetooth).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_device_and_bluetooth).setBackgroundResource(R.drawable.drawable_background_connect_to_pheezee);
    }

    /**
     * Bluetooth connected
     */
    private void bluetoothConnected() {
        iv_bluetooth_disconnected.setVisibility(View.GONE);
        iv_bluetooth_connected.setVisibility(View.VISIBLE);
        ll_add_bluetooth.setVisibility(View.GONE);
        findViewById(R.id.ll_device_and_bluetooth).setBackgroundResource(R.drawable.drawable_background_turn_on_device);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothReceiver);
        if(bluetoothGatt!=null){
            disconnectDevice();
        }
        super.onDestroy();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            backpressCount++;
            if (backpressCount == 1) {
                Toast.makeText(PatientsView.this, "press again to close pheezee app", Toast.LENGTH_SHORT).show();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            backpressCount = 0;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
            if (backpressCount == 2) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                finishAffinity();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.patients_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
            if (id==R.id.pheeze_device_info){
                 Intent i = new Intent(PatientsView.this, DeviceInfoActivity.class);
                 i.putExtra("deviceMacAddress", sharedPref.getString("deviceMacaddress", ""));
                 startActivity(i);
            }

            else if(id==R.id.nav_home){

            }

            else if(id==R.id.nav_add_device){
                addPheezeeDevice(item.getActionView());
            }
            else if(id==R.id.nav_add_patient){
                iv_addPatient.performClick();
            }
            else if(id==R.id.nav_app_version){
                startActivity(new Intent(PatientsView.this,AppInfo.class));
            }
            else if (id == R.id.nav_logout) {
    //            if (GoogleSignIn.getLastSignedInAccount(this) != null)
    //                signOut();
    //            else
    //                AccessToken.setCurrentAccessToken(null);
                 editor.clear();
                 editor.commit();
                 repository.clearDatabase();
                 repository.deleteAllSync();
                 disconnectDevice();
                 startActivity(new Intent(this, LoginActivity.class));
                 finish();
            }

//        else if(id == R.id.ota_device) {
//            Intent intent;
//            if (!bleStatusTextView.getText().toString().equals("C")) {
//                Toast.makeText(context, "Please Connect to Pheeze.", Toast.LENGTH_SHORT).show();
//            } else {
//                intent = new Intent(this, DfuActivity.class);
//                intent.putExtra("deviceMacAddress", sharedPref.getString("deviceMacaddress", ""));
//                startActivity(intent);
//            }
//
//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void drawSideBar(View view) {
        drawer.openDrawer(GravityCompat.START);
    }


    /**
     *
     */
    private void initiatePopupWindow() {
        AddPatientPopUpWindow patientPopUpWindow = new AddPatientPopUpWindow(this,json_phizioemail);
        patientPopUpWindow.openAddPatientPopUpWindow();
        patientPopUpWindow.setOnClickListner(new AddPatientPopUpWindow.onClickListner() {
            @Override
            public void onAddPatientClickListner(PhizioPatients patient, PatientDetailsData data, boolean isvalid) {
                if(isvalid){
                    repository.insertPatient(patient);
                    new SendDataAsyncTask().execute(data);
                }
                else {
                    showToast("Invalid Input!!");
                }
            }
        });
    }


    /**
     * Bluetooth callback
     */
    public BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTING) {
                    refreshDeviceCache(gatt);
                    isBleConnected = true;
                    Message msg = Message.obtain();
                    msg.obj = "C..";
                    Log.i("connected","true");
                    deviceState = true;
                    bleStatusHandler.sendMessage(msg);
                } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                    refreshDeviceCache(gatt);
                    Message msg = Message.obtain();
                    isBleConnected = true;
                    msg.obj = "C";
                    deviceState=true;
                    Log.i("connected", "connected");
                    bleStatusHandler.sendMessage(msg);
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                    Message msg = Message.obtain();
                    isBleConnected = false;
                    msg.obj = "N/C";
                    bleStatusHandler.sendMessage(msg);
                }
                else {
                    Message msg = Message.obtain();
                    isBleConnected = false;
                    msg.obj = "N/C";
                    bleStatusHandler.sendMessage(msg);
                }
            }
            if(status == BluetoothGatt.GATT_FAILURE){
                Message msg = Message.obtain();
                msg.obj = "N/C";
                bleStatusHandler.sendMessage(msg);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) throws NullPointerException {
            BluetoothGattCharacteristic characteristic = gatt.getService(service1_uuid).getCharacteristic(characteristic1_service1_uuid);
            Log.i("TEST", "INSIDE IF");
            bluetoothGatt = gatt;
            if(characteristic!=null)
                mCustomCharacteristic = characteristic;
            gatt.setCharacteristicNotification(mCustomCharacteristic,true);
            mBluetoothGattDescriptor = mCustomCharacteristic.getDescriptor(descriptor_characteristic1_service1_uuid);
            mCharacteristic = gatt.getService(battery_service1_uuid).getCharacteristic(battery_level_battery_service_characteristic_uuid);
            new MyAsync().execute();
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.i("inside","descritor");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Message msg = new Message();
            if(characteristic.getUuid().equals(battery_level_battery_service_characteristic_uuid)){
                byte b[] = characteristic.getValue();
                int battery  = b[0];
                int usb_state = b[1];
                if(usb_state==1) {
                    if(findViewById(R.id.rl_battery_usb_state).getVisibility()==View.GONE) {
                        msg.obj = "c";
                        batteryUsbState.sendMessage(msg);
                    }
                    Log.i("battery notif read","connected");
                }
                else if(usb_state==0) {
                    msg.obj = "nc";
                    batteryUsbState.sendMessage(msg);
                    Log.i("battery notif read","disconnected");
                }

                Log.i("battery changed to",battery+"");
                Message message = new Message();
                message.obj = battery+"";
                batteryStatus.sendMessage(message);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Message msg = new Message();
            Log.i("chatacteristic","read");
                if(characteristic.getUuid()==mCustomCharacteristic.getUuid()) {
                    byte info_packet[] = characteristic.getValue();
                    Log.i("inside", "inside");
                    int battery = info_packet[11] & 0xFF;
                    int device_status = info_packet[12] & 0xFF;
                    int device_usb_state = info_packet[13] & 0xFF;
                    if(device_usb_state==1) {
                        msg.obj = "c";
                        batteryUsbState.sendMessage(msg);
                    }
                    else if(device_status==0) {
                        msg.obj = "nc";
                        batteryUsbState.sendMessage(msg);
                    }
                    Log.i("device", battery + " " + device_status + " " + device_usb_state);
                    bluetoothGatt.readCharacteristic(mCharacteristic);
                }

                //remove comments later now.
                else if(characteristic.getUuid()==mCharacteristic.getUuid()) {
                    byte b[] = characteristic.getValue();
                    int battery  = b[0];
                    int usb_state = b[1];
                    if(usb_state==1) {
                        msg.obj = "c";
                        batteryUsbState.sendMessage(msg);
                        Log.i("battery service read","connected");
                    }
                    else if(usb_state==0) {
                        msg.obj = "nc";
                        batteryUsbState.sendMessage(msg);
                        Log.i("battery service read","disconnected");
                    }
                    Log.i("battery",battery+" read");
                    Message message = new Message();
                    message.obj = battery+"";
                    batteryStatus.sendMessage(message);

                    gatt.setCharacteristicNotification(mCharacteristic, true);
                    mBluetoothGattDescriptor = mCharacteristic.getDescriptor(descriptor_characteristic1_service1_uuid);
                    mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    bluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);
                }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i("chatacteristic","written");
            bluetoothGatt.readCharacteristic(mCustomCharacteristic);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothProfile.EXTRA_STATE);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothReceiver, filter);
        insidePatientViewActivity = true;
        if(bluetoothAdapter==null || !bluetoothAdapter.isEnabled()){
            Message message = Message.obtain();
            message.obj = "N/C";
            bleStatusHandler.sendMessage(message);
        }
        try {
            json_phizio = new JSONObject(sharedPref.getString("phiziodetails", ""));
            email.setText(json_phizioemail);
            fullName.setText(json_phizio.getString("phizioname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        insideMonitor=false;
        insidePatientViewActivity = false;
        if(bluetoothAdapter==null || !bluetoothAdapter.isEnabled()){
            Message message = Message.obtain();
            message.obj = "N/C";
            bleStatusHandler.sendMessage(message);
        }
        if(remoteDevice == null){
            Message message = Message.obtain();
            message.obj = "N/C";
            bleStatusHandler.sendMessage(message);
        }
    }

    @Override
    protected void onRestart() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothProfile.EXTRA_STATE);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothReceiver, filter);
        if(bluetoothAdapter==null || !bluetoothAdapter.isEnabled()){
            Message message = Message.obtain();
            message.obj = "N/C";
            bleStatusHandler.sendMessage(message);
        }
        if(!deviceState){
            disconnectDevice();
            pheezeeDisconnected();
        }
        super.onRestart();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    /**
     *
     * @param patient
     */
    public void editPopUpWindow(PhizioPatients patient){
        EditPopUpWindow popUpWindow = new EditPopUpWindow(this,patient,json_phizioemail);
        popUpWindow.openEditPopUpWindow();
        popUpWindow.setOnClickListner(new EditPopUpWindow.onClickListner() {
            @Override
            public void onAddClickListner(PhizioPatients patients, PatientDetailsData data, boolean isvalid) {
                if(isvalid){
                    deletepatient_progress = new ProgressDialog(PatientsView.this);
                    deletepatient_progress.setTitle("Updating patient details, please wait");
                    deletepatient_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    deletepatient_progress.setIndeterminate(true);
                    deletepatient_progress.show();
                    repository.updatePatientDetailsServer(patient,data);
                }
                else {
                    showToast("Invalid Input!!");
                }
            }
        });
    }

    /**
     * Bluetooth device status handler
     */
    @SuppressLint("HandlerLeak")
    public final Handler bleStatusHandler = new Handler() {
        public void handleMessage(Message msg) {
            String status = (String) msg.obj;
            if(status.equalsIgnoreCase("N/C"))
                pheezeeDisconnected();
            else if(status.equalsIgnoreCase("C")) {
                pheezeeConnected();
                showToast("Device Connected");
            }
        }
    };

    /**
     * Called when device connects to update the view
     */
    private void pheezeeConnected() {
        iv_device_disconnected.setVisibility(View.GONE);
        iv_device_connected.setVisibility(View.VISIBLE);
        ll_add_device.setVisibility(View.GONE);
        ll_device_and_bluetooth.setVisibility(View.GONE);
        Drawable drawable = getResources().getDrawable(R.drawable.drawable_progress_battery);
        battery_bar.setProgressDrawable(drawable);
        @SuppressLint("ResourceAsColor") Drawable drawable_cap = new ColorDrawable(R.color.battery_gray);
        rl_cap_view.setBackground(drawable_cap);
    }

    /**
     * called when device disconnected to update the view
     */
    private void pheezeeDisconnected() {
        Log.i("Inside disconnect", "Device Disconnected");
        iv_device_connected.setVisibility(View.GONE);
        iv_device_disconnected.setVisibility(View.VISIBLE);
        ll_add_device.setVisibility(View.VISIBLE);
        if(iv_bluetooth_connected.getVisibility()==View.VISIBLE)
            ll_device_and_bluetooth.setBackgroundResource(R.drawable.drawable_background_turn_on_device);
        else
            ll_device_and_bluetooth.setBackgroundResource(R.drawable.drawable_background_connect_to_pheezee);
        ll_device_and_bluetooth.setVisibility(View.VISIBLE);
        Drawable drawable = getResources().getDrawable(R.drawable.drawable_progress_battery_disconnected);
        battery_bar.setProgressDrawable(drawable);
        @SuppressLint("ResourceAsColor") Drawable drawable_cap = new ColorDrawable(getResources().getColor(R.color.red));
        rl_cap_view.setBackground(drawable_cap);
        rl_battery_usb_state.setVisibility(View.GONE);
        Log.i("red color","red");
    }

    /**
     * Receiver for bluetooth connectivity and device disconnection
     */
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
                connected_disconnected_toast.setText("The device got disconnected..");
                connected_disconnected_toast.show();
                if(sessionStarted && insideMonitor){
                    sessionStarted=false;
                    deviceState=false;
                }
                if(sharedPref.getBoolean("isLoggedIn",false)==false)
                    finish();

                if(sharedPref.getString("pressed", "").equals("c")){
                    if(bluetoothGatt==null){
                    if (bluetoothAdapter==null || !bluetoothAdapter.isEnabled()) {
                        bluetoothDisconnected();
                        startBluetoothRequest();
                    }
                    else {
                        if(!Objects.requireNonNull(sharedPref.getString("deviceMacaddress", "")).equals("")) {
                            Log.i("Enabled","true");
                            remoteDevice = bluetoothAdapter.getRemoteDevice(sharedPref.getString("deviceMacaddress", "EC:24:B8:31:BD:67"));


                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    bluetoothGatt = remoteDevice.connectGatt(PatientsView.this, true, callback);
                                    if(bluetoothGatt!=null) {
                                        gattconnection_established = true;
                                        BluetoothGattSingleton.getmInstance().setAdapter(bluetoothGatt);
                                    }
                                }
                            });
                        }
                    }
                }
                    editor = sharedPref.edit();
                    editor.putString("pressed","");
                    editor.commit();
                }
                else {
                    isBleConnected = false;
                    Message message = Message.obtain();
                    message.obj = "N/C";
                    bleStatusHandler.sendMessage(message);
                    if (deviceState && !insidePatientViewActivity) {       //The device state is related to the device info screen if the user forcefully disconnects and forget the device
                        Intent i = getIntent();
                        finish();
                        startActivity(i);
                    }
                }
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    bluetoothConnected();
                    if(!MacAddress.equals("")) {
                        remoteDevice = bluetoothAdapter.getRemoteDevice(MacAddress);
                        if (remoteDevice != null) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    bluetoothGatt = remoteDevice.connectGatt(PatientsView.this, true, callback);
                                }
                            });
                        }
                    }
                }
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    bluetoothDisconnected();
                    Toast.makeText(PatientsView.this, "Bluetooth turned off", Toast.LENGTH_LONG).show();
                    if(bluetoothGatt!=null){
                        bluetoothGatt.disconnect();
                        bluetoothGatt.close();
                    }
                    Message message = Message.obtain();
                    message.obj = "N/C";
                    bleStatusHandler.sendMessage(message);
                }
            }

        }
    };

    /**
     *
     * @param patient
     */
    public void startSession(PhizioPatients patient) {
        final Intent intent = new Intent(PatientsView.this, BodyPartSelection.class);
        intent.putExtra("deviceMacAddress", sharedPref.getString("deviceMacaddress", ""));
        intent.putExtra("patientId", patient.getPatientid());
        intent.putExtra("patientName", patient.getPatientname());
        if (Objects.requireNonNull(sharedPref.getString("deviceMacaddress", "")).equals("")) {
            Toast.makeText(this, "First add pheezee to your application", Toast.LENGTH_LONG).show();
        } else if (!(iv_device_connected.getVisibility()==View.VISIBLE)  ) {
            Toast.makeText(this, "Make sure that the pheezee is on", Toast.LENGTH_LONG).show();
        }
        else {
            String message = BatteryOperation.getDialogMessageForLowBattery(deviceBatteryPercent,this);
            if(!message.equalsIgnoreCase("c")){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Battery Low");
                builder.setMessage(message);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                    }
                });
                builder.show();
            }
            else
                startActivity(intent);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothProfile.EXTRA_STATE);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothReceiver, filter);
    }

    /**
     * Disconnects the device
     */
    public static void disconnectDevice() {
        if(bluetoothGatt==null){
            Log.i("inside","null");
            editor = sharedPref.edit();
            editor.putString("pressed","");
            editor.commit();
            return;
        }
        if(mCharacteristic!=null && mBluetoothGattDescriptor!=null) {
            Log.i("inside","Characteristics");
            bluetoothGatt.setCharacteristicNotification(mCharacteristic, false);
            mBluetoothGattDescriptor = mCharacteristic.getDescriptor(descriptor_characteristic1_service1_uuid);
            mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);
        }
        bluetoothGatt.close();
        Log.i("bluetooth gatt closed","closed");
        bluetoothGatt = null;
        deviceState=false;
    }

    /**
     * Updates the chache of the device connected from the blutooth as it will not discover new services.
     * @param gatt
     * @return
     */
    private boolean refreshDeviceCache(BluetoothGatt gatt){
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                return (Boolean) localMethod.invoke(localBluetoothGatt, new Object[0]);
            }
        }
        catch (Exception localException) {
            Log.e("EXCEPTION", "An exception occured while refreshing device");
        }
        return false;
    }

    /**
     * Updating the image of patient
     * @param view
     */
    public void chooseImageUpdateAction(final View view){
        patientLayoutView = view;
        UploadImageDialog dialog = new UploadImageDialog(this);
        dialog.showDialog();
    }

    /**
     * Opens the builer for different device connecting techniques
     * @param view
     */
    public void addPheezeeDevice(View view){
        builder = new AlertDialog.Builder(PatientsView.this);
        builder.setTitle("Add Pheezee Device!");
        builder.setItems(peezee_items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (peezee_items[item].equals("Scan Nearby Devices")) {
                    to_scan_devices_activity = new Intent(PatientsView.this, ScanDevicesActivity.class);
                    if (bluetoothAdapter==null || !bluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT_SCAN);
                    }
                    else
                        startActivity(to_scan_devices_activity);
                }  else if (peezee_items[item].equals("Qrcode Scan")) {
                    startActivity(new Intent(PatientsView.this, Scanner.class));
                }
                else{
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * For photo editing of patient
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 1
        if(requestCode==5){
            if(resultCode == RESULT_OK){
                ImageView imageView_patientpic = patientLayoutView.findViewById(R.id.patientProfilePic);
                patientTabLayout= (LinearLayout) (patientLayoutView).getParent().getParent();
                patientTabLayout = (LinearLayout) patientTabLayout.getChildAt(1);
                Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                photo = BitmapOperations.getResizedBitmap(photo,128);
                imageView_patientpic.setImageBitmap(photo);
                TextView tv_patientId = (TextView) patientTabLayout.getChildAt(1);
                if(NetworkOperations.isNetworkAvailable(this))
                    repository.uploadPatientImage(tv_patientId.getText().toString().substring(4).replaceAll("\\s+",""),json_phizioemail,photo);
                else
                    NetworkOperations.networkError(this);
            }
        }
        if(requestCode==6){
            ImageView imageView_patientpic = patientLayoutView.findViewById(R.id.patientProfilePic);
            patientTabLayout= (LinearLayout) (patientLayoutView).getParent().getParent();
            patientTabLayout = (LinearLayout) patientTabLayout.getChildAt(1);
            if(data!=null) {
                Uri selectedImage = data.getData();
                Glide.with(this).load(selectedImage).apply(new RequestOptions().centerCrop()).into(imageView_patientpic);
                TextView tv_patientId = (TextView) patientTabLayout.getChildAt(1);
                Bitmap photo = null;
                try {
                    photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    photo = BitmapOperations.getResizedBitmap(photo,128);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(NetworkOperations.isNetworkAvailable(this))
                    repository.uploadPatientImage(tv_patientId.getText().toString().substring(4).replaceAll("\\s+",""),json_phizioemail,photo);
                else
                    NetworkOperations.networkError(this);
            }
        }
        if(requestCode==2){
            if(resultCode!=0){
                startActivity(new Intent(this,ScanDevicesActivity.class));
            }
        }
    }


    /**
     * Opens the bottom bar sheet
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    public void openOpionsPopupWindow(View view, PhizioPatients patient){
        Bitmap patientpic_bitmap=null;
        patientTabLayout= (LinearLayout) (view).getParent();
        LinearLayout iv_layout = (LinearLayout)patientTabLayout.getChildAt(0);

        ImageView iv_patient_pic = iv_layout.findViewById(R.id.patientProfilePic);

        if(!(iv_patient_pic.getDrawable() ==null)) {
            try {
                patientpic_bitmap = ((BitmapDrawable) iv_patient_pic.getDrawable()).getBitmap();
            }
            catch (ClassCastException e){
                patientpic_bitmap = null;
            }
        }
        myBottomSheetDialog = new MyBottomSheetDialog(patientpic_bitmap, patient);
        myBottomSheetDialog.show(getSupportFragmentManager(),"MyBottomSheet");

    }


    public void editThePatientDetails(PhizioPatients patient){
        myBottomSheetDialog.dismiss();
        if(NetworkOperations.isNetworkAvailable(PatientsView.this))
            editPopUpWindow( patient);
        else {
            NetworkOperations.networkError(PatientsView.this);
        }
    }
    /**
     *
     * @param patientid
     * @param patientname
     */
    public void openReportActivity(String patientid, String patientname){
        if(NetworkOperations.isNetworkAvailable(PatientsView.this)){
            Intent mmt_intent = new Intent(PatientsView.this, SessionReportActivity.class);
            mmt_intent.putExtra("patientid", patientid);
            mmt_intent.putExtra("patientname", patientname);
            try {
                mmt_intent.putExtra("phizioemail", json_phizio.getString("phizioemail"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(mmt_intent);
            myBottomSheetDialog.dismiss();
        }
        else {
            NetworkOperations.networkError(PatientsView.this);
        }
    }

    /**
     *
     * @param patient
     */
    public void updatePatientStatus(PhizioPatients patient){
        myBottomSheetDialog.dismiss();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Archive Patient");
        builder.setMessage("Are you sure you want to archive the patient?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(NetworkOperations.isNetworkAvailable(PatientsView.this)){
                    patient.setStatus("inactive");
                    deletepatient_progress = new ProgressDialog(PatientsView.this);
                    deletepatient_progress.setTitle("Updating patient status, please wait");
                    deletepatient_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    deletepatient_progress.setIndeterminate(true);
                    deletepatient_progress.show();
                    PatientStatusData data = new PatientStatusData(json_phizioemail,patient.getPatientid(),patient.getStatus());
                    repository.updatePatientStatusServer(patient,data);
                }
                else {
                    NetworkOperations.networkError(PatientsView.this);
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    /**
     *
     * @param patient
     */
    public void deletePatient(PhizioPatients patient){
        myBottomSheetDialog.dismiss();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Patient");
        builder.setMessage("Are you sure you want to delete the patient?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(NetworkOperations.isNetworkAvailable(PatientsView.this)){
                    deletepatient_progress = new ProgressDialog(PatientsView.this);
                    deletepatient_progress.setTitle("Deleting patient, please wait");
                    deletepatient_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    deletepatient_progress.setIndeterminate(true);
                    deletepatient_progress.show();
                    DeletePatientData data = new DeletePatientData(json_phizioemail,patient.getPatientid());
                    repository.deletePatientFromServer(data,patient);
                }
                else {
                    NetworkOperations.networkError(PatientsView.this);
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public void onItemClick(PhizioPatients patient, View view) {
        openOpionsPopupWindow(view,patient);
    }

    @Override
    public void onStartSessionClickListner(PhizioPatients patient) {
        startSession(patient);
    }

    @Override
    public void onDeletePateintResponse(boolean response) {
        if(response){
            if(deletepatient_progress!=null){
                deletepatient_progress.dismiss();
                showToast("Patient Deleted");
            }
        }
        else {
            showToast("Please try again");
        }
    }

    @Override
    public void onUpdatePatientDetailsResponse(boolean response) {
        if(response){
            if(deletepatient_progress!=null){
                deletepatient_progress.dismiss();
                showToast("Patient details updated");
            }
        }else {
            showToast("Please try again");
        }
    }

    @Override
    public void onUpdatePatientStatusResponse(boolean response) {
        if(response){
            if(deletepatient_progress!=null){
                deletepatient_progress.dismiss();
                showToast("Patient Staus Updated");
            }
        }
        else {
            showToast("Please try again");
        }
    }

    @Override
    public void onSyncComplete(boolean response, String message) {
        progress.dismiss();
        showToast(message);
    }

    private class MyAsync extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            byte b[] = ByteToArrayOperations.hexStringToByteArray("AA02");
            if(send(b)){
                Log.i("SENDING","MESSAGE SENT");
            }
            else {
                Log.i("SENDING","UNSUCCESSFULL");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    }

    /**
     * sends the data to the device by writing into a characteristic
     * @param data
     * @return
     */
    public boolean send(byte[] data) {

        if (bluetoothGatt == null ) {
            return false;
        }
        if (mCustomCharacteristic == null) {
            return false;
        }

        BluetoothGattService service = bluetoothGatt.getService(service1_uuid);

        if(service==null){
            if (mCustomCharacteristic == null) {
                return false;
            }
        }
        if(characteristic1_service1_uuid.equals(mCustomCharacteristic.getUuid())){
            Log.i("TRUE", "TRUE");
        }


        mCustomCharacteristic.setValue(data);

        return bluetoothGatt.writeCharacteristic(mCustomCharacteristic);
    }

    /**
     * This handler handles the battery status and updates the bars of the battery symbol.
     */
    @SuppressLint("HandlerLeak")
    public final Handler batteryStatus = new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            Log.i("Battery",msg.obj.toString());
            tv_battery_percentage.setText(msg.obj.toString().concat("%"));
            Log.i("Battery Percentage",msg.obj.toString());
            deviceBatteryPercent = Integer.parseInt(msg.obj.toString());
            int percent = BatteryOperation.convertBatteryToCell(deviceBatteryPercent);
            Log.i("After percent Formulae",percent+"");
            if(deviceBatteryPercent<15) {
                Drawable drawable = getResources().getDrawable(R.drawable.drawable_progress_battery_low);
                battery_bar.setProgressDrawable(drawable);
            }
            else {
                Drawable drawable = getResources().getDrawable(R.drawable.drawable_progress_battery);
                battery_bar.setProgressDrawable(drawable);
            }
            battery_bar.setProgress(percent);
        }
    };

    /**
     * This handler handels the batery state view change
     */
    @SuppressLint("HandlerLeak")
    public final Handler batteryUsbState = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj.toString().equalsIgnoreCase("c"))
                rl_battery_usb_state.setVisibility(View.VISIBLE);
            else
                rl_battery_usb_state.setVisibility(View.GONE);
        }
    };


    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }



    /**
     * Stores the topic and message in database and sends to the server if internet is available.
     */
    public class SendDataAsyncTask extends AsyncTask<PatientDetailsData,Void,JSONObject>{

        @Override
        protected JSONObject doInBackground(PatientDetailsData... patientDetailsData) {
            PheezeeDatabase database = PheezeeDatabase.getInstance(PatientsView.this);
            JSONObject object = null;
            try {
                Gson gson = new GsonBuilder().create();
                String json = gson.toJson(patientDetailsData[0]);
                object = new JSONObject(json);
                MqttSync mqttSync = new MqttSync(mqtt_publish_phizio_addpatient,object.toString());
                object.put("id",database.mqttSyncDao().insert(mqttSync));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return object;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if(NetworkOperations.isNetworkAvailable(PatientsView.this)) {
                Gson gson = new Gson();
                AddPatientData data = gson.fromJson(jsonObject.toString(),AddPatientData.class);
                Call<ResponseData> add_patient_call = getDataService.addPatient(data);
                add_patient_call.enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        if(response.code()==200) {
                            ResponseData responseData = response.body();
                            Log.i("Response",responseData.getResponse());
                            if(responseData.getResponse().equalsIgnoreCase("inserted"))
                                repository.deleteParticular(responseData.getId());
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        Log.i("parseerror",t.getMessage());
                    }
                });
            }
        }
    }



}
