package com.startoonlabs.apps.pheezee.activities;

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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.classes.BluetoothSingelton;
import com.startoonlabs.apps.pheezee.popup.SessionSummaryPopupWindow;
import com.startoonlabs.apps.pheezee.repository.MqttSyncRepository;
import com.startoonlabs.apps.pheezee.utils.AngleOperations;
import com.startoonlabs.apps.pheezee.utils.BatteryOperation;
import com.startoonlabs.apps.pheezee.utils.ByteToArrayOperations;
import com.startoonlabs.apps.pheezee.utils.TakeScreenShot;
import com.startoonlabs.apps.pheezee.utils.ValueBasedColorOperations;
import com.startoonlabs.apps.pheezee.views.ArcViewInside;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MonitorActivity extends AppCompatActivity implements MqttSyncRepository.GetSessionNumberResponse {

    //session inserted on server
    private boolean  sessionCompleted = false, first_packet=true, inside_ondestroy = false;;
    MqttSyncRepository repository;
    String json_phizioemail="";
    TextView tv_max_angle, tv_min_angle, tv_max_emg, tv_snap, Repetitions, holdTime,
            tv_session_no, tv_body_part, tv_repsselected, EMG, time, patientId, patientName, tv_action_time;

    private int ui_rate = 0, gain_initial=20, software_gain = 0, maxAnglePart, minAnglePart, angleCorrection = 0,
            currentAngle=0, Seconds, Minutes, maxAngle, minAngle, maxEmgValue;
    int REQUEST_ENABLE_BT = 1;
    public final int sub_byte_size = 48;
    boolean angleCorrected = false,devicePopped = false, servicesDiscovered = false, isSessionRunning=false, pheezeeState = false, recieverState=false;
    String bodypart,orientation="NO", timeText ="", holdTimeValue="0:0", exerciseType;
    SharedPreferences sharedPreferences;
    JSONObject json_phizio = new JSONObject();
    JSONObject sessionObj = new JSONObject();
    JSONArray emgJsonArray, romJsonArray;
    ImageView iv_back_monitor;
    File  file_session_emgdata, file_dir_session_emgdata,file_session_romdata,file_session_sessiondetails;
    FileOutputStream  outputStream_session_emgdata,outputStream_session_romdata,outputStream_session_sessiondetails;
    int visiblity=View.VISIBLE;
    List<Entry> dataPoints;
    LineChart lineChart;
    LineDataSet lineDataSet;
    BluetoothGattCharacteristic mCharacteristic;
    BluetoothGatt mBluetoothGatt;
    ArcViewInside arcViewInside;
    ImageView iv_angle_correction;
    LineData lineData, lineDataNew;
    Button timer, btn_emg_decrease_gain, btn_emg_increase_gain, stopBtn, cancelBtn;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    Handler handler;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice remoteDevice;
    LinearLayout emgSignal;
    Date rawdata_timestamp;
    Long tsLong=0L;
    BluetoothGattDescriptor mBluetoothGattDescriptor;
    ArrayList<BluetoothGattCharacteristic> arrayList;
    AngleOperations angleOperations;

    //All the constant uuids are written here
    public static final UUID service1_uuid = UUID.fromString("909a1400-9693-4920-96e6-893c0157fedd");
    public static final UUID characteristic1_service1_uuid = UUID.fromString("909a1401-9693-4920-96e6-893c0157fedd");
    public static final UUID descriptor_characteristic1_service1_uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    /**
     * popup window when device gets disconnected in middle of session.
     */
    public void deviceDisconnectedPopup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MonitorActivity.this);
        builder.setTitle("Device Disconnected");
        builder.setMessage("please come in range to the device to continue the session");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setNegativeButton("End Session", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopBtn.performClick();
            }
        });
        builder.show();
    }


    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600)
        {
            setContentView(R.layout.activity_monitor);
        }
        else
        {
            setContentView(R.layout.fragment_monitor);
        }

        arrayList = new ArrayList<>();
        PatientsView.insideMonitor = true;
        angleOperations = new AngleOperations();
        lineChart                   = findViewById(R.id.chart);
//        Angle                       = findViewById(R.id.Angle);
        EMG                         = findViewById(R.id.emgValue);
