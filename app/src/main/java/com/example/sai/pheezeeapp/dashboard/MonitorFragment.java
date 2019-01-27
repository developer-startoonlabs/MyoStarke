package com.example.sai.pheezeeapp.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.Classes.BluetoothSingelton;
import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.services.MqttHelper;
import com.example.sai.pheezeeapp.views.ArcView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.goodiebag.protractorview.ProtractorView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MonitorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonitorFragment extends Fragment {



    public final int sub_byte_size = 26;
    boolean f_report_pop = false;
    boolean servicesDiscovered = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    JSONObject json_phizio = new JSONObject();
    JSONArray jsonData;
    JSONArray emgJsonArray;

    //MQTT
    MqttHelper mqttHelper;
    String mqtt_publish_add_patient_session = "phizio/addpatientsession";
    String mqtt_publish_add_patient_session_emg_data = "patient/entireEmgData";

    ConstraintLayout cl_monitor;
    public static Context context;
    public String exercise_type;
    PopupWindow report;
    Float protime=0.0f;
    int visiblity=View.VISIBLE;
    long k;
    boolean blestatus = false;
    String timeText ="";
    ProgressBar bleProgressBar;
    File sessionDataTrackedFile;
    File sessionDataUnTrackedFile;
    List<Entry> dataPoints;
    LineChart lineChart;
    BluetoothGattDescriptor bluetoothGattDescriptor;
    LineDataSet lineDataSet;
    TextView myTextProgressBar;
    ArrayList<Entry> data;
    int SessionTimeForGraph;
    MqttAndroidClient client;
    View rootView;
    private static final String TAG = null;
    UUID characteristicUUID;
    String message;
    BluetoothGattCharacteristic mCharacteristic;
    BluetoothGatt mBluetoothGatt;
    BluetoothGattService mBluetoothGattService;
    TextView Angle;
    TextView Repetitions;
    TextView holdTime;
    TextView EMG;
    ProtractorView rangeOfMotion;
    TextView time;
    TextView patientId;
    TextView patientName;
    LineData lineData;
    JSONArray sessionResult  = new JSONArray();
    JSONObject sessionObj = new JSONObject();
    boolean recieverState=false;
    boolean pheezeeState = false;
    Button timer;
    Button stopBtn;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    Handler handler;
    int Seconds, Minutes, MilliSeconds ;
    ProgressBar p;
    ProgressBar r;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice remoteDevice;
    int REQUEST_ENABLE_BT = 1;
    UUID serviceUUID;
    UUID descriptorUUID;
    BluetoothGatt bluetoothGatt;
    ConnectivityManager connectivityManager;
    Message msg;
    LinearLayout emgSignal;
    ViewGroup mcontainer;
    private ConstraintLayout constraintLayout;
    LayoutInflater minflater;
    String holdTimeValue;
    int maxAngle,minAngle,maxEmgValue;
    Long tsLong=0L;
    String exerciseType;
    BluetoothGattDescriptor mBluetoothGattDescriptor;




    //All the constant uuids are written here
    public static final UUID service1_uuid = UUID.fromString("909a1400-9693-4920-96e6-893c0157fedd");
    public static final UUID characteristic1_service1_uuid = UUID.fromString("909a1401-9693-4920-96e6-893c0157fedd");
    public static final UUID descriptor_characteristic1_service1_uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID device_info_service1_uuid = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public static final UUID firmware_version_characteristic_uuid = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");



    // TODO: Rename and change types of parameters

    public MonitorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MonitorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonitorFragment newInstance(int milliSeconds) {
        MonitorFragment fragment = new MonitorFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        boolean isServicesDiscoverd = false;

        // Inflate the layout for this fragment
        context = getActivity();
        minflater = inflater;
        constraintLayout = new ConstraintLayout(context);
        mcontainer =container;
            rootView                    = inflater.inflate(R.layout.fragment_monitor, container, false);
        sessionDataTrackedFile      = generateFile("sessionDataTracked.txt");
        sessionDataUnTrackedFile    = generateFile( "sessionDataUnTracked.txt");
        lineChart                   = rootView.findViewById(R.id.chart);
        Angle                       = rootView.findViewById(R.id.Angle);
        EMG                         = rootView.findViewById(R.id.emgValue);
        rangeOfMotion               = rootView.findViewById(R.id.rangeOfMotion);
        Repetitions                 = rootView.findViewById(R.id.Repetitions);
        holdTime                    = rootView.findViewById(R.id.holdtime);
        timer                       = rootView.findViewById(R.id.timer);
        stopBtn                     = rootView.findViewById(R.id.stopBtn);
        patientId                   = rootView.findViewById(R.id.patientId);
        patientName                 = rootView.findViewById(R.id.patientName);
        time                        = rootView.findViewById(R.id.displayTime);
        emgSignal                   = rootView.findViewById(R.id.emg);
        handler                     = new Handler();
        emgJsonArray                = new JSONArray();
        cl_monitor                  = rootView.findViewById(R.id.monitorLayout);

        //mqtt
        mqttHelper = new MqttHelper(getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        try {
            json_phizio = new JSONObject(sharedPreferences.getString("phiziodetails", ""));
            Log.i("Patient View", json_phizio.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        connectivityManager         = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        patientId.setText(Objects.requireNonNull(getActivity()).getIntent().getStringExtra("patientId"));
        patientName.setText(Objects.requireNonNull(getActivity()).getIntent().getStringExtra("patientName"));
        creatGraphView();

        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        exerciseType = getActivity().getIntent().getStringExtra("exerciseType");
        Log.i("Exercise Type", exerciseType);

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emgJsonArray = new JSONArray();
                maxAngle = 0;
                minAngle = 360;
                maxEmgValue = 0;
                creatGraphView();
                //dataPoints.clear();
                k = 0;
                timer.setVisibility(View.GONE);
                SessionTimeForGraph = 0;
                stopBtn.setVisibility(View.VISIBLE);

                if (servicesDiscovered == false) {
                    stopBtn.setVisibility(View.GONE);
                    timer.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Make Sure Pheeze is On", Toast.LENGTH_SHORT).show();
                } else {

                    //BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(service1_uuid).getCharacteristic(characteristic1_service1_uuid);


                    //Sending the enable to the device

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mBluetoothGatt.setCharacteristicNotification(mCharacteristic, true);
                            mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);
                            Log.i("HEllo", "HELLO");
                        }
                    });


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendParticularDataToPheeze(exerciseType);
                        }
                    }, 100);


                    StartTime = SystemClock.uptimeMillis();
                    visiblity = View.GONE;
                    recieverState = true;
                    pheezeeState = true;
                    handler.postDelayed(runnable, 0);
                }
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Blehandler.removeCallbacks(null);
                timer.setBackgroundResource(R.drawable.rounded_start_button);
                visiblity = View.VISIBLE;
                stopBtn.setVisibility(View.GONE);
                timer.setVisibility(View.VISIBLE);


                //Discable notifications

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothGatt.setCharacteristicNotification(mCharacteristic,false);
                        mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                        mBluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);
                        mBluetoothGatt.writeCharacteristic(mCharacteristic);
                    }
                });



                TimeBuff += MillisecondTime;
                handler.removeCallbacks(runnable);
                MillisecondTime = 0L ;
                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;
                MilliSeconds = 0 ;
                pheezeeState =false;
                timer.setText("Start");
                recieverState = false;
                Log.i("minAngle",""+minAngle);
                if(maxAngle!=0&&!(maxAngle>180)&&minAngle!=180&&!(minAngle<0)) {
                    tsLong = System.currentTimeMillis();
                    String ts = tsLong.toString();
                    try {

                        JSONObject summary = new JSONObject();
                        summary.put("repetition", Integer.parseInt(Repetitions.getText().toString()));
                        summary.put("maxAngle", maxAngle);
                        summary.put("minAngle", minAngle);
                        summary.put("maxEmg", maxEmgValue);
                        summary.put("holdTime", holdTimeValue);
                        summary.put("sessionTime",time.getText().toString().substring(16));
                        summary.put("timeStamp", tsLong);
                        sessionObj.put("Summary", summary);
                        sessionObj.put("timeStamp", ts);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /*if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        try {
                            writeToPhoneStorage(sessionDataTrackedFile, sessionObj);
                            if(client!=null)
                                client.publish("sessionData", sessionObj.toString().getBytes(), 0, false);
                        } catch (MqttException e) {
                            writeToPhoneStorage(sessionDataUnTrackedFile, sessionObj);
                            e.printStackTrace();
                        }
                    } else {
                        writeToPhoneStorage(sessionDataUnTrackedFile, sessionObj);
                    }*/

//                StartTime = SystemClock.uptimeMillis();
//                handler.postDelayed(runnable, 0);
                    //initiatePopupWindow(v);
                    initiatePopupWindowModified(v);
                }else {
                    Toast.makeText(context,"your alignment is wrong!! try again,",Toast.LENGTH_LONG).show();
                }
            }
        });
        MillisecondTime = 0L ;
        StartTime = 0L ;
        TimeBuff = 0L ;
        UpdateTime = 0L ;
        Seconds = 0 ;
        Minutes = 0 ;
        MilliSeconds = 0 ;
        time.setText("Session time:   00 : 00 : 00");

        bluetoothAdapter = BluetoothSingelton.getmInstance().getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(context, "Bluetooth Disabled", Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
            //mBluetoothGatt = BluetoothGattSingleton.getmInstance().getAdapter();
            if(mBluetoothGatt!=null){
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
                Toast.makeText(context, "GATT CLOSED", Toast.LENGTH_SHORT).show();
            }
            Log.i("MAC ADDRESS",""+getActivity().getIntent().getStringExtra("deviceMacAddress"));
            //Toast.makeText(context, ""+getActivity().getIntent().getStringExtra("deviceMacAddress"), Toast.LENGTH_SHORT).show();
            remoteDevice = bluetoothAdapter.getRemoteDevice(getActivity().getIntent().getStringExtra("deviceMacAddress"));
        Toast.makeText(context, ""+remoteDevice.getName(), Toast.LENGTH_SHORT).show();
            if(remoteDevice==null){
                Toast.makeText(getActivity(), "Make sure pheeze is On.", Toast.LENGTH_SHORT).show();
            }
        //Toast.makeText(getActivity(), ""+remoteDevice.getAddress(), Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mBluetoothGatt = remoteDevice.connectGatt(getContext(),true,callback);
                }
            });

        //Toast.makeText(getActivity(), ""+mBluetoothGatt.getConnectionState(remoteDevice), Toast.LENGTH_SHORT).show();
            if(mBluetoothGatt!=null){
                Log.i("BLGATT","not connected");
            }

        try {
            sessionObj.put("PatientId",getActivity().getIntent().getStringExtra("patientId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //context.registerReceiver(new BluetoothReceiver(),filter);

        return rootView;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ConstraintSet mConstraintSet1 = new ConstraintSet();
        mConstraintSet1.clone(context,R.layout.fragment_monitor);
        ConstraintLayout mConstraintLayout = rootView.findViewById(R.id.monitorLayout);
        LinearLayout linearLayout = mConstraintLayout.findViewById(R.id.pIdAndPName);
        ProtractorView rangeOfMotionTemp = mConstraintLayout.findViewById(R.id.rangeOfMotion);
        TextView angleTemp = mConstraintLayout.findViewById(R.id.Angle);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            rangeOfMotionTemp.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,0);
            angleTemp.setTextColor(Color.parseColor("#000000"));
            angleTemp.setLayoutParams(params);
        }

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            rangeOfMotionTemp.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,-120,0,0);
            angleTemp.setTextColor(Color.parseColor("#4B7080"));
            angleTemp.setLayoutParams(params);
        }

        mConstraintSet1.setVisibility(R.id.timer,visiblity);
        mConstraintSet1.setVisibility(R.id.stopBtn,ConstraintSet.VISIBLE);
        mConstraintSet1.applyTo(mConstraintLayout);
    }

    private void creatGraphView()throws IndexOutOfBoundsException,RuntimeException {
        dataPoints = new ArrayList<>();
        dataPoints.add(new Entry(0,0));
        lineDataSet=new LineDataSet(dataPoints, "Emg Graph");
        lineDataSet.setDrawCircles(false);
        lineDataSet.setValueTextSize(0);
        lineDataSet.setDrawValues(false);
        lineDataSet.setColor(Color.parseColor("#4b7080"));
        lineData = new LineData(lineDataSet);
        lineChart.getXAxis();
        lineChart.setVisibleXRangeMaximum(1000);
        lineChart.getXAxis().setAxisMinimum(0f);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.setHorizontalScrollBarEnabled(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.setScaleXEnabled(true);
        lineChart.setData(lineData);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 1
        if(resultCode!=0){
            remoteDevice = bluetoothAdapter.getRemoteDevice(Objects.requireNonNull(getActivity()).getIntent().getStringExtra("deviceMacAddress"));

           new Handler(Looper.getMainLooper()).post(new Runnable() {
               @Override
               public void run() {
                   mBluetoothGatt = remoteDevice.connectGatt(context,true,callback);
               }
           });

        }
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
                Log.i("GATT CONNECTED", "Attempting to start the service discovery"+ gatt.discoverServices());
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
                servicesDiscovered = true;
                BluetoothGattCharacteristic characteristic = gatt.getService(service1_uuid).getCharacteristic(characteristic1_service1_uuid);
                Log.i("TEST", "INSIDE IF");

                if(characteristic1_service1_uuid.equals(characteristic.getUuid()))
                    mCharacteristic = characteristic;

                mBluetoothGatt = gatt;
                gatt.setCharacteristicNotification(characteristic,true);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(descriptor_characteristic1_service1_uuid);
                //BluetoothGattCharacteristic firmware_characteristic = gatt.getService(device_info_service1_uuid).getCharacteristic(firmware_version_characteristic_uuid);
                //descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                //gatt.writeDescriptor(descriptor);
                //mBluetoothGatt.readCharacteristic(characteristic);

                mBluetoothGattDescriptor = descriptor;
            }
            //BluetoothGattCharacteristic characteristic = gatt.getService(service1_uuid).getCharacteristic(characteristic1_service1_uuid);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(characteristic1_service1_uuid.equals(characteristic.getUuid())){
                Log.i("CHARACTERISTIC CHANGED","CHARACTERISTIC "+characteristic.getUuid()+" VALUE "+characteristic.getValue());
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            if(characteristic1_service1_uuid.equals(characteristic.getUuid())) {
                byte temp_byte[];

                temp_byte = characteristic.getValue();

                String header = String.valueOf(temp_byte[0]);

                byte header_main = temp_byte[0];
                byte header_sub = temp_byte[1];


                byte sub_byte[] = new byte[sub_byte_size];
                int j = 2;
                for (int i = 0; i < sub_byte_size; i++, j++) {
                    sub_byte[i] = temp_byte[j];
                }


                if (byteToStringHexadecimal(header_main).equals("AA")) {
                    if (byteToStringHexadecimal(header_sub).equals("01")) {
                        Message message = Message.obtain();
                        message.obj = sub_byte;

                        myHandler.sendMessage(message);

                        try {
                            sessionResult.put(message);
                            sessionObj.put("data", sessionResult);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Log.i("HELLO","HELLO");
                    }
                }
                else {
                    Toast.makeText(getActivity(), "HELLO", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if(descriptor_characteristic1_service1_uuid.equals(descriptor.getUuid())){
                Log.i("READ DESCRIPTOR", ""+status);
                Log.i("DESCRIPTOR VALUE", ""+descriptor.getValue());
            }
        }



        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.i("DES", "ENTERED DESCRIPTOR");
        }
    };



    public void StartMqtt(){
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, "tcp://18.236.141.171:1883", clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                static final String TAG = "Mqtt Message";

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    //syncDataToServer();
                    client.setCallback(new MqttCallback() {
                        public void messageArrived(String topic, MqttMessage message) {}
                        public void connectionLost(Throwable cause) {}
                        public void deliveryComplete(IMqttDeliveryToken token) {}
                    });
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static File generateFile(String fileName){
        File file = new File(context.getFilesDir(),  fileName);
        if(!file.exists()){
            try {
                    if(file.createNewFile())
                        System.out.println("File created");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }

    public boolean send(byte[] data) {

        if (mBluetoothGatt == null ) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return false;
        }
        if (mCharacteristic == null) {
            Log.w(TAG, "Send characteristic not found");
            return false;
        }

        BluetoothGattService service = mBluetoothGatt.getService(service1_uuid);

        if(service==null){
            if (mCharacteristic == null) {
                Log.w(TAG, "Send service not found");
                return false;
            }
        }
        Log.i("CHARACTERISTICCCCC", ""+data);
        if(characteristic1_service1_uuid.equals(mCharacteristic.getUuid())){
            Log.i("TRUE", "TRUE");
        }


        mCharacteristic.setValue(data);

        return mBluetoothGatt.writeCharacteristic(mCharacteristic);
    }

    public void writeToPhoneStorage(File file,JSONObject data){
        JSONArray temp;
        try {

            temp = readFromFile(file);
            if (temp == null)
                temp = new JSONArray();
            temp.put(data);
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(temp.toString().getBytes());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONArray readFromFile(File file){
        try {
            FileInputStream fi = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fi);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }
            fi.close();
            if (!(stringBuilder.length()<=0))
                return new JSONArray(stringBuilder.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof OnFragmentInteractionListener)){
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        if (client!=null){
            client.unregisterResources();
            client.close();
        }
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPause() {
        super.onPause();
//        context.unregisterReceiver(mReceiver);
//        context.unregisterReceiver(new BluetoothReceiver());
    }

    @Override
    public void onResume(){
        super.onResume();
        //context.registerReceiver(mReceiver,filter);
    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            timeText ="Session time:   " + String.format("%02d", Minutes) + " : " + String.format("%02d", Seconds) + " : " + String.format("%02d", MilliSeconds);

            time.setText(timeText);

            handler.postDelayed(this, 0);
        }

    };

    @SuppressLint("HandlerLeak")
    public Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            byte[] data = (byte[]) msg.obj;
            //Angle.setText(Integer.toString(data[0]& 0xFF));
            int t = data[4] << 24 | (data[3] & 0xff) << 16 | (data[2] & 0xff) << 8
                    | (data[1] & 0xff);
            System.out.println("by pheezee"+t);
                    lineData.addEntry(new Entry(((float)t)/1000, data[0] & 0xFF), 0);
                    lineChart.getXAxis().setAxisMinimum(0f);
                    lineChart.notifyDataSetChanged();
                    lineChart.invalidate();
                    lineChart.moveViewToX( ((float)t)/1000);
//            for(int i=1;i<5;i++){
//                    int emg = data[i] & 0xFF;
//                    lineData.addEntry(new Entry(protime, data[i] & 0xFF), 0);
//                    lineChart.notifyDataSetChanged();
//                    lineChart.invalidate();
//                    protime += 0.001f;
//                    lineChart.moveViewToX(protime+1);
//
//            }
        }
    };

    @SuppressLint("HandlerLeak")
    public final Handler myHandler = new Handler() {
        public void handleMessage(Message message ) {
            int angleDetected,num_of_reps, hold_time_minutes, hold_time_seconds;
            int[] emg_data;
            byte[] sub_byte;
            sub_byte = (byte[]) message.obj;
            emg_data = constructEmgData(sub_byte);
            angleDetected = getAngleFromData(sub_byte[20],sub_byte[21]);
            num_of_reps = getNumberOfReps(sub_byte[22], sub_byte[23]);
            hold_time_minutes = sub_byte[24];
            hold_time_seconds = sub_byte[25];
            String angleValue = ""+angleDetected;
            String repetitionValue = ""+num_of_reps;

            String minutesValue = ""+hold_time_minutes;
            String secondsValue = ""+hold_time_seconds;
            holdTimeValue = minutesValue+" : "+secondsValue;

            if(angleDetected>0 && angleDetected<=180)
                rangeOfMotion.setAngle(angleDetected);
            Angle.setText(angleValue);
            for (int i=0;i<emg_data.length;i++) {
                lineData.addEntry(new Entry((float) UpdateTime / 1000, emg_data[i]), 0);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
                lineChart.getXAxis();
                lineChart.getAxisLeft();
                lineChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return (int) value + "μA";
                    }
                });
                if (UpdateTime / 1000 > 3)
                    lineChart.setVisibleXRangeMaximum(5f);
                lineChart.moveViewToX((float) UpdateTime / 1000);
            }
//            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            for (int i=0;i<emg_data.length;i++) {
                emgJsonArray.put(emg_data[i]);
                EMG.setText(Integer.toString(emg_data[i]));
            }
            Repetitions.setText(repetitionValue);
            LinearLayout.LayoutParams params;
            params = (LinearLayout.LayoutParams) emgSignal.getLayoutParams();
            for (int i=0;i<emg_data.length;i++) {
                maxEmgValue = maxEmgValue < emg_data[i] ? emg_data[i] : maxEmgValue;
                if (maxEmgValue == 0)
                    maxEmgValue = 1;
                params.height = ((View) emgSignal.getParent()).getMeasuredHeight() * emg_data[i] / maxEmgValue;
            }

            maxAngle = maxAngle<angleDetected?angleDetected:maxAngle;
            minAngle = minAngle>angleDetected?angleDetected:minAngle;
            emgSignal.setLayoutParams(params);
            holdTime.setText(holdTimeValue);

        }
    };

    private  void initiatePopupWindowModified(View v){
        View layout = getLayoutInflater().inflate(R.layout.session_summary, null);

        report = new PopupWindow(layout, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT,true);
        report.setWindowLayoutMode(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT);
        report.setOutsideTouchable(true);
        report.showAtLocation(v, Gravity.CENTER, 0, 0);

        ProgressBar progressBar = layout.findViewById(R.id.progress_max_emg);
        progressBar.setMax(400);
        progressBar.setProgress(260);
        progressBar.setEnabled(false);

        //Gettig all the view items from the layout

        LinearLayout ll_min_max_arc = (LinearLayout)layout.findViewById(R.id.ll_min_max_arc);
        TextView tv_patient_name = (TextView)layout.findViewById(R.id.tv_summary_patient_name);
        TextView tv_patient_id = (TextView)layout.findViewById(R.id.tv_summary_patient_id);
        TextView tv_held_on = (TextView)layout.findViewById(R.id.session_held_on);
        TextView tv_overall_summary = (TextView)layout.findViewById(R.id.tv_overall_summary);
        TextView tv_min_angle = (TextView)layout.findViewById(R.id.tv_min_angle);
        TextView tv_max_angle = (TextView)layout.findViewById(R.id.tv_max_angle);
        TextView tv_total_time = (TextView)layout.findViewById(R.id.tv_total_time);
        TextView tv_action_time = (TextView)layout.findViewById(R.id.tv_action_time);
        TextView tv_hold_time = (TextView)layout.findViewById(R.id.tv_hold_time);
        TextView tv_num_of_reps = (TextView)layout.findViewById(R.id.tv_num_of_reps);
        TextView tv_max_emg = (TextView)layout.findViewById(R.id.tv_max_emg);


        //Share and cancel image view

        LinearLayout summary_go_back = (LinearLayout) layout.findViewById(R.id.summary_go_back);
        LinearLayout summary_share = (LinearLayout) layout.findViewById(R.id.summary_share);


        summary_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report.dismiss();
            }
        });


        //Emg Progress Bar

        ProgressBar pb_max_emg = (ProgressBar)layout.findViewById(R.id.progress_max_emg);

        //Setting the text views
        tv_patient_id.setText(patientId.getText().toString());
        tv_patient_name.setText(patientName.getText().toString());


        //for held on date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(new Date(tsLong));
        String dateString_date = formatter_date.format(new Date(tsLong));
        tv_held_on.setText(dateString_date);


        tv_min_angle.setText(Integer.toString(minAngle)+"°");
        tv_max_angle.setText(Integer.toString(maxAngle)+"°");


        //total session time
        String tempSessionTime = time.getText().toString().substring(16);
        tempSessionTime = tempSessionTime.substring(0,2)+"m"+tempSessionTime.substring(3,7)+"s";
        tv_total_time.setText(tempSessionTime);


        tv_action_time.setText(tempSessionTime);
        tv_hold_time.setText(holdTimeValue.substring(0,2)+"m"+holdTimeValue.substring(2)+"s");


        tv_num_of_reps.setText(Repetitions.getText().toString());
        tv_max_emg.setText(Integer.toString(maxEmgValue)+"μA");

        //Creating the arc
        ArcView arcView = new ArcView(getActivity());
        arcView.setMaxAngle(maxAngle);
        arcView.setMinAngle(minAngle);
        TextView tv_180 = (TextView)layout.findViewById(R.id.tv_180);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            tv_180.setPadding(5,1,170,1);
        }

        arcView.setRangeColor(getResources().getColor(R.color.good_green));
        ll_min_max_arc.addView(arcView);


        //Max Emg Progress
        pb_max_emg.setMax(400);
        pb_max_emg.setProgress(maxEmgValue);
        pb_max_emg.setEnabled(false);


        storeLocalSessionDetails(dateString,tempSessionTime);


    }

    private void initiatePopupWindow(View v) {

            View layout = getLayoutInflater().inflate(R.layout.session_analysis, null);

            report = new PopupWindow(layout, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            report.setOutsideTouchable(true);
            report.setContentView(layout);
            report.setFocusable(true);
            report.showAtLocation(rootView, Gravity.CENTER, 0, 0);

            //imp stuff 921-968
            LinearLayout cancelbtn = layout.findViewById(R.id.cancel_action);
            LinearLayout shareIcon = layout.findViewById(R.id.shareIcon);
            final ConstraintLayout summaryView = layout.findViewById(R.id.summaryView);
            TextView popUpPatientId = layout.findViewById(R.id.patientId);
            TextView popUpPatientName = layout.findViewById(R.id.patientName);
            TextView maxAngleView = layout.findViewById(R.id.maxAngle);
            TextView repetitions = layout.findViewById(R.id.totalReps);
            TextView minAngleView  = layout.findViewById(R.id.minAngle);
            TextView maxEmgView  = layout.findViewById(R.id.maxEmg);
            TextView holdTimeView  = layout.findViewById(R.id.holdtime);
            TextView heldTime = layout.findViewById(R.id.heldtime);
            TextView sessionTime = layout.findViewById(R.id.sessionTime);
            repetitions.setText(Repetitions.getText().toString());
            popUpPatientId.setText(patientId.getText().toString());
            popUpPatientName.setText(patientName.getText().toString());
            maxAngleView.setText(Integer.toString(maxAngle)+"°");
            minAngleView.setText(Integer.toString(minAngle)+"°");
            maxEmgView.setText(Integer.toString(maxEmgValue)+"μA");
            holdTimeView.setText(holdTimeValue.substring(0,2)+"m"+holdTimeValue.substring(2)+"s");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(new Date(tsLong));
            heldTime.setText(dateString);
            String tempSessionTime = time.getText().toString().substring(16);
            tempSessionTime = tempSessionTime.substring(0,2)+"m"+tempSessionTime.substring(3,7)+"s";
            sessionTime.setText(tempSessionTime);





            cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    report.dismiss();
                }
            });
            shareIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,"shot",Toast.LENGTH_SHORT).show();
                    OnClickShare(summaryView);
                }
            });


        storeLocalSessionDetails(dateString,tempSessionTime);
    }

    private void storeLocalSessionDetails(String dateString,String tempsession) {
        JSONArray array ;
        if (!sharedPreferences.getString("phiziodetails", "").equals("")) {
            Log.i("Patient View","Inside");
            try {
                jsonData = new JSONArray(json_phizio.getString("phiziopatients"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(jsonData.length()>0) {
                for (int i = 0; i < jsonData.length(); i++) {
                    try {
                        int numofsessions;
                        if (jsonData.getJSONObject(i).get("patientid").equals(patientId.getText().toString())) {
                            if(jsonData.getJSONObject(i).has("numofsession")) {
                                numofsessions = Integer.parseInt(jsonData.getJSONObject(i).get("numofsession").toString());
                                numofsessions += 1;
                            }
                            else {
                                numofsessions = 1;
                            }
                            jsonData.getJSONObject(i).put("numofsession",""+numofsessions);
                            array = new JSONArray(jsonData.getJSONObject(i).getString("sessions"));
                            JSONObject object = new JSONObject();
                            object.put("heldon",dateString);
                            object.put("maxangle",maxAngle);
                            object.put("minangle",minAngle);
                            object.put("maxemg",maxEmgValue);
                            object.put("holdtime",holdTimeValue);
                            object.put("sessiontime",tempsession);
                            object.put("numofreps",Repetitions.getText().toString());
                            array.put(object);
                            jsonData.getJSONObject(i).put("sessions",array);
                            json_phizio.put("phiziopatients",jsonData);
                            editor = sharedPreferences.edit();
                            editor.putString("phiziodetails", json_phizio.toString());
                            editor.commit();
                            object.put("numofsessions",""+numofsessions);
                            object.put("phizioemail",json_phizio.get("phizioemail"));
                            object.put("patientid",patientId.getText().toString());

                            //mqtt publishing

                            MqttMessage mqttMessage = new MqttMessage();
                            mqttMessage.setPayload(object.toString().getBytes());

                            object.put("emgdata",emgJsonArray);


                            mqttHelper.publishMqttTopic(mqtt_publish_add_patient_session,mqttMessage);
                            mqttMessage.clearPayload();
                            mqttMessage.setPayload(object.toString().getBytes());

                            Log.i("message",mqttMessage.toString());
                            mqttHelper.publishMqttTopic(mqtt_publish_add_patient_session_emg_data,mqttMessage);

                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public void OnClickShare(View view){

        Bitmap bitmap =getBitmapFromView(view);
        try {
            File file = new File(context.getExternalCacheDir(),"sessionSummary.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }


    public void sendParticularDataToPheeze(String string){

        switch (string){
            case "knee":{
                byte b[] = hexStringToByteArray("AA01");
                if(send(b)){
                    Log.i("SENDING","MESSAGE SENT");
                }
                else {
                    Log.i("Knee", "true");
                    Log.i("SENDING","UNSUCCESSFULL");
                }
                break;
            }


            case "sholder":{

                break;
            }

            case "hip":{
                break;
            }

            case "elbow":{
                byte b[] = hexStringToByteArray("AA02");
                if(send(b)){
                    Log.i("SENDING","MESSAGE SENT");
                }
                else {
                    Log.i("SENDING","UNSUCCESSFULL");
                }
                break;
            }
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






    public int[] constructEmgData(byte[] sub_byte){
        int k=0;
        int[] emg_data = new int[10];
        for (int i=0;i<20;i++){
            int a = sub_byte[i]&0xFF;
            int b = sub_byte[i+1]&0xFF;

            emg_data[k] = b<<8 | a;
            i++;
            k++;
        }
        return emg_data;
    }

    public int getAngleFromData(byte a, byte b){

        int a32 = a;
        int a33 = b;

        int angle = a33<<8 | a32&0xFF;
        return angle;
    }

    public int getNumberOfReps(byte a, byte b){
        int a32 = a;
        int a33 = b;

        int num_of_reps = (a32 & 0xff) |
                ((a33 & 0xff) << 8);
        return num_of_reps;


    }


    public String byteToStringHexadecimal(byte b){
        String st = String.format("%02X",b);
        return st;
    }


}
