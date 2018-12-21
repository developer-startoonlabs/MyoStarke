package com.example.sai.pheezeeapp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
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
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.opengl.Visibility;
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
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.Layout;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.Classes.BluetoothGattSingleton;
import com.example.sai.pheezeeapp.Classes.BluetoothSingelton;
import com.example.sai.pheezeeapp.Classes.MyBottomSheetDialog;
import com.example.sai.pheezeeapp.DFU.DfuActivity;
import com.example.sai.pheezeeapp.DemoActivity;
import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.dashboard.DashboardActivity;
import com.example.sai.pheezeeapp.patientsRecyclerView.PatientsListData;
import com.example.sai.pheezeeapp.patientsRecyclerView.PatientsRecyclerViewAdapter;
import com.example.sai.pheezeeapp.services.MqttHelper;
import com.example.sai.pheezeeapp.services.PicassoCircleTransformation;
import com.example.sai.pheezeeapp.services.Scanner;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatientsView extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener, PopupMenu.OnMenuItemClickListener {


    View patientLayoutView;
    MyBottomSheetDialog myBottomSheetDialog;
    Dialog optionsPopdialog ;

    //For Alert Dialog
    final CharSequence[] items = { "Take Photo", "Choose from Library",
            "Cancel" };

    AlertDialog.Builder builderToChooseImage;


    TextView email,fullName;
    public static ImageView ivBasicImage;
    //new
    JSONObject json_phizio = new JSONObject();
    //MQTT HELPER

    MqttHelper mqttHelper;
    String mqtt_publish_phizio_addpatient = "phizio/addpatient";
    String mqtt_publish_phizio_deletepatient = "phizio/deletepatient";
    String mqtt_publish_phizio_update_patientdetails = "phizio/updatepatientdetails";

    String mqtt_publish_phizio_patient_profilepic = "phizio/update/patientProfilePic";

    String mqtt_get_profile_pic_response = "phizio/getprofilepic/response";



    String mqtt_subs_phizio_addpatient_response = "phizio/addpatient/response";
    String mqtt_subs_phizio_deletepatient_response = "phizio/deletepatient/response";
    //Request action intents

    int REQUEST_ENABLE_BT = 1;

    boolean isBleConnected;
    public boolean gattconnection_established = false;
    //All the intents

    Intent to_scan_devices_activity;

    private List<PatientsListData> mdataset = new ArrayList<>();
    private PatientsRecyclerViewAdapter mAdapter;
    EditText patientNameField;
    PopupWindow pw;
    int backpressCount = 0;
    DrawerLayout drawer;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    JSONArray jsonData;
    AlertDialog.Builder builder;
    AlertDialog.Builder editPatientBuilder;
    public static TextView bleStatusTextView;
    BluetoothGatt bluetoothGatt;
    BluetoothDevice remoteDevice;
    BluetoothAdapter bluetoothAdapter;
    BluetoothManager mBluetoothManager;
    String MacAddress;
    TextView patientId;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    ImageView imageView;
    Context context;
    FloatingActionButton addPatientsBtn;
    LinearLayout patientTabLayout;
    private LinearLayout patientLayout;

    RecyclerView mRecyclerView;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
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



        //Getting previous patient data

        try {
            json_phizio = new JSONObject(sharedPref.getString("phiziodetails", ""));
            Log.i("Patient View", json_phizio.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ivBasicImage = (ImageView) view.findViewById(R.id.imageViewdp);
        Picasso.get().load(Environment.getExternalStoragePublicDirectory("profilePic"))
                .placeholder(R.drawable.user_icon)
                .error(R.drawable.user_icon)
                .transform(new PicassoCircleTransformation())
                .into(ivBasicImage);
         email = (TextView) view.findViewById(R.id.emailId);
         fullName = (TextView) view.findViewById(R.id.fullName);
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



       /* try {
            if (!sharedPref.getString("patientsData", "").equals("")) {
                findViewById(R.id.noPatient).setVisibility(View.GONE);
                pushJsonData(new JSONArray(sharedPref.getString("patientsData", "")));
            }
            if (!(getIntent().getStringExtra("macAddress") == null || getIntent().getStringExtra("macAddress").equals(""))) {
                macAddress = getIntent().getStringExtra("macAddress");
                editor.putString("deviceMacaddress", macAddress);
                editor.apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
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
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i("MQTT ARRIVED",""+message);
                 if(topic.equals(mqtt_get_profile_pic_response)){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(message.getPayload(), 0, message.getPayload().length);
                    ivBasicImage.setImageBitmap(bitmap);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothReceiver);
        super.onDestroy();
    }

    public void pushJsonData(JSONArray data) {
        /*mdataset.clear();
        PatientsListData patientsList;
        for (int i = data.length()-1; i >=0 ; i--) {
            try {
                patientsList = new PatientsListData(data.getJSONObject(i).getString("patientName"), data.getJSONObject(i).getString("patientId"));
                mdataset.add(patientsList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (data.length() > 0)
            findViewById(R.id.noPatient).setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();*/
        if (data.length() > 0) {
            mdataset.clear();
            PatientsListData patientsList;
            for (int i = data.length() - 1; i >= 0; i--) {
                try {
                    patientsList = new PatientsListData(data.getJSONObject(i).getString("patientname"), data.getJSONObject(i).getString("patientid"),data.getJSONObject(i).getString("patientid"));
                    mdataset.add(patientsList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            findViewById(R.id.noPatient).setVisibility(View.GONE);

        }
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

         if(id == R.id.demo_app){
            startActivity(new Intent(PatientsView.this,DemoActivity.class));
        }/*else if (id == R.id.nav_settings) {

        }*/

         else if (id==R.id.pheeze_device_info){
             Intent i = new Intent(PatientsView.this, DeviceInfoActivity.class);
             i.putExtra("deviceMacAddress", sharedPref.getString("deviceMacaddress", ""));
             startActivity(i);
         }

        else if (id == R.id.rawdatacollection){
            if (bleStatusTextView.getText().equals("C")) {
                Intent i = new Intent(PatientsView.this, RawDataCollection.class);
                i.putExtra("deviceMacAddress", sharedPref.getString("deviceMacaddress", ""));
                startActivity(i);
            }
            else {
                Toast.makeText(context, "Please connect to pheeze" , Toast.LENGTH_SHORT).show();
            }
        }
        else if (id == R.id.nav_logout) {
            if (GoogleSignIn.getLastSignedInAccount(this) != null)
                signOut();
            else
                AccessToken.setCurrentAccessToken(null);
            editor.putBoolean("isLoggedIn",false);
             editor.clear();
             editor.commit();

             startActivity(new Intent(this, LoginActivity.class));
             finish();

        } else if (id == R.id.nav_share) {

        }

        else if(id == R.id.ota_device) {
            Intent intent = null;
            if (!bleStatusTextView.getText().toString().equals("C")) {
                Toast.makeText(context, "Please Connect to Pheeze.", Toast.LENGTH_SHORT).show();
            } else {
                intent = new Intent(this, DfuActivity.class);
                intent.putExtra("deviceMacAddress", sharedPref.getString("deviceMacaddress", ""));
                startActivity(intent);
            }

        }

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
            Button addBtn = layout.findViewById(R.id.addBtn);
            Button cancelBtn = layout.findViewById(R.id.cancelBtn);
            /*if (!sharedPref.getString("patientsData", "").equals("")) {
                jsonData = new JSONArray(sharedPref.getString("patientsData", ""));
                for (int i = 0; i < jsonData.length(); i++) {
                    if (jsonData.getJSONObject(i).get("patientId").equals(patientId.getText().toString())) {
                        Toast.makeText(PatientsView.this,
                                "Patient with same patient id is already exsists at patient name " + jsonData.getJSONObject(i).get("patientName"),
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((!patientName.getText().toString().equals("")) && (!patientId.getText().toString().equals(""))) {
                        if (!sharedPref.getString("patientsData", "").equals("")) {
                            try {
                                jsonData = new JSONArray(sharedPref.getString("patientsData", ""));
                                for (int i = 0; i < jsonData.length(); i++) {
                                    if (jsonData.getJSONObject(i).get("patientId").equals(patientId.getText().toString())) {
                                        Toast.makeText(PatientsView.this,
                                                "Patient with same patient id is already exsists for patient name " + jsonData.getJSONObject(i).get("patientName"),
                                                Toast.LENGTH_LONG).show();
                                        pw.dismiss();
                                        return;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            jsonObject.put("patientName", patientName.getText().toString());
                            jsonObject.put("patientId", patientId.getText().toString());
                            jsonData.put(jsonObject);
                            editor.putString("patientsData", jsonData.toString());
                            editor.commit();
                            pushJsonData(new JSONArray(sharedPref.getString("patientsData", "")));
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
            });*/

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
                            jsonObject.put("patientphone", patientId.getText().toString());
                            jsonObject.put("patientprofilepicurl", "empty");
                            jsonObject.put("sessions", new JSONArray());
                            jsonData.put(jsonObject);
                            jsonObject.put("phizioemail",json_phizio.get("phizioemail"));
                            mqttMessage.setPayload(jsonObject.toString().getBytes());
                            mqttHelper.publishMqttTopic(mqtt_publish_phizio_addpatient,mqttMessage);
                            json_phizio.put("phiziopatients",jsonData);
                            editor.putString("phiziodetails", json_phizio.toString());
                            editor.commit();
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

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.i("characteristic",characteristic.getValue().toString());
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
        editPatientBuilder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            final MqttMessage mqttMessage = new MqttMessage();
            final JSONObject object = new JSONObject();
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for(int k=0;k<jsonData.length();k++){
                    try {
                        if(jsonData.getJSONObject(k).getString("patientid").equals(patientIdTemp.getText().toString().substring(5))){


                            object.put("phizioemail", json_phizio.get("phizioemail"));
                            object.put("patientid",jsonData.getJSONObject(k).get("patientid"));
                            jsonData.remove(k);
                            json_phizio.put("phiziopatients",jsonData);

                            editor.putString("phiziodetails",json_phizio.toString());
                            editor.apply();
                            pushJsonData(new JSONArray(json_phizio.getString("phiziopatients")));
                            mqttMessage.setPayload(object.toString().getBytes());
                            mqttHelper.publishMqttTopic(mqtt_publish_phizio_deletepatient,mqttMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        editPatientBuilder.show();
    }

    @SuppressLint("HandlerLeak")
    public final Handler bleStatusHandler = new Handler() {
        public void handleMessage(Message msg) {
            bleStatusTextView.setText((String) msg.obj);
        }
    };

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
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

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    public void startSession(View view) {
        LinearLayout linearLayout = (LinearLayout) view;
        if (sharedPref.getString("deviceMacaddress", "").equals("")) {
            Toast.makeText(this, "First add pheezee to your application", Toast.LENGTH_LONG).show();
        } else if (!bleStatusTextView.getText().toString().equals("C")  ) {
            Toast.makeText(this, "Make sure that the pheezee is on", Toast.LENGTH_LONG).show();
        }

        else if (isBleConnected==false){
            Toast.makeText(this, "Make sure that the pheezee is on", Toast.LENGTH_LONG).show();
        }

        else {
           bodyPartsPopupWindow(view);
//            patientTabLayout= (LinearLayout) ((Button)view).getParent().getParent();
//            patientTabLayout = (LinearLayout) patientTabLayout.getChildAt(0);
//
//            TextView patientName = (TextView) patientTabLayout.getChildAt(0);
//            TextView patientId = (TextView) patientTabLayout.getChildAt(1);
//            System.out.println(patientId.getText().toString());
//            Intent intent = new Intent(PatientsView.this, DashboardActivity.class);
//            //intent.putExtra("exerciseType",exerciseType);
//            intent.putExtra("deviceMacAddress", sharedPref.getString("deviceMacaddress", ""));
//            intent.putExtra("patientId", patientId.getText().toString().substring(13));
//            intent.putExtra("patientName", patientName.getText().toString());
//            startActivity(intent);
        }
    }


    //target to save

    private void bodyPartsPopupWindow(View view) {

        View layout = this.getLayoutInflater().inflate(R.layout.bady_parts_layout,null);

        final PopupWindow bodyPartLayout = new PopupWindow(layout, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);

        patientTabLayout= (LinearLayout) ((Button)view).getParent().getParent();
        patientTabLayout = (LinearLayout) patientTabLayout.getChildAt(1);

        bodyPartLayout.showAtLocation(view.getRootView().getRootView(), Gravity.CENTER, 0, 0);
        LinearLayout cancelbtn = layout.findViewById(R.id.cancel_action);
        CardView elbowView = layout.findViewById(R.id.elbow);
        CardView kneeView = layout.findViewById(R.id.knee);
        CardView sholderView = layout.findViewById(R.id.sholder);
        CardView hipView = layout.findViewById(R.id.hip);

        elbowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDashboard(patientTabLayout,"elbow");
            }
        });
        kneeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDashboard(patientTabLayout,"knee");
            }
        });

        sholderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDashboard(patientTabLayout,"sholder");
            }
        });
        hipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDashboard(patientTabLayout,"hip");
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bodyPartLayout.dismiss();
            }
        });


    }
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
}


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.scan_nearby_devices: {
                to_scan_devices_activity = new Intent(PatientsView.this, ScanDevicesActivity.class);
                startActivity(to_scan_devices_activity);
                break;
            }
            case R.id.qrcode_scan:{
                final Context context = this;
                startActivity(new Intent(context, Scanner.class));
                break;
            }

            case R.id.enter_mac_address:{
                builder = new AlertDialog.Builder(this);
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


    public void historyoreditdetails(final View view){
        PopupMenu popupMenu = new PopupMenu(PatientsView.this,mRecyclerView);
        popupMenu.getMenuInflater().inflate(R.menu.patientoptionmenu,popupMenu.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popupMenu.setGravity(Gravity.CENTER);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.history){
                    Intent intent= new Intent(PatientsView.this,SessionList.class);
                    final TextView patientIdTemp = view.findViewById(R.id.patientId);
                    intent.putExtra("patientid",patientIdTemp.getText().toString().substring(4).replaceAll("\\s+",""));
                    //Toast.makeText(context, patientIdTemp.getText().toString(), Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                else {
                    editPatientDetails(view);
                }

                return false;
            }
        });
        popupMenu.show();
    }

    public void updateImage(Bitmap bitmap){
        Log.i("hello","hello");
        ivBasicImage.setImageBitmap(bitmap);
        ivBasicImage.notify();
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
                patientTabLayout= (LinearLayout) ((LinearLayout)patientLayoutView).getParent().getParent();
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
            patientTabLayout= (LinearLayout) ((LinearLayout)patientLayoutView).getParent().getParent();
            patientTabLayout = (LinearLayout) patientTabLayout.getChildAt(1);
            Uri selectedImage = data.getData();
            imageView_patientpic.setImageURI(selectedImage);
            TextView tv_patientId = (TextView) patientTabLayout.getChildAt(1);;
            Log.i("tv value",tv_patientId.getText().toString());
            imageView_patientpic.invalidate();
            BitmapDrawable drawable = (BitmapDrawable) imageView_patientpic.getDrawable();
            Bitmap photo = drawable.getBitmap();
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
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    public void openOpionsPopupWindow(View view){
        myBottomSheetDialog = new MyBottomSheetDialog();
        myBottomSheetDialog.show(getSupportFragmentManager(),"MyBottomSheet");



        patientTabLayout= (LinearLayout) ((LinearLayout)view).getParent();
        patientTabLayout = (LinearLayout) patientTabLayout.getChildAt(1);

    }


    public void editThePatientDetails(View view){
        myBottomSheetDialog.dismiss();

        editPatientDetails(patientTabLayout);
    }


}