//        rangeOfMotion               = findViewById(R.id.rangeOfMotion);
        arcViewInside               = findViewById(R.id.arcViewInside);
        Repetitions                 = findViewById(R.id.Repetitions);
        holdTime                    = findViewById(R.id.holdtime);
        timer                       = findViewById(R.id.timer);
        stopBtn                     = findViewById(R.id.stopBtn);
        patientId                   = findViewById(R.id.patientId);
        patientName                 = findViewById(R.id.patientName);
        time                        = findViewById(R.id.displayTime);
        emgSignal                   = findViewById(R.id.emg);
        tv_session_no               = findViewById(R.id.tv_session_no);
        tv_body_part                = findViewById(R.id.bodyPart);
        cancelBtn                   = findViewById(R.id.cancel);
        iv_angle_correction           = findViewById(R.id.tv_angleCorrection);
        tv_action_time              = findViewById(R.id.tv_action_time);
        tv_max_angle                = findViewById(R.id.tv_max_angle);
        tv_min_angle                = findViewById(R.id.tv_min_angle);
        tv_max_emg                  = findViewById(R.id.tv_max_emg_show);
        tv_repsselected             = findViewById(R.id.repsSelected);
        btn_emg_decrease_gain       = findViewById(R.id.btn_emg_decrease_gain);
        btn_emg_increase_gain       = findViewById(R.id.btn_emg_increase_gain);

        handler                     = new Handler();
        emgJsonArray                = new JSONArray();
        romJsonArray                = new JSONArray();
        repository = new MqttSyncRepository(getApplication());
        repository.setOnSessionNumberResponse(this);

        iv_back_monitor = findViewById(R.id.iv_back_monitor);
        tv_snap = findViewById(R.id.snap_monitor);
        tv_snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakeScreenShot screenShot = new TakeScreenShot(MonitorActivity.this,patientName.getText().toString(),patientId.getText().toString());
                screenShot.takeScreenshot(null);
                showToast("Took Screenshot");
            }
        });
        btn_emg_increase_gain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PatientsView.sessionStarted==true){
                    btn_emg_decrease_gain.setBackgroundResource(R.drawable.monitor_gain_btn);
                    if(gain_initial<120){
                        gain_initial+=10;
                        lineChart.zoom(1.4f,1.4f,ui_rate,ui_rate);
                        if(gain_initial==120){
                            btn_emg_increase_gain.setBackgroundColor(ContextCompat.getColor(MonitorActivity.this,R.color.home_semi_red));
                        }
                    }
                    byte[] gain_increase = ByteToArrayOperations.hexStringToByteArray("AD01");
                    send(gain_increase);
                }
                else {
                    showToast("Please start the session!");
                }
            }
        });
        btn_emg_decrease_gain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PatientsView.sessionStarted==true){
                    btn_emg_increase_gain.setBackgroundResource(R.drawable.monitor_gain_btn);
                    if(gain_initial>10){
                        gain_initial-=10;
                        if (gain_initial==10){
                            btn_emg_decrease_gain.setBackgroundColor(ContextCompat.getColor(MonitorActivity.this,R.color.home_semi_red));
                        }
                        lineChart.zoomOut();
                    }
                    byte[] gain_decrease = ByteToArrayOperations.hexStringToByteArray("AD02");
                    send(gain_decrease);
                }
                else {
                    showToast("Please start the session!");
                }
            }
        });
        tv_snap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    tv_snap.setAlpha(0.4f);
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    tv_snap.setAlpha(1f);
                }
                return false;
            }
        });
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            json_phizio = new JSONObject(sharedPreferences.getString("phiziodetails", ""));
            json_phizioemail = json_phizio.getString("phizioemail");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        patientId.setText(getIntent().getStringExtra("patientId"));
        patientName.setText(getIntent().getStringExtra("patientName"));
        //setting session number
        repository.getPatientSessionNo(patientId.getText().toString());
        bodypart = getIntent().getStringExtra("exerciseType");
        orientation = getIntent().getStringExtra("orientation");
        tv_body_part.setText(tv_body_part.getText().toString().concat(bodypart));
        tv_body_part.setText(orientation+"-"+bodypart+"-"+BodyPartSelection.musclename);
        if(BodyPartSelection.repsselected!=0){
            tv_repsselected.setText("/".concat(String.valueOf(BodyPartSelection.repsselected)));
        }
        else {
            tv_repsselected.setVisibility(View.GONE);
        }
        maxAnglePart = angleOperations.getMaxAngle(bodypart);
        minAnglePart = angleOperations.getMinAngle(bodypart);
        arcViewInside.setMinAngle(0);
        arcViewInside.setMaxAngle(0);

        creatGraphView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        exerciseType = getIntent().getStringExtra("exerciseType");
        /**
         * calls start session
         */
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = BatteryOperation.getDialogMessageForLowBattery(PatientsView.deviceBatteryPercent,MonitorActivity.this);
                if(!message.equalsIgnoreCase("c")){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MonitorActivity.this);
                    builder.setTitle("Battery Low");
                    builder.setMessage(message);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startSession();
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
                    startSession();
            }
        });
        /**
         * Cancel session
         */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer.getVisibility()==View.GONE){
                    sessionCompleted=true;
                    PatientsView.sessionStarted = false;

                    timer.setBackgroundResource(R.drawable.rounded_start_button);
                    visiblity = View.VISIBLE;
                    stopBtn.setVisibility(View.GONE);
                    timer.setVisibility(View.VISIBLE);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if(mBluetoothGatt!=null && mBluetoothGattDescriptor!=null && mCharacteristic!=null) {
                                mBluetoothGatt.setCharacteristicNotification(mCharacteristic, false);
                                mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                                mBluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);
                                mBluetoothGatt.writeCharacteristic(mCharacteristic);
                            }
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
                    pheezeeState =false;
                    timer.setText(R.string.timer_start);
                    recieverState = false;
                    isSessionRunning=false;
