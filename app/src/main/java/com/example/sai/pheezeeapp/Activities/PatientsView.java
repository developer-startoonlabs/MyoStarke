package com.example.sai.pheezeeapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
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
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sai.pheezeeapp.Classes.BluetoothGattSingleton;
import com.example.sai.pheezeeapp.Classes.BluetoothSingelton;
import com.example.sai.pheezeeapp.Classes.MyBottomSheetDialog;
import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.dashboard.DashboardActivity;
import com.example.sai.pheezeeapp.patientsRecyclerView.PatientsListData;
import com.example.sai.pheezeeapp.patientsRecyclerView.PatientsRecyclerViewAdapter;
import com.example.sai.pheezeeapp.services.MqttHelper;
import com.example.sai.pheezeeapp.services.PicassoCircleTransformation;
import com.example.sai.pheezeeapp.services.Scanner;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.squareup.picasso.Picasso;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class PatientsView extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    //Caracteristic uuids
    //All the constant uuids are written here
    public static final UUID service1_uuid = UUID.fromString("909a1400-9693-4920-96e6-893c0157fedd");
    public static final UUID characteristic1_service1_uuid = UUID.fromString("909a1401-9693-4920-96e6-893c0157fedd");
    public static final UUID descriptor_characteristic1_service1_uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    public static final UUID battery_service1_uuid = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID battery_level_battery_service_characteristic_uuid = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");

    //Boolean for weather the bodypart window is present or not on the activity
    boolean f_bodypart_popup = false;

    BluetoothGattCharacteristic mCharacteristic;
    BluetoothGattCharacteristic mCustomCharacteristic;
    BluetoothGattDescriptor mBluetoothGattDescriptor;



    PopupWindow bodyPartLayoutWndow;
    PopupWindow bodyPartLayout;
    View patientLayoutView;
    MyBottomSheetDialog myBottomSheetDialog;

    //For Alert Dialog
    final CharSequence[] items = { "Take Photo", "Choose from Library",
            "Cancel" };


    TextView email,fullName;
    public static ImageView ivBasicImage;
    ImageView bluetooth_image;
    //new
    JSONObject json_phizio = new JSONObject();
    //MQTT HELPER

    MqttHelper mqttHelper;
    String mqtt_publish_phizio_addpatient = "phizio/addpatient";
    String mqtt_publish_phizio_deletepatient = "phizio/deletepatient";
    String mqtt_publish_phizio_update_patientdetails = "phizio/updatepatientdetails";

    String mqtt_publish_phizio_patient_profilepic = "phizio/update/patientProfilePic";

    String mqtt_get_profile_pic_response = "phizio/getprofilepic/response";

    String mqtt_update_patient_status = "phizio/update/patientStatus";



    String mqtt_subs_phizio_addpatient_response = "phizio/addpatient/response";
    String mqtt_phizio_profilepic_change_response = "phizio/profilepic/upload/response";
    //Request action intents

    int REQUEST_ENABLE_BT = 1;
    final int REQUEST_ENABLE_BT_SCAN = 2;

    boolean isBleConnected;
    public boolean gattconnection_established = false;
    //All the intents

    Intent to_scan_devices_activity;

    private List<PatientsListData> mdataset = new ArrayList<>();
    private PatientsRecyclerViewAdapter mAdapter;
    EditText patientNameField;
    TextView tv_battery_percentage;
    PopupWindow pw;
    int backpressCount = 0;
    DrawerLayout drawer;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    JSONArray jsonData;
    AlertDialog.Builder builder;
    public static TextView bleStatusTextView;
    BluetoothGatt bluetoothGatt;
    BluetoothDevice remoteDevice;
    BluetoothAdapter bluetoothAdapter;
    BluetoothManager mBluetoothManager;
    String MacAddress;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    Context context;
    FloatingActionButton addPatientsBtn;
    LinearLayout patientTabLayout;
    private LinearLayout patientLayout;

    RecyclerView mRecyclerView;

    android.support.v7.widget.SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_view);
        context = this;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
        MacAddress = sharedPref.getString("deviceMacaddress", "");
        addPatientsBtn = findViewById(R.id.addPatients);
        mRecyclerView = findViewById(R.id.patients_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PatientsRecyclerViewAdapter(mdataset,PatientsView.this);
        mRecyclerView.setAdapter(mAdapter);
        bleStatusTextView = findViewById(R.id.bleStatus);
        tv_battery_percentage = findViewById(R.id.tv_battery_percentage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.nav_header_patients_view, navigationView);

        Log.i("shared pref",sharedPref.getString("sync_emg_session",""));

        //external storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        //Getting previous patient data

        try {
            json_phizio = new JSONObject(sharedPref.getString("phiziodetails", ""));
            Log.i("Patient View", json_phizio.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        bluetooth_image = findViewById(R.id.bluetooth_imamge);
        searchView = findViewById(R.id.search_view);
        ivBasicImage =  view.findViewById(R.id.imageViewdp);
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
                Log.i("inside check",temp);
                Picasso.get().load(temp)
                        .placeholder(R.drawable.user_icon)
                        .error(R.drawable.user_icon)
                        .transform(new PicassoCircleTransformation())
                        .into(ivBasicImage);

            }
            email = view.findViewById(R.id.emailId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fullName = view.findViewById(R.id.fullName);
        try {
            email.setText(json_phizio.getString("phizioemail"));
            fullName.setText(json_phizio.getString("phizioname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ivBasicImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ":LLOO", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PatientsView.this, PhizioProfile.class));
            }
        });

        String macAddress;


        //MQTT HELPER
        mqttHelper  = new MqttHelper(this,"patientsview");



        try {
            if (!sharedPref.getString("phiziodetails", "").equals("")) {

                JSONObject object = new JSONObject(sharedPref.getString("phiziodetails", ""));
                JSONArray array = new JSONArray(object.getString("phiziopatients"));
                Log.i("Patients View", "array"+array);
                if(array.length()>0) {
                    findViewById(R.id.noPatient).setVisibility(View.GONE);
                    pushJsonData(array);
                }
            }
            if (!(getIntent().getStringExtra("macAddress") == null || getIntent().getStringExtra("macAddress").equals(""))) {
                macAddress = getIntent().getStringExtra("macAddress");
                editor.putString("deviceMacaddress", macAddress);
                editor.apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(ScanDevicesActivity.selectedDeviceMacAddress != null){
            macAddress = ScanDevicesActivity.selectedDeviceMacAddress;
            editor.putString("deviceMacaddress",macAddress);
            editor.apply();
        }
        mBluetoothManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = mBluetoothManager.getAdapter();
        BluetoothSingelton.getmInstance().setAdapter(bluetoothAdapter);
        if (bluetoothAdapter==null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (bluetoothGatt != null) {
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
                BluetoothGattSingleton.getmInstance().setAdapter(bluetoothGatt);
                gattconnection_established = false;
                Message message = Message.obtain();
                message.obj = "N/C";
                bleStatusHandler.sendMessage(message);
            }
            if(!sharedPref.getString("deviceMacaddress", "").equals("")) {
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

        addPatientsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatePopupWindow(view);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothProfile.EXTRA_STATE);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothReceiver, filter);


        //mqttcallback
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message){
                Log.i(topic,message.toString());
                 if(topic.equals(mqtt_get_profile_pic_response)){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(message.getPayload(), 0, message.getPayload().length);
                    ivBasicImage.setImageBitmap(bitmap);
                }
                else if(topic.equals(mqtt_subs_phizio_addpatient_response)){
                     if(message.toString().equals("inserted")){
                          Log.i("message emg",message.toString());
                          editor = sharedPref.edit();
                          editor.putString("sync_emg_session","");
                          editor.apply();
                     }
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


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

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothReceiver);
            mqttHelper.mqttAndroidClient.unregisterResources();
            mqttHelper.mqttAndroidClient.close();
        super.onDestroy();
    }

    public void pushJsonData(JSONArray data) {
        mdataset.clear();
        if (data.length() > 0) {
            PatientsListData patientsList;
            for (int i = data.length() - 1; i >= 0; i--) {
                try {
                    if(!data.getJSONObject(i).has("status")  || data.getJSONObject(i).getString("status").equals("active")) {
                        patientsList = new PatientsListData(data.getJSONObject(i).getString("patientname"), data.getJSONObject(i).getString("patientid"), data.getJSONObject(i).getString("patientid"));
                        mdataset.add(patientsList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



        }

        if(mdataset.size()>0)
            findViewById(R.id.noPatient).setVisibility(View.GONE);
        else {
            findViewById(R.id.noPatient).setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();

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
            if(f_bodypart_popup){ bodyPartLayout.dismiss(); f_bodypart_popup = false; }
           else {
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
                    finish();
                }
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
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
//                PopupMenu popupMenu = new PopupMenu(PatientsView.this, view, Gravity.CENTER);
//                popupMenu.setOnMenuItemClickListener(PatientsView.this);
//                popupMenu.inflate(R.menu.popupmenu);
//                popupMenu.show();
            }
            else if(id==R.id.nav_add_patient){

            }
            else if(id==R.id.nav_app_version){

            }
            else if (id == R.id.nav_logout) {
    //            if (GoogleSignIn.getLastSignedInAccount(this) != null)
    //                signOut();
    //            else
    //                AccessToken.setCurrentAccessToken(null);
                 editor.clear();
                 editor.commit();

                 startActivity(new Intent(this, LoginActivity.class));
                 finish();
             }

//        } else if (id == R.id.nav_share) {
//             File file = new File(Environment.getExternalStorageDirectory() + "/ca.pdf");
//             Uri pdfURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", file);
//
//             Intent i = new Intent();
//             i.setAction(Intent.ACTION_SEND);
//             i.putExtra(Intent.EXTRA_STREAM,pdfURI);
//             i.setType("application/pdf");
//             startActivity(Intent.createChooser(i, "share pdf"));
//        }

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



    private void initiatePopupWindow(View v) {
        try {
            final JSONObject jsonObject = new JSONObject();
            final MqttMessage mqttMessage = new MqttMessage();
            jsonData = new JSONArray();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            LayoutInflater inflater = (LayoutInflater) PatientsView.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.popup, null);

            pw = new PopupWindow(layout);
            pw.setHeight(height - 400);
            pw.setWidth(width - 100);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                pw.setElevation(10);
            }
            pw.setTouchable(true);
            pw.setOutsideTouchable(true);
            pw.setContentView(layout);
            pw.setFocusable(true);
            pw.showAtLocation(v, Gravity.CENTER, 0, 0);

            final TextView patientName = layout.findViewById(R.id.patientName);
            final TextView patientId = layout.findViewById(R.id.patientId);

            final String todaysDate = dateInMmDdYyyy();


            Log.i("Date", todaysDate);

            Button addBtn = layout.findViewById(R.id.addBtn);
            Button cancelBtn = layout.findViewById(R.id.cancelBtn);

            if (!sharedPref.getString("phiziodetails", "").equals("")) {
                Log.i("Patient View","Inside");
                jsonData = new JSONArray(json_phizio.getString("phiziopatients"));
                if(jsonData.length()>0) {
                    for (int i = 0; i < jsonData.length(); i++) {
                        if (jsonData.getJSONObject(i).get("patientid").equals(patientId.getText().toString())) {
                            Toast.makeText(PatientsView.this,
                                    "Patient with same patient id is already exsists at patient name " + jsonData.getJSONObject(i).get("patientName"),
                                    Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                }
            }
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((!patientName.getText().toString().equals("")) && (!patientId.getText().toString().equals(""))) {
                        if (!sharedPref.getString("phiziodetails", "").equals("")) {
                            if(jsonData.length()>0) {
                                try {
                                    for (int i = 0; i < jsonData.length(); i++) {
                                        if (jsonData.getJSONObject(i).get("patientid").equals(patientId.getText().toString())) {
                                            Toast.makeText(PatientsView.this,
                                                    "Patient with same patient id is already exsists for patient name " + jsonData.getJSONObject(i).get("patientname"),
                                                    Toast.LENGTH_LONG).show();
                                            Log.i("ALREDY PRESENT","ALREADY");

                                            pw.dismiss();
                                            return;
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        try {
                            jsonObject.put("patientname", patientName.getText().toString());
                            jsonObject.put("patientid", patientId.getText().toString());
                            jsonObject.put("numofsessions", "0");
                            jsonObject.put("status","active");
                            jsonObject.put("patientphone", patientId.getText().toString());
                            jsonObject.put("patientprofilepicurl", "empty");
                            jsonObject.put("dateofjoin",todaysDate);
                            jsonObject.put("sessions", new JSONArray());
                            jsonData.put(jsonObject);
                            jsonObject.put("phizioemail",json_phizio.get("phizioemail"));
                            mqttMessage.setPayload(jsonObject.toString().getBytes());

                            //temprary array
                            JSONArray temp ;
                            if(isNetworkAvailable()) {
//                                mqttHelper.syncData();
                                mqttHelper.publishMqttTopic(mqtt_publish_phizio_addpatient, mqttMessage);
                            }

                            json_phizio.put("phiziopatients",jsonData);
                            editor.putString("phiziodetails", json_phizio.toString());
                            editor.commit();
                            JSONObject object = new JSONObject();
                            object.put("topic",mqtt_publish_phizio_addpatient);
                            object.put("message",mqttMessage.toString());
                            if(!sharedPref.getString("sync_session","").equals("")){
                                temp = new JSONArray(sharedPref.getString("sync_session",""));
                                temp.put(object);
                                editor.putString("sync_session",temp.toString());
                                editor.commit();
                            }
                            else {
                                temp = new JSONArray();
                                temp.put(object);
                                editor.putString("sync_session",temp.toString());
                                editor.commit();
                            }
                            pushJsonData(new JSONArray(json_phizio.getString("phiziopatients")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    pw.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pw.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTING) {
                    refreshDeviceCache(gatt);
                    isBleConnected = true;
                    Message msg = Message.obtain();
                    msg.obj = "C..";
                    bleStatusHandler.sendMessage(msg);
                } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                    refreshDeviceCache(gatt);
                    Message msg = Message.obtain();
                    isBleConnected = true;
                    msg.obj = "C";
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

            new MyAsync().execute();

        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.i("inside","descritor");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
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
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.i("chatacteristic","read");

                byte info_packet[] = characteristic.getValue();
                Log.i("inside","inside");
                int battery = info_packet[2]&0xFF;
                int device_status = info_packet[3]&0xFF;
                int device_usb_state = info_packet[4]&0xFF;
                Log.i("device",battery+" "+device_status+" "+device_usb_state);

                //remove comments later now.

//            mCharacteristic = gatt.getService(battery_service1_uuid).getCharacteristic(battery_level_battery_service_characteristic_uuid);
//            gatt.setCharacteristicNotification(mCharacteristic,true);
//            mBluetoothGattDescriptor = mCharacteristic.getDescriptor(descriptor_characteristic1_service1_uuid);
//            mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            bluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i("chatacteristic","written");
            bluetoothGatt.readCharacteristic(mCustomCharacteristic);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addpheezee(View view) {
        PopupMenu popupMenu = new PopupMenu(PatientsView.this, view, Gravity.CENTER);
        popupMenu.setOnMenuItemClickListener(PatientsView.this);
        popupMenu.inflate(R.menu.popupmenu);
        popupMenu.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        try {
            json_phizio = new JSONObject(sharedPref.getString("phiziodetails", ""));
            email.setText(json_phizio.getString("phizioemail"));
            fullName.setText(json_phizio.getString("phizioname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        super.onRestart();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void editPatientDetails(View view) {
        patientLayout = (LinearLayout)view;
        AlertDialog.Builder editPatientBuilder= new AlertDialog.Builder(this);
        editPatientBuilder.setTitle("You can edit patient details here");
        patientNameField = new EditText(this);

        final TextView patientIdTemp = view.findViewById(R.id.patientId);
        TextView patientNameTemp = view.findViewById(R.id.patientName);
        patientNameField.setText(patientNameTemp.getText().toString());
        editPatientBuilder.setView(patientNameField);
        try {
            jsonData = new JSONArray(json_phizio.getString("phiziopatients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final MqttMessage mqttMessage = new MqttMessage();
        final JSONObject object = new JSONObject();

        editPatientBuilder.setPositiveButton("Save",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String patientName = patientNameField.getText().toString();

                for(int k=0;k<jsonData.length();k++){
                    try {
                        if(jsonData.getJSONObject(k).getString("patientid").equals(patientIdTemp.getText().toString().substring(5))){

                            jsonData.getJSONObject(k).put("patientname",patientName);
                            json_phizio.put("phiziopatients",jsonData);
                            object.put("phizioemail", json_phizio.get("phizioemail"));
                            object.put("patientid",jsonData.getJSONObject(k).get("patientid"));
                            object.put("patientname",jsonData.getJSONObject(k).get("patientname"));

                            editor.putString("phiziodetails",json_phizio.toString());
                            editor.apply();
                            pushJsonData(new JSONArray(json_phizio.getString("phiziopatients")));
                            mqttMessage.setPayload(object.toString().getBytes());
                            mqttHelper.publishMqttTopic(mqtt_publish_phizio_update_patientdetails,mqttMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        editPatientBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        editPatientBuilder.show();
    }

    @SuppressLint("HandlerLeak")
    public final Handler bleStatusHandler = new Handler() {
        public void handleMessage(Message msg) {
            String status = (String) msg.obj;
            bleStatusTextView.setText((String) msg.obj);
            ScanDevicesActivity.setBleStatus(status);
        }
    };

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
                Log.i("Device Status: ", "Device Disconnected");
                Toast.makeText(PatientsView.this, "The device has got disconnected...", Toast.LENGTH_LONG).show();
                isBleConnected = false;
                Message message = Message.obtain();
                message.obj = "N/C";
                bleStatusHandler.sendMessage(message);
                Intent i = getIntent();
                finish();
                startActivity(i);

            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
                    Toast.makeText(PatientsView.this, "Bluetooth turned off", Toast.LENGTH_LONG).show();
                    Message message = Message.obtain();
                    message.obj = "N/C";
                    bleStatusHandler.sendMessage(message);
                }
            }

        }
    };

    public void startSession(View view) {
        patientTabLayout= (LinearLayout) ((Button)view).getParent().getParent();
        patientTabLayout = (LinearLayout) patientTabLayout.getChildAt(1);


        //patientid and name
        TextView patientName = (TextView) patientTabLayout.getChildAt(0);
        TextView patientId = (TextView) patientTabLayout.getChildAt(1);

        Intent intent = new Intent(PatientsView.this, BodyPartSelection.class);
        intent.putExtra("deviceMacAddress", sharedPref.getString("deviceMacaddress", ""));
        intent.putExtra("patientId", patientId.getText().toString().substring(5));
        intent.putExtra("patientName", patientName.getText().toString());



        LinearLayout linearLayout = (LinearLayout) view;
        if (sharedPref.getString("deviceMacaddress", "").equals("")) {
            Toast.makeText(this, "First add pheezee to your application", Toast.LENGTH_LONG).show();
        } else if (!bleStatusTextView.getText().toString().equals("C")  ) {
            Toast.makeText(this, "Make sure that the pheezee is on", Toast.LENGTH_LONG).show();
        }
        else {
            startActivity(intent);
        }

    }


    //target to save

    public void openDashboard(LinearLayout view,String exerciseType) {

        TextView patientName = (TextView) view.getChildAt(0);
        TextView patientId = (TextView) view.getChildAt(1);
        System.out.println(patientId.getText().toString());
        Intent intent = new Intent(PatientsView.this, DashboardActivity.class);
        intent.putExtra("exerciseType",exerciseType);
        intent.putExtra("deviceMacAddress", sharedPref.getString("deviceMacaddress", ""));
        intent.putExtra("patientId", patientId.getText().toString().substring(5));
        try {
            intent.putExtra("phizioemail",json_phizio.getString("phizioemail"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra("patientName", patientName.getText().toString());
        startActivity(intent);
        bodyPartLayout.dismiss();
}


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.scan_nearby_devices: {
                to_scan_devices_activity = new Intent(PatientsView.this, ScanDevicesActivity.class);
                if (bluetoothAdapter==null || !bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT_SCAN);
                }
                else
                    startActivity(to_scan_devices_activity);
                break;
            }
            case R.id.qrcode_scan:{
                final Context context = this;
                startActivity(new Intent(context, Scanner.class));
                break;
            }

            case R.id.enter_mac_address:{
                builder = new AlertDialog.Builder(this,R.style.AlertDialogStyle_entermac);
                builder.setTitle("ENTER THE MAC ADDRESS");
                final EditText input_macAddress = new EditText(this);

                builder.setMessage("Please enter the mac address and press connect");
                input_macAddress.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input_macAddress);
                builder.setPositiveButton("CONNECT", new DialogInterface.OnClickListener() {
            @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putString("deviceMacaddress", input_macAddress.getText().toString());
                            editor.commit();
                        }
                    });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
                break;
            }
        }
        return false;
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

    private boolean refreshDeviceCache(BluetoothGatt gatt){
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        }
        catch (Exception localException) {
            Log.e("EXCEPTION", "An exception occured while refreshing device");
        }
        return false;
    }

    public void chooseImageUpdateAction(final View view){
        patientLayoutView = view;
        builder = new AlertDialog.Builder(PatientsView.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if(ContextCompat.checkSelfPermission(PatientsView.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(PatientsView.this, new String[]{Manifest.permission.CAMERA}, 5);
                        cameraIntent();
                    }
                    else {
                        cameraIntent();
                    }
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 6);
    }


    private void cameraIntent() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(takePicture, 5);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 1
        if(requestCode==5){
            if(resultCode == RESULT_OK){
                ImageView imageView_patientpic = patientLayoutView.findViewById(R.id.patientProfilePic);
                patientTabLayout= (LinearLayout) (patientLayoutView).getParent().getParent();
                patientTabLayout = (LinearLayout) patientTabLayout.getChildAt(1);
                //TextView tv_patientId = patientLayoutView.getRootView().findViewById(R.id.patientId);
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView_patientpic.setImageBitmap(photo);
                TextView tv_patientId = (TextView) patientTabLayout.getChildAt(1);;
                JSONObject object = new JSONObject();

                MqttMessage message = new MqttMessage();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String encodedString = Base64.encodeToString(byteArray,Base64.DEFAULT);
                try {
                    object.put("image",encodedString);
                    object.put("phizioemail",json_phizio.getString("phizioemail"));
                    object.put("patientid",tv_patientId.getText().toString().substring(4).replaceAll("\\s+",""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                message.setPayload(object.toString().getBytes());

                mqttHelper.publishMqttTopic(mqtt_publish_phizio_patient_profilepic,message);
            }
        }

        if(requestCode==6){
            ImageView imageView_patientpic = patientLayoutView.findViewById(R.id.patientProfilePic);
            patientTabLayout= (LinearLayout) (patientLayoutView).getParent().getParent();
            patientTabLayout = (LinearLayout) patientTabLayout.getChildAt(1);
            Uri selectedImage = data.getData();
            Glide.with(this).load(selectedImage).apply(new RequestOptions().centerCrop()).into(imageView_patientpic);
            TextView tv_patientId = (TextView) patientTabLayout.getChildAt(1);
            Bitmap photo = null;
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject object = new JSONObject();

            MqttMessage message = new MqttMessage();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String encodedString = Base64.encodeToString(byteArray,Base64.DEFAULT);
            try {
                object.put("image",encodedString);
                object.put("phizioemail",json_phizio.getString("phizioemail"));
                object.put("patientid",tv_patientId.getText().toString().substring(4).replaceAll("\\s+",""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            message.setPayload(object.toString().getBytes());

            mqttHelper.publishMqttTopic(mqtt_publish_phizio_patient_profilepic,message);
        }
        if (resultCode != 0) {
            if(!sharedPref.getString("deviceMacaddress", "").equals("")) {
                remoteDevice = bluetoothAdapter.getRemoteDevice(sharedPref.getString("deviceMacaddress", ""));


                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothGatt = remoteDevice.connectGatt(PatientsView.this, true, callback);
                    }
                });

            }
        }

        if(requestCode==2){
            if(resultCode!=0){
                startActivity(new Intent(this,ScanDevicesActivity.class));
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    public void openOpionsPopupWindow(View view){
        Bitmap patientpic_bitmap=null;


        patientTabLayout= (LinearLayout) (view).getParent();

        LinearLayout iv_layout = (LinearLayout)patientTabLayout.getChildAt(0);
        patientTabLayout = (LinearLayout) patientTabLayout.getChildAt(1);

        ImageView iv_patient_pic = iv_layout.findViewById(R.id.patientProfilePic);

        if(!(iv_patient_pic.getDrawable() ==null)) {
            patientpic_bitmap = ((BitmapDrawable) iv_patient_pic.getDrawable()).getBitmap();
        }

        String dateofjoin = getJoinDateOfPatiet();
        dateofjoin = getDateInMonthAndDate(dateofjoin);

        TextView tv_patient_name = patientTabLayout.findViewById(R.id.patientName);
        TextView tv_patient_id = patientTabLayout.findViewById(R.id.patientId);

        myBottomSheetDialog = new MyBottomSheetDialog(tv_patient_name.getText().toString(),patientpic_bitmap,tv_patient_id.getText().toString().substring(5),dateofjoin);



        myBottomSheetDialog.show(getSupportFragmentManager(),"MyBottomSheet");

    }


    public void editThePatientDetails(View view){
        myBottomSheetDialog.dismiss();
        if(isNetworkAvailable())
            editPatientDetails(patientTabLayout);
        else {
            networkError();
        }
    }


    //Self reference

//    public void startCalibrationSession(View view){
//        myBottomSheetDialog.dismiss();
//        if(bleStatusTextView.getText().toString().equals("C")) {
//            bodyPartsPopup(view,2);
//        }
//        else{
//            Toast.makeText(PatientsView.this, "Please Connect pheezee..", Toast.LENGTH_SHORT).show();
//        }
//    }


    private void openCalibrationActivity(String bodypart) {
        final TextView patientIdTemp = patientTabLayout.findViewById(R.id.patientId);
        TextView patientNameTemp = patientTabLayout.findViewById(R.id.patientName);

        Intent mmt_intent = new Intent(PatientsView.this, CalibrationSession.class);
        mmt_intent.putExtra("deviceMacAddress", sharedPref.getString("deviceMacaddress", ""));
        mmt_intent.putExtra("patientid", patientIdTemp.getText().toString().substring(5));
        mmt_intent.putExtra("bodypart", bodypart);
        mmt_intent.putExtra("patientname", patientNameTemp.getText().toString());
        startActivity(mmt_intent);
        bodyPartLayoutWndow.dismiss();
    }

    public void openReportActivity(View view){
        if(isNetworkAvailable()){
            final TextView patientIdTemp = patientTabLayout.findViewById(R.id.patientId);
            TextView patientNameTemp = patientTabLayout.findViewById(R.id.patientName);
            Intent mmt_intent = new Intent(PatientsView.this, SessionReportActivity.class);
            mmt_intent.putExtra("patientid", patientIdTemp.getText().toString().substring(5));
            mmt_intent.putExtra("patientname", patientNameTemp.getText().toString());
            try {
                mmt_intent.putExtra("phizioemail", json_phizio.getString("phizioemail"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(mmt_intent);
            myBottomSheetDialog.dismiss();
        }
        else {
            networkError();
        }
    }

    public void updatePatientStatus(View view){
        myBottomSheetDialog.dismiss();
        final MqttMessage mqttMessage = new MqttMessage();
        final JSONObject object = new JSONObject();
        try {
            jsonData = new JSONArray(json_phizio.getString("phiziopatients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Archive Patient");
        builder.setMessage("Are you sure you want to archive the patient?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isNetworkAvailable()){
                    final TextView patientIdTemp = patientTabLayout.findViewById(R.id.patientId);
                    for(int k=0;k<jsonData.length();k++){
                        try {
                            if(jsonData.getJSONObject(k).getString("patientid").equals(patientIdTemp.getText().toString().substring(5))){


                                object.put("phizioemail", json_phizio.get("phizioemail"));
                                object.put("patientid",jsonData.getJSONObject(k).get("patientid"));
                                object.put("status","inactive");
                                jsonData.getJSONObject(k).put("status","inactive");
                                json_phizio.put("phiziopatients",jsonData);

                                editor.putString("phiziodetails",json_phizio.toString());
                                editor.commit();
                                pushJsonData(new JSONArray(json_phizio.getString("phiziopatients")));
                                mqttMessage.setPayload(object.toString().getBytes());
                                mqttHelper.publishMqttTopic(mqtt_update_patient_status,mqttMessage);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    networkError();
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


    public void deletePatient(View view){
        myBottomSheetDialog.dismiss();
        final MqttMessage mqttMessage = new MqttMessage();
        final JSONObject object = new JSONObject();
        try {
            jsonData = new JSONArray(json_phizio.getString("phiziopatients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Patient");
        builder.setMessage("Are you sure you want to delete the patient?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isNetworkAvailable()){
                    final TextView patientIdTemp = patientTabLayout.findViewById(R.id.patientId);
                    for(int k=0;k<jsonData.length();k++){
                        try {
                            if(jsonData.getJSONObject(k).getString("patientid").equals(patientIdTemp.getText().toString().substring(5))){


                                object.put("phizioemail", json_phizio.get("phizioemail"));
                                object.put("patientid",jsonData.getJSONObject(k).get("patientid"));
                                jsonData.remove(k);
                                json_phizio.put("phiziopatients",jsonData);

                                editor.putString("phiziodetails",json_phizio.toString());
                                editor.commit();

                                Log.i("jsonData", json_phizio.getString("phiziopatients"));
                                pushJsonData(new JSONArray(json_phizio.getString("phiziopatients")));
                                mqttMessage.setPayload(object.toString().getBytes());
                                mqttHelper.publishMqttTopic(mqtt_publish_phizio_deletepatient,mqttMessage);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    networkError();
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


    public String dateInMmDdYyyy(){
        Date today =new Date();
        int date = today.getDate();
        int month = today.getMonth()+1;
        int year = today.getYear()+1900;
        String todaysDate, d,m;
        d=date+""; m=month+"";
        if(date<10)
            d = "0"+date;

        if(month<10)
            m = "0"+month;



            todaysDate = d+"/"+m+"/"+year+"";

        return todaysDate;
    }

    public String getJoinDateOfPatiet(){
        String dateofjoin="";
        try {
            jsonData = new JSONArray(json_phizio.getString("phiziopatients"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final TextView patientIdTemp = patientTabLayout.findViewById(R.id.patientId);
        for(int k=0;k<jsonData.length();k++){
            try {
                if(jsonData.getJSONObject(k).getString("patientid").equals(patientIdTemp.getText().toString().substring(5))){
                    if(jsonData.getJSONObject(k).has("dateofjoin")){
                        dateofjoin = jsonData.getJSONObject(k).getString("dateofjoin");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i("dateofjoin",dateofjoin);
        return dateofjoin;
    }


    private static String getDateInMonthAndDate(String date) throws ParseException {
        if(!date.equals("")) {
            int d= Integer.parseInt(date.substring(0,2));
            int m  = (Integer.parseInt(date.substring(3,5))-1);
            int y = Integer.parseInt(date.substring(6,10));
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DATE,d);
            cal.set(Calendar.MONTH,m);
            cal.set(Calendar.YEAR,y);
            String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
            String dayName = new SimpleDateFormat("EEEE").format(cal.getTime());
            String dat = new SimpleDateFormat("dd").format(cal.getTime());
            return (monthName.substring(0,3)+" "+dat+", "+dayName);
        }

        return "";
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void networkError(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Network Error");
        builder.setMessage("Please connect to internet and try again");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }



    private class MyAsync extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            byte b[] = hexStringToByteArray("AA02");
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


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

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

    public final Handler batteryStatus = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_battery_percentage.setText(msg.obj.toString()+"%");
        }
    };

}