//                if(maxAngle!=0&&!(maxAngle>180)&&minAngle!=180&&!(minAngle<0)) {
                    tsLong = System.currentTimeMillis();
                    insertValuesAndNotifyMediaStore();
                }
            }
        });
        /**
         * Stop session
         */
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionCompleted=true;
                PatientsView.sessionStarted = false;
                cancelBtn.setVisibility(View.GONE);
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
                pheezeeState =false;
                timer.setText(R.string.timer_start);
                recieverState = false;
                isSessionRunning=false;
                tsLong = System.currentTimeMillis();
                initiatePopupWindowModified();
                insertValuesAndNotifyMediaStore();
            }
        });

        /**
         * Angle correction popup
         */
        iv_angle_correction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MonitorActivity.this);
                builder.setTitle("Correct Angle");
                builder.setMessage("please enter the expected angle");
                final EditText editText = new EditText(MonitorActivity.this);
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                editText.setLayoutParams(lp);
                builder.setView(editText);
                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!editText.getText().toString().equals("")){
                            try {
                                if(PatientsView.sessionStarted) {
                                    angleCorrection = Integer.parseInt(editText.getText().toString());
                                    angleCorrected = true;
                                    maxAngle=angleCorrection;
                                    minAngle=angleCorrection;
                                    angleCorrection-=currentAngle;
                                    currentAngle+=angleCorrection;


                                }
                            }catch (NumberFormatException e){

                            }
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                if(PatientsView.sessionStarted) {
                    builder.show();
                }
                else {
                    showToast("Please start session!");
                }
            }
        });
        MillisecondTime = 0L ;
        StartTime = 0L ;
        TimeBuff = 0L ;
        UpdateTime = 0L ;
        Seconds = 0 ;
        Minutes = 0 ;
        time.setText("Session time:   00 : 00");

        bluetoothAdapter = BluetoothSingelton.getmInstance().getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            showToast("Bluetooth Disabled");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if(mBluetoothGatt!=null){
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
        if(!getIntent().getStringExtra("deviceMacAddress").equals(""))

            remoteDevice = bluetoothAdapter.getRemoteDevice(getIntent().getStringExtra("deviceMacAddress"));
        if(remoteDevice==null){
            showToast("Make sure pheeze is On.");
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(remoteDevice!=null)
                    mBluetoothGatt = remoteDevice.connectGatt(MonitorActivity.this,true,callback);
            }
        });

        if(mBluetoothGatt!=null){
            Log.i("BLGATT","not connected");
        }

        try {
            sessionObj.put("PatientId",this.getIntent().getStringExtra("patientId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        iv_back_monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void startSession(){
        updateGainView();
        sessionCompleted=false;first_packet=true;
        ui_rate = 0;devicePopped=false;
        PatientsView.sessionStarted = true;
        isSessionRunning = true;
        angleCorrected = false;
        angleCorrection=0;
        emgJsonArray = new JSONArray();
        romJsonArray = new JSONArray();
        maxAngle = 0;minAngle = 360;maxEmgValue = 0;
        creatGraphView();
        timer.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.VISIBLE);
        if (!servicesDiscovered) {
            stopBtn.setVisibility(View.GONE);
            timer.setVisibility(View.VISIBLE);
            showToast("Make Sure Pheeze is On");
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendParticularDataToPheeze(exerciseType);
                }
            }, 100);
            rawdata_timestamp = Calendar.getInstance().getTime();
            android.text.format.DateFormat df = new android.text.format.DateFormat();
//            String s = rawdata_timestamp.toString().substring(0, 19);
            String s = String.valueOf(DateFormat.format("yyyy-MM-dd hh-mm-ssa", rawdata_timestamp));
            String child = patientName.getText().toString()+patientId.getText().toString();
            file_dir_session_emgdata = new File(Environment.getExternalStorageDirectory()+"/Pheezee/files/EmgData/"+child+"/sessiondata/",s);
            if (!file_dir_session_emgdata.exists()) {
                file_dir_session_emgdata.mkdirs();
            }
            file_session_emgdata = new File(file_dir_session_emgdata, "emg.txt");
            file_session_romdata = new File(file_dir_session_emgdata, "rom.txt");
            file_session_sessiondetails = new File(file_dir_session_emgdata, "sessiondetails.txt");
            try {
                file_session_emgdata.createNewFile();
                file_session_romdata.createNewFile();
                file_session_sessiondetails.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream_session_emgdata = new FileOutputStream(file_session_emgdata, true);
                outputStream_session_romdata = new FileOutputStream(file_session_romdata, true);
                outputStream_session_sessiondetails = new FileOutputStream(file_session_sessiondetails, true);

                //emg file output stream
                outputStream_session_emgdata.write("EMG".getBytes());
                outputStream_session_emgdata.write("\n".getBytes());

                //rom file output stream
                outputStream_session_romdata.write("EMG".getBytes());
                outputStream_session_romdata.write("\n".getBytes());


                //sessiondetails file output stream
                outputStream_session_sessiondetails.write("Patient Name : ".getBytes());
                outputStream_session_sessiondetails.write(patientName.getText().toString().getBytes());
                outputStream_session_sessiondetails.write("\n".getBytes());
                outputStream_session_sessiondetails.write("Patient Id: ".concat(patientId.getText().toString()).getBytes());
                outputStream_session_sessiondetails.write("\n".getBytes());
                outputStream_session_sessiondetails.write("Orientation-Bodypart-ExerciseName : ".concat(orientation+"-"+bodypart+"-"+BodyPartSelection.exercisename).getBytes());
                outputStream_session_sessiondetails.write("\n".getBytes());
                outputStream_session_sessiondetails.write("Painscale-Muscletone : ".concat(BodyPartSelection.painscale+"-"+BodyPartSelection.muscletone).getBytes());
                outputStream_session_sessiondetails.write("\n".getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StartTime = SystemClock.uptimeMillis();
            visiblity = View.GONE;
            recieverState = true;
            pheezeeState = true;
            handler.postDelayed(runnable, 0);
        }
    }

    /**
     * Updates the view of gain to default
     */
    private void updateGainView() {
        btn_emg_decrease_gain.setBackgroundResource(R.drawable.monitor_gain_btn);
        btn_emg_increase_gain.setBackgroundResource(R.drawable.monitor_gain_btn);
    }

    /**
     * Inserts the summary values in files and also tells the media to scan the files for visibility when connected to the laptop.
     */
    private void insertValuesAndNotifyMediaStore() {
        try {


            outputStream_session_sessiondetails.write("Angle-Corrected : ".concat(String.valueOf(angleCorrection)).getBytes());

            outputStream_session_sessiondetails.write("\n\n\n".getBytes());
            outputStream_session_sessiondetails.write("Session Details".getBytes());
            outputStream_session_sessiondetails.write("\n".getBytes());
            outputStream_session_sessiondetails.write("Session Stop Pressed".getBytes());
            outputStream_session_sessiondetails.write("\n".getBytes());
            outputStream_session_sessiondetails.write("Max Angle:".concat(String.valueOf(maxAngle)).getBytes());
            outputStream_session_sessiondetails.write("\n".getBytes());
            outputStream_session_sessiondetails.write("Min Angle:".concat(String.valueOf(minAngle)).getBytes());
            outputStream_session_sessiondetails.write("\n".getBytes());
            outputStream_session_sessiondetails.write("Max Emg:".concat(String.valueOf(maxEmgValue)).getBytes());
            outputStream_session_sessiondetails.write("\n".getBytes());
            outputStream_session_sessiondetails.write("Hold Time:".concat(holdTimeValue).getBytes());
            outputStream_session_sessiondetails.write("\n".getBytes());
            outputStream_session_sessiondetails.write("Num of Reps:".concat(Repetitions.getText().toString()).getBytes());
            outputStream_session_sessiondetails.write("\n".getBytes());
            outputStream_session_sessiondetails.write("Session Time:".concat(timer.getText().toString()).getBytes());
            outputStream_session_sessiondetails.write("\n".getBytes());
            outputStream_session_sessiondetails.write("Painscale-Muscletone : ".concat(BodyPartSelection.painscale+"-"+BodyPartSelection.muscletone).getBytes());
            outputStream_session_sessiondetails.write("\n".getBytes());
            outputStream_session_sessiondetails.write("Comment : ".concat(BodyPartSelection.commentsession).getBytes());
            outputStream_session_sessiondetails.write("\n\n\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaScannerConnection.scanFile(
                getApplicationContext(),
                new String[]{file_session_emgdata.getAbsolutePath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
        MediaScannerConnection.scanFile(
                getApplicationContext(),
                new String[]{file_session_romdata.getAbsolutePath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
        MediaScannerConnection.scanFile(
                getApplicationContext(),
                new String[]{file_session_sessiondetails.getAbsolutePath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
//                        Log.v("grokkingandroid",
//                                "file " + path + " was scanned seccessfully: " + uri);
                    }
                });
    }

    /**
     * Refreshes the line graph
     */
    private void creatGraphView() {
        lineChart.setHardwareAccelerationEnabled(true);
        dataPoints = new ArrayList<>();
        dataPoints.add(new Entry(0,0));
        lineDataSet=new LineDataSet(dataPoints, "Emg Graph");
        lineDataSet.setDrawCircles(false);
        lineDataSet.setValueTextSize(0);
        lineDataSet.setDrawValues(false);
        lineDataSet.setColor(getResources().getColor(R.color.pitch_black));
        lineData = new LineData(lineDataSet);
        lineDataNew = new LineData(lineDataSet);    //for 30000
        lineChart.getXAxis();
        lineChart.getXAxis().setAxisMinimum(0f);
        lineChart.getAxisLeft().setSpaceTop(60f);
        lineChart.getAxisRight().setSpaceTop(60f);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getAxisLeft().setStartAtZero(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.setHorizontalScrollBarEnabled(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.setScaleXEnabled(true);
        lineChart.fitScreen();
        lineChart.setData(lineData);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=0){
            remoteDevice = bluetoothAdapter.getRemoteDevice(getIntent().getStringExtra("deviceMacAddress"));
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mBluetoothGatt = remoteDevice.connectGatt(MonitorActivity.this,true,callback);
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
                Log.i("GATT CONNECTED", "Attempting to start the service discovery in monitoring"+ gatt.discoverServices());
            }
            else if (newState == BluetoothProfile.STATE_CONNECTING){
//                Log.i("GATT DISCONNECTED", "GATT server is being connected");
            }
            else if(newState == BluetoothProfile.STATE_DISCONNECTING){
//                Log.i("GATT DISCONNECTING","Gatt server disconnecting");
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED){
//                Log.i("GATT DISCONNECTED", "Gatt server disconnected");
            }
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if(characteristic.getUuid().equals(characteristic1_service1_uuid)) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothGatt.setCharacteristicNotification(mCharacteristic, true);
                        mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mBluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);
                    }
                });
            }
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
                mBluetoothGattDescriptor = characteristic.getDescriptor(descriptor_characteristic1_service1_uuid);
                arrayList.add(mCharacteristic);
                if(timer.getVisibility()==View.GONE){
                    PatientsView.sessionStarted = true;
                    devicePopped = false;
                    sendParticularDataToPheeze(exerciseType);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }

        //        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            if(characteristic1_service1_uuid.equals(characteristic.getUuid())) {
                byte[] temp_byte;
                temp_byte = characteristic.getValue();
                byte header_main = temp_byte[0];
                byte header_sub = temp_byte[1];
                byte[] sub_byte = new byte[sub_byte_size];
                if (ByteToArrayOperations.byteToStringHexadecimal(header_main).equals("AA")) {
                    if (ByteToArrayOperations.byteToStringHexadecimal(header_sub).equals("01")) {
                        int j = 2;
                        for (int i = 0; i < sub_byte_size; i++, j++) {
                            sub_byte[i] = temp_byte[j];
                        }
                        Message message = Message.obtain();
                        message.obj = sub_byte;
                        if(!sessionCompleted && !first_packet) {
                            myHandler.sendMessage(message);
                        }
                        else {
                            first_packet=false;
                        }
                    }
                }
                if(ByteToArrayOperations.byteToStringHexadecimal(header_main).equals("AF")){
                    software_gain = header_sub;
                    Message message = Message.obtain();
                    message.obj = sub_byte;
                    myHandler.sendMessage(message);
                }
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if(inside_ondestroy){
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
            }
        }
    };

    /**
     * sends data to the device by writing values to the characteristic
     * @param data
     * @return
     */
    public boolean send(byte[] data) {
        if (mBluetoothGatt == null ) {
            return false;
        }
        if (mCharacteristic == null) {
            return false;
        }

        BluetoothGattService service = mBluetoothGatt.getService(service1_uuid);

        if(service==null){
            if (mCharacteristic == null) {
                return false;
            }
        }
        mCharacteristic.setValue(data);

        return mBluetoothGatt.writeCharacteristic(mCharacteristic);
    }

    /**
     * handler for session time incrimental
     */
    public Runnable runnable = new Runnable() {
        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            timeText ="Session time:   " + String.format("%02d", Minutes) + " : " + String.format("%02d", Seconds);
            time.setText(timeText);
            handler.postDelayed(this, 0);
            if(PatientsView.sessionStarted==false && devicePopped==false){
                devicePopped = true;
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        deviceDisconnectedPopup();
                    }
                });
            }
        }
    };


    /**
     * Handler to post the values received from device in the view
     */
    @SuppressLint("HandlerLeak")
    public final Handler myHandler = new Handler() {
        public void handleMessage(Message message ) {
            int angleDetected=0,num_of_reps=0, hold_time_minutes, hold_time_seconds, active_time_minutes,active_time_seconds;
            int[] emg_data;byte[] sub_byte;
            sub_byte = (byte[]) message.obj;
            emg_data = ByteToArrayOperations.constructEmgDataWithGain(sub_byte,software_gain);
            angleDetected = ByteToArrayOperations.getAngleFromData(sub_byte[40],sub_byte[41]);
            if(ui_rate==0){
                minAngle = angleDetected;
                maxAngle = angleDetected;
            }
            num_of_reps = ByteToArrayOperations.getNumberOfReps(sub_byte[42], sub_byte[43]);
            hold_time_minutes = sub_byte[44];
            hold_time_seconds = sub_byte[45];
            active_time_minutes = sub_byte[46];
            active_time_seconds = sub_byte[47];
            currentAngle=angleDetected;
            String repetitionValue = ""+num_of_reps;
            Repetitions.setText(repetitionValue);
            String minutesValue=""+hold_time_minutes,secondsValue=""+hold_time_seconds;
            if(hold_time_minutes<10)
                minutesValue = "0"+hold_time_minutes;
            if(hold_time_seconds<10)
                secondsValue = "0"+hold_time_seconds;
            holdTimeValue = minutesValue+"m: "+secondsValue+"s";
            if(angleCorrected) {
                angleDetected+=angleCorrection;
                arcViewInside.setMaxAngle(angleDetected);
            }
            else {
                arcViewInside.setMaxAngle(angleDetected);
            }
            romJsonArray.put(angleDetected);
            try {
                outputStream_session_romdata = new FileOutputStream(file_session_romdata, true);
                outputStream_session_romdata.write(String.valueOf(angleDetected).getBytes());
                outputStream_session_romdata.write("\n".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream_session_romdata.flush();
                outputStream_session_romdata.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            LinearLayout.LayoutParams params;
            params = (LinearLayout.LayoutParams) emgSignal.getLayoutParams();
            for (int i=0;i<emg_data.length;i++) {
                ++ui_rate;
                lineData.addEntry(new Entry((float) ui_rate / 1000, emg_data[i]), 0);

                try {
                    outputStream_session_emgdata = new FileOutputStream(file_session_emgdata, true);
                    outputStream_session_emgdata.write(String.valueOf(emg_data[i]).getBytes());
                    outputStream_session_emgdata.write("\n".getBytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                emgJsonArray.put(emg_data[i]);
                try {
                    outputStream_session_emgdata.flush();
                    outputStream_session_emgdata.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                maxEmgValue = maxEmgValue < emg_data[i] ? emg_data[i] : maxEmgValue;
                if (maxEmgValue == 0)
                    maxEmgValue = 1;
                tv_max_emg.setText(String.valueOf(maxEmgValue));
                params.height = (int) (((View) emgSignal.getParent()).getMeasuredHeight() * emg_data[i] / maxEmgValue);
            }
            EMG.setText(Integer.toString(emg_data[emg_data.length-1]).concat(getResources().getString(R.string.emg_unit)));
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
            lineChart.getXAxis();
            lineChart.getAxisLeft();
            lineChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return (int) value + getResources().getString(R.string.emg_unit);
                }
            });
            if (UpdateTime / 1000 > 3)
                lineChart.setVisibleXRangeMaximum(5f);
            lineChart.moveViewToX((float) ui_rate / 1000);

            maxAngle = maxAngle < angleDetected ? angleDetected : maxAngle;
            tv_max_angle.setText(String.valueOf(maxAngle));
            minAngle = minAngle > angleDetected ? angleDetected : minAngle;
            tv_min_angle.setText(String.valueOf(minAngle));
//            }
            emgSignal.setLayoutParams(params);
            holdTime.setText(holdTimeValue);
            minutesValue=""+active_time_minutes;secondsValue=""+active_time_seconds;
            if(active_time_minutes<10)
                minutesValue = "0"+active_time_minutes;
            if(active_time_seconds<10)
                secondsValue = "0"+active_time_seconds;
            tv_action_time.setText(minutesValue+"m: "+secondsValue+"s");
            if(num_of_reps>=BodyPartSelection.repsselected && BodyPartSelection.repsselected!=0 && !sessionCompleted){
                sessionCompleted=true;
                openSuccessfullDialogAndCloseSession();
            }
        }
    };

    /**
     * Close session in 2000ms once the session goal is reached
     */
    private void openSuccessfullDialogAndCloseSession() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MonitorActivity.this);
        builder.setTitle("Session Completed");
        builder.setMessage("You have reached the goal.");
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        stopBtn.performClick();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.cancel();
            }
        },2000);
    }


    @SuppressLint("ClickableViewAccessibility")
    private  void initiatePopupWindowModified(){
        String sessionNo = tv_session_no.getText().toString();
        String sessiontime = time.getText().toString().substring(16);
        String actiontime = tv_action_time.getText().toString();
        SessionSummaryPopupWindow window = new SessionSummaryPopupWindow(this,maxEmgValue, sessionNo,maxAngle,minAngle,orientation,bodypart,
                json_phizioemail, sessiontime, actiontime, holdTime.getText().toString(),Repetitions.getText().toString(),emgJsonArray,romJsonArray,
                angleCorrection, patientId.getText().toString(),patientName.getText().toString(), tsLong);
        window.showWindow();
        window.setOnSessionDataResponse(new MqttSyncRepository.OnSessionDataResponse() {
            @Override
            public void onInsertSessionData(Boolean response, String message) {
                if (response)
                    showToast(message);
            }

            @Override
            public void onSessionDeleted(Boolean response, String message) {
                showToast(message);
            }

            @Override
            public void onMmtValuesUpdated(Boolean response, String message) {
                showToast(message);
            }

            @Override
            public void onCommentSessionUpdated(Boolean response) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inside_ondestroy = true;
        if(mBluetoothGatt!=null && mCharacteristic!=null){
            mBluetoothGatt.setCharacteristicNotification(mCharacteristic,false);
            mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);
            mBluetoothGatt.writeCharacteristic(mCharacteristic);
        }
        handler.removeCallbacks(runnable);
        isSessionRunning = false;
        PatientsView.sessionStarted = false;
        PatientsView.insideMonitor  = false;
        timer.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.INVISIBLE);
        if(isSessionRunning==false){
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
    }
    /**
     * sends the particular header to pheezee based on body part selected
     * calls the send function.
     * @param string
     */
    public void sendParticularDataToPheeze(String string){
        send(ValueBasedColorOperations.getParticularDataToPheeze(string));
    }

    /**
     * show toask
     * @param message
     */
    private void showToast(String message){
        Toast.makeText(MonitorActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSessionNumberResponse(String sessionnumber) {
        tv_session_no.setText(sessionnumber);
    }
}
